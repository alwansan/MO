# MO-20261108-CHAT-P21-LIGHT-CONTAINERS-034 — Rootless lightweight containers for Outlanders
Date: 2026-11-08 Asia/Baghdad. origin=chat. Repository alwansan/MO branch main.

## User task
Research very lightweight fully open-source Android-emulating app allowing Monster Hunter Outlanders (Global com.tencent.mhadv) to run inside guest, with altered perceived device Build/CPU/GPU identities, Android15 ARM64 POCO F5 Pro SM8475 Adreno730, no Root/bootloader or heavy VMOS+. Owner previously attempted BlackBoxReborn and it failed to launch MHO. User requested broad live research, not permission to modify original game or to run a new APK.

## Evidence and source code read
- Read live MO AGENTS, RESTORE, PLAN, MEMORY, STATUS, RESEARCH, architecture/security and prior P20 reports. Last main head prior to writes: 6eb7ee429877e7aa0dfcdd663dbcc524cd442cb5.
- Renjana: actual InstanceLauncher.kt launch strategy StubActivity isolation first, then a direct-launch fallback WITHOUT isolation. CoreHooks/PineHookManager lack direct GL/Vulkan string interception. DeviceFingerprint covers IDs. No public GitHub Release or Actions run; issues #1/#2 ask for a binary. Apache2, pushed Aug25 2026. Untested for MHO or POCO.
- NEXTVM: actual native-hook.cpp hooks libc open/access/stat and __system_property_get, NOT glGetString/vkGetPhysicalDeviceProperties. IdentitySpoofingEngine mutates Java Build; NativeHookBridge warns phased native implementation. No release. CI action_required/failure; open PR #5 for guest launcher. Apache2; no in-game proof.
- BlackBoxReborn user previously tried and MHO did not launch. Not return to it unprompted.
- Twoyi rootless full guest but discontinued, archived Apr20 2023; README Android host support 8.1–12, ROM not fully source-buildable. 
- Ananbox rootless guest with PRoot; WIP, Binder compromises, 2026 release v0.0.2 APK 11.7MB plus ARM64 ROM 189.6MB, Android15 black-screen issue #13 open.
- Shelter Work Profile runs apps via normal Android framework, but no GPU/Build identity spoof.
- DroidVM Root and virtualization kernel node required and newer Snapdragon; prohibited/impractical.
- ZCore free Android14 and below claims; Android15 full features paid proprietary source; not fully open.
- Older VirtualXposed Android12 release and VirtualApp business-only modern support; Linux DroidNative is NOT APK runner. Android AVF Microdroid restricted system API, not normal UI/game environment.

## Result and action limits
No verified zero-overhead, ready, fully FOSS, non-root Android15 virtualizer known to launch MHO native/Unreal split APK with host hardware GPU and host Google login AND spoof both GL/Vulkan device identity inside game. User-space app virtualization lower expected overhead than full guest OS, but unmeasured. Build.MODEL is not glGetString or native Vulkan; fake string doesn't add GPU capabilities or guarantee unlocked FPS; game may check server and anti-virtualization.
Only plausible experimental R&D candidate Renjana, after license/dependency audit and owned disposable test app using host GL and Vulkan, verifying launched INSIDE stub not direct fallback; NEXTVM useful secondary native property hook source but no proven correct GL hooks. Do not install/outsource user's game data and accounts, do not delete/resign original.
Detailed report: research/P21_LIGHTWEIGHT_ROOTLESS_ANDROID_CONTAINER_2026-11-08_AR.md.
No APK/CI triggered or game/phone changes. No proprietary iUnlocker assets copied.
