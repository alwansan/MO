# Current status

- Date (Asia/Baghdad): 2026-11-07
- Phase: **P0 PLANNING / RESEARCH**
- Source tree: GitHub alwansan/MO main
- Session ID: `MO-20261107-PLANNING-001`
- Deliverables: project plan, architecture, research, privilege matrix, licensing, restore prompt, source evidence, tests, ADR, session log.
- **No Android application source or APK built. No bootloader/root/ROM changes.**
- Confirmed blocking constraint: iUnlockerGL Zygisk behavior cannot be replicated by merely installing its ZIP with non-root Shizuku.
- Confirmed opportunity: standalone Android app with ZIP inspection/extraction; Shizuku optional ADB diagnostics; conditional ANGLE testing if device supports it.
- Blockers for full original functionality: Android privilege boundary and iUnlockerGL derivative-use license.
- Device tests: not run (no direct device access).
- Next phase proposed: P1 Kotlin skeleton + permission UI + non-destructive diagnostics **after user explicitly moves out of planning**.
- Open questions for actual device validation: Shizuku active UID, ANGLE availability/selection on HyperOS, ability to install extracted app UI normally, effects of OEM security restrictions; none required to complete P0.
- Resume: `prompt/RESTORE_AR.md`.

## Update from MO-20261107-CHAT-STATIC-002 (verified)
- Latest verified session: `MO-20261107-CHAT-STATIC-002`, read-only local archive tests, **no build/on-device experiment**.
- New screenshots: original iUnlocker app runs but reports `No root permission`, `revision null`, `NOT GRANTED`.
- Latest archive comparison: same critical APK/Zygisk/install scripts; 98 actual files in each of 3 archives; four missing paths declared by installer. See `research/reports/2026-11-07-static-audit.md`.
- **New viable conditional research path:** Android **external GLES/Vulkan layer** for **debuggable or explicit opt-in** applications; stock nondebuggable closed games do not permit general external injection. Details in `docs/ROOTLESS_GRAPHICS_LAYERS_AR.md`.
- 2/2 local Python synthetic unit tests passed; archive checks executed (3/3). There was **no real-device integration test**.
- Next engineering proof: original self-built GLES probe and own passive layer, invoked via Shizuku-permitted settings with exact rollback. The owner requests no root/unlock.
- CAUTION: `angle_gl_driver_all_angle=1` excluded because AOSP warns it can make device unbootable.

## Latest update MO-20261107-CHAT-ECOSYSTEM-003 — expanded ecosystem research
- Phase remains **P0 RESEARCH / NO DEVICE CHANGES / NO APK BUILD**.
- Reviewed published source and docs: GAMA, KaosGFX, CatSmoker, LSPatch, libadrenotools, root Zygisk equivalents, containers, Android ANGLE/Game Mode.
- New promising path: **APK-embedded bootstrap on a disposable app we own**, inspired by KaosGFX + libadrenotools; works conceptually without root because app loads its own library. Shizuku assists with install/staging but cannot inject unmodified games.
- CRITICAL: upstream KaosGFX source automatically uninstalls on certificate mismatch; backups do not include app internal data. Never expose this risk to owner's original games.
- GAMA mechanism actually tweaks HWUI, not GPU capabilities; CatSmoker rootless only game configs/settings.
- Tool uploads inspected as archive metadata only; no execution, see research/UPLOADED_TOOLING_INVENTORY.md.
- Deliverables: research/ROOTLESS_ECOSYSTEM_COMPARISON_AR.md, research/POC_TEST_MATRIX_AR.md, research/UPLOADED_TOOLING_INVENTORY.md, and this session log.
- Next (only after authorized implementation phase): instrument independent debug GLES and Vulkan probe; Shizuku read-only UID; official layers; self-owned APK bootstrap with unique signature; rollback verification.

## MO-20261107-CHAT-GAME-PROFILES-004 — focused gaming identity/FPS investigation
- Goal clarified: per-game device/CPU/GPU spoof to unlock disabled FPS/graphics tiers on stock unrooted phone.
- New read-only upstream source comparisons: Xposed Game-Unlocker, mixed-architecture GameUnlocker, ULTRA model props, two Shizuku PUBG GFX config editors. Documented real code vs advertised capabilities.
- Latest research docs `research/GAME_PROFILE_SPOOFING_AR.md` and `research/POCO_GAME_COMPATIBILITY_AR.md`. Device specs corrected (8+ Gen1).
- Verified public announcement PUBG 4.4 first-batch 120 FPS Snapdragon 8 Gen3+, with exceptions/device expansions; not proof of actual in-game current 4.6/4.7 status on POCO.
- New risk: official PUBG policy warns of permanent bans for unauthorized game modifications. No patching user games or account tests.
- Status remains P0 planning/research. No Android APK built or Shizuku device experiment run in this chat.

