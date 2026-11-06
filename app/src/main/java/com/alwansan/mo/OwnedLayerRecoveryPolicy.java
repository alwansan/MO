package com.alwansan.mo;

import java.util.Map;

/** An exact local fingerprint of the four Developer GPU Debug settings MO can edit. */
public final class OwnedLayerRecoveryPolicy {
    private OwnedLayerRecoveryPolicy() {}

    static final String ENABLE = "enable_gpu_debug_layers";
    static final String TARGET_KEY = "gpu_debug_app";
    static final String LAYERS = "gpu_debug_layers_gles";
    static final String SOURCE = "gpu_debug_layer_app";

    public static boolean exactlyOurActiveLayer(Map<String, String> s) {
        return s != null
                && "1".equals(s.get(ENABLE))
                && "com.alwansan.mo.glesprobe".equals(s.get(TARGET_KEY))
                && "libMOGlesProofLayer.so".equals(s.get(LAYERS))
                && "com.alwansan.mo.glesprobe".equals(s.get(SOURCE));
    }

    /** Never treat an already enabled/provisioned layer as a new clean baseline. */
    public static boolean canStartNewExperiment(Map<String, String> s) {
        if (s == null) return false;
        // This refuses any existing enabled debugging, even if it is for another app.
        if ("1".equals(s.get(ENABLE))) return false;
        if (s.containsKey(TARGET_KEY) || s.containsKey(LAYERS) || s.containsKey(SOURCE))
            return false;
        return true;
    }

    public static boolean cleanupVerified(Map<String, String> s) {
        return s != null && "0".equals(s.get(ENABLE))
                && !s.containsKey(TARGET_KEY)
                && !s.containsKey(LAYERS)
                && !s.containsKey(SOURCE);
    }
}
