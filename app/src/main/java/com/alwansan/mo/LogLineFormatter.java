package com.alwansan.mo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/** Pure Java formatter; every event has human-readable local time and monotonic uptime. */
public final class LogLineFormatter {
    private static final int MAX_EVENT_CHARS = 16384;
    private LogLineFormatter() {}

    public static String timestamp(long epochMillis) {
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
        date.setTimeZone(TimeZone.getDefault());
        return date.format(new Date(epochMillis));
    }

    public static String sanitize(String input) {
        if (input == null) return "(null)";
        String clean = input.replace("\r", "\\r").replace("\n", "\\n")
                .replace("\u0000", "(NUL)");
        return clean.length() > MAX_EVENT_CHARS
                ? clean.substring(0, MAX_EVENT_CHARS) + " [TRUNCATED EVENT]" : clean;
    }

    public static String line(long epochMillis, long elapsedMs,
                              int pid, long threadId, String event, String details) {
        return timestamp(epochMillis) + " | elapsed_ms=" + elapsedMs
                + " | pid=" + pid + " | thread=" + threadId
                + " | " + sanitize(event) + " | " + sanitize(details) + "\n";
    }
}
