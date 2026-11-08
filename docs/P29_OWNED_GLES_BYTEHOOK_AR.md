# P29 — تجربة Native GLES interception داخل MO-owned APK + حدود Shizuku

**Date:** 2026-11-08 Asia/Baghdad  
**Session:** MO-20261108-CHAT-P29-OWNED-GLES-BYTEHOOK-042  
**Scope:** MO Layer Test only (com.alwansan.mo.glesprobe), Android15 ARM64, no root, no game.

## Source reviewed before implementation

- Main MO started at df40b39abdb8a5d0ca09c4e2edfc19ff54e67d3e (P28 decision: Native first).
- ByteHook **v1.1.0**, upstream https://github.com/bytedance/bhook/tree/v1.1.0, MIT license. Exact upstream public API: https://github.com/bytedance/bhook/blob/v1.1.0/bytehook/src/main/cpp/include/bytehook.h and sample hacker_bytehook.cpp; Maven group com.bytedance:bytehook:1.1.0 uses Android Prefab.
- API: bytehook_hook_single(caller_path_name, callee_path_name, sym_name, new_func, hooked, hooked_arg), BYTEHOOK_CALL_PREV plus BYTEHOOK_STACK_SCOPE in C++ automatic mode; bytehook_unhook(stub). Returning a non-null stub alone does not certify installed hook; callback/actual call validation required.
- Previous P27 owner evidence: same-process public glGetString symbols via linked/EGL/RTLD_DEFAULT all libGLESv2.so with equal pointers, vkGetPhysicalDeviceProperties linked/default libvulkan.so with equal pointers; true GLES/Vulkan names were Adreno730 despite Java Build.MODEL spoof. PID23440 was NOT independently mapped with new ps; P26 older guest PID17047 independently verified. Never infer virtual Guest from old PID.
- ByteHook is PLT/GOT caller interception, not a general-purpose GLES driver wrapper, not an APK injector, not a way to replace physical GPU capabilities or affect Vulkan. Direct calls via eglGetProcAddress will generally bypass a given caller's PLT GOT entry.

## P29 implementation in MO Layer Test v0.9.0 (source only until CI and device verification)

- probe/src/main/cpp/mo_p29_gles_hook.cpp creates an explicit **on-button-click** native trial, only when the owned thread has a valid EGL context. Limits ByteHook to caller **libMOProbeRenderer.so**, symbol **glGetString**, callee NULL (avoid Android15 linker basename ambiguity). NO hook_all/partial, no third-party packages or processes, no wide Android/Qualcomm libraries.
- Before installing the hook: read real linked glGetString(GL_RENDERER) plus separate EGL-proc direct read.
- During hook: controlled thread-local replacement of only GL_RENDERER with fixed unique constant MO_P29_OWNED_GLES_HOOK_ONLY; reads linked and EGL-proc values separately. Calls original via BYTEHOOK_CALL_PREV and cleans up ByteHook stack automatically. Other GLES enums and other threads receive original results.
- Always disable replacement flag and call bytehook_unhook before report; compare original linked value after unhook. Record callback success count, last status, actual proxy hit count and unhook result (0 success).
- PASS strictly requires installed callback success, actual proxy hits, exact owned mock value visible through native linked API, original value restored after unhook, unhook status zero. A UI-only MOCK, hook stub without callbacks, or ByteHook initialized alone is **NOT PASS**.
- probe/src/main/java/com/alwansan/mo/glesprobe/ProbeNativeRenderer.java initializes Java ByteHook in automatic mode only upon trial button, and calls native JNI on GL thread.
- ProbeActivity owns button P29 OFF/ON/OFF, user-triggered with separate text result and app diagnostic log. Shares existing temporary offscreen EGL setup; does not start hook automatically at startup. Existing P25 storage and P27 read-only diagnostics remain intact.
- probe/build.gradle upgraded **only disposable MO Layer Test to v0.9.0**, pinned MIT dependency com.bytedance:bytehook:1.1.0, enabled Prefab. CMake links bytehook::bytehook only into MOProbeRenderer.so. This may package libbytehook.so plus its transitive MIT dependency ShadowHook; review binary sizes and transitive license before redistribution. No third-party source copied into MO.
- A clean Android Gradle/NDK CI build is required. No P29 phone result, no claim on Guest loading, no actual game performance or native Vulkan spoof.

