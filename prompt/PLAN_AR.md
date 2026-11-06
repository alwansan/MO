# خطة MO الهندسية الكاملة — الإصدار الأول

**الوضع: التخطيط فقط، 2026-11-07.** الهدف تطبيق أصلي مفيد لمستخدم غير عامل روت، لا وعد بتركيب مود Magisk عبر Shizuku.

## 0) تعريف المشكلة وقواعد النجاح
- الطلب الأصلي: تنزيل/فحص/تثبيت GLTool ZIP تلقائيًا وتنفيذ spoof للـGPU عبر Shizuku بدون root/unlock.
- النتيجة الفنية: **هذا المستوى من التزييف العام غير متاح عبر ADB-shell وحده.** لا تحاول التحايل بإعادة تسمية الملفات أو fake-root أو تعطيل SELinux أو ثغرات.
- الهدف القابل للتحقيق: أداة تستكشف ZIP وتحدد مكوناته؛ تستخرج APK وتثبته بإذن المستخدم؛ تقرأ معلومات الجهاز؛ تفحص صلاحيات Shizuku؛ تسمح بتحسينات/اختبارات رسومية مدعومة رسميًا ومحدودة، مع استعادة آمنة.
- شروط قبول: شفاف بخصوص الدعم، لا يسبب bootloop، لا يغير bootloader ولا partitions، كل تغيير قابل للرجوع، لا تشغّل code داخل ZIP.

## 1) مراحل التنفيذ
### P0 — Planning (هذا الإنجاز)
1. مراجعة مصادر أصلية، حقوق النشر، ملفات النسخة المرفوعة، Android 15 وShizuku API.
2. رسم مصفوفة ما يمكن عمله بلا root، وإجراءات الرجوع.
3. وضع ملفات README/AGENTS/prompt/docs وجلسة استعادة؛ إغلاق ادعاء تثبيت وحدات root.
4. لا build ولا APK ولا GitHub workflow.
**Gate:** وثائق مكتملة + ادعاءات مدعومة.

### P1 — Android shell مستقل
1. Gradle Android/Kotlin + Compose/Material 3، minSdk مناسب بعد اختبار CI (مبدئيًا API 28)، targetSdk محدث ومختبر.
2. تصميم UI عربي/إنجليزي: Home, Device, Shizuku, ZIP Inspector, APKs, Graphics Tests, Logs, Settings, About.
3. اعتماد مبدأ least privilege؛ no root APIs; non-root operation even if Shizuku unavailable.
4. اختبارات unit ثم lint/build على GitHub Actions. استعمال Gradle wrapper pinned وdependency verification.
**Gate:** app installs on locked Android 15; UI runs no special permissions.

### P2 — Shizuku adapter, read-only
1. Use official Shizuku SDK and binder/service, with standard user permission prompt.
2. Query Shizuku status, version, UID, connectivity. Require `getUid()==2000` for actual non-root path; show if rooted (do not enable it).
3. Safe command/API allowlist; no arbitrary shell exposed; begin with harmless `id`, `getprop`, graphics diagnostics. Don't request broad dangerous permissions.
4. Shizuku stopped after reboot -> actionable reconnect UI; no false auto-start guarantee.
**Gate:** proof of effective UID and restricted-operation failures logged.

### P3 — ZIP and APK inspector
1. SAF document picker; in-app read-only ZIP parsing with zip-slip protection, decompression limits, anti-bomb measures, hashes and signature checks when possible.
2. Parse `module.prop` and detect `zygisk/*.so`, `customize.sh`, `post-fs-data.sh`, `iUnlockerGL.apk`; mark requires-root.
3. Extract **APK as data only** to app-private cache or user-selected folder, not scripts/binaries.
4. Allow normal Android Package Installer with user consent. Optional Shizuku `pm install` only when verified permitted; do not claim silent install works on all OEMs.
5. 'Compatible/incompatible' report: installer status vs true module capability explicitly separate.
**Gate:** original ZIP analyzed without running scripts; APK extraction hash equals source; app installs with informed user consent, module remains unsupported.

### P4 — Supported graphics path
1. Read supported GLES/Vulkan specs using standard public APIs on own process; identify device driver and capabilities without spoof.
2. On Android 15+ test system ANGLE support; verify actual `settings get global angle_gl_driver_selection_pkgs` and `_values` and graphics behavior.
3. Per-package opt-in for supported OpenGL ES apps only; capture *all* preexisting global settings as exact values; show preview and explicit confirmation.
4. Apply via Shizuku ADB only if permitted and check effective result; else disable feature with honest error.
5. Rollback precisely to previous values even if user interrupted; do not clobber other package rules. Add single app locking/atomicity where practical.
6. No GL/Vulkan fake caps, no injection into other apps, no pretending unavailable HDR is real.
**Gate:** independent real-device A/B test, restoring original settings verified.

### P5 — Safety/CI/release after P1–P4
1. GitHub Actions reproducible debug APK, tests, checksum, signed release only under explicit decision and secured repo secrets.
2. Device test report from POCO F5 Pro: screenshots and redacted logs, baseline before changes, no regressions, reboot behavior.
3. Threat model (ZIP bombs, unsafe installers, Shizuku privilege abuse, privacy), legal review, docs and rollback tests.
4. Publish measured capabilities and any unsupported features; no 'full rootless Zygisk' marketing.

