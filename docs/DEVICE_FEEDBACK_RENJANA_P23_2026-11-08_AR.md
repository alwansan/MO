# P23 — تحقق Renjana rootless على POCO F5 Pro: تشغيل Guest وتغيير Build فقط
**8 نوفمبر 2026 | جلسة:** MO-20261108-CHAT-P23-RENJANA-RUNTIME-036 | **origin=chat**

## أدلة المستخدم المحلية — لا ننشر السجلات الخام
- الصور تثبت أن شاشة MO Layer Test داخل Renjana تعرض **PID 17591** و`Android Build.MODEL: RMX3491`، بينما النسخة الخارجية تعرض **PID 14458** و`23013PC75G`. Rish shell `ps -A -o PID,NAME`: `14458 com.alwansan.mo.glesprobe`, `14699 com.fesu.renjana`, `17591 com.fesu.renjana:p0`. وجود تطابق PID مع process `:p0` وظهور `com.fesu.renjana/.core.StubActivity_0` في مهام Android دليل قوي أن البرنامج اشتغل داخل Renjana، وليس مجرد fallback تشغيل النسخة الخارجية. **لا يثبت كل hooks أو عزل الموارد بشكل كامل**.
- صورة إنشاء instance تبين GMS OFF بينما Spoof Signature وAnti-Detection وDevice Fingerprint Spoofing ظاهرة ON؛ لذا نجاح Java Build.MODEL لا يثبت أنه يعمل مع جميع الخيارات OFF، ولا يثبت أي تغير أصلي في النظام. مصدر Renjana `StubActivity.kt` يطبق `DeviceFingerprint.applyDeviceSpoofToProcess` عندما enableFingerprint؛ `DeviceFingerprint.kt` يغيّر `Build.MODEL` في **عملية guest**.
- GPU داخل guest يظل `Adreno (TM) 730` في `GL_RENDERER` Java وNDK وeglGetProcAddress. Vulkan لم يظهر تغيّرًا حقيقيًا مثبتًا في نفس guest؛ لا توجد أدلة فتح رسومات أو FPS. صورة **النسخة الخارجية** PID14458 تُظهر `MO-GLES-LAYER-PROOF (SIMULATED)` على مسار NDK مع Java=Adreno730، بينما guest PID17591 يعرض NDK=Adreno730. **لا تخلط developer GPU debug layers المحتملة للنسخة الخارجية مع GPU spoof في Renjana**. يلزم readback جديد للمفاتيح الأربعة قبل إعادة التمكين أو تحديث MO.
- صورة Game Identity Lab عند PID14458 تثبت أن اختيار 6/6 يغيّر واجهة محاكاة فقط، وليس Android أو Outlanders.

## سجل المستخدم MO_RENJANA_LAB_LOG.txt
- استلم الملف **600 سطر، 304111 بايت**، SHA256 `eefc93541ef784c219bd3780ae003b381dc757df9aff789df3d22eb883af56eb`; يبدأ 2026-11-08 03:40:47.674 وينتهي 03:43:17.371 (وقت الهاتف). تم تحليله محليًا ولم تُرفع سطوره الخام إلى repo العام.
- هو ناتج tail 600 سطر من *الرسائل المطابقة الأخيرة* بعد وصول pipe `logcat -d | grep | tail` إلى نهايته. ليس سجل دورة إطلاق guest كاملة؛ معظم الرسائل UI Recents من MIUI تشمل `StubActivity_0` و`ProbeActivity`.
- توزيع مراجعة الرسائل: `RecentsTaskLoader=207`, `ActivityManagerWrapper=201`, `TaskViewThumbnail=64`, `RecentsModel=53`; لا توجد رسائل مباشرة `StubActivity launch succeeded`, `Pine`, `CoreHooks` أو `DeviceFingerprint` في هذا المقطع. صيغة `E ActivityManagerWrapper` ليست استثناء قاتلًا بحد ذاتها. تحذيرا `MiuiStubRegistry ClassNotFoundException` عند 03:41:12 مرتبطان بسياق `ShizukuShellLoader` في المقطع ولا يثبتان crash Renjana. سجل `AndroidRuntime` يظهر result code0 لعملية أخرى؛ لا استنتاج من غياب crash في tail بأن الجلسة بكاملها خالية من الأخطاء.
- إخراج الملف بعد إغلاق التطبيق **لا يعني** أن الإغلاق أنشأ الملف. الأمر القديم غير محدود `logcat -d` قد يحتاج زمنًا لمعالجة سيل نظامي ضخم؛ `tail -n600` يحفظ آخر 600 ولكنه يقرأ ما قبله كله.

