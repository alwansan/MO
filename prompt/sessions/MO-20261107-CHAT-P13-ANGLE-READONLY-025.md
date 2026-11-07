# Session MO-20261107-CHAT-P13-ANGLE-READONLY-025 — begin real cross-app GPU identity strategy with read-only ANGLE audit
Date: 2026-11-07 Asia/Baghdad
origin=chat
Repo https://github.com/alwansan/MO, write branch `main`
Initial branch HEAD: `f7f919c87ef141769a033519bdd93854a8124b51`
Previous goal-reset session `MO-20261107-CHAT-P13-REAL-SPOOF-024`.

## Owner objective and current scope
User sent "Continue" after explicitly correcting original MO goal: make **independent apps/games** observe a higher reported GPU string, e.g. Adreno840 vs real Adreno730, as a possible route to unlocked graphics/FPS whitelist. Do not mistake P10 fake in-app menu or P11 own callback counters as real target-app spoof. This cycle implements only the **first gating requirement**, a zero-write audit of system ANGLE support on Android15 via Shizuku UID2000. No privilege escalations, root, VM, game's APK modification or unsafe driver selection.

## Primary sources and safety
- Reviewed live GitHub `AGENTS.md`, `prompt/RESTORE_AR.md`, current STATUS/MEMORY/PLAN, all relevant main MO `MainActivity.java`, `ReadOnlyProbeService.java`, `ReadOnlyCommandPolicy.java`, AIDL, Gradle, Manifest, and CI workflow.
- Official ANGLE sources:
  - https://android.googlesource.com/platform/external/angle/+/HEAD/doc/DebuggingTips.md documents `debug.angle.gl_renderer` and related override strings.
  - https://android.googlesource.com/platform/packages/modules/ANGLE/+/51ed50098f5f67d70778e6d91da2963647654d64/Readme.md documents per-app driver choice, settings keys, and warns **`angle_gl_driver_all_angle=1` can prevent device boot** on some builds.
  - https://github.com/google/angle/blob/main/src/android_system_settings/README.md says Settings.Global values are source of truth for supported configs; absence of selection alone does **not** prove ANGLE missing.
  - https://developer.android.com/ndk/guides/rootless-debug-gles explains production-app rootless injection gating.
- Existing POCO context: Outlanders `com.tencent.mhadv` targetSdk36, debuggable NO, manifest external GLES layer opt-in NO; no demonstrated game injection under Shizuku ADB Shell UID2000. Built-in system ANGLE and `setprop` write permission on this specific firmware **not yet observed**.

## New implemented Android source
1. `app/src/main/java/com/alwansan/mo/AngleReadOnlyCommandPolicy.java`: fixed allowlist of **14** commands only: `pm list packages angle`, `pm path` for three known ANGLE package IDs, five `settings get global` keys including boot-risk `angle_gl_driver_all_angle` as **read-only**, and five `getprop` keys including `debug.angle.gl_renderer`/vendor/version and Android debug build metadata. No parameter from app user passed into command. Verifier rejects `settings put/delete`, `setprop`, arbitrary `pm`, shell commands.
2. `app/src/test/java/com/alwansan/mo/AngleReadOnlyCommandPolicyTest.java`: JUnit verify all fixed commands are allowlisted/read-only and writes, unusual package IDs, dangerous shell inputs rejected.
3. `app/src/main/aidl/com/alwansan/mo/IReadOnlyProbe.aidl`: new `String getAngleSnapshot() = 7`, maintaining previous method IDs.
4. `app/src/main/java/com/alwansan/mo/ReadOnlyProbeService.java`: service method gated by `Os.getuid()==2000`; executes only policy commands through existing capped/timed `run()`; returns explicit no-claims and all-app boot warning. Does not touch `Settings.Global` values.
5. `app/src/main/java/com/alwansan/mo/MainActivity.java`: new `05B P13 ANGLE CAPABILITY — READ ONLY` panel and explicit button, Shizuku UID/permission precondition, async service call/log `ANGLE_CAPABILITY_READ_ONLY`, report/Download log text includes ANGLE state. Bumped UserService version from4 to5 for new AIDL. No automatic new graphics writes, no ANGLE apply button, no Outlanders test.
6. `app/build.gradle`: versionCode11, versionName `0.11.0-alpha.11`. `probe/` remains unchanged v0.6.0.
7. `docs/P13_ANGLE_READONLY_DEVICE_TEST_AR.md`: exact device test steps, interpretation gates, and high-severity AOSP warning to **never set angle_gl_driver_all_angle=1**.

