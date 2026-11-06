# تشخيص فشل طبقة GLES في تطبيق MO التجريبي — بدون روت
2026-11-07 | MO-20261107-CHAT-P4-FEEDBACK-011

## النتيجة المثبتة
Shizuku نجح في تغيير `enable_gpu_debug_layers` إلى `1` وتحديد تطبيقنا `com.alwansan.mo.glesprobe` كهدف. لكن التطبيق التجريبي بقي يعرض `Adreno (TM) 730` و`Layer intercepted: NO`. **تغيير Settings.Global لا يثبت تحميل .so أو اعتراض glGetString**.

## قبل أي تحديث
بما أن آخر تقرير أظهر إعدادات GPU Debug مفعلة، افتح **MO v0.4** واضغط **استعادة إعدادات GPU Debug القديمة**؛ تأكد من رسالة `OK: all original GPU debug values restored`، ثم أعد فحص `enable_gpu_debug_layers` و`gpu_debug_app`. لا تزل التطبيق ولا تثبّت APK آخر قبله؛ لأن نسخة القيم القديمة في SharedPreferences وقد تضيع عند حذف التطبيق بسبب اختلاف توقيع Debug.

## لماذا ظهر NO؟
أولًا: قد يكون تطبيق الاختبار بقي داخل نفس العملية دون إغلاق إجباري وإعادة فتح، فلا يعيد EGL Loader فحص إعدادات المطور.
ثانيًا: قد يفشل Android/HyperOS في إيجاد المكتبة داخل مجلد `lib/arm64-v8a`، أو في تحميلها، أو في اعتماد debug layer للمستهدف.
ثالثًا: قد يرى Native GLES القيمة مختلفة عن Java GLES20 (تجربة v0.5 ستقارن الاثنين).
رابعًا: احتمال اختلاف ربط رموز AndroidGLESLayer_Initialize وAndroidGLESLayer_GetProcAddress، أو مسارات تحميل طبقة EGL في نسخة ROM. تحتاج logs وليس استنتاجًا من الاسم فقط.
الواجهة التجريبية تشير إلى `Mock 120 FPS` (محاكاة فقط) وليس قياس FPS حقيقي.

## ما الذي يختبره v0.5؟
- PID جديد، مقارنة Java `GLES20.glGetString` مع C++ `glGetString` داخل نفس EGL Context.
- زر `اختبار تحميل مكتبة GLES` يختبر فتح ELF يدويًا فقط، **لا يشغّل الطبقة الرسمية بحد ذاته**.
- زر تشخيص في MO الأساسي عبر Shizuku يقرأ أربعة مفاتيح GPU Debug، ويتحقق من وجود APK ويعرض رسائل EGL loader المفلترة لتطبيق MO Test.
- لا ملفات لعبة، لا root، لا VM، لا إعادة توقيع Outlanders.

## ترتيب الاختبار المستقبلي
1. **استعد إعدادات GPU القديمة في v0.4** ثم ثبّت v0.5 إن احتجنا، بعد تأكيد Restore.
2. اجمع baseline من التطبيق الاختباري الجديد.
3. فعّل تجربتنا فقط ووافق، أوقف التطبيق الاختباري إجباريًا ثم افتحه من جديد، واحتفظ بتقرير Java/NDK/PID ونتيجة Read-only Diagnosis.
4. استعد الإعدادات من MO **قبل أي حذف أو تحديث**، وتحقق من رجوع اسم Adreno الحقيقي.
5. إن فشل التحميل، استخرج سبب فشل مكتبة محدد من السجلات؛ لا تجبر طبقة على تطبيق لعبة مغلق ولا ترفع الصلاحيات.

المراجع الرسمية: https://developer.android.com/ndk/guides/rootless-debug-gles

## v0.5 CI build verification (2026-11-07)
- Source commit `79ae93392461f03b0fe5a5d33b2c59be328a3345` from ongoing P5 diagnostic effort.
- GitHub Actions run [42312793163](https://github.com/alwansan/MO/actions/runs/42312793163): **completed SUCCESS**, all tests and builds and Release upload.
- Direct GitHub prerelease: https://github.com/alwansan/MO/releases/tag/mo-v0.5.0-alpha.5-run11
- Raw assets: `MO-Graphics-Probe-v0.5.0-alpha.5.apk` (1,118,826 bytes) and `MO-Layer-Test-v0.2.0.apk` (23,952 bytes).
- P5 adds native-vs-Java renderer comparisons, PID, explicit self-test of layer library loadability, and Shizuku read-only inspection of exact GPU debug keys and filtered layer-related logs.
- **Not yet device tested.** Most recent P4 owner evidence still layer intercepted NO; do not claim FPS unlock or hooking succeeded.
- **Critical**: the user still has not confirmed restoring GPU Debug settings in installed MO v0.4. The prior backup is stored in v0.4 private app data. User must press Restore and confirm original values BEFORE uninstalling/updating MO or installing v0.5. If restore fails, retain v0.4 and ask for exact error instead of overwriting settings.
