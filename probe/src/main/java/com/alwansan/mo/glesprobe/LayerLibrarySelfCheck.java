package com.alwansan.mo.glesprobe;

/** Explicit user-triggered binary loading, NOT activation of Android GLES layer hooks. */
public final class LayerLibrarySelfCheck {
    private static boolean loaded;
    private LayerLibrarySelfCheck() {}
    public static synchronized String check() {
        if (loaded) return "Library ELF already loadable. This is NOT proof of EGL loader interception.";
        try {
            System.loadLibrary("MOGlesProofLayer");
            loaded = true;
            return "PASS: libMOGlesProofLayer.so can be loaded in this app process. "
                    + "Android system layer injection is still unverified.";
        } catch (UnsatisfiedLinkError | SecurityException e) {
            return "FAIL: the owned GLES layer cannot be opened: "
                    + e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }
}
