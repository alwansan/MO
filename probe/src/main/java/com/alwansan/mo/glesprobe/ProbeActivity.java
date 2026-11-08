package com.alwansan.mo.glesprobe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Process;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Owned, disposable GLES test target. No Shizuku, no Internet, no game data access.
 * Its mock FPS tier is only a visual indicator and never changes rendering speed.
 */
public final class ProbeActivity extends Activity {
    private TextView result;
    private final String title = "MO  /  GLES LAYER TEST";

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(Color.rgb(16, 23, 37));
        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        int px = (int) (22 * getResources().getDisplayMetrics().density);
        column.setPadding(px, px * 2, px, px);
        scroll.addView(column);
        setContentView(scroll);

        TextView heading = new TextView(this);
        heading.setText(title);
        heading.setTextSize(23f);
        heading.setTextColor(Color.WHITE);
        heading.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        column.addView(heading);

        TextView disclaimer = new TextView(this);
        disclaimer.setText("SIMULATION ONLY\nهذا تطبيق تجريبي منفصل. فتح 120 FPS هنا مجرد محاكاة، وليس في Monster Hunter Outlanders.");
        disclaimer.setTextSize(15f);
        disclaimer.setTextColor(Color.rgb(177, 192, 219));
        disclaimer.setPadding(0, px / 2, 0, px);
        column.addView(disclaimer);

        result = new TextView(this);
        result.setTextSize(17f);
        result.setTextColor(Color.rgb(226, 237, 255));
        result.setTextIsSelectable(true);
        result.setGravity(Gravity.START);
        column.addView(result);

        Button refresh = new Button(this);
        refresh.setText("إعادة قراءة GL_RENDERER");
        refresh.setAllCaps(false);
        refresh.setOnClickListener(v -> queryAsync());
        column.addView(refresh);

        // v0.6 deliberately does not use System.loadLibrary on the GLES layer
        // during diagnostics: loading it manually can obscure loader evidence.
        Button refreshEgl = new Button(this);
        refreshEgl.setText("فحص مسارات EGL وGLES");
        refreshEgl.setAllCaps(false);
        refreshEgl.setOnClickListener(v -> queryAsync());
        column.addView(refreshEgl);

        Button lab = new Button(this);
        lab.setText("مختبر هوية الجهاز (محاكاة مستقلة فقط)");
        lab.setAllCaps(false);
        lab.setOnClickListener(v -> startActivity(
                new Intent(this, GameIdentityLabActivity.class)));
        column.addView(lab);

