/*
 * P29: user-triggered, tightly-scoped native GLES symbol interception experiment.
 * Only the MO-owned libMOProbeRenderer.so caller's glGetString PLT import is
 * targeted. No hooks of Android, games, other callers, EGL dispatch, or Vulkan.
 *
 * ByteHook v1.1.2, MIT: https://github.com/bytedance/bhook/tree/v1.1.2
 * ByteHook's automatic-mode stack scope is mandatory for proxy recursion safety.
 */
#include <jni.h>
#include <EGL/egl.h>
#include <GLES2/gl2.h>
#include <bytehook.h>
#include <atomic>
#include <mutex>
#include <string>
#include <cstring>

namespace {
constexpr const char* kP29Mock = "MO_P29_OWNED_GLES_HOOK_ONLY";
std::mutex g_p29_test_mutex;
std::atomic<int> g_p29_hook_ok{0};
std::atomic<int> g_p29_hook_callbacks{0};
std::atomic<int> g_p29_hook_last_code{-1};
std::atomic<int> g_p29_hook_hits{0};
// Only the invoking test thread may receive the replacement string.
thread_local bool g_p29_test_thread_active = false;

using GetStringFn = const GLubyte* (GL_APIENTRY*)(GLenum);
static const GLubyte* GL_APIENTRY p29_proxy_gl_get_string(GLenum name) {
    BYTEHOOK_STACK_SCOPE();
    // Calls previous implementation in the ByteHook chain, never calls the PLT
    // entry directly from the hook (which would recurse).
    const GLubyte* original = BYTEHOOK_CALL_PREV(p29_proxy_gl_get_string, name);
    if (name == GL_RENDERER && g_p29_test_thread_active) {
        ++g_p29_hook_hits;
        return reinterpret_cast<const GLubyte*>(kP29Mock);
    }
    return original;
}

static void p29_hooked(bytehook_stub_t, int status_code, const char*,
                       const char*, void*, void*, void*) {
    if (status_code == BYTEHOOK_STATUS_CODE_ORIG_ADDR) return;
    ++g_p29_hook_callbacks;
    g_p29_hook_last_code.store(status_code);
    if (status_code == BYTEHOOK_STATUS_CODE_OK) ++g_p29_hook_ok;
}

std::string asText(const GLubyte* s) {
    return s ? reinterpret_cast<const char*>(s) : "(null renderer)";
}
std::string viaEgl() {
    const auto ptr = eglGetProcAddress("glGetString");
    if (!ptr) return "(EGL lookup unavailable)";
    auto fn = reinterpret_cast<GetStringFn>(ptr);
    return asText(fn(GL_RENDERER));
}
} // namespace

extern "C" JNIEXPORT jstring JNICALL
Java_com_alwansan_mo_glesprobe_ProbeNativeRenderer_nativeP29GlesTrial(
        JNIEnv* env, jclass) {
    // Prevent two UI taps/threads from racing a process-local native GOT patch.
    std::unique_lock<std::mutex> guard(g_p29_test_mutex, std::try_to_lock);
    if (!guard.owns_lock()) return env->NewStringUTF("P29 REFUSED: another trial is active");

    if (eglGetCurrentContext() == EGL_NO_CONTEXT) {
        return env->NewStringUTF("P29 REFUSED: no EGL context on invoking thread");
    }
    if (bytehook_get_mode() != BYTEHOOK_MODE_AUTOMATIC) {
        return env->NewStringUTF("P29 REFUSED: requires ByteHook AUTOMATIC mode");
    }

    const std::string before = asText(glGetString(GL_RENDERER));
    const std::string beforeEgl = viaEgl();

    g_p29_hook_ok = 0;
    g_p29_hook_callbacks = 0;
    g_p29_hook_last_code = -1;
    g_p29_hook_hits = 0;

    // Register only this owned library as caller. NULL callee avoids guesses
    // about Android15 GLES linker namespace paths; no global hook_all().
    bytehook_stub_t hook = bytehook_hook_single(
        "libMOProbeRenderer.so", nullptr, "glGetString",
        reinterpret_cast<void*>(p29_proxy_gl_get_string),
        p29_hooked, nullptr);

    std::string during = "(hook registration failed)";
    std::string duringEgl = "(not tested)";
    if (hook != nullptr && g_p29_hook_ok.load() > 0) {
        g_p29_test_thread_active = true;
        during = asText(glGetString(GL_RENDERER));
        duringEgl = viaEgl(); // typically bypasses a caller's PLT import
        g_p29_test_thread_active = false;
    }

    // Always disable replacement behavior BEFORE unregistering the hook.
    g_p29_test_thread_active = false;
    const int unhookStatus = hook != nullptr ? bytehook_unhook(hook) : -1;
    const std::string after = asText(glGetString(GL_RENDERER));
    const std::string afterEgl = viaEgl();
    const int hits = g_p29_hook_hits.load();
    const int successfulCallbacks = g_p29_hook_ok.load();

    bool pass = hook != nullptr && successfulCallbacks > 0 && hits > 0 &&
                during == kP29Mock && before == after && unhookStatus == 0;
    std::string report = "[P29 owned Native GLES PLT A/B/A — ByteHook 1.1.2]\n";
    report += "Scope: ONLY libMOProbeRenderer.so calling glGetString(GL_RENDERER)\n";
    report += "Baseline linked NDK: " + before + "\n";
    report += "Baseline EGL proc direct: " + beforeEgl + "\n";
    report += "Hook stub registered: " + std::string(hook ? "YES" : "NO") + "\n";
    report += "Hook callback OK: " + std::to_string(successfulCallbacks) + "\n";
    report += "Hook callbacks total: " + std::to_string(g_p29_hook_callbacks.load()) + "\n";
    report += "Last hook status code: " + std::to_string(g_p29_hook_last_code.load()) + "\n";
    report += "Linked NDK while active: " + during + "\n";
    report += "EGL direct pointer while active: " + duringEgl + "\n";
    report += "Owned hook hits: " + std::to_string(hits) + "\n";
    report += "Unhook status (0=OK): " + std::to_string(unhookStatus) + "\n";
    report += "Linked NDK after unhook: " + after + "\n";
    report += "EGL direct pointer after unhook: " + afterEgl + "\n";
    report += std::string("P29 result: ") + (pass ? "PASS (owned linked-call intercept, restored)"
                                                   : "NOT PROVEN / FAILED") + "\n";
    report += "Java GLES and Vulkan are NOT hooked; this does NOT unlock any FPS,\n";
    report += "game capability, driver or third-party application.";
    return env->NewStringUTF(report.c_str());
}
