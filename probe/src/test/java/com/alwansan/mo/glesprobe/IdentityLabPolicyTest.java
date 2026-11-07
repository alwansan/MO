package com.alwansan.mo.glesprobe;

import static org.junit.Assert.*;
import org.junit.Test;

/** No emulator, Shizuku or external library required for these pure-Java tests. */
public class IdentityLabPolicyTest {
    private static IdentityLabPolicy.Signals real(double hz) {
        return new IdentityLabPolicy.Signals(
                "23013PC75G", "SM8475",
                "Adreno (TM) 730", "Adreno (TM) 730",
                "Adreno (TM) 730", hz);
    }

    @Test public void actualReadingsRemainUnmodifiedAndLocked() {
        IdentityLabPolicy.Signals input = real(120.0);
        IdentityLabPolicy.Decision decision =
                IdentityLabPolicy.evaluate(input, IdentityLabPolicy.Mode.ACTUAL);
        assertEquals("23013PC75G", decision.presentedModel);
        assertEquals("Adreno (TM) 730", decision.presentedNativeGles);
        assertFalse(decision.modelGate);
        assertFalse(decision.combined120Tier);
        assertTrue(decision.refreshGate);
        assertEquals("23013PC75G", input.model);
    }

    @Test public void oneChannelDoesNotUnlockOthers() {
        IdentityLabPolicy.Signals input = real(120.0);
        IdentityLabPolicy.Decision modelOnly =
                IdentityLabPolicy.evaluate(input, IdentityLabPolicy.Mode.MODEL_ONLY);
        assertTrue(modelOnly.modelGate);
        assertFalse(modelOnly.cpuGate);
        assertFalse(modelOnly.javaGlesGate);
        assertFalse(modelOnly.nativeGlesGate);
        assertFalse(modelOnly.vulkanGate);
        assertFalse(modelOnly.combined120Tier);
        assertEquals("Adreno (TM) 730", modelOnly.presentedNativeGles);

        IdentityLabPolicy.Decision javaOnly =
                IdentityLabPolicy.evaluate(input, IdentityLabPolicy.Mode.JAVA_GLES_ONLY);
        assertTrue(javaOnly.javaGlesGate);
        assertFalse(javaOnly.nativeGlesGate);
        assertFalse(javaOnly.combined120Tier);

        IdentityLabPolicy.Decision nativeOnly =
                IdentityLabPolicy.evaluate(input, IdentityLabPolicy.Mode.NATIVE_GLES_ONLY);
        assertTrue(nativeOnly.nativeGlesGate);
        assertFalse(nativeOnly.javaGlesGate);
        assertFalse(nativeOnly.combined120Tier);

        IdentityLabPolicy.Decision vulkanOnly =
                IdentityLabPolicy.evaluate(input, IdentityLabPolicy.Mode.VULKAN_ONLY);
        assertTrue(vulkanOnly.vulkanGate);
        assertFalse(vulkanOnly.combined120Tier);
    }

    @Test public void allMockSignalsAreStillBoundByRealDisplayThreshold() {
        assertTrue(IdentityLabPolicy.evaluate(real(120.0),
                IdentityLabPolicy.Mode.ALL_SIMULATED).combined120Tier);
        assertFalse(IdentityLabPolicy.evaluate(real(60.0),
                IdentityLabPolicy.Mode.ALL_SIMULATED).combined120Tier);
        assertFalse(IdentityLabPolicy.evaluate(real(119.9),
                IdentityLabPolicy.Mode.ALL_SIMULATED).combined120Tier);
    }

    @Test public void invalidReadingsNeverCreateHardwareCapability() {
        IdentityLabPolicy.Signals invalid =
                new IdentityLabPolicy.Signals(null, null, null, null, null, Double.NaN);
        IdentityLabPolicy.Decision state =
                IdentityLabPolicy.evaluate(invalid, IdentityLabPolicy.Mode.ACTUAL);
        assertEquals("(unavailable)", state.presentedModel);
        assertFalse(state.refreshGate);
        assertFalse(state.combined120Tier);
        assertEquals("LOCKED (MOCK)", IdentityLabPolicy.gate(state.combined120Tier));
        try {
            IdentityLabPolicy.evaluate(invalid, null);
            fail("Null mode must be rejected");
        } catch (IllegalArgumentException expected) {
            // Good: no misleading default mode.
        }
    }
}
