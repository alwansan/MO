# MO v0.9.0-alpha.9 — owner device feedback and verified GPU Debug cleanup
2026-11-07 Asia/Baghdad | session `MO-20261107-CHAT-ZYGISK-COPG-AUDIT-016`
**Source:** 4 owner-supplied screenshots and 2 pasted MO Graphics Probe v0.9.0-alpha.9 reports. No direct remote device access. Do NOT copy proprietary third-party software or raw screenshots into public repository.

## Environment observed
- POCO F5 Pro / `23013PC75G`, `mondrian`, Qualcomm `SM8475` (Snapdragon 8+ Gen 1), Android 15 / API35, 120 Hz.
- Shizuku v13 API: Binder connected, permission GRANTED, effective ADB shell UID 2000.
- MO-process GLES: Qualcomm, `Adreno (TM) 730`, OpenGL ES 3.2 V@0615.95; Vulkan native GPU Adreno730, API1.1.128, vendor 0x5143.
- Outlanders global `com.tencent.mhadv` v1.0.9.0.3308842: targetSdk36, Debuggable NO, external GLES layer opt-in NO; Game Mode standard/custom, no existing interventions; ANGLE selection `null`. Stock game not compatible with official nonroot external layers.

## Screenshots: separate observations, do not invent ordering beyond evidence
- Owned MO Layer Test screenshot at around 04:10 shows `PID 16719`, Java/NDK/`eglGetProcAddress` renderer all `Adreno (TM) 730`, `Layer intercepted: NO`, Mock FPS tier `LOCKED (simulation)`. This demonstrates a **real Adreno baseline in that process**, not necessarily a **post-cleanup** relaunch because another screenshot around 04:12 shows subsequent layer activation.
- Screenshot of MO v0.9 `OWNED GLES LAYER TEST` after layer activation: `OK: experimental GLES layer enabled for MO TEST ONLY`; `[READBACK AFTER ACTION]` reports `enable_gpu_debug_layers=1`, `gpu_debug_app=com.alwansan.mo.glesprobe`, `gpu_debug_layers_gles=libMOGlesProofLayer.so`, `gpu_debug_layer_app=com.alwansan.mo.glesprobe`, UID2000. These are **active settings**, but not proof of native hook in a newly launched process. Already-running processes may retain old loader state. Logs show no matching layer messages (inconclusive).
- First pasted full report duplicates the enabled/active four keys (`1` plus owned markers), matching activation state.
- **Second pasted full report is post-cleanup**, explicitly:
  - `[SHIZUKU SYSTEM READ-ONLY] enable_gpu_debug_layers 0`; `gpu_debug_app null` (`settings get` null alone is not proof of absent key).
  - `[OWNED GLES LAYER TEST] OK: MO test settings cleaned, GPU debugging disabled (0)...`.
  - `[READBACK AFTER ACTION] enable_gpu_debug_layers = 0`, `gpu_debug_app = (not present)`, `gpu_debug_layers_gles = (not present)`, `gpu_debug_layer_app = (not present)`.
  - `لا توجد تغييرات معلقة مسجلة لدى MO.`
  - Thus **P9 post-action readback and panel refresh succeeded in the final pasted report**, unlike the earlier stale v0.8 panel. This verifies *at that reported moment* a cleaned MO-owned four-key state.
- Full exact **historical** pre-first-test backup cannot be reconstructed if lost; this is bounded owned-marker cleanup, not exact prior configuration restore. Never extrapolate to unrelated Android debug keys or system-wide effects.
- **Still missing:** definite new-process `PID` and Java/NDK/`eglGetProcAddress` screenshot taken **after the latest reported cleanup**. A future one-time read-only check is appropriate; do not re-enable just to collect it.

## What this proves and does not prove
| Observation | Status |
|---|---|
| Shizuku ADB UID2000 and read-only diagnostics | PASS on device |
| v0.9 refresh shows consistent post-action shell/layer 4-key readback | PASS from pasted final report |
| v0.9 guarded cleanup removed three exact MO markers and disabled GPU debugging | PASS at moment of final report |
| New-process renderer after latest cleanup | PENDING; PID16719 screenshot predates or has unproved relation to latest cleanup |
| Native GLES interception in **owned** debuggable process during older P7/P8 sessions | Previously observed; only simulated values |
| Java GLES intercept in owned process | NO in earlier controlled observations |
| Original Outlanders FPS/graphics option unlock | NOT demonstrated; app is nondebuggable/does not opt in |
| Root, game patch, App Cloner, VM or bootloader alteration | Not used by MO |

## Safe next operation
1. Do **not** activate the MO layer again merely to repeat screenshots.
2. In currently installed MO v0.9, use **read-only** `تشخيص GLES` to verify four keys still 0/absent, if the user has done other actions since final paste.
3. Force-stop **only `com.alwansan.mo.glesprobe`**, reopen, copy its **new PID**, Java/NDK/eglGetProcAddress renderer. Expect Adreno 730 across all paths with Mock locked; treat as an expectation, not already-proven post-cleanup observation.
4. If all consistent, mark P9 cleanup + fresh-process A/B rollback proven and proceed with **research-only** Game Identity Laboratory proposal. Do not modify game data or launch a new build without an explicit, narrowed goal.
5. If active markers return, no blanket settings cleanup: inspect the precise keys and stored MO snapshot, preserving any other app configuration.

## References
- `docs/DEVICE_FEEDBACK_ALPHA8_2026-11-07.md`
- `docs/GPU_DEBUG_RECOVERY_AR.md`
- `research/ZYGISK_COPG_NO_ROOT_FEASIBILITY_AR.md`
- https://developer.android.com/ndk/guides/rootless-debug-gles