## Latest MO-20261107-CHAT-BUY-VS-BUILD-005
P0 research only. Game clarified Outlanders com.tencent.mhadv, not Monster Hunter Now. Ready app comparison finished and source-level ShizukuPlus audit. App Cloner 4.0.12 promising for cloned apps but signature changes; VMOS+ 1.1.4 ARM64 Android15 system20013 supports virtual Magisk/ZygiskNext/LSPosed without rooting physical host, but guest root outside Shizuku-only and games unproven. ShizukuPlus's spoof only intercepts shell getprop commands and is NOT a universal game spoofer. No verified working solution for original Outlanders on stock host. Do not build until ready alternatives evaluated safely; require separate permission for any guest-root setup. No phone action or APK build. Read new research documents.

## P1 development underway — MO-20261107-CHAT-IMPLEMENT-006
- Owner explicitly authorized implementation ("ابدأ بالصنع التطبيق ... Continue") on 2026-11-07. No cloning, VMOS, root or unlock.
- Source commit `ca3378fae3b2d5ae9f488486d7af0332856f869a` adds native Android read-only app + Shizuku 13.1.5, EGL probe, package inventory, JUnit, GitHub Action.
- First CI attempt: https://github.com/alwansan/MO/actions/runs/42305774716 FAILED **before compilation** in android-actions/setup-android@v3 because it tried unavailable SDK package 'tools'. This follow-up corrects workflow to use preinstalled runner SDK. Check new run status; don't claim APK built until actual success.
- Next: repair any compiler/dependency issues from CI; publish only debug artifact, ask user for on-device feedback.

## P1 build verification — same session MO-20261107-CHAT-IMPLEMENT-006
- **CI success confirmed**: https://github.com/alwansan/MO/actions/runs/42305983786 (push SHA 817960c40d97ad5216224920ff1221ccc602c9f8).
- JUnit `testDebugUnitTest` success; `assembleDebug` success; APK hash step success; artifact uploaded.
- Artifact name `MO-Graphics-Probe-debug-2`, artifact ID 13545813166. Installable APK `app-debug.apk`, 112,063 bytes, SHA256 `13c72cbb626e4ec6238df7e5c9262c5781b1f819487753c568c90befe2a2fba2`.
- Connector artifact downloaded and exact bytes rechecked locally (APK ZIP CRC good, 3 DEX files). This is **not** a real-device UI test.
- **Next waiting on POCO device evidence**: install debug APK, authorize Shizuku through its normal UI, confirm binder UID 2000/GLES renderer/game version, copy report.
- No root, no game patches. Current P1 is diagnostics only. Further P2 code should build against this successful baseline.

## Latest MO-20261107-CHAT-P2-DEVICE-007
User on-device proof confirms MO P1 success: Shizuku UID2000 permission GRANTED API13, Qualcomm Adreno730 GLES3.2 V@0615.95, Outlanders global com.tencent.mhadv v1.0.9.0.3308842, 120Hz. P2 source implements read-only Shizuku UserService + Android Vulkan declared features, version0.2.0-alpha.2. Build validation required from GitHub Actions. No on-device P2 test, no game spoof/unlock.

## P2 CI verified 2026-11-07
- GitHub Actions run https://github.com/alwansan/MO/actions/runs/42307946638 **COMPLETED SUCCESS** for commit 598fc91eb3324762765ef9e7a38fc2c7410e23ae.
- JUnit tests including read-only allowlist passed; Android build and artifact upload passed.
- Artifact `MO-Graphics-Probe-debug-3` ID `13547166555`, APK 125919 bytes, SHA256 `7a98dbbb0393256210b76249c79505df34b89e34e98c3677545cd0d6b5e23d5b`.
- APK ZIP and artifact CRC checked after download; direct v0.2.0-alpha2 APK provided in current chat.
- **Still untested on physical device:** Shizuku UserService connection, HyperOS cmd game list-modes/list-configs, ANGLE reads, Vulkan feature reporting. Need owner report before planning write operations.

## Signing caveat alpha1 → alpha2
- Verified v2 APK signature certificate SHA256 `4d2e87fa998d2c8c901c6e8f21417fbe28ce6b2f8109e35cb8dd7ef46f1be365` differs from v1 `6ea43d29063eae454fa7769c20392cd58effe4653585d79df5c5384b3ce7a2da` because GitHub Actions uses ephemeral per-run debug keystore. Android in-place update therefore fails with signature mismatch.
- Owner told to **uninstall MO Graphics Probe only**, then install alpha2; neither Shizuku nor games should be removed. P1 app had no persistent user/game data.
- Future stable update signing must use private signing key stored in GitHub Actions Secrets (never commit private signing key to public MO). Add workflow support after secure credential setup; no key can be recovered from unsigned release bundles or APK.

