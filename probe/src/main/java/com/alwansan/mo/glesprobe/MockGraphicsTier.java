package com.alwansan.mo.glesprobe;

/** Synthetic game profile used ONLY for the test application, not the user's games. */
public final class MockGraphicsTier {
    private MockGraphicsTier() {}
    public static final String TEST_LAYER_RENDERER = "MO-GLES-LAYER-PROOF (SIMULATED)";
    public static boolean isProofLayerRenderer(String renderer) {
        return TEST_LAYER_RENDERER.equals(renderer);
    }
}
