# MO-20261108-CHAT-P29-FEEDBACK-LAYER-SEPARATION-043

**Date:** 2026-11-08 Asia/Baghdad  
**Origin:** chat | **Branch:** main | **Source HEAD at start:** 50b70d841e6319c837721461925c1426fda3112d

## Restored live state
Fetched current GitHub main HEAD, sessions through P29-042, Actions #78 SUCCESS, pinned owned MO Layer Test v0.9.0 source and code in probe/src/main/cpp/mo_gles_layer.cpp and probe/src/main/cpp/mo_p29_gles_hook.cpp. No newer work before 043.

## Real owner device results
User supplied 1006531235.jpg, 1006531236.jpg and repeated Rish \`id\`/\`ps\`. Full privacy-safe transcription, image SHA256 hashes and interpretation: docs/DEVICE_FEEDBACK_P29_GLES_LAYER_SEPARATION_2026-11-08_AR.md. Do NOT publish raw images or private device fields.

- Rish \`id\`: uid=2000(shell), SELinux u:r:shell:s0. Shizuku ADB-shell mode confirmed NOT root.
- Host standalone MO app PID414 -> Android Build.MODEL 23013PC75G; Java GLES Adreno730, Native GLES and EGL function pointer return **MO-GLES-LAYER-PROOF (SIMULATED)**; Vulkan Adreno730, mock FPS UNLOCKED (simulation only).
- Renjana main PID3201; Guest com.fesu.renjana:p0 PID3585 == app screenshot PID3585. Guest Build.MODEL ASUS_AI2302, all Java/Native/EGL GLES and Vulkan Adreno730, legacy layer native NO, mock FPS LOCKED (simulation only).
- Storage Activity/Application direct Java File, Context streams, native libc stat/read/write/cleanup PASS in BOTH snapshots. Confirms P26 owned storage functional in this new Guest.
- P27 symbol path in both still libGLESv2.so direct/EGL/RTLD_DEFAULT, libvulkan.so linked/default, equal pointers YES; symbol origins are not proof of vendor ICD or inability to hook.

## Critical distinction
- \`MO-GLES-LAYER-PROOF (SIMULATED)\` is the literal from *legacy* MO-owned \`mo_gles_layer.cpp\` (AndroidGLESLayer_GetProcAddress), and \`MockGraphicsTier.isProofLayerRenderer\` maps that text to its Native Layer YES / mock FPS UNLOCKED indicator. The code only detects this previous GLES proof layer.
- New ByteHook P29 uses the distinct marker \`MO_P29_OWNED_GLES_HOOK_ONLY\`, only activates when the explicit P29 button is pressed, and logs callback hits + uninstall. Both screenshots show \`P29 Native GLES interception: NOT RUN on this process.\`
- Thus **P29 on-device hook result NOT TESTED; neither PASS nor FAIL**. Do not misattribute legacy layer effects to ByteHook. The host screenshot is confounded by previous GLES proof-layer installation, not clean native baseline.
- No information provided about exact current GPU Debug/ANGLE Settings.Global keys. Do not enable/disable global system layers.

## Next controlled action
No new APK build or Actions necessary: already available MO Layer Test v0.9.0 from https://github.com/alwansan/MO/releases/tag/mo-v0.11.0-alpha.11-run78 . Best isolate first **inside existing Renjana P26 Guest** (which currently reads real Adreno730); press P29 button once. Owner can send visible P29 report with linked baseline/during/after, callback/hits, unhook. If PID changed, use read-only ps in Rish again; no need to repeat old P25/P26/P27 testing. Optional second test outside container under active legacy layer is a separate layered scenario. Do not reset settings without explicit device-owner approval. No Outlanders game files or account manipulation, no Root/Zygisk/Bootloader. Keep ByteHook native hook only in owned application.
