#include <jni.h>
#include <GLES2/gl2.h>
#include <EGL/egl.h>
#include <vulkan/vulkan.h>
#include <cstring>
#include <string>
#include <vector>
#include <sys/stat.h>
#include <unistd.h>
#include <cerrno>
#include <cstdlib>

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

/*
 * Independent, read-only Vulkan discovery for the MO-owned process. We do not
 * expose or spoof features, insert a Vulkan layer, or inspect other processes.
 */
extern "C" JNIEXPORT jstring JNICALL
Java_com_alwansan_mo_glesprobe_ProbeNativeRenderer_nativeVulkanName(JNIEnv* env, jclass) {
    VkApplicationInfo app{};
    app.sType = VK_STRUCTURE_TYPE_APPLICATION_INFO;
    app.pApplicationName = "MO Identity Lab (owned)";
    app.apiVersion = VK_API_VERSION_1_0;

    VkInstanceCreateInfo info{};
    info.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
    info.pApplicationInfo = &app;

    VkInstance instance = VK_NULL_HANDLE;
    VkResult created = vkCreateInstance(&info, nullptr, &instance);
    if (created != VK_SUCCESS || instance == VK_NULL_HANDLE) {
        return env->NewStringUTF("(Vulkan instance unavailable)");
    }

    std::string name = "(no Vulkan physical GPU)";
    uint32_t total = 0;
    if (vkEnumeratePhysicalDevices(instance, &total, nullptr) == VK_SUCCESS && total > 0) {
        VkPhysicalDevice physical = VK_NULL_HANDLE;
        uint32_t requested = 1;
        if (vkEnumeratePhysicalDevices(instance, &requested, &physical) == VK_SUCCESS
                && requested > 0 && physical != VK_NULL_HANDLE) {
            VkPhysicalDeviceProperties properties{};
            vkGetPhysicalDeviceProperties(physical, &properties);
            name.assign(properties.deviceName);
        }
    }
    vkDestroyInstance(instance, nullptr);
    return env->NewStringUTF(name.c_str());
}


/*
 * P25: native libc stat/mkstemp/write/read/unlink on the MO-owned private filesDir.
 * Called ONLY from the user-triggered owned-app storage test; no arbitrary shell,
 * system directories, external app data, or persistent native data.
 * Reports status only (never private filesystem paths).
 */
extern "C" JNIEXPORT jstring JNICALL
Java_com_alwansan_mo_glesprobe_ProbeNativeRenderer_nativeStorageRoundTrip(
        JNIEnv* env, jclass, jstring directory) {
    if (directory == nullptr) return env->NewStringUTF("UNAVAILABLE(null filesDir)");
    const char* utf = env->GetStringUTFChars(directory, nullptr);
    if (utf == nullptr) return env->NewStringUTF("UNAVAILABLE(path encoding)");
    std::string dir(utf);
    env->ReleaseStringUTFChars(directory, utf);

    // Context.getFilesDir normally resolves to /data/user/0/... (or /data/data/...).
    // Reject any untrusted external or system location if a container returns one.
    if (dir.size() > 2048 || dir.rfind("/data/", 0) != 0) {
        return env->NewStringUTF("REFUSED(non-private Android filesDir)");
    }

    struct stat st{};
    bool directoryExists = ::stat(dir.c_str(), &st) == 0 && S_ISDIR(st.st_mode);
    std::string status = std::string("libc stat directory: ") +
            (directoryExists ? "YES" : "NO");
    std::string pattern = dir + "/mo_p25_native_XXXXXX";
    std::vector<char> tmp(pattern.begin(), pattern.end());
    tmp.push_back('\0');
    int fd = ::mkstemp(tmp.data()); // O_EXCL, mode 0600; unique file we own
    if (fd < 0) {
        status += " / native I/O: FAIL(mkstemp errno=" + std::to_string(errno) + ")";
        return env->NewStringUTF(status.c_str());
    }

    constexpr char payload[] = "MO-P25-NATIVE-IO";
    char buffer[sizeof(payload)] = {};
    bool ok = (::write(fd, payload, sizeof(payload)) == sizeof(payload))
            && (::lseek(fd, 0, SEEK_SET) == 0)
            && (::read(fd, buffer, sizeof(payload)) == sizeof(payload))
            && (std::memcmp(buffer, payload, sizeof(payload)) == 0);
    int ioError = ok ? 0 : errno;
    int closeResult = ::close(fd);
    int unlinkResult = ::unlink(tmp.data()); // ONLY the file made by mkstemp
    status += ok && closeResult == 0 ? " / native I/O: PASS" :
            " / native I/O: FAIL(errno=" + std::to_string(ioError) + ")";
    status += unlinkResult == 0 ? " / cleanup: PASS" : " / cleanup: FAIL";
    return env->NewStringUTF(status.c_str());
}
