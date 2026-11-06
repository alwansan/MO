# أمر الاستعادة — انسخ هذا النص إلى محادثة جديدة

اسم المشروع: **MO**. ابق في الدردشة الحالية واستخدم الأدوات المتاحة مباشرة. مستودع المشروع: https://github.com/alwansan/MO ، الفرع الافتراضي main. المستخدم يريد بناء تطبيق Android مستقل يعتمد على Shizuku ويعمل **بلا روت وبلا فتح بوتلودر**. الحالة الحالية: **Planning only**.

نفّذ بالتسلسل:
1. افتح المستودع الحي ولا تعتمد على ذاكرة الدردشة وحدها. اقرأ `AGENTS.md` ثم `prompt/README.md` و`prompt/MEMORY.md` و`prompt/STATUS.md` و`prompt/PLAN_AR.md` و`prompt/RESEARCH_AR.md` و`prompt/ARCHITECTURE.md` و`prompt/DECISIONS.md` و`prompt/SOURCE_INVENTORY.md` و`prompt/DEVICE_TESTS.md` و`docs/SHIZUKU_CAPABILITY_MATRIX.md` و`docs/LEGAL_AND_SECURITY.md`.
2. افحص سجلات `prompt/sessions/` الحديثة، commit SHA الحالي، والوضع الحالي للمصادر والتراخيص. حافظ على كل ما ثبت سابقًا ولا تستبدله بتخمين.
3. افهم ملفات المستخدم الأصلية: iUnlocker-GLTool-for-android-v1.1.5-r4.zip وKernelSU_v3.3.1_32680-release.apk؛ بصماتهما في SOURCE_INVENTORY. قد لا يتوفر الملفان في الدردشة الجديدة؛ **لا تفترض وجودهما ولا تطلبهما إلا إذا احتجت فعليًا إلى إعادة تحليل ثنائية**.
4. الحقائق: ZIP عبارة عن Magisk/KernelSU module مع zygisk .so وAPK منفصل. تطبيق KernelSU Manager وحده ليس Root. Shizuku عبر ADB UID 2000 لا يملك صلاحية kernel/root، وبالتالي لا يستطيع تشغيل Zygisk أو حقن مكتبة في ألعاب الآخرين. تثبيت APK الواجهة ليس تثبيت وحدة ROOT.
5. احترم ترخيص iUnlockerGL الذي يحظر إعادة الهندسة والتعديل وإنشاء المشتقات. لا تستورد مصدره المحمي كجزء من التطبيق. اشترط ترخيصًا صريحًا قبل أي استخدام مشتق. لا تنسخ KernelSU لمجرد أنه GPLv3.
6. تابع فقط المرحلة المصرّح بها. إذا بقيت PLANNING، استكمل البحث والخطة والوثائق؛ لا تنشئ APK/Workflow بناء تلقائيًا. إذا تغيرت المرحلة بتوجيه المستخدم، نفّذ خطة محدودة واختبرها.
7. بعد كل دورة، اكتب `prompt/sessions/<session_id>.md`، وحدّث STATUS وMEMORY والقرارات وروابط الأدلة مع commit SHA، وقدّم تقريرًا عربيًا: ما ثبت، ما تغير، ما تعذر، والخطوة التالية.

**معايير لا تتغير:** لا روت، لا فتح bootloader، لا flash boot/init_boot، لا insmod، لا تعليمات كسر حماية، لا فرض adb root، لا تشغيل سكربتات ZIP الخارجية، لا ادعاء بأن Shizuku بديل Zygisk. استخدم GitHub Actions للتجميع لاحقًا بعد الانتقال لمرحلة التنفيذ.

