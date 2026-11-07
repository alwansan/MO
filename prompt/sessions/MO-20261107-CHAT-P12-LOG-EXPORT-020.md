# MO-20261107-CHAT-P12-LOG-EXPORT-020 — full available owned-app logs, clipboard and Downloads export
- Date: 2026-11-07 (Asia/Baghdad)
- origin: chat
- Repository: https://github.com/alwansan/MO
- branch: main
- starting HEAD: `a99d75eb0a2f101e639a919c1317bd840e6cb77c`
- User request: **before first P11 phone test**, add button to generate/copy event log and save `log.txt` directly into `/storage/emulated/0/Download`, complete app test details with timestamps. User will manually upload the file(s).
- Phase: P12 diagnostics; preserves no-root, no-bootloader, no game mods; P11 test device proof still pending.

## Live state read and design
- Read `AGENTS.md`, `prompt/RESTORE_AR.md`, `STATUS.md`, `MEMORY.md`, both application manifests/Gradle configs, `MainActivity.java`, `ProbeActivity.java`, `GameIdentityLabActivity.java`, `FramePacingLabActivity.java`, and current workflow.
- Original P11 source had **no persistent event log or Download export**. Implemented two app-scoped loggers rather than claiming to bypass Android sandbox. On Android 10+, official `MediaStore.Downloads` supports writing app-owned files to `Download/` with no broad storage permission. Reference: https://developer.android.com/training/data-storage/shared/media
- The user previously reported restoring GPU Debug and deleting an app but did not identify the removed package or provide an independent post-action four-key readback. No attempt to reenable GPU layers, Shizuku service privileges, or test other games occurred.

## Implemented, main + disposable app
- New `LogLineFormatter.java`, `DiagnosticLog.java`, `DiagnosticLogExporter.java`, `DiagnosticLogUi.java` in **both** `app/src/main/java/com/alwansan/mo/` and `probe/src/main/java/com/alwansan/mo/glesprobe/`, independently compiled into their respective APKs.
- New `MOApplication.java` in main and `MOTestApplication.java` in probe as process-scoped lifecycle/low-memory observers; default uncaught-exception handler logs a best-effort stack and chains to original Android crash handler.
- Asynchronous persistent app-private event writer, bounded to **3 rotated segments × 3 MiB**. Every event records local ISO timestamp with milliseconds and UTC offset, monotonic uptime `elapsed_ms`, PID, thread ID, event type and sanitized content. Truncation/retention explicitly described; complete means **all currently retained app-owned events**, not unlimited history/system-wide logcat.
- On export, first enqueue current UI snapshot (for main MO), then consistent ordered log snapshot. `MediaStore.Downloads` writes `Download/log.txt` (app-owned URI reused in subsequent exports) with pending bit during first creation. A filename collision may cause OS rename; UI reports verified actual file basename and does **not** overwrite an unrelated app-owned file.
- Both screens have **one-tap export + clipboard** and a separate clipboard-only button. Clipboard copies whole retained log if ≤180000 chars; for larger log it includes *only last 180000 chars with an explicit warning* while the downloaded file has entire retained history. Export failure and clipboard failure are reported separately. No automatic uploads, root, `MANAGE_EXTERNAL_STORAGE`, or game access.
- Probe v0.6.0 records EGL/GLES read results, identity real readings/simulation mode, P11 frame stride/samples, lifecycle and exceptions. Main MO v0.10.0-alpha.10 records its Shizuku system snapshot, read-only device/GPU/native Vulkan reports, owned GPU debug action outcome/readbacks, main UI panels at export, lifecycle/crashes.
- JUnit pure-Java `LogLineFormatterTest.java` added to both APK projects for timezone, elapsed/PID/thread, line sanitization and event length cap.
- Main source bump to v0.10.0-alpha.10 (versionCode10); probe to v0.6.0 (versionCode6). Earlier P11 pacing logic preserved. Do not imply original Shizuku application was installed on device; actual new app test still pending.

## Verified CI and artifact evidence
- Last complete P12 code commit: `d40d0b6ec70d14ede1731eac2eae4a4f08e7ff81`.
- GitHub Actions [run 42327821781](https://github.com/alwansan/MO/actions/runs/42327821781), run number 53: **completed SUCCESS**. Logs show `:app:testDebugUnitTest`, `:probe:testDebugUnitTest`, both `assembleDebug` tasks, APK ZIP integrity and direct Release upload all passed.
- Public prerelease: https://github.com/alwansan/MO/releases/tag/mo-v0.10.0-alpha.10-run53, tag target above.
  - `MO-Graphics-Probe-v0.10.0-alpha.10.apk` 1,151,614 bytes; SHA-256 `a8e227174036732eb31ab7a27e667981e8f6da16102c9f25fe1d737a85fe86ce`.
  - `MO-Layer-Test-v0.6.0.apk` 208,508 bytes; SHA-256 `ce3771295b2728fad25342ca4c5c86f21904673df55115d56a207615da02ae1f`.
- Exact tag was checked via Releases tag API; generic release listing may be stale and should NOT override exact-tag verification.

## What remains unverified
- On-device install and runtime of these new APKs; MediaStore file creation/download/file name on owner's POCO Android15; clipboard behavior and app crash trace recording in actual runtime. GitHub tests cannot establish these. P11 on-device frame pacing results also still pending.
- If user removed old APK, its private diagnostic data cannot be recovered after uninstall. This new logger starts recording when **new** version launches, not retroactively.
- Shizuku UID2000 has not been used for global logcat collection; each app has its **own private log**. One app cannot export the other's private history.

## Next user test
1. Install `MO-Layer-Test-v0.6.0.apk` from run53. No main MO or Shizuku is needed solely for P11; if Android rejects old test APK signature, only uninstall the disposable test after confirming no active MO GPU layer markers, never games/Shizuku.
2. Launch, use Game Identity Lab and P11 pace 1/2/4, then press **إخراج السجل: نسخه + حفظ Download/log.txt**.
3. Check the visible save result and File Manager → Download. Upload the saved file (or OS-renamed exact filename shown by the UI) to this chat. P11 rates are counted drawing callbacks, *not* SurfaceFlinger-presented FPS.
4. If testing main MO/ Shizuku separately, use the main `v0.10.0-alpha.10` APK and export its own log; upload separately with a distinct filename/label. Do not activate owned GPU developer layers just to collect logs.
5. Treat any export problem as a device bug to fix based on error/status screenshot; no global storage permission workaround without proof and owner decision.

## Docs updated
- `docs/DIAGNOSTIC_LOG_EXPORT_AR.md`
- `docs/IMPLEMENTATION_PHASES.md`
- `prompt/STATUS.md`, `prompt/MEMORY.md`, `prompt/DECISIONS.md`, `prompt/RESTORE_AR.md`, `AGENTS.md`, `README.md`, this session.
