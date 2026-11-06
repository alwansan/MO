# MO Implementation Plan (begun 2026-11-07)

P1 is now **IMPLEMENTED IN SOURCE** (build/device runtime not proven until CI/device test):
- Original Android Java app, `com.alwansan.mo`, minSdk28, targetSdk35, compileSdk35.
- Gradle 8.11.1 / AGP 8.9.2 / Java17, Shizuku official api/provider 13.1.5.
- Shizuku UID and permission check; rejects UID0 and unrecognized identities.
- Device Build, SoC, ABI, CPU cores, RAM, current display Hz.
- EGL14 GLES2 offscreen context to query actual GL_VENDOR/GL_RENDERER/GL_VERSION and MAX_TEXTURE_SIZE.
- Detects Outlanders Global `com.tencent.mhadv`, Garena `com.garena.game.mho`, PUBG and BGMI by package visibility.
- Local copyable redacted report.
- CI builds debug APK on GitHub Actions and uploads only artifact; no Release or proprietary binaries.
- No storage/network/root privileges, no commands, no game modifications.

P1 verification gate:
1. GitHub Actions testDebugUnitTest + assembleDebug successful, APK SHA-256 recorded.
2. Install APK on POCO F5 Pro; start Shizuku normally via Wireless Debugging; tap 'السماح' and confirm UID 2000.
3. Confirm GLES/Build and game version data match device; copy diagnostic report with no identifiers.
4. Keep all games intact. UI screenshot and crash/permission report if issues.

P2 planned:
- Implement typed restricted Shizuku `UserService` (not insecure arbitrary `sh -c`) for *read-only* system probes, after obtaining confirmed device feedback.
- Compare Android Game Mode / ANGLE availability on specific target without writing any setting.
- Respect renderer selection and game whitelists; never claim Shizuku can spoof closed game processes.

P3 planned:
- Independent GLES/Vulkan layer inside **own debuggable app** to prove official read interception; user consent + rollback if developer settings involved.
- Own test game that gates mock FPS options by Android Build vs GL string. Not PUBG/Outlanders modifications.

P4 exploratory:
- Only if no existing safe method, evaluate internal bootstrap in own APK (AppComponentFactory, driver in cooperating process). Third-party source license review mandatory; no original games re-signed; no VMOS/App Cloner per owner refusal.
- Do not integrate iUnlockerGL proprietary components into public MO.

Important limitation: without real root, cooperation, or modifying a target APK, no general third-party game process hooks are available from Shizuku shell UID2000. Do not claim otherwise.
