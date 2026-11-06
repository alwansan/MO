# iUnlockerGL 1.1.5-r4 — قراءة بنية الحزم المرفوعة (دون تنفيذها)
2026-11-07 | MO-20261107-CHAT-STATIC-002

## الدليل العياني من الهاتف
صور المستخدم 00:43 بتوقيت بغداد: تظهر واجهة iUnlocker revision **null**، `No application added`، `ROOT: NOT GRANTED`، ورسالة `No root permission! Either your phone is not rooted or the application has no root permission`.
هذا يثبت تثبيت واجهة APK وعجز صلاحياتها الحالية عن إكمال عملها، **ولا يثبت سبب null على مستوى الاتصال بشكل قاطع**. لا دليل في الصورة على نجاح hooks أو اشتغال backend.

## فحص الأرشيفات الثلاثة
| ملف | SHA256 | الملفات الفعلية |
|---|---|---|
| release module ZIP `iUnlocker-GLTool-for-android-v1.1.5-r4.zip` | `c40a742aba1907d915881ccfb74c639387caa8669fdca234e36bb5420e9130f7` | 98 |
| upstream `iUnlockerGL-main.zip` | `e90c8d8b30d5cbc08bf2262fb13a8b1bd35856b0368c9ca5a90aaf070b5b00da` | 98 |
| upstream tag `iUnlockerGL-1.1.5-r4.tar.gz` | `65059a131d29728dbcaf1142987bffe5e7912aa2ddf44acf3a4294f6251b9620` | 98 |

ملاحظة: التحليل السابق عدّ **123 ZIP entries** تشمل أدلة/مجلدات وملفات؛ عدد **الملفات الفعلية = 98**؛ لا تعارض.

الملفات المهمة متطابقة على مستوى SHA-256 عبر الأرشيفات الثلاثة:
`module.prop`, `customize.sh`, `installer.sh`, `post-fs-data.sh`, `iUnlockerGL.apk`, `zygisk/arm64-v8a.so`.
الاختلافات المرصودة في الملفات النصية README/whatsnew وليس في APK أو Zygisk.

## هندسة التنفيذ كما تظهر من النصوص المتاحة
1. `module.prop`: declares Magisk module, versionCode 1001504, version v1.1.5-r4.
2. `customize.sh`: detects Magisk/KernelSU/APatch root environment; selects ABI; places matching `zygisk/<ABI>.so`; uses its own bash installer. Requires module install variables e.g. `MODPATH`, `TMPDIR`, `ZIPFILE`.
3. `installer.sh`: references module files and installs `iUnlockerGL.apk`; arranges backend under `/data/adb/iunlocker`; uses native helper `AppInstaller`. This root-only path cannot be recreated with Shizuku UID2000.
4. `post-fs-data.sh`: boot-time operations managing `system.prop`, optional plugin files, and per-device state under protected root/module paths.
5. `zygisk/arm64-v8a.so`: native compiled shared object for pre-/post-app process lifecycle *when* Zygisk environment is actually present. Behavioral details inside the binary are **not proven by static script analysis**.
6. `iunlocker/tools/arm64/` native utilities (`AppInstaller`, `DataParser`, `StdUnlockerHandler`, etc.); their names/filenames are evidenced, actual runtime protocols not validated.
7. `properties.h` exposes feature switches `USE_ZYGISK_LIBRARY`, symbol resolver, container modes (GHOST/LILITH). Comments are **design claims**, not proof of production runtime behavior.

## Additional installer risk found
The `NEEDED=(...)` static file list in `installer.sh` names **four absent archive paths** across all three sources:
`sepolicy.rule`, `system.prop`, `plugin_flasher.sh`, `service.sh`.
`extract()` appears to abort if listed path cannot be located; this is a potential installation blocker **even under root** unless an upstream tool generates/provides paths before extraction. Do not patch blindly or claim confirmed runtime failure without installation logs.

`updater.sh` can download `extra.sh` from upstream and then execute it in the rooted module environment. This is a security-relevant supply-chain behavior. **MO must not execute supplied scripts**, download-and-run third-party code, or implement module updater behavior.

## License/handling
Files are personal local inputs to read-only static evaluation. GitHub repo `alwansan/MO` is **public**; uploading proprietary source/binaries there is publishing regardless of intention. Their license prohibits modification/derivative use; no original code/assets uploaded. For reuse seek permission or use independently written implementations.

## Unverified
Need real-device evidence for root-call stack, backend IPC and package debuggability; not available from screenshot alone. No Android install, privileged command, or binary execution was performed.
