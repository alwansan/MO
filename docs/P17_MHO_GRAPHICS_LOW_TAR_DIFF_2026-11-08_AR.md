# MO P17 — مقارنة بيانات Outlanders بعد التحويل Medium → Low

**التاريخ:** 2026-11-08 Asia/Baghdad  
**origin:** chat  
**session:** `MO-20261108-CHAT-P17-GRAPHICS-030`  
**الأدلة الخاصة:** رفع المستخدم `differences_graphics_low.txt` و`MHO_AFTER_GRAPHICS_LOW.tar` (لا ترفع ملفات لعبة المستخدم الخام إلى المستودع العام).

## ما ثبت فعليًا
- أمر Rish `tar -cf .../MHO_AFTER_GRAPHICS_LOW.tar -C ... com.tencent.mhadv` أعاد **`TAR_EXIT=0`** وملفًا معروضًا بحجم **237M** عند `2026-11-08 01:59`. أرشيف المرجع `MHO_BEFORE.tar` حجمه 230M عند `01:43`.
- المستخدم بدأ اللعبة وغير جودة الرسوميات من Medium إلى Low بين النسختين؛ لكنه شغّل اللعبة وحدث نشاط تنزيل/Cache أثناء هذه الفترة، لذا لا يمكن عزو كل تغير للرسوميات.
- Python archive comparator نجح **على الهاتف** بالفعل (`Reading first...`, `Reading second...`, `Saved report: differences_graphics_low.txt`). **ملاحظة منهجية:** المخرجات تقارن SHA-256 لمحتويات الأرشيف فقط؛ لا تعطي المضمون النصي أو حقائق سبب التغيير.
- تقرير الملف المرفوع: `BEFORE 53 files`، `AFTER 51 files`، `CHANGES SHOWN: 18` = **6 ADDED + 8 DELETED + 4 MODIFIED**. لا يظهر ملف `*.ini` أو ملف DeviceProfiles/GameUserSettings جديد في هذه المقارنة.

## الـ18 تغيّرًا — حسب الفئة
| العملية | المسار | الحجم قبل → بعد (بايت) | ما تدعمه الأدلة |
|---|---|---|---|
| MODIFIED | `cache/RemoteConfig.config` | 146 → 146 | **محتوى مختلف**، لا يثبت أنه يتحكم في جودة الرسوميات أو GPU whitelist |
| MODIFIED | `files/RHICache/ProgramBinaryCache/GLSL_ES3_1_ANDROID_.../PrecacheBinaries` | 55,635,081 → 55,635,081 | كاش OpenGL ES تغير محتواه، **ليس ملف إعدادات واضحًا** |
| DELETED × 8 | `files/MHHotRes/Android_HotResDesc_3392342.json`, `Android_HotResPak_3392342_*.pak/.ucas/.utoc` | مجموع أحجام قديمة متنوعة | حذف مجموعة تحديثات/موارد مرتبطة بالإصدار 3392342؛ السبب غير مثبت (تنظيف/تحديث محتمل) |
| ADDED × 3 | `cache/GCloudSDKLog/GCloud/GCloud_2026110801.log`, `GCloudCore/...log`, `GPMSDK/...log` | 0 → 699,877 / 58,694 / 9,023 | سجلات خدمات جديدة أثناء تشغيل اللعبة |
| ADDED | `files/GameletLog/gamelet_20261108.log` | 0 → 232 | سجل جديد |
| ADDED | `files/UnrealGame/MHMobile/MHMobile/Saved/Logs/MHMobile-backup-2026.11.07-23.20.30.log` | 0 → 9,477,983 | تدوير سجلات Unreal |
| MODIFIED | `files/UnrealGame/MHMobile/MHMobile/Saved/Logs/MHMobile.log` | 9,477,983 → 13,283,940 | سجل زاد أثناء التشغيل |
| MODIFIED | `files/centauri_log/com.tencent.mhadv/centauriLog.mmap` | 153,600 → 153,600 | محتوى سجل/ذاكرة mmap تغيّر |
| ADDED | `files/centauri_log/com.tencent.mhadv/٢٠٢٦١١٠٨_1.txt` | 0 → 25,856 | سجل جديد |

**مصدر هذا التفصيل:** الأسطر 1–28 من رفع المستخدم `differences_graphics_low.txt`. الحذف الثماني يتضمن ملف وصف JSON و 5 PAKs و UCAS و UTOC؛ لا تتعامل معها كإعدادات. العدد 53→51 يتوافق مع 6−8 = −2.

## ما لم يثبت
- إن خيار Low ينشئ ملف إعدادات في `Android/data`: **غير مثبت**؛ لا ملف config مرئي أضيف.
- إن `RemoteConfig.config` هو whitelist GPU أو يتغير بسبب Medium→Low: **غير مثبت**. الاسم وحده ليس برهانًا، وقد يتغير مع جلسة/تحديث/إعداد بعيد.
- إن تغير `PrecacheBinaries` يعني تبديل Renderer من Adreno730 إلى840 أو تمكين FPS: **غير صحيح استدلاليًا**؛ هذا Cache برامج GLES وليس مصدر GPU API.
- إن حذف HotRes يثبت تعديلاً مطلوبًا: **غير مثبت**، وقد يتسبب نسخ/تحرير موارد محذوفة بإعادة تنزيلها أو كسر محتوى اللعبة.
- أرشيف `Android/data` لا يشمل `/data/user/0/com.tencent.mhadv` ولا قواعد الخادم.
- تتيح صور المستخدم Medium وLow، و**High Field FPS بقي عليه قفل**. في صف Free Hunt/Arena كان خيار High ظاهرًا بدون قفل عند Medium، واختير عند Low؛ الصورة لا تثبت أن خيار High لم يكن متاحًا قبل تغيير الجودة.

