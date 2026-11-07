# Current status

## CURRENT — MO-20261107-CHAT-P12-LOG-EXPORT-020: P12 log export APKs built; phone test pending
- User asked for one-tap full available event log + clipboard copy and direct `/storage/emulated/0/Download/log.txt` save before testing P11. Implemented in **both MO apps** with app-private asynchronous bounded event history (3×3MiB), timestamps in local time including milliseconds+timezone, uptime, PID/thread, lifecycle, owned GLES/Vulkan/Identity/P11 frame counters, main MO Shizuku output, errors/uncaught Java crashes (best effort). Neither can access third-party game/system-wide logs.
- User-visible export/copy buttons use MediaStore.Downloads without broad storage permission. Clipboard copies full ≤180000 chars, otherwise the newest 180000 (explicitly marked), while Download contains full currently retained history; OS filename collisions may rename log.txt and UI shows actual name. No automatic remote upload.
- **CI PASSED:** [42327821781](https://github.com/alwansan/MO/actions/runs/42327821781) at complete source `d40d0b6ec70d14ede1731eac2eae4a4f08e7ff81`, both module tests and APK builds/releases. Public https://github.com/alwansan/MO/releases/tag/mo-v0.10.0-alpha.10-run53: `MO-Graphics-Probe-v0.10.0-alpha.10.apk` 1,151,614 bytes SHA256 `a8e227174036732eb31ab7a27e667981e8f6da16102c9f25fe1d737a85fe86ce`; `MO-Layer-Test-v0.6.0.apk` 208,508 bytes SHA256 `ce3771295b2728fad25342ca4c5c86f21904673df55115d56a207615da02ae1f`.
- **No phone validation** of new export UI/MediaStore/clipboard or P11 drawing yet. Guide `docs/DIAGNOSTIC_LOG_EXPORT_AR.md`; session `prompt/sessions/MO-20261107-CHAT-P12-LOG-EXPORT-020.md`. GitHub builds are not device proof.
- Prior user self-reported restoring GPU Debug and deleting one app, but did not identify package or provide independent 4-key readback; do not claim exact final system state. New log buttons themselves never write developer GPU settings. No root/bootloader/game modification.


## CURRENT — MO-20261107-CHAT-P11-FRAME-PACING-019 (supersedes earlier P10 GPU Debug status)
- Owner states **they restored GPU Debug and deleted an app** after P10. They did not specify whether MO Graphics Probe or the disposable Layer Test was deleted, and provided no post-restore four-key readback. Treat it as **owner-reported rollback, not independently verified system settings**. Do not require installing the main MO solely for P11; do not reactivate GPU debug layers.
- P11 source: **measured frame callback lab** added only to the disposable `probe/` app, version `0.5.0`. `FramePacingLabActivity` measures Choreographer callbacks/s and GLES Renderer.onDrawFrame callbacks/s under stride 1,2,4, **not SurfaceFlinger-presented FPS or other-game FPS**. `GameIdentityLabActivity` gets an internal entry button and Android15 inset correction. App main stays source v0.9.0-alpha.9.
- Unit test code covers sample arithmetic, independent rates, clock/count resets. Evidence/guide `docs/OWNED_FRAME_PACING_LAB_AR.md`; session `prompt/sessions/MO-20261107-CHAT-P11-FRAME-PACING-019.md`.
- **CI verified success:** GitHub Actions https://github.com/alwansan/MO/actions/runs/42325865139 at `9abd11a3cbc3062836ceef6f2facd968286faa6f` passed app/probe JUnit tests, both APK builds and Release upload. Public raw APK https://github.com/alwansan/MO/releases/tag/mo-v0.9.0-alpha.9-run32 : `MO-Layer-Test-v0.5.0.apk`, 182484 bytes, SHA256 `5aca59d8dc61ec0a1e3763963de96974f02c62f60d15d7041233d774afae37d8`. **P11 phone test still pending.**
- No on-device P11 validation, game FPS unlock, root, APK clone, global Android settings writes, or proprietary code import. P10 true/fictional identity result on POCO remains as documented.


## CURRENT — MO-20261107-CHAT-P10-DEVICE-018 (supersedes older P10 and P9 clean-state notes)
- **P10 owned identity lab ON-DEVICE PASS (limited)** from three owner screenshots: PID `5783`, Build.MODEL `23013PC75G`, SoC `SM8475`, Java GLES, native GLES and native Vulkan `Adreno (TM) 730`, panel `120.0 Hz`. Mode0 actual-only **fake combined FPS LOCKED**; mode6 all-local-synthetic **fake combined FPS UNLOCKED**; real readings unchanged. This proves mock UI only, **not Outlanders FPS/graphics unlock**. Individual modes 1–5 lack phone screenshots.
- **CRITICAL: newer owner MO v0.9 report supersedes prior clean-state evidence:** `enable_gpu_debug_layers=1` with `gpu_debug_app=com.alwansan.mo.glesprobe`, `gpu_debug_layers_gles=libMOGlesProofLayer.so`, `gpu_debug_layer_app=com.alwansan.mo.glesprobe`, corroborated by both SHIZUKU SYSTEM and `[READBACK AFTER ACTION]`. Latest **reported** GPU Debug settings are active despite PID5783 showing no intercepted renderer. The cause/time of activation was not established; mock dropdown cannot activate system settings.
- Immediate safety priority: owner should preserve installed MO main/app data and use its valid normal saved-setting restore if present, then obtain fresh four-key readback; if restore cannot proceed, collect the exact error and use only a separately consented, narrowly guarded MO-marker cleanup as fallback. No new layer activation, reinstall, upgrade, or game modifications while active.
- A fresh **post-restoration** owned test PID and physical renderer is still pending; PID5783 with active keys is not post-rollback proof.
- Evidence: `docs/DEVICE_FEEDBACK_P10_2026-11-07.md` and `prompt/sessions/MO-20261107-CHAT-P10-DEVICE-018.md`. No new source APK or Actions build; last verified P10 CI 42324225397.


## CURRENT — MO-20261107-CHAT-P10-IDENTITY-LAB-017 (supersedes older status below)
- P10 **original, owned-only Game Identity Laboratory** source implemented inside `probe` v0.4.0; `app` main version remains v0.9.0-alpha.9.
- Success: [GitHub Actions 42324225397](https://github.com/alwansan/MO/actions/runs/42324225397) at source `4166210d4fb5e62b391b759f3b4508eb50d02e97`, passed Java unit tests, NDK native ARM64 builds, both APK builds and release upload.
- Public [run25 prerelease](https://github.com/alwansan/MO/releases/tag/mo-v0.9.0-alpha.9-run25): `MO-Layer-Test-v0.4.0.apk` 172148 bytes SHA-256 `6fdc64c09d76bd0b071d46f5e10f850347a79212256a846a745ac9e8263095dd`. **Do NOT upgrade/uninstall current MO main** to use the owned lab: debug signing changes per run.
- Lab: pure Java fake identity menu gates by Build.MODEL, CPU, Java GLES, native GLES, Vulkan and real screen Hz, uses own-process read-only EGL/NDK/Vulkan measurements. Mock mode changes only strings in activity, not OS or games. Docs `docs/OWNED_GAME_IDENTITY_LAB_AR.md`.
- Previous owner device report v0.9 verified last post-cleanup GPU Debug `enable=0` with three MO keys absent. No **new PID recorded after final cleanup** yet, and no P10 phone test. Before probe APK replacement, re-confirm four keys clean; do not trigger a new GPU layer experiment.
- Stock Outlanders `com.tencent.mhadv` is non-debuggable and opt-in NO; no rootless official layer injection or real FPS unlock. New session `prompt/sessions/MO-20261107-CHAT-P10-IDENTITY-LAB-017.md`, origin chat.


## CURRENT — MO-20261107-CHAT-ZYGISK-COPG-AUDIT-016 (supersedes older audit entries)
- Stage: **P9 device evidence verified partially + research extension**. No new Android build or live game modification performed this session.
- Owner's **last v0.9.0-alpha.9 pasted report** contains consistent `[SHIZUKU SYSTEM READ-ONLY]` and `[READBACK AFTER ACTION]`: `enable_gpu_debug_layers=0`; `gpu_debug_app`, `gpu_debug_layers_gles`, `gpu_debug_layer_app` **not present**. The v0.9 post-action UI refresh and MO-owned cleanup both worked at the reported moment. This cleanup is not guaranteed exact restoration of the unknown historical baseline.
- Earlier screenshot PID16719 shows real Adreno730 in all three owned test paths (Java/NDK/eglGetProcAddress), but was around 04:10, before a later 04:12 layer activation screenshot; therefore **post-final-cleanup fresh-process renderer is still unverified**, not a full A/B closure. No further cleanup is needed while readback remains 0/absent.
- Live upstream analysis added: `research/ZYGISK_COPG_NO_ROOT_FEASIBILITY_AR.md`; phone analysis: `docs/DEVICE_FEEDBACK_ALPHA9_2026-11-07.md`; new session: `prompt/sessions/MO-20261107-CHAT-ZYGISK-COPG-AUDIT-016.md`.
- Zygisk Next, ReZygisk, NeoZygisk and COPG all depend on rooted process/bootstrap infrastructure; no stock Shizuku injection into Outlanders. Zygisk Next newer code restrictions, ReZygisk README/license mismatch, COPG source completeness caveat documented.
- Next: optional read-only four-key recheck and **new PID after final cleanup** from disposable MO Layer Test only, then clean-room Game Identity Laboratory design (owned app) with no stock game patch. P9 release remains v0.9, latest previously verified run 42316925749.

## Historical P9 audit (2026-11-07)

## CURRENT — 2026-11-07 (supersedes historical P0 summary below)
- Phase: **P9 implementation / waiting for real-device clean GPU Debug readback**. Source and release v0.9 exist; this is **not** Planning-only.
- Audit session: `MO-20261107-CHAT-P9-AUDIT-015`; verified starting main HEAD `3c3c20a51ce976c64984ea3a076cf5bd9a5bec5b`.
- Latest verified APK CI: [run 42316925749](https://github.com/alwansan/MO/actions/runs/42316925749), completed success at source `968e13d19a956a0016c4e57609ca744c253da3db`.
- Latest verified direct raw APK prerelease: [mo-v0.9.0-alpha.9-run16](https://github.com/alwansan/MO/releases/tag/mo-v0.9.0-alpha.9-run16). Latest documentation-only HEAD is newer than the built source.
- Last **device** evidence is from installed v0.8: guarded cleanup returned OK, but shell text within the copied report was stale; **fresh independent four-key readback and test-app fresh-process renderer are still unverified**.
- NEXT SAFE ACTION: on installed v0.8 press `تشخيص GLES (قراءة فقط)`, share all four key values; force-stop/reopen *owned MO Layer Test only* and send new PID + Java/NDK/eglGetProcAddress renderer. **Do not re-enable experiment, uninstall, or upgrade MO before cleanup is verified**.
- No new source changes, new APK build, privileged commands, or device tests in audit session. `docs/DEVICE_FEEDBACK_ALPHA8_2026-11-07.md` documents last actual phone evidence.

## Historical P0 starting snapshot (retained for provenance)

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

## Release publishing confirmed — 2026-11-07
- GitHub Actions run https://github.com/alwansan/MO/actions/runs/42311901394: **completed success**. Commit `58de082ab63a797087f827633ba4dbd35fb5aa2f`.
- GitHub prerelease https://github.com/alwansan/MO/releases/tag/mo-v0.4.0-alpha.4-run9 created, id 437174044, public (draft=false), two raw APK assets, state=uploaded:
  1. `MO-Graphics-Probe-v0.4.0-alpha.4.apk` (1,118,830 bytes) https://github.com/alwansan/MO/releases/download/mo-v0.4.0-alpha.4-run9/MO-Graphics-Probe-v0.4.0-alpha.4.apk
  2. `MO-Layer-Test-v0.1.0.apk` (17,463 bytes) https://github.com/alwansan/MO/releases/download/mo-v0.4.0-alpha.4-run9/MO-Layer-Test-v0.1.0.apk
- No ZIP Release asset. GitHub automatically provides Source code ZIP/tar links; separate from APK release assets.
- On future source/workflow pushes, building both APKs and publishing a unique experimental release is automatic after CI success. Actions Artifacts ZIP step removed.
- Debug signature remains unstable: **restore P4 debug GPU settings before uninstalling/updating MO**. No actual Outlanders FPS unlock or P4 device hook proven.

## Latest MO-20261107-CHAT-P4-FEEDBACK-011 — owned GLES real device FAIL, diagnostics implemented
- Owner supplied v0.4 owned `MO Layer Test` baseline+after screenshots. With Shizuku `enable_gpu_debug_layers=1`, `gpu_debug_app=com.alwansan.mo.glesprobe`, app still `GL_RENDERER=Adreno (TM) 730`, mock FPS LOCKED, `Layer intercepted: NO`. Settings write PASSED, actual layer injection NOT CONFIRMED/failed in observed trial. See docs/DEVICE_FEEDBACK_ALPHA4_2026-11-07.md.
- **Owner has not shown restoration**. First priority: use currently installed v0.4 to restore original GPU Debug keys BEFORE uninstall/update. Avoid losing SharedPreferences snapshot due changing signing key.
- New source v0.5 app+probe: diagnostic read-only Shizuku method, exact keys/logcat filtered, native vs Java GLES, PID, explicit ELF loadability test. GitHub Actions run must be checked and user must test after safe restore.
- No root/unlock/VM/clone, no games modified. Distinguish settings activated from GLES layer actually intercepting.

## v0.5 CI build verification (2026-11-07)
- Source commit `79ae93392461f03b0fe5a5d33b2c59be328a3345` from ongoing P5 diagnostic effort.
- GitHub Actions run [42312793163](https://github.com/alwansan/MO/actions/runs/42312793163): **completed SUCCESS**, all tests and builds and Release upload.
- Direct GitHub prerelease: https://github.com/alwansan/MO/releases/tag/mo-v0.5.0-alpha.5-run11
- Raw assets: `MO-Graphics-Probe-v0.5.0-alpha.5.apk` (1,118,826 bytes) and `MO-Layer-Test-v0.2.0.apk` (23,952 bytes).
- P5 adds native-vs-Java renderer comparisons, PID, explicit self-test of layer library loadability, and Shizuku read-only inspection of exact GPU debug keys and filtered layer-related logs.
- **Not yet device tested.** Most recent P4 owner evidence still layer intercepted NO; do not claim FPS unlock or hooking succeeded.
- **Critical**: the user still has not confirmed restoring GPU Debug settings in installed MO v0.4. The prior backup is stored in v0.4 private app data. User must press Restore and confirm original values BEFORE uninstalling/updating MO or installing v0.5. If restore fails, retain v0.4 and ask for exact error instead of overwriting settings.

## MO-20261107-CHAT-P5-PARTIAL-NATIVE-012: v0.5 partial GLES proof on-device
- Owner screenshots show MO Layer Test v0.2 Android15: native NDK GL_RENDERER **MO-GLES-LAYER-PROOF (SIMULATED)**, native YES, **Java GLES20 GL_RENDERER remains Adreno(TM)730, Java NO**, synthetic mock 120 FPS UNLOCKED. Shizuku four GPU debug layer settings correct and currently enabled. Logcat layer messages not found; manual ELF library loadability cannot independently prove automatic loader init.
- This is a PARTIAL PASS limited to disposable debuggable com.alwansan.mo.glesprobe. **NOT** actual FPS improvement, not Outlanders compatibility or global spoof.
- No confirmed restoration yet. **Priority restore from current MO v0.5 BEFORE uninstall/update** and read back enable_gpu_debug_layers=baseline, gpu_debug_app=baseline, plus two layer keys. Backup is in MO app-private data and CI debug signatures are not stable.
- Grounding: docs/DEVICE_FEEDBACK_ALPHA5_2026-11-07.md; upcoming test should separate Java/NDK/eglGetProcAddress. Android official debug layers only debuggable/opt-in targets.

## P6 owned test path isolation (source, device test pending)
- Probe v0.3 separates Java GLES20, NDK direct glGetString, and eglGetProcAddress lookup under SAME EGL context, records EGL_ANDROID_GLES_layers extension support and process PID.
- Replaced manually loading the layer (System.loadLibrary) with ordinary re-query so results won't be muddied by developer-forced library load. Existing LayerLibrarySelfCheck class is unused and must not be treated as injection proof.
- Mock tier explicitly marked **Mock Native FPS tier (simulation)** based on NDK only, NOT real game FPS; no ambiguous aggregate "Layer intercepted YES" when Java is still unaffected.
- Main MO v0.6 updated for versioned Release. **Do NOT install before restoring GPU Debug settings via CURRENT MO v0.5** — private rollback snapshot will be lost if debug signing requires uninstall. Device P6 runtime proof pending.

## P7 manifest eligibility (2026-11-07, read only)
- Source adds Android PackageManager GET_META_DATA scan for game's installed ApplicationInfo: targetSdkVersion, FLAG_DEBUGGABLE, and manifest meta-data `com.android.graphics.injectLayers.enable`.
- Shows **manifest prerequisites only**, not guarantee hook works. Debuggable or opted-in+targetSdk30+ is candidate; otherwise official external GLES debugging layers cannot be assumed available on stock nonroot Android. Actual com.tencent.mhadv flag values are UNKNOWN until owner's device report.
- Scans package-visible installed games from explicit queries; no game file modifications or privacy-sensitive access.
- Released after CI verification, and **user must restore GPU debug settings from v0.5 before any install**.

## P7 CI and raw APK release closeout — 2026-11-07
- Built source commit `0111eb311d32305de0ed83db016a4fed1252e115` with GitHub Actions run `42314365656`. Java unit tests, both APK builds, native NDK build, APK verification and GitHub Release upload have all **passed**.
- Published prerelease: https://github.com/alwansan/MO/releases/tag/mo-v0.7.0-alpha.7-run14 .
- Confirmed uploaded raw assets:
  - `MO-Graphics-Probe-v0.7.0-alpha.7.apk` (1,118,830 bytes).
  - `MO-Layer-Test-v0.3.0.apk` (155,196 bytes).
- New capabilities: user-visible installed-game debug/opt-in/target-SDK flags (read only); owned test separates Java/native/eglGetProcAddress and EGL_ANDROID_GLES_layers extension; removes explicit System.loadLibrary layer manual button to avoid conflating loader evidence.
- **No on-device P7 result yet**. The last observed owner's phone state was GPU Debug layers enabled. First action remains restoration from **existing MO v0.5** before uninstall/upgrade. Do not claim stock Outlanders FPS unlock.

## MO-20261107-CHAT-P8-RECOVERY-013 — P7 real device and P8 safety fix
- User v0.7 report proved native and eglGetProcAddress renderer interception in owned debuggable MO Layer Test, PID 20827; Java path remains Adreno730. EGL_ANDROID_GLES_layers declared YES; synthetic FPS tier only, not real FPS gain.
- **Outlanders** com.tencent.mhadv version1.0.9.0.3308842, targetSdk36, Debuggable NO, opt-in `com.android.graphics.injectLayers.enable` NO: stock app not eligible for official external rootless GLES layer. Do not claim Shizuku can override this.
- **Urgent persistent settings:** Shizuku reports 4 MO debug keys still enabled. Earlier v0.7 could save already-active test state as original baseline, and user may have lost v0.5 backup across APK reinstall. Check normal Restore in currently installed v0.7 then re-read keys; a success string alone is insufficient.
- P8 source v0.8.0-alpha.8 adds `OwnedLayerRecoveryPolicy` and JUnit tests, refusal to activate when any debug config already exists, refusal to restore from a snapshot of active MO settings, and separately consented strictly guarded **MO-owned stale cleanup** (sets enable=0, deletes 3 exact owned markers only, captures pre-cleanup snapshot before writing, verifies, partial failure rollback).
- This cleanup is **not** reconstruction of unknown old baseline values. Keep original games untouched; need CI build result and user device confirmation.
- Evidence docs/DEVICE_FEEDBACK_ALPHA7_2026-11-07.md; recovery guide docs/GPU_DEBUG_RECOVERY_AR.md.

## P8 CI and Release verified (2026-11-07)
- GitHub Actions https://github.com/alwansan/MO/actions/runs/42315468593 finished **SUCCESS**, commit `2e2ddb7cc4ba29731bddf1f612955339ac9931da`; app and probe JUnit tests, NDK builds and release upload passed.
- Raw APK release: https://github.com/alwansan/MO/releases/tag/mo-v0.8.0-alpha.8-run15.
  - `MO-Graphics-Probe-v0.8.0-alpha.8.apk` (1,135,214 bytes), raw .apk verified uploaded.
  - `MO-Layer-Test-v0.3.0.apk` (155,196 bytes), unchanged probe.
- On-device P8 emergency cleanup **NOT tested**; user still must test existing v0.7 Restore/readback first. Do not force an upgrade or claim full prior-state restoration. If v0.7 restore fails and exact four owned markers persist, v0.8 offers explicit guarded cleanup, only with owner consent. Shizuku UID2000 is mandatory.
- New run results are not evidence original Outlanders FPS/graphics options unlocked; original game's manifest flags still exclude official GLES external layers.

## MO-20261107-CHAT-P9-REFRESH-014 (2026-11-07): v0.8 cleanup success, stale cached report fix
- User v0.8 screenshot shows guarded cleanup returned OK (backend checks 0 and missing owned markers); retry activation protection also correctly refused already configured MO developer GPU layer settings.
- Copied report still contained `enable_gpu_debug_layers=1` in independently cached shell readout; source code confirmed it never refreshed `shellOutput` after cleanup. Do not construe this as proof cleanup failed; require fresh readback from user.
- v0.9 source refreshes shell panel and exact Shizuku-owned four key diagnostic after each operation, includes readback in report and invalidates stale data on refresh failure.
- User should re-diagnose GPU settings and force-stop/reopen the owned test app, verify Native GL_RENDERER returns Adreno730 before updating. Do not enable a new experiment.
- Outlanders game still Debuggable NO, external layer opt-in NO; no rootless official GLES hook possible via Shizuku alone.
- Evidence: docs/DEVICE_FEEDBACK_ALPHA8_2026-11-07.md.

## v0.9 Release closeout (2026-11-07)
- Source commit `968e13d19a956a0016c4e57609ca744c253da3db` build via GitHub Actions https://github.com/alwansan/MO/actions/runs/42316925749; Java tests, ARM64 native build, both APK builds and Release asset upload passed.
- Public experimental Release: https://github.com/alwansan/MO/releases/tag/mo-v0.9.0-alpha.9-run16.
- Uploaded direct assets: `MO-Graphics-Probe-v0.9.0-alpha.9.apk` (1,135,214 bytes) and unchanged `MO-Layer-Test-v0.3.0.apk` (155,196 bytes).
- Fix: after every Shizuku operation UI re-reads four exact GPU keys and refreshes the cached shell panel; report appends `[READBACK AFTER ACTION]`. This addresses contradictory stale `enable_gpu_debug_layers=1` after a cleanup verified by the backend.
- **On-device v0.9 not yet tested**. Owner v0.8 cleanup reported OK. First action is to verify with v0.8 read-only diagnosis and restart owned test app; do NOT reinstall/update unless no active GPU debug state and no rollback needed.
