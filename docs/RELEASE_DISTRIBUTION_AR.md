# النشر المباشر لملفات MO APK — GitHub Releases

تاريخ التعديل: 2026-11-07 — MO-20261107-CHAT-RELEASE-010

## المطلوب
المستخدم يريد رابط https://github.com/alwansan/MO/releases لتحميل APK مباشرة، وليس Artifacts ZIP.

## التنفيذ
- workflow: `.github/workflows/mo-android-debug.yml`
- عند Push لملفات `app/**` أو `probe/**` أو إعدادات Gradle والـworkflow، أو تشغيل يدوي، يبدأ الاختبار والبناء.
- `testDebugUnitTest` للوحدتين ثم `assembleDebug` للوحدتين شرط مسبق لأي إصدار.
- بعد البناء تنسخ الملفات بأسماء واضحة:
  - `MO-Graphics-Probe-v<appVersion>.apk`
  - `MO-Layer-Test-v<probeVersion>.apk`
- تُفحص بنية APK بـ`unzip -t` وتُحسب SHA-256، ويُذكر التجزئتان داخل وصف Release.
- ينشر GitHub CLI `gh release create` بوسم تجريبي فريد `mo-v<appVersion>-run<run_number>` عند نجاح البناء؛ الأصول المنشورة **ملفا APK فقط**، وليست ZIP أو Android App Bundle.
- عند إعادة تشغيل نفس رقم البناء، تستخدم خطوة النشر `gh release upload --clobber` و`gh release edit` لتحديث الإصدار نفسه.
- يحتاج GITHUB_TOKEN إلى `contents: write`؛ الأمر لا يحتاج GitHub PAT من المستخدم.
- يبقى الإصدار `--prerelease` لأن P4 ما زالت تجريبية، ونجاح CI ليس دليلًا على نجاح حقن طبقة GLES داخل جهاز المستخدم.
- إذا فشل النشر بسبب صلاحيات Actions، لا تُدّعى النتيجة؛ افحص سجلات Workflow وصحح الأذونات وفق إعدادات المستودع.

## التثبيت والسلامة
GitHub يضيف تلقائيًا وصلات `Source code (zip)` للإصدارات، لكن هذه ليست الملفات التي ينبغي تحميلها؛ المطلوب ملفا `.apk` في قسم Assets.

**قبل تحديث/إزالة MO:** إذا كان المستخدم قد فعّل طبقة الاختبار، يضغط زر استعادة إعدادات GPU Debug داخل MO ويتحقق من نجاح الاستعادة، لأن النسخة السابقة محفوظة في بيانات التطبيق الخاصة. لا تحذف اللعبة الأصلية أو Shizuku. توقيع Debug ما زال يتغير بين تشغيلات CI، وقد لا يدعم التحديث فوق النسخة السابقة.

الـRelease يحتوي تطبيق MO وMO Layer Test مستقلين؛ لا يعدّل أيٌّ منهما `com.tencent.mhadv` أو يفتح FPS داخلها تلقائيًا.
