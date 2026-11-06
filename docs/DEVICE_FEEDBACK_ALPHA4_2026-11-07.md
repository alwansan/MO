# MO P4 GLES layer real-device feedback — partial FAIL
Date 2026-11-07 Asia/Baghdad | session MO-20261107-CHAT-P4-FEEDBACK-011
Source: owner supplied 3 screenshots and two complete MO v0.4.0-alpha.4 diagnostic reports from POCO F5 Pro Android 15, Shizuku UID2000. No phone direct access.

## Before test
- MO Layer Test disposable separate APK `com.alwansan.mo.glesprobe` correctly installed/runs.
- `Android Build.MODEL=23013PC75G`; `GL_VENDOR=Qualcomm`; `GL_RENDERER=Adreno (TM) 730`; GLES 3.2 V@0615.95; synthetic `Mock 120 FPS: LOCKED`; `Layer intercepted: NO`.
- MO/Shizuku before: `enable_gpu_debug_layers=0`; `gpu_debug_app=null` (and four-key exact snapshot saved in app-private prefs by current code).

## After activation
- MO confirmed `OK: experimental GLES layer enabled for MO TEST ONLY`, then later reported `توجد نسخة احتياطية سابقة. استعدها أولاً قبل التفعيل.`
- `enable_gpu_debug_layers` changed `0 -> 1`, `gpu_debug_app=com.alwansan.mo.glesprobe`. No evidence from user of values of `gpu_debug_layers_gles` and `gpu_debug_layer_app`; implementation writes them, but only a new readback can confirm actual phone values.
- Disposable test app screenshot **still** `GL_RENDERER: Adreno (TM) 730`, `Mock 120 FPS: LOCKED`, `Layer intercepted: NO`. This is **not proof that Android loaded the library**. The test may not have re-created a process after activation; force-stop/restart was advised but device screenshot alone does not verify new process/PID.
- No report of restoration yet. **Safety action first: in existing MO v0.4 press Restore original GPU Debug settings, confirm success and original baseline BEFORE changing/removing/updating MO**.
- Native layer loader failure cause not yet identified. Need evidence from EGL loader and test app after forced process restart. Do not invent OS prohibition or API hook success.
- `Monster Hunter Outlanders` original untouched `com.tencent.mhadv` version `1.0.9.0.3308842`; no FPS/graphics tier unlocked.

## Engineering response in v0.5
- Added separate native C++ GLES query alongside Android `GLES20.glGetString` in the owned probe; reports process PID, Java vs native renderer, and debuggability to distinguish same-process issues.
- Added **manual** test of `System.loadLibrary("MOGlesProofLayer")` to detect missing/unloadable ELF, clearly *not* proof of EGL layer injection.
- Added Shizuku UID2000 **read-only** diagnostics of four exact settings, installed owned APK, global layer property, and filtered EGL tag logs that only retain references to owned `com.alwansan.mo.glesprobe` / library. Does not read general device accounts or apply writes.
- Current v0.4 saved backup state is app-private; never recommend installing v0.5 before restore, as debug certificates vary across CI runs.

## Android official references
https://developer.android.com/ndk/guides/rootless-debug-gles
https://android.googlesource.com/platform/frameworks/native/+/refs/heads/master/opengl/libs/EGL/GLES_layers.md
