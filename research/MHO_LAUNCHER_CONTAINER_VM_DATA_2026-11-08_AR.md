# MO — دراسة مسارات Launcher / App Container / Android VM / Android-data لهدف GPU identity spoof
التاريخ: 2026-11-08 (Asia/Baghdad)
المصدر: بحث ويب رسمي ومشاريع GitHub ومراجعة **للقراءة فقط** لأرشيف المستخدم الخاص `com.tencent.mhadv.zip`.
السياق: هدف MO هو أن **تقرأ اللعبة أو تطبيق مستقل فعلًا** هوية GPU أعلى من الحقيقي (مثال `Adreno 840` بدل `Adreno 730`)، على أمل فتح خيارات Graphics/FPS الخاصة به. إثبات MOCK داخلي في MO أو 120 Choreographer callbacks/s لا يحقق الهدف.

## نتيجة مختصرة
**لا يوجد حل تلقائي مثبت لجعل النسخة الأصلية المثبتة من Monster Hunter Outlanders تقرأ GPU مزيفًا على Android15 stock مع Shizuku UID2000 فقط.**
- Launcher عادي لا يغيّر هوية العملية المستهدفة؛ نظام Android يعزل عمليات التطبيقات.
- أخف معمارية تحمل قابلية نظرية لتغيير معلومات Android/GL داخل تطبيق آخر **من دون Root للهاتف** هي **user-space App Virtualization Container** لا Full Android VM. لكنها تعني تشغيل نسخة مستضافة/مُحَوّاة من التطبيق داخل عمليات الحاوية؛ نجاح نسخة Outlanders، خدمات GMS/Play Integrity و native64/Unreal وخصوصية الحساب **غير مُثبت**، وقد يخالف الشروط.
- Android VM حقيقي (VMOS+ مثلاً) يوفر Android guest مستقلًا لكنه ليس «بلا كلفة» ولا يرث هوية Google Play المُصدّقة أو وصول GPU الحقيقي بلا متطلبات. **Microdroid/AVF** أخف لكنه يفتقر إلى SystemServer وZygote وGUI وGraphics/HALs، وليس بيئة لتشغيل لعبة كاملة.
- تغيير ملفات `Android/data` لا يغير تلقائيًا قيم `glGetString` أو `VkPhysicalDeviceProperties` أو `Build.MODEL`; الأرشيف المرفوع لا يحتوي ملف DeviceProfiles.ini/GameUserSettings.ini واضحًا يمكن اختبار وجوده داخله.
- أقل خطوة تغيّر النظام وتختبر آلية فعلية هي **الفحص read-only لـANGLE P13** الجاري في MO v0.11.0 ثم (فقط بموافقة منفصلة) تجربة قابلة للتراجع في **تطبيق مستقل نملكه**.
- طلب دعم device whitelist من المطور مسار أصلي بديل ذو أساس: رد Level Infinite على Google Play بتاريخ 2026-11-06 قال إن دعم الأجهزة والتحسينات يتوسع؛ هذا تصريح عام وليس وعدًا بفتح 120FPS لهذا الهاتف.

## حقائق معروفة عن الهاتف واللعبة
- هاتف المالك: Xiaomi 23013PC75G / POCO F5 Pro / SM8475 / Qualcomm Adreno(TM)730 / Android15 API35 / شاشة 120Hz (بناء على تقارير MO التي شاركها).
- اللعبة: `com.tencent.mhadv` إصدار 1.0.9.0.3308842 كما ظهر في آخر MO probe؛ targetSdk36، `debuggable=false` و`external GLES layer opt-in=false`.
- Android GLES external layers على نظام إنتاجي تتطلب Debuggable target أو meta-data opt-in أو root/userdebug. Shizuku عبر ADB Shell UID2000 ليس root؛ تشغيل `Intent` من launcher لا يغير عملية اللعبة.
- Unreal Engine **في العموم** يستخدم `SRC_DeviceModel`, `SRC_DeviceMake`, `SRC_GPUFamily`, `SRC_GlVersion`, `SRC_VulkanAvailable`, `SRC_VulkanVersion` ضمن Device Profile Matching Rules. هذه قواعد **المحرك الرسمية، لا دليل مثبت على قواعد Outlanders الخاصة**.
المراجع:
https://developer.android.com/guide/components/fundamentals
https://developer.android.com/ndk/guides/rootless-debug-gles
https://dev.epicgames.com/documentation/unreal-engine/customizing-device-profiles-and-scalability-in-unreal-engine-projects-for-android

