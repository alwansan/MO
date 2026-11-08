# P25 — Renjana VirtualContext، AntiDetection وملفّات Guest + Native GLES/Vulkan

**التاريخ:** 2026-11-08 | **session:** MO-20261108-CHAT-P25-GUEST-IO-038 | **branch:** main

## الاستعادة
- P24 مثبت: المالك شغّل أداة Rish P23 على الهاتف. Renjana main PID14699 وGuest :p1 PID23140، مجموع PSS اللحظي 189636kB، جميع قراءات مفاتيح GPU Debug فشلت (UNKNOWN). لا قياس FPS أو Vulkan spoof داخل PID23140.
- HEAD عند بدء P25: 5343f4d22707885f5fdbadb621e0373835294bfb. آخر Renjana APK Release: renjana-owned-lab-run2؛ الاختبار الجاري هنا سيكون على MO Layer Test المملوك فقط.

## اكتشاف المصدر: لماذا VirtualCtx warns رغم تشغيل Guest؟
في المصدر المثبت Renjana SHA 14302a57cd66114c6979acf7ca56c97f845a6841:
1. PineHookManager.installGuestHooks: يسجل Guest ثم إذا enableAntiDetection مفعّل يستدعي AntiDetection.initialize(instanceId, dataPath)، ويُثبت hookFileExists (حتى حين تكون anti-detection معطلة، ولا يُثبت أن callback يخفي حينها).
2. AntiDetection.initialize يضيف dataPath و/data/data/com.fesu.renjana و/data/user/0/com.fesu.renjana إلى containerPaths.
3. AntiDetection.shouldHidePath تعيد true لمسار يبدأ بأي containerPath، **وكذلك لمسارات تحتوي على renjana**.
4. CoreHooks.createFileExistsHook يقوم بضبط result=false عندما shouldHidePath=true.
5. VirtualContext.ensureDir يفحص dir.exists ثم dir.mkdirs ثم dir.exists، ويصدر تحذير Could not create virtual directory عند عدم رؤيته للدليل.

**استنتاج المصدر فقط:** مع Anti-Detection ON (كما في صورة المالك P23)، قد يعيد File.exists=false حتى لمسار تخزين guest صالح فعليًا. هذا تفسير مرجح لتحذيرات VirtualCtx الخمسة عشر في P24، **وليس إثباتًا أن نظام الملفات تعطل**. لا تخلط file-existence cloaking مع نجاح الدليل الحقيقي أو العزل الكامل.

روابط المصدر:
- https://github.com/josskixg/renjana/blob/14302a57cd66114c6979acf7ca56c97f845a6841/app/src/main/java/com/fesu/renjana/hooks/AntiDetection.kt
- https://github.com/josskixg/renjana/blob/14302a57cd66114c6979acf7ca56c97f845a6841/app/src/main/java/com/fesu/renjana/hooks/CoreHooks.kt
- https://github.com/josskixg/renjana/blob/14302a57cd66114c6979acf7ca56c97f845a6841/app/src/main/java/com/fesu/renjana/virtual/VirtualContext.kt
- https://github.com/josskixg/renjana/blob/14302a57cd66114c6979acf7ca56c97f845a6841/app/src/main/java/com/fesu/renjana/hooks/PineHookManager.kt

