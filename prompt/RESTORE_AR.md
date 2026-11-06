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