## 2) Dependency and alternative decisions
- **No full vendoring of iUnlockerGL**: restrictive license; link to original, keep checksums only. For derivative integration request written permission.
- KernelSU GPLv3: research/reference only unless future actual need; not required for rootless product; avoid copying kernel binaries.
- Prefer normal Android APIs; Shizuku only for specific accessible capabilities. Study ANGLE and OEM alternatives rather than fake global kernel spoof.
- Runtime operation should be offline by default. Updating definitions from network requires verification and explicit opt-in.

## 3) Milestone estimates and failures
Prioritize functional correctness over invented dates. Unknowns: HyperOS permission quirks, ANGLE availability, game compatibility, Android install consent behavior, legality for third-party code reuse. A failure means feature disabled with a clear diagnostic and recoverable settings, not improvising unsafe hacks.

## 4) Evidence required per feature
Source URL + date/version, before/after device logs, adb identity, screenshots and APK checksum, rollback test, attribution/license status. Unsupported kernel features remain permanently 'not available' without a change to user's no-root constraints.

## ملحق P0.2 — تجربة الحقن الرسمية الممكنة (MO-20261107-CHAT-STATIC-002)
تحليل المستودع الجديد كشف طريق Android GLES/Vulkan debug layers المسموح رسميًا للتطبيقات `debuggable` أو تلك التي تفعل `com.android.graphics.injectLayers.enable` (target API 30+)؛ هذا **ليس حقنًا عالميًا**. خطة التجربة اللاحقة:
1. بناء تطبيق اختبار GLES أصلي يعلن `debuggable` في Debug فقط؛ تسجيل نتائج glGetString قبل/بعد.
2. بناء layer أصلية no-op (لا تغيّر القيم) تُثبت أنها محمّلة؛ بدون أي مكونات مشتقة من iUnlockerGL.
3. Shizuku يقرأ UID ويطبّق فقط allowed developer settings لتطبيق الاختبار بعد حفظ القيم؛ اختبار التحميل الفعلي + rollback.
4. فقط عند النجاح يمكن اختبار تغيير قيمة مرجعة في التطبيق المملوك للمشروع؛ لا تنسب دعم ميزات حقيقية زائفة.
5. اختبر Vulkan منفصلًا بشروطه، وANGLE كاختيار driver وليس hook عام. لا تُفعّل angle_gl_driver_all_angle؛ خطر bootloop مذكور رسميًا.
6. سجل نتائج الألعاب التي **تتيح طبقات خارجية** فقط. الألعاب المغلقة non-debuggable التي لا تتيحها: حظر مسار الحقن الخارجي بوضوح.
الدليل: `docs/ROOTLESS_GRAPHICS_LAYERS_AR.md` و`docs/IUNLOCKER_STATIC_ARCHITECTURE_AR.md`.
الاختبار الفعلي الآن: بايثون read-only للمصادر، 2/2 اختبارات ناجحة؛ لم يُجرّب حقن حي على الجهاز ولم يُبنَ APK.

## ملحق 2026-11-07 — مقارنة مشاريع الطرف الثالث (MO-20261107-CHAT-ECOSYSTEM-003)
المراجع الأساسية الجديدة: `research/ROOTLESS_ECOSYSTEM_COMPARISON_AR.md` و`research/POC_TEST_MATRIX_AR.md`.

**تحديث الخيارات حسب قدرة التنفيذ الحقيقي:**
- GAMA مفيد لبنية Shizuku والأوامر المقيدة والتراجع؛ يغير `debug.hwui.renderer` لواجهة النظام Skia وليس معرف GPU داخل لعبة Vulkan.
- KaosGFX يثبت إمكانية *تصميم* حقن مدمج في APK جديد بتعديل `AppComponentFactory` وإضافة native hooks وتعريفات Adreno؛ Shizuku هنا مساعد في تثبيت APK المعدّل. لم يتحقق نجاحه على الجهاز الحالي. **لا يستخدم على الألعاب الأصلية**؛ نسخ التوقيع المختلف قد يضيع بياناتها، وكود upstream قد يزيلها تلقائيا.
- libadrenotools منصة أصيلة للتطبيقات المتعاونة؛ نختبرها في تطبيق نملكه بعد التحقق من معماريّة GPU والـdriver ونتيجة Vulkan الحقيقية.
- LSPatch/Frida Gadget نموذج لتعديل APK مملوك/مصرح به، وليس حقن أي تطبيق موجود بدون تعديل أو روت.
- CatSmoker يوضح اختلاف تحرير إعدادات الألعاب عبر Shizuku عن hooks داخل العملية عبر LSPosed.
- Android Game Mode Intervention API + ANGLE per-app + GLES/VK layers مسارات رسمية قابلة للقياس، وفق دعم OEM وrollback.
- الاستبعاد: Zygisk/KernelSU وGlobal GPU spoof للعمليات غير المتعاونة؛ container للألعاب التجارية يبقى بحثًا ضعيف الثقة.
تُدرج تجربة PoC جديدة بعد GLES layers: `AppComponentFactory` داخل تطبيق disposable تجريبي خاص بـMO، بدون إعادة استخدام مصدر iUnlocker ولا كسر توقيع برنامج موجود.
