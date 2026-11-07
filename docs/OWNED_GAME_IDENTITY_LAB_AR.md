# MO P10 — مختبر هوية الجهاز الوهمية (تطبيق نملكه فقط)
التاريخ: 2026-11-07 | جلسة `MO-20261107-CHAT-P10-IDENTITY-LAB-017`

## الهدف والمنهج
هذا مختبر تعليمي داخل APK المملوك لـMO `com.alwansan.mo.glesprobe`، **وليس** برنامج Spoof للألعاب الأصلية. أصل البحث هو رغبة المالك في فهم لماذا تُقفل بعض ألعاب Android خيارات Graphics/FPS بحسب هوية الجهاز، مع التزام صارم: هاتف stock بدون Root أو Bootloader Unlock أو VM/clone أو إعادة توقيع ألعاب.

الوظيفة المضافة في مصدر `probe/` فقط:
- زر **مختبر هوية الجهاز (محاكاة مستقلة فقط)** يفتح Activity داخل تطبيق **MO Layer Test** نفسه، غير مُصدَّر للتطبيقات الأخرى (`android:exported=false`).
- قياس Read-only من عملية المختبر: `Build.MODEL`، `Build.SOC_MODEL`، `GLES20.glGetString(GL_RENDERER)`، `glGetString` عبر NDK، `vkGetPhysicalDeviceProperties.deviceName` عبر Vulkan، ومعدل عرض الشاشة.
- تشغيل **قواعد قائمة لعبة اصطناعية** مختلفة لست قنوات: Java model، SoC، Java GLES، NDK GLES، Vulkan، وشرط عرض الشاشة 120Hz. اختيار Spinner يبدّل **سلاسل Java المحلية المعروضة للمحاكاة فقط**، ولا يغير القراءات الحقيقية أو دوال GLES/Vulkan أو إعدادات Android.
- خيارات: القيم الحقيقية؛ Model فقط؛ CPU فقط؛ Java GLES فقط؛ Native GLES فقط؛ Vulkan فقط؛ كل القنوات الوهمية. مثال `MO-LAB-MODEL (SIMULATED)` **ليس اسم هاتف حقيقي**.
- كل مسار له `LOCKED/UNLOCKED (MOCK)` مستقل؛ والخيار المركب الوهمي يتطلب توافق جميع القنوات وشرط 120Hz، لإظهار أن تعديل قناة واحدة لا يعني بالضرورة فتح قائمة لعبة حقيقية.
- **لا مقياس FPS حقيقيًا، ولا برنامج تشغيل GPU جديدًا، ولا ادعاء بمعرفة القواعد الداخلية لـOutlanders.**

## التنفيذ والتغييرات
- `probe/src/main/java/com/alwansan/mo/glesprobe/IdentityLabPolicy.java`: محاكاة pure Java غير مؤثرة على النظام، واستجابة حتمية قابلة للاختبار.
- `probe/src/test/java/com/alwansan/mo/glesprobe/IdentityLabPolicyTest.java`: أربع اختبارات للحالة الحقيقية، انفصال القنوات، شرط Hz، والقيم غير المتاحة.
- `probe/src/main/java/com/alwansan/mo/glesprobe/GameIdentityLabActivity.java`: واجهة قراءات حقيقية ووضعية محاكاة يمكن تبديلها فقط داخل المختبر.
- `ProbeNativeRenderer.java` و`mo_probe_renderer.cpp`: JNI `nativeVulkanName` يقرأ أول Vulkan physical GPU في **نفس العملية**، دون أي طبقة جديدة أو تغيير خصائص GPU.
- `CMakeLists.txt`: ربط Vulkan NDK للقراءة فقط.
- `ProbeActivity.java` و`AndroidManifest.xml`: إضافة زر المختبر وActivity غير مُصدَّر.
- `probe/build.gradle`: رفع إصدار APK الاختبار إلى `0.4.0`، versionCode4. تطبيق MO الأساسي ما زال في المصدر `0.9.0-alpha.9`.
- Workflow الحالي يبني **الـAPKين معًا** بعد تغييرات probe وينشرهما كملفات APK مستقلة، لذلك لا تستنتج وجوب تحديث MO الرئيسي من ظهور APK جديد في Release.

## ضوابط الهاتف المهمة
1. آخر تقرير للجهاز في v0.9 أثبت `enable_gpu_debug_layers=0` مع غياب علامات الطبقة الثلاث، لكن آخر اختبار PID للتطبيق المملوك **بعد** هذا التنظيف لم يُوثق بعد. اختبر من نسخة التطبيق المثبتة **قراءة فقط** دون إعادة تفعيل الطبقة.
2. **لا تحذف ولا تحدث MO Graphics Probe الرئيسي** إذا كان يحتفظ ببيانات إعدادات لا تزال تحتاج الرجوع. مفاتيح توقيع APK التجريبية تختلف بين عمليات Actions.
3. إذا أردت تجربة مختبر P10 بعد نجاح CI وتحقق الحالة الآمنة، يمكن استبدال تطبيق الاختبار المنفصل فقط `com.alwansan.mo.glesprobe`. افحص أن GPU Debug معطل قبل إزالة نسخة الاختبار القديمة؛ لا تحذف ألعابًا أصلية أبدًا.
4. داخل **MO Layer Test v0.4.0** افتح زر «مختبر هوية الجهاز»، سجل القراءة الحقيقية، ثم غيّر وضع المحاكاة. لن يلزم Shizuku لأن هذا النشاط لا ينفذ أي أوامر شل أو معاملات privileged.
5. النجاح يعني ظهور اختلاف في **نتائج القواعد الوهمية فقط** دون اختلاف القراءات الحقيقية. لا تستعمل عبارة «فتح 120FPS في Outlanders» مهما ظهر في المختبر.

