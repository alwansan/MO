# دليل مجلد prompt

المجلد هو ذاكرة المشروع الطويلة، وليس ذاكرة شخصية خارج GitHub.

- `RESTORE_AR.md`: النص الوحيد الذي يكفي نسخه إلى جلسة جديدة.
- `MEMORY.md`: حقيقة المشروع والقيود والقرارات والسياق الثابت.
- `STATUS.md`: آخر حالة مؤكدة وما الذي نعمل عليه لاحقًا.
- `PLAN_AR.md`: الخطة التنفيذية المرحلية ومقاييس النجاح.
- `RESEARCH_AR.md`: الأدلة والمصادر ونتائج الفحص.
- `ARCHITECTURE.md`: تصميم المنتج والواجهات والحدود الأمنية.
- `DECISIONS.md`: سجل القرارات (ADR).
- `SOURCE_INVENTORY.md`: الروابط والإصدارات وبصمات الملفات والحقوق.
- `DEVICE_TESTS.md`: برنامج الاختبارات الواقعية والرجوع الآمن.
- `SESSION_TEMPLATE.md`: هيكل كل جلسة استكمال.
- `sessions/`: سجلات الدورات المنفصلة؛ لا تفترض أن STATUS أحدث من كل جلسة.

إذا تعارضت الملاحظات، افحص الأدلة والتاريخ وHEAD في GitHub؛ لا تدمج افتراضات مع نتائج مختبرة.

## أبحاث جديدة 2026-11-07
- `../research/ROOTLESS_ECOSYSTEM_COMPARISON_AR.md` مصفوفة المشاريع والطرق والمخاطر.
- `../research/POC_TEST_MATRIX_AR.md` تجارب إثبات التحميل والحقن المسموح دون روت.
- `../research/UPLOADED_TOOLING_INVENTORY.md` بيانات أدوات REA/JADX/Node المرفوعة (بصمات، من دون تشغيل).

- `../research/GAME_PROFILE_SPOOFING_AR.md` مقارنة تخصصية لتزييف هوية الجهاز وفتح FPS في الألعاب، مع الملفات المقروءة من المشاريع.
- `../research/POCO_GAME_COMPATIBILITY_AR.md` معلومات شريحة POCO وسياسات PUBG وMonster Hunter Now ومحددات الاختبار.

## 2026-11-07 P9 source ecosystem and device recheck
- `../research/ZYGISK_COPG_NO_ROOT_FEASIBILITY_AR.md` — live ZygiskNext/ReZygisk/NeoZygisk/COPG/iUnlockerGL, rights, architecture and feasible rootless paths.
- `../docs/DEVICE_FEEDBACK_ALPHA9_2026-11-07.md` — v0.9 final four-key cleanup PASS and post-cleanup process PID still pending.
- `sessions/MO-20261107-CHAT-ZYGISK-COPG-AUDIT-016.md` — this chat-origin research and safety evidence cycle.