## فحص الأرشيف الذي رفعه المالك — المصدر الخاص وليس مصادر الإنترنت
- ملف ZIP `/mnt/data/com.tencent.mhadv.zip` تم التحقق منه محليًا للقراءة فقط، حجمه المضغوط ~101MiB؛ `ZIP.testzip()` أعطى no damaged members.
- **87 entries = 53 files + 34 directories**؛ مجموع أحجام الملفات غير المضغوطة `241130830` bytes.
- SHA-256 `7fbda67c3366a5639f54790860cdeab479a82b5619ed827f1919828dc0756911`. ليس ملف APK كاملاً ولا تفريغًا لكل `/data/user/0/`.
- التصنيف: **9 `MHMobile` Unreal logs** (في `files/UnrealGame/MHMobile/MHMobile/Saved/Logs/`)، **11 GCloud SDK logs**، **17 resource-hotfix files** ضمن `MHHotRes/` (`*.pak` و`*.ucas` و`*.utoc` و`Android_HotResDesc_*.json` وMount text)، **1 cache** `RHICache/ProgramBinaryCache/GLSL_ES3_1_ANDROID_.../PrecacheBinaries` بحجم **55,635,081 bytes**، و15 ملفات أخرى.
- اسم `GLSL_ES3_1_ANDROID` **قرينة قوية** لاستخدام/تخزين برامج GLES 3.1 على الجهاز؛ **ليس إثباتًا** أن التطبيق لا يستخدم Vulkan بأي سياق أو أن GL_RENDERER هو معيار القفل الوحيد.
- **لا يوجد بين الـ53 ملفًا ملف `*.ini`, `*.xml`, `*.properties`, `*.db` أو `*.sqlite`**، وبالتالي لا يوجد دليل من هذا الأرشيف على GameUserSettings.ini أو DeviceProfiles.ini نصية في `Android/data`.
- عدة ملفات تنتهي `.json` من نوع HotResDesc **لا تُحلّل كـJSON نصي**؛ أول بايتاتها ثنائية غير قياسية. لا يمكن الاستنتاج أنها «ملفات إعدادات نصية يمكن تعديلها» من اسم الامتداد وحده.
- ملفات `MHMobile*.log` تبدأ غالبًا BOM UTF8 ثم أسطر أحرف Base64-like، ومثلها في عدة chunks، **وليست سجلات Unreal نصية مقروءة بشكل مباشر**. هذا يوحي بترميز أو تغليف وربما حماية، لكنه لا يكفي لإثبات خوارزمية تشفير معينة أو محتوى GPU الداخلي.
- `LaunchMountPaks.txt` يحتوي رقم إصدار مثل `1.0.9.0` ومسارات Pak وحقل سداسي 32 رمزًا شبيه ببصمة/معرّف؛ تعديل أي PAK قد يؤدي إلى عدم قبول البيانات أو إعادة تنزيلها. **لا ندّعي دون دليل أن هذا الحقل بالضرورة توقيع أمنـي**. بعض الملفات المهمة في المسار الخاص `/data/user/0/com.tencent.mhadv/files/PufferRes` مذكورة في Mount list لكن **غير موجودة في ZIP**.
- مخاطر الخصوصية: GCloud/Unreal logs والبيانات قد تتضمن مُعرّفات جلسات/حسابات؛ **لا تُنشر المحتويات الخام في GitHub**. هذه المذكرة لا تتضمن أي token أو حساب أو IP أو محتوى سري.
- لم نفك PAK ولا نشغّل executables ولا نحقن مكتبة ولا نعدّل ملفًا.

## تحليل كل مسار

### A) Launcher عادي (يُشغّل الـAPK الأصلي المثبّت)
- الميزة: خفيف للغاية ويمكنه توفير UI profiles, launch shortcut, documentation, user-authorized global options when supported.
- القيد الجوهري: Intent لا يغيّر Process UID، Java Build، native `getprop`, GLES/Vulkan API المحملة في عملية `com.tencent.mhadv`.
- لا يصح تصوير launcher بحد ذاته كـGPU-spoof. يصبح فعالًا فقط إذا كان معه **نظام خارجي مدعوم** يغير ما تقرؤه اللعبة، مثل system ANGLE per-app إن كان مدعومًا. على الإنتاج stock بلا root هذا غير مثبت.
- **تقييم:** واجهة تشغيل مساعدة فقط، لا تتجاوز القفل بمفردها.