## إلزامي بعد جلسة MO-20261107-CHAT-STATIC-002
اقرأ `prompt/sessions/MO-20261107-CHAT-STATIC-002.md` و`docs/ROOTLESS_GRAPHICS_LAYERS_AR.md` و`docs/IUNLOCKER_STATIC_ARCHITECTURE_AR.md` و`research/reports/2026-11-07-static-audit.md`.
المسار الجديد: تجربة طبقات GLES/Vulkan الخارجية على **تطبيق تجريبي debug أو opt-in فقط**؛ لا تقول إن الحقن يعمل في ألعاب الإنتاج المغلقة مع Shizuku. الملفات المرفوعة الأصلية لا تُنسخ إلى MO العام. لا تُفعّل ANGLE عام لكل التطبيقات. التجارب المحلية الحالية فقط: فحص أرشيفات واختبارات بايثون؛ لا اختبار حقيقي لهاتف أو APK.

## آخر توسعة بحثية (MO-20261107-CHAT-ECOSYSTEM-003)
اقرأ أيضًا `prompt/sessions/MO-20261107-CHAT-ECOSYSTEM-003.md`, `research/ROOTLESS_ECOSYSTEM_COMPARISON_AR.md`, `research/POC_TEST_MATRIX_AR.md`, `research/UPLOADED_TOOLING_INVENTORY.md`. لا تخلط GAMA (HWUI switch) مع spoof GL/Vulkan داخل لعبة. KaosGFX يحقق مساره المعلن بدون روت عبر **تعديل APK وإعادة توقيعه**، وليس Shizuku يحقن العملية الأصلية. لا تعيد تثبيت أي لعبة أصلية أو تحذفها عند اختلاف التوقيع؛ قد تضيع بياناتها الداخلية. الثلاثة أدوات المرفوعة موجودة فقط في محادثة اليوم، وقد تحتاج إعادة رفعها مستقبلاً عند التحليل الثنائي. الحالة P0؛ لم يُبنَ APK أو يُختبر على الجهاز.

## جلسة أحدث MO-20261107-CHAT-GAME-PROFILES-004 — هدف الألعاب الفعلي
اقرأ `research/GAME_PROFILE_SPOOFING_AR.md`, `research/POCO_GAME_COMPATIBILITY_AR.md`, `prompt/sessions/MO-20261107-CHAT-GAME-PROFILES-004.md`. المالك يريد تغيير الهوية المرئية للألعاب لفتح إعدادات FPS/Graphics، لا تبديل الدريفير فقط. Game-Unlocker الحقيقي Xposed يغيّر Java Build (لا يثبت GL/Vulkan)، أما GFX عبر Shizuku يبدّل ملفات إعدادات UE4. POCO F5 Pro Snapdragon **8+ Gen1**, 120Hz/Adreno 730. دعم PUBG 120fps وفق سياسة الشريحة وليس اسم جهاز فقط؛ Monster Hunter Now 60fps hunting رسمياً. لا تغيّر لعبة حية أو نظام الهاتف؛ لا root/unlock. الحالة بحثية P0 ولم يُبنَ APK.

## استعادة أحدث جلسة MO-20261107-CHAT-BUY-VS-BUILD-005
اقرأ prompt/sessions/MO-20261107-CHAT-BUY-VS-BUILD-005.md وresearch/READY_APP_DECISION_AR.md وresearch/MHO_OUTLANDERS_SPECIFIC_AR.md وresearch/SHIZUKUPLUS_SPOOF_CODE_AUDIT_AR.md. التصحيح الأهم: Monster Hunter Outlanders com.tencent.mhadv وليس Monster Hunter Now. المستخدم يريد حلًا جاهزًا قبل بناء MO. App Cloner يعيد توقيع clone، VMOS+ يتيح root/Zygisk داخل النظام الافتراضي فقط، وShizukuPlus spoof يبدل ناتج getprop في أوامر الخدمة لا داخل عملية اللعبة. الحالة P0، لا تجارب جهاز ولا بناء APK.

## تنفيذ P1 — MO-20261107-CHAT-IMPLEMENT-006
بدأ المستخدم التنفيذ رسميًا. اقرأ `docs/IMPLEMENTATION_PHASES.md`, `app/src/main/java/com/alwansan/mo/MainActivity.java`, `GraphicsProbe.java`, `PrivilegeGate.java`, `GameInventory.java`, و`.github/workflows/mo-android-debug.yml`. لا تعد الحالة P0 فقط. لا روت ولا VMOS ولا App Cloner. أول محاولة Actions رقم 42305774716 فشلت في إعداد Android SDK بسبب أداة 'tools' قديمة، وعدّل workflow؛ افحص أحدث run وحل الفشل التالي قبل إعلان APK جاهز. التطبيق P1 يشخّص ولا يحقن اللعبة.

