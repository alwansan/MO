# MO v0.7 real-device evidence / Outlanders gate
2026-11-07 Asia/Baghdad, MO-20261107-CHAT-P8-RECOVERY-013; origin=chat.
The user supplied POCO F5 Pro screenshots and two full reports from MO v0.7.0-alpha.7.
No access to the owner's OS, Shizuku, game files or stored app data from this session.

## Experiment result
- Separate owned `com.alwansan.mo.glesprobe` debuggable APK, Android15, new PID **20827**:
  - EGL_ANDROID_GLES_layers: **YES** (system supports the interface).
  - Java GLES20 GL_RENDERER: `Adreno (TM) 730` (unmodified).
  - NDK glGetString(GL_RENDERER): `MO-GLES-LAYER-PROOF (SIMULATED)`.
  - eglGetProcAddress("glGetString")(GL_RENDERER): same simulated renderer.
  - Mock Native FPS tier: UNLOCKED **(simulation only)**; observed scope NATIVE ONLY.
- Shizuku UID2000/GRANTED; four global GPU debug settings still set to MO proof:
  - enable_gpu_debug_layers=1
  - gpu_debug_app=com.alwansan.mo.glesprobe
  - gpu_debug_layers_gles=libMOGlesProofLayer.so
  - gpu_debug_layer_app=com.alwansan.mo.glesprobe
- Filtered logs show no matching loader entries (inconclusive); runtime returned replacement in native paths.

## Actual Outlanders installed package audit
- `com.tencent.mhadv` version `1.0.9.0.3308842`.
- Android Target SDK **36**, application debuggable **NO**, manifest `com.android.graphics.injectLayers.enable` opt-in **NO**.
- Official rootless GLES debug-layer manifest prerequisites **NOT MET**. Stock Shizuku UID2000 cannot override app signature or force stock production app to opt in.
- Android Game Mode standard/custom with **no interventions**; ANGLE selected packages `null`; Vulkan API1.1.128, Adreno 730. None imply FPS/quality unlock.
- Don't claim FPS boost; no indication Outlanders renders using GL or Vulkan in any particular circumstance. Actual game feature gating may involve local allowlists, renderer API, config, server or entitlement.

## SAFETY: stale setting still active and backup may be invalid
- Report after P7 still shows settings active: `enable_gpu_debug_layers=1` and exact owned target. One P7 report printed no private rollback session, later an enabled-session report; this suggests a **possible** missing or incorrect backup after APK reinstall.
- V0.7's previous code could capture an already-enabled four-key MO profile as the new "baseline" if user activated again after reinstall, then misleadingly restore to enabled=1.
- We CANNOT know if original snapshot survived or what original values of the other two keys were. Do not claim exact baseline restoration from a missing snapshot.
- First user action: in their CURRENT installed v0.7, try normal **استعادة إعدادات GPU Debug القديمة**, then read back four keys. If the values remain MO profile after an "OK", restore was ineffective against pre-test state.
- New v0.8 implements a strictly guarded CLEANUP ONLY when current values match all four exact MO proof keys, with user confirmation and persisted pre-cleanup snapshot; disables enable flag to known former value 0 and removes 3 owned values, verifies, attempts rollback if partial failure. This is NOT an exact reconstruction of unknown old values. User should only install v0.8 if normal restore of v0.7 fails or there is no usable backup, and understands per-run debug signing may require uninstall.

Official documentation: https://developer.android.com/ndk/guides/rootless-debug-gles


# Implementation

## MO-20261107-CHAT-P8-RECOVERY-013 — P7 real device and P8 safety fix
- User v0.7 report proved native and eglGetProcAddress renderer interception in owned debuggable MO Layer Test, PID 20827; Java path remains Adreno730. EGL_ANDROID_GLES_layers declared YES; synthetic FPS tier only, not real FPS gain.
- **Outlanders** com.tencent.mhadv version1.0.9.0.3308842, targetSdk36, Debuggable NO, opt-in `com.android.graphics.injectLayers.enable` NO: stock app not eligible for official external rootless GLES layer. Do not claim Shizuku can override this.
- **Urgent persistent settings:** Shizuku reports 4 MO debug keys still enabled. Earlier v0.7 could save already-active test state as original baseline, and user may have lost v0.5 backup across APK reinstall. Check normal Restore in currently installed v0.7 then re-read keys; a success string alone is insufficient.
- P8 source v0.8.0-alpha.8 adds `OwnedLayerRecoveryPolicy` and JUnit tests, refusal to activate when any debug config already exists, refusal to restore from a snapshot of active MO settings, and separately consented strictly guarded **MO-owned stale cleanup** (sets enable=0, deletes 3 exact owned markers only, captures pre-cleanup snapshot before writing, verifies, partial failure rollback).
- This cleanup is **not** reconstruction of unknown old baseline values. Keep original games untouched; need CI build result and user device confirmation.
- Evidence docs/DEVICE_FEEDBACK_ALPHA7_2026-11-07.md; recovery guide docs/GPU_DEBUG_RECOVERY_AR.md.