### B) Lightweight app container (VirtualApp / BlackBox family)
- معماريًا أقرب للهدف: تشغيل تطبيق «مستضاف» ضمن عملية حاوية تتحكم ببعض واجهات PackageManager وBuild/UID/path APIs؛ native/GLES/Vulkan interception أصعب ويتطلب برهانًا منفصلًا.
- **BlackBox Reborn** https://github.com/fdavids77/BlackBoxReborn يعلن دعم Android14/15/16، و`COMPAT.md` يشرح إصلاح Android API34+ وART offsets API35+ وGMS passthrough. تم فحص README وCOMPAT وLICENSE عبر GitHub connector. **هذه مزاعم المشروع ولم تختبر على POCO / Outlanders**؛ لا نجزم بلعب اللعبة أو Google Login. **تحذير حقوق:** README يقول GPL-3.0 بينما ملف LICENSE الموجود فعلاً Apache-2.0؛ ينبغي التحقق من حقوق الشيفرة والتبعيات upstream قبل أي استيراد أو إعادة توزيع.
- **VirtualApp_16** https://github.com/f0restw0w/VirtualApp_16 يذكر Android14 وarm64، ويقول صراحة إنه غير مستقر وXposed معطل على Android14 بسبب ART؛ إذن Android15 غير مثبت.
- **VirtualApp الأصلي** https://github.com/asLody/VirtualApp (README_eng.md) يعلن توقف كود GitHub عن التحديث منذ ديسمبر 2017؛ تحديثات Android17 وGMS تخص **نسخة تجارية ليست بالضرورة متوفرة في الكود العام**. لا نخلط مزاعم التجاري بقدرات الـOSS.
- **VirtualXposed** https://github.com/android-hacker/VirtualXposed يدعم Android5–10 وفق README الأصلي فقط؛ ليس حلًا حديثًا موثوقًا للعبة ARM64 على Android15.
- **القرار:** إن أردنا بناء إثبات مستخدم بلا روت، نقيّم حاوية مفتوحة المصدر **بتطبيق اختبار مستقل نملكه أولاً**، ونتأكد من Android15/arm64/native EGL/Vulkan، تحميل تطبيق أندرويد تقليدي بلا patch، وسياسة GMS. لا ننقل حساب اللعبة الرئيسي أو ملفات أصلية أو نرسل sign-in إلى مشاريع غير موثقة.

### C) Full Android VM (VMOS+ أو ما شابهه)
- **VMOS+** https://vmos.app/en/ يعلن رسميًا نظام Android15 داخل تطبيق غير root، إمكان تثبيت ألعاب وتفعيل Google services plugin داخل الضيف وMagisk plugin في VM، ومزامنة الشاشة/120Hz. هذه **مطالبات البائع** وليست إثباتًا مستقلًا لأداء Monster Hunter Outlanders أو GPU hardware passthrough أو Play Integrity compatibility؛ جوجل بلاي في VM ليس تلقائيًا نسخة حساب Google على الهاتف.
- VM كامل له RAM وstorage وإقلاع وAndroid services وgraphics translation / passthrough costs؛ وصفه «بلا أي موارد» غير واقعي، خصوصًا مع لعبة كبيرة.
- حساب Google المثبت في النظام الأساسي لا يمكن نسخه/استعارة رموزه تلقائيًا بشكل موثوق ومشروع من داخل بيئة مستقلة؛ Google Play Services داخل VM يجب أن يكون متوافقًا ومهيأً بشكل مستقل، لا ضمان نجاح اللعبة أو سلامة الحساب.
- Google Play Integrity الرسمي https://developer.android.com/google/play/integrity/overview يستطيع التمييز بين بيئة مؤهلة وأجهزة غير موثوقة ومحاكيات؛ لا يعني أنه مطبّق بالضرورة في Outlanders (معلومة غير مثبتة).
- Android AVF/Microdroid خفيف لكنه **ليس full Android graphics game VM**: Google AOSP يقول لا SystemServer/Zygote/graphics/UI/HALs، وAVF API نظامية مقيّدة. https://source.android.com/docs/core/virtualization/microdroid و https://android.googlesource.com/platform/packages/modules/Virtualization/+/HEAD/libs/framework-virtualization/README.md
- **القرار:** خيار احتياطي لإثبات مفهوم على تطبيق مجاني مملوك، وليس طريقًا مضمونًا وسريعًا لـOutlanders 120FPS/Graphics مع GMS.

