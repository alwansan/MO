## P29 Field Check 043 — 2026-11-08: نطاق P29 لم يُجرَّب بعد

- صور المالك الجديدة تثبت فصل PID414 لتطبيق MO المثبّت مباشرةً عن PID3585 لـRenjana Guest، مؤكدًا بالأمر `ps`، وأن Shizuku يعمل فعليًا بصلاحية `uid=2000(shell)` وSELinux shell وليس Root.
- في PID414 ترجع Native GLES وeglGetProcAddress النص **MO-GLES-LAYER-PROOF (SIMULATED)** المطابق تمامًا لمكتبة طبقة GLES القديمة `mo_gles_layer.cpp`، بينما Java GLES وVulkan حقيقيان Adreno730؛ نطاقها Native ONLY وFPS UNLOCKED محاكاة فقط.
- في Guest PID3585 ترجع GLES Java/NDK/EGL وVulkan Adreno730، وتنجح كل اختبارات التخزين والـcleanup؛ وهو خط أساس أنظف لتجربة ByteHook.
- **كلتا صورتَي P29 تعرضان NOT RUN on this process**، لذا لا تُسجّل نجاحًا/فشلًا لاعتراض ByteHook. علامته المختلفة هي `MO_P29_OWNED_GLES_HOOK_ONLY` وتظهر فقط في تقرير مستقل بعد الضغط على زر P29.
- **التالي:** داخل Renjana P26 الحالية مع MO Layer Test v0.9.0 الحالية، اضغط زر **P29 اختبار اعتراض GLES (OFF/ON/OFF)** مرة واحدة واحتفظ بنتيجة baseline/active/after وCallback OK/hits/status/unhook، مع PID جديد إذا تغير. لا تعِد P25-P27 ولا تغيّر إعدادات النظام أو تطلب إعادة بناء APK. تجربة host لاحقًا تُحلَّل كطبقة GLES قديمة فعالة وليست bare host baseline.
- المصدر: docs/DEVICE_FEEDBACK_P29_GLES_LAYER_SEPARATION_2026-11-08_AR.md والجلسة 043. لا Root أو تعديل للعبة أو FPS حقيقي.

## أحدث مرحلة P29 — 2026-11-08: تجربة Native glGetString داخل MO المملوك

- **تم تنفيذ كود P29 ونشر APK منفصل للتجربة**: MO Layer Test v0.9.0 فقط؛ hook PLT يستهدف glGetString في libMOProbeRenderer.so حصريًا بالضغط على زر P29، مع baseline الحقيقي، mock ثابت مؤقت ضمن Thread الاختبار، قياس callback/hits، وإزالة hook ثم مقارنة original. لا يغير GPU الحقيقي، Vulkan، FPS، اللعبة أو النظام.
- بُني بـByteHook **v1.1.0** (MIT، مع ShadowHook1.1.1) لأنه متوافق مع compileSdk35. فشل build أولي #76 عند AAR metadata في v1.1.2 الذي يطلب compileSdk37؛ عولج بتثبيت إصدار متوافق دون ترقية SDK لكل مشروع MO.
- **CI النهائي #78 SUCCESS** على source ead4f202ea5b9aa957d6edc9a9532004538836c8، و[Release](https://github.com/alwansan/MO/releases/tag/mo-v0.11.0-alpha.11-run78) يضم [MO-Layer-Test-v0.9.0.apk](https://github.com/alwansan/MO/releases/download/mo-v0.11.0-alpha.11-run78/MO-Layer-Test-v0.9.0.apk) بحجم 342024B SHA256 3718601e4e26b31da697a4cdb04f50585fd4405e18af4dc6f1d32c1e73606041.
- **لم يُختبر P29 على الهاتف**. الاختبار القادم لصاحب الجهاز هو تشغيل التطبيق المملوك خارج Renjana أولًا إن أمكن ثم داخل Renjana P26، والضغط على زر P29، وإرسال سطور callback/hits/unhook وbaseline/active/restored مع PID وربطه بـps عند الحاجة. ByteHook نجح في البناء، لكن نجاح الـHook الفعلي غير مثبت.
- **Shizuku** إذا بدأ عبر Wireless Debugging يعمل بصلاحية UID2000 Shell، وليس root وهميًا يعادل UID0، ويمكن استعماله لتشخيص ps/meminfo/logcat قراءة فقط؛ لا يمنح حق حقن مكتبات في تطبيقات الإنتاج المغلقة أو تغيير GPU عالميًا. لا حاجة إليه لتجربة P29 المباشرة. لا Root/Bootloader/Zygisk ولا Settings.GPU Debug/ANGLE ولا Outlanders أو حساباتها.
- القرارات والتفاصيل: docs/P29_OWNED_GLES_BYTEHOOK_AR.md، session MO-20261108-CHAT-P29-OWNED-GLES-BYTEHOOK-042.md. P30 Vulkan لا يبدأ باعتبار GLES إثباتًا له.