## البناء الأول نجح
التشغيل Actions `42305983786` نجح بجميع الخطوات، Artifacts اسم `MO-Graphics-Probe-debug-2` ID `13545813166`. APK SHA256 `13c72cbb626e4ec6238df7e5c9262c5781b1f819487753c568c90befe2a2fba2`. لا تكرر مشكلة workflow رقم 42305774716، كانت SDK tools غير متاحة وقد أصلحناها. المهم: البناء ناجح فقط؛ ننتظر اختبار هاتف POCO وصلاحيات Shizuku. عند الاستعادة اطلب تقرير MO من المستخدم إذا لم يكن مرفقًا، ثم طور P2.

## Latest MO-20261107-CHAT-P2-DEVICE-007 — device feedback / P2 code
Read docs/DEVICE_FEEDBACK_2026-11-07.md and prompt/sessions/MO-20261107-CHAT-P2-DEVICE-007.md. P1 device confirmed Shizuku UID2000 GRANTED API13, Adreno730 GLES3.2 driver V@0615.95, Outlanders com.tencent.mhadv v1.0.9.0.3308842. P2 code includes app/src/main/aidl/com/alwansan/mo/IReadOnlyProbe.aidl, ReadOnlyProbeService.java, ReadOnlyCommandPolicy.java, VulkanFeatures.java. Build status requires checking latest Actions; no proof of game FPS unlock or P2 runtime yet.

## Verify P2 CI and continue
The GitHub Actions P2 run 42307946638 SUCCEEDED. Artifact ID13547166555, name MO-Graphics-Probe-debug-3, app-debug.apk SHA256 7a98dbbb0393256210b76249c79505df34b89e34e98c3677545cd0d6b5e23d5b, 125919 bytes, provided to user as MO-Graphics-Probe-v0.2.0-alpha2.apk. Ask for P2 'فحص Game Mode وANGLE' result when user tests. Keep no root/VM/clone, no game mods.

## Important debug install constraint
v0.1 and v0.2 are signed by **different per-run GitHub Actions debug keys**, cannot upgrade in place. User advised uninstall MO only before alpha2; don't uninstall Shizuku or games. Future stable signing needs private keystore GitHub Secret, not public key source commit. See docs/IMPLEMENTATION_PHASES.md.

## Session MO-20261107-CHAT-P3-VULKAN-008 — P2 verified and native Vulkan build
Read docs/DEVICE_FEEDBACK_ALPHA2_2026-11-07.md, app/src/main/cpp/native_vulkan.cpp and NativeVulkanProbe.java. User P2 report: Shizuku UID2000 works; Outlanders game mode [standard,custom] no interventions, ANGLE selection null, Vulkan declared1.1. New v0.3 source adds real native physical GPU properties in **MO process** only, NDK/CMake, and fixes literal \\n report. Check GitHub Actions results, repair compile problems, then get user device test. Never suggest this by itself unlocks FPS or changes game identity.

## P3 APK built successfully (2026-11-07)
GitHub Actions `42308837332` SUCCESS for source `eef8fb46f8ab49412ab4ab04554128e56b0dad36`; artifact `MO-Graphics-Probe-debug-4`, ID13547731335, APK v0.3.0-alpha3 SHA256 `203b8c47d3da67cca1e6ffe212682824d67a864a556b6cd12d7717b7f14dbe92` 1102410 bytes. Native libmo_vulkan.so ARM64 verified. Owner received APK in chat, **but phone Vulkan runtime test pending**. Ask for MO 'Vulkan Native GPU' report if absent. Do not claim it changes game GPU or unlocks options.

