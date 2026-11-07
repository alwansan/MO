package com.alwansan.mo.glesprobe;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.Choreographer;
import android.view.View;
import android.view.WindowInsets;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Measures one MO-owned GLSurfaceView's frame-request callbacks and its GL
 * Renderer.onDrawFrame() invocations. Neither value is a presentation counter
 * or a measurement of the FPS of another app, including Outlanders.
 *
 * No Shizuku, no Settings writes, no external GLES layer, no cross-app access.
 */
public final class FramePacingLabActivity extends Activity
        implements Choreographer.FrameCallback {
    private static final int[] REQUEST_EVERY = {1, 2, 4};
    private static final String[] MODES = {
            "كل إشارة رسم (قريب من معدل الشاشة)",
            "كل إشارتين (نصف وتيرة الطلب)",
            "كل أربع إشارات (ربع وتيرة الطلب)"
    };

    private final AtomicLong glDrawCallbacks = new AtomicLong();
    private final AtomicLong uiFrameCallbacks = new AtomicLong();
    private final FramePacingWindow window = new FramePacingWindow();

    private GLSurfaceView surface;
    private TextView measured;
    private Spinner cadence;
    private boolean active;
    private long callbackOrdinal;
    private int requestEvery = REQUEST_EVERY[0];

    @Override public void onCreate(Bundle saved) {
        super.onCreate(saved);
        final int density = Math.round(16f * getResources().getDisplayMetrics().density);
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(Color.rgb(16, 23, 37));
        // Android 15 enforces edge-to-edge for targetSdk35: keep title under
        // status bar rather than obscured by it, without changing system UI.
        scroll.setClipToPadding(false);
        scroll.setOnApplyWindowInsetsListener((v, insets) -> {
            int top = Build.VERSION.SDK_INT >= 30 ?
                    insets.getInsets(WindowInsets.Type.statusBars()).top :
                    insets.getSystemWindowInsetTop();
            int bottom = Build.VERSION.SDK_INT >= 30 ?
                    insets.getInsets(WindowInsets.Type.navigationBars()).bottom :
                    insets.getSystemWindowInsetBottom();
            v.setPadding(0, Math.max(top, 0), 0, Math.max(bottom, 0));
            return insets;
        });

        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        column.setPadding(density, density, density, density);
        scroll.addView(column);
        setContentView(scroll);

        column.addView(text("MO / OWNED FRAME PACING LAB", 21, Color.WHITE));
        column.addView(text(
                "REAL TEST IN OUR DISPOSABLE APP ONLY\n"
                + "هذا الاختبار يرسم لونًا متغيرًا داخل MO Layer Test فقط. "
                + "نقيس إشارات Choreographer واستدعاءات onDrawFrame داخل التطبيق. "
                + "هذه ليست FPS الشاشة المعروضة فعليًا، ولا FPS لعبة Outlanders.",
                14, Color.rgb(178, 196, 227)));

        surface = new GLSurfaceView(this);
        surface.setEGLContextClientVersion(2);
        surface.setRenderer(new GLSurfaceView.Renderer() {
            @Override public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                GLES20.glClearColor(0.11f, 0.32f, 0.55f, 1f);
            }
            @Override public void onSurfaceChanged(GL10 gl, int width, int height) {
                GLES20.glViewport(0, 0, width, height);
            }
            @Override public void onDrawFrame(GL10 gl) {
                long index = glDrawCallbacks.incrementAndGet();
                // Distinct visual animation in our test surface, no GPU spoof.
                float oscillation = (float) ((Math.sin(index * 0.075) + 1) * 0.5);
                GLES20.glClearColor(0.10f + 0.50f * oscillation,
                        0.17f, 0.50f - 0.30f * oscillation, 1f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            }
        });
        surface.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        LinearLayout.LayoutParams glParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                Math.round(180f * getResources().getDisplayMetrics().density));
        column.addView(surface, glParams);

        column.addView(text("اختر وتيرة طلب رسم المشهد المملوك لـMO:", 16, Color.WHITE));
        cadence = new Spinner(this);
        ArrayAdapter<String> choices = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, MODES);
        choices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cadence.setAdapter(choices);
        column.addView(cadence);
        cadence.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view,
                                                  int position, long id) {
                if (position < 0 || position >= REQUEST_EVERY.length) return;
                requestEvery = REQUEST_EVERY[position];
                resetWindow();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        measured = text("بدء القياس عند عرض التطبيق...", 16, Color.WHITE);
        measured.setTextIsSelectable(true);
        column.addView(measured);
        column.addView(text(
                "التفسير: عدد callbacks المرسل من Choreographer يختلف عن عدد "
                + "استدعاءات Renderer.onDrawFrame. حتى اكتمال رسم GLES لا يثبت "
                + "توقيت عرض الإطار بواسطة SurfaceFlinger. "
                + "تغيير الاختيار يضبط طلبات الرسم في اختبارنا وحده؛ لا يغير Hz "
                + "أو حد FPS أو تعريف الجهاز أو أي لعبة.\n"
                + "توقف القياسات بمجرد مغادرة هذه الشاشة لتقليل الاستهلاك.",
                13, Color.rgb(178, 196, 227)));
    }

    private TextView text(String content, int sp, int color) {
        TextView t = new TextView(this);
        t.setText(content);
        t.setTextSize(sp);
        t.setTextColor(color);
        t.setPadding(0, 0, 0,
                Math.round(14f * getResources().getDisplayMetrics().density));
        return t;
    }

    private void resetWindow() {
        window.reset();
        uiFrameCallbacks.set(0L);
        glDrawCallbacks.set(0L);
        callbackOrdinal = 0L;
        if (measured != null) measured.setText("قياس جديد... (نافذة ثانية واحدة)");
    }

    @Override protected void onResume() {
        super.onResume();
        surface.onResume();
        resetWindow();
        active = true;
        Choreographer.getInstance().postFrameCallback(this);
    }

    @Override protected void onPause() {
        active = false;
        Choreographer.getInstance().removeFrameCallback(this);
        surface.onPause();
        super.onPause();
    }

    @Override public void doFrame(long frameTimeNanos) {
        if (!active) return;
        callbackOrdinal++;
        long vsyncTicks = uiFrameCallbacks.incrementAndGet();
        if (callbackOrdinal % requestEvery == 0L) surface.requestRender();

        FramePacingWindow.Sample sample = window.sample(
                SystemClock.elapsedRealtimeNanos(), vsyncTicks, glDrawCallbacks.get());
        if (sample.ready && measured != null) {
            double reportedHz = getWindowManager().getDefaultDisplay().getRefreshRate();
            measured.setText(String.format(Locale.US,
                    "Current display setting: %.1f Hz\n"
                    + "Choreographer frame callbacks: %.1f / s\n"
                    + "MO GLES draw callbacks: %.1f / s\n"
                    + "Request stride: 1 per %d callbacks\n"
                    + "Sample: %.2f s, %d callbacks, %d GL draws\n"
                    + "Presented/displayed FPS: NOT MEASURED\n"
                    + "Other game's FPS: NOT MEASURED",
                    reportedHz, sample.callbackRate, sample.drawRate,
                    requestEvery, sample.seconds, sample.callbackCount,
                    sample.drawCount));
        }
        if (active) Choreographer.getInstance().postFrameCallback(this);
    }
}
