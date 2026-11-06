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
