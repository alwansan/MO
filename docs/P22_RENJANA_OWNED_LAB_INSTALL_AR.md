# MO P22 — تجربة Renjana داخل تطبيق مملوك (بلا روت، بلا لعبة)

## تحديث P23 بعد اختبار الهاتف الحقيقي (2026-11-08)
المالك شغّل **MO Layer Test داخل `com.fesu.renjana:p0` فعليًا**: guest PID 17591 وBuild.MODEL=RMX3491 مقابل host PID14458 وBuild.MODEL=23013PC75G. Java وNDK GLES في guest ما زالت Adreno730؛ لا إثبات spoof Vulkan أو FPS. سجل logcat السابق الذي وصل بعد إغلاق التطبيق هو آخر 600 سطر من رسائل UI غالبًا، وليس dump كاملًا من بدء guest. Meminfo السابق PID14699 يحصي **Renjana الرئيسي فقط**: PSS77021 kB؛ guest `:p0` لم يُقَس.
**الأداة الحالية الأسهل والأدق** (لا تتطلب Root ولا تبدّل إعدادات): افتح MO Layer Test من Renjana أولًا واتركه مفتوحًا؛ نفّذ في Termux:
```sh
cd /storage/emulated/0/Download
curl -fL https://raw.githubusercontent.com/alwansan/MO/main/tools/renjana_owned_lab_capture.sh -o renjana_owned_lab_capture.sh
cd ~/test
sh rish
sh /storage/emulated/0/Download/renjana_owned_lab_capture.sh
```
ارفع `Download/MO_RENJANA_OWNED_CAPTURE_YYYYMMDD_HHMMSS.txt` بعد مراجعة محتواه؛ يتضمن PID والميموري لكل عملية ومفاتيح GPU Debug **قراءة فقط** وlogcat bounded `-t 12000` وينتهي تلقائيًا. لا تستعمل `logcat -c` ولا تكرر فتح لعبتك. لاحظ أن شاشة إنشاء Instance السابقة أظهرت Device Fingerprint Spoof ON بجانب Anti-Detection وSignature Spoof ON؛ للتجربة التالية اعزل خيار Fingerprint فقط في instance اختبار مملوكة، دون الحسابات أو اللعبة. إذا مفاتيح GPU Debug الحالية ما زالت مفعّلة، استعد إعداداتها الصحيحة عبر MO قبل حذفه/تحديثه.
التفاصيل: [تقرير P23](../docs/DEVICE_FEEDBACK_RENJANA_P23_2026-11-08_AR.md).

**التاريخ:** 2026-11-08 · **الجلسة:** MO-20261108-CHAT-P22-RENJANA-OWNED-LAB-035 · origin=chat

## ما هو هذا وما ليس هو
بحثنا في `josskixg/renjana` نسخة v0.2.0 مفتوحة المصدر Apache-2.0. الإصدار غير منشور رسميًا كـ APK؛ GitHub Actions Workflow المنفصل في MO يجلب **SHA محددًا** `14302a57cd66114c6979acf7ca56c97f845a6841` ويبني debug لـ ARM64 من المصدر وينشره **فقط بعد** نجاح اختبارات الوحدة والبناء والتحقق من توقيع APK.
- Workflow: https://github.com/alwansan/MO/actions/workflows/mo-renjana-owned-lab.yml
- أول تشغيل: https://github.com/alwansan/MO/actions/runs/42429358721
## حالة البناء والنشر الفعلية (2026-11-08)
- Build #1 run `42429358721`: جميع اختبارات المصدر وتجميع/فحص توقيع APK نجحت، لكن مهمة النشر فشلت فقط بسبب وجود `renjana-output/` في `SHA256SUMS.txt` بعد انتقال الملف إلى مجلد `release/`.
- أصلحت شكل SHA256SUMS في commit `77867f4ec178fadfb25719692be859f1333f2300` بحيث تكون أسماء ملفات نسبية.
- Build #2 https://github.com/alwansan/MO/actions/runs/42429759178 **completed SUCCESS**: اختبارات الوحدة، بناء ARM64، توقيع APK والتحقق، ثم النشر مرّت كلها.
- GitHub prerelease **مؤكد، draft=false**: https://github.com/alwansan/MO/releases/tag/renjana-owned-lab-run2
- رابط APK المباشر: https://github.com/alwansan/MO/releases/download/renjana-owned-lab-run2/Renjana-v0.2.0-arm64-OWNED-LAB-debug.apk
- الحجم **55,348,455 بايت**، SHA-256 `7b62bfad247050e03bcdcefd693cb47b14d45b4c5ac945ca74fa58f092349b63` (verified in GitHub uploaded release asset metadata); source pinned to `14302a57cd66114c6979acf7ca56c97f845a6841`.
- هذا إثبات **build+publish فقط**، لا اختبار هاتف ولا ضمان تخصيص GLES/Vulkan. لا توجد ضرورة لـ Shizuku لتشغيل Renjana الأساسي.

