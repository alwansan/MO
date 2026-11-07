package com.alwansan.mo.glesprobe;

import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.os.SystemClock;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Persistent, bounded log of events in the MO-owned disposable test app ONLY.
 * Never reads other processes, other apps' logs, shell history, credentials,
 * or private game files. Asynchronous writes avoid blocking GLES/UI drawing.
 */
public final class DiagnosticLog {
    private static final Object FILE_LOCK = new Object();
    private static final int MAX_FILE_BYTES = 3 * 1024 * 1024;
    private static final int SEGMENTS = 3;
    private static final String FILE_NAME = "events.log";
    private static final ExecutorService WRITER = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "MO-DiagnosticLog-Writer");
        t.setDaemon(true);
        return t;
    });
    private static volatile Context app;
    private static volatile String sessionId;

    private DiagnosticLog() {}

    public static synchronized void initialize(Context context) {
        if (app != null) return;
        app = context.getApplicationContext();
        sessionId = LogLineFormatter.timestamp(System.currentTimeMillis())
                .replace(':', '-').replace('+', '_') + "-pid" + Process.myPid();
        log("PROCESS_START", "session=" + sessionId
                + "; package=" + app.getPackageName()
                + "; version=" + BuildConfig.VERSION_NAME
                + "; sdk=" + Build.VERSION.SDK_INT
                + "; model=" + Build.MODEL
                + "; soc=" + (Build.VERSION.SDK_INT >= 31 ? Build.SOC_MODEL : "(unavailable)")
                + "; ABIs=" + android.text.TextUtils.join(",", Build.SUPPORTED_ABIS)
                + "; utcOffset=" + java.util.TimeZone.getDefault().getID());
    }

    public static void log(String category, String message) {
        if (app == null) return;
        final String line = LogLineFormatter.line(System.currentTimeMillis(),
                SystemClock.elapsedRealtime(), Process.myPid(),
                Thread.currentThread().getId(), category, message);
        WRITER.execute(() -> {
            synchronized (FILE_LOCK) {
                try {
                    appendLocked(line);
                } catch (IOException ex) {
                    android.util.Log.e("MO-Diagnostic", "Persistent write failed", ex);
                }
            }
        });
    }

    public static void failure(String context, Throwable problem) {
        if (problem == null) {
            log("ERROR", context + ": unknown");
            return;
        }
        StringWriter stack = new StringWriter();
        problem.printStackTrace(new PrintWriter(stack));
        log("ERROR", context + ": " + stack);
    }

    /** Crash path writes synchronously, then caller MUST chain to Android's handler. */
    public static void crashNow(Thread thread, Throwable problem) {
        if (app == null) return;
        StringWriter stack = new StringWriter();
        if (problem != null) problem.printStackTrace(new PrintWriter(stack));
        String line = LogLineFormatter.line(System.currentTimeMillis(),
                SystemClock.elapsedRealtime(), Process.myPid(),
                thread == null ? -1 : thread.getId(), "UNCAUGHT_CRASH", stack.toString());
        synchronized (FILE_LOCK) {
            try {
                appendLocked(line);
            } catch (IOException ex) {
                android.util.Log.e("MO-Diagnostic", "Crash log failed", ex);
            }
        }
    }

    private static File directory() throws IOException {
        if (app == null) throw new IOException("Logger not initialized");
        File path = new File(app.getFilesDir(), "diagnostics");
        if (!path.isDirectory() && !path.mkdirs())
            throw new IOException("Cannot create private diagnostics directory");
        return path;
    }

    private static File segment(File dir, int index) {
        return new File(dir, index == 0 ? FILE_NAME : FILE_NAME + "." + index);
    }

    private static void appendLocked(String line) throws IOException {
        File dir = directory();
        File live = segment(dir, 0);
        byte[] bytes = line.getBytes(StandardCharsets.UTF_8);
        if (live.length() + bytes.length > MAX_FILE_BYTES) {
            File old = segment(dir, SEGMENTS - 1);
            if (old.exists() && !old.delete()) throw new IOException("Cannot rotate oldest log");
            for (int i = SEGMENTS - 2; i >= 0; i--) {
                File previous = segment(dir, i);
                if (previous.exists() && !previous.renameTo(segment(dir, i + 1)))
                    throw new IOException("Cannot rotate diagnostics segment " + i);
            }
        }
        try (FileOutputStream stream = new FileOutputStream(live, true)) {
            stream.write(bytes);
            stream.flush();
        }
    }

    /** Read in chronological order, including rotated recent sessions. */
    private static String readAllLocked() throws IOException {
        File dir = directory();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        String header = "# MO Layer Test — app-owned full available diagnostic history\n"
                + "# export_time_local=" + LogLineFormatter.timestamp(System.currentTimeMillis()) + "\n"
                + "# app_package=" + app.getPackageName() + "; version=" + BuildConfig.VERSION_NAME + "\n"
                + "# scope=only MO test process, activities, owned GLES/Vulkan measurements and exceptions\n"
                + "# NOT INCLUDED: system-wide logcat, other games, actual displayed FPS, account data\n"
                + "# bounded_history=3 files x 3 MiB; earliest events may rotate out\n"
                + "# Lines have local wall-clock time (with timezone offset) and monotonic elapsed_ms\n\n";
        buffer.write(header.getBytes(StandardCharsets.UTF_8));
        byte[] chunk = new byte[8192];
        for (int i = SEGMENTS - 1; i >= 0; i--) {
            File file = segment(dir, i);
            if (!file.isFile()) continue;
            String banner = "\n# segment=" + file.getName() + " bytes=" + file.length() + "\n";
            buffer.write(banner.getBytes(StandardCharsets.UTF_8));
            try (FileInputStream stream = new FileInputStream(file)) {
                int read;
                while ((read = stream.read(chunk)) != -1) buffer.write(chunk, 0, read);
            }
        }
        return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
    }

    public interface SnapshotCallback {
        void onSnapshot(String completeText, String error);
    }

    /**
     * Enqueued after all earlier log() calls, providing an ordered snapshot.
     * Callback runs on main thread; callers must not do heavy I/O there.
     */
    public static void snapshot(SnapshotCallback callback) {
        if (app == null) {
            callback.onSnapshot(null, "Logger not initialized");
            return;
        }
        WRITER.execute(() -> {
            String content = null;
            String error = null;
            synchronized (FILE_LOCK) {
                try {
                    content = readAllLocked();
                } catch (IOException ex) {
                    error = ex.getClass().getSimpleName() + ": " + ex.getMessage();
                }
            }
            String complete = content;
            String failure = error;
            new android.os.Handler(android.os.Looper.getMainLooper()).post(
                    () -> callback.onSnapshot(complete, failure));
        });
    }
}
