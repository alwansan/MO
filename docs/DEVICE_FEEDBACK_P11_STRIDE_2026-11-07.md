# MO P11 — real Android15 stride=1/2/4 callback-rate validation
Date: 2026-11-07 Asia/Baghdad | Session: `MO-20261107-CHAT-P11-STRIDE-022`
Evidence: private owner-uploaded **`log (2).txt`** (15,982 bytes, SHA-256 `66a24cae18b578d22624e15439dbbf7693ef9a50c6b4b5c8d7f72bfa34d76cff`). The raw private upload is **not** included in this public repository.
Status: **PASS** for MO-owned, application-side frame-request cadence, not real screen-present FPS.

## File scope and device
- Application: `com.alwansan.mo.glesprobe`, v`0.6.0`; Android 15/API35 on POCO F5 Pro `23013PC75G`, `SM8475`.
- Exported at `2026-11-07T05:39:35.968+0300`. Captured **76 app-owned records** across `05:38:31.196–05:39:35.966+0300` (~64.77 s), single process `PID 31111`. No `ERROR`, `UNCAUGHT_CRASH`, or other Java-crash categories were present.
- Process was created **after** the prior main-app logged GPU Debug four-key cleanup at `05:25:51` (last repeated readback `05:26:04`). The new process reports physical `Adreno (TM) 730` in Java, native NDK and eglGetProcAddress; identity lab also reports Vulkan deviceName `Adreno (TM) 730` and panel `120.0 Hz`. This is new-process physical renderer evidence **after earlier known cleanup**, but the new upload has **no contemporaneous Shizuku four-key values**, so current global settings cannot be independently proven from this probe-only log.
- Identity modes observed: `ACTUAL` (mock_combined120=false) and `NATIVE_GLES_ONLY` (mock_combined120=false); physical GPU remains real. No stock Outlanders game code or graphics menu was touched.

## P11 45 one-second windows (all three requested modes)
Measurements are **`Choreographer.doFrame` callback counts** and **MO-owned GLES `Renderer.onDrawFrame` callback counts**, not SurfaceFlinger-presented frames, physical pixels, or FPS of any commercial game.

| Request cadence | Mode switch (+0300) | Windows | Mean UI callback/s | Mean owned GLES draw callback/s | Draw min–max /s | Draw callbacks / Choreographer callbacks |
|---|---|---:|---:|---:|---:|---:|
| `stride=1` (every callback) | 05:38:49.578 | 14 | **119.632** | **119.632** | 114.681–120.699 | 1.000 |
| `stride=2` (every 2 callbacks) | 05:39:03.649 | 19 | **119.127** | **59.563** | 56.776–60.845 | 0.500 |
| `stride=4` (every 4 callbacks) | 05:39:22.983 | 12 | **120.316** | **30.079** | 29.777–30.925 | ~0.250 |

Aggregate raw counts from each interval window:
- stride1: **1,680 UI callbacks / 1,680 draws** over ~14.042 seconds.
- stride2: **2,270 UI callbacks / 1,135 draws** over ~19.058 seconds.
- stride4: **1,448 UI callbacks / 362 draws** over ~12.034 seconds.

The mean draw rate declines **50.21%** (stride2) and **74.86%** (stride4) relative to the observed stride1 mean. Sample-to-sample jitter exists (e.g., 114.681 callbacks/s in a stride1 window, 113.552 callbacks/s in a stride2 window). This test does **not** measure GPU utilization, power draw, thermal behavior or SurfaceFlinger present rate. Claiming reduced energy consumption from these numbers would be unproven.

## Events, order and diagnostic coverage
- `05:38:31.196` — process `PID31111` starts. GEL/GLES reads return physical Adreno730; both intercepted flags NO.
- `05:38:39.639` — identity lab physical Vulkan and Java/NDK GLES same Adreno730; screen 120Hz.
- `05:38:49.578` — `FRAME_MODE request_stride=1`; 14 `FRAME_SAMPLE` windows.
- `05:39:03.649` — `FRAME_MODE request_stride=2`; 19 `FRAME_SAMPLE` windows. Mean draw count matches 1 per 2 frame callbacks.
- `05:39:22.983` — `FRAME_MODE request_stride=4`; 12 `FRAME_SAMPLE` windows. Mean draw count matches 1 per 4 frame callbacks.
- `05:39:35.966` — `USER_ACTION` diagnostic export requested; the user could retrieve and upload the file. The export file is in user-accessible Download; clipboard remains independently untested.
- No recorded `FRAME_LAB_PAUSE` after the last stride4 sample because the export snapshot was taken **while the lab was still active**. This is not proof of a lifecycle bug; need a later snapshot if we want to verify onPause on this run.

## P11 acceptance status updated
| Check | State |
|---|---|
| Owned app starts with clean new process after prior last logged rollback | **PASS** for process & real renderer; global keys at this time not read |
| Read-only own GLES/NDK/Vulkan | PASS, Adreno730 |
| Stride 1 (~120 UI callbacks/s and ~120 owned renderer callbacks/s) | PASS |
| Stride 2 (~120 UI callbacks/s, ~60 owned draws/s) | PASS |
| Stride 4 (~120 UI callbacks/s, ~30 owned draws/s) | PASS |
| Stable app crash-free for recorded session | No explicit Java crash logged; not a general reliability guarantee |
| Physical presented-FPS, GPU thermal/power, real Outlanders FPS | **Not measured** |
| Shizuku settings after 05:26:04 clean report | **Not present** in this probe-only upload |

## Next engineering step
- Mark **P11 basic pacing acceptance complete** for the owned test. No code patch or new APK is required to interpret these results.
- Future optional accuracy work (only if justified): add SurfaceFlinger FrameTimeline/Choreographer jank diagnostics where available, CPU/GPU workload indicators, app-local percentiles and CSV/JSON reporting, correctly distinguishing rendering callbacks from true presented frames. Do not imply hidden game modification.
- Keep GPU Debug layers disabled; do not reactivate solely for this benchmark. If an exact fresh four-key system check is needed for a future layer experiment, obtain a separate read-only Shizuku report with explicit user action; no need now.
- Preserve privacy: uploaded `log (2).txt` must remain in private chat, not committed verbatim to the public MO project.

## Related evidence
- `docs/DEVICE_FEEDBACK_P12_LOGS_2026-11-07.md` — earlier two uploaded logs, 154 events, actual guarded MO debug cleanup.
- `docs/OWNED_FRAME_PACING_LAB_AR.md` — P11 code/measurements and limits.
- `prompt/sessions/MO-20261107-CHAT-P12-DEVICE-LOGS-021.md`
