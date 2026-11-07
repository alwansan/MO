package com.alwansan.mo.glesprobe;

/**
 * An intentionally artificial game-menu gate. This class never writes Android
 * properties, hooks APIs or reads data from another app. "Presented" identities
 * exist only as local Java strings for the MO-owned educational test.
 */
public final class IdentityLabPolicy {
    public static final String FAKE_MODEL = "MO-LAB-MODEL (SIMULATED)";
    public static final String FAKE_SOC = "MO-LAB-SOC (SIMULATED)";
    public static final String FAKE_JAVA_GLES = "MO-LAB-JAVA-GLES (SIMULATED)";
    public static final String FAKE_NATIVE_GLES = "MO-LAB-NDK-GLES (SIMULATED)";
    public static final String FAKE_VULKAN = "MO-LAB-VULKAN (SIMULATED)";

    private IdentityLabPolicy() {}

    public enum Mode {
        ACTUAL,
        MODEL_ONLY,
        CPU_ONLY,
        JAVA_GLES_ONLY,
        NATIVE_GLES_ONLY,
        VULKAN_ONLY,
        ALL_SIMULATED
    }

    /** Physical/device readings copied from MO's *own* process, never edited. */
    public static final class Signals {
        public final String model;
        public final String soc;
        public final String javaGles;
        public final String nativeGles;
        public final String vulkanDevice;
        public final double displayHz;

        public Signals(String model, String soc, String javaGles,
                       String nativeGles, String vulkanDevice, double displayHz) {
            this.model = safe(model);
            this.soc = safe(soc);
            this.javaGles = safe(javaGles);
            this.nativeGles = safe(nativeGles);
            this.vulkanDevice = safe(vulkanDevice);
            this.displayHz = Double.isFinite(displayHz) && displayHz >= 0.0
                    ? displayHz : 0.0;
        }
    }

    /** A hypothetical game's decisions: each reader can disagree with others. */
    public static final class Decision {
        public final String presentedModel;
        public final String presentedSoc;
        public final String presentedJavaGles;
        public final String presentedNativeGles;
        public final String presentedVulkanDevice;
        public final boolean modelGate;
        public final boolean cpuGate;
        public final boolean javaGlesGate;
        public final boolean nativeGlesGate;
        public final boolean vulkanGate;
        public final boolean refreshGate;
        public final boolean combined120Tier;

        private Decision(String model, String soc, String javaGles,
                         String nativeGles, String vulkan, double hz) {
            presentedModel = model;
            presentedSoc = soc;
            presentedJavaGles = javaGles;
            presentedNativeGles = nativeGles;
            presentedVulkanDevice = vulkan;
            modelGate = FAKE_MODEL.equals(model);
            cpuGate = FAKE_SOC.equals(soc);
            javaGlesGate = FAKE_JAVA_GLES.equals(javaGles);
            nativeGlesGate = FAKE_NATIVE_GLES.equals(nativeGles);
            vulkanGate = FAKE_VULKAN.equals(vulkan);
            refreshGate = hz >= 120.0;
            combined120Tier = modelGate && cpuGate && javaGlesGate
                    && nativeGlesGate && vulkanGate && refreshGate;
        }
    }

    public static Decision evaluate(Signals real, Mode mode) {
        if (real == null || mode == null) {
            throw new IllegalArgumentException("Measured identity and mode are required");
        }
        String model = real.model;
        String soc = real.soc;
        String javaGles = real.javaGles;
        String nativeGles = real.nativeGles;
        String vulkan = real.vulkanDevice;

        switch (mode) {
            case MODEL_ONLY:
                model = FAKE_MODEL;
                break;
            case CPU_ONLY:
                soc = FAKE_SOC;
                break;
            case JAVA_GLES_ONLY:
                javaGles = FAKE_JAVA_GLES;
                break;
            case NATIVE_GLES_ONLY:
                nativeGles = FAKE_NATIVE_GLES;
                break;
            case VULKAN_ONLY:
                vulkan = FAKE_VULKAN;
                break;
            case ALL_SIMULATED:
                model = FAKE_MODEL;
                soc = FAKE_SOC;
                javaGles = FAKE_JAVA_GLES;
                nativeGles = FAKE_NATIVE_GLES;
                vulkan = FAKE_VULKAN;
                break;
            case ACTUAL:
            default:
                break;
        }
        return new Decision(model, soc, javaGles, nativeGles, vulkan,
                real.displayHz);
    }

    public static String gate(boolean passed) {
        return passed ? "UNLOCKED (MOCK)" : "LOCKED (MOCK)";
    }

    private static String safe(String source) {
        return source == null || source.trim().isEmpty() ? "(unavailable)" : source;
    }
}
