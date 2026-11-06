package com.alwansan.mo.glesprobe;
import static org.junit.Assert.*;
import org.junit.Test;
public class MockGraphicsTierTest {
    @Test public void onlyProofLayerTextUnlocksSyntheticTier() {
        assertTrue(MockGraphicsTier.isProofLayerRenderer("MO-GLES-LAYER-PROOF (SIMULATED)"));
        assertFalse(MockGraphicsTier.isProofLayerRenderer("Adreno (TM) 730"));
        assertFalse(MockGraphicsTier.isProofLayerRenderer("Adreno (TM) 750"));
        assertFalse(MockGraphicsTier.isProofLayerRenderer(null));
    }
}
