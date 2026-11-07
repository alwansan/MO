package com.alwansan.mo;

/**
 * P13 zero-write ANGLE capability discovery allowlist.
 *
 * Never permits setprop, settings put/delete, pm install, shell, setenforce,
 * any app-specific process attachment, or the dangerous all-app ANGLE toggle.
 * Seeing an ANGLE package or a property value does not prove it is used by
 * a separately launched app. Every call runs as Shizuku ADB UID 2000.
 */
public final class AngleReadOnlyCommandPolicy {
    private AngleReadOnlyCommandPolicy() {}

    private static final String[] SETTINGS = {
            "angle_gl_driver_selection_pkgs",
            "angle_gl_driver_selection_values",
            "angle_gl_driver_all_angle",
            "angle_debug_package",
            "show_angle_in_use_dialog_box"
    };

    private static final String[] PROPS = {
            "debug.angle.gl_renderer",
            "debug.angle.gl_vendor",
            "debug.angle.gl_version",
            "ro.debuggable",
            "ro.build.type"
    };

    private static final String[] PACKAGES = {
            "com.android.angle",
            "org.chromium.angle",
            "com.google.android.angle"
    };

    public static String[][] commands() {
        String[][] results = new String[1 + PACKAGES.length
                + SETTINGS.length + PROPS.length][];
        int n = 0;
        results[n++] = new String[]{"pm", "list", "packages", "angle"};
        for (String p : PACKAGES) results[n++] = new String[]{"pm", "path", p};
        for (String key : SETTINGS) {
            results[n++] = new String[]{"settings", "get", "global", key};
        }
        for (String key : PROPS) results[n++] = new String[]{"getprop", key};
        return results;
    }

    public static boolean readOnly(String[] command) {
        if (command == null || command.length == 0) return false;
        if (command.length == 4 && "pm".equals(command[0])
                && "list".equals(command[1]) && "packages".equals(command[2])
                && "angle".equals(command[3])) return true;
        if (command.length == 3 && "pm".equals(command[0])
                && "path".equals(command[1])) {
            return contains(PACKAGES, command[2]);
        }
        if (command.length == 4 && "settings".equals(command[0])
                && "get".equals(command[1]) && "global".equals(command[2])) {
            return contains(SETTINGS, command[3]);
        }
        return command.length == 2 && "getprop".equals(command[0])
                && contains(PROPS, command[1]);
    }

    private static boolean contains(String[] list, String needle) {
        for (String item : list) if (item.equals(needle)) return true;
        return false;
    }
}
