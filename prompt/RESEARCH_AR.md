# البحث الفني والنتائج المثبتة — 2026-11-07

## (A) iUnlockerGL
Source: https://github.com/i-Taylo/iUnlockerGL ; release https://github.com/i-Taylo/iUnlockerGL/releases/tag/v1.1.5-r4 .
- وصف المشروع: وحدة Magisk/KernelSU لتغيير المعلومات التي تراها ألعاب Android عن GPU/CPU/RAM/display، ومنها GLES/Vulkan.
- شروط README الرسمية: API >=27، Magisk >=26.4 مع Zygisk أو KernelSU مع ZygiskNext.
- ZIP المرفوع: 25,969,823 bytes، 123 entries؛ `module.prop`, `customize.sh`, `installer.sh`, `post-fs-data.sh`, `zygisk/arm64-v8a.so`, `iUnlockerGL.apk` (7,346,905 bytes). No scripts executed.
- installer يعتمد على `/data/adb/magisk` أو `/data/adb/ksu`, root module environment, boot hook, tools and root-owned files. ADB UID 2000 cannot replicate these.
- وجود APK داخل ZIP لا يساوي تفعيل وظيفته؛ يمكن تثبيته مستقلًا لغرض الواجهة فقط، وقد يفشل الربط مع خدمة المود.
- ملف LICENSE يجيز إعادة توزيع الأصل غير المعدل، ويحظر reverse engineering, decompiling, modifying, derivative works. لا تنسخ مكوناته داخل MO.
- لا دليل على أن تزييف GL/Vulkan يخلق GPU capabilities حقيقية أو يعوض hardware extensions.

## (B) KernelSU
Sources: https://kernelsu.org/guide/faq and https://kernelsu.org/guide/installation.html ; https://github.com/tiann/KernelSU .
- APK Manager `KernelSU_v3.3.1_32680-release.apk` المرفوع مستقل عن النواة؛ 13,637,206 bytes.
- صور الهاتف تعرض 'Not installed'؛ وفق FAQ يشير لعدم تثبيت KernelSU داخل النواة (وليس لأن تطبيق مديره مفقود).
- FAQ: kernel-based root normally requires unlocked bootloader. LKM mode is still kernel/root capability, not APK sideload with Shizuku.
- KernelSU supports modules, but root module infrastructure/ZygiskNext prerequisites cannot run as ordinary shell. KernelSU is GPL-3.0.

## (C) Shizuku
Sources: https://github.com/RikkaApps/Shizuku and https://shizuku.rikka.app/guide/setup/ and https://github.com/RikkaApps/Shizuku/blob/master/README.md .
- On non-root device Shizuku server launched via wireless debugging, UID 2000, not root UID 0.
- Grants selected ADB-backed binder or shell operations (OEM dependent), not arbitrary system file writes, kernel loading, or Zygisk injection. Cannot bypass SELinux by design.
- Service normally must be restarted after device reboot; no guaranteed permanent unattended daemon.

## (D) Android 15 graphics alternative
Sources: https://developer.android.com/games/develop/vulkan/overview and https://developer.android.com/agi/troubleshooting .
- Developer docs explain ANGLE as optional GLES-over-Vulkan on Android 15+, with experimental per-package ADB global `angle_gl_driver_selection_pkgs` and `angle_gl_driver_selection_values`. Not guaranteed to exist on all vendor builds or work for every game.
- Must store existing values, confirm actual driver usage, and restore exact originals. Test first on harmless app.
- ANGLE does not fake Vulkan device capabilities nor replace Zygisk/GPU spoofing.
- OEM may restrict settings writes; use Shizuku permission + runtime probe and graceful fallback.

## (E) Security and viability assessment
- No credible route for full original iUnlockerGL global spoof via Shizuku on a stock locked device.
- Feasible route: inspection, safe APK extraction/normal install, hardware diagnostics, official supported settings.
- No unknown exploit proposals, kernel image modification, disable-verified-boot/SELinux, or root emulator misrepresentation.
- Claims marked conditional: actual ANGLE behavior on POCO/HyperOS; APK UI usability standalone; Shizuku shell permission quirks. These need device evidence.

## References
- iUnlocker README: https://github.com/i-Taylo/iUnlockerGL/blob/main/README.md
- iUnlocker LICENSE: https://github.com/i-Taylo/iUnlockerGL/blob/main/LICENSE
- KernelSU FAQ: https://kernelsu.org/guide/faq
- KernelSU installation: https://kernelsu.org/guide/installation.html
- Shizuku API privilege differences: https://github.com/RikkaApps/Shizuku/blob/master/README.md
- Shizuku setup: https://shizuku.rikka.app/guide/setup/
- Android ANGLE: https://developer.android.com/games/develop/vulkan/overview
- Android settings rollback examples: https://developer.android.com/agi/troubleshooting

## إضافات مؤكدة MO-20261107-CHAT-STATIC-002
- Existing APK installed by user: UI works; says No root permission, revision null. This is evidence of missing root integration but not exact IPC failure.
- release/module ZIP, upstream GitHub main ZIP and tag tar.gz: 98 files each; APK+Zygisk+installer scripts same. Static installer lists 4 absent filenames; `research/reports/2026-11-07-static-audit.md`.
- New *important* rootless path: official Android GLES external layers and Vulkan validation layers for debuggable/manifest-opt-in apps, not ordinary nondebuggable commercial games.
- AOSP disallows external code injection into third-party nondebuggable processes on production devices. Shizuku shell UID2000 does not remove that policy.
- Docs https://developer.android.com/ndk/guides/rootless-debug-gles ; https://developer.android.com/ndk/guides/graphics/validation-layer ; https://source.android.com/docs/core/graphics/implement-vulkan .
- AOSP ANGLE warns `angle_gl_driver_all_angle=1` can render device unbootable; forbidden in MO. https://github.com/google/angle/blob/main/src/android_system_settings/README.md


## Additional live upstream audit — MO-20261107-CHAT-ZYGISK-COPG-AUDIT-016
- Full comparison: `research/ZYGISK_COPG_NO_ROOT_FEASIBILITY_AR.md`. Public pinned README/source audits of ZygiskNext, ReZygisk, NeoZygisk, COPG, MMRL and iUnlockerGL. NO binaries executed or copied.
- ZygiskNext current README restricts redistribution/derivatives since v4-0.9.2. ReZygisk README vs LICENSE ambiguity (AGPL vs GPL) and COPG public source incompleteness are documented, not resolved by guessing.
- Shizuku is UID2000 and cannot enter non-debuggable commercial-game process through any of these root-bootstrapped frameworks. Official GLES target opt-in remains mandatory. Android Game Mode `--fps` is an intervention/throttle, not changing the GPU identity or opening a game's locked menu.
- Device evidence `docs/DEVICE_FEEDBACK_ALPHA9_2026-11-07.md` confirms v0.9 final post-cleanup four-key readback 0/absent, but the separate PID16719 Adreno renderer screenshot should **not** be claimed as a new process launched after the final cleanup.
