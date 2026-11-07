# MO-20261107-CHAT-P12-DEVICE-LOGS-021 — owner-uploaded P12 logs, P11 frame evidence and guarded GPU rollback
- Date: 2026-11-07, Asia/Baghdad
- origin: chat
- Repository: https://github.com/alwansan/MO
- branch: main
- initial live HEAD at start of analysis: `61d8fb042de764461a3495e6f642e9292154055d`.
- Request context: owner provided two uploaded exported logs `log.txt` and `log (1).txt` without accompanying text, following an explicit plan to upload P12 app logs to aid deeper MO troubleshooting. Analyze direct uploaded file bytes, distinguish observed vs unverified, update durable documentation. **Do NOT publish raw uploaded logs in the public repo**.

## File scope and measured counts
- `log (1).txt` was **MO Graphics Probe main v0.10.0-alpha.10**, package `com.alwansan.mo`, PID22510, 35 events, 05:23:23.215–05:26:04.463 (+0300), 18048 UTF-8 bytes.
- `log.txt` was **MO Layer Test v0.6.0**, package `com.alwansan.mo.glesprobe`, PID23395, 119 events, 05:24:09.444–05:25:37.990 (+0300), 23184 bytes.
- Total **154 records**, all parseable local timestamps, 2 app processes, no explicit `UNCAUGHT_CRASH`/`ERROR` categories, no system-wide logcat access. Clipboard completion not independently proven. The files have readable MediaStore Download export headers and were manually uploaded by the owner.
- Full quantitative/timeline report `docs/DEVICE_FEEDBACK_P12_LOGS_2026-11-07.md`.

## Key chronological device observations
- 05:23:57 main Shizuku readout: `enable_gpu_debug_layers=0` and `gpu_debug_app=null`.
- 05:24:09 owned probe process PID23395 starts while disabled.
- 05:24:29.958 main app owner action=1 activates the MO-owned official GLES test layer; follow-up action=3 at 05:24:33 shows `enable=1`, target `com.alwansan.mo.glesprobe`, GLES layer lib `libMOGlesProofLayer.so`, and layer provider package same owned test.
- 05:24:34–05:25:35 owned probe still uses **SAME PID23395** despite activation, and reports real Adreno(TM)730 across Java/NDK/eglGetProcAddress, no interception observed in that running process. This CANNOT be counted as proof against layer loading in a **new process after activation**, because the process was not restarted. Do not assert a successful hook or stock game compatibility.
- 05:25:51.316 main app owner action=4 guarded **MO-owned stale cleanup** succeeded, verified enable=0 and three exact owned keys not present. Repeat action=3 at 05:25:55.392 and 05:26:02.096 plus 05:26:04 UI snapshot agree. This supersedes previous *uncertain* post-rollback status and confirms **last recorded four-key safe cleanup**. It is **not** exact recovery of unknown historical baseline.
- No owned-test query after that 05:25:51 cleanup, so a new PID after clean state is still technically unproven.
- Android `TRIM_MEMORY=20` is UI_HIDDEN, not GPU OOM or low-RAM evidence, and no crashes shown.

## P10 and P11 actual results
- Owned Identity Lab reads Build.MODEL23013PC75G, SoC SM8475, Adreno(TM)730 in Java GLES, NDK GLES and native Vulkan, panel ~120Hz. `ACTUAL` and `VULKAN_ONLY` mock_combined120=false; `ALL_SIMULATED` true (fictional only).
- 3 visits to Frame Pacing Lab; **all 21 one-second samples are stride=1**. Mean Choreographer callback/s **120.4749**, mean GL draw callbacks/s **120.4749**; range **118.597–121.383**, population stdev ~0.683. Each window's callback count equals owned-GLES draw count in log. The three sessions closed with counts (1501,1501), (830,830), (367,367). This is not evidence of SurfaceFlinger-presented FPS or Outlanders FPS.
- **Missing device tests:** stride2/stride4, true screen present fps, a post-cleanup new PID/renderer, clipboard action success, log file rotation/crash reporting.

## Work completed this chat
- Read both explicitly mounted uploaded text files directly; parsed records and samples using Python, cross-compared event ordering and settings.
- Verified current live repository main HEAD and latest CI status (P12 source run 42327821781 completed success, no new build needed for evidence-only report).
- Added `docs/DEVICE_FEEDBACK_P12_LOGS_2026-11-07.md` and this session; updated STATUS/MEMORY/DECISIONS/RESTORE and P12/P11 evidence notes.
- **No Android app source changes, no workflow invocation, no game/system/device writes, no root, no third-party code execution; private raw user logs remain only in this conversation.**

## Next device request
1. Leave GPU debug layer disabled; no need to reinstall MO main solely for this test.
2. Open disposable owned MO Layer Test (no Shizuku) **in a fresh process** after clean state; new PID and Adreno GL readout can close pending fresh-process proof.
3. Visit P11 lab, select `كل إشارتين` (stride2) for about 5 seconds and `كل أربع إشارات` (stride4) for about 5 seconds. Export again and upload log.txt. Compare rate ≈half/quarter of Choreographer rate without claiming true displayed FPS; actual values may vary.
4. For current upload, no additional build needed; existing APK P12 run53 is adequate.

## Evidence references
- Private uploaded file `log (1).txt`: main MO run from 05:23:23.
- Private uploaded file `log.txt`: disposable probe run from 05:24:09.
- `docs/DEVICE_FEEDBACK_P12_LOGS_2026-11-07.md`
- https://github.com/alwansan/MO/actions/runs/42327821781

## Closing verification and commits
- Verified current main branch HEAD immediately before this session file closeout: `ecd3610600d744b24674d3bd12595cd689429514`.
- GitHub Actions latest remains **`42327821781`, completed/success**, source `d40d0b6ec70d14ede1731eac2eae4a4f08e7ff81`. This evidence-only work changed Markdown/docs/AGENTS/README, **not** Java, Gradle, native files or workflow. No new Actions build triggered.
- The existing public release `mo-v0.10.0-alpha.10-run53` still contains `MO-Graphics-Probe-v0.10.0-alpha.10.apk` and `MO-Layer-Test-v0.6.0.apk`. **No new APK required** for user to test stride2/4 with already installed v0.6.
- Updated `AGENTS.md`, `README.md`, `prompt/STATUS.md`, `prompt/MEMORY.md`, `prompt/RESTORE_AR.md`, `prompt/DECISIONS.md`, `docs/DIAGNOSTIC_LOG_EXPORT_AR.md`, `docs/OWNED_FRAME_PACING_LAB_AR.md` with the new log-derived device truth. Private raw uploaded logs were not added to GitHub.