## معايير قبول P10
- CI: `:probe:testDebugUnitTest` و`:probe:assembleDebug` و`:app:testDebugUnitTest` و`:app:assembleDebug` بنجاح؛ تحقق رفع ملفَي APK خام إلى GitHub Releases؛ لا تُسمِّه نجاح هاتف قبل التجربة.
- الهاتف: شاشة مختبر تعمل دون أذونات جديدة أو Shizuku، قراءات Java/native/Vulkan من نفس تطبيق الاختبار، فصل صارم بين الحقيقي والوهمي، لا تغير `Settings.Global` عبر هذا النشاط.
- أي فشل Vulkan يحمل حالة unavailable بدل crash أو ادعاء دعم وهمي.
- لا يعتمد على كود أو ثنائيات iUnlockerGL/COPG/NeoZygisk/ReZygisk، ولا مسارات Root أو نسخ ألعاب.

## المراجع
- `docs/DEVICE_FEEDBACK_ALPHA9_2026-11-07.md`
- `research/ZYGISK_COPG_NO_ROOT_FEASIBILITY_AR.md`
- https://developer.android.com/ndk/guides/graphics/getting-started
- https://developer.android.com/ndk/guides/rootless-debug-gles


## CI and direct APK verified — 2026-11-07
- Source: `4166210d4fb5e62b391b759f3b4508eb50d02e97`. GitHub Actions [42324225397](https://github.com/alwansan/MO/actions/runs/42324225397): **completed success** for app/probe unit tests, ARM64 native builds, APK builds and Release upload.
- Public [prerelease run25](https://github.com/alwansan/MO/releases/tag/mo-v0.9.0-alpha.9-run25), with **raw APKs**:
  - [MO-Layer-Test-v0.4.0.apk](https://github.com/alwansan/MO/releases/download/mo-v0.9.0-alpha.9-run25/MO-Layer-Test-v0.4.0.apk), 172148 bytes, SHA-256 `6fdc64c09d76bd0b071d46f5e10f850347a79212256a846a745ac9e8263095dd`.
  - `MO-Graphics-Probe-v0.9.0-alpha.9.apk` (unchanged source, newly debug-signed build), 1135214 bytes, SHA-256 `42fdb05b4256feb1ff6024f43f99b9206bbbd17c40f70ff925cbd719a9965282`. **DO NOT uninstall/update installed MO main just to use this test lab**.
- Earlier intermediate CI runs also produced releases from partial P10 commits. Use run25 as the complete P10 delivery candidate.
- No P10 device runtime test yet. Before replacing *only the disposable probe* APK, reconfirm `enable_gpu_debug_layers=0` and absence of the three MO owned layer keys; debug APK signatures differ between CI builds.

## نتيجة الهاتف بعد الإصدار — MO-20261107-CHAT-P10-DEVICE-018
- **PASS ضمن حدود المختبر فقط:** صور المالك تثبت تشغيل MO Layer Test v0.4.0 وGame Identity Lab على POCO F5 Pro في العملية PID5783. القراءات الحقيقية Build.MODEL `23013PC75G`، SoC `SM8475`، Java/NDK GLES + native Vulkan `Adreno (TM) 730`، الشاشة 120.0Hz.
- Mode0 يعرض combined fake FPS LOCKED؛ Mode6 يعرض combined fake FPS UNLOCKED مع سلسلة اسم كل قناة موسومة `(SIMULATED)` والقراءات الأصلية ثابتة. لا يعد هذا فتح 120FPS في Outlanders. أوضاع المحاكاة المفردة 1–5 لم توثق صورها حتى الآن.
- **تنبيه جديد أعلى أولوية:** تقرير MO v0.9 الأحدث يعرض `enable_gpu_debug_layers=1` وثلاث علامات مملوكة لـMO في النظام وبعد العملية. هذا يأتي بعد تقرير P9 سابق نظيف 0/absent؛ لذا الحالة الأخيرة المرصودة *مفعّلة*. لا تحذف أو تحدّث تطبيق MO الأساسي، ولا تعِد التفعيل. استخدم الاستعادة العادية القائمة على النسخة الاحتياطية السليمة، وافحص القيم الأربع. إذا تعذرت الاستعادة شارك الخطأ، ويمكن مناقشة التنظيف المحروس الاختياري فقط عند تطابق بصمة MO.
- **مطلوب لإغلاق اختبار التراجع:** قيم الأربع مفاتيح 0/غياب علامات MO، ثم **PID جديد بعد التراجع** مع GL_RENDERER الحقيقي. PID5783 أثناء استمرار التفعيل لا يكفي.
- التفاصيل: `docs/DEVICE_FEEDBACK_P10_2026-11-07.md`.
