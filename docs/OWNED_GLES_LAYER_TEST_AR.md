# MO GLES Layer Proof — تجربة منفصلة بدون روت

المشروع يحتوي الآن على APK أساسي (MO Graphics Probe) وAPK اختبار ثانٍ (MO Layer Test) فقط.
الحزمة التجريبية: com.alwansan.mo.glesprobe. لا تستخدم نسخة من Outlanders ولا تغيّر ملفاتها.

1. نزّل APKين من GitHub Actions وثبتهما. ملاحظة: توقيعات Debug قد تتغير بين البناءات؛ احذف MO القديم قبل تثبيت الجديد فقط عندما لا توجد تجربة GPU Debug مفعلة.
2. افتح MO Layer Test قبل التفعيل، وسجّل GL_RENDERER الحقيقي. خيار Mock 120 FPS سيكون LOCKED (simulation).
3. افتح MO الأساسي مع خدمة Shizuku UID2000 GRANTED. اضغط تفعيل تجربة GLES ووافق على الرسالة.
4. MO يحفظ الأربعة مفاتيح Settings.Global المعنية (presence/value) داخل SharedPreferences قبل تعديلها. يضبط gpu_debug_app ومصدر الطبقة على com.alwansan.mo.glesprobe فقط، واسم المكتبة libMOGlesProofLayer.so.
5. أغلق تطبيق الاختبار وأعد فتحه. إذا عملت طبقة Android الرسمية، يصبح GL_RENDERER = MO-GLES-LAYER-PROOF (SIMULATED) ويظهر Mock 120 FPS: UNLOCKED (simulation).
6. ارجع إلى MO واضغط استعادة GPU Debug. إذا كانت حالة الإعدادات لم تتغير من برنامج خارجي، سيعيد كل قيمة سابقة حرفيًا بما فيها المفاتيح غير الموجودة.
7. أعد فتح التطبيق التجريبي للتأكد من رجوع Adreno 730 والقفل التجريبي.

**تنبيه:** لا تحذف MO أو تحدثه قبل الاستعادة. النسخة الاحتياطية محفوظة داخل بياناته الخاصة، والإعدادات العالمية تبقى بعد الإغلاق أو إعادة التشغيل. إذا فشلت الاستعادة انسخ النص وأرسله؛ لا تجرّب خيارات GPU عامة أو ANGLE all.

الطبقات الرسمية تُحمَّل للتطبيقات debuggable أو opt-in فحسب. هذه التجربة لا تفعّل FPS فعليًا ولا تتجاوز حماية الألعاب الإنتاجية.
المرجع: https://developer.android.com/ndk/guides/rootless-debug-gles

## Build evidence 2026-11-07
Full GitHub Actions: https://github.com/alwansan/MO/actions/runs/42310714620
Artifacts: MO-Graphics-Probe-debug-8, contains:
- app-debug.apk (MO v0.4, SHA256 74e50eece4eae020d7a9808f97552cbc5d843bf37142b8818282ab106c71b8f7)
- probe-debug.apk (MO Layer Test v0.1, SHA256 180c32ab62a06f8e26686d651ad274cf316eb2828a1e52a73a29bca98670b4d0)
Both built and ZIP integrity checked; **not yet tested** as an active GLES layer on POCO F5 Pro.
For testing, force-stop **only** MO Layer Test after enabling/restoring, then reopen to ensure new process initializes EGL loader.
