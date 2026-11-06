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
