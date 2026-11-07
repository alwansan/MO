package com.alwansan.mo.glesprobe;

/** Native GLES2 glGetString in the same EGL context and thread as the Java query. */
public final class ProbeNativeRenderer {
    private static final boolean READY;
    static {
        boolean ready;
        try {
            System.loadLibrary("MOProbeRenderer");
            ready = true;
        } catch (UnsatisfiedLinkError | SecurityException ex) {
            ready = false;
        }
        READY = ready;
    }
    private ProbeNativeRenderer() {}
    private static native String nativeRenderer();
    private static native String nativeEglEvidence();
    private static native String nativeVulkanName();
    public static String eglEvidence() {
        if (!READY) return "EGL evidence: NDK module unavailable";
        try {
            return nativeEglEvidence();
        } catch (RuntimeException | LinkageError ex) {
            return "EGL evidence unavailable: " + ex.getClass().getSimpleName();
        }
    }

    /** Real VkPhysicalDeviceProperties.deviceName within only our own process. */
    public static String vulkanDeviceName() {
        if (!READY) return "(native Vulkan probe unavailable)";
        try {
            return nativeVulkanName();
        } catch (RuntimeException | LinkageError ex) {
            return "(Vulkan query failed: " + ex.getClass().getSimpleName() + ")";
        }
    }

    public static String inspect() {
        if (!READY) return "(MO native GLES test library unavailable)";
        try {
            return nativeRenderer();
        } catch (RuntimeException | LinkageError ex) {
            return "(native GLES query failed: " + ex.getClass().getSimpleName() + ")";
        }
    }
}
