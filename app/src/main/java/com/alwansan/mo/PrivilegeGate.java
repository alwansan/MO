package com.alwansan.mo;

/** Pure logic: Shizuku ADB identity is not root. */
public final class PrivilegeGate {
    private PrivilegeGate() {}

    public static final int ADB_SHELL_UID = 2000;
    public static final int ROOT_UID = 0;

    public enum Level {
        OFFLINE,
        ADB_SHELL,
        ROOT_PRESENT_NOT_USED,
        OTHER_IDENTITY
    }

    public static Level classify(boolean binderAlive, int serverUid) {
        if (!binderAlive) return Level.OFFLINE;
        if (serverUid == ADB_SHELL_UID) return Level.ADB_SHELL;
        if (serverUid == ROOT_UID) return Level.ROOT_PRESENT_NOT_USED;
        return Level.OTHER_IDENTITY;
    }

    public static boolean rootlessShizukuSupported(boolean binderAlive, int serverUid) {
        return classify(binderAlive, serverUid) == Level.ADB_SHELL;
    }

    public static String explain(Level level) {
        switch (level) {
            case ADB_SHELL:
                return "ADB Shell (UID 2000) — ليس روت، ولا يستطيع حقن لعبة مغلقة";
            case ROOT_PRESENT_NOT_USED:
                return "الخدمة تعمل كروت (UID 0)؛ MO لن يستخدم هذا المسار";
            case OTHER_IDENTITY:
                return "هوية خدمة غير متوقعة؛ العمليات ذات الصلاحيات معطلة";
            case OFFLINE:
            default:
                return "خدمة Shizuku متوقفة أو غير متاحة";
        }
    }
}
