#include <EGL/egl.h>
#include <GLES2/gl2.h>
#include <android/log.h>
#include <cstring>
#include <atomic>

// Original, tiny GLES proof layer. This file belongs ONLY to disposable MO-owned
// test APK, never to an installed commercial game or global driver.
namespace {
using GlesFunction = void (*)();
using NextAddress = void* (*)(void*, const char*);
using GetString = const GLubyte* (GL_APIENTRY*)(GLenum);

std::atomic<GetString> nextGetString{nullptr};
constexpr char kRenderer[] = "MO-GLES-LAYER-PROOF (SIMULATED)";

const GLubyte* GL_APIENTRY interceptedGetString(GLenum name) {
    if (name == GL_RENDERER) {
        return reinterpret_cast<const GLubyte*>(kRenderer);
    }
    GetString next = nextGetString.load();
    return next ? next(name) : nullptr;
}
} // namespace

extern "C" __attribute__((visibility("default")))
void AndroidGLESLayer_Initialize(void* layerId, NextAddress getNext) {
    (void) layerId;
    (void) getNext;
    __android_log_print(ANDROID_LOG_INFO, "MO-GLES-PROOF",
                        "MO-owned test GLES layer loaded; GL_RENDERER override only");
}

extern "C" __attribute__((visibility("default")))
void* AndroidGLESLayer_GetProcAddress(const char* function, GlesFunction next) {
    if (function && strcmp(function, "glGetString") == 0) {
        nextGetString.store(reinterpret_cast<GetString>(next));
        return reinterpret_cast<void*>(&interceptedGetString);
    }
    return reinterpret_cast<void*>(next);
}
