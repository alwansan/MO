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

## New delivery channel MO-20261107-CHAT-RELEASE-010
- CI now uploads raw APKs to GitHub Releases on main branch after successful unit tests and build, not a zipped Actions artifact.
- Each release is tagged `mo-v<appVersion>-run<runNumber>` and contains separate MO Graphics Probe and MO Layer Test APKs.
- Verify repository Releases API actually shows both APK files after workflow run. `--prerelease` until device test and stable signing.
- Release documentation `docs/RELEASE_DISTRIBUTION_AR.md`.

## P4 real device status / P5 diagnosis MO-20261107-CHAT-P4-FEEDBACK-011
- [x] Shizuku UID2000 writes developer GPU debug settings for *owned test* only.
- [ ] GLES layer actually intercepts `glGetString`: **NO on user screenshots**, still Adreno730/Mock FPS LOCKED.
- [ ] Owner restoration of exact original GPU debug settings **NOT YET CONFIRMED**; must happen before upgrading/installing next MO.
- [x] Added P5 device diagnostics in source: Java vs native glGetString / PID / manual ELF load test / Shizuku read-only EGL log filter.
- [ ] Check P5 GitHub Actions and direct Release assets; after restoration test app's new process behavior.
See docs/GLES_LAYER_TROUBLESHOOTING_AR.md.

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
