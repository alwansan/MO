# MO memory — 2026-11-07

## طلب المالك
إنشاء تطبيق Android مفتوح التطوير في alwansan/MO يعمل بواجهة Shizuku على جهاز POCO F5 Pro بدون روت أو فك بوتلودر؛ فهم iUnlockerGL وKernelSU؛ بناء خطة كاملة وملفات جلسات قابلة للاستعادة. أولوية هذه الجلسة التخطيط فقط لا البناء.

## بيئة الاختبار
- Screenshots: KernelSU Manager v3.3.1 (32680-5), state 'Not installed'; kernel text 5.10.236-android12-9-00003..., model POCO F5 Pro; fingerprint for Android 15 HyperOS OS3.0.5.0.VMNMIXM.
- يجب إعادة تحقق build/API/graphics drivers على الجهاز عند الاختبارات، وعدم اعتبار الصور دليلًا على أن الخدمة Shizuku فعالة الآن.
- المستخدم لديه Shizuku، لا PC؛ يمكن تطوير/بناء لاحقًا عبر GitHub Actions. لا تغييرات للنظام.

## حقائق تقنية حاسمة
- Root module ZIP يحوي Zygisk libraries، installer and boot scripts، `iUnlockerGL.apk`. لا تنفذ السكربتات.
- KernelSU Manager APK لا يجعل النواة rooted. KernelSU الرسمي يحتاج bootloader unlocked للتثبيت التقليدي.
- Shizuku ADB = UID 2000 shell؛ ليس UID 0. يمكنه الوصول لواجهات/إعدادات مسموحة ولا يمكنه فعل Magisk mount/Zygisk/kernel patch.
- ANGLE per-app عبر `settings global` قد يكون تجربة رسمية على Android 15، **حسب وجود ANGLE وسياسات OEM**؛ لا يعادل GPU spoofing؛ واجب حفظ واستعادة قيم الإعدادات بدقة.
- iUnlockerGL LICENSE يمنع reverse engineering/modify/derivatives؛ لا ترحيل شيفرة إلى MO. KernelSU license GPLv3، ويمكن الإحالة إليه دون نسخة.

## قرار معماري
MO مستقل: ZIP inspector + safe APK extraction + package install via documented Android flows + Shizuku diagnostics + opt-in ANGLE and restore + exportable test report. No 'root module installer' labeling.

## ما تحقق
فحص محلي لأسماء وسكربتات وحدة iUnlockerGL وحجمها وبصماتها، وملف KernelSU APK المرفوع، ومراجعة المصادر الرسمية. لم يُثبت شيء على هاتف المستخدم، ولم ينفذ ZIP، ولا اختبارات Android device أو CI.

## التواصل
العربية أولًا. لا تطلب تفاصيل سبق إثباتها إلا عندما يلزم اختبار جديد فعلًا. اقرأ STATUS وSESSION logs معًا؛ حالة التخطيط ليست تصريحًا للبناء.
