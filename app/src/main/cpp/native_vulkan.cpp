#include <jni.h>
#include <vulkan/vulkan.h>
#include <cstdint>
#include <iomanip>
#include <sstream>
#include <string>
#include <vector>

namespace {
std::string hex(uint32_t v) {
    std::ostringstream out;
    out << "0x" << std::hex << std::uppercase << v;
    return out.str();
}

std::string readVulkan() {
    VkApplicationInfo app{};
    app.sType = VK_STRUCTURE_TYPE_APPLICATION_INFO;
    app.pApplicationName = "MO Graphics Probe";
    app.applicationVersion = VK_MAKE_VERSION(0, 3, 0);
    app.pEngineName = "MO Local Diagnostics";
    app.engineVersion = VK_MAKE_VERSION(0, 3, 0);
    app.apiVersion = VK_API_VERSION_1_0;

    VkInstanceCreateInfo info{};
    info.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
    info.pApplicationInfo = &app;
    VkInstance instance = VK_NULL_HANDLE;
    VkResult result = vkCreateInstance(&info, nullptr, &instance);
    if (result != VK_SUCCESS || instance == VK_NULL_HANDLE) {
        return "vkCreateInstance failed. VkResult=" + std::to_string(result)
            + "\nNo device modifications made.";
    }

    std::ostringstream out;
    uint32_t count = 0;
    result = vkEnumeratePhysicalDevices(instance, &count, nullptr);
    if (result != VK_SUCCESS || count == 0) {
        out << "vkEnumeratePhysicalDevices: " << result
            << "; count=" << count << "\n";
    } else if (count > 16) {
        out << "Unexpected physical-device count: " << count << "\n";
    } else {
        std::vector<VkPhysicalDevice> devices(count, VK_NULL_HANDLE);
        result = vkEnumeratePhysicalDevices(instance, &count, devices.data());
        if (result != VK_SUCCESS && result != VK_INCOMPLETE) {
            out << "Device enumeration failed: " << result << "\n";
        } else {
            out << "Physical GPUs: " << count << "\n";
            for (uint32_t i = 0; i < count; i++) {
                VkPhysicalDeviceProperties props{};
                VkPhysicalDeviceFeatures features{};
                vkGetPhysicalDeviceProperties(devices[i], &props);
                vkGetPhysicalDeviceFeatures(devices[i], &features);
                out << "\nGPU " << i << ": " << props.deviceName << "\n"
                    << "Vulkan API: " << VK_VERSION_MAJOR(props.apiVersion)
                    << "." << VK_VERSION_MINOR(props.apiVersion)
                    << "." << VK_VERSION_PATCH(props.apiVersion) << "\n"
                    << "Vendor ID: " << hex(props.vendorID) << "\n"
                    << "Device ID: " << hex(props.deviceID) << "\n"
                    << "Driver version (raw): " << hex(props.driverVersion) << "\n"
                    << "Device type: " << static_cast<int>(props.deviceType) << "\n"
                    << "Max 2D image: " << props.limits.maxImageDimension2D << "\n"
                    << "Max compute workgroup invocations: "
                    << props.limits.maxComputeWorkGroupInvocations << "\n"
                    << "Sampler anisotropy: " << (features.samplerAnisotropy ? "yes" : "no")
                    << "\n";
            }
        }
    }
    vkDestroyInstance(instance, nullptr);
    out << "\nRead only, within MO. The game may use its own profile and policies.";
    return out.str();
}
}  // namespace

extern "C" JNIEXPORT jstring JNICALL
Java_com_alwansan_mo_NativeVulkanProbe_nativeRead(JNIEnv* env, jclass) {
    const std::string result = readVulkan();
    return env->NewStringUTF(result.c_str());
}
