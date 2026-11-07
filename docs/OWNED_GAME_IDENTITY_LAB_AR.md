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
