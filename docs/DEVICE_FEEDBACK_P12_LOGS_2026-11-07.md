# MO P12 — analysis of two real POCO Android logs (2026-11-07)
Session: `MO-20261107-CHAT-P12-DEVICE-LOGS-021` | origin=chat
Evidence: **owner uploaded** `log (1).txt` (main MO) and `log.txt` (disposable owned test). The original private text uploads remain in the conversation and are **not committed to public repository**. This summary records only technical results.

## Source identity and dates
| Upload | App package | Version | PID | Time range (+0300) | Recorded events |
|---|---|---|---|---|---:|
| `log (1).txt` | `com.alwansan.mo` | `0.10.0-alpha.10` | `22510` | 05:23:23.215–05:26:04.463 | 35 |
| `log.txt` | `com.alwansan.mo.glesprobe` | `0.6.0` | `23395` | 05:24:09.444–05:25:37.990 | 119 |

Both are **app-private retained** logs newly created by P12; neither is system-wide logcat. File contents confirm that actual Android15/POCO filesystem export through MediaStore succeeded at least twice with separate filenames. The user uploaded them; clipboard copy and success-toast wording were **not independently verified**. No `UNCAUGHT_CRASH`, `ERROR`, or exception categories were recorded in the retained interval, but missing events cannot rule out native/system errors.

## Verified GPU debug state from MAIN log (new strongest device proof)
- 05:23:57.444 — initial Shizuku read-only snapshot: `enable_gpu_debug_layers=0`, `gpu_debug_app=null`. Shizuku UserService UID=2000, Game Mode Outlanders standard/custom, no interventions, ANGLE selection null.
- 05:24:29.958 — **owner activated** official debug layer for **our disposable test only** (action=1). Readback confirms `enable_gpu_debug_layers=1`; `gpu_debug_app=com.alwansan.mo.glesprobe`; `gpu_debug_layers_gles=libMOGlesProofLayer.so`; `gpu_debug_layer_app=com.alwansan.mo.glesprobe`.
- 05:24:33.790 — read-only diagnosis (action=3) reconfirms enabled settings.
- 05:25:51.316 — **owner executed guarded MO-owned stale cleanup (action=4)**, not a normal exact snapshot restore. Backend reports `OK: MO test settings cleaned, GPU debugging disabled (0)`. Its immediate `[READBACK AFTER ACTION]` explicitly shows `enable_gpu_debug_layers=0`, the three exact MO-owned markers **(not present)**.
- 05:25:55.392 and 05:26:02.096 — two further independent action=3 diagnoses reconfirm `0` and all three markers not present. The 05:26:04.457 main UI snapshot also agrees. **This closes the four-key settings cleanup evidence for the last observed moment (05:26:04)**. It does **not** reconstruct unknown original historical values before all earlier tests.
- No system-wide graphics configuration should be activated again merely to gather evidence. The stock `com.tencent.mhadv` still is targetSdk36, debuggable NO, opt-in NO, so official external GLES layer rootless eligibility remains NO.

