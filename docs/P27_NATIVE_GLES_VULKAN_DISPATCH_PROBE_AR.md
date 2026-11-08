# P27 — خطة فحص مسار دوال GLES/Vulkan داخل Guest المملوك بعد نجاح P26

**Session:** MO-20261108-CHAT-P27-P26-DEVICE-PASS-NATIVE-DISPATCH-040  
**الحالة:** كود P27 قابل للبناء، نتائج الجهاز لا تزال غير متوفرة؛ لا حاجة لإعادة P25/P26.

## نقطة الانطلاق المؤكدة
- المالك أثبت في P26 تشغيل MO Layer Test v0.7 داخل Renjana Guest PID17047 (com.fesu.renjana:p0) عبر Rish. كل اختبارات تخزين Activity/Application وJava File وContext وnative libc نجحت بعد تعديل File.exists المحصور في تطبيقنا.
- ظل Java/Native/EGL GL_RENDERER وVulkan deviceName جميعًا Adreno730، وBuild.MODEL أصبح RMX3740. File.exists storage repair ليس GPU-hook.
- المصدر والتفاصيل: docs/DEVICE_FEEDBACK_RENJANA_P26_2026-11-08_AR.md.

## لماذا نفصل الدوال الأصلية عن طراز Android
- Renjana يغير حقول Java Build لكنه لا يعدل تلقائيًا GLES/Vulkan vendor dispatch. أي GPU spoof نظري يجب أن يثبت على وجه التحديد المسار الذي تستخدمه الدالة المملوكة الحقيقية، لا مجرد شاشة/قائمة وهمية.
- Android official rootless GLES debug layering لا يتيح التحميل الخارجي لكل التطبيقات: يشترط debuggable أو opt-in API30+ أو بيئة ذات صلاحيات خاصة. الإعدادات العامة قد تستمر بعد reboot وهي غير موثوقة في Rish P24؛ **لا نكتب أي key**.
- توثيق Android Bionic يوضح عزل linker namespaces والمنع الرسمي لاستخدام non-NDK private APIs عبر dlopen؛ لذلك لا نفترض أن الاسم أو مؤشر الدالة الذي نراه يمكن اعتراضه عالميًا.
- Vulkan له loader/ICD/dispatch خاص؛ اسم المكتبة التي يعيدها dladdr ليس بالضرورة مكان تنفيذ GPU vendor الفعلي.

## تنفيذ P27: read-only evidence داخل MO Layer Test
- مصدر جديد: probe/src/main/cpp/mo_dispatch_evidence.cpp، مرتبط فقط بمكتبة MOProbeRenderer.so عبر GLESv2/EGL/vulkan/dl.
- يقرأ **بدون تغيير**:
  - منشأ الدالة المرتبطة NDK glGetString.
  - منشأ مؤشر eglGetProcAddress("glGetString").
  - منشأ dlsym(RTLD_DEFAULT, "glGetString") من فضاء الرموز المرئي حاليًا.
  - تساوي مؤشرات هذه المسارات دون طباعة عنوان ذاكرة أو قراءته من عملية أخرى.
  - منشأ vkGetPhysicalDeviceProperties المرتبط وdlsym-visible داخل نفس العملية، مع فحص تساوي المؤشرين.
- يستخدم dladdr فقط ويطبع **اسم ملف المكتبة basename** (لا مسارات بيانات خاصة ولا raw pointer addresses)، ويصرح بحالة unavailable بدل تخمين.
- واجهة ProbeActivity تعرض التقرير بعد Java/NDK/EGL/Vulkan الحقيقية ضمن **PID واحد**، وتضعه في سجل MO-owned diagnostics المعتاد.
- نسخة APK المستهدفة MO Layer Test **0.8.0**. لا تتطلب Root أو Shizuku، لا تفعّل ANGLE أو Layer settings، لا توظف تخطي حماية لعبة، لا تقرأ عمليات أخرى، لا تغيّر قيمة الدوال أو تتظاهر بقدرات GPU وهمية.

## بوابات قبول دقيقة
1. CI: نجاح Java unit tests، بناء C++/JNI arm64 ورفع raw APK + SHA256. لا تدعُ لتثبيت APK قبل اكتمال النشر.
2. على هاتف المالك لاحقًا: شغّل v0.8 فقط في Renjana P26 الذي ثبت تخزينه، أرسل صورة تقرير P27 مع PID، واطلب ps read-only مرة واحدة فقط إذا تغيّر PID لإثبات العملية.
3. النتائج ستُقرأ كمصادر رموز جزئية **داخل التطبيق المملوك**، لا كإثبات native hook أو إمكان تطبيقه على closed-source apps.
4. إذا ظهر اختلاف بين direct/egl/RTLD_DEFAULT أو Vulkan، حدد ما إذا كان اختلاف loader/wrapper طبيعيًا قبل تفسيره اعتراضًا. لا تستخدم هذه الأداة لتغيير تعريف GPU أو رفع FPS.
5. لا تُحدث MO الرئيسي ولا Renjana P26 لأجل هذا الفحص، ولا تثبت APK تجريبيًا جديدًا على اللعبة.

## مراجع فنية رسمية
- Android NDK GLES layers: https://developer.android.com/ndk/guides/rootless-debug-gles
- Android NDK Vulkan external layers: https://developer.android.com/ndk/guides/graphics/validation-layer
- Android 15 Bionic linker restrictions: https://android.googlesource.com/platform/bionic/+/refs/heads/android15-tests-release/android-changes-for-ndk-developers.md

## حدود الاختبار
- **P26 STORAGE PASS مثبت على الجهاز** (موثق على حدة).
- **P27 native dispatch لم يُختبر على الهاتف بعد**. أي نجاح GitHub Actions لا يغير هذه الحالة.
- **GPU spoof/Adreno840، Vulkan feature replacement، زيادة FPS، تشغيل Outlanders داخل Renjana:** غير مثبتة، وليست هدف هذا الفحص.
