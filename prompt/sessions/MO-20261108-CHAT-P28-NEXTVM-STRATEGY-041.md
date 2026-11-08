# MO-20261108-CHAT-P28-NEXTVM-STRATEGY-041
2026-11-08 Asia/Baghdad | origin=chat | branch=main | type=research / direction choice
Starting HEAD 24892a396c10c12227427fcc20e00790a418e363

## Inputs and live restoration
- Owner uploaded eight screenshots from Renjana + MO Layer Test v0.8.0 and supplied link https://github.com/TanvirHossain2/NEXTVM, asking whether to stop developing Renjana and choose a more workable native GPU identity spoof route. User did NOT request a new APK or global settings changes.
- Fetched current MO live HEAD, sessions through P27-040, Actions/release history and AGENTS/RESTORE/PLAN/DECISIONS/security. No newer session before P28. Read P20 RemoteConfig result and P21 prior container comparison to avoid repeating or contradicting earlier research.
- Current verified v0.8.0 APK from run 42435412976 (#71); P26 Renjana modified owned lab from run 42434382187. No new build necessary in P28.

## Real new P27 device observations
- Image hashes and privacy-safe values detailed in research/P28_NATIVE_FIRST_CONTAINER_STRATEGY_AR.md. Raw screenshots contain private instance paths/serial/device IDs and were NOT copied to public repository.
- Instance UI says MO Layer Test v0.8.0. Inside app Build.MODEL NE2215, Java/NDK/EGL GLES Adreno730, native Vulkan Adreno730, Java/Native interception NO; mock FPS tier LOCKED. P27 dladdr reports linked/egl/RTLD_DEFAULT glGetString all libGLESv2.so, both pointer equality YES; linked/RTLD_DEFAULT Vulkan vkGetPhysicalDeviceProperties libvulkan.so, equality YES. These are public loader symbols in a specific owned PID; not proof of ultimate Qualcomm ICD call or a generic hook.
- In-app PID23440; there is NO current Rish ps PID-to-process-name mapping for PID23440. Previous P26 PID17047 mapping cannot be reused. P27 Activity/Application File.exists/Context read-write/cleanup and native libc stat/I/O/cleanup PASS, consistent with P26 successful owned-app storage fix.
- Renjana Config screenshot GMS, Signature Spoof, Anti-Detection and Fingerprint Spoof toggled ON; no evidence user entered Google account. We do not ask to sign in, and do not change it automatically.

## Live NEXTVM and alternative source checks
- NEXTVM upstream HEAD f581a6642596db396a6fe606addd735979403b6b Apache-2.0. Source contains actual ELF GOT/PLT patcher in core/hook/src/main/cpp/native-hook.cpp, but g_hook_entries only libc open/openat/access/stat/lstat/readlink/fopen/__system_property_get, not glGetString, eglGetProcAddress, or vkGetPhysicalDeviceProperties. Java IdentitySpoofingEngine changes Build fields; HookEngine describes inline hook solutions as future. nativeInit may report initialized even if PLT patch fails. No GitHub Releases, some CI failures/action_required, old CI success for PR not current default main. Open NEXTVM issue #5 details failed Android10+ guest launch, ActivityTaskManager routing, ABI handling and unresolved acceptance. Therefore NO basis to claim NEXTVM better than Renjana P26.
- Already P21 researched NEXTVM, and owner previously tried BlackBoxReborn with Outlanders unsuccessfully. P28 corroborates instead of restarting entire analysis.
- ByteHook (bytedance/bhook) MIT supports arm64/Android15 PLT and newly-loaded libraries. Official Android GLES and Vulkan external code loading restrictions still apply; ByteHook does NOT inject into closed third-party processes without app cooperation/container-supplied code. No Root/Shizuku privilege escalation.

## Decision and changes
- Research report created: research/P28_NATIVE_FIRST_CONTAINER_STRATEGY_AR.md, commit a0cbf226a69f35c30f191835b54f458a4279bebe.
- Pivot from container swapping to scoped native-first R&D, preserve Renjana P26 known-good for owned testing. P29 native GLES in owned app -> P30 separate Vulkan owned -> P31 optional Renjana Guest-delivered hook independent of app changes. Each requires actual API readback, same PID, hook count, off/on/unhook and no feature/FPS exaggeration.
- No game file/APK/signature/account interaction; no global ANGLE/GPU debug; no root/bootloader or proprietary code reuse. No build, Release or device actions executed in P28. Follow user safety and non-root/no-account constraints.
- Next safe implementation task, if requested, is controlled ByteHook proof in disposable owned MO Layer Test without touching Renjana main baseline. If failed, stop/pivot with evidence instead of substituting another virtualizer.
