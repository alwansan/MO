package com.alwansan.mo.glesprobe;

import android.app.Activity;
import android.graphics.Color;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;

/**
 * Game Identity Laboratory: all "spoofed" identifiers are ordinary Java values
 * used by a fictional menu rule. No system properties, graphics drivers, games,
 * Settings.Global or Shizuku APIs are written or called by this activity.
 */
public final class GameIdentityLabActivity extends Activity {
    private static final int TEXT = Color.rgb(228, 237, 255);
    private static final int MUTED = Color.rgb(176, 196, 225);
    private static final String[] MODE_LABELS = {
            "0 / القيم الحقيقية فقط",
            "1 / تغيير Build.MODEL داخل المحاكاة",
            "2 / تغيير هوية CPU داخل المحاكاة",
            "3 / تغيير Java GLES داخل المحاكاة",
            "4 / تغيير NDK GLES داخل المحاكاة",
            "5 / تغيير Vulkan داخل المحاكاة",
            "6 / تغيير جميع القنوات داخل المحاكاة"
    };

    private TextView originalView;
    private TextView simulatedView;
    private Spinner modeSelector;
    private IdentityLabPolicy.Signals measured;
    private boolean reading;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(Color.rgb(16, 23, 37));
        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        int margin = Math.round(18f * getResources().getDisplayMetrics().density);
        column.setPadding(margin, margin + margin, margin, margin);
        scroll.addView(column);
        setContentView(scroll);

        column.addView(label("MO / GAME IDENTITY LAB", 22, TEXT, true));
        column.addView(label(
                "OWNED APP • IN-MEMORY SIMULATION ONLY\n"
                + "هذا المختبر يعرض قراءات تطبيق MO الحقيقية ثم يجرّب قيمًا وهمية "
                + "داخل منطق القائمة التجريبية فقط. لا يغيّر Build أو GLES أو Vulkan "
                + "في الهاتف ولا يمس Monster Hunter Outlanders.\n",
                14, MUTED, false));
        column.addView(label("القياسات الحقيقية (MO Layer Test فقط)", 16, TEXT, true));
        originalView = label("جارٍ القياس...", 14, TEXT, false);
        originalView.setTextIsSelectable(true);
        column.addView(originalView);

        Button refresh = new Button(this);
        refresh.setAllCaps(false);
        refresh.setText("إعادة قياس الجهاز (قراءة فقط)");
        refresh.setOnClickListener(v -> refreshReal());
        column.addView(refresh);

