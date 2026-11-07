# MO P13 — فحص ANGLE النظامي قبل أي محاولة لتزييف GPU حقيقي
**التاريخ:** 2026-11-07 | session `MO-20261107-CHAT-P13-ANGLE-READONLY-025`

## الهدف الأساسي كما صححه المالك
الهدف النهائي ليس «FPS داخل MO» ولا فتح قائمة وهمية داخله. يريد المالك أن يُبلِّغ تطبيق مستقل عن هوية GPU أعلى (مثل `Adreno 840` بدل `Adreno (TM) 730`) **من داخل API الرسوميات التي يستخدمها ذلك التطبيق**، وبالتالي معرفة إن كان سيغير خيارات Graphics/FPS في تلك اللعبة.

هذا التقرير يتناول **المرحلة الأولى فقط: هل تسمح نسخة Android 15 المثبتة بالوصول إلى ANGLE ومعلوماته؟** لا يحتوي APK P13 على زر تزييف هوية أو اختيار driver جديد أو تعديل خاصية النظام، ولا توجد تجربة حقيقية على Outlanders حتى الآن.

## ما أضفناه إلى MO الرئيسي
- **قسم واجهة جديد:** `05B / P13 ANGLE CAPABILITY — READ ONLY` وزر `فحص قدرة ANGLE (قراءة فقط)`.
- **AIDL / Shizuku `getAngleSnapshot()`:** يمتثل لـ `Os.getuid() == 2000`، ويستخدم `ProcessBuilder` فقط مع لائحة أوامر ثابتة لـ `pm list/path` و`settings get global` و`getprop`. ليس تنفيذ shell حرًّا من إدخال المستخدم.
- **`AngleReadOnlyCommandPolicy`:** 14 أمرًا محددًا، منها محاولات استعلام عن حزم `com.android.angle` و`org.chromium.angle` و`com.google.android.angle`، وأسماء حزم ANGLE المرئية عبر `pm list packages angle`.
- قراءة إعدادات اختيار الـdriver: `angle_gl_driver_selection_pkgs`، `angle_gl_driver_selection_values`، `angle_debug_package`، `show_angle_in_use_dialog_box`، وأيضًا المفتاح **عالي الخطورة** `angle_gl_driver_all_angle` *قراءة فقط*.
- قراءة خصائص `debug.angle.gl_renderer`، `debug.angle.gl_vendor`، `debug.angle.gl_version`، بالإضافة إلى `ro.debuggable` و`ro.build.type`.
- تسجيل `ANGLE_CAPABILITY_REQUEST` و`ANGLE_CAPABILITY_READ_ONLY` في تشخيص التطبيق مع التوقيت، وتضمين لوحة ANGLE في زر نسخ تقرير MO الكامل وكذلك `Download/log.txt`.
- اختبار JUnit لـ `AngleReadOnlyCommandPolicy`: تحقق أن جميع الأوامر المرسلة للمنفذ read-only، ورفض `setprop` و`settings put/delete` وأسماء الحزم غير المصرح بها وإدخال shell عشوائي.
- **MO الرئيسي:** versionCode 11 / `v0.11.0-alpha.11`. **MO Layer Test** يبقى المصدر والإصدار `v0.6.0` من P12 ولا نعيد بناء ميزة تزييف وهمية على أنها هدف المشروع.
- نسخة AIDL service زادت إلى `version(5)` لتلافي إعادة استخدام binder قديم. إذا كانت Shizuku غير متصلة أو UID غير 2000، يظهر رفض واضح بلا تغييرات.

## ضوابط التفسير: ما تعنيه النتائج فعليًا
| ما قد يظهر على POCO | التفسير الصحيح |
|---|---|
| `pm list packages angle` يعرض حزمة | توجد حزمة مرئية؛ **ليس دليلًا** على تفعيل ANGLE للعبة |
| `pm path com.android.angle` يعطي مسارًا | مسار حزمة، وليس برهانًا على تحميل مكتباتها لعملية اللعبة |
| الحزم المعروفة كلها غير موجودة | لا يجزم بعدم وجود ANGLE مضمّن أو موجود باسم OEM مختلف |
| `angle_gl_driver_selection_pkgs/values = null` | لا يوجد اختيار محدد في هذين المفتاحين الآن، **لا يثبت** عدم دعم ANGLE |
| `debug.angle.gl_renderer` فارغ | لا يوجد override نصي معلن عبر هذه الخاصية الآن؛ لا يثبت إمكانية الكتابة فيها |
| `angle_gl_driver_all_angle=1` | علامة **خطر**؛ سجلها ولا تحاول إصلاحها بلا مراجعة snapshot وموافقة المستخدم |
| `ro.debuggable=0` أو build.type=user | إنتاج عادي؛ يضع قيودًا على التطوير والحقن الخارجي |
| ظهور قيمة `Adreno 840` في `getprop` | **ليس دليلًا** أن `glGetString(GL_RENDERER)` في التطبيق المستهدف تغير |
| ANGLE package + property متاحتان | *مؤشرات* لمرحلة اختبار A/B لاحقة؛ يجب أن يقرأ التطبيق المستقل `glGetString` من عمليته فعلاً |

## تحذير خاص من التفعيل العام
وثائق AOSP ANGLE تحذر أن `angle_gl_driver_all_angle=1` قد يجعل بعض الأجهزة غير قابلة للإقلاع. **MO P13 لا ينفذ أي `settings put` أو `setprop` مطلقًا ولا يعرض أي زر لتبديل التفعيل الشامل.** لا تستخدم هذا الإعداد على الجهاز بغرض اختبار الهوية.

