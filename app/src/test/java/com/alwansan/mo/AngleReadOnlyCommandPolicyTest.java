package com.alwansan.mo;

import org.junit.Test;
import static org.junit.Assert.*;

public class AngleReadOnlyCommandPolicyTest {
    @Test public void everyCommandIsStrictlyReadOnly() {
        String[][] commands = AngleReadOnlyCommandPolicy.commands();
        assertEquals(14, commands.length);
        for (String[] command : commands) {
            assertTrue("Unsafe command in fixed list: " + String.join(" ", command),
                    AngleReadOnlyCommandPolicy.readOnly(command));
            for (String part : command) {
                assertNotEquals("put", part);
                assertNotEquals("delete", part);
                assertNotEquals("setprop", part);
                assertNotEquals("install", part);
            }
        }
    }

    @Test public void rejectsEscalatingOrArbitraryCommands() {
        assertFalse(AngleReadOnlyCommandPolicy.readOnly(null));
        assertFalse(AngleReadOnlyCommandPolicy.readOnly(new String[0]));
        assertFalse(AngleReadOnlyCommandPolicy.readOnly(
                new String[]{"settings", "put", "global", "angle_gl_driver_all_angle", "1"}));
        assertFalse(AngleReadOnlyCommandPolicy.readOnly(
                new String[]{"setprop", "debug.angle.gl_renderer", "Adreno 840"}));
        assertFalse(AngleReadOnlyCommandPolicy.readOnly(
                new String[]{"pm", "path", "com.tencent.mhadv"}));
        assertFalse(AngleReadOnlyCommandPolicy.readOnly(
                new String[]{"pm", "list", "packages", "--user", "0"}));
        assertFalse(AngleReadOnlyCommandPolicy.readOnly(
                new String[]{"getprop", "persist.sys.fake"}));
        assertFalse(AngleReadOnlyCommandPolicy.readOnly(
                new String[]{"settings", "get", "secure", "angle_gl_driver_selection_pkgs"}));
        assertFalse(AngleReadOnlyCommandPolicy.readOnly(
                new String[]{"sh", "-c", "getprop debug.angle.gl_renderer"}));
        assertFalse(AngleReadOnlyCommandPolicy.readOnly(
                new String[]{"pm", "list", "packages", "angle", ";", "id"}));
    }

    @Test public void includesImportantBootRiskKeyAsReadOnlyOnly() {
        boolean present = false;
        for (String[] c : AngleReadOnlyCommandPolicy.commands()) {
            if (c.length == 4 && "angle_gl_driver_all_angle".equals(c[3])) {
                assertEquals("get", c[1]);
                present = true;
            }
        }
        assertTrue(present);
    }
}
