# P24 — نتيجة تشغيل ملتقط Renjana P23 على Android 15، وتحليل API والذاكرة

**التاريخ:** 2026-11-08 (Asia/Baghdad) | **الجلسة:** MO-20261108-CHAT-P24-RENJANA-CAPTURE-037
**النطاق:** Renjana وMO Layer Test المملوك فقط؛ لا Outlanders.

## إثبات تشغيل ملتقط P23
- استلمنا MO_RENJANA_OWNED_CAPTURE_20261108_035734.txt، حجمه 42866 بايت، 336 سطر، SHA256: 7f18d75643390c8a33952bc0bec46ae4251e7293a8ea509a7c6965cf401191a0.
- زمن القياس 03:57:34 +0300؛ UID2000 shell عبر Rish Android API35. **هذه أول تجربة جهاز مثبتة لسكربت P23**؛ الاختبارات المحلية السابقة لا تثبت عمله على الهاتف. السجل الخام لم يُرفع إلى GitHub العام.
- مصدر MO main قبل P24: 7cc3439b95821bee60b845c50fec35a5b3b93513؛ أحدث Renjana workflow ناجح 42429759178، Release renjana-owned-lab-run2. لا جلسة أحدث من P23 وقت الفحص.
- Guest الحالي هو PID23140 واسم العملية com.fesu.renjana:p1. PID17591/p0 كان من تجربة P23 السابقة؛ لم نتلق لقطة شاشة جديدة لقراءات Java/GLES/Vulkan لعملية PID23140.

## السلوك في سجل Logcat الجديد
- Launch 03:56:55–03:56:56: InstanceLauncher وStubActivity_1 onCreate(pid23140)، ثم VirtualClassLoader، Pine guest hooks installed، DeviceFingerprint logged realme RMX3491 Android12، Guest Application started، وlibMOProbeRenderer.so loaded using isolated linker namespace clns-9.
- ضمن guest PID23140 سُجّل تشغيل تعريف Qualcomm الحقيقي: /vendor/lib64/egl/libGLESv2_adreno.so (Version 0615.95). هذا ليس إثباتًا لتغيير هوية GPU أو Vulkan؛ بل يدعم فصل مسار Java Build عن مكتبات الرسوميات الأصلية.
- **مشاكل:** رسالتا Pine عن رموز hiddenapi غير موجودة في libart.so، رغم إعلان Pine guest hooks installed؛ لا يعني READY نجاح كل hook. تكررت 15 مرة تحذيرات VirtualCtx: Could not create virtual directory ضمن بيانات guest. السبب وتأثيره الفعلي على حفظ الملفات لم يثبتا.
- فشلت محاولات قراءة مفاتيح GPU Debug الأربعة جميعها برسالة cmd: Failure calling service settings: Failed transaction (2147483646). النتيجة **UNVERIFIED** وليست 0 أو null؛ لم نغيّر أي إعداد.

## ذاكرة العمليات المقاسة — kB

| PID / الدور | TOTAL PSS | TOTAL RSS | TOTAL SWAP PSS |
|---|---:|---:|---:|
| 14458 / MO خارجي | 40820 | 99424 | 34697 |
| 14699 / Renjana main | 83127 | 175340 | 22181 |
| 23140 / Renjana guest p1 | 106509 | 231020 | 164 |

مجموع PSS للعملية الرئيسية وGuest النشط بنفس اللحظة **189636 kB ~185.19 MiB**. ليس هذا كلفة الحاوية الإضافية؛ لا baseline مع Renjana فارغة أو اختبار مستقل مماثل. جمع RSS ليس طريقة صحيحة لقياس الذاكرة الفريدة، ولا توجد قياسات FPS/حرارة في هذا التقرير. تقرير main السابق PSS77021 kB كان في ظرف زمني مختلف.

## تحليل Renjana SHA 14302a57cd66114c6979acf7ca56c97f845a6841
- StubActivity.kt ينشئ VirtualContext/VirtualClassLoader، يركّب Pine Hooks، ويستدعي DeviceFingerprint.applyDeviceSpoofToProcess عند Enable Fingerprint قبل تشغيل Guest Application وActivity.
- VirtualClassLoader.kt يستخدم DexClassLoader مع native lib search paths؛ هذا لا يستبدل vendor GLES/Vulkan driver.
- DeviceFingerprint.kt يكتب حقول android.os.Build Java بـreflection ويسجل اعتراضات Java SystemProperties؛ لا يغيّر بحد ذاته glGetString أو vkGetPhysicalDeviceProperties.
- PineHookManager.kt وCoreHooks.kt يستخدمان Java/ART/Xposed bridge وحيل Context/PackageManager/File؛ لم نجد اعتراض GPU native مثبتًا في الملفات المفحوصة. قد تؤثر File hooks على تحذيرات البيانات؛ السبب ما زال فرضية.
- المصدر Apache-2.0: https://github.com/josskixg/renjana/tree/14302a57cd66114c6979acf7ca56c97f845a6841 . لم نستورد مصدر Renjana أو iUnlockerGL إلى MO.

## تحسينات P24
- أضفنا tools/analyze_renjana_owned_capture.py: ملخص محلي دون نشر Logcat الخام؛ يميز main/guest وPSS/Settings UNKNOWN، ويحصي التحذيرات. اختبارات Python fixtures: **3/3 PASS محليًا**. تشغيله على ملف الجهاز كشف 2 Pine symbol warnings و15 VirtualCtx warnings.
- حسّنّا tools/renjana_owned_lab_capture.sh: فشل Settings service يعرض UNVERIFIED بدل اعتباره حالة نظيفة. اختبار حالة الفشل أضيف إلى tools/test_renjana_owned_lab_capture.py. **لم نختبر النسخة المعدلة من shell على الجهاز**.
- لا APK أو Renjana build جديد في P24: تغيرت أدوات التقارير/التوثيق فقط.

## ما التالي
1. لا تعِد اختبار P23 المكتمل. احصل لاحقًا على readback موثوق للمفاتيح الأربعة عبر قراءة فقط من MO أو غيره قبل أي Debug Layer activation. لا تغيير إعدادات بالتخمين.
2. قبل بناء Guest hook أصلي جديد، افصل القراءات الحقيقية Java GLES وNative GLES وEGL وVulkan في نفس Guest PID، مع عزل أي محاكاة UI. اختبار مقصود في تطبيق مملوك فقط؛ لا يعني إمكان تشغيل لعبة كبيرة أو زيادة FPS.
3. افحص تحذيرات Pine/VirtualContext على Android15، واختبر حفظ بيانات Guest المملوك وbaseline الذاكرة مع Renjana فارغة إذا لزم قياس overhead.
4. لا Root أو Bootloader أو Zygisk أو لعبة Outlanders/حساباتها أو تغيير ANGLE/GPU Debug عام، ولا اقتباس مواد iUnlockerGL المحمية.
