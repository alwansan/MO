# MO memory — 2026-11-07

## طلب المالك
إنشاء تطبيق Android مفتوح التطوير في alwansan/MO يعمل بواجهة Shizuku على جهاز POCO F5 Pro بدون روت أو فك بوتلودر؛ فهم iUnlockerGL وKernelSU؛ بناء خطة كاملة وملفات جلسات قابلة للاستعادة. أولوية هذه الجلسة التخطيط فقط لا البناء.

## بيئة الاختبار
- Screenshots: KernelSU Manager v3.3.1 (32680-5), state 'Not installed'; kernel text 5.10.236-android12-9-00003..., model POCO F5 Pro; fingerprint for Android 15 HyperOS OS3.0.5.0.VMNMIXM.
- يجب إعادة تحقق build/API/graphics drivers على الجهاز عند الاختبارات، وعدم اعتبار الصور دليلًا على أن الخدمة Shizuku فعالة الآن.
- المستخدم لديه Shizuku، لا PC؛ يمكن تطوير/بناء لاحقًا عبر GitHub Actions. لا تغييرات للنظام.

## حقائق تقنية حاسمة
- Root module ZIP يحوي Zygisk libraries، installer and boot scripts، `iUnlockerGL.apk`. لا تنفذ السكربتات.
- KernelSU Manager APK لا يجعل النواة rooted. KernelSU الرسمي يحتاج bootloader unlocked للتثبيت التقليدي.
- Shizuku ADB = UID 2000 shell؛ ليس UID 0. يمكنه الوصول لواجهات/إعدادات مسموحة ولا يمكنه فعل Magisk mount/Zygisk/kernel patch.
- ANGLE per-app عبر `settings global` قد يكون تجربة رسمية على Android 15، **حسب وجود ANGLE وسياسات OEM**؛ لا يعادل GPU spoofing؛ واجب حفظ واستعادة قيم الإعدادات بدقة.
- iUnlockerGL LICENSE يمنع reverse engineering/modify/derivatives؛ لا ترحيل شيفرة إلى MO. KernelSU license GPLv3، ويمكن الإحالة إليه دون نسخة.

## قرار معماري
MO مستقل: ZIP inspector + safe APK extraction + package install via documented Android flows + Shizuku diagnostics + opt-in ANGLE and restore + exportable test report. No 'root module installer' labeling.

## ما تحقق
فحص محلي لأسماء وسكربتات وحدة iUnlockerGL وحجمها وبصماتها، وملف KernelSU APK المرفوع، ومراجعة المصادر الرسمية. لم يُثبت شيء على هاتف المستخدم، ولم ينفذ ZIP، ولا اختبارات Android device أو CI.

## التواصل
العربية أولًا. لا تطلب تفاصيل سبق إثباتها إلا عندما يلزم اختبار جديد فعلًا. اقرأ STATUS وSESSION logs معًا؛ حالة التخطيط ليست تصريحًا للبناء.

## New evidence — MO-20261107-CHAT-STATIC-002
- User personally installed extracted iUnlocker APK. It displays revision `null` and ROOT NOT GRANTED; root error confirmed by screenshots; precise backend call not established.
- Additional source snapshots uploaded `iUnlockerGL-main.zip` and `iUnlockerGL-1.1.5-r4.tar.gz`; all 3 contain 98 actual files. Critical APK and Zygisk binary identical.
- Installer script's static NEEDED list includes four paths missing from all archives: sepolicy.rule, system.prop, plugin_flasher.sh, service.sh; potential bug, not tested installation.
- AOSP documents rootless per-app external GLES/Vulkan layers if target debuggable or target 30+ manifest opts into com.android.graphics.injectLayers.enable. Shizuku shell CAN help set debug-layer settings but does NOT override the eligibility gate. We can test with **our own** debuggable sample; don't promise external injection into stock commercial games.
- Do not enable global ANGLE for all apps: AOSP documents boot risk. Use per-app only with rollback.
- License unchanged. User says personal use but alwansan/MO is public, so copying assets there constitutes publishing. Keep analysis results and original independent tools only.
- Local static Python experiment passed 2/2 tests. No phone operations, no app built.

## Additional ecosystem findings (MO-20261107-CHAT-ECOSYSTEM-003)
- GAMA is close Shizuku precedent: `setprop debug.hwui.renderer` with readback; **changes HWUI/Skia**, not game GPU identifiers/direct Vulkan HAL.
- KaosGFX rootless mode is more relevant but it modifies APK+AppComponentFactory/native hooks, re-signs all splits, then uses Shizuku to install and stage drivers. It does **not** hook stock unmodified app. README claims Adreno 750 test; not proven on POCO 15.
- KaosGFX code auto-uninstalls on signature mismatch and only backs up /sdcard/Android/data and Android/obb, thus can destroy /data/user/0 app state. MO forbids auto-uninstall and primary game patching.
- CatSmoker Shizuku edits game files/Game Mode; native GLES/GL spoof there uses rooted LSPosed channel.
- LSPatch rootless APK repatching, original archived 2023; fork A15 exists but no guaranteed compatibility. Libadrenotools works only in target that uses it.
- VirtualXposed old, BlackBoxReborn fork experimental; neither is proof of rootless Zygisk into arbitrary protected installed game.
- Android Game Mode interventions are official if OEM supports; can adjust game FPS/downscale only, not create GPU features.
- Three uploaded analysis archives were inspected statically and are not in public MO; REA v6.8.0 MIT (npm), JADX headless Java JAR, Node 24.22.2 Linux x64 (not phone arm64).
- Reference matrix `research/ROOTLESS_ECOSYSTEM_COMPARISON_AR.md`; test plan `research/POC_TEST_MATRIX_AR.md`; tool hashes `research/UPLOADED_TOOLING_INVENTORY.md`.

## New user goal refined MO-20261107-CHAT-GAME-PROFILES-004
- Target is **unlock hidden graphics/FPS options by profile/CPU/GPU identity**, not fundamentally replace GPU drivers or just improve frame time.
- Two distinct viable approaches: per-game config overrides (Shizuku may access allowed Android/data), vs true in-process spoof (normal closed game blocked without root or repack/consent).
- Direct Game-Unlocker source (rushiranpise) only changes Java Build via Xposed; code maps PUBG com.tencent.ig -> OnePlus 8 Pro. ULTRA-EXTREME-UNLOCKER only ro.product.model variants. Proprince11 and princezakirx rootless GFX tools modify UE4 .sav/.ini files; not GL/Vulkan strings. yadavnikhil03 mixed Xposed/Zygisk marketing needs runtime verification.
- **Hardware correction:** POCO F5 Pro = Snapdragon **8+ Gen 1** (Xiaomi official), 120Hz panel; Adreno 730 from published specs. This corrects any earlier shorthand '8 Gen 1'.
- PUBG Mobile v4.4 (2026) chipset-based first-batch 120 fps Snapdragon 8 Gen 3+ plus evaluated exceptions, final availability by game; 120Hz does not force 120fps. MH Now has official 60fps hunting option and rooted devices unsupported.
- Official PUBG ban policy: unauthorized external app/game file modifications may permanently ban account; no changes to live games in tests.
- New complete files: `research/GAME_PROFILE_SPOOFING_AR.md`, `research/POCO_GAME_COMPATIBILITY_AR.md`, latest session MO-20261107-CHAT-GAME-PROFILES-004.
