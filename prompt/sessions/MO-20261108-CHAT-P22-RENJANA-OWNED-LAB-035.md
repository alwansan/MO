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

## Final CI outcome — verified 2026-11-08
- First run `42429358721`: all upstream Java unit tests, ARM64 APK build, APK signing/package checks passed, but `publish-explicitly-experimental` failed because `SHA256SUMS.txt` named `renjana-output/Renjana-v0.2.0-arm64-OWNED-LAB-debug.apk` and after transfer `release/` no longer contained that subdirectory. This is a **release artifact path bug only**; source compilation did not fail.
- Fixed workflow to compute checksum **from within** `renjana-output` so path is portable. Workflow-fix commit `77867f4ec178fadfb25719692be859f1333f2300`.
- Second run `42429759178` (https://github.com/alwansan/MO/actions/runs/42429759178) completed **SUCCESS**: test, build, signing verification, artifact download, checksum verification, and GitHub prerelease step all succeeded.
- Confirmed public GitHub prerelease (not draft): https://github.com/alwansan/MO/releases/tag/renjana-owned-lab-run2 (ID 437937741); exact direct asset `Renjana-v0.2.0-arm64-OWNED-LAB-debug.apk`, **55,348,455 bytes**, SHA256 `7b62bfad247050e03bcdcefd693cb47b14d45b4c5ac945ca74fa58f092349b63`; asset uploaded ID 722395830, state uploaded. License, provenance and checksum separately uploaded.
- User is now given the raw APK verified GitHub URL and `MO-Layer-Test-v0.6.0.apk` from verified previous MO release. Instructions updated `docs/P22_RENJANA_OWNED_LAB_INSTALL_AR.md`, include curl and sha256sum, install via Android Files, only owned test instance with 4 invasive toggles OFF, Rish `ps -A -o PID,NAME`/filtered logcat and dumpsys meminfo. No original game cloned, no accounts entered, no privileged system changes and no device-tested performance or GPU spoof proven.
- Followup gate remains user screenshots + logs from actual Android15 device, proving guest ran under Renjana process (no fallback) and read Java/NDK GLES/Vulkan, plus memory costs.
