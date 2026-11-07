# MO — повторный / repeated POCO owned P10/P11 run from cumulative P12 log
Date: 2026-11-07 Asia/Baghdad | session `MO-20261107-CHAT-P11-REPEAT-023` | origin=chat
Source: owner uploaded **new** private `log (2).txt` (41,724 bytes; SHA-256 `dbdfe2ecfa429012378459ae212e34c3181619ebe699dba055e30676d5bb7415`). This is a **cumulative retained-events export**, not 201 new events; never upload the raw file to public MO.

## Scope and differentiation from the prior file
- App: `com.alwansan.mo.glesprobe`, version `0.6.0`; Android 15/API35 on POCO F5 Pro (model `23013PC75G`, SoC `SM8475`).
- Export header timestamp `2026-11-07T05:47:32.182+0300`. Parsed **201** app event records from two process sessions:
  - Earlier `PID31111`: **85** retained records, `05:38:31.196`–`05:39:39.417`. Previous first upload at `05:39:35.968` had only 76 records; this cumulative export additionally contains later successful `EXPORT_RESULT`, a `FRAME_LAB_PAUSE`, lifecycle and memory events.
  - **New `PID3604`**: **116** records, `05:45:19.732`–`05:47:32.179`; this is the *new* independent device session analyzed here.
- The new snapshot ends at a `USER_ACTION` export request; that snapshot cannot include its **own** subsequently completed `EXPORT_RESULT`. The earlier session's recorded result at `05:39:36.090` reports saved `/storage/emulated/0/Download/log (2).txt` and app-reported clipboard copy of **15,943** characters. This is evidence that Android APIs returned success, **not independent verification that clipboard contents were subsequently pasted**.
- In earlier `PID31111`, logger recorded `FRAME_LAB_PAUSE` at `05:39:37.891` (callbacks1794, draws448) and `TRIM_MEMORY=20` after UI was hidden. This closes the missing prior in-app pause-event observation; it does not prove every lifecycle scenario.

## Newly observed P10 Game Identity Laboratory validation (PID3604)
- New owned-process Java/NDK/`eglGetProcAddress` `GL_RENDERER` and native Vulkan `deviceName`: **Adreno (TM) 730**. Real Android model `23013PC75G`, SoC `SM8475`, screen refresh setting **120.0Hz**. Repeated own-process readings kept these physical values unchanged.
- The user cycled through all seven **synthetic** options `ACTUAL`, `MODEL_ONLY`, `CPU_ONLY`, `JAVA_GLES_ONLY`, `NATIVE_GLES_ONLY`, `VULKAN_ONLY`, `ALL_SIMULATED`; repeated `IDENTITY_REFRESH` operations preserved actual identity.
- `mock_combined120=false` in `ACTUAL` and every **single** simulated channel; `mock_combined120=true` only in `ALL_SIMULATED`, and returned to `false` when changing to Vulkan-only or CPU-only. This validates the fictional combined gate's application state and reversibility. It does **not** alter Android Build, OpenGL/Vulkan properties or commercial-game settings.

## Newly observed P11 repeated frame pacing (PID3604)
Pacing values below are counts of **Choreographer UI callbacks** and owned-app `GLSurfaceView.Renderer.onDrawFrame` calls, not `SurfaceFlinger` frame presentations and not any game's measured FPS.

| App request mode | Samples | Mean Choreographer callbacks/s | Mean own GLES renderer callbacks/s | Total UI callbacks | Total draws | Observation |
|---|---:|---:|---:|---:|---:|---|
| `stride=1` | 13 | **120.061** | **120.061** | 1565 | 1565 | 1 draw per callback |
| `stride=2` | 16 | **119.667** | **59.834** | 1920 | 960 | 1 draw per 2 callbacks |
| `stride=4` | 9 | **120.478** | **30.037** | 1087 | 271 | approximately 1 draw per 4 callbacks |

- The new test observed **38** complete one-second sample windows, started by `FRAME_MODE` switches at `05:46:52.573`, `05:47:06.155`, and `05:47:22.690` respectively.
- Draw rates scaled by ~1, 1/2, 1/4 of reported UI callbacks; this **replicates** the earlier 45-window P11 result on the same device with a different PID. The drop in callback rate at reduced stride is intentional, not evidence of thermal or FPS gains.
- UI thread's refresh setting stayed near 120Hz. These measurements do NOT establish actual presented FPS, CPU/GPU power or performance in Monster Hunter Outlanders.

## Diagnostics and safety
- There are **zero explicitly logged** `ERROR`/`UNCAUGHT_CRASH` events in the entire cumulative log. This cannot exclude native failures that happened outside capture.
- Old session has a logged `EXPORT_RESULT` and `FRAME_LAB_PAUSE` event; new session has lifecycle transitions, `TRIM_MEMORY=20` when UI hidden. This level corresponds to UI hidden, **not proof of out-of-memory**.
- No `Shizuku` global settings readback appears because this is the independent disposable test app; the previous main app's last confirmed GPU Debug cleanup at 05:26:04 is the last directly measured global 4-key state. New PID3604 reported original Adreno730/no intercepted GLES, but it is **not** a contemporary 4-key proof.
- No root, bootloader, third-party package edit, OEM game process logging, GPU debug reactivation or new app source/build was performed as part of this analysis.
- The raw upload remains private. This public report stores only aggregated metrics and timestamps, not a verbatim copy of device logs.

## Acceptance and further work
- **P10 local, owned Game Identity Laboratory:** all seven selection modes and repeated `IDENTITY_REFRESH` device-verified. Fictional combined gate only unlocks in `ALL_SIMULATED`; no real game spoof.
- **P11 owned frame pacing:** three request strides passed independently **twice** in separate device process sessions (`PID31111` and `PID3604`).
- **P12 persistent log/Downloads export:** cumulative record from two process sessions proves retained history across process restarts and an app-reported completed export. Clipboard paste is still not independently verified; latest export request lacks its own result by snapshot timing.
- There is no source defect established that warrants new APK or CI run. Potential future independent measurement of actual presentation/jank is a distinct P13 research task requiring reliable Android-owned-app instrumentation, not an inference from `onDrawFrame`.
- Continue respecting: no root/bootloader unlock, no injection into stock `com.tencent.mhadv`, no copying iUnlockerGL proprietary binaries/code, no activation of global GPU layers just to repeat accepted tests.

## Related source reports
- `docs/DEVICE_FEEDBACK_P11_STRIDE_2026-11-07.md` — earlier first uploaded stride1/2/4 run in PID31111.
- `docs/DEVICE_FEEDBACK_P12_LOGS_2026-11-07.md` — main MO Shizuku safe four-key cleanup at 05:25.
- `docs/OWNED_GAME_IDENTITY_LAB_AR.md`, `docs/OWNED_FRAME_PACING_LAB_AR.md`, `docs/DIAGNOSTIC_LOG_EXPORT_AR.md`.