## ما سيتطلبه الاختبار الحقيقي لاحقًا (لم ينفّذ)
بعد إرسال تقرير P13، وإذا أثبت وجود ANGLE نظامي مدعوم **وموافقة المستخدم الصريحة** على تجربة اختيار driver لتطبيق نملكه فقط:
1. تثبيت **تطبيق مستقل ثانٍ مخوّل** بقراءة `glGetString(GL_RENDERER)` الحقيقية، وليس الاعتماد على TextView محاكاة داخل MO. نسجل baseline لكل من GLES / Vulkan / Build.
2. فحص صلاحية ومرئية ANGLE في ذلك التطبيق ثم snapshot دقيق قبل تغيير أي مفتاح. رفض التنفيذ إذا لم تكن الشروط أو خطة rollback مضمونة.
3. تجربة اختيار ANGLE للتطبيق المملوك فقط، والتحقق من driver المستخدم داخل عمليته، وتجربة override للخاصية `debug.angle.gl_renderer` فقط إذا سمح النظام بها وكان بالإمكان الرجوع بدقة. نثبت بعد إعادة التشغيل تطابق قراءة `glGetString` ثم نلغي التجربة ونتحقق من عودة القيم الأصلية.
4. **لا تجربة أولية على `com.tencent.mhadv`**، ولا تغيير Vulkan/Build غير المثبتين. نجاح تغيير نص GLES وحده لا يعني أن اللعبة ستفتح خيارات جديدة.

## اختبار الهاتف المطلوب في هذه المرحلة
1. إن كان **MO Graphics Probe v0.11.0-alpha.11** متاحًا بعد نجاح CI، ثبّته بحسب قواعد التوقيع المتغيرة. **لا تحذف التطبيق القديم إذا كان لديك rollback backup أو GPU Debug غير معاد**؛ بعد P12 كان آخر readback من جهازك `enable=0` وثلاث علامات مفقودة عند الساعة 05:26:04 لكن لم يُعاد فحصها في أحدث سجل اختبار منفصل.
2. افتح Shizuku (ADB mode UID 2000) وامنح صلاحية MO، ثم في MO اضغط **فحص قدرة ANGLE (قراءة فقط)**، وانتظر نتيجة الـ14 أمرًا.
3. اضغط زر إخراج السجل إلى `Download/log.txt` وارفع الملف أو انسخ التقرير مباشرة. يشمل `ANGLE_CAPABILITY_READ_ONLY` والقيم الأخرى. لا يلزم لمس خيارات GPU Debug أو تثبيت حزمة ANGLE.
4. سنقرر من التقرير إن كان هناك مسار قابل للبحث أم أن ANGLE غير متاح في هذا النظام؛ **حتى التقرير الناجح لا يثبت حصول تطبيق آخر على Adreno840**.

## المصدر الأساسي
- ANGLE code/docs: https://android.googlesource.com/platform/external/angle/+/HEAD/doc/DebuggingTips.md
- ANGLE per-app settings: https://android.googlesource.com/platform/packages/modules/ANGLE/+/51ed50098f5f67d70778e6d91da2963647654d64/Readme.md
- ANGLE Setup: https://github.com/google/angle/blob/main/doc/DevSetupAndroid.md
- Rootless GLES loader policy: https://developer.android.com/ndk/guides/rootless-debug-gles
- `research/REAL_GPU_IDENTITY_SPOOF_ANDROID15_ANGLE_AR.md`

## نتيجة بناء P13 المنشور — تحقق نهائي
- Source commit: `5666cb45c6f8601acf14aeaca02a8b626979ff63`.
- GitHub Actions [42332224614](https://github.com/alwansan/MO/actions/runs/42332224614), **completed SUCCESS**: `:app:testDebugUnitTest`, `:probe:testDebugUnitTest`, بناء Android/NDK للتطبيقين، تجهيز ملفَي APK ونشرهما في Releases.
- Public prerelease: [mo-v0.11.0-alpha.11-run59](https://github.com/alwansan/MO/releases/tag/mo-v0.11.0-alpha.11-run59)، ملفا APK بحالة uploaded:
  - [MO-Graphics-Probe-v0.11.0-alpha.11.apk](https://github.com/alwansan/MO/releases/download/mo-v0.11.0-alpha.11-run59/MO-Graphics-Probe-v0.11.0-alpha.11.apk) — **1,167,998 bytes**, SHA-256 `4372a3b077632925df8c51161843f110c53777d9009254c6c51c2ffd6fe5925f`.
  - [MO-Layer-Test-v0.6.0.apk](https://github.com/alwansan/MO/releases/download/mo-v0.11.0-alpha.11-run59/MO-Layer-Test-v0.6.0.apk) — **208,508 bytes**, SHA-256 `8540a452ee162a7e13a94a9e28d6ba2e50883239a3c44565ee3c37ca040fd607`. لا توجد تغييرات وظيفية لتطبيق المختبر مقارنة بـv0.6؛ أُعيد توقيعه فقط ببناء CI جديد.
- **هذا نجاح بناء وليس اختبار هاتف.** توفر ANGLE وتطبيق override فعلي على POCO ما يزالان غير مثبتين حتى يرسل المالك فحص ANGLE من تطبيق MO الرئيسي.
- **توقيع Debug مختلف لكل CI:** إذا اضطررت لإزالة MO الرئيسي لتثبيت إصدار جديد، تأكد أولًا من عدم وجود أي إعداد GPU Debug مفعّل وضرورة الاحتفاظ بالنسخة الاحتياطية. آخر تقرير P12 السابق أكد التنظيف المحروس إلى 0/غياب ثلاث علامات، وليس قراءة حديثة بعد P13. لا تحذف لعبة ولا تجبر خيارات ANGLE العامة.
