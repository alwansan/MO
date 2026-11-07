# MO session MO-20261107-CHAT-P13-REAL-SPOOF-024 — restore original cross-app GPU identity-spoof goal
- Date: 2026-11-07 Asia/Baghdad; origin=chat; write branch main.
- Initial main SHA `ea3224c5bd55d2cbad612cb2c9076e2d99fd8198`. Previous session `MO-20261107-CHAT-P11-REPEAT-023`.
- User corrected project drift: main intended function is to **make other apps/games read higher GPU identity** (e.g., Adreno840 instead of real Adreno730), hoping their graphics/FPS whitelist unlocks. P10 mock identity and P11 FPS callback counts are side diagnostic tools, NOT core acceptance.

## Direct primary-source validation
- Official ANGLE AOSP `doc/DebuggingTips.md` states `adb shell setprop debug.angle.gl_renderer "foo"` and related GL vendor/version strings; ANGLE `Context::initRendererString` reads `debug.angle.gl_renderer` and uses nonempty override. Links pinned in `research/REAL_GPU_IDENTITY_SPOOF_ANDROID15_ANGLE_AR.md`.
- ANGLE System Settings and AOSP documentation describe per-package selection `angle` vs `native` on supported setups; on POCO last read-only settings values both `null`, indicating no recorded choice but not whether ANGLE driver is present or eligible for Outlanders. Don't confuse developer/debug ANGLE packages needing root/debuggable with built-in ANGLE.
- Official Android external GLES layers require target debuggable or manifest opt-in/root; stock Outlanders targetSdk36 and both flags NO. Native Vulkan validation/external layers cannot load into unowned nondebuggable app on normal production OS; Shizuku ADB UID2000 no cross-process code privileges.
- Critical nuance: ANGLE GLS renderer override might be a **real GL string seen by an independent app** *if* it runs through supported ANGLE and property override is readable on this exact firmware. This is **not yet tested on POCO** and cannot rewrite Vulkan physical deviceName/Java Build or actual GPU hardware. Fake renderer name alone may or may not influence a game's whitelist. Not a guaranteed unlocked 120FPS.

## New project direction
1. Restore main acceptance: an independently running, authorized target must observe alternate `glGetString(GL_RENDERER)` from the actual API, not MO mock UI or a shell command value. Required before any "applied" claim. Separate Vulkan/Build/CPU/game menu checks.
2. Perform read-only ANGLE availability audit via existing Shizuku service before any setting write; do not activate MO GLES layer, change GPU debug settings, or touch stock game merely to explore.
3. Only with separate explicit owner authorization, run reversible A/B on independently owned/opt-in GL app with exact before/after/rollback journal for driver selection and `debug.angle.gl_renderer`. Probe real returned API strings plus independent Java and Vulkan; never assume privilege.
4. If no built-in ANGLE or no property/selection permissions, declare unavailable under stock nonroot limits. No Zygisk/root/bootloader/VM/clone/re-sign Outlanders.
5. P10–P12 results remain valid as scoped supporting diagnostics; do not repeat them as the main goal.

## Work performed
- Read live MO AGENTS, PLAN, ARCHITECTURE, RESEARCH, STATUS, MEMORY, DECISIONS and official Android/AOSP/Google ANGLE/Shizuku/driver sources.
- Created `research/REAL_GPU_IDENTITY_SPOOF_ANDROID15_ANGLE_AR.md` with grounded architecture, proof standard and safe A/B plan.
- Updated live plan/status/memory/decisions/restore/README/AGENTS to prevent future clients from drifting to FPS widget features as a substitute for genuine cross-app identity change.
- No APK/CI build, app source modification, device/ADB/Shizuku write, game file modification, root, proprietary ZIP execution or phone access performed.