### D) تحرير ملفات `Android/data/com.tencent.mhadv`
- لا علاقة مباشرة بين تغيير اسم ملف/Cache GPU و`glGetString(GL_RENDERER)` في تعريف Qualcomm أو `Build.MODEL` أو `vkGetPhysicalDeviceProperties`.
- في Unreal العادي تُضبط defaults من ملفات Engine/Project Config وAndroid DeviceProfiles وقد تتضمن ملفات مدمجة cooked PAK، لكن لا يجوز افتراض أن نسخة لعبة تجارية توافق تحميل `DeviceProfiles.ini` جديدة من Android/data دون وجود دليل صريح من قواعدها.
- المرفوع يثبت HotRes/GL shader cache/logs، وليس `GameUserSettings.ini` أو `AndroidDeviceProfiles.ini` قابل للقراءة. محاولة تحرير PAK/UTOC/UCAS أو Cache وتغيير hash بدون فهم آلية التحديث قد يعطل اللعبة ويعرض الحساب للخطر.
- Android11+ scoped storage يقيّد وصول التطبيقات العادية إلى `Android/data` الخاص بالتطبيقات الأخرى. https://developer.android.com/about/versions/11/privacy/storage
- **القرار:** اكتفاء بالقراءة القانونية/المسموحة لسجلات الإعدادات التي يصدّرها المستخدم؛ لا إدخال ملفات bypass أو تفعيل خيارات مخفية في اللعبة من دون موافقة صاحب التطبيق وشروطه.

### E) Built-in system ANGLE (أقل مسار مخاطرة على الأصل المثبت)
- إن كان النظام يتيح per-app ANGLE، وتدعم النسخة installed `debug.angle.gl_renderer`, يمكن نظريًا إعطاء **تطبيق مستقل فعلي** سلسلة GL_RENDERER بديلة من ANGLE، وليس mock UI. لكن هذا يتعلق فقط بـGLES: لا Vulkan native ولا Java Build بدون آليات مستقلة.
- في MO الحالي **v0.11.0-alpha.11** يوجد فحص P13 read-only جاهز، موثق في `docs/P13_ANGLE_READONLY_DEVICE_TEST_AR.md`. هذا الفحص أهم خطوة تالية قبل أي بيئة افتراضية أو تغيير غير قابل للتراجع. لم يصل تقرير هاتف P13 في هذه الجلسة.
- يحظر تفعيل `angle_gl_driver_all_angle` إجمالاً، فقد حذرت AOSP من مشاكل boot. وأي تغيير لاحق يتطلب موافقة المستخدم وrollback وتجربة تطبيق مستقل نملكه وليس Outlanders مباشرة.
- **القرار:** تنفيذ الفحص الحالي (بدون أي كتابة) أولًا. لا ادعاء نجاح تزييف حتى يقرأ تطبيق مستقل value مختلفة بعد الاختبار.

## الشروط الرسمية والسلامة/الحساب
- اتفاقية استخدام اللعبة الرسمي https://monsterhunteroutlanders.com/useragreement/en/index.html ، القسم 7، تتناول برامج غير مصرح بها، المحاكاة/mods والعبث بالعميل أو تدابير الحماية. ويذكر وجود آليات مضادة للغش وعواقب قد تشمل إنهاء وصول الحساب. لا يعني ذلك أن كل launcher بريء ممنوع؛ لكنه يجعل اختبار الحاويات وحقن المعلومات على الحساب الرئيسي **مخاطرة حقيقية**.
- Google Play Integrity https://developer.android.com/google/play/integrity/overview يفحص حقيقة التطبيق والجهاز والمخاطر البيئية حسب تكوين مطور التطبيق؛ وجود GMS أو اسم جهاز مختلف لا يضمن شهادة الجهاز.
- منصات Game Mode FPS intervention الرسمية **تستطيع الحد من FPS فقط وليس رفع لعبة أصلًا مقفلة على 60 إلى 120**: https://developer.android.com/games/optimize/adpf/gamemode/fps-throttling
- اللي يقرأه التطبيق قد يأتي من whitelist client/server أو UE device profile أو Vulkan capability؛ **لا نعرف المصدر الحقيقي لقفل Outlanders من أرشيف البيانات الجزئي**.
- Level Infinite رد في مراجعة Google Play بتاريخ 2026-11-06 بأن دعم وتوافق الأجهزة يتوسع. لا وعد لموديل POCO بعينه أو FPS محدد. https://play.google.com/store/apps/details?id=com.tencent.mhadv