## Acceptance and limitations

1. CI assemble arm64 and JUnit must be successful and actual MO-Layer-Test-v0.9.0.apk uploaded with SHA256; otherwise report blocker and fix code without claiming phone readiness.
2. For first phone trial run **outside Renjana** if practical to establish owned app PLT hooking, then repeat with working Renjana P26 as a separate Guest experiment (after Rish ps PID mapping). Each trial captures full P29 report and keeps error/status codes.
3. Even PASS only proves a tightly scoped MO-owned linked Native call was intercepted temporarily. Java GLES and eglGetProcAddress may remain Adreno730; Android Vulkan independent. Neither the real device's graphics capabilities nor frame scheduling changed.
4. If callback fails or linked native call still returns original, inspect import PLT, namespace and hook status. Do not widen scope to other ELF callers as blind fallback; no native game code experiments.
5. Full P30 Vulkan investigation will require instance dispatch evaluation and own app native proof; never infer success from GLES alone.

## Shizuku clarification (user offered "simulated root")

- If Shizuku is started through Android Wireless Debugging/ADB, effective identity is shell **UID 2000**, not Unix root UID 0. Rish will show this with \`id\`. Shizuku does not grant ptrace access to arbitrary protected UID apps, bypass SELinux, write to /system or load external .so into non-debuggable production game.
- Allowed optional tools: \`id\`; read-only \`ps -A -o PID,NAME | grep -E 'com.fesu.renjana|com.alwansan.mo.glesprobe'\`; \`dumpsys meminfo <owned PID>\` when needed; bounded, user-exported logs. No global settings writes, GPU debug layers, ANGLE forcing, bootloader unlock, kernel/Zygisk modules or game APK changes.
- Root-backed Shizuku requires **actual root**, which is explicitly outside project rules; don't call it a harmless "virtual root".


## CI failure / version pin correction
- First P29 CI run 42437452741 (#76) failed before any native compilation at Android AAR metadata: ByteHook 1.1.2 and its transitive ShadowHook 2.0.1 require compileSdk >=37; MO probe and AGP8.9.2 use compileSdk35. The failure is **not a P29 native hook result**.
- Selected ByteHook v1.1.0 after reviewing upstream GitHub source build.gradle: compileSdkVersion35, shadowhookVersion1.1.1. Its native header still includes bytehook_hook_single, bytehook_unhook, automatic mode stack scope, hook status callback and BYTEHOOK_CALL_PREV. Pin only the standalone owned probe dependency at 1.1.0; do NOT globally upgrade targetSdk/compileSdk to satisfy latest AAR. A new CI build must validate actual Maven artifacts and NDK links.


## P29 final GitHub Actions and Release verification (2026-11-08)
- Run #77 https://github.com/alwansan/MO/actions/runs/42437612759 SUCCESS for first compileSdk35-compatible ByteHook pin. Final corrected source run #78 https://github.com/alwansan/MO/actions/runs/42437618103 **completed SUCCESS**, source commit **ead4f202ea5b9aa957d6edc9a9532004538836c8**.
- Public prerelease https://github.com/alwansan/MO/releases/tag/mo-v0.11.0-alpha.11-run78 contains **MO-Layer-Test-v0.9.0.apk** uploaded (**342024 bytes**, SHA256 **3718601e4e26b31da697a4cdb04f50585fd4405e18af4dc6f1d32c1e73606041**): https://github.com/alwansan/MO/releases/download/mo-v0.11.0-alpha.11-run78/MO-Layer-Test-v0.9.0.apk .
- Existing workflow also rebuilt MO Graphics Probe main APK; **do not replace main MO** to test P29 (different debug signature/rollback data risk).
- ByteHook 1.1.0 + transitive ShadowHook1.1.1 compiled successfully for arm64. CI native compile and test success does **not** establish on-device hook callback hits or actual GL string interception. No device output P29 yet.
- Next owner trial: backup/export any disposable test app logs, install/upgrade only MO Layer Test v0.9.0. Ideally test direct outside Renjana first, then separate run inside working Renjana P26 (owner-initiated) with ps mapping if PID changed. Press button **P29 Native GLES OFF/ON/OFF** once and share textual outcome with hook callback count, hit count, unhook status, original/active/restored values. Run no Shizuku actions unless needed for **read-only** current PID mapping.
