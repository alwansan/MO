# MO P22 — تجربة Renjana داخل تطبيق مملوك (بلا روت، بلا لعبة)
**التاريخ:** 2026-11-08 · **الجلسة:** MO-20261108-CHAT-P22-RENJANA-OWNED-LAB-035 · origin=chat

## ما هو هذا وما ليس هو
بحثنا في `josskixg/renjana` نسخة v0.2.0 مفتوحة المصدر Apache-2.0. الإصدار غير منشور رسميًا كـ APK؛ GitHub Actions Workflow المنفصل في MO يجلب **SHA محددًا** `14302a57cd66114c6979acf7ca56c97f845a6841` ويبني debug لـ ARM64 من المصدر وينشره **فقط بعد** نجاح اختبارات الوحدة والبناء والتحقق من توقيع APK.
- Workflow: https://github.com/alwansan/MO/actions/workflows/mo-renjana-owned-lab.yml
- أول تشغيل: https://github.com/alwansan/MO/actions/runs/42429358721
- عند النجاح فقط، Release تجريبي بعنوان `Renjana OWNED APP Lab` وفيه APK مباشر وملف ترخيص وSHA256. راجع: https://github.com/alwansan/MO/releases . **لا تفترض وجود الرابط أو نجاحه قبل تحقق النتيجة**.
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
9. للحصول على سجلات مختصرة من Rish shell UID2000 فقط (في حال فشل تشغيل guest)، نفّذ بعد محاولة الإطلاق:

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