## إمكانية فحص TAR الثنائية في هذه الدورة
جرت محاولات مباشرة بواسطة `container.exec` و`python.exec` و`python_user_visible.exec` لقراءة الملف المرفوع `/mnt/data/MHO_AFTER_GRAPHICS_LOW.tar` وفق المسار المقدم، وكلها رجعت خطأ جلسة `ClientError`. **لذلك هذا التقرير لا يدّعي قراءة المحتوى الثنائي المباشر أو تحليل الـ146 بايت في RemoteConfig**؛ يعتمد في حقائق التغيير على ملف diff الذي قدّمه المستخدم والموثّق بالسطور. يجب إعادة محاولة تحليل المحتوى عندما تعمل بيئة الملفات، أو طلب مقتطف آمن من الملف إذا احتجنا ذلك.

## أقوى تجربة تالية: عكس الاختيار مع ثبات الموارد
1. اعتبر `MHO_AFTER_GRAPHICS_LOW.tar` الحالي مرجعًا بعد تشغيل اللعبة وLow؛ **لا تستبدله**.
2. شغّل اللعبة، حوّل من **Low إلى Medium** بالطريقة الرسمية، وانتظر تطبيق الإعداد، وأغلق اللعبة تمامًا، ثم أنشئ `MHO_AFTER_GRAPHICS_MEDIUM.tar` في Download بواسطة Rish `tar -cf` وتحقق exit0.
3. قارِن `MHO_AFTER_GRAPHICS_LOW.tar` مع `MHO_AFTER_GRAPHICS_MEDIUM.tar` عبر Python Termux بأمر `--all --output LOW_TO_MEDIUM.txt`.
4. اختياري لكنه أقوى: عُد من **Medium إلى Low**، ثم التقط `MHO_AFTER_GRAPHICS_LOW2.tar`؛ وقارن `MHO_AFTER_GRAPHICS_MEDIUM.tar` مع `MHO_AFTER_GRAPHICS_LOW2.tar` في `MEDIUM_TO_LOW.txt`.
5. ابحث عن ملف يتغير **بشكل منتظم مع الاختيار في الاتجاهين** مع تقليل تغيرات logging/HotRes العارضة. الملف ذو تغيرات متكررة يصبح مرشحًا للفحص فقط، لا دليلًا على أن تعديل/حقن نص خارج سياق اللعبة مسموح أو يفتح مستويات مقفلة.
6. إذا لم يظهر مرشح إعدادات، لا تتوسع في تعديل موارد اللعبة عشوائيًا. بحث GPU الحقيقي يظل في ANGLE read-only أو واجهة لعبة مصرح بها؛ الصور السابقة لا تثبت bypass عبر Android/data.

## مصدر خارجي منفصل (غير مشتق من بيانات Outlanders)
تتحدث مجتمعات Unreal في أمثلة **مشاريع أخرى** عن `Saved/Config/Android/GameUserSettings.ini` وعن إمكان أن يكون DeviceProfile أعلى أولوية من الإعدادات المخزنة. هذا يبرر البحث عن ملف Settings *إذا وجد*، لكنه لا يثبت ظهوره في هذه اللعبة أو وصول التعديل إلى الخيارات المقفلة.
- https://forums.unrealengine.com/t/unable-to-change-game-user-settings-at-runtime-on-android/472385
- https://dev.epicgames.com/documentation/unreal-engine/customizing-device-profiles-and-scalability-in-unreal-engine-projects-for-android

## السلامة والخصوصية
لم نغيّر لعبة المستخدم أو أرشيفاته ولم نرفع بياناته الخام إلى GitHub؛ جميع التغييرات كانت في وثائق هندسية. لا نوصي بتحرير `.pak/.ucas/.utoc` أو `PrecacheBinaries` أو `RemoteConfig.config` قبل فهم بنية الملف ودليل أن اللعبة تستخدمه لأذونات جودة الرسوميات. أي ملف أو إعداد قابل للتعديل لا يساوي حقًا في تصعيد مواصفات العتاد أو يتجنب شروط اللعبة تلقائيًا.

## محاسبة حجم الأرشيف من عمليات المقارنة نفسها
- Total ADDED files' reported uncompressed sizes: **10,271,665 bytes**.
- Total DELETED HotRes' reported uncompressed sizes: **6,929,592 bytes**.
- `MHMobile.log` grew by **3,805,957 bytes** (13,283,940 − 9,477,983).
- `RemoteConfig.config`, `PrecacheBinaries` and `centauriLog.mmap` had **unchanged length**, though their content SHA256 changed.
- Net file-data change = `10,271,665 − 6,929,592 + 3,805,957 = 7,148,030 bytes` ≈ **6.817 MiB**, consistent with reported TAR 230M→237M difference. This increase is explained by new/expanded logs plus HotRes cleanup; it is **not** independent evidence of a graphics-settings file.
