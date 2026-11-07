# MO session MO-20261107-CHAT-P10-IDENTITY-LAB-017
Date: 2026-11-07 Asia/Baghdad | origin=chat
Repository: https://github.com/alwansan/MO | writable branch: main
Starting HEAD: `7e5d67bfd2fb8d475927bd836ef85e527a21f78d`
Preceding audit session: `MO-20261107-CHAT-ZYGISK-COPG-AUDIT-016`

## Purpose / owner instruction
User requested "Continue" after a live ZygiskNext/ReZygisk/NeoZygisk/COPG feasibility research session. Resume the next proposed P10 engineering proof: a **clean-room, MO-owned Game Identity Laboratory** showing independent in-memory device identity gates and synthetic graphics/FPS menu tiers. Do not install root/VM/clone, bypass game protection, patch Outlanders, modify Settings.Global, activate GPU layers or pretend any real FPS improvement.

## Live repository revalidation
- Read `AGENTS.md`, `prompt/STATUS.md`, `prompt/MEMORY.md`, `prompt/RESTORE_AR.md`, `prompt/DECISIONS.md`, `prompt/PLAN_AR.md`, `docs/DEVICE_FEEDBACK_ALPHA9_2026-11-07.md`, preceding session, `probe/` main sources, its manifest, build.gradle, tests, NDK CMake, and the Actions workflow.
- Starting HEAD `7e5d67bfd2fb8d475927bd836ef85e527a21f78d` documented v0.9 actual four-key GPU Debug cleanup; **fresh post-cleanup owned-app PID** remains unverified on phone.
- Existing probe `com.alwansan.mo.glesprobe` is a self-owned disposable, debuggable, explicitly opt-in GLES layer target. It already has a Java/NDK native renderer reader; we extend it without altering the actual layer or controller.
- MO main version `0.9.0-alpha.9`; probe v0.3.0 before changes; native NDK/Gradle versions pinned in CI.

## Code implemented
1. `probe/src/main/java/com/alwansan/mo/glesprobe/IdentityLabPolicy.java` — immutable real `Signals`, per-surface simulated `Mode`, explicit fake strings labeled SIMULATED, independent mock flags (Build.MODEL, SoC, Java GLES, NDK GLES, Vulkan), combined hypothetical 120 tier requiring all mock flags plus actual >=120Hz panel refresh. No Android API calls or system writes.
2. `probe/src/test/java/com/alwansan/mo/glesprobe/IdentityLabPolicyTest.java` — JUnit assertions for real values, isolated overrides, all mock gates vs physical refresh, null/unavailable readings and non-mutated input.
3. `probe/src/main/java/com/alwansan/mo/glesprobe/GameIdentityLabActivity.java` — own-process read-only EGL and NDK GLES, Java Build and SOC, Vulkan physical GPU name, display Hz; Spinner changes local synthetic values only. No Shizuku, Settings.Global or game data access. Clear Arabic/English simulation disclaimers.
4. `probe/src/main/java/com/alwansan/mo/glesprobe/ProbeNativeRenderer.java` and `probe/src/main/cpp/mo_probe_renderer.cpp` — JNI entry reads actual Vulkan `VkPhysicalDeviceProperties.deviceName` in MO test process, fails closed to an unavailable label; no Vulkan layers or hook.
5. `probe/src/main/cpp/CMakeLists.txt` links `vulkan`; `ProbeActivity.java` exposes optional button; `probe/src/main/AndroidManifest.xml` registers lab Activity as `android:exported=false`.
6. `probe/build.gradle`: owned probe versionCode4 / versionName `0.4.0`. MO main source version **unchanged** `0.9.0-alpha.9`.
7. `docs/OWNED_GAME_IDENTITY_LAB_AR.md` and `docs/IMPLEMENTATION_PHASES.md` explain scope, source, test/install gates and signer warning.

## CI tracking
- Auto-triggered latest complete-source P10 workflow run: https://github.com/alwansan/MO/actions/runs/42324225397 (source commit `4166210d4fb5e62b391b759f3b4508eb50d02e97`), pending initial check.
- GitHub CI later showed `Run unit tests` **completed SUCCESS** and `Build debug APKs` **in progress** while documentation was being written. Do not state APK/release exists until actual completed success and uploaded raw APK assets are verified.
- Earlier short-lived runs were cancelled during successive source pushes; only the latest source run is the delivery candidate. Docs-only commits do not trigger source-path workflow.
- Final build status to be appended once verified in this same session.

## Safety / what did not happen
No Shizuku write, no new GPU layer activation, no root, no bootloader change, no VM/clone, no stock game file modification, no third-party proprietary Zygisk/COPG/iUnlockerGL code/binaries imported, no direct phone access, no proof of FPS unlock in Outlanders. Existing owner v0.9 cleanup report shows GPU Debug 0 and three owned keys not present at report time. Updated lab APK may have **different per-run debug signature**; user must not uninstall/update main MO until rollback state is secure.

## Next evidence
- GitHub Actions `:app:testDebugUnitTest`, `:probe:testDebugUnitTest`, native ARM64 builds, raw `MO-Layer-Test-v0.4.0.apk` Release asset verification.
- On phone after confirming GPU Debug keys remain disabled: reopen existing MO-owned test in new process for definitive post-cleanup PID/Adreno readback; then test new lab, comparing one simulated channel and ALL modes, and prove physical output remains same. Never trigger a new layer experiment merely for this readback.

## Final P10 CI/release verification
- `https://github.com/alwansan/MO/actions/runs/42324225397` completed **SUCCESS**, job `verify-and-build` all steps passed. Logs explicitly show `:app:testDebugUnitTest`, `:probe:testDebugUnitTest`, `:app:assembleDebug`, `:probe:assembleDebug` and two Gradle BUILD SUCCESSFUL messages, followed by release publishing. ARM64 native Vulkan was linked and built.
- Release https://github.com/alwansan/MO/releases/tag/mo-v0.9.0-alpha.9-run25 is public, prerelease, draft=false, both raw assets state=uploaded:
  - `MO-Layer-Test-v0.4.0.apk` — 172148 bytes, SHA-256 `6fdc64c09d76bd0b071d46f5e10f850347a79212256a846a745ac9e8263095dd`.
  - `MO-Graphics-Probe-v0.9.0-alpha.9.apk` — 1135214 bytes, SHA-256 `42fdb05b4256feb1ff6024f43f99b9206bbbd17c40f70ff925cbd719a9965282`; **a re-signed main build, not an app-feature upgrade**. Do not install main just to access lab.
- Current main HEAD immediately before this session-file closeout: `d3001281b9d62b89c4c616fd5811da184f348fec`. This includes documentation-only updates after the verified source commit; docs-only paths did not create new app builds.
- New docs `docs/OWNED_GAME_IDENTITY_LAB_AR.md`, updated `docs/IMPLEMENTATION_PHASES.md`, `README.md`, `prompt/PLAN_AR.md`, `prompt/STATUS.md`, `prompt/MEMORY.md`, `prompt/DECISIONS.md`, `prompt/RESTORE_AR.md`, `AGENTS.md`.
- **Outstanding:** no POCO P10 runtime result yet, no fresh post-last-cleanup owned-app PID confirmation, no measured Outlanders FPS unlock. No phone/device/game write or ROOT action in this cycle.
