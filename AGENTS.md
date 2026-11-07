# AGENTS.md — MO agents' operating contract

1. Read `prompt/RESTORE_AR.md`, `prompt/MEMORY.md`, `prompt/STATUS.md`, `prompt/PLAN_AR.md`, `docs/LEGAL_AND_SECURITY.md` BEFORE acting.
2. Respect owner's immutable limits: **no root, no bootloader unlocking, no kernel flashing, no Zygisk, no PC dependency**, Shizuku as sole optional privileged broker, POCO F5 Pro Android 15.
3. Respect the latest authorized phase: **P10 owned-app identity laboratory / device verification pending**, following the owner's explicit P1 implementation approval and subsequent P4–P9 work. Historical P0 planning restrictions are superseded for authorized original app source/CI work, but never for root, bootloader, proprietary-code or game-safety limits. Do not trigger new device settings changes without consent; do not mistake CI for on-device validation.
4. Source truth: live GitHub files + primary vendor docs + device test evidence. Separate observed, documented, suspected and unverified. Never invent hardware capabilities or successful actions.
5. Never label a ZIP 'installed' merely because it was extracted. Root-module install is unavailable without root infrastructure; report missing permissions candidly.
6. Do not run third-party ZIP shell scripts, native binaries, updater commands, or network-supplied executable code. Treat archives as untrusted.
7. Avoid reverse engineering, modifying, or incorporating iUnlockerGL proprietary components; see license. KernelSU GPLv3 is a distinct dependency and must not be copied unnecessarily.
8. Keep changes narrow; before writes inspect current repo HEAD and files, write to the requested branch, use non-destructive commits, verify the result.
9. Each work session: create a unique session_id; update `prompt/STATUS.md`, `prompt/MEMORY.md` and a log under `prompt/sessions/`; include evidence, decisions, blockers, next steps.
10. Language: user communication and restore in Arabic (Iraqi Arabic is fine); technical terms and code may remain English. Never solicit secrets or personally identifying info.
11. Do not promise automatic restart of Shizuku after every reboot. Wireless debugging on non-root devices ordinarily requires manual start again.
12. Acceptance always includes clean rollback for any optional change, explicit confirmation for writes, and tests against stock device without elevated privilege.

## Addendum MO-20261107-CHAT-STATIC-002
- User approves *private static evaluation* of uploaded binaries/source. Treat this as permission to analyze artifacts, **not a license to publish/distribute modified proprietary files**, especially because MO is public. Independent reimplementation and documented APIs only.
- External GLES/Vulkan layer proof is authorized for **eligible debuggable/opt-in** apps during an implementation phase; Android forbids unconsented injection in stock nondebuggable apps. Do not claim Shizuku bypasses this restriction.
- For P0 research experiments, pure static read-only tools and local test fixtures are allowed; Android builds/privileged device changes remain off until phase change.
- Never set `angle_gl_driver_all_angle=1` (documented boot failure risk).

## Addendum MO-20261107-CHAT-ECOSYSTEM-003
- Read `research/ROOTLESS_ECOSYSTEM_COMPARISON_AR.md` and `research/POC_TEST_MATRIX_AR.md` after restore. Don't conflate GAMA HWUI Skia setting with GPU spoofing.
- Protect original installed games and internal app data: NEVER use third-party APK patcher auto-uninstall/re-sign-and-replace flow on them. Only self-owned disposable test APK and explicit device-owner operation in later phase.
- Source review of GPLv3/AGPLv3/BSD/MIT projects is allowed; imports and redistribution demand compatibility compliance, attribution and testing. Public MO is publication.

## 2026-11-07 explicit phase transition
- Owner authorized starting actual original Android implementation. Replace old P0-only restriction: **P1 source + CI APK builds and read-only device diagnostics are now permitted**. All other safety, source, privacy and root restrictions remain.
- Do not run root, VMOS, App Cloner, APK re-signing of games, or Shizuku writes to game files. Owner explicitly rejected cloning/VMs.
- Verify GitHub Actions CI result, not just successful workflow dispatch. Before claiming APK exists, require successful uploaded artifact.
- New source lives in `app/`; CI in `.github/workflows/mo-android-debug.yml`; check `docs/IMPLEMENTATION_PHASES.md` and latest session.

## 2026-11-07 P4 scope
- Owner authorized continuous MO engineering with Shizuku only. P4 is a **consenting, reversible Android Developer GPU debug settings experiment** for MO-owned disposable test package com.alwansan.mo.glesprobe.
- Apply/restore actions are limited to exactly four allowlisted Settings.Global keys, UID2000, and only this owned package; never accept arbitrary commands, game package or enable global ANGLE forcing.
- Activation may leave persistent settings until restored. Warn to RESTORE inside MO before uninstall/upgrade, as backup is in app private data and CI debug signing changes every run.
- A successful build or settings write does not prove hook loading; require actual on-device GL_RENDERER change inside owned probe and subsequent rollback before marking P4 passed.

## MO-20261107-CHAT-P8-RECOVERY-013 — P7 real device and P8 safety fix
- User v0.7 report proved native and eglGetProcAddress renderer interception in owned debuggable MO Layer Test, PID 20827; Java path remains Adreno730. EGL_ANDROID_GLES_layers declared YES; synthetic FPS tier only, not real FPS gain.
- **Outlanders** com.tencent.mhadv version1.0.9.0.3308842, targetSdk36, Debuggable NO, opt-in `com.android.graphics.injectLayers.enable` NO: stock app not eligible for official external rootless GLES layer. Do not claim Shizuku can override this.
- **Urgent persistent settings:** Shizuku reports 4 MO debug keys still enabled. Earlier v0.7 could save already-active test state as original baseline, and user may have lost v0.5 backup across APK reinstall. Check normal Restore in currently installed v0.7 then re-read keys; a success string alone is insufficient.
- P8 source v0.8.0-alpha.8 adds `OwnedLayerRecoveryPolicy` and JUnit tests, refusal to activate when any debug config already exists, refusal to restore from a snapshot of active MO settings, and separately consented strictly guarded **MO-owned stale cleanup** (sets enable=0, deletes 3 exact owned markers only, captures pre-cleanup snapshot before writing, verifies, partial failure rollback).
- This cleanup is **not** reconstruction of unknown old baseline values. Keep original games untouched; need CI build result and user device confirmation.
- Evidence docs/DEVICE_FEEDBACK_ALPHA7_2026-11-07.md; recovery guide docs/GPU_DEBUG_RECOVERY_AR.md.