## Latest MO-20261107-CHAT-P3-VULKAN-008 — P2 device report verified; P3 native Vulkan source
- User-supplied MO v0.2 report proves Shizuku UID2000 GRANTED, Game Mode standard/custom no intervention; ANGLE pkgs/values null; GPU debug layer 0; ro.board.platform taro and ro.hardware.vulkan adreno. Android declares Vulkan 1.1.
- Stored evidence docs/DEVICE_FEEDBACK_ALPHA2_2026-11-07.md. **No game identity modification or FPS unlock has occurred**.
- New source v0.3.0-alpha.3: JNI Vulkan native GPU properties in own process, ARM64 NDK/CMake, fixed report literal \\n. Check latest GitHub Actions and device before claiming full success.
- Still debug APK ephemeral signing; uninstall only MO to install update if signature mismatch, never game or Shizuku.

## P3 build success — 2026-11-07
- GitHub Actions https://github.com/alwansan/MO/actions/runs/42308837332 SUCCESS on source commit `eef8fb46f8ab49412ab4ab04554128e56b0dad36`.
- `testDebugUnitTest`, `assembleDebug`, SHA-256, artifact upload successful. Native ARM64 `libmo_vulkan.so` present in APK.
- Artifact `MO-Graphics-Probe-debug-4` ID `13547731335`; APK SHA-256 `203b8c47d3da67cca1e6ffe212682824d67a864a556b6cd12d7717b7f14dbe92`, APK 1,102,410 bytes. Downloaded, ZIP CRC and native library checked.
- Direct user APK provided in chat as `MO-Graphics-Probe-v0.3.0-alpha3.apk`. **Device Vulkan runtime test still pending**; v0.3 compile success is not native runtime success.
- CI per-run debug signing may require reinstall of MO only. Do not uninstall Shizuku or game. Next: user's copied Vulkan Native GPU section, plus entire report.

## Latest MO-20261107-CHAT-P4-LAYER-009: v0.3 native Vulkan confirmed; P4 owned-app layer proof in source
User report proves native Vulkan API1.1.128 GPU Adreno730 vendor0x5143 device0x7030002 driver0x8026705F in MO process, P3 real device PASS. See docs/DEVICE_FEEDBACK_ALPHA3_2026-11-07.md.
P4 owns disposable debug app `com.alwansan.mo.glesprobe`, original GLES interception library for glGetString(GL_RENDERER) and clearly marked synthetic frame-tier profile. No system settings or games changed yet; next controlled Shizuku per-app settings and rollback, source in probe/. Need CI compile + on-device test before declaring injection success. No root/boot unlock/VM/clone.

## P4 v0.4 UI and controller completed in source
- Typed Shizuku Service now has captureLayerSettings/activateOwnedLayer/restoreOwnedLayer, fixed only to disposable com.alwansan.mo.glesprobe. Four exact Settings.Global keys with before/after presence/value and rollback backup in MO app data.
- UI explicit opt-in, restore and launch test app. No changes on user's phone yet, test app does not touch Outlanders.
- Need verify latest GitHub Actions run; then test both APKs on POCO and restore settings before updating/removing MO.

## P4 build complete — 2026-11-07
- Final source commit 00558159502af56d8451ee03dac57e04d9bd34d3; GitHub Actions https://github.com/alwansan/MO/actions/runs/42310714620 SUCCESS (tests and both APK builds).
- Artifact MO-Graphics-Probe-debug-8 ID 13547164678; contains app/build/outputs/apk/debug/app-debug.apk and probe/build/outputs/apk/debug/probe-debug.apk.
- Main APK v0.4.0-alpha.4 1,118,830 bytes SHA256 74e50eece4eae020d7a9808f97552cbc5d843bf37142b8818282ab106c71b8f7.
- Separate MO Layer Test v0.1.0 17,463 bytes SHA256 180c32ab62a06f8e26686d651ad274cf316eb2828a1e52a73a29bca98670b4d0, contains lib/arm64-v8a/libMOGlesProofLayer.so.
- Both APKs extracted and ZIP CRC checked. **No real-device P4 activation/injection test yet.**
- P4.0 first Actions run 42309871683 failed due missing probe JUnit dependency. Corrected at cdb8f29f99985b4e0893db2466990856107cbc5f; later run 42309995020 succeeded probe build, 42310714620 succeeded full v0.4 controls.
- Owner instructions: uninstall old MO v0.3 if signature mismatch (no active layer in old version), install v0.4 + disposable test APK; open test before enabling for baseline. Under Shizuku UID2000, opt in, observe layer, RESTORE SETTINGS IN MO BEFORE UNINSTALL/UPGRADE, and verify original renderer returned. Do not modify Outlanders.

## Latest MO-20261107-CHAT-RELEASE-010 — User requested direct APK GitHub Releases
- Changed MO build workflow: no more upload-artifact ZIP. On successful two-module tests/build, package two **raw APK Release assets**, prerelease tagged by version and run number; include SHA256 in release notes. GitHub CLI `gh release create` with workflow contents:write and token, or upload --clobber on rerun.
- Current repo Releases was empty before this change. First build triggered by workflow update **must be checked** before claiming published URLs.
- Important owner safety: P4 GLES developer layer settings snapshot is in MO app data. **Restore original settings before uninstall/update**, especially with CI per-run debug signing mismatch. No real game modified.
- Docs: `docs/RELEASE_DISTRIBUTION_AR.md`.
