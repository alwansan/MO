package com.alwansan.mo.glesprobe;

import android.app.Activity;
import android.content.Context;
import android.os.Process;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

/**
 * P25: explicit, user-triggered file I/O diagnostics for our disposable app only.
 * Writes a few bytes to unique temporary files beneath its own private filesDir,
 * reads them back, and deletes only those test files. Never changes system settings.
 */
final class OwnedStorageProbe {
    private static final byte[] PAYLOAD = "MO-P25-CONTEXT-IO".getBytes(StandardCharsets.UTF_8);

    private OwnedStorageProbe() {}

    static String inspect(Activity activity) {
        StringBuilder report = new StringBuilder("MO P25 / OWNED GUEST STORAGE TEST\n");
        report.append("PID: ").append(Process.myPid()).append('\n');
        report.append("Scope: only current MO test private storage; ephemeral files\n");
        Context app = activity.getApplicationContext();
        try {
            File activityDir = activity.getFilesDir();
            report.append("Activity filesDir: ").append(FilePathCheck.check(activityDir).summary()).append('\n');
            report.append("Activity Context streams: ").append(contextRoundTrip(activity)).append('\n');
            report.append("Native stat + libc roundTrip: ")
                    .append(ProbeNativeRenderer.storageRoundTrip(activityDir.getAbsolutePath()))
                    .append('\n');
            if (app != null) {
                File appDir = app.getFilesDir();
                report.append("Application filesDir: ").append(FilePathCheck.check(appDir).summary()).append('\n');
                report.append("Application Context streams: ").append(contextRoundTrip(app)).append('\n');
                try {
                    report.append("Activity/Application filesDir same canonical path: ")
                            .append(activityDir.getCanonicalPath().equals(appDir.getCanonicalPath()))
                            .append('\n');
                } catch (IOException | SecurityException ex) {
                    report.append("Activity/Application canonical comparison: UNAVAILABLE(")
                            .append(ex.getClass().getSimpleName()).append(")\n");
                }
            } else {
                report.append("Application context: UNAVAILABLE\n");
            }
        } catch (RuntimeException ex) {
            report.append("Storage probe error: ").append(ex.getClass().getSimpleName()).append('\n');
        }
        report.append("A false File.exists and a successful native stat can indicate Java hook interference.\n")
                .append("This cannot prove complete guest storage isolation or compatibility with games.");
        return report.toString();
    }

    private static String contextRoundTrip(Context context) {
        String fileName = "mo_p25_ctx_" + UUID.randomUUID().toString() + ".tmp";
        boolean attempted = false;
        String status = "FAIL(UNKNOWN)";
        String cleanup = "N/A";
        try {
            attempted = true;
            try (FileOutputStream out = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
                out.write(PAYLOAD);
                out.flush();
            }
            byte[] bytes = new byte[PAYLOAD.length + 1];
            int length;
            try (FileInputStream in = context.openFileInput(fileName)) {
                length = in.read(bytes);
                if (length == PAYLOAD.length && in.read() != -1) length = -1;
            }
            status = length == PAYLOAD.length
                    && Arrays.equals(PAYLOAD, Arrays.copyOf(bytes, length))
                    ? "PASS" : "FAIL(CONTENT_MISMATCH)";
        } catch (IOException | RuntimeException ex) {
            status = "FAIL(" + ex.getClass().getSimpleName() + ")";
        } finally {
            if (attempted) {
                try {
                    cleanup = context.deleteFile(fileName) ? "PASS" : "NOT_FOUND_OR_FAILED";
                } catch (RuntimeException ex) {
                    cleanup = "FAIL(" + ex.getClass().getSimpleName() + ")";
                }
            }
        }
        return status + " / cleanup=" + cleanup;
    }
}
