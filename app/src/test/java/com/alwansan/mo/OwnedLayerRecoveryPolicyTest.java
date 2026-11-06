package com.alwansan.mo;

import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class OwnedLayerRecoveryPolicyTest {
    private Map<String, String> owned() {
        Map<String, String> m = new HashMap<>();
        m.put("enable_gpu_debug_layers", "1");
        m.put("gpu_debug_app", "com.alwansan.mo.glesprobe");
        m.put("gpu_debug_layers_gles", "libMOGlesProofLayer.so");
        m.put("gpu_debug_layer_app", "com.alwansan.mo.glesprobe");
        return m;
    }

    @Test public void exactlyOurActiveFingerprintRequired() {
        Map<String, String> m = owned();
        assertTrue(OwnedLayerRecoveryPolicy.exactlyOurActiveLayer(m));
        m.put("gpu_debug_app", "com.tencent.mhadv");
        assertFalse(OwnedLayerRecoveryPolicy.exactlyOurActiveLayer(m));
        m.put("gpu_debug_app", "com.alwansan.mo.glesprobe");
        m.put("gpu_debug_layers_gles", "libOtherLayer.so");
        assertFalse(OwnedLayerRecoveryPolicy.exactlyOurActiveLayer(m));
        m.remove("gpu_debug_layers_gles");
        assertFalse(OwnedLayerRecoveryPolicy.exactlyOurActiveLayer(m));
    }

    @Test public void cannotRecordAlreadyActiveLayerAsCleanBaseline() {
        assertFalse(OwnedLayerRecoveryPolicy.canStartNewExperiment(owned()));
        Map<String, String> other = new HashMap<>();
        other.put("enable_gpu_debug_layers", "1");
        other.put("gpu_debug_app", "com.other.app");
        assertFalse(OwnedLayerRecoveryPolicy.canStartNewExperiment(other));
        other.put("enable_gpu_debug_layers", "0");
        assertFalse(OwnedLayerRecoveryPolicy.canStartNewExperiment(other));
        other.clear();
        other.put("enable_gpu_debug_layers", "0");
        assertTrue(OwnedLayerRecoveryPolicy.canStartNewExperiment(other));
    }

    @Test public void cleanupMustRemoveOnlyOwnedMarkers() {
        Map<String, String> state = new HashMap<>();
        state.put("enable_gpu_debug_layers", "0");
        assertTrue(OwnedLayerRecoveryPolicy.cleanupVerified(state));
        state.put("gpu_debug_app", "com.other.app");
        assertFalse(OwnedLayerRecoveryPolicy.cleanupVerified(state));
        state.clear();
        assertFalse(OwnedLayerRecoveryPolicy.cleanupVerified(state));
    }
}
