package com.alwansan.mo;

/** Reads Vulkan physical GPU properties via native NDK in this app only. */
public final class NativeVulkanProbe {
    private static final boolean LOADED;
    static {
        boolean loaded;
        try {
            System.loadLibrary("mo_vulkan");
            loaded = true;
        } catch (UnsatisfiedLinkError | SecurityException e) {
            loaded = false;
        }
        LOADED = loaded;
    }
    private NativeVulkanProbe() {}

    private static native String nativeRead();

    public static String inspect() {
        if (!LOADED) return "Vulkan native library unavailable on this device/ABI.";
        try {
            return nativeRead();
        } catch (UnsatisfiedLinkError | RuntimeException e) {
            return "Native Vulkan query failed: " + e.getClass().getSimpleName();
        }
    }
}
