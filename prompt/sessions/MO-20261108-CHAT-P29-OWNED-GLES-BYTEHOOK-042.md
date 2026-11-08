# MO-20261108-CHAT-P29-OWNED-GLES-BYTEHOOK-042

**2026-11-08 Asia/Baghdad | origin=chat | branch=main | P29 first native intercept gate**  
**Starting main HEAD:** df40b39abdb8a5d0ca09c4e2edfc19ff54e67d3e (P28 decision)

## User instruction and Shizuku availability
User requested "Continue" and offered Shizuku as simulated root. Explained explicitly: Wireless Debugging/ADB Shizuku is **UID2000 shell, not UID0 root**, and cannot bypass Android15 SELinux, patch vendor drivers or inject native code into a non-debuggable game. Shizuku may be used for read-only id/ps/meminfo/Logcat for the owned process; never for system-wide GPU setting modifications or playing with game data.

## Source restoration and P29 design
Live GitHub main, P28 decisions/plans, probe Activity/NativeRenderer/JNI/CMake/Gradle reviewed. ByteHook upstream tagged release and API inspected via GitHub: MIT https://github.com/bytedance/bhook; single caller PLT hook API, automatic mode BYTEHOOK_STACK_SCOPE/CALL_PREV, callback statuses/unhook. Main design P29 narrowly scopes glGetString(GL_RENDERER) to only **libMOProbeRenderer.so** in our disposable app, never global hook_all/partial or system driver functions.

- probe/src/main/cpp/mo_p29_gles_hook.cpp: opt-in A/B/A Native linked GLES, ByteHook single-caller import replacement, thread-local owned test string MO_P29_OWNED_GLES_HOOK_ONLY, calls previous original via ByteHook macro, validates hook callback OK count/hits and unhook+restoration. EGL-proc direct query remains separate; no Vulkan modifications. Commit 7c63ec95926a0d91192a8477156c401e58b74bf6.
- probe/build.gradle: only disposable MO Layer Test versionCode9/versionName0.9.0; Prefab enabled; ByteHook pinned. Initial commit 9313a08991b0a1034345168de4ad0b469381fd89, corrected pin commit 5bae606e668a3db5bffc473993be159166ba0aa7.
- CMake new source and ByteHook prefab linking commit d90f34143f95da63b225b257e0d42d747703c979.
- ProbeNativeRenderer.java ByteHook Java init and native API commit 027a966a3159213302744d1250da9972b3812ce7.
- ProbeActivity.java separate opt-in P29 button, reuses real offscreen EGL context, threads and diagnostic log commit 10278ff610e1bdb76c4175b4bdb57a202bf893a6.
- docs/P29_OWNED_GLES_BYTEHOOK_AR.md first commit 0abb5d66b382306161e60dc3c1a1efa2e09f8ec3, corrected pin and failure notes commit a9c7e6b471b4a0b806c5f2a15317cb29f9cedfaa.
- mo_p29_gles_hook.cpp version text corrected commit ead4f202ea5b9aa957d6edc9a9532004538836c8.

## CI evidence, not phone evidence
- Initial GitHub Actions run #76 https://github.com/alwansan/MO/actions/runs/42437452741 **FAILED at AAR metadata validation before native compile**: ByteHook v1.1.2 + transitive ShadowHook v2.0.1 require compileSdk37, whereas MO target is compileSdk35 and AGP8.9.2. This is dependency compatibility, NOT evidence that GLES hook failed.
- Compared pinned upstream build.gradle: ByteHook v1.1.0 compiles against Android35 and depends on ShadowHook v1.1.1; all native hook APIs used by P29 still exist at v1.1.0. Fixed only probe dependency/source/docs, no project-wide SDK upgrade.
- Auto CI run #78 https://github.com/alwansan/MO/actions/runs/42437618103 for corrected C++ source commit ead4f202ea5b9aa957d6edc9a9532004538836c8 was pending at documentation time. Must verify full successful run, Release asset upload and SHA before recommending APK. P29 v0.9.0 is NOT yet device tested and cannot be marked intercepted based on source/CI.
- Existing P27/ P26 results remain true and not repeated: P26 Guest storage PASS mapped PID17047 in Rish; P27 actual symbols libGLESv2.so/libvulkan.so with Adreno730, process PID23440 lacked independently matched Rish pid.

## Safety and next action
Only run owned APK P29 under opt-in button, ideally direct outside Renjana first. Test result includes baseline, during, after, callback status, true proxy hit counter and unhook result, with normal string restoration. If direct-host succeeds, consider a new owned-only Renjana P26 Guest test with read-only current PID mapping; if it fails, inspect hook result and stop, don't broaden to closed games or arbitrary libraries.
No Root, Bootloader, Zygisk/KernelSU, no system Settings / ANGLE / GPU Debug writes, no Outlanders files/APK/signature/account changes and no false claims of unlock/real FPS. ByteHook's MIT license must be attributed.


## P29 final GitHub Actions and Release verification (2026-11-08)
- Run #77 https://github.com/alwansan/MO/actions/runs/42437612759 SUCCESS for first compileSdk35-compatible ByteHook pin. Final corrected source run #78 https://github.com/alwansan/MO/actions/runs/42437618103 **completed SUCCESS**, source commit **ead4f202ea5b9aa957d6edc9a9532004538836c8**.
- Public prerelease https://github.com/alwansan/MO/releases/tag/mo-v0.11.0-alpha.11-run78 contains **MO-Layer-Test-v0.9.0.apk** uploaded (**342024 bytes**, SHA256 **3718601e4e26b31da697a4cdb04f50585fd4405e18af4dc6f1d32c1e73606041**): https://github.com/alwansan/MO/releases/download/mo-v0.11.0-alpha.11-run78/MO-Layer-Test-v0.9.0.apk .
- Existing workflow also rebuilt MO Graphics Probe main APK; **do not replace main MO** to test P29 (different debug signature/rollback data risk).
- ByteHook 1.1.0 + transitive ShadowHook1.1.1 compiled successfully for arm64. CI native compile and test success does **not** establish on-device hook callback hits or actual GL string interception. No device output P29 yet.
- Next owner trial: backup/export any disposable test app logs, install/upgrade only MO Layer Test v0.9.0. Ideally test direct outside Renjana first, then separate run inside working Renjana P26 (owner-initiated) with ps mapping if PID changed. Press button **P29 Native GLES OFF/ON/OFF** once and share textual outcome with hook callback count, hit count, unhook status, original/active/restored values. Run no Shizuku actions unless needed for **read-only** current PID mapping.
