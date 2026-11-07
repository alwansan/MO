package com.alwansan.mo;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Exports the owned-app diagnostic history to Downloads/log.txt through
 * Android MediaStore (Android 10+). No broad storage permission and no root.
 * Reuses ONLY a URI this very app previously recorded in private preferences.
 */
public final class DiagnosticLogExporter {
    private static final String FILE_NAME = "log.txt";
    private static final String PREFS = "mo_log_export";
    private static final String LAST_URI = "owned_download_uri";
    private static final int MAX_CLIP_CHARS = 180000;
    private static final ExecutorService EXPORT_WORKER = Executors.newSingleThreadExecutor(
            r -> new Thread(r, "MO-OwnedLog-Export"));

    private DiagnosticLogExporter() {}

    public interface ResultCallback {
        void finished(String status);
    }

    public static void copyAndExport(Activity activity, ResultCallback callback) {
        DiagnosticLog.log("USER_ACTION", "Requested copy + Download/log.txt export");
        DiagnosticLog.snapshot((full, error) -> {
            if (error != null || full == null) {
                callback.finished("تعذر قراءة سجل MO: " + error);
                return;
            }
            Context app = activity.getApplicationContext();
            EXPORT_WORKER.execute(() -> {
                String saved = null;
                String failed = null;
                try {
                    saved = saveToDownloads(app, full);
                } catch (Exception ex) {
                    failed = ex.getClass().getSimpleName() + ": " + ex.getMessage();
                    DiagnosticLog.failure("MEDIASTORE_EXPORT", ex);
                }
                final String exported = saved;
                final String exportError = failed;
                activity.runOnUiThread(() -> {
                    String copied;
                    try {
                        copied = copyWithLimit(activity, full);
                    } catch (RuntimeException ex) {
                        copied = "تعذر النسخ: " + ex.getClass().getSimpleName();
                        DiagnosticLog.failure("CLIPBOARD", ex);
                    }
                    String outcome = exported == null
                            ? "فشل حفظ الملف في Download: " + exportError
                            : "تم الحفظ: /storage/emulated/0/Download/" + exported;
                    String message = outcome + "\n" + copied;
                    DiagnosticLog.log("EXPORT_RESULT", message);
                    callback.finished(message);
                });
            });
        });
    }

    public static void copyOnly(Activity activity, ResultCallback callback) {
        DiagnosticLog.log("USER_ACTION", "Requested diagnostic clipboard copy");
        DiagnosticLog.snapshot((full, error) -> {
            if (error != null || full == null) {
                callback.finished("تعذر قراءة السجل: " + error);
                return;
            }
            try {
                String answer = copyWithLimit(activity, full);
                DiagnosticLog.log("CLIPBOARD_RESULT", answer);
                callback.finished(answer);
            } catch (RuntimeException ex) {
                DiagnosticLog.failure("CLIPBOARD", ex);
                callback.finished("فشل النسخ: " + ex.getClass().getSimpleName());
            }
        });
    }

    private static String copyWithLimit(Activity activity, String complete) {
        boolean truncated = complete.length() > MAX_CLIP_CHARS;
        String toCopy = truncated
                ? "[MO LOG — CLIPBOARD ONLY LAST " + MAX_CLIP_CHARS
                        + " CHARACTERS; FULL LOG AVAILABLE IN Download/log.txt]\n"
                        + complete.substring(complete.length() - MAX_CLIP_CHARS)
                : complete;
        ClipboardManager manager = (ClipboardManager)
                activity.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager == null) throw new IllegalStateException("Clipboard unavailable");
        manager.setPrimaryClip(ClipData.newPlainText("MO Graphics Probe diagnostic log", toCopy));
        return truncated
                ? "نسخت آخر " + MAX_CLIP_CHARS
                        + " حرف للحافظة لتجنب حد Android؛ الملف المحفوظ يحتوي السجل الكامل المتاح."
                : "تم نسخ السجل الكامل المتاح إلى الحافظة (" + toCopy.length() + " حرف).";
    }

    private static String saveToDownloads(Context context, String text) throws IOException {
        if (android.os.Build.VERSION.SDK_INT < 29)
            throw new IOException("MediaStore.Downloads requires Android 10+");
        ContentResolver resolver = context.getContentResolver();
        Uri uri = validPreviouslyOwnedUri(context);
        boolean created = uri == null;
        if (created) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, FILE_NAME);
            values.put(MediaStore.Downloads.MIME_TYPE, "text/plain");
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/");
            values.put(MediaStore.Downloads.IS_PENDING, 1);
            uri = resolver.insert(MediaStore.Downloads.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY), values);
            if (uri == null) throw new IOException("MediaStore returned no output URI");
        }
        try {
            // rwt is an explicit truncate to avoid old bytes lingering on re-export.
            try (OutputStream stream = resolver.openOutputStream(uri, "rwt")) {
                if (stream == null) throw new IOException("Cannot open Downloads output stream");
                stream.write(text.getBytes(StandardCharsets.UTF_8));
                stream.flush();
            }
            if (created) {
                ContentValues done = new ContentValues();
                done.put(MediaStore.Downloads.IS_PENDING, 0);
                if (resolver.update(uri, done, null, null) != 1)
                    throw new IOException("Failed to publish finished Downloads file");
            }
            String actual = findName(resolver, uri);
            if (actual == null) throw new IOException("MediaStore cannot verify saved file");
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                    .putString(LAST_URI, uri.toString()).apply();
            return actual;
        } catch (IOException | RuntimeException ex) {
            if (created) {
                try { resolver.delete(uri, null, null); } catch (RuntimeException ignored) {}
            }
            if (ex instanceof IOException) throw (IOException) ex;
            throw new IOException("MediaStore operation failed", ex);
        }
    }

    private static Uri validPreviouslyOwnedUri(Context context) {
        String value = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(LAST_URI, null);
        if (value == null) return null;
        try {
            Uri candidate = Uri.parse(value);
            ContentResolver resolver = context.getContentResolver();
            String name = findName(resolver, candidate);
            // Only a URI created by our previous export is stored privately.
            if (name != null && name.startsWith("log") && name.endsWith(".txt"))
                return candidate;
        } catch (RuntimeException ignored) {
            // Stale URI after reinstall/provider cleanup. Create a new owned item.
        }
        return null;
    }

    private static String findName(ContentResolver resolver, Uri uri) {
        try (Cursor cursor = resolver.query(uri,
                new String[]{MediaStore.Downloads.DISPLAY_NAME,
                        MediaStore.Downloads.RELATIVE_PATH}, null, null, null)) {
            if (cursor == null || !cursor.moveToFirst()) return null;
            String name = cursor.getString(0);
            String path = cursor.getString(1);
            if (path == null || !(path.equals("Download/") || path.equals("Downloads/")))
                return null;
            return name;
        }
    }
}