        column.addView(label("\nاختر قناة واحدة لمحاكاة هويتها:", 16, TEXT, true));
        modeSelector = new Spinner(this);
        ArrayAdapter<String> choices = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, MODE_LABELS);
        choices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSelector.setAdapter(choices);
        column.addView(modeSelector);
        modeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view,
                                                  int position, long id) {
                renderSimulated();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                renderSimulated();
            }
        });

        simulatedView = label("جارٍ تجهيز المختبر...", 14, TEXT, false);
        simulatedView.setTextIsSelectable(true);
        column.addView(simulatedView);
        column.addView(label(
                "\nهذا ليس تعديلًا لهوية GPU في ألعاب الآخرين، ولا FPS حقيقيًا. "
                + "حتى فتح الخيار داخل هذه القائمة الوهمية لا يؤدي إلى عرض 120 إطارًا. "
                + "شاشة 120Hz لا تضمن محرك لعبة 120FPS.\n"
                + "لا يوجد زر تفعيل للطبقات في هذا المختبر، ولا يلزم Shizuku أو Root.",
                13, MUTED, false));
        refreshReal();
    }

    private TextView label(String value, int size, int color, boolean bold) {
        TextView text = new TextView(this);
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(color);
        text.setPadding(0, 0, 0, Math.round(12f * getResources().getDisplayMetrics().density));
        if (bold) text.setTypeface(null, android.graphics.Typeface.BOLD);
        return text;
    }

    private void refreshReal() {
        if (reading) return;
        reading = true;
        originalView.setText("قراءة EGL وVulkan في عملية MO الحالية...");
        double hz = getWindowManager().getDefaultDisplay().getRefreshRate();
        String model = Build.MODEL;
        String soc = Build.VERSION.SDK_INT >= 31 ? Build.SOC_MODEL : "(API below 31)";
        new Thread(() -> {
            String[] gles = sampleGles();
            String vulkan = ProbeNativeRenderer.vulkanDeviceName();
            IdentityLabPolicy.Signals result =
                    new IdentityLabPolicy.Signals(model, soc, gles[0], gles[1], vulkan, hz);
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                measured = result;
                reading = false;
                originalView.setText(
                        "PID: " + Process.myPid()
                        + "\nAndroid Build.MODEL: " + result.model
                        + "\nSoC model: " + result.soc
                        + "\nJava GLES GL_RENDERER: " + result.javaGles
                        + "\nNDK GLES GL_RENDERER: " + result.nativeGles
                        + "\nNative Vulkan deviceName: " + result.vulkanDevice
                        + "\nCurrent display Hz: "
                        + String.format(Locale.US, "%.1f", result.displayHz)
                        + "\nكل هذه قراءات داخل تطبيق الاختبار المملوك لـMO.");
                renderSimulated();
            });
        }, "MO-Owned-Identity-ReadOnly").start();
    }

    private void renderSimulated() {
        if (simulatedView == null || modeSelector == null) return;
        if (measured == null) {
            simulatedView.setText("بانتظار القياس الحقيقي...");
            return;
        }
        int position = modeSelector.getSelectedItemPosition();
        if (position < 0 || position >= IdentityLabPolicy.Mode.values().length) {
            position = 0;
        }
        IdentityLabPolicy.Mode mode = IdentityLabPolicy.Mode.values()[position];
        IdentityLabPolicy.Decision d = IdentityLabPolicy.evaluate(measured, mode);

        simulatedView.setText("\n[MO SYNTHETIC PRESENTATION — NOT ANDROID VALUES]"
                + "\nMODEL: " + d.presentedModel
                + "\nCPU: " + d.presentedSoc
                + "\nJava GLES: " + d.presentedJavaGles
                + "\nNative GLES: " + d.presentedNativeGles
                + "\nVulkan: " + d.presentedVulkanDevice
                + "\n\n[FICTIONAL GAME MENU CHECKS]"
                + "\nBuild.MODEL: " + IdentityLabPolicy.gate(d.modelGate)
                + "\nCPU identity: " + IdentityLabPolicy.gate(d.cpuGate)
                + "\nJava GL: " + IdentityLabPolicy.gate(d.javaGlesGate)
                + "\nNDK GL: " + IdentityLabPolicy.gate(d.nativeGlesGate)
                + "\nVulkan name: " + IdentityLabPolicy.gate(d.vulkanGate)
                + "\nDisplay 120Hz minimum: " + IdentityLabPolicy.gate(d.refreshGate)
                + "\n\nFake combined 120 FPS menu item: "
                + IdentityLabPolicy.gate(d.combined120Tier)
                + "\n\nEach individual signal has a distinct rule. The combined "
                + "menu item needs ALL simulated signals AND >=120Hz in this "
                + "artificial example, not in Outlanders.");
    }

    /** Own-process off-screen EGL query, independent of the system debug-layer UI. */
    private static String[] sampleGles() {
        EGLDisplay display = EGL14.EGL_NO_DISPLAY;
        EGLSurface surface = EGL14.EGL_NO_SURFACE;
        EGLContext context = EGL14.EGL_NO_CONTEXT;
        try {
            display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
            if (display == EGL14.EGL_NO_DISPLAY) return unavailable("no EGL display");
            int[] version = new int[2];
            if (!EGL14.eglInitialize(display, version, 0, version, 1))
                return unavailable("EGL init failed");
            int[] attrs = {
                    EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                    EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT, EGL14.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] count = new int[1];
            if (!EGL14.eglChooseConfig(display, attrs, 0, configs, 0, 1, count, 0)
                    || count[0] == 0) return unavailable("EGL config failed");
            context = EGL14.eglCreateContext(display, configs[0], EGL14.EGL_NO_CONTEXT,
                    new int[]{EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE}, 0);
            if (context == EGL14.EGL_NO_CONTEXT) return unavailable("EGL context failed");
            surface = EGL14.eglCreatePbufferSurface(display, configs[0],
                    new int[]{EGL14.EGL_WIDTH, 1, EGL14.EGL_HEIGHT, 1, EGL14.EGL_NONE}, 0);
            if (surface == EGL14.EGL_NO_SURFACE ||
                    !EGL14.eglMakeCurrent(display, surface, surface, context))
                return unavailable("EGL current failed");
            String javaRenderer = GLES20.glGetString(GLES20.GL_RENDERER);
            return new String[]{
                    javaRenderer != null ? javaRenderer : "(Java GLES null)",
                    ProbeNativeRenderer.inspect()
            };
        } catch (RuntimeException | LinkageError error) {
            return unavailable(error.getClass().getSimpleName());
        } finally {
            if (display != EGL14.EGL_NO_DISPLAY) {
                EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE,
                        EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
                if (surface != EGL14.EGL_NO_SURFACE)
                    EGL14.eglDestroySurface(display, surface);
                if (context != EGL14.EGL_NO_CONTEXT)
                    EGL14.eglDestroyContext(display, context);
                EGL14.eglTerminate(display);
            }
        }
    }

    private static String[] unavailable(String reason) {
        return new String[]{"(Java GLES unavailable: " + reason + ")",
                "(Native GLES unavailable: " + reason + ")"};
    }
}
