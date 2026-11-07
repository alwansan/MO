package com.alwansan.mo.glesprobe;

import static org.junit.Assert.*;
import org.junit.Test;

/** Tests pure counting math, no Android runtime, GPU or Shizuku required. */
public class FramePacingWindowTest {
    @Test public void oneSecondSampleHasDistinctCallbackAndDrawRates() {
        FramePacingWindow meter = new FramePacingWindow();
        assertFalse(meter.sample(10L, 0, 0).ready);
        FramePacingWindow.Sample r = meter.sample(1_000_000_010L, 120, 60);
        assertTrue(r.ready);
        assertEquals(120.0, r.callbackRate, 0.001);
        assertEquals(60.0, r.drawRate, 0.001);
        assertEquals(120L, r.callbackCount);
        assertEquals(60L, r.drawCount);
    }

    @Test public void shortIntervalIsPendingAndWindowCanReset() {
        FramePacingWindow meter = new FramePacingWindow();
        meter.sample(1_000, 0, 0);
        assertFalse(meter.sample(900_000_000L, 30, 25).ready);
        assertTrue(meter.sample(1_100_000_000L, 50, 40).ready);
        meter.reset();
        assertFalse(meter.sample(2_000_000_000L, 51, 41).ready);
        FramePacingWindow.Sample sample = meter.sample(3_000_000_000L, 91, 61);
        assertEquals(40, sample.callbackCount);
        assertEquals(20, sample.drawCount);
    }

    @Test public void backgroundLikeCounterResetStartsNewWindow() {
        FramePacingWindow meter = new FramePacingWindow();
        meter.sample(100, 200, 90);
        assertFalse(meter.sample(1_000_000_100L, 10, 0).ready);
        assertTrue(meter.sample(2_000_000_100L, 130, 28).ready);
        assertEquals(28.0, meter.sample(3_000_000_100L, 250, 58).drawRate, 0.001);
    }

    @Test public void subZeroInputIsRejected() {
        FramePacingWindow meter = new FramePacingWindow();
        try {
            meter.sample(-1, 0, 0);
            fail("Must reject negative clock");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Negative"));
        }
    }

    @Test public void ratesUseElapsedTimeNotAssume120Hz() {
        FramePacingWindow meter = new FramePacingWindow();
        meter.sample(1_000_000_000L, 0, 0);
        FramePacingWindow.Sample r = meter.sample(3_000_000_000L, 120, 90);
        assertEquals(60.0, r.callbackRate, 0.001);
        assertEquals(45.0, r.drawRate, 0.001);
    }
}
