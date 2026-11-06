# بحث مشاريع الرسوميات والتزييف بدون روت — MO
التاريخ: 2026-11-07 | الجلسة: MO-20261107-CHAT-ECOSYSTEM-003 | الحالة: بحث في المصدر، **ليس اختبارًا على الجهاز**.

## خلاصة فنية
Shizuku ADB user 2000 is a **privileged command/API transport, not a root/runtime injection framework**. Distinguish mechanisms:
1. System developer settings (ANGLE per package, debug.hwui.renderer, Game Mode): require relevant OEM support; no GPU spoof.
2. Official GLES/Vulkan layer loader: executable in cooperative eligible target (debuggable or manifest opt-in); no APK repack for that class.
3. APK repack/embedded library: original APK must change and be re-signed; works in that *replacement APK process*, not the signed original package. Third-party games may reject altered signatures, lose app-private data, fail split package or integrity checks.
4. Virtualized/container apps: custom runtime in host process, often incompatible with protected/native games and OEM-specific Android versions.
5. Zygisk/LSPosed/root kernels: no-go under the owner's locked, nonroot constraint.

## High-priority upstreams (verified by reading published README and selected source)
| Project | Repository | Mechanism | Rootless relevance | Evaluation / caveat |
|---|---|---|---|---|
| GAMA (Graphics API Manager) | https://github.com/palincat/gama | Shizuku `setprop debug.hwui.renderer` (skia Vulkan/OpenGL UI renderer) | High for shell integration and verified rollbacks | **Affects Android HWUI 2D UI rendering**, not the Vulkan/GL driver API used directly by games. Restarting system UI/launcher can destabilize HyperOS. See ShizukuHelper.kt + RendererController.kt. MIT. |
| KaosGFX | https://github.com/ulolol/Ka0SGFX | In nonroot mode, modifies base.apk with `AppComponentFactory`, adds DEX & native libraries and re-signs splits; Shizuku installs/re-stages driver; supports Vulkan via in-process hooking | **Highest conceptual relevance** but requires APK replacement | README claims Adreno 750 nonroot test on Android 16; **claim not independently verified on POCO Android 15/Adreno 730**. AGPL-3.0. Code `PatchOrchestrator.kt`, `ApkPatcherEngine.kt`. |
| CatSmoker | https://github.com/catsmoker/catsmoker-app | Shizuku applies supported game configs/files and Game Mode; root Xposed hooks Java/native GL | High for game-data/ADB mode, high for distinguishing claimed spoof from actual | Rootless channel **does not equal global OpenGL/Vulkan spoof**. Game asset config edits can violate game rules and break files. |
| LSPatch (original) | https://github.com/LSPosed/LSPatch | Rootless patch/re-sign target APK with embedded Xposed runtime | High to understand APK self-instrumentation | Source archived Dec 2023; Android 15 modern support not assured; GPLv3. Changing signature may require uninstall. |
| LSPatch-A15 fork | https://github.com/konglongbt/LSPatch-A15 | Modern fork of nonroot patch loader | Medium; inspect code/issues before testing | Shizuku optional **for installing** patched APK, not a magic unrestricted hook. Fork claims Android 15 compatibility, unverified on POCO. |
| libadrenotools | https://github.com/bylaws/libadrenotools | Per-app custom Adreno Vulkan driver loading, incl Turnip | High for a cooperating app | BSD-2-Clause. Not installed globally; README clearly says *each app must use library itself*. |
| AdrenoToolsDrivers | https://github.com/K11MCH1/AdrenoToolsDrivers | Distributes drivers used in emulators (Vita3K, Dolphin, Winlator etc.) | Medium for permitted in-app driver testing | Drivers may be ABI/GPU-specific. Files from third-party releases require provenance/integrity scanning. No system-wide driver swap. |
| Frida Gadget | https://frida.re/docs/android/ | Repackage app to load Gadget, or use debuggable target with suitable access | Medium for owned test app tracing | OWASP notes re-signing requires uninstall if certificate changes. Not a solution to arbitrary unmodified commercial games. |
| VirtualXposed | https://github.com/android-hacker/VirtualXposed | VirtualApp container & Xposed inside it | Low for Android 15 | Original docs say Android 5–10; latest repository release shown 2022. Native games / Google services compatibility doubtful. |
| BlackBox Reborn | https://github.com/fdavids77/BlackBoxReborn | Virtualization in host APK with containerized game process | Experimental | Fork README claims Android 14–16 support; no POCO device proof. Do not treat README claims as verified. |
| ZygiskNext | https://github.com/LSPosed/ZygiskNext | Root module provides Zygisk on KernelSU etc | Reference only | Requires actual root infrastructure; cannot be used by Shizuku-only. |
| COPG | https://github.com/AlirezaParsi/COPG | Rooted Zygisk-based Build/CPU/GPU property hooks | Research only | GPU spoof advertised as PRO feature; **Root+Zygisk required**. |
| Render-Switcher | https://github.com/EXLOUD/Render-Switcher | Per-app HWUI via Zygisk plus global property | Research only | Root/Zygisk needed for per-process private property-page changes; clarifies why Shizuku global setprop ≠ per-process spoof. |

