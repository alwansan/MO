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
[GitHub Actions → MO Android Debug APK](https://github.com/alwansan/MO/actions/workflows/mo-android-debug.yml). On successful run open the run → Artifacts → `MO-Graphics-Probe-debug-...` → download and install `app-debug.apk`. Debug-only, no signed release claimed.

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
