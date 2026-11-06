package com.alwansan.mo;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;

/**
 * Creates a small offscreen EGL context inside MO itself and queries the real GLES driver.
 * Does NOT hook games, replace driver libraries, or write device properties.
 */
public final class GraphicsProbe {
    private GraphicsProbe() {}

    public static String inspect() {
        EGLDisplay display = EGL14.EGL_NO_DISPLAY;
        EGLSurface surface = EGL14.EGL_NO_SURFACE;
        EGLContext context = EGL14.EGL_NO_CONTEXT;
        try {
            display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
            if (display == EGL14.EGL_NO_DISPLAY) return "EGL: no display";

            int[] versions = new int[2];
            if (!EGL14.eglInitialize(display, versions, 0, versions, 1))
                return "EGL: init failed (0x" + Integer.toHexString(EGL14.eglGetError()) + ")";

            int[] attributes = {
                    EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                    EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
                    EGL14.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] count = new int[1];
            if (!EGL14.eglChooseConfig(display, attributes, 0, configs, 0, 1, count, 0)
                    || count[0] < 1) return "EGL: no GLES2 pbuffer config";

            int[] contextAttributes = {
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE
            };
            context = EGL14.eglCreateContext(
                    display, configs[0], EGL14.EGL_NO_CONTEXT, contextAttributes, 0);
            if (context == EGL14.EGL_NO_CONTEXT) return "EGL: context failed";

            int[] pbuffer = {
                    EGL14.EGL_WIDTH, 1, EGL14.EGL_HEIGHT, 1, EGL14.EGL_NONE
            };
            surface = EGL14.eglCreatePbufferSurface(display, configs[0], pbuffer, 0);
            if (surface == EGL14.EGL_NO_SURFACE) return "EGL: pbuffer failed";

            if (!EGL14.eglMakeCurrent(display, surface, surface, context))
                return "EGL: makeCurrent failed";

            int[] maxTexture = new int[1];
            GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTexture, 0);
            return "GL_VENDOR: " + safe(GLES20.glGetString(GLES20.GL_VENDOR)) + "\n"
                    + "GL_RENDERER: " + safe(GLES20.glGetString(GLES20.GL_RENDERER)) + "\n"
                    + "GL_VERSION: " + safe(GLES20.glGetString(GLES20.GL_VERSION)) + "\n"
                    + "MAX_TEXTURE_SIZE: " + maxTexture[0]
                    + "\n\nهذه معلومات عملية MO فقط؛ لا تثبت ما تقرؤه لعبة أخرى.";
        } catch (RuntimeException | LinkageError e) {
            return "EGL/GLES failed: " + e.getClass().getSimpleName()
                    + ": " + safe(e.getMessage());
        } finally {
            if (display != EGL14.EGL_NO_DISPLAY) {
                EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE,
                        EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
                if (surface != EGL14.EGL_NO_SURFACE) {
                    EGL14.eglDestroySurface(display, surface);
                }
                if (context != EGL14.EGL_NO_CONTEXT) {
                    EGL14.eglDestroyContext(display, context);
                }
                EGL14.eglTerminate(display);
            }
        }
    }

    private static String safe(String value) {
        return value == null ? "غير متاح" : value;
    }
}
