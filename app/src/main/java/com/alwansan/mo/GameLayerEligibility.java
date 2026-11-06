package com.alwansan.mo;

/**
 * Declarative prerequisite for official Android GLES debug layers.
 * Not a test of driver compatibility, actual library loading, or game policy.
 */
public final class GameLayerEligibility {
    private GameLayerEligibility() {}

    public static boolean meetsManifestPrerequisites(boolean debuggable,
            boolean optedIn, int targetSdk) {
        return debuggable || (optedIn && targetSdk >= 30);
    }

    public static String classify(boolean debuggable, boolean optedIn, int targetSdk) {
        return meetsManifestPrerequisites(debuggable, optedIn, targetSdk)
                ? "POSSIBLE (manifest prerequisites only)"
                : "NO (not debuggable or eligible opt-in)";
    }
}