## تجربة P25 المملوكة (APK v0.7.0)
تعديلات جديدة على **probe/** الأصلي MO فقط؛ لا تعديلات على Renjana upstream ولا اللعبة أو النظام:
- ProbeActivity يعرض Java GLES، NDK GLES، eglGetProcAddress، **Native Vulkan deviceName** وPID في قراءة واحدة ضمن نفس العملية. عدم ظهور Vulkan أو خطأه سيعرض واضحًا، لا يختلق الاسم.
- زر اختياري: **فحص تخزين Guest المملوك (ملفات مؤقتة فقط)**، ينشئ ويقرأ ويزيل ملفات صغيرة وفريدة داخل ملفات تطبيق MO Layer Test الخاصة فقط، ولا يختبر ملفات الآخرين ولا يكتب إعدادات Android.
- FilePathCheck يعرض File.exists / isDirectory / canWrite مستقلة عن نجاح كتابة وقراءة Java File streams؛ الاختلاف يساعد على اكتشاف False-negative من hooks.
- OwnedStorageProbe يقيس Activity Context.openFileInput/openFileOutput مقابل Application context وcanonical-path equality **دون إظهار المسارات الخاصة**.
- MOProbeRenderer.so يستخدم libc stat وmkstemp/write/read/unlink على getFilesDir الناتج من Context، مع رفض أي مسار غير /data/، وفحص التنظيف. لا يتم إلا بطلب المستخدم؛ لا يدعي تشغيلاً في عملية لعبة أو عزل native syscalls.
- JUnit: FilePathCheckTest يتحقق من نجاح roundtrip مع حماية ملف مجاور، ورفض المسار المفقود وغير الدليل.

**تحذير:** كتابة ملفات اختبار مؤقتة يعني أن زر التخزين ليس قراءة فقط؛ هذه كتابة محدودة في مساحة التطبيق التجريبي الذي نملكه، مع حذف ملفات الاختبار. لا ينبغي اعتبارها اختبارًا كاملًا لسلامة التخزين أو أداء ألعاب ثقيلة.

## تفسير النتائج المطلوبة
- File.exists=false وisDirectory=true وnative libc stat=YES وNative I/O=PASS: **يدعم بقوة** فرضية إخفاء الوجود على مستوى Java. لا يثبت أن كل hooks سليمة.
- Native stat=NO أو Native I/O=FAIL مع Java File FAIL: مشكلة مسار/صلاحية/ABI تحتاج دراسة مستقلة؛ لا تستنتج السبب من errno وحده.
- Java File PASS وContext streams FAIL: مسار Context wrapper أو hooks يحتاج مراجعة؛ ليس ملف GPU.
- جميع اختبارات التخزين PASS: لا يلغي تحذيرات P24؛ ربما التحذيرات تشير إلى false negatives أو إلى سياقات/أزمنة أخرى.
- Java/NDK/EGL/Vulkan متطابقة مع Adreno730: لا يوجد spoof أصلي في الاختبار. إذا اختلف أحدها، نحتاج PID جديد والملف المصدر للتحقق من Scope وعدم التباس طبقة GLES الخارجية.

## تعليمات جهاز بعد نجاح CI وتوفر APK
1. لا تغيّر GPU Debug أو ANGLE ولا تثبّت Main MO جديدًا، فالتوقيع قد يختلف وبياناته قد تتضمن سجل rollback. فقط APK اختبار MO Layer Test v0.7.0 **بعد نجاح نشره**.
2. إن كانت بيانات تطبيق الاختبار القديم مهمة، صدّر سجله قبل تغيير نسخة APK. توقيع GitHub Actions debug قد يختلف مما يستلزم إزالة APK الاختبار المملوك فقط بموافقة المستخدم؛ لا تمس Renjana/اللعبة.
3. افتح MO Layer Test v0.7 من Renjana؛ تأكد من PID داخل Guest com.fesu.renjana:pN، والتقط شاشة GLES/Vulkan. اضغط زر P25 مرة واحدة واحفظ نص النتيجة أو صدّر سجل MO المملوك.
4. إن أردنا عزل أثر Anti-Detection بعد النتيجة، نقارن في **instance تجريبية مملوكة** وخيار Anti-Detection OFF، مع تثبيت بقية الخيارات؛ هذا اختبار لاحق وليس نجاحًا مثبتًا الآن.

## أدلة البناء
- GitHub workflow mo-android-debug.yml يعمل بعد probe/** ويجري JUnit وGradle/NDK ويصدر MO Layer Test APK. لا تعتبر نجاح source commit أو وصول run إلى pending نجاح APK.
- لم يُختبر P25 على هاتف المالك عند كتابة التقرير؛ لا يوجد دليل FPS أو GPU spoof داخل Renjana، ولا Outlanders.
