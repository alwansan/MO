# MO-20261108-CHAT-P22-RENJANA-OWNED-LAB-035 — build a safe isolated Renjana test APK
**2026-11-08 Asia/Baghdad | origin=chat | branch=main**

## Owner goal
Following P21 research, owner authorizes practical continuation and requests exact app/download/commands/installation steps for a very lightweight no-root Android application virtual container. Goal is eventual independent app (not current original game) GPU identity experiment without compromising original Outlanders or Google account.

## Live upstream audit
- Repository `josskixg/renjana` main commit `14302a57cd66114c6979acf7ca56c97f845a6841`; Apache License 2.0 verified. Version in `app/build.gradle` 0.2.0, minSdk29, targetSdk34, ABI splits armeabi-v7a/arm64-v8a, Gradle Kotlin1.9.20, AGP8.5, runner SDK34 Java17. No official downloadable Release was found.
- `InstanceLauncher.kt` explicitly falls back to direct launch of an external app without sandbox if stub/Pine fails; test must distinguish `LaunchResult.Success` vs `FallbackNoIsolation`.
- `docs/CREATE_INSTANCE.md` confirms toggles Enable GMS / Enable Fingerprint / Spoof Signature / Enable Anti-Detection; all OFF for first owned-app test.
- Official developer README assertions are not treated as validated on device.

## MO changes
- Created `.github/workflows/mo-renjana-owned-lab.yml`, standalone from core MO. Triggered only by its own initial push or manual invocation. Checkout exact pinned upstream SHA, test Apache license, JDK17, Android SDK34, Gradle8.11.1; run upstream unit tests; build ARM64 debug APK, verify zip/APK signing, package name, SHA256, carry provenance LICENSE, then issue a clearly labeled GitHub prerelease raw APK **only if tests/build pass**. CI build job uses contents:read; release job contents:write, no running third-party code; no secrets or original game files copied.
- Workflow push commit `00b40cc7b8000b23edcf67c14487e4ed0d5b01d6`, GitHub Actions run `42429358721` (https://github.com/alwansan/MO/actions/runs/42429358721). Initial inspected step result: checkout pinned source, Apache check, JDK17, Android SDK34, Gradle setup passed; test still in progress; DO NOT report APK ready unless CI and release confirmed.
- Wrote `docs/P22_RENJANA_OWNED_LAB_INSTALL_AR.md` with exact owned app test steps, safe Rish filtered logcat command, and existing direct APK `MO-Layer-Test-v0.6.0.apk` from `mo-v0.11.0-alpha.11-run59`. No production game install, clone, Google account, spoofing, debug system settings or device changes authorized.

## Next action and acceptance gate
After verifying CI success/release, owner can install an experimental Renjana APK + owned MO Layer Test v0.6, create instance with all advanced toggles OFF, inspect baseline and virtual PID/Build.MODEL/GLES/Vulkan and Renjana StubActivity logs (reject fallback). Then test isolated Java Build identity if host and guest stability demonstrated. No GL/Vk fake hardware promise. No phone changes performed by this session.
