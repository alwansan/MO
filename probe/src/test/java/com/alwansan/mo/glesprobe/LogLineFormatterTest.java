package com.alwansan.mo.glesprobe;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.TimeZone;

public class LogLineFormatterTest {
    @Test public void timestampsContainOffsetAndMilliseconds() {
        TimeZone old = TimeZone.getDefault();
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("GMT+03:00"));
            String time = LogLineFormatter.timestamp(0L);
            assertEquals("1970-01-01T03:00:00.000+0300", time);
            String line = LogLineFormatter.line(0L, 900, 12, 5, "ACTIVITY_START", "start");
            assertTrue(line.contains("elapsed_ms=900"));
            assertTrue(line.contains("pid=12"));
            assertTrue(line.contains("thread=5"));
            assertTrue(line.contains("ACTIVITY_START | start"));
            assertTrue(line.endsWith("\n"));
        } finally {
            TimeZone.setDefault(old);
        }
    }

    @Test public void eventLinesCannotInjectUnmarkedRows() {
        String safe = LogLineFormatter.sanitize("line one\nline two\rvalue\u0000");
        assertEquals("line one\\nline two\\rvalue(NUL)", safe);
        assertEquals("(null)", LogLineFormatter.sanitize(null));
        assertTrue(LogLineFormatter.sanitize(new String(new char[17000])
                .replace('\u0000', 'x')).endsWith("[TRUNCATED EVENT]"));
    }
}
