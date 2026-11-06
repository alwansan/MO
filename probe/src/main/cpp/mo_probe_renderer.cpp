#include <jni.h>
#include <GLES2/gl2.h>
#include <EGL/egl.h>
#include <cstring>
#include <string>

namespace {
using GetStringFn = const GLubyte* (GL_APIENTRY*)(GLenum);

std::string readRendererViaEglProcAddress() {
    auto ptr = eglGetProcAddress("glGetString");
    if (!ptr) return "(glGetString lookup unavailable)";
    GetStringFn fn = reinterpret_cast<GetStringFn>(ptr);
    const GLubyte* renderer = fn(GL_RENDERER);
    return renderer ? reinterpret_cast<const char*>(renderer) : "(null GL_RENDERER)";
}

bool hasExtension(const char* extensions, const char* expected) {
    if (!extensions || !expected) return false;
    const size_t len = std::strlen(expected);
    const char* cur = extensions;
    while ((cur = std::strstr(cur, expected))) {
        bool leftOk = cur == extensions || cur[-1] == ' ';
        bool rightOk = cur[len] == '\0' || cur[len] == ' ';
        if (leftOk && rightOk) return true;
        cur += len;
    }
    return false;
}
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_alwansan_mo_glesprobe_ProbeNativeRenderer_nativeRenderer(JNIEnv* env, jclass) {
    const GLubyte* value = glGetString(GL_RENDERER);
    return env->NewStringUTF(value ? reinterpret_cast<const char*>(value) : "(null renderer)");
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_alwansan_mo_glesprobe_ProbeNativeRenderer_nativeEglEvidence(JNIEnv* env, jclass) {
    // Query EGL extensions and core function pointer under the existing EGL
    // context (same thread as Java and NDK direct query). No dlopen/manual load.
    const char* extensions = eglQueryString(EGL_NO_DISPLAY, EGL_EXTENSIONS);
    std::string result = "EGL_ANDROID_GLES_layers declared: ";
    result += extensions == nullptr ? "UNKNOWN (no EGL_NO_DISPLAY extensions)" :
        (hasExtension(extensions, "EGL_ANDROID_GLES_layers") ? "YES" : "NO");
    result += "\nGL_RENDERER (eglGetProcAddress): ";
    result += readRendererViaEglProcAddress();
    return env->NewStringUTF(result.c_str());
}