        TextView storageResult = new TextView(this);
        storageResult.setTextSize(14f);
        storageResult.setTextColor(Color.rgb(226, 237, 255));
        storageResult.setTextIsSelectable(true);
        storageResult.setText("P25: فحص التخزين لم يُشغّل بعد.");
        Button checkGuestStorage = new Button(this);
        checkGuestStorage.setAllCaps(false);
        checkGuestStorage.setText("P25: فحص تخزين Guest المملوك (ملفات مؤقتة فقط)");
        checkGuestStorage.setOnClickListener(v -> {
            checkGuestStorage.setEnabled(false);
            storageResult.setText("جارٍ فحص ملفات MO الخاصة...");
            new Thread(() -> {
                String info = OwnedStorageProbe.inspect(this);
                DiagnosticLog.log("OWNED_STORAGE_RESULT", info);
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        storageResult.setText(info);
                        checkGuestStorage.setEnabled(true);
                    }
                });
            }, "MO-owned-p25-storage").start();
        });
        column.addView(checkGuestStorage);
        column.addView(storageResult);
        TextView storageNotice = new TextView(this);
        storageNotice.setText("فحص اختياري: ينشئ ويقرأ ويحذف ملفات صغيرة وفريدة داخل التخزين الخاص "
                + "بتطبيق MO Layer Test فقط؛ لا يغيّر Settings.Global أو اللعبة أو ملفات التطبيقات الأخرى.");
        storageNotice.setTextColor(Color.rgb(177, 192, 219));
        column.addView(storageNotice);

        TextView instructions = new TextView(this);
        instructions.setText("عند تغيير إعدادات طبقات GLES من تطبيق MO، أغلق تطبيق الاختبار وافتحه مجددًا.\nهذا التطبيق لا يكتب أي إعداد للنظام.");
        instructions.setTextColor(Color.rgb(177, 192, 219));
        column.addView(instructions);
        DiagnosticLogUi.attach(this, column);
        DiagnosticLog.log("PROBE_SCREEN", "ProbeActivity initialized; owned test only");
        queryAsync();
    }

    private void queryAsync() {
        DiagnosticLog.log("GLES_READ_BEGIN", "Offscreen EGL query in owned process");
        result.setText("جارٍ إنشاء EGL context...");
        new Thread(() -> {
            String text = query();
            DiagnosticLog.log("GLES_READ_RESULT", text);
            runOnUiThread(() -> {
                if (!isFinishing() && !isDestroyed()) result.setText(text);
            });
        }, "MO-owned-test-gles").start();
    }

    private static String query() {
        EGLDisplay d = EGL14.EGL_NO_DISPLAY;
        EGLContext c = EGL14.EGL_NO_CONTEXT;
        EGLSurface s = EGL14.EGL_NO_SURFACE;
        try {
            d = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
            if (d == EGL14.EGL_NO_DISPLAY) return "EGL: no display";
            int[] v = new int[2];
            if (!EGL14.eglInitialize(d, v, 0, v, 1)) return "eglInitialize failed";
            int[] attrs = {
                    EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                    EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
                    EGL14.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] count = new int[1];
            if (!EGL14.eglChooseConfig(d, attrs, 0, configs, 0, 1, count, 0)
                    || count[0] == 0) return "eglChooseConfig failed";
            c = EGL14.eglCreateContext(d, configs[0], EGL14.EGL_NO_CONTEXT,
                    new int[]{EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE}, 0);
            if (c == EGL14.EGL_NO_CONTEXT) return "eglCreateContext failed";
            s = EGL14.eglCreatePbufferSurface(d, configs[0],
                    new int[]{EGL14.EGL_WIDTH, 1, EGL14.EGL_HEIGHT, 1, EGL14.EGL_NONE}, 0);
            if (s == EGL14.EGL_NO_SURFACE || !EGL14.eglMakeCurrent(d, s, s, c))
                return "eglMakeCurrent failed";
            String vendor = safe(GLES20.glGetString(GLES20.GL_VENDOR));
            String renderer = safe(GLES20.glGetString(GLES20.GL_RENDERER));
            String nativeRenderer = ProbeNativeRenderer.inspect();
            String eglEvidence = ProbeNativeRenderer.eglEvidence();
            String nativeVulkan = ProbeNativeRenderer.vulkanDeviceName();
            boolean javaIntercept = MockGraphicsTier.isProofLayerRenderer(renderer);
            boolean nativeIntercept = MockGraphicsTier.isProofLayerRenderer(nativeRenderer);
            // Explicitly model a synthetic NATIVE engine profile. Java's
            // GL_RENDERER is separately reported, never silently conflated.
            boolean sampleTier = nativeIntercept;
            return "Android Build.MODEL: " + Build.MODEL
                    + "\nGL_VENDOR: " + vendor
                    + "\nGL_RENDERER: " + renderer
                    + "\nGL_RENDERER (NDK): " + nativeRenderer
                    + "\nNative Vulkan deviceName: " + nativeVulkan
                    + "\nProcess PID: " + Process.myPid()
                    + "\nDebuggable APK: " + (BuildConfig.DEBUG ? "YES" : "NO")
                    + "\n" + eglEvidence
                    + "\nGL_VERSION: " + safe(GLES20.glGetString(GLES20.GL_VERSION))
                    + "\n\n"
                    + (sampleTier ? "Mock Native FPS tier: UNLOCKED (simulation)" :
                            "Mock Native FPS tier: LOCKED (simulation)")
                    + "\n\nLayer intercepted (Java): " + (javaIntercept ? "YES" : "NO")
                    + "\nLayer intercepted (Native): " + (nativeIntercept ? "YES" : "NO")
                    + "\nObserved scope: "
                    + (javaIntercept && nativeIntercept ? "Java + Native" :
                            nativeIntercept ? "NATIVE ONLY" :
                                    javaIntercept ? "JAVA ONLY" : "Not intercepted")
                    + "\n\nOfficial GLES layers can apply only to debuggable or opted-in apps."
                    + "\nNo real GPU capabilities, FPS, or game settings were modified.";
        } catch (RuntimeException | LinkageError e) {
            DiagnosticLog.failure("GLES_QUERY", e);
            return "GL query error: " + e.getClass().getSimpleName();
        } finally {
            if (d != EGL14.EGL_NO_DISPLAY) {
                EGL14.eglMakeCurrent(d, EGL14.EGL_NO_SURFACE,
                        EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
                if (s != EGL14.EGL_NO_SURFACE) EGL14.eglDestroySurface(d, s);
                if (c != EGL14.EGL_NO_CONTEXT) EGL14.eglDestroyContext(d, c);
                EGL14.eglTerminate(d);
            }
        }
    }

    private static String safe(String s) { return s == null ? "(null)" : s; }
}
