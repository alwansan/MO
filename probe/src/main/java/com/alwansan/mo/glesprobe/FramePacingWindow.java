package com.alwansan.mo.glesprobe;

/**
 * Counts callbacks/requested draws and Renderer.onDrawFrame() invocations in
 * an owned, synthetic GLES test. It NEVER measures SurfaceFlinger-presented
 * frames or the FPS of a different process/game.
 */
public final class FramePacingWindow {
    private static final long MIN_WINDOW_NS = 1_000_000_000L;
    private long startedAtNanos = -1L;
    private long startCallbacks;
    private long startDraws;

    public static final class Sample {
        public final boolean ready;
        public final double callbackRate;
        public final double drawRate;
        public final long callbackCount;
        public final long drawCount;
        public final double seconds;

        private Sample(boolean ready, double callbackRate, double drawRate,
                       long callbackCount, long drawCount, double seconds) {
            this.ready = ready;
            this.callbackRate = callbackRate;
            this.drawRate = drawRate;
            this.callbackCount = callbackCount;
            this.drawCount = drawCount;
            this.seconds = seconds;
        }
    }

    public void reset() {
        startedAtNanos = -1L;
        startCallbacks = 0L;
        startDraws = 0L;
    }

    /**
     * All arguments are monotonic snapshots; invoke on the activity UI thread.
     * Only emits a new sample when the elapsed interval reaches one second.
     * A renderer draw callback is NOT proof of an actual screen presentation.
     */
    public Sample sample(long nowNanos, long callbackCount, long drawCount) {
        if (nowNanos < 0 || callbackCount < 0 || drawCount < 0)
            throw new IllegalArgumentException("Negative counters/time");
        if (startedAtNanos < 0 || nowNanos < startedAtNanos
                || callbackCount < startCallbacks || drawCount < startDraws) {
            startedAtNanos = nowNanos;
            startCallbacks = callbackCount;
            startDraws = drawCount;
            return new Sample(false, 0.0, 0.0, 0L, 0L, 0.0);
        }
        long elapsedNanos = nowNanos - startedAtNanos;
        if (elapsedNanos < MIN_WINDOW_NS) {
            return new Sample(false, 0.0, 0.0, 0L, 0L,
                    elapsedNanos / 1_000_000_000.0);
        }

        long callbacks = callbackCount - startCallbacks;
        long draws = drawCount - startDraws;
        double seconds = elapsedNanos / 1_000_000_000.0;
        Sample result = new Sample(true, callbacks / seconds,
                draws / seconds, callbacks, draws, seconds);
        startedAtNanos = nowNanos;
        startCallbacks = callbackCount;
        startDraws = drawCount;
        return result;
    }
}
