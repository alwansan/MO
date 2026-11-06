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