## Owned MO Layer Test — what *actually* happened
- The single recorded process `PID=23395` **started at 05:24:09.444**, approximately **20.5 s BEFORE** debug layer activation at 05:24:29.958. Repeated Java/NDK/eglGetProcAddress GLES readings around 05:24:34–05:25:35 remain `Adreno (TM) 730`, with `Layer intercepted (Java)=NO`, Native NO. **Critical:** these same-process repeated reads are **NOT** a clean test of loader insertion after developer setting activation; no process restart/new PID is observed after 05:24:29. Do not conclude official layer loader categorically failed from these observations.
- The last owned GLES query was 05:25:35.373, **before** cleanup at 05:25:51.316. Therefore no post-cleanup **fresh-process renderer** evidence exists in the uploaded logs. Four-key system cleanup is nevertheless confirmed independently by the main app.
- Game Identity Lab read own physical model `23013PC75G`, SoC `SM8475`, Java GLES/NDK GLES/native Vulkan deviceName `Adreno (TM) 730`, panel `120.0 Hz`. Mode `ACTUAL` yields `mock_combined120=false`, `VULKAN_ONLY` also `false`; `ALL_SIMULATED` yields `true`. **All flags are fictional menu rules**, not a real game unlock.
- Three separate visits to Frame Pacing Lab: 05:24:48–05:25:00; 05:25:03–05:25:10; 05:25:22–05:25:25. Three separate `FRAME_MODE request_stride=1`, with corresponding end counters 1501/1501, 830/830 and 367/367 (Choreographer callback count / own GLES render calls).
- Across **21 one-second FRAME_SAMPLE windows** all `stride=1`:
  - mean callback/s = **120.474905** (rounded 120.47);
  - mean owned GLES draw callback/s = **120.474905** (same in every sample);
  - range **118.597–121.383** /s;
  - standard deviation `~0.683` /s;
  - no sample labelled `stride=2` or `stride=4`. Therefore **cadence scaling not yet device-tested**.
  - These are `onDrawFrame` callbacks, not SurfaceFlinger-presented frames, monitor FPS or Outlanders FPS.
- An Android `TRIM_MEMORY level=20` event was observed in each app after going to background. On Android this is the `TRIM_MEMORY_UI_HIDDEN` value and is **not** in itself low-memory/oom evidence. No crash was logged.
- All 154 parsed events across two files were timestamped and had per-process PID/thread data; export contains a human-readable header with version, scope and retention. No broad storage permission, root or bootloader procedure observed.

## Acceptance matrix
| Test | Result / nuance |
|---|---|
| P12 event logging on real Android15, two packages | **PASS**; 35 main + 119 disposable app events |
| P12 direct Download export to user-accessible `log.txt` | **PASS in effect**; both real files uploaded, main file was OS-renamed `log (1).txt` |
| P12 timestamp and app/session/PID fields | **PASS** |
| P12 clipboard copying | **Not independently verified by uploaded files** |
| P12 crash trace / large-file rotation on real device | **Not tested** |
| Shizuku read-only Game Mode/ANGLE and own GPU settings | **PASS** |
| Shizuku MO-specific debug-layer cleanup + four-key readback | **PASS at last recorded time** |
| Exact historical pretest GPU setting restoration | **Not proven**; action=4 is guarded cleanup |
| P11 `stride=1` frame-loop callbacks | **PASS** at approx 120 callbacks/s |
| P11 `stride=2` / `stride=4` | **NOT TESTED** |
| Fresh PID and physical renderer after last cleanup | **NOT TESTED** |
| Outlanders actual FPS unlock, real game GL spoof | **NOT SHOWN** |

## Next narrowly scoped step
1. **No new layer activation**; cleanup is verified. To check after cleanup without modifying settings, simply force-stop/reopen **the disposable MO Layer Test** and record new PID and original Java/NDK GL renderer.
2. To complete P11, open the existing Frame Pacing Lab and explicitly select **كل إشارتين** for ~5s and **كل أربع إشارات** for ~5s, then export `log.txt` again. Since log history persists through sessions within package data, run logs can be compared directly. No main MO, Shizuku or GLES developer setting required.
3. If the user wants more readable large logs later, add an optional one-tap automated cadence 1→2→4 benchmark and a `FRAME_SUMMARY` summary, but do not build/activate new source just to reinterpret this device test without owner request.
4. No need to upload or commit raw log contents to public GitHub; user uploads are sufficient and may include diagnostic environment details.

## Prior project context
- `docs/DEVICE_FEEDBACK_P10_2026-11-07.md`
- `docs/DIAGNOSTIC_LOG_EXPORT_AR.md`
- `docs/GPU_DEBUG_RECOVERY_AR.md`
- `prompt/sessions/MO-20261107-CHAT-P12-LOG-EXPORT-020.md`