## Honest acceptance and remaining proof
- Static source and JUnit/CI may prove code compiles and fixed command policy rejects writes; they **cannot prove ANGLE is present or operates on POCO**. User must run new main app on phone and send its read-only ANGLE panel or `Download/log.txt`.
- A successful `pm path`, `getprop` reading or `settings get` is only a capability clue. **Not** proof that external app reads `Adreno840`; proof later requires independent app's actual GLES API `glGetString(GL_RENDERER)`, real driver evidence and exact before/after/rollback. Native Vulkan/Java Build need separate evaluation.
- Next optional A/B stage requires separate explicit user consent to modify settings (owned/cooperating app only), journal and rollback; no global all-app driver enable, root, Zygisk or protected game injection.
- Source complete before docs commit: `5666cb45c6f8601acf14aeaca02a8b626979ff63`. Auto triggered CI [42332224614](https://github.com/alwansan/MO/actions/runs/42332224614), pending build verification. Intermediate source CI may be cancelled due to consecutive pushes.
- No phone access or settings writes, no APK installation and no runtime game change in this cycle.

## Next
- Confirm P13 CI app unit test + Android/AIDL/NDK build + publish both raw APKs. Fix source compile failures if any. Only then give verified APK link.
- Obtain user ANGLE read-only report for this exact firmware; decide whether a safe owned-app-only ANGLE A/B test is technically possible.
- Do not repeat P10 or P11 benchmark as primary progress.

## Final verified CI/release status (supersedes pending section above)
- Completed [GitHub Actions 42332224614](https://github.com/alwansan/MO/actions/runs/42332224614): `completed/success`, source `5666cb45c6f8601acf14aeaca02a8b626979ff63`. Test phase and APK builds for both apps, release publish all passed.
- Public prerelease [`mo-v0.11.0-alpha.11-run59`](https://github.com/alwansan/MO/releases/tag/mo-v0.11.0-alpha.11-run59), assets state uploaded:
  - `MO-Graphics-Probe-v0.11.0-alpha.11.apk`, 1,167,998 bytes SHA256 `4372a3b077632925df8c51161843f110c53777d9009254c6c51c2ffd6fe5925f` ([direct APK](https://github.com/alwansan/MO/releases/download/mo-v0.11.0-alpha.11-run59/MO-Graphics-Probe-v0.11.0-alpha.11.apk)).
  - `MO-Layer-Test-v0.6.0.apk`, unchanged source, 208,508 bytes SHA256 `8540a452ee162a7e13a94a9e28d6ba2e50883239a3c44565ee3c37ca040fd607`.
- Verified branch main SHA immediately prior to this closeout: `e161e5c8f0f40da2af1b7ba52b710c3805282352`; documentation-only commits after P13 source do not trigger a new Android source build. Latest Actions run `42332224614`, `completed/success`, and latest raw APK assets match the code.
- Updated `README.md`, `AGENTS.md`, `prompt/PLAN_AR.md`, `prompt/STATUS.md`, `prompt/MEMORY.md`, `prompt/DECISIONS.md`, `prompt/RESTORE_AR.md`, `docs/IMPLEMENTATION_PHASES.md` and `docs/P13_ANGLE_READONLY_DEVICE_TEST_AR.md`.
- **Device test remains pending**. Do not claim ANGLE package selection or GL_RENDERER override on POCO, or actual independent-game identity spoof, until owner supplies direct read-only P13 report and later a consented reproducible A/B. No device write, root or game changes this cycle.