## Primary docs / code references
- Official Android GLES layers: https://developer.android.com/ndk/guides/rootless-debug-gles
- Official Android Vulkan validation layers: https://developer.android.com/ndk/guides/graphics/validation-layer
- ANGLE individual GLES app: https://github.com/google/angle/blob/main/doc/DevSetupAndroid.md
- Android Game Mode interventions: https://developer.android.com/games/optimize/adpf/gamemode/gamemode-interventions
- Android FPS throttling intervention: https://developer.android.com/games/optimize/adpf/gamemode/fps-throttling
- OWASP nonroot APK patch/repackage: https://mas.owasp.org/MASTG/techniques/android/MASTG-TECH-0026/
- GAMA implementation: https://github.com/palincat/gama/blob/main/app/src/main/java/com/popovicialinc/gama/ShizukuHelper.kt
- KaosGFX main orchestrator: https://github.com/ulolol/Ka0SGFX/blob/main/app/src/main/kotlin/dev/kaos/kaosgfx/domain/PatchOrchestrator.kt
- KaosGFX patcher: https://github.com/ulolol/Ka0SGFX/blob/main/patcher/src/main/kotlin/dev/kaos/kaosgfx/patcher/ApkPatcherEngine.kt
- KaosGFX privileged install: https://github.com/ulolol/Ka0SGFX/blob/main/privileged/src/main/kotlin/dev/kaos/kaosgfx/privileged/PrivilegedService.kt
- CatSmoker detailed source explanation: https://github.com/catsmoker/catsmoker-app/blob/main/docs/SPOOF_DEVICE.md

## Critical distinction for game graphics features
`debug.hwui.renderer=skiavk/opengl` changes Android's **HWUI/Skia path**. A game with its own GLES/Vulkan renderer is not guaranteed to use that path; this does not fabricate GL_RENDERER, Vulkan deviceName, capabilities or unlock all graphics presets.
`angle_gl_driver_selection_{pkgs,values}` selects (if supported) ANGLE for *selected GLES apps*; ANGLE runs GLES atop Vulkan but is not a universal spoof mechanism.
`device_config game_overlay` can apply Game Mode resolution/FPS interventions only where OEM/game support them; not a general FPS cap unlock nor a Vulkan driver injection.
`libadrenotools` can operate in a **cooperating** process, or in a replaced app that has been re-signed with injected bootstrap.

## Safety findings from direct code inspection
**KaosGFX / PrivilegedService.kt:** On `INSTALL_FAILED_UPDATE_INCOMPATIBLE`, default `allowReinstallOnSignatureMismatch=true` triggers `pm uninstall --user 0` and re-install. It attempts to rename/restore only `/sdcard/Android/data/<pkg>` and `/sdcard/Android/obb/<pkg>`, **not the protected `/data/user/0/<pkg>` internal app data**; uninstall may permanently lose saves/login. Therefore **do not use this auto-uninstall path on user's primary apps**.
KaosGFX publicly includes `patcher/src/main/resources/kaosgfx_signing.p12`; never adopt its bundled keystore as MO's signing identity. Generate unique local/CI test keys and secure release signing per build policies.
**GAMA / ShizukuHelper.kt:** Shell `setprop debug.hwui.renderer`, reads `getprop` for verification, may force-stop selected apps and restart UI. Code deliberately excludes `com.miui.home` to avoid Xiaomi/HyperOS launcher loops. MO must **not** force-stop SystemUI or launcher as a first experiment.
**CatSmoker:** Shizuku option for games focuses on game data and settings; native GL hook channel is LSPosed/Vector. Treat naming 'device spoofing via Shizuku' with caution; verify what is actually changed in target process.
**LSPatch/Frida patching:** changes certificate; may break Play Integrity, local encrypted data, OBB paths, updates, anti-tamper and account terms. Use only independently owned experimental APK, with isolated app IDs and rollback.

## Prioritized inference
A. Direct no-root/no-repack: Shizuku capability probe, game-mode read-only, per-app ANGLE detection, allowed GLES/VK layers in owned/debuggable apps.
B. No-root with controlled **owned APK**: KaosGFX design-inspired independent `AppComponentFactory` test; own native loader and self-signed test app, compare driver reporting; **do not copy proprietary iUnlockerGL code**. Shizuku mainly installs/stages app, not injects into stock code.
C. Containers and broad third-party game patching: exploratory only, needs device proof and user-specific permission/license/data-safety review. No automated operation on actual commercial games.
D. Zygisk/KernelSU route permanently outside user's constraints.

All capabilities are hypothesis/provenance-coded until tested on the user's POCO F5 Pro. Public MO repository contains only this independent analysis.
