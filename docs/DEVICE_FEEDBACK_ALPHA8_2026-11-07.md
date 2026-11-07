# MO v0.8 on-device cleanup and stale display — real device outcome
Date 2026-11-07 Asia/Baghdad, session MO-20261107-CHAT-P9-REFRESH-014, origin=chat.

The owner tested MO Graphics Probe v0.8.0-alpha.8 on POCO F5 Pro / Android 15.
- Initial Shizuku read showed all four MO owned debug layer values active, UID2000/GRANTED.
- Owned layer still worked inside the already-running disposable native test APK: GL_RENDERER(NDK)/eglGetProcAddress = MO-GLES-LAYER-PROOF (SIMULATED), Java GL_RENDERER = Adreno 730, PID 4036.
- Attempting to activate again returned **ERROR: GPU debug settings were already configured/active before this test...**. This proves new protection blocked recording an already-enabled state as a clean baseline.
- Explicit cleanup returned **OK: MO test settings cleaned, GPU debugging disabled (0). This is safe cleanup, not recovery of unknown old values.** On v0.8 this message comes only after four settings are written and `OwnedLayerRecoveryPolicy.cleanupVerified(readCurrent())` returns true.
- However `[SHIZUKU SYSTEM READ-ONLY]` still showed enable_gpu_debug_layers=1 in the same copied report: this panel was previously populated by a separate command and was NOT refreshed after cleanup. Its snapshot is stale; it does NOT override the cleanup verification result.
- **External post-cleanup readback is still not available** from the owner's report. User should tap `تشخيص GLES (قراءة فقط)` and verify enable=0 and all 3 owned markers absent. Force-stop and restart MO Layer Test; Native renderer should revert to physical Adreno730 after fresh process. Existing PID may continue showing intercepted renderer until it ends.
- Stock Outlanders com.tencent.mhadv target SDK 36, Debuggable NO, external GLES opt-in NO; not eligible for official Android rootless layers. No game files modified and no FPS boost proven.

## v0.9 fix
- MO UI immediately re-reads Shizuku shell snapshot and four exact GPU Debug keys after each action. It replaces stale output in both panels and includes a `[READBACK AFTER ACTION]` section in the copied report.
- Refresh failure invalidates old panel instead of silently presenting an outdated success/failure.
- All cleanup commands remain constrained to the four MO test keys; no new game hooks or root commands.
- CI may sign with different debug certificate; install v0.9 **only after confirming cleanup/absence of test settings**, never erase an existing rollback snapshot.

## v0.9 Release closeout (2026-11-07)
- Source commit `968e13d19a956a0016c4e57609ca744c253da3db` build via GitHub Actions https://github.com/alwansan/MO/actions/runs/42316925749; Java tests, ARM64 native build, both APK builds and Release asset upload passed.
- Public experimental Release: https://github.com/alwansan/MO/releases/tag/mo-v0.9.0-alpha.9-run16.
- Uploaded direct assets: `MO-Graphics-Probe-v0.9.0-alpha.9.apk` (1,135,214 bytes) and unchanged `MO-Layer-Test-v0.3.0.apk` (155,196 bytes).
- Fix: after every Shizuku operation UI re-reads four exact GPU keys and refreshes the cached shell panel; report appends `[READBACK AFTER ACTION]`. This addresses contradictory stale `enable_gpu_debug_layers=1` after a cleanup verified by the backend.
- **On-device v0.9 not yet tested**. Owner v0.8 cleanup reported OK. First action is to verify with v0.8 read-only diagnosis and restart owned test app; do NOT reinstall/update unless no active GPU debug state and no rollback needed.
