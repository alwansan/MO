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