### تحميل عبر Termux (بدون روت)
```sh
cd /storage/emulated/0/Download
curl -fL 'https://github.com/alwansan/MO/releases/download/renjana-owned-lab-run2/Renjana-v0.2.0-arm64-OWNED-LAB-debug.apk' -o Renjana-OWNED-LAB.apk
sha256sum Renjana-OWNED-LAB.apk
```
طابق الـSHA مع القيمة أعلاه ثم افتح `Renjana-OWNED-LAB.apk` من مدير الملفات وثبته. لا تثبّت نسخة بعنوان Renjana من رابط فيديو أو متجر غير معروف.

- عند النجاح فقط، Release تجريبي بعنوان `Renjana OWNED APP Lab` وفيه APK مباشر وملف ترخيص وSHA256. راجع: https://github.com/alwansan/MO/releases . **تم التحقق من نجاح الإصدار الثاني؛ التعليمات أدناه تستخدم رابط run2 المباشر**.
- هذا ليس مُنتج MO الرسمي ولا نسخة upstream رسمية ولا نتيجة إثبات التوافق؛ ليس VM بنظام Android ROM كامل؛ لا وعد أداء أو تشغيل Outlanders أو فتح FPS.
- لا يستخدم روت/Shizuku/تعديل نظام لتشغيل الحاوية. لا يحتاج فك البوتلودر.

## تطبيق الاختبار المستقل الذي نملكه
`com.alwansan.mo.glesprobe` (MO Layer Test), v0.6.0 من GitHub Releases:
https://github.com/alwansan/MO/releases/download/mo-v0.11.0-alpha.11-run59/MO-Layer-Test-v0.6.0.apk
يوفر قراءات Build.MODEL، Java GLES، Native GLES، EGL، Vulkan مستقلة. ليست قراءات Outlanders.

## خطوات التنفيذ على Poco F5 Pro Android 15
1. إذا CI فشل لا تثبّت أية نسخة ولا APK من مصادر مجهولة، وأرسل log Actions. تأكد أن Release يحمل عبارة `Renjana OWNED APP Lab` وترخيص المصدر.
2. حمّل **ملف Renjana*arm64*OWNED-LAB-debug.apk نفسه** وليس ZIP المصدر من Release MO بعد التحقق؛ يمكن استخدام متصفح الهاتف أو Termux/curl للرابط المباشر المعلن في Release. لا تستورد iUnlockerGL.
3. ثبّت MO Layer Test v0.6 فقط إذا لم يكن موجودًا. التحديث فوق توقيع مختلف قد يفشل؛ **لا تحذف بيانات MO الأساسية ولا تغيّر GPU Debug settings**. اختبار Renjana لا يتطلب أي GPU debug toggle.
4. افتح MO Layer Test خارج Renjana. في `MO / GAME IDENTITY LAB` و`MO / GLES LAYER TEST`، صور baseline الحقيقي: `Build.MODEL=23013PC75G`, `GL_RENDERER=Adreno (TM) 730`, `Vulkan=Adreno (TM) 730`. لا تغيّر هوية النظام؛ هذه أرقام مرجعية فقط.
5. ثبّت Renjana APK التجريبي إن اجتاز CI، افتح **Apps** وابحث عن `MO Layer Test` / `com.alwansan.mo.glesprobe`; أنشئ **instance** منفصلًا. إن وجدت شاشة إعدادات: اجعل Enable GMS **OFF**، Spoof Signature **OFF**، Anti-Detection **OFF**، Enable Fingerprint **OFF** أولًا. لا تضف حساب Google أو أي لعبة أو بيانات المستخدم.
6. من Home في Renjana اضغط instance/Play، سجّل هل يفتح MO Layer Test بصورة طبيعية أم يتوقف أم يعيدك إلى التطبيق المثبت خارجيًا. **مهم**: مصدر `InstanceLauncher.kt` فيه fallback direct launch بدون عزل، لذلك مجرد فتح الشاشة لا يثبت نجاح الافتراضية. احفظ screenshot لرسالة Success / Fallback No Isolation / Failure إن ظهرت.
7. داخل MO Layer Test الافتراضي، صور قراءات Game Identity Lab/GLES/Vulkan وسجّل PID وقارن مع baseline؛ أعد الاختبار بخيار Enable Fingerprint إذا ثبت تشغيل instance الافتراضي نفسه. حتى لو تغير Build.MODEL فلا تفترض تغير GL/Vulkan.
8. قبل التقاط السجل، و**أثناء بقاء MO Layer Test مفتوحًا داخل Renjana**، قارن اسم العملية الفعلي من Rish (UID2000):

