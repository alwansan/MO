# MO P10 — first real device test of the owned Game Identity Laboratory
Date: 2026-11-07 Asia/Baghdad
Session: `MO-20261107-CHAT-P10-DEVICE-018`
Source: **3 owner-supplied screenshots + a pasted MO Graphics Probe v0.9.0-alpha.9 report**; no remote control of the phone. Uploaded images are evidence only and are NOT copied into public MO.

## Device and version
- POCO F5 Pro model `23013PC75G`, SoC `SM8475` / Snapdragon 8+ Gen1, `mondrian`, Android 15/API35, Adreno (TM) 730.
- P10 disposable `MO Layer Test` v0.4.0 from [run25](https://github.com/alwansan/MO/releases/tag/mo-v0.9.0-alpha.9-run25) is **actually running on the phone**, and its `MO / GAME IDENTITY LAB` Activity is visible.
- Same test-app process **PID 5783** across owned GLES screenshot and both Identity Lab screenshots. Shown actual readings:
  - `Build.MODEL=23013PC75G`
  - `Build.SOC_MODEL=SM8475`
  - `Java GLES GL_RENDERER=Adreno (TM) 730`
  - `NDK GLES GL_RENDERER=Adreno (TM) 730`
  - `Native Vulkan deviceName=Adreno (TM) 730`
  - display refresh `120.0 Hz`
  - Owned GLES probe Java/Native/eglGetProcAddress GL_RENDERER physical Adreno730, layer intercepted Java NO, Native NO, mock tier LOCKED.
- **ACTUAL mode 0**: all synthetic Build/CPU/GLES/Vulkan gates `LOCKED (MOCK)`, 120Hz condition `UNLOCKED (MOCK)`, fictional combined 120 FPS menu `LOCKED (MOCK)`.
- **ALL_SIMULATED mode 6**: presented five MOCK strings explicitly marked `(SIMULATED)`; individual fake gates and fictional combined 120 FPS menu `UNLOCKED (MOCK)`, while the *real readings displayed above remain* SM8475/Adreno730/120.0Hz.
- This verifies UI and real Vulkan JNI query on the phone, state-switching local fake UI gates without changing physical graphics identity. It **does not verify** the other five intermediate per-channel modes on-device yet, nor actual game performance or 120 FPS in Outlanders. Their logic was validated in JUnit CI.
- No additional permissions/Root are shown or required by the lab; no evidence any commercial game data changed.

## Important current safety state — latest owner pasted report shows MO debug layer ACTIVE
- Owner's full MO v0.9 report, accompanying these photos, independently includes:
  - `[SHIZUKU SYSTEM READ-ONLY] enable_gpu_debug_layers=1`, `gpu_debug_app=com.alwansan.mo.glesprobe`.
  - `[OWNED GLES LAYER TEST]` and `[READBACK AFTER ACTION]` both show `enable_gpu_debug_layers=1`, `gpu_debug_app=com.alwansan.mo.glesprobe`, `gpu_debug_layers_gles=libMOGlesProofLayer.so`, `gpu_debug_layer_app=com.alwansan.mo.glesprobe`.
  - Shizuku ADB Shell UID2000 / permission GRANTED; filtered loader log `No matching layer messages` (inconclusive).
- Thus the **latest supplied report** describes MO-owned GPU Debug settings **enabled**, unlike the previous P9 last report of 0/absent. Either test activation was re-run or the reports reflect different times. Do not infer cause without an action log. **Never overwrite this current observation with older clean proof.**
- Observing physical Adreno730 in a process PID 5783 when settings are enabled is consistent with *no intercept observed in that process*. It does not prove GPU Debug settings are off, and it does not prove the layer could never load in a newly launched process.
- This lab is exclusively a simulated menu; its UI has no code to activate global layer settings. The active flags originated from a separate MO Graphics Probe control transaction or persisted state, not merely choosing the lab's `ALL_SIMULATED` option.

## Immediate owner action / rollback gate
1. **Do not uninstall, update or clear data of MO Graphics Probe** while this state exists; its private snapshot may hold an original baseline. Do not re-activate the GPU layer or touch stock game installations.
2. In the **currently installed MO main app** use **«استعادة إعدادات GPU Debug القديمة»** if a trustworthy pre-activation snapshot exists. Then run the **read-only** GLES diagnosis and copy exact four keys; a success toast alone is insufficient.
3. If restore refuses (no backup, protected active-MO snapshot, changed external setting), retain MO unchanged and share exact error and fresh readback. The documented guarded **MO-only stale cleanup** is an opt-in fallback **after** verifying the four markers match, *not* exact historical baseline restoration. It requires explicit user consent; it may set enable=0 and delete three MO-only keys.
4. Accept rollback at that moment only if `enable_gpu_debug_layers=0`, `gpu_debug_app=(not present)`, `gpu_debug_layers_gles=(not present)`, `gpu_debug_layer_app=(not present)`, or another accurately restored preexisting non-MO state supported by saved snapshot and proven readback.
5. **Only after rollback** force-stop/reopen the disposable MO Layer Test; record **new PID** and Java/NDK/eglGetProcAddress physical renderer. This closes the post-rollback process evidence gap.
6. No phone changes were made by this conversation; these are owner-side steps, not remotely executed operations.

## Acceptance status
| Check | Result |
|---|---|
| P10 owned lab opens on Android 15 | PASS owner screenshots |
| Owned Java/NDK GLES + native Vulkan readings from same process | PASS; real Adreno730 |
| Mode0 real-only locks synthetic combined menu | PASS |
| Mode6 all synthetic unlocks simulated combined menu | PASS |
| Real FPS unlock/Outlanders profile spoof | **NOT DONE / NOT SHOWN** |
| Official stock Outlanders external GLES eligibility | NO (targetSdk36, debuggable NO, opt-in NO) |
| Current GPU Debug rollback | **ACTION NEEDED — latest report 1 + three MO markers** |
| Post-restoration fresh PID / real renderer | PENDING |
| Stable APK signing across CI | PENDING |

## Evidence links
- `docs/OWNED_GAME_IDENTITY_LAB_AR.md`
- `docs/DEVICE_FEEDBACK_ALPHA9_2026-11-07.md`
- `docs/GPU_DEBUG_RECOVERY_AR.md`
- [Successful P10 source build 42324225397](https://github.com/alwansan/MO/actions/runs/42324225397)
