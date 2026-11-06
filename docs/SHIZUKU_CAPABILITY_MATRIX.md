# Practical capability matrix — non-root Shizuku (ADB UID 2000)

| Action | Without root/bootloader unlock? | Conditions/notes |
|---|---|---|
| Read ZIP/module.prop and show contents | **Yes** | Normal Android storage picker, no Shizuku needed |
| Extract embedded standalone APK | **Yes** | Parse as data, never execute ZIP scripts |
| Install extracted APK (normal Android installer) | **Often yes** | User consents; Android OEM/version permissions apply. This installs UI only, NOT root module |
| Shizuku app request & access to shell-granted APIs | **Yes, conditional** | Shizuku started via wireless debugging, user grants, OEM permission restrictions |
| Device build, GLES & Vulkan diagnostics | **Yes, limited** | Standard Android APIs and permitted read-only commands |
| ANGLE per-package GLES-over-Vulkan trial | **Conditional** | Android 15+, ANGLE implementation, Shizuku permitted settings writes, functional verification, rollback |
| Enable Magisk/KernelSU module /data/adb hooks | **No** | Requires actual root/module framework |
| Inject Zygisk into arbitrary other app processes | **No** | Not granted to shell UID 2000 |
| Install or load KernelSU LKM/boot image | **No** | Kernel/boot operation requires privileges; bootloader locked |
| Modify protected system/vendor GPU libraries/drivers | **No** | Verified partition, SELinux and mount restrictions |
| Spoof global OpenGL/Vulkan vendor/device/driver in other games | **No**, via plain Shizuku alone | Different from changing optional ANGLE driver |
| Automatic Shizuku start after every reboot | **Not guaranteed** | Non-root wireless debugging generally requires manual restart |

**Implementation rule:** display error/unsupported clearly on failed proof-of-permission, never silently retry with other privilege escalations.

Authoritative: https://github.com/RikkaApps/Shizuku/blob/master/README.md ; https://kernelsu.org/guide/faq ; https://developer.android.com/games/develop/vulkan/overview .

## Clarification MO-20261107-CHAT-STATIC-002: Android graphics external layers
| Target | Shizuku non-root |
|---|---|
| Debuggable Android GLES/Vulkan app, with documented settings and valid layer APK | **Potentially yes**, real device test needed |
| targetSdk>=30 app with opt-in `com.android.graphics.injectLayers.enable=true` | **Potentially yes**, only if target developer consented |
| Stock nondebuggable release app with no external layer consent | **No** external code injection via official layer loader |
| ANGLE per-app selection | **Conditional**: changes rendering driver for GLES, no generic spoofing |
| `angle_gl_driver_all_angle=1` | **Never use**: documented bootloop risk |

Reference: https://developer.android.com/ndk/guides/rootless-debug-gles ; https://source.android.com/docs/core/graphics/implement-vulkan .
