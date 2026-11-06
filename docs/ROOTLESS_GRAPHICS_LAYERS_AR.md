# Android rootless graphics research — GLES / Vulkan / ANGLE
2026-11-07 | origin=chat | MO-20261107-CHAT-STATIC-002

## الدليل الجديد والحاسم
نظام Android **يدعم رسميًا طبقات GLES وVulkan دون root لبعض التطبيقات**؛ وليس لتطبيقات الإنتاج المغلقة عامةً.

وفق وثائق Android الرسمية، تُقبل external layers إذا:
- التطبيق `android:debuggable=true`، **أو**
- التطبيق targetSdk >= 30 ويتضمن في Manifest إذن opt-in:
  `<meta-data android:name="com.android.graphics.injectLayers.enable" android:value="true" />`،
- أو صورة نظام userdebug مع صلاحيات مناسبة (مرفوضة هنا).

ليست هذه الخاصية تصريحًا عامًا لـ Shizuku. يملك المطوّر تحديد debuggability وmanifest عند بناء تطبيقه؛ Shizuku UID 2000 يستطيع تغيير إعدادات developer مسموحة **فقط بعد** أن يسمح Android بتحميل المكتبة إلى العملية. وضع الإعدادات لا يزيل شرط أهلية التطبيق.

## التجربة المسموحة الأولى: تطبيق GLES تجريبي نملكه
1. نبني تطبيق `MO.GraphicsProbe` debug مستقلًا يعرض ناتج `glGetString(GL_VENDOR)`, `GL_RENDERER`, `GL_VERSION` ويطبع علامة طبقة اختبار.
2. نبني مكتبة GLES layer أصلية من الصفر، بدون نسخ iUnlocker binaries، تصدر `AndroidGLESLayer_Initialize` و`AndroidGLESLayer_GetProcAddress`. المرحلة الأولى ترصد الاستدعاء وتعيد القيمة الحقيقية بلا تعديل.
3. نحزمها في APK layer موقّع تجريبيًا. نتحقق من manifest/ABI والتحميل.
4. نستخدم Shizuku **بالوضع shell** لإعداد `Settings.Global` الخاصة بتطبيق probe فقط بعد حفظ كل قيمها السابقة؛ تشمل: `enable_gpu_debug_layers`, `gpu_debug_app`, `gpu_debug_layers_gles`, `gpu_debug_layer_app`.
5. نعيد تشغيل تطبيق probe ونفحص دليل loader/logcat/قيمة الطبقة فعلًا؛ ثم نعيد كل قيمة سابقة حرفيًا، ونؤكد الاستعادة. لا تغييرات عامة لكل التطبيقات.
6. بعد إثبات read-only interception فقط، نفحص نظريًا إمكانية تعديل نتيجة `glGetString` **داخل التطبيق الذي نملكه فقط**؛ لا نفترض أن spoofing يدعم إمكانات GPU غير موجودة.
7. Vulkan طبقة منفصلة بواجهاتها، لا تعتمد على GLES layer؛ تبدأ مع probe مستقل، `gpu_debug_layers` وتوثيق دعم API.

**لا تحاول حقن طبقات في ألعاب مغلقة non-debuggable لم توافق صراحة.** توثيق AOSP يقول إنه لا يُسمح بتحميل external code into nondebuggable process على إنتاج بدون root؛ وطبقات Vulkan المُحقنة بغير موافقة التطبيق غير مدعومة.

## ANGLE — اختيار برنامج التشغيل وليس حقن مكتبة خاصة
- `angle_gl_driver_selection_pkgs` + `angle_gl_driver_selection_values` تسمح لبعض الأجهزة باختيار ANGLE vs native per-app.
- تحقق من وجود ANGLE في الروم ومن رفض الشركات/القيود، ولا تخلط بين مجرد كتابة Settings Global وبين تحميل driver بالفعل.
- تحفظ السلسلتين كاملتين كما هما؛ في حال تعدد الحزم تدمج بترتيب 1:1؛ عند التعارض الخارجي لا تكتب فوق تغيير طرف آخر.
- **ممنوع** خيار `angle_gl_driver_all_angle=1`: تذكر الوثائق الرسمية أنه قد يمنع الجهاز من الإقلاع. لا تعرضه حتى على أنه تجربة.

## اختبار الأهلية للتطبيق المستهدف
| حالة التطبيق | GLES/Vulkan external layer | ANGLE selection |
|---|---|---|
| MO probe debuggable | نعم مبدئيًا، يحتاج اختبار على الجهاز | ربما، إذا ANGLE موجود |
| third party explicitly injectLayers opt-in (target 30+) | نعم بحسب النظام وAPI، يحتاج اختبار | ربما |
| stock commercial release non-debuggable, no opt-in | **لا** بوساطة Shizuku العادي | ربما، لكنه ليس spoofing |
| rooted/userdebug device | خارج شروط المالك | خارج النطاق |

## المسارات البديلة ذات المخاطرة
- التطبيقات التي تملك مصدرها: تعديل المصدر مباشرة أو إدخال plugin عبر API مصرح به.
- Container virtualization/clone: دراسة مستقلة لاحقًا؛ Android 15/native games قد لا تتوافق، وتطبيق container لا يحقن original installed process؛ لا يوجد ضمان.
- Repacking third-party game يُغيّر التوقيع وقد ينتهك الشروط ويكسر Integrity/anti-cheat؛ لا تنفذه تلقائيًا.
- Shizuku service لا يستطيع إرسال قيمة `glGetString()` مباشرة إلى عملية لعبة غير متعاونة؛ لا يوجد Binder عام يفرض رد GL على كل العمليات.

## المصادر الرسمية
1. https://developer.android.com/ndk/guides/rootless-debug-gles
2. https://developer.android.com/ndk/guides/graphics/validation-layer
3. https://source.android.com/docs/core/graphics/implement-vulkan
4. https://github.com/google/angle/blob/main/src/android_system_settings/README.md
5. https://github.com/RikkaApps/Shizuku-API/blob/master/README.md
