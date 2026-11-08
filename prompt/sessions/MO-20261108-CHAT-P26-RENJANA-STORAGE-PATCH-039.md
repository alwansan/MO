# MO-20261108-CHAT-P26-RENJANA-STORAGE-PATCH-039
**2026-11-08 Asia/Baghdad | origin=chat | branch=main**
**Source main HEAD at session start:** d6a9680b52a0baecb22f22c8c60019c77900c464

## Inputs / phone proof
User supplied TWO screenshots from MO Layer Test v0.7 P25:
- first SHA256 b46f9c75186ddca99e320fa05fbd492b27759b401c1d726dc18d9b69ed7e3eec (257610B)
- second SHA256 1ad86da503740a09a9c117e375825c9965c972b90184d0bbbd6e227e4c204e99 (325389B)
Raw images remain private to chat; evidence transcription is docs/DEVICE_FEEDBACK_RENJANA_P25_2026-11-08_AR.md.
App-reported PID8544; **no Rish process-name mapping for that PID supplied**. Java Build.MODEL XT2251-1 (different from host), Java/NDK/eglGetProcAddress GL_RENDERER Adreno730, native Vulkan deviceName Adreno730, EGL_ANDROID_GLES_layers YES, both layer interceptions NO; synthetic mock FPS tier LOCKED. No graphics spoof proven and no game changes.
Activity and Application filesDir File.exists FALSE, isDirectory TRUE, canWrite TRUE, direct Java File write/read and cleanup PASS, native libc stat directory YES and native roundTrip/cleanup PASS, canonical dir equal. But BOTH Context streams report FAIL(FileNotFoundException), cleanup NOT_FOUND_OR_FAILED. Source code of our OwnedStorageProbe catches IO exception for BOTH output and input in one handler; hence **cannot identify whether the exception came from openFileOutput or openFileInput solely by screenshot**.

## P26 source analysis of pinned Renjana
Pinned upstream SHA 14302a57cd66114c6979acf7ca56c97f845a6841 reviewed live.
- AntiDetection.initialize adds app dataPath / Renjana-internal storage roots to hidden paths; shouldHidePath returns true for any "renjana" containing path.
- CoreHooks.createFileExistsHook overrides File.exists result to false for hidden paths.
- VirtualContext.openFileInput calls file.exists then throws FileNotFoundException if false. Its deleteFile similarly checks file.exists before deleting, and ensureDir uses exists/mkdirs/exists. This matches the observed false-negative conditions and may explain P24 directory warnings and Context I/O failures.
- This is a **strong source-and-phone-backed hypothesis**, NOT verified causal repair until P26 patched Renjana works under the same guest conditions. We do not claim a Vulkan or GLES native hook.

## P26 change / isolation
- tools/patch_renjana_owned_file_exists_p26.py (commit efe54f4dfff78fe222793d46e5bc185dc957eb03) applies exactly one guarded substitution in the upstream PineHookManager.installGuestHooks call site. For package com.alwansan.mo.glesprobe only, skip hookFileExists(). All other packages retain original hook installation. Fails on source drift or duplicate application. No private Renjana fork or third-party modules copied into MO.
- tools/test_patch_renjana_owned_file_exists_p26.py (commit 9d6eb3e8ecf40f0853a5b14796eb16fd6231ad59) checks one-package guard, source drift and repeat-patch refusal.
- .github/workflows/mo-renjana-owned-storage-p26.yml (commit 20627f8a6d5317c7137c3c119fd3a9324456d6fb): a NEW independent, non-cancelling CI. Checks out original upstream SHA + MO patch, checks Apache license and one-file diff, runs Python patch tests, upstream Renjana JUnit, ARM64 Gradle APK build, APK signature/CRC and SHA256, publishes separate clearly labeled prerelease with original license + exact patch diff and provenance. Never modifies old successful Renjana workflow or its Release.
- P26 Actions run https://github.com/alwansan/MO/actions/runs/42434382187 started on commit 20627f8a6d5317c7137c3c119fd3a9324456d6fb. Do not report success until run and Release asset confirmed.
- docs/DEVICE_FEEDBACK_RENJANA_P25_2026-11-08_AR.md (commit 30d100fd96dd68cefab51cc769cb13e95057a0a0) documents images, values, Context uncertainty, causal test acceptance.

## Device safety and next evidence
Only disposable MO Layer Test inside Renjana. No root, bootloader unlock, KernelSU/Zygisk, global GPU layers/ANGLE, game files, game APK, or Google/game accounts. P24 Settings.Global readings still UNKNOWN.
Debug-signed Renjana APK may have a different signature; installing patched Renjana could require removing old Renjana and lose its guest instances and logs. Do NOT casually overwrite/uninstall; export only disposable test logs first and allow the device owner to choose. Do not touch installed MO main or game.
After verified P26 APK: only compare same-PID owned P25 v0.7 storage results on a fresh guest started via patched Renjana. Expected causal signal: File.exists becomes true and Context stream + cleanup PASS with direct Java/native still PASS. If result differs, diagnose exact output/input stage and Context dataPath, do not assume solved. Vulkan/GLES spoof not part of this patch.