## قياسات الذاكرة
- الملف `MO_RENJANA_MEMINFO.txt`، SHA256 `b5a608ff6c1c1829295d414774540dfc9ceef6ee56e477367ef1b9a7e2b07860`: فقط `pid 14699 [com.fesu.renjana]` **العملية الرئيسية**.
- `TOTAL PSS 77021 kB` (~75.22 MiB), `TOTAL RSS 158380 kB` (~154.67 MiB), `TOTAL SWAP PSS 47886 kB` (~46.76 MiB). لا تجمع RSS+PSS أو Swap PSS باعتبارها استهلاك ذاكرة حقيقية مستقل. ليست كلفة guest لأن `:p0` (PID17591) لم يقس؛ ولا توجد baseline مع Renjana فارغ.
- لا Benchmark/FPS/Thermal منشور حتى الآن؛ عدم استهلاك موارد إضافية أو القدرة على تشغيل Outlanders غير مثبتة.

## إصلاح أدوات القياس (P23)
- سكربت مستقل read-only `tools/renjana_owned_lab_capture.sh`: مستخدم Rish UID2000 في Android15 فقط؛ يلتقط الساعة وPID إلى process `com.fesu.renjana:pN` (guest) و`com.fesu.renjana` (main) وMO host، `dumpsys meminfo <pid>` لكل واحدة، و`settings get global` للمفاتيح الأربعة **قراءة فقط**، و`logcat -t 12000 -b main -b crash` bounded auto-exit مع فلترة PID/tag لتقليل انكشاف بيانات تطبيقات أخرى. يكتب تقريرًا واحدًا في `Download/MO_RENJANA_OWNED_CAPTURE_*.txt`. لا ينفّذ حذف/تغيير إعدادات أو logcat -c. اختبارات Python offline ضد mocks في `tools/test_renjana_owned_lab_capture.py` نجحت (2/2) + `sh -n`؛ **لم يُنفّذ السكربت بعد على جهاز المستخدم**.
- تتبع Android15 AOSP logcat -t implies -d: https://android.googlesource.com/platform/system/logging/+/refs/heads/android15-s1-release/logcat/logcat.cpp

## أمر الاختبار القادم الآمن
افتح MO Layer Test من *نسخة Renjana guest*، اتركه أمامك، ثم من Termux:
```sh
cd /storage/emulated/0/Download
curl -fL https://raw.githubusercontent.com/alwansan/MO/main/tools/renjana_owned_lab_capture.sh -o renjana_owned_lab_capture.sh
cd ~/test
sh rish
sh /storage/emulated/0/Download/renjana_owned_lab_capture.sh
```
بعد ظهور `Saved:` ارفع ملف `MO_RENJANA_OWNED_CAPTURE_*.txt` فقط بعد مراجعته؛ يجب أن يتضمن meminfo للعمليات الثلاث `p0` وRenjana الرئيسية وhost إن كانت قيد التشغيل. اختبر أيضًا في Renjana **instance واحدة فقط** لتجنب اختلاط النتائج؛ لا تفتح اللعبة ولا تُدخل حساب Google.
- إذا `enable_gpu_debug_layers=1` أو `gpu_debug_app` ليس `null` راجع الإعدادات وحالة نسخة MO المثبتة أولًا، **لا تحذف أو تحدّث MO قبل استعادة حالتها من واجهته**. لا تكتب Settings.Global يدويًا؛ لا نعلم baseline القديم ولا نعطي تعليمات عمياء.
- سجلات Renjana الداخلية Settings → Error Logs قد تعرض أدلة أدق من logcat المحدود، إن وجدت.
- المرحلة التالية الهندسية: source-only R&D لعزل **Java Build fingerprint vs NDK GLES vs EGL vs Vulkan** على app مملوك في guest؛ لا تثبت قدرة على تغيير GPU داخل لعبة مغلقة، ولا تبنِ APK لتطبيقات الغير قبل إثبات هذا المسار.