## تحديث الخطة P28 — 2026-11-08: Native-first بدل تبديل الحاويات

- **القرار الحالي:** احتفظ بـRenjana P26 كتجربة Guest مملوكة ثبت نجاح التخزين فيها؛ لا تستبدلها بـNEXTVM الآن. تكرر فحص P27 الحقيقي من MO Layer Test v0.8.0: Build.MODEL NE2215 لكن GLES Java/NDK/EGL وVulkan deviceName جميعها Adreno730. رموز glGetString من libGLESv2.so وVulkan من libvulkan.so في فضاء العملية المقاسة، وليس إثباتًا لنجاح intercept.
- فحصنا NEXTVM الموجود أصلًا في بحث P21 مرة أخرى: هو يطبق GOT/PLT لـlibc/path/system_property فقط، ولا توجد native glGetString أو Vulkan hooks في جدول C++، والـGuest launch ما زال موضع Issue مفتوحة. لا GitHub Release مثبتة، ولا سبب لمبادلة حاوية مثبتة بأخرى غير مختبرة. جرّب المالك BlackBoxReborn مسبقًا ولم يشغّل Outlanders.
- **P29:** إثبات Native GLES interception داخل التطبيق التجريبي المملوك فقط، باستخدام مكتبة مثل ByteHook (MIT) مع pinning وترخيص واضح. اختبار OFF/ON/OFF يستدعي glGetString الفعلي ويسجل hook count وscope واختلاف Java/NDK/EGL وrollback؛ لا نعتمد محاكاة واجهة ولا نغير capabilities/extensions أو FPS.
- **P30:** Vulkan native properties/dispatch proof منفصل، يشمل lookup والـphysical device handle الصحيح، على APK مملوك فقط؛ ليس ضمن إثبات P29 تلقائيًا.
- **P31:** إن نجحا فقط، تجربة Renjana-delivered helper داخل Guest المملوك قبل تحميل JNI دون أن يحمّل التطبيق المكتبة بنفسه؛ إثبات PID مستقل، وقراءات Java/NDK/EGL/Vulkan A/B وكلفة الذاكرة والثبات. لا تشغيل لعبة أو نقل حساب.
- **توقف مبكر:** إذا فشل تحميل الهوك أو أغلب مسارات API أو تدهور الثبات/الأداء على Android15 ARM64، اشرح حدود no-root/no-repackaging، ولا تبنِ حاويات بديلة بلا سبب. نجاح تغيير GL_RENDERER نصيًا لا يثبت خيارات Ultra/120FPS؛ P20 RemoteConfig لم يحدد gate، و120Hz شاشة لا يعني 120FPS فعلي.
- **P28 بحث وتوثيق فقط؛ لم يُبن APK ولم تُنفذ تجربة هاتف جديدة.** المرجع: research/P28_NATIVE_FIRST_CONTAINER_STRATEGY_AR.md وprompt/sessions/MO-20261108-CHAT-P28-NEXTVM-STRATEGY-041.md.

# خطة MO الهندسية الكاملة — الإصدار الأول

## آخر تقدّم P13 المُثبت — 2026-11-07
- **نُشرت نسخة MO Graphics Probe `v0.11.0-alpha.11`**، فحص ANGLE قراءة فقط عبر Shizuku UID2000، 14 أمر `pm list/path` و`settings get` و`getprop` ثابت في AIDL. اجتاز البناء GitHub Actions `42332224614` ورفع APK إلى https://github.com/alwansan/MO/releases/tag/mo-v0.11.0-alpha.11-run59 .
- **لم يُثبت حتى الآن** وجود ANGLE صالح لاختيار driver في جهاز POCO، ولا قراءة Adreno840 من داخل تطبيق مستقل، ولا فتح جرافيكس/FPS داخل Outlanders. هذا هو معيار النجاح الحقيقي، والفحص الحالي يجمع الحد الأدنى من الأدلة لتحديد الطريق التالي فقط.
- اختبار المستخدم التالي: Shizuku ADB2000، اضغط `فحص قدرة ANGLE (قراءة فقط)`، وصدّر `Download/log.txt`. لا تفعّل `angle_gl_driver_all_angle` إطلاقًا، ولا تغيّر GPU Debug من أجل الفحص. المرجع `docs/P13_ANGLE_READONLY_DEVICE_TEST_AR.md`.


