# MO — Android graphics identity research & diagnostics

**Active phase: P1 development.** Native Android app for POCO F5 Pro Android 15, locked bootloader, without root. This repo contains original code and research only.

The end goal is to understand why games such as **Monster Hunter Outlanders (`com.tencent.mhadv`)** lock FPS/graphics choices and explore supported non-root options. There is no verified Shizuku-only technique that globally spoofs Android Build/GL/Vulkan properties inside unmodified commercial games.

## What works in P1 source
- Arabic-first local diagnostic UI, no network/storage permission.
- Official Shizuku API/provider **13.1.5**; reports binder, permission and actual service UID. Rejects any root path.
- Android model/SoC/ABI/RAM, current screen refresh.
- **Actual EGL14 GLES2 `GL_VENDOR` / `GL_RENDERER` / `GL_VERSION` read in MO's own process**, not a global game spoof.
- Shows installation/version of `com.tencent.mhadv`, `com.garena.game.mho`, PUBG and BGMI.
- Copies an optional local evidence report (no account or Android ID).
- JUnit logic tests and GitHub Actions automatic debug APK artifact.

### Build
**Direct APK downloads:** [MO Releases](https://github.com/alwansan/MO/releases) — each successful main-branch build publishes two separate `.apk` release assets (MO Graphics Probe + MO Layer Test), without wrapping them in ZIP. These remain experimental **debug-signed** APKs; installer signatures may differ between builds.

Locally with Android SDK 35, Java17 and Gradle8.11.1:
```
gradle :app:testDebugUnitTest :app:assembleDebug
```

Test: start Shizuku via Android Wireless Debugging, open MO and authorize; verify UID **2000** and the actual GLES output. If the server is not available the app should still show device/GPU information.

## Hard limits
We will NOT root your phone, unlock the bootloader, run Zygisk, run VMs, clone apps, uninstall original games, or change protected GPU drivers. Shizuku via wireless debugging has ADB shell UID2000 and **cannot hook arbitrary non-debuggable games**.

Next P2 research: Shizuku read-only UserService capability checks; no action altering games until tested. See [phase plan](docs/IMPLEMENTATION_PHASES.md), [original research](research/), [restore prompt](prompt/RESTORE_AR.md), and [agent instructions](AGENTS.md).

All code here is independently written. Third-party iUnlockerGL proprietary binaries are not vendored; source references remain in research docs.

## P2 alpha.2
Owner confirmed on POCO that Shizuku API13 UID2000 is GRANTED and Outlanders is installed. Version 0.2.0-alpha.2 adds fixed *read-only* UserService checking Android Game Mode and ANGLE global values for com.tencent.mhadv, plus declared Vulkan feature status (not actual Vulkan GPU driver properties). No settings or app changes. See docs/DEVICE_FEEDBACK_2026-11-07.md.

## P3 v0.3 native Vulkan inspection (read only)
2026-11-07 second POCO report verified Shizuku UserService UID2000, Game Mode [standard,custom] but **no configured intervention**, ANGLE selection null and declared Vulkan1.1. V0.3 adds original NDK C++ `vkEnumeratePhysicalDevices` and `vkGetPhysicalDeviceProperties` inside MO's own app to measure the actual Vulkan device, API/driver version and hardware limits. Android ROM/driver may expose different capability tiers in different applications; this does not spoof Outlanders or force new graphical modes. Report newline formatting fixed. APK CI validates build; user must validate Vulkan on device.

## P4 disposable GLES layer probe (in progress)
A second **separate** APK is built from `probe/`: `com.alwansan.mo.glesprobe`. It's a disposable owned target, not a clone of Outlanders, and never reads game data. It includes an original Android GLES debug layer intercepting `glGetString(GL_RENDERER)` and returning `MO-GLES-LAYER-PROOF (SIMULATED)` **only when officially enabled for this debuggable test app**. Its Mock 120 FPS indicator is a demonstration, not real FPS or graphics quality unlock. Settings activation/rollback UI remains next step.
P3 POCO Vulkan results are in docs/DEVICE_FEEDBACK_ALPHA3_2026-11-07.md.

## P4 test layer controls
MO v0.4 has a user-confirmed native GLES layer trial ONLY for disposable MO Layer Test app (com.alwansan.mo.glesprobe). It backs up four developer GPU debug settings with exact presence and values and restores them after the trial. User must restore before uninstalling/updating MO. No change to Monster Hunter Outlanders or other games; unlocking mock FPS in test does not unlock real FPS. Docs: docs/OWNED_GLES_LAYER_TEST_AR.md.


## Direct GitHub Releases distribution
Workflow `.github/workflows/mo-android-debug.yml` runs Java tests, builds **both APKs**, verifies APK archives, and creates a pre-release tagged `mo-v<version>-run<number>`. GitHub Releases contains raw `.apk` assets only, not Actions ZIP artifacts. Restore GPU Debug settings **before uninstalling or updating MO** if its owned GLES experiment was enabled. See docs/RELEASE_DISTRIBUTION_AR.md.

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

## MO-20261107-CHAT-P8-RECOVERY-013 — P7 real device and P8 safety fix
- User v0.7 report proved native and eglGetProcAddress renderer interception in owned debuggable MO Layer Test, PID 20827; Java path remains Adreno730. EGL_ANDROID_GLES_layers declared YES; synthetic FPS tier only, not real FPS gain.
- **Outlanders** com.tencent.mhadv version1.0.9.0.3308842, targetSdk36, Debuggable NO, opt-in `com.android.graphics.injectLayers.enable` NO: stock app not eligible for official external rootless GLES layer. Do not claim Shizuku can override this.
- **Urgent persistent settings:** Shizuku reports 4 MO debug keys still enabled. Earlier v0.7 could save already-active test state as original baseline, and user may have lost v0.5 backup across APK reinstall. Check normal Restore in currently installed v0.7 then re-read keys; a success string alone is insufficient.
- P8 source v0.8.0-alpha.8 adds `OwnedLayerRecoveryPolicy` and JUnit tests, refusal to activate when any debug config already exists, refusal to restore from a snapshot of active MO settings, and separately consented strictly guarded **MO-owned stale cleanup** (sets enable=0, deletes 3 exact owned markers only, captures pre-cleanup snapshot before writing, verifies, partial failure rollback).
- This cleanup is **not** reconstruction of unknown old baseline values. Keep original games untouched; need CI build result and user device confirmation.
- Evidence docs/DEVICE_FEEDBACK_ALPHA7_2026-11-07.md; recovery guide docs/GPU_DEBUG_RECOVERY_AR.md.

## MO-20261107-CHAT-P9-REFRESH-014 (2026-11-07): v0.8 cleanup success, stale cached report fix
- User v0.8 screenshot shows guarded cleanup returned OK (backend checks 0 and missing owned markers); retry activation protection also correctly refused already configured MO developer GPU layer settings.
- Copied report still contained `enable_gpu_debug_layers=1` in independently cached shell readout; source code confirmed it never refreshed `shellOutput` after cleanup. Do not construe this as proof cleanup failed; require fresh readback from user.
- v0.9 source refreshes shell panel and exact Shizuku-owned four key diagnostic after each operation, includes readback in report and invalidates stale data on refresh failure.
- User should re-diagnose GPU settings and force-stop/reopen the owned test app, verify Native GL_RENDERER returns Adreno730 before updating. Do not enable a new experiment.
- Outlanders game still Debuggable NO, external layer opt-in NO; no rootless official GLES hook possible via Shizuku alone.
- Evidence: docs/DEVICE_FEEDBACK_ALPHA8_2026-11-07.md.
