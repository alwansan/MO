# P26 — نتيجة اختبار الإصلاح الحقيقي على POCO Android 15 (2026-11-08)

**Session:** MO-20261108-CHAT-P27-P26-DEVICE-PASS-NATIVE-DISPATCH-040  
**Renjana P26 release:** https://github.com/alwansan/MO/releases/tag/renjana-owned-storage-p26-run1  
**Owner-owned test:** MO Layer Test v0.7.0 (no game, no system settings changed)

## مصدر الدليل
- لقطة المالك الأولى \`1006531223.jpg\`: 254565 bytes؛ SHA256 \`db502548697c684ed7075597853ac7dfe52338eee49989ebe43eefa16ce93ba7\`.
- اللقطة الثانية \`1006531224.jpg\`: 315589 bytes؛ SHA256 \`985510b4e53ff12ef8e89d5cd94327110ce08638bd15dfd717a6e3258b855190\`.
- سجّل المالك في Rish UID2000 أمر read-only: \`ps -A -o PID,NAME | grep -E 'com.fesu.renjana|com.alwansan.mo.glesprobe'\`، والنتيجة:
  - \`4548 com.alwansan.mo.glesprobe\` (التطبيق المثبت الخارجي).
  - \`16543 com.fesu.renjana\` (عملية الحاوية الرئيسية).
  - \`17047 com.fesu.renjana:p0\` (Guest الافتراضي).
- **PID17047** في الصور = **17047 com.fesu.renjana:p0** في Rish. هنا تأكدنا من التشغيل الفعلي داخل Guest، وليس fallback إلى التطبيق المثبت الخارجي. **لم يُقدّم قياس ذاكرة جديد للـPID17047؛ تبقى قياسات P24 مستقلة تاريخيًا.**
- الصور خاصة بمحادثة المالك ولا تُنشَر في المستودع؛ نتجنب رفع تفاصيل السجلات الخام/المسارات الداخلية.

## مقارنة هاتفية فعلية P25 مقابل P26

| الخاصية | P25 / PID8544 / النموذج XT2251-1 | P26 / PID17047 / النموذج RMX3740 |
|---|---|---|
| Activity File.exists | false | **true** |
| Activity isDirectory / canWrite | true / true | true / true |
| Activity direct File roundTrip / cleanup | PASS / PASS | PASS / PASS |
| Activity Context streams / cleanup | FAIL(FileNotFoundException) / NOT_FOUND_OR_FAILED | **PASS / PASS** |
| libc stat / native I/O / cleanup | YES / PASS / PASS | YES / PASS / PASS |
| Application File.exists | false | **true** |
| Application isDirectory / canWrite | true / true | true / true |
| Application direct File roundTrip / cleanup | PASS / PASS | PASS / PASS |
| Application Context streams / cleanup | FAIL(FileNotFoundException) / NOT_FOUND_OR_FAILED | **PASS / PASS** |
| Activity/Application canonical path equality | true | true |

**تفسير مثبت على الهاتف:** في P26 صارت واجهات التخزين المختبرة تعمل بعد تجاوز Renjana File.exists cloaking hook لتطبيق MO Layer Test المملوك وحده. الملاحظات متسقة مع تفسير مصدر Renjana الثابت: AntiDetection.shouldHidePath تخفي ملفات Renjana، وCoreHooks.createFileExistsHook يُرجع false، وVirtualContext.openFileInput/deleteFile/ensureDir تعتمد على File.exists.

**حدود الإثبات:** الاختبار قبل/بعد استخدم Instance وهوية مزيفة مختلفتين (XT2251-1 مقابل RMX3740)، ولم يتضمن تبديلًا متكررًا A/B/A تحت شروط مطابقة تمامًا. لذلك توجد **قرينة سببية قوية** وحل عملي لهذا السيناريو، لكن لا نزعم إثباته لكل Guest أو كل إصدار/ظروف Renjana. لم نختبر أداء الألعاب أو Persisted data عبر إيقاف الحاوية.

## GLES/Vulkan في P26
- Build.MODEL = RMX3740 (بدل الهاتف الفعلي 23013PC75G).
- GL_VENDOR = Qualcomm، Java GLES GL_RENDERER = Adreno (TM) 730؛ Native NDK GLES = Adreno (TM) 730؛ eglGetProcAddress = Adreno (TM) 730؛ native Vulkan deviceName = Adreno (TM) 730.
- GL_VERSION = OpenGL ES 3.2 V@0615.95، EGL_ANDROID_GLES_layers declared YES، Debuggable APK YES، Java/Native intercepted NO، mock FPS tier LOCKED (simulation).
- **لا يوجد تغيير فعلي في هوية GPU native، ولا Vulkan spoof، ولا قياس FPS في Outlanders.**

## القرار بعد نجاح P26
- احفظ نسخة Renjana P26 منفصلة كما هي؛ لا تدمج تجاوز File.exists عامًا لكل التطبيقات، لأن ذلك يعطل آليات anti-detection الأخرى وقد يغيّر ضمانات العزل. التصميم الأكثر أمانًا لتطوير Renjana مستقبلًا هو التمييز بين إخفاء المسارات الداخلية الحقيقية وعمليات Guest على مساراتها الخاصة؛ يحتاج اختبارات متعددة قبل التعميم.
- ابدأ P27 بالتحقق **قراءةً فقط** من مصدر رموز GLES/Vulkan للـMO-owned JNI في نفس عملية Guest، عبر dladdr/dlsym وEGL proc address، دون root، دون استبدال مكتبات، دون تعديل debug layers أو بيانات ألعاب.
- المصادر الرسمية: https://developer.android.com/ndk/guides/rootless-debug-gles ، https://developer.android.com/ndk/guides/graphics/validation-layer ، https://android.googlesource.com/platform/bionic/+/refs/heads/android15-tests-release/android-changes-for-ndk-developers.md .
- **فصل مهم:** منشأ الرمز الذي يعيده dladdr ليس بالضرورة منشأ تنفيذ تعريف GPU vendor النهائي؛ وظهور وظيفة قابلة للحل لا يعني أن الاعتراض ممكن داخل لعبة مغلقة بلا Root أو تعاون.