## خطة قرار مقترحة لـMO — بدون عمليات على اللعبة الأصلية
1. **الآن:** اجمع تقرير `P13 ANGLE CAPABILITY READ ONLY` من MO v0.11.0-alpha.11 الموجود في GitHub Releases، دون أي تغيير إعدادات أو حقن، وشاركه بعد مراجعة الخصوصية.
2. **بالتوازي:** إن قرر المالك تجربة container، راجع **BlackBoxReborn** كمرجع معماري فقط (لا تستورد source الآن بسبب اختلاف الترخيص README/LICENSE وتوافق POCO غير مثبت). أنشئ مختبرًا منفصلًا يمتلك تطبيقين تجريبيين: app-host وsample GLES/Vulkan reader، مع إثبات أن التطبيق المستضاف نفسه يرى بديلًا من API native/Java وأدلة أنّه لا يعتمد على MO mock.
3. **أبواب الرفض:** إن لم تعمل 64-bit native Android15، إذا لم يمكن إثبات GLES حقيقي داخل guest/cooperating app، إن تشوه GMS أو ازدادت المخاطر على الحساب، يتوقف المسار دون لمس اللعبة.
4. **تجربة Full VM:** فقط إذا صار تحقيق A/B بالحاوية غير كافٍ ويرغب المالك بمعزل كامل، بعد تقييم استهلاك RAM/storage وGoogle services وعدم استخدام حساب لعبة رئيسي.
5. **لا أوصي** بتحرير HotRes PAK أو Cache أو إعادة توقيع APK اللعبة أو تحميل ملفات معدلة أو ادعاء بيانات GPU جديدة بلا evidence. والأكثر عملية على النسخة الأصلية مع الحفاظ على الحساب هو طلب إضافة POCO F5 Pro إلى profiles المؤهلة رسميًا.

## Acceptance gates
- `GL_RENDERER` يتغير في process **غير MO** (أو مستضاف مستقل) من خلال API الفعلية ويتراجع بعد إيقاف الاختبار، مع سجل before/after/rollback.
- لا يُفترض تغيير Vulkan وJava Model ما لم تقرأهما العملية وتظهر أدلة مستقلة.
- فتح Graphics/FPS داخل **اللعبة الأصلية** يحتاج دعم اللعبة/موافقتها أو آلية مدعومة، وليس مجرد واجهة MOCK؛ هذا **غير مثبت**.
- لا تُنفذ أي تجربة على Outlanders ولا Google Play تسجيل دخول داخل VM/Container غير مدقّق من دون قرار واعٍ بشأن المخاطر والشروط.

## تحديث أثناء البحث: إصلاح رسمي محتمل يجعل المحاولات الخطرة أقل أولوية
- مقال Notebookcheck المنشور **2026-11-07**، نقلاً عن إشعار تحديث اللعبة بتاريخ **2026-11-06**، يشير إلى إصلاح مشكلة عدم القدرة على ضبط **Graphics Quality** على بعض الأجهزة، بالإضافة إلى خلل بصري في **High Frame Rate Mode**. المصدر الثانوي: https://www.notebookcheck.net/Monster-Hunter-Outlanders-receives-new-update.1444972.0.html ؛ ورابط إشعار اللعبة الرسمي المشار إليه: https://monsterhunteroutlanders.com/en/news-update-notice1105.html . الموقع الرسمي يعرض العنوان دون محتوى الخبر في عارض النص لدينا، لذلك نُسنِد تفاصيل الإصلاح للمصدر الثانوي لا إلى نص رسمي غير مقروء.
- **اختبار غير خطر موصى به قبل أي حاوية/VM:** بعد تحديث اللعبة بطريقة رسمية، أعد تشغيلها وافتح قائمة Graphics/High Frame Rate، ثم وثّق بالصور بالضبط الخيارات المتاحة والمقفلة. لا تنفذ اختيارًا غير مدعوم أو تعدّل ملفات أو تعريفات. هذا قد يبيّن أن القفل الذي يواجهه المالك شمله الإصلاح، لكنه **غير مضمون** للجهاز SM8475 ولا يعني فتح 120FPS.
