package com.alwansan.mo;

import org.junit.Test;
import static org.junit.Assert.*;

public class GameLayerEligibilityTest {
    @Test public void debugAppIsEligibleEvenOldTarget() {
        assertTrue(GameLayerEligibility.meetsManifestPrerequisites(true, false, 28));
    }
    @Test public void optedInRequiresAndroid11Target() {
        assertTrue(GameLayerEligibility.meetsManifestPrerequisites(false, true, 30));
        assertFalse(GameLayerEligibility.meetsManifestPrerequisites(false, true, 29));
    }
    @Test public void protectedNotOptedInAppIsNotEligible() {
        assertFalse(GameLayerEligibility.meetsManifestPrerequisites(false, false, 35));
    }
}