## الأولوية الحالية المصححة من المستخدم — P13: تزييف هوية GPU الذي تقرؤه التطبيقات الأخرى فعليًا
- **الغاية الأساسية الأصلية:** جعل تطبيق أو لعبة أخرى ترى هوية عتاد أعلى من الحقيقي (مثال نص `Adreno 840` بدل `Adreno (TM) 730`)، لمعرفة هل يفتح ذلك خيارات الرسوميات أو FPS التي تعتمد على ملفات تعريف الأجهزة. نجاح قائمة وهمية داخل MO **ليس** هدف المشروع.
- **معيار الإثبات:** قراءة `glGetString(GL_RENDERER)` **من داخل عملية تطبيق مستقل** قبل/بعد آلية supported/reversible؛ ثم إن أمكن مراقبة قائمة خيارات ذلك التطبيق. لا نسوّي بين OpenGL GLS/Vulkan/Java Build ولا نزعم زيادة قدرات Adreno730 الحقيقية.
- **المسار البحثي الجديد:** ANGLE لديه دعم موثّق لخاصية `debug.angle.gl_renderer` لتغيير نص GL_RENDERER إذا عمل ANGLE driver فعليًا. يدعم اختيار driver لكل تطبيق في الأنظمة المجهزة. **غير مؤكد** توفر/قابلية تشغيله في POCO HyperOS Android15 أو وصول shell UID2000 للخاصية، أو استخدام Outlanders لـGLES بدل Vulkan. اقرأ `research/REAL_GPU_IDENTITY_SPOOF_ANDROID15_ANGLE_AR.md`.
- **تسلسل التنفيذ:** (أ) قراءة-only لوجود system ANGLE وإعدادات/خصائص driver الحالية، دون كتابة؛ (ب) إن كانت ممكنة، تجربة A/B مستقلة على تطبيق GLES نملكه أو متعاون ومصرح، مع نسخ الإعدادات الأصلية والتراجع؛ (ج) لا ننقل اختبارًا على لعبة مغلقة إلا بعد إثبات API حقيقةً وبموافقة صريحة، ولا نستخدم root/bootloader/VM/clone/تعديل ألعاب/مكونات iUnlockerGL.
- **القيود الصريحة:** Android يسمح طبقات GLES/Vulkan خارجية فقط للتطبيقات debuggable أو opted-in (أو ظروف root/userdebug). Outlanders الموثقة غير مؤهلة؛ Shizuku UID2000 لا يحقن عملية مغلقة. إذا لا تتوفر ANGLE system أو كتابة الخاصية، يجب قول **غير متاح ضمن الشروط الحالية** لا تصعيد وهمي.
- **P10/P11/P12 مكتملة ضمن حدود الأدوات المساعدة:** محاكاة محلية 7 أوضاع، callback pacing 1/2/4، logs داخل التطبيق. ليست مساوية لهدف تزييف GPU للغير. لا نستهلك دورة أخرى في توسيع FPS HUD بدل إثبات مسار real identity spoof.
- جلسة `prompt/sessions/MO-20261107-CHAT-P13-REAL-SPOOF-024.md`; مصادر ANGLE الرسمية مرفقة في البحث الجديد.


## الحالة الحالية بعد الانتقال الرسمي للتنفيذ — P10 (2026-11-07)
- **مرحلة P10 منفذة في المصدر ومُجمعة بنجاح**: مختبر هوية الجهاز التجريبي في تطبيق `MO Layer Test v0.4.0` المملوك فقط؛ قياسات Java Build/GLES وNDK GLES وVulkan وHz حقيقية في عملية الاختبار، أما تغيير الهوية واختيارات FPS فهي محاكاة محلية. لا تغيير لهوية الهاتف أو لعبة Outlanders.
- نجاح GitHub Actions `42324225397` ورفع APK خام من https://github.com/alwansan/MO/releases/tag/mo-v0.9.0-alpha.9-run25 . مراجعة `docs/OWNED_GAME_IDENTITY_LAB_AR.md` و`prompt/sessions/MO-20261107-CHAT-P10-IDENTITY-LAB-017.md`.
- **ملاحظة السلامة:** تقرير v0.9 الأخير أكد `enable_gpu_debug_layers=0` وغياب العلامات الثلاث؛ ما زال إثبات PID جديد بعد التنظيف واختبار P10 على الجهاز مطلوبين. لا تُفعّل طبقة ثانية ولا تحذف MO الأساسي؛ الاختبار المحدث APK منفصل وقد يحتاج استبدال تطبيق الاختبار المملوك فقط.
- الخطوات التالية: تقرير PID/GL بعد التنظيف، ثم مقارنة أوضاع المختبر، وبعدها تقييم الفروق بين قيود الألعاب الحقيقية ومجرد محاكاة الهوية. لا root ولا VM ولا patch لعبة ولا أي دليل على 120FPS حقيقي.

