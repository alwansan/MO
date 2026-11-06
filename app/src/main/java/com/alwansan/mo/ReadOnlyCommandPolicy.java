package com.alwansan.mo;
public final class ReadOnlyCommandPolicy {
    private ReadOnlyCommandPolicy() {}
    public static boolean allowedPackage(String p) {
        return "com.tencent.mhadv".equals(p) || "com.garena.game.mho".equals(p)
            || "com.tencent.ig".equals(p) || "com.pubg.imobile".equals(p);
    }
    public static String[][] commands(String p) {
        if (!allowedPackage(p)) throw new IllegalArgumentException("Not allowlisted");
        return new String[][]{
            {"cmd","game","list-modes",p},
            {"cmd","game","list-configs",p},
            {"settings","get","global","angle_gl_driver_selection_pkgs"},
            {"settings","get","global","angle_gl_driver_selection_values"},
            {"settings","get","global","enable_gpu_debug_layers"},
            {"settings","get","global","gpu_debug_app"},
            {"getprop","ro.board.platform"},
            {"getprop","ro.hardware.vulkan"},
            {"getprop","ro.soc.model"}
        };
    }
    public static boolean readOnly(String[] c) {
        if (c == null || c.length == 0) return false;
        if ("cmd".equals(c[0])) return c.length == 4 && "game".equals(c[1])
            && ("list-modes".equals(c[2]) || "list-configs".equals(c[2]))
            && allowedPackage(c[3]);
        if ("settings".equals(c[0])) return c.length == 4 && "get".equals(c[1])
            && "global".equals(c[2]) && ("angle_gl_driver_selection_pkgs".equals(c[3])
            || "angle_gl_driver_selection_values".equals(c[3])
            || "enable_gpu_debug_layers".equals(c[3]) || "gpu_debug_app".equals(c[3]));
        if ("getprop".equals(c[0])) return c.length == 2
            && ("ro.board.platform".equals(c[1]) || "ro.hardware.vulkan".equals(c[1])
            || "ro.soc.model".equals(c[1]));
        return false;
    }
}