```sh
ps -A -o PID,NAME | grep -E 'com.fesu.renjana|com.alwansan.mo.glesprobe'
```

إذا ظهر PID الذي تعرضه شاشة MO Layer Test تحت اسم العملية `com.alwansan.mo.glesprobe` فقد يكون تشغيلًا خارجيًا عاديًا. إذا ظهر تحت `com.fesu.renjana:pN` فهذا يدعم تشغيله داخل حاوية Renjana لكن لا يثبت بمفرده صحة كل hooks. أرفق الصورة والإخراج للمقارنة. لا تحتاج root.
إذا أردت قياس كلفة الحاوية أثناء تشغيل تطبيق الاختبار، نفّذ في Rish فقط:

```sh
dumpsys meminfo com.fesu.renjana > /storage/emulated/0/Download/MO_RENJANA_MEMINFO.txt
```

هذا يقيس ذاكرة عمليات Renjana وقت الفحص، وليس FPS لعبة؛ التقط قياسًا مع عدم تشغيل أي instance ثم قياسًا مع MO Layer Test guest لتقدير الزيادة، مع تسجيل الوقت/حرارة الجهاز والسياق.

9. في Renjana نفسه يمكن فتح **Settings → Error Logs** ثم فتح رسالة الخطأ واختيار **Copy** أو **Share** (حسب واجهة الإصدار)، وهي طريقة أبسط قبل Rish. دليل upstream: https://github.com/josskixg/renjana/blob/main/docs/ERROR_LOG.md . وللحصول على سجل أوسع من Rish shell UID2000 فقط (في حال فشل تشغيل guest)، نفّذ بعد محاولة الإطلاق:

```sh
logcat -d -v threadtime | grep -Ei 'renjana|fesu|glesprobe|stubactivity|fallback|no isolation|AndroidRuntime|FATAL EXCEPTION|ClassNotFoundException|UnsatisfiedLinkError' | tail -n 600 > /storage/emulated/0/Download/MO_RENJANA_LAB_LOG.txt
ls -lh /storage/emulated/0/Download/MO_RENJANA_LAB_LOG.txt
```
هذا لا يحتاج Python أو pkg داخل shell. إن لم تكن داخل Rish UID2000 استخدم `sh ~/test/rish` من Termux بعد ضبط `RISH_APPLICATION_ID` سابقًا. راجع السجل لاحتمال وجود بيانات حساسة قبل رفعه.
10. اختبار المصدر والهوية مكتمل فقط عندما نرى في سجل Renjana أنه أطلق التطبيق **ضمن StubActivity** وأن التطبيق guest يعمل لا مجرد fallback، ويعرض قراءات Android الحقيقية/المتغيرة. لا تختبر أو تستنسخ Outlanders الآن.
11. عند الانتهاء، أوقف/احذف instance التجريبي داخل Renjana فقط. لا تحذف `com.tencent.mhadv` ولا تنسخ ملفاته ولا تغيّر توقيعه أو حسابه. لا تتوقع تشغيل/تغيير اللعبة بفتح Launcher عادي.

## ماذا نطلب من المالك بعد الاختبار
- Screenshot من Create Instance ونتيجة Play.
- Snapshot للقراءات داخل الحاوية؛ Game Identity Lab (real Build, Java GLES, NDK GLES, Vulkan).
- `MO_RENJANA_LAB_LOG.txt` فقط عند فشل/عودة مباشرة؛ سجلات خاصة بحاوية Renjana لا للنظام كله.
- أداء تقريبي وتجمّد/سخونة إذا ظهر، دون استنتاج FPS لعبة أصلية.

## روابط أدلة المصدر
- https://github.com/josskixg/renjana/blob/14302a57cd66114c6979acf7ca56c97f845a6841/app/src/main/java/com/fesu/renjana/core/InstanceLauncher.kt
- https://github.com/josskixg/renjana/blob/14302a57cd66114c6979acf7ca56c97f845a6841/docs/CREATE_INSTANCE.md
- https://github.com/josskixg/renjana/blob/14302a57cd66114c6979acf7ca56c97f845a6841/LICENSE
- https://github.com/josskixg/renjana/blob/14302a57cd66114c6979acf7ca56c97f845a6841/app/build.gradle