## الخطة التاريخية الأصلية أدناه (P0/P1 ولا تعكس المرحلة الحالية)

**الوضع عند كتابة المسودة الأصلية: التخطيط فقط؛ تم تجاوز هذه المرحلة بتصريح المستخدم وبلوغ P10.** الهدف تطبيق أصلي مفيد لمستخدم غير عامل روت، لا وعد بتركيب مود Magisk عبر Shizuku.

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

## ملحق MO-20261107-CHAT-GAME-PROFILES-004: التشخيص حسب هوية الجهاز التي تقرؤها الألعاب
- أول سؤال في كل تجربة: هل الهدف مجرد إظهار خيار FPS مقفل؟ أم تغيّر معدل الإطارات المقاس فعليًا؟ أم رفع جودة الصورة؟ نفصل هذه المؤشرات ولا نفترض تساويها.
- أنشئ أولًا **MO Identity Probe** (في مرحلة التنفيذ) يعرض Java Build، native `__system_property_get`, GLES `glGetString`, Vulkan `vkGetPhysicalDeviceProperties`, CPU and physical display modes. لا تغيّر هوية الهاتف عالميًا.
- أنشئ لعبة اختبار مستقلة بسلم وهمي لخيارات FPS تتدرج حسب Build vs GL vs Vulkan؛ أثبت الفرق بين تغيير `Build.MODEL` داخل برنامج نملكه وتغيير GL_VENDOR/RENDERER، وبين Game Mode/settings. لا تجارب على PUBG الحقيقي.
- شغّل Shizuku فقط لقراءة UID2000 وSettings/Device Config المسموحة؛ قيّم تطبيقات GFX Tool كتحرير ملف config (شرط الوصول/الحفظ وغياب كسر الحماية)، وليس GPU spoof.
- كل لعبة تحتاج تشخيصًا منفصلًا: PUBG سياسة دعم الشريحة في 2026 (Snapdragon 8Gen3+ first batch)، Monster Hunter Now إتاحة 60fps أثناء الصيد والروت غير المدعوم. انظر `research/POCO_GAME_COMPATIBILITY_AR.md`.
- منع أي تعديل تلقائي لألعاب حية/حسابات PUBG، وانتحال عتاد غير مدعوم على لعبة تستخدم anticheat، أو auto-uninstall حفاظًا على بيانات المستخدم. وثّق مخاطر الحساب والنسخ الاحتياطي.

## مرحلة قبل التنفيذ MO-20261107-CHAT-BUY-VS-BUILD-005
لا نبدأ بناء MO قبل مراجعة حلول جاهزة: App Cloner (يعدل ويمضي نسخة APK، لا يعمل على الأصل)، VMOS+ Android15 الافتراضي (يمكنه Magisk/Zygisk/LSPosed داخل الضيف لكنه ليس Shizuku فقط)، ShizukuPlus (قراءة الشيفرة تثبت فقط محاكاة استجابة getprop في عميل الخدمة)، وGFX Tool/PGT+ (إعدادات PUBG لا Outlanders). اللعبة الصحيحة Outlanders com.tencent.mhadv. أولًا تصوير واجهة Graphics الأصلية وتسجيل الخيارات المقفلة في Graphics Quality, High Frame Rate, Field FPS, Free Hunt/Arena FPS مع الإصدار؛ ثم قياسات read-only؛ لا تعديل تطبيق تجاري أو حساب. راجع research/READY_APP_DECISION_AR.md. إن فشل الجاهز يمكن بناء MO اختبار محدود على تطبيق خاص بنا، وليس ادعاء أنه يتجاوز صلاحيات Android.

## التنفيذ الفعلي بعد موافقة المالك (MO-20261107-CHAT-IMPLEMENT-006)
تم فتح P1: تطبيق Android أصلي `com.alwansan.mo` يتصل بـShizuku ويقرأ Build وGLES ويكتشف نسخة Outlanders. `docs/IMPLEMENTATION_PHASES.md` المرجع للتسليم الحالي. CI على GitHub Actions، يتحقق من اختبارات JUnit ويُصدر debug APK. لا توهم بأن هذه النسخة فتحت FPS، ولا تغيّر تطبيقًا أصليًا أو ملفاته. عند نجاح CI، المستخدم يختبر التطبيق في POCO F5 Pro ويرسل تقريره، وبعدها يبدأ P2.
