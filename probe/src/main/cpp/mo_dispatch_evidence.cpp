// P27: symbol-origin inventory inside the explicitly owned MO Layer Test process.
// Read-only. No dlopen, no patching, no system debug settings, no symbol replacement.
#include <jni.h>
#include <GLES2/gl2.h>
#include <EGL/egl.h>
#include <vulkan/vulkan.h>
#include <dlfcn.h>
#include <cstring>
#include <string>

namespace {
std::string libraryBasename(const char* path) {
    if (!path || !*path) return "(unknown)";
    const char* slash = std::strrchr(path, '/');
    return slash ? std::string(slash + 1) : std::string(path);
}

template <typename Fn>
std::string moduleOf(Fn pointer) {
    if (!pointer) return "(symbol unavailable)";
    Dl_info info{};
    if (::dladdr(reinterpret_cast<const void*>(pointer), &info) == 0) {
        return "(dladdr unavailable)";
    }
    return libraryBasename(info.dli_fname);
}

const char* equalsPointer(const void* first, const void* second) {
    if (!first || !second) return "UNKNOWN";
    return first == second ? "YES" : "NO";
}
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_alwansan_mo_glesprobe_ProbeNativeRenderer_nativeDispatchEvidence(
        JNIEnv* env, jclass) {
    // Only existing linker-visible symbols and the official EGL lookup API.
    // dlsym(RTLD_DEFAULT) does not inject or load a library.
    const auto directGles = &glGetString;
    const auto eglGles = eglGetProcAddress("glGetString");
    const void* defaultGles = dlsym(RTLD_DEFAULT, "glGetString");
    const auto directVulkan = &vkGetPhysicalDeviceProperties;
    const void* defaultVulkan = dlsym(RTLD_DEFAULT, "vkGetPhysicalDeviceProperties");

    const void* glAddress = reinterpret_cast<const void*>(directGles);
    const void* eglAddress = reinterpret_cast<const void*>(eglGles);
    const void* vkAddress = reinterpret_cast<const void*>(directVulkan);

    std::string report = "[P27 native symbol origins, owned process only]\n";
    report += "glGetString linked symbol module: " + moduleOf(directGles) + "\n";
    report += "glGetString eglGetProcAddress module: " + moduleOf(eglGles) + "\n";
    report += "glGetString RTLD_DEFAULT module: " + moduleOf(defaultGles) + "\n";
    report += std::string("linked vs EGL pointer equal: ") +
            equalsPointer(glAddress, eglAddress) + "\n";
    report += std::string("linked vs RTLD_DEFAULT pointer equal: ") +
            equalsPointer(glAddress, defaultGles) + "\n";
    report += "vkGetPhysicalDeviceProperties linked symbol module: " +
            moduleOf(directVulkan) + "\n";
    report += "vkGetPhysicalDeviceProperties RTLD_DEFAULT module: " +
            moduleOf(defaultVulkan) + "\n";
    report += std::string("Vulkan linked vs RTLD_DEFAULT pointer equal: ") +
            equalsPointer(vkAddress, defaultVulkan) + "\n";
    report += "Module basename only; no process addresses, function patching, "
              "driver substitution or changed GPU values.\n";
    report += "Symbol module is not proof of where the vendor ICD ultimately executes.";
    return env->NewStringUTF(report.c_str());
}
