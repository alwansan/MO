# Decision gates for next phase — POCO F5 Pro / Android 15

No device experiment or APK build performed in this research session. Tests require separate explicitly authorized phase and user-installed test APK.

## Experiment ranking
| Rank | Experiment | Input | Expected measurable proof | Rollback | Stop criteria |
|---|---|---|---|---|---|
| P1 | Baseline Shizuku diagnostics | Our own Kotlin minimal app | `uid=2000`, Shizuku permission, graphical capabilities and build data | Nothing changed | Shizuku unavailable/denied |
| P2 | GLES layer no-op (official) | Own debuggable GLES app + own layer APK | Loader logs, glGetString before/after, visible layer heartbeat | Restore exact Settings.Global keys, uninstall owned test APK | No layer in process; device incompatibility |
| P3 | Vulkan layer no-op (official) | Own debuggable Vulkan app | VK loader invocation and layer evidence | Restore exact settings | No Vulkan layers support |
| P4 | AppComponentFactory bootstrapped native instrumentation | Own APK (second independent test identity), our own test SO | Log entry from injected bootstrap; original app baseline vs patched; signature verification | Uninstall only sandbox test package | Any data loss / signature mismatch / crash |
| P5 | libadrenotools/Turnip *inside owned app* | Compatible validated driver package with explicit license; own app calls loader | Actual `VK_DRIVER_ID`, device name/driver, a real rendered frame | Stop own test app, clear test cache | Unsupported Adreno/ABI, stability problems |
| P6 | ANGLE per app | A single owned GLES test APK | Driver selection actually changes / ANGLE logs | Restore both prior global settings and verify | ANGLE not installed or ignored |
| P7 | Game Mode intervention | Owned game app opting out appropriately per Android docs | Effective FPS/resolution intervention via logs and frame timing | Restore original `device_config game_overlay` value | OEM unsupported |
| Research | Virtualization (BlackBox) | disposable test APK only | Runtime starts and native EGL/Vulkan works | Uninstall container, never primary accounts | Incompatible Android 15 or protected app |

## Inputs not allowed for initial tests
- User's primary games, payment apps, bank apps, accounts, or protected files.
- Any auto-`pm uninstall` of existing package; signature mismatch is a **stop** not a fallback.
- Bulk GPU driver replacement, force global ANGLE, system property mass-rewrite, `/data/adb` changes, SELinux changes.
- Scripts, binaries or drivers from unknown downloads run without provenance, checksums and inspection.
- Root, bootloader unlock, kernel/boot patch, enabling experimental vulnerabilities.

## Evidence required for pass
1. Baseline and effective UID; Android build/API/ABI; Shizuku status.
2. Package name, test APK signing certificate hash, verified manifest debuggable/opt-in.
3. Before/after actual OpenGL/Vulkan **runtime** values (not configuration-only self-report).
4. System log of layer/driver invoked, no crashes, test executed without root.
5. Precise rollback and state re-verification. Detect if external changes occurred and refuse to overwrite.
6. Separate performance data after correctness: stable frame timing, thermals, memory, FPS; not only one synthetic number.
7. Source and license of any external binary. No publication of proprietary iUnlockerGL artifacts.

## Specific claims needing independent verification
- KaosGFX README asserts Adreno 750 nonroot Turnip result (Android 16). No claim this will work on POCO F5 Pro (Android 15; presumed Adreno family must be verified).
- LSPatch-A15 fork's Android 15 support; no endorsement before build/test.
- GAMA's `setprop` permission/actual outcome on HyperOS; may fail or impact launcher/SystemUI.
- ANGLE present on particular HyperOS firmware; setting keys alone insufficient.
- Game-specific anti-tamper, Play Integrity, privacy and data loss risks.

Do not start P1–P7 by automated GitHub Actions without phase authorization.