## Latest MO-20261107-CHAT-P4-LAYER-009: Vulkan success, graphics layer PoC source
Read docs/DEVICE_FEEDBACK_ALPHA3_2026-11-07.md, probe/src/main/cpp/mo_gles_layer.cpp, probe/src/main/java/com/alwansan/mo/glesprobe/ProbeActivity.java, prompt/sessions/MO-20261107-CHAT-P4-LAYER-009.md. P3 native Vulkan on POCO passed: Adreno730 API1.1.128 vendor0x5143. P4 separate disposable owned test app contains official GLES Layer intercept and mock tier; cannot affect Outlanders. Check CI. Next build reversible Shizuku developer per-app GPU debug layer activation **only for com.alwansan.mo.glesprobe**, then test in that app; no broad game injection.

## P4 v0.4 (current)
Read docs/OWNED_GLES_LAYER_TEST_AR.md, app/src/main/java/com/alwansan/mo/OwnedLayerSettings.java, MainActivity.java, probe/ source. The version 0.4 MO permits activation/restore via Shizuku of GPU debug layers for ONLY our disposable test app com.alwansan.mo.glesprobe. Four settings backed up in MO app-local data before changes; IMPORTANT restore before deleting/updating MO. Confirm build status and wait for user proof GL_RENDERER changed within test app. Neither game nor protected files are modified.

## P4 complete build, device test pending
As of 2026-11-07 GitHub Actions run 42310714620 PASS. Artifact ID13547164678 MO-Graphics-Probe-debug-8 has two APKs. MO v0.4.0-alpha.4 SHA256 74e50eece4eae020d7a9808f97552cbc5d843bf37142b8818282ab106c71b8f7; owned MO Layer Test v0.1.0 SHA256 180c32ab62a06f8e26686d651ad274cf316eb2828a1e52a73a29bca98670b4d0. Read docs/OWNED_GLES_LAYER_TEST_AR.md and prompt/sessions/MO-20261107-CHAT-P4-LAYER-009.md. NEXT: get user's baseline GL_RENDERER, layer-enabled GL_RENDERER and post-restore GL_RENDERER **inside disposable test app**. Never claim Outlanders was altered, and **restore the four global debug settings from MO before uninstalling MO**.

## Release distribution switch (MO-20261107-CHAT-RELEASE-010)
Owner specifically requests https://github.com/alwansan/MO/releases hosting APK directly, not ZIP. Updated `.github/workflows/mo-android-debug.yml` to test+build two APKs and publish `MO-Graphics-Probe-v<version>.apk` and `MO-Layer-Test-v<version>.apk` in GitHub **prerelease** via gh. Read `docs/RELEASE_DISTRIBUTION_AR.md`; verify latest successful run and actual release assets. Never claim release created before checking. Restore P4 GPU debug settings before uninstalling/updating MO. Debug signing changes across CI runs.

## Release publishing confirmed — 2026-11-07
- GitHub Actions run https://github.com/alwansan/MO/actions/runs/42311901394: **completed success**. Commit `58de082ab63a797087f827633ba4dbd35fb5aa2f`.
- GitHub prerelease https://github.com/alwansan/MO/releases/tag/mo-v0.4.0-alpha.4-run9 created, id 437174044, public (draft=false), two raw APK assets, state=uploaded:
  1. `MO-Graphics-Probe-v0.4.0-alpha.4.apk` (1,118,830 bytes) https://github.com/alwansan/MO/releases/download/mo-v0.4.0-alpha.4-run9/MO-Graphics-Probe-v0.4.0-alpha.4.apk
  2. `MO-Layer-Test-v0.1.0.apk` (17,463 bytes) https://github.com/alwansan/MO/releases/download/mo-v0.4.0-alpha.4-run9/MO-Layer-Test-v0.1.0.apk
- No ZIP Release asset. GitHub automatically provides Source code ZIP/tar links; separate from APK release assets.
- On future source/workflow pushes, building both APKs and publishing a unique experimental release is automatic after CI success. Actions Artifacts ZIP step removed.
- Debug signature remains unstable: **restore P4 debug GPU settings before uninstalling/updating MO**. No actual Outlanders FPS unlock or P4 device hook proven.
