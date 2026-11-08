# P25 device evidence — first owned guest storage/Vulkan test (2026-11-08)

**Follow-up session:** MO-20261108-CHAT-P26-RENJANA-STORAGE-PATCH-039 | **origin:** chat

## Images supplied by owner (private; not committed as images)
- Screenshot 1: \`1006531219.jpg\` (257610 bytes, SHA256 b46f9c75186ddca99e320fa05fbd492b27759b401c1d726dc18d9b69ed7e3eec).
- Screenshot 2: \`1006531220.jpg\` (325389 bytes, SHA256 1ad86da503740a09a9c117e375825c9965c972b90184d0bbbd6e227e4c204e99).
- These are **real phone screenshots** of MO Layer Test v0.7 P25 UI, unlike CI reports; package/process label from Rish corresponding to fresh PID8544 **was not included**. Prior Renjana Guest P23/P24 is independently supported by earlier process mappings. Treat current new PID as reported by app, not independently mapped to com.fesu.renjana:pN.

## New actual GLES/Vulkan values at PID8544
- Java Build.MODEL: XT2251-1, different from original POCO 23013PC75G; fingerprint spoof is consistent with the Renjana guest workflow, but current process mapping still missing.
- GL_VENDOR Qualcomm; Java GL_RENDERER Adreno (TM) 730; native NDK GL_RENDERER Adreno (TM) 730; eglGetProcAddress GL_RENDERER Adreno (TM) 730.
- Native Vulkan deviceName Adreno (TM) 730; OpenGL ES3.2 V@0615.95.
- Debuggable APK YES; EGL_ANDROID_GLES_layers declared YES; intercepted Java NO, Native NO, synthetic FPS tier LOCKED (simulation).
- **What this proves:** OpenGL and native Vulkan identity remain physical GPU in the measured app process; Build.MODEL spoof or changing presentation values does not alter native graphics properties. No Outlanders gameplay/FPS, effective independent game spoof, or present-rate benchmark.

## New actual private storage check at PID8544
| P25 owned-app check | On-device observation |
|---|---|
| Activity File.exists / isDirectory / canWrite | false / true / true |
| Activity direct Java File roundTrip / cleanup | PASS / PASS |
| Activity Context streams / cleanup | FAIL(FileNotFoundException) / NOT_FOUND_OR_FAILED |
| Native libc stat / roundTrip / cleanup | directory YES / PASS / PASS |
| Application File.exists / isDirectory / canWrite | false / true / true |
| Application direct Java File roundTrip / cleanup | PASS / PASS |
| Application Context streams / cleanup | FAIL(FileNotFoundException) / NOT_FOUND_OR_FAILED |
| Activity and Application canonical filesDir same | true |

### Reading correctly
- False File.exists while File.isDirectory and libc stat indicate usable directory is **strong device evidence for Java File.exists false negatives**; in pinned Renjana, AntiDetection.shouldHidePath hides paths containing renjana and CoreHooks.createFileExistsHook forces false when hidden.
- Upstream VirtualContext.openFileInput checks file.exists and throws FileNotFoundException if it returns false. This is a **source-backed explanation** for Context-stream failures even if underlying FileOutputStream writes succeed.
- The v0.7 storage probe reports the exception class only, **not whether failure occurred during Context.openFileOutput or during openFileInput**. Do not say the screenshot proves openFileOutput itself failed.
- Context cleanup also returned NOT_FOUND_OR_FAILED (VirtualContext.deleteFile checks exists before delete); it does not prove the temp file was actually removed. Only the direct Java File and libc temporary-file cleanup reports PASS. Do not claim 100% clean Context I/O.
- This does not prove entire guest filesystem isolation, GPU spoof, compatibility with Unreal Engine or large game.

## P26 engineering decision — validate causality with an owned-only diagnostic patch
- No change to upstream Renjana baseline release or MO main app. A separate GitHub workflow checks out pinned Renjana SHA14302a57 and applies a **one-call-site Kotlin modification** from tools/patch_renjana_owned_file_exists_p26.py.
- P26 skips installing CoreHooks File.exists cloaking hook ONLY for guest package com.alwansan.mo.glesprobe. Other packages retain the original behavior. Patch identifies itself as modified source and distributes exact diff + Apache-2.0 license.
- Test expected: in a fresh, truly virtualized MO Layer Test v0.7 guest, if File.exists becomes true, Context roundTrip and Context cleanup PASS while GLES/Vulkan remain Adreno730, that supports the specific Java hook explanation. If not, revisit underlying Context path mapping, exact exception stage and other system hooks. None of these results are proven before another on-device trial.
- No root, bootloader unlock, Google/game accounts, game APK/files, GPU debug Settings, ANGLE or third-party protected code. Installation of modified debug Renjana may require reinstall due signature mismatch; this can remove private Renjana instances, so owner should protect disposable instance data/logs first. Do not uninstall MO main or original game.
