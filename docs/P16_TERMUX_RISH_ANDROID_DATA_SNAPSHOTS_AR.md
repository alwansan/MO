# MO P16 — أخذ نسختين TAR من Android/data عن طريق Rish دون روت

التاريخ: 2026-11-08 Asia/Baghdad | session `MO-20261108-CHAT-P16-RISH-028`

## سبب أخطاء المستخدم
- Termux `python: can't open file /data/data/com.termux/files/home/compare_android_data_snapshots.py`: المستخدم كان في `~` بينما سكربت المقارنة في `Download`.
- داخل `Download`: `Both ZIP snapshot paths must exist` لأن `BEFORE.zip` و`AFTER.zip` كانت **أسماء مثال**، ولم تكن موجودة فعلًا.
- داخل `sh rish`: `python` و`pkg` غير متاحين لأن Rish يدخل **Android system shell** وليس بيئة Termux. `id` عادةً يعرض UID2000، وهي صلاحية ADB Shell **وليست Root**. `cl` ليس أمرًا صحيحًا؛ لا علاقة لهذه الرسالة بالوصول للملفات.
- `/storage/emulated/0/Android/data/com.tencent.mhadv` هو تخزين اللعبة الخارجي؛ نجاح قراءة Shell **مرتبط بصلاحيات Android والأجهزة** ويجب التحقق منه أولًا. أما `/data/user/0/com.tencent.mhadv` فهي مساحة التطبيق الخاصة، ولا تمنح Rish أو Termux بصلاحيات عادية وصولًا إليها.

## التغيير المنفذ في مشروع MO
- `tools/compare_android_data_snapshots.py` أُصلح لقبول `.zip` و`.tar` و`.tar.gz` بنفس المقارنة؛ يستخدم SHA-256 تدفقي لبيانات الملفات داخل الأرشيف، لتمييز تغيّر المحتوى حتى إذا بقي حجم الملف ثابتًا. لا يستخرج ملفات أو يشغّلها ولا يكتب على ملفات اللعبة.
- `tools/test_compare_android_data_snapshots.py` — اختبارات ZIP↔TAR مع تغيّر نص بنفس الحجم، حالة الملفات المتطابقة، أرشيفات ضارة ذات مسار `../` أو اسم مكرر، ورفض ملفات ليست أرشيف.
- `.github/workflows/mo-archive-diff-tests.yml` — اختبارات Python3.11 تلقائية عند تغير الأداة.
- **CI ناجح:** https://github.com/alwansan/MO/actions/runs/42421428529 عند commit `aab7306eaa905063731136807f99412ac16d6b20`. هذا يثبت اختبار وحدات Python وصحة الصياغة، وليس إثبات أن Rish يستطيع الوصول لمجلد المستخدم على هاتفه.

## طريقة استخدام ذات مرحلتين، دون تعديل ملف اللعبة
**أولاً Termux** يحوي Python، وRish *فقط عند الحاجة* لقراءة Android/data:
```bash
cd ~/test
sh rish
```
داخل Android Shell `mondrian:/ $`:
```sh
id
ls -ld /storage/emulated/0/Android/data/com.tencent.mhadv
ls /storage/emulated/0/Android/data/com.tencent.mhadv
command -v tar
```
إذا أظهرت الاستعلامات نجاح القراءة ووجود `tar`، **أغلق اللعبة تمامًا** وابدأ النسخة الأولى:
```sh
tar -cf /storage/emulated/0/Download/MHO_BEFORE.tar -C /storage/emulated/0/Android/data com.tencent.mhadv
echo $?
ls -lh /storage/emulated/0/Download/MHO_BEFORE.tar
exit
```
لا تعتبر النسخة صحيحة إذا لم تكن النتيجة `0`، أو ظهرت `Permission denied`. قد يتطلب الأمر مساحة تخزين كبيرة؛ التحضير يكون أثناء إغلاق اللعبة. `tar` ينشئ ملفًا خارج اللعبة في Download ولا يعدل بيانات اللعبة.
افتح اللعبة، غيّر إعدادًا **مسموحًا من واجهتها الأصلية** إن وجد، وأغلقها. أعد دخول Rish ثم كرر `tar -cf ...` باسم `MHO_AFTER.tar`. إذا لم تتوفر إعدادات يمكن تغييرها، قد تُظهر النسختان اختلافات كاش وسجلات عند تشغيل اللعبة فقط؛ هذا **ليس دليلًا على إعداد GPU/FPS قابل للتعديل**.

**ثانيًا في Termux** (بعد `exit`، وليس داخل Rish):
```bash
cd /storage/emulated/0/Download
curl -fL https://raw.githubusercontent.com/alwansan/MO/main/tools/compare_android_data_snapshots.py -o compare_android_data_snapshots.py
ls -lh MHO_BEFORE.tar MHO_AFTER.tar
python compare_android_data_snapshots.py MHO_BEFORE.tar MHO_AFTER.tar --output differences.txt
python compare_android_data_snapshots.py MHO_BEFORE.tar MHO_AFTER.tar --all --output all_differences.txt
```
إذا لم تتوفر `curl` أو `python` في Termux نفسه، `pkg install curl python` (فقط بعد `exit` من Rish). هذا لا يتطلب Root.
قبل مشاركة ملف `all_differences.txt`، راجع أسماء الملفات فقد تكشف مسارات مُعرّفات؛ السكربت لا ينسخ محتويات ملفات اللعبة إلى التقرير.

## حد إثبات مقصود
- إن ظهر ملف مختلف بعد تغيير إعداد لعبة متاح، فقد حددنا **مكان تخزين محتمل**، وليس برهانًا على أنه يتحكم في حدود GPU/FPS أو يقبل ملفات من الخارج.
- إن لم يظهر ملف إعدادات، قد تُحفظ القيود في `/data/user/0` أو ضمن ملفات اللعبة المجمّعة أو لدى الخادم؛ هذا لا يخول الوصول إلى بيانات خاصة عبر Rish.
- لا تُنشئ أو تلصق `DeviceProfiles.ini` عشوائيًا، ولا تعدل PAK/UTOC/Shader Cache؛ الـruntime spoof عبر KernelSU/Zygisk مختلف تمامًا عن تعديل Android/data.
- لا يثبت الفحص أن `com.tencent.mhadv` سيقرأ `Adreno840` بدل `Adreno730`؛ الهدف الرئيسي لمشروع MO ما زال غير مثبت على النسخة الأصلية بلا Root.
