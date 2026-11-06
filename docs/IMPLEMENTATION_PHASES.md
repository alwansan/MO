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

## Verified build — 2026-11-07
- [x] Build workflow SDK fix after obsolete tools package failure.
- [x] Run https://github.com/alwansan/MO/actions/runs/42305983786 **SUCCESS**.
- [x] `:app:testDebugUnitTest` and `:app:assembleDebug` both success.
- [x] Artifact uploaded: `MO-Graphics-Probe-debug-2` (ID `13545813166`).
- [x] APK bytes locally tested as ZIP after downloading GitHub artifact.
- [x] SHA256: `13c72cbb626e4ec6238df7e5c9262c5781b1f819487753c568c90befe2a2fba2`.
- [ ] UI, permissions and GLES results verified by user on POCO F5 Pro.
- [ ] Shizuku read-only UserService and supported settings diagnostics P2.
- [ ] In-process graphics instrumentation proof in our own app only P3.
**Never turn build success into an assertion of rootless GPU spoof inside a commercial title.**

## P1 device verified, P2 added MO-20261107-CHAT-P2-DEVICE-007
- [x] P1 tested on POCO: Shizuku UID2000 GRANTED API13; Adreno730 GLES3.2; com.tencent.mhadv v1.0.9.0.3308842.
- [x] Read-only P2 Shizuku UserService source, explicit allowlist, no external arbitrary shell; Vulkan declared features.
- [ ] P2 CI result and user phone service connectivity: check latest GitHub Actions.
- [ ] Real native Vulkan GPU metadata and owned-app GLES layer P3.
See docs/DEVICE_FEEDBACK_2026-11-07.md. No claim of FPS unlock or game spoof.

## P2 build gate — pass
- [x] https://github.com/alwansan/MO/actions/runs/42307946638 SUCCESS all steps (unit tests, assembleDebug, checksum, upload).
- [x] Valid APK artifact `MO-Graphics-Probe-debug-3` id13547166555, 125919 bytes sha256 7a98dbbb0393256210b76249c79505df34b89e34e98c3677545cd0d6b5e23d5b.
- [ ] P2 Shizuku read-only UserService runs on POCO and returns Game Mode/ANGLE (owner install/report required).
- [ ] Vulkan declared features returned on POCO.
- [ ] Actual Outlanders graphics options unlock remains **not implemented** and may not be achievable with Shizuku under current stock device constraints.

## Signing migration issue
- GitHub-hosted ephemeral build keys vary per run. Android v0.1 certificate SHA256: `6ea43d29063eae454fa7769c20392cd58effe4653585d79df5c5384b3ce7a2da`.
- Android v0.2 certificate SHA256: `4d2e87fa998d2c8c901c6e8f21417fbe28ce6b2f8109e35cb8dd7ef46f1be365`.
- The only safe immediate update path is uninstall **MO** only, install v0.2. Existing Shizuku and original Outlanders untouched.
- Implement stable signing using owner-provisioned **GitHub Actions encrypted Secrets** before next release. Never publish private keystore in repo or chat. Changing keys later again means uninstall one final time unless managed signing key transition is available on OS and target.

## P2 on-device success / P3 Vulkan MO-20261107-CHAT-P3-VULKAN-008
- [x] User v0.2 real device UserService returned UID2000 + Game Mode: Outlanders [standard,custom], current standard, no interventions.
- [x] ANGLE per-app selection null; debug layers 0; props taro/adreno/SM8475; declared Vulkan1.1/compute0/level1.
- [x] Fixed literal \\n in report source.
- [ ] P3 native VkPhysicalDeviceProperties on device (new v0.3 source; build/test required).
- [ ] Own-app official GLES/Vulkan layer proof; not a general closed game hook.
- [ ] Stable signing Secrets configuration (CI debug cert changes on each run, MO reinstall likely required).
See docs/DEVICE_FEEDBACK_ALPHA2_2026-11-07.md.

## P3 CI pass, pending on-device Vulkan proof
- [x] Workflow run 42308837332 success incl tests, CMake/NDK 27.3 and Java/Android APK build.
- [x] ARM64 libmo_vulkan.so included, verified via APK ZIP inspection.
- [x] APK v0.3.0-alpha3 SHA256 `203b8c47d3da67cca1e6ffe212682824d67a864a556b6cd12d7717b7f14dbe92`, artifact ID13547731335, 1102410 bytes.
- [ ] Verify native Vulkan GPU query on POCO, user screenshot/report.
- [ ] Stable signing Secrets in CI (debug keys change each run).
- [ ] Actual gameplay option unlock is not implemented and not guaranteed possible with shell-level Shizuku.

## P3 on-device PASS / P4 owned test app source — MO-20261107-CHAT-P4-LAYER-009
- [x] Native Vulkan loader/device query passed on real POCO: Adreno730, Vulkan1.1.128, vendor0x5143, device0x7030002, driver0x8026705F. Evidence docs/DEVICE_FEEDBACK_ALPHA3_2026-11-07.md.
- [x] Created disposable debuggable app `com.alwansan.mo.glesprobe` and own GLES intercept library `libMOGlesProofLayer.so`. Synthetic FPS tier toggles on *simulated renderer string* ONLY in test app.
- [ ] Verify GitHub CI builds two separate APKs.
- [ ] Add transactional Shizuku global GPU debug settings controller for **test app only**, with per-key backup and rollback; no writes until user action.
- [ ] Prove layer loader intercept on POCO. This proof does not grant permission to hook an unmodified nondebuggable Outlanders game.
Official GLES layer docs: https://developer.android.com/ndk/guides/rootless-debug-gles

## P4 controlled layer transaction
- MO v0.4 implements a confirmation-only GPU debug layer test for disposable app com.alwansan.mo.glesprobe.
- Shizuku UID2000 service captures exact presence/value for four Settings.Global keys before writing and stores rollback in MO app-private SharedPreferences; rejects another active debugger, verifies writes, restores exact old state and prevents overwriting external edits.
- Never accepts a third-party game as target; no VM, root or APK cloning.
- Requires separate test APK from GitHub Actions, and actual device testing before claiming GLES interception.
- **Restore settings before uninstalling/updating MO** due per-run Debug signing changes.
- Guide: docs/OWNED_GLES_LAYER_TEST_AR.md.

## P4 build / rollout gate
- [x] Owned separate test package + ARM64 native lib built and validated ZIP.
- [x] Version v0.4 UI+Shizuku AIDL scoped global debug settings activation/restoration, fixed target and user confirmation.
- [x] CI run 42310714620 passed unit tests, compiled two Android APKs and native C++.
- [ ] On POCO prove baseline Adreno730 -> MO-GLES-LAYER-PROOF (SIMULATED) -> Adreno730 after exact rollback. No other target app selected.
- [ ] Check HyperOS respects official GLES external layer config. A successful Settings.Global write is NOT proof the layer loaded.
- [ ] Stable APK signing secret for future same-package upgrades (no public signing key).
- [ ] No rootless integration with stock Outlanders; original FPS/graphics locks unchanged.
Guide docs/OWNED_GLES_LAYER_TEST_AR.md.
