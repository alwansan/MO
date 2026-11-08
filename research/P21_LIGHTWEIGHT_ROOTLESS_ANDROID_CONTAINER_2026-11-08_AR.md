# MO P21 — بحث المحاكي/الحاوية الأخف على Android 15 لتجربة Monster Hunter Outlanders
**التاريخ:** 2026-11-08 Asia/Baghdad · **الجلسة:** MO-20261108-CHAT-P21-LIGHT-CONTAINERS-034 · **origin:** chat · **حالة العمل:** Research only; لا APK ولا تعديل لعبة.
**قاعدة المالك:** POCO F5 Pro (mondrian; Snapdragon 8+ Gen1 SM8475, Adreno 730, Android 15, arm64), لعبة Global `com.tencent.mhadv`، لا Root، لا Unlock، لا VMOS+/الأنظمة الثقيلة، لا حذف النسخة الأصلية، لا نسخ iUnlockerGL.

## السؤال المعماري الرئيسي
- **Launcher عادي:** يرسل Intent لتطبيق اللعبة المثبت؛ لا يملك صلاحية تغيير `Build.MODEL` أو `glGetString(GL_RENDERER)` أو Vulkan في عملية لعبة أخرى. ثقله ضئيل لكنه لا يحل الهدف.
- **App virtualization container** مثل BlackBox/Renjana/NEXTVM: يعمل داخل Android المضيف مع virtual package manager / class loaders / binder hooks / native library interception. لا يقلع ROM كامل، لذلك *نظريًا* كلفة الإقلاع والذاكرة أقل بكثير من VM كاملة ويظل GPU الحقيقي متاحًا إذا نجحت عملية اللعبة. لا يوجد قياس أداء موثوق على جهاز المالك؛ cannot claim zero overhead, near-native FPS or game compatibility. Native Unreal + Google login + Play Integrity + split APK + EGL/Vulkan namespaces + native plugins هي أصعب الحالات.
- **Android system-in-app container** مثل Twoyi/Ananbox: يقلع user-space Android مع Binder/Graphics وPRoot غالبًا؛ ذاكرة وخدمات إضافية واستقرار GPU أصعب، لكن يمكن تغيير إعدادات نظام guest أكثر. ليس Virtual Machine مدعومة من EL2 على stock POCO.
- **Full hypervisor VM** مثل DroidVM: يعتمد Root و/dev/kvm/gunyah، إضافة إلى متطلبات GPU وعمر الشريحة، خارج القيود.
- **Shelter work profile:** تطبيق خفيف ومستقر يستخدم Android نفسه، Google system services المسموحة، لكنه لا يغيّر GPU/Build الخاصة بتطبيق جديد، لذلك لا يحقق هدف fake Adreno.

## مرشحون من مستودعات GitHub الحية، لا من صفحات تسويق وحدها
| المشروع | الفئة | Root؟ | الأداء النسبي المتوقع، غير مقاس | Android 15 / تشغيل MHO | تغيير Build vs GL/Vulkan | حالة المصادر والتثبيت |
| --- | --- | --- | --- | --- | --- | --- |
| [Renjana](https://github.com/josskixg/renjana) | App container, Pine hooks, stub Activity, split APK, GMS abstraction | لا | من الأخف نظريًا | غير مثبت في لعبة MHO؛ Android15 مذكور في PROBLEM.md | Device identifier/Java hooks؛ لا `glGetString` / `vkGetPhysicalDeviceProperties` hook في CoreHooks/PineHookManager الذي راجعناه | Apache-2.0؛ آخر push 2026-08-25؛ **لا GitHub Releases ولا APK عام ولا successful CI runs**، issue #1 و#2 يسألان عن APK. يحتاج build موثوق ولا تحفظ حساب Google فيه قبل تدقيق الأمان. `InstanceLauncher.kt` يصرّح fallback direct launch **NO isolation** عند فشل Stub/Pine، لهذا قد يظهر التطبيق شغّالاً دون أي spoof. |
| [NEXTVM](https://github.com/TanvirHossain2/NEXTVM) | App container متعدد الوحدات + libc native hooks | لا | خفيف نظريًا، تعقيد عالٍ | غير مثبت. PR مفتوح لخطأ guest launcher؛ CI runs فشل/Action required | `IdentitySpoofingEngine.kt` يغيّر Java `Build.*` و`native-hook.cpp` يهوك `__system_property_get` وI/O؛ **لا native GL/Vulkan interception** في الملف الذي راجعناه | Apache-2.0؛ pushed 2026-03-10؛ لا public Releases، docs تعترف بمشاكل compilation. لا تنصّب APK مجهولًا. |
| [BlackBoxReborn](https://github.com/fdavids77/BlackBoxReborn) | App container / multi-instance | لا | خفيف نظريًا | **مالك MO جربه على POCO ولم يشغّل Outlanders**. سجل سابق: package installed but `getLaunchIntentForPackage` null. هذه ليست برهان تعطل GPU. | hooks متنوعة؛ لا إثبات spoof Vulkan/GL داخل اللعبة | Apache-2.0؛ updated source 2026-08-14. مؤجل بطلب المالك |
| [Twoyi](https://github.com/twoyi/twoyi) | User-space Android OS in Android | لا | أثقل من simple app container | يدعي Android host 8.1–12 فقط؛ **أرشف 2023-04-20 وانتهى التطوير**؛ عدم إثبات Android15 أو MHO | guest system identity customization نظريًا، لا إثبات guest GPU spoof يشغّل Outlanders | MPL-2.0؛ ROM لا يمكن بناؤها كلها من المصدر بحسب README؛ لا توصي به لهاتفك |
| [Ananbox](https://github.com/Ananbox/ananbox) | rootless Anbox fork via PRoot, guest ROM | لا | أعلى من app container، overhead user-space system وPRoot | README says WIP, Binder security incomplete, graphics bugs؛ **issue #13 Android15 black screen** | guest ROM قابلة للتعديل، **لا إثبات GLES/Vulkan pass-through أو MHO gaming** | GitHub v0.0.2 2026-03-17: app-debug.apk 11,662,711B + rootfs_arm64.7z 189,561,958B؛ ليس "app very very light" ولا production-ready |
| [Shelter](https://github.com/PeterCxy/Shelter) | Work profile of stock Android | لا | أخف وآمن نسبيًا | apps use normal device framework but separate profile; installing a second game can require storage/login, no fps claim | **no Build or GL spoof** | GPL-3.0، published F-Droid, good app isolation alternative but not unlock solution |
| [DroidVM](https://github.com/Droid-VM/DroidVM) | Linux/KVM/Gunyah hypervisor | **نعم** | near-native only if hardware backend available, not on stock constraints | requires Snapdragon 8 Gen 3/Gunyah or /dev/kvm & root, host SM8475 excluded | Full guest possible but no native API GPU identity spoof guarantee | GPL-3.0؛ rejected |
| [ZCore enhanced BlackBox](https://github.com/ZENINXOP/BlackBox) | derivative virtual engine | no root claimed | unknown | public free version claims only Android5–14, Android15 limited, full support in **paid proprietary** edition | source availability incomplete | public repo README/ZIP binary only; not good auditable full OSS solution |

Other:
- VirtualXposed https://github.com/android-hacker/VirtualXposed — older release Android12 (2022), Android15 large ARM64 Unreal game unverified; GPL-3.0.
- VirtualApp https://github.com/asLody/VirtualApp — core historically open but support up to Android14 claimed in commercial/business version. Free branch is old; unsuitable Android15 without major fork.
- DroidNative https://github.com/DroidNative/DroidNativeApp — Linux desktop apps on Android via PRoot/Mesa; **not Android APK runner**; irrelevant even though GPU and no root advertised.
- Android Virtualization Framework / Microdroid: official AOSP `android.system.virtualmachine` API is SystemApi and MANAGE_VIRTUAL_MACHINE restricted, guest for payload code, **not a graphics-complete stock Android game VM**. ADB dev-grant language does not imply any Play game guest graphics pathway.

## Primary-source evidence (specific paths)
- Renjana `PROBLEM.md` explicitly lists unresolved/previous classloading/Android14/15 ActivityInfo/Split APK issues.
  https://github.com/josskixg/renjana/blob/main/PROBLEM.md
- Renjana `app/src/main/java/com/fesu/renjana/core/InstanceLauncher.kt` comments and code: StubActivity with isolation first; falls back to `getLaunchIntentForPackage` **NO isolation** when unavailable. This fallback is NOT success in overriding device model/GPU.
  https://github.com/josskixg/renjana/blob/main/app/src/main/java/com/fesu/renjana/core/InstanceLauncher.kt
- Renjana `.../hooks/DeviceFingerprint.kt` generates Android ID, serial, IMEI and network identifiers; not actual GPU backend. `CoreHooks.kt`, `PineHookManager.kt` did not expose native OpenGL/Vulkan API replacement in source inspected.
- NEXTVM `core/hook/src/main/cpp/native-hook.cpp`: I/O redirects, `__system_property_get`, GOT/PLT; not glGetString or VkPhysicalDeviceProperties. `IdentitySpoofingEngine.kt`: Build fields. `NativeHookBridge.kt` comments that native interception is phased and some Java-only hooks can't cover native until implemented. `core/apk/.../VirtualClassLoader.kt` includes split path/namespace handling; risky Android15 compatibility with native library linking.
  https://github.com/TanvirHossain2/NEXTVM/blob/main/core/hook/src/main/cpp/native-hook.cpp
  https://github.com/TanvirHossain2/NEXTVM/blob/main/core/hook/src/main/java/com/nextvm/core/hook/IdentitySpoofingEngine.kt
- NEXTVM GitHub Actions latest available: run 38006365277 `action_required` Oct9 2026; run 36251947085 `failure` Sept26 2026. Open PR #5 "Fix guest app launch: ATM proxy, ABI-safe natives, and launcher resolution", not known merged. Don't pretend repo compiles/works.
- Renjana GitHub Releases API returns zero releases; Actions zero runs; github issues #1 and #2 ask APK. No immediately downloadable, verified OSS installation ready for user.
- Ananbox v0.0.2 hosted at https://github.com/Ananbox/ananbox/releases/tag/v0.0.2 ; issue https://github.com/Ananbox/ananbox/issues/13 "Black screen on Android 15".
- Twoyi README explicitly discontinued, ROM not fully compilable, supported host 8.1–12: https://github.com/twoyi/twoyi.
- AVF permission official AOSP: https://android.googleid.googlesource.com/platform/packages/modules/Virtualization/+/HEAD/libs/framework-virtualization/README.md
- Shelter https://github.com/PeterCxy/Shelter; DroidVM https://github.com/Droid-VM/DroidVM

## Risk & acceptance gates before a real game experiment
1. **Do not uninstall original com.tencent.mhadv**, do not reinstall/re-sign it, do not delete data/caches, do not copy account credentials. No internet-required live login into untrusted fork without security audit; open source is not automatically secure.
2. Bench/run own disposible `com.alwansan.mo.glesprobe` or other licensed 64-bit EGL/Vulkan reader *inside* one candidate container first; prove it starts with host GPU without black screen and measure app RSS/PSS and frame pacing with/without container. Run same APK baseline on host before/after; note loader may fall back to direct external app launch without isolation.
3. Only test toggles if `Build.MODEL` + Java GLES + Native GLES + EGL dlsym + Vulkan VkPhysicalDeviceProperties` **within guest** have verified capability boundaries. Container handles Java Build only by default; cannot assert fake Vulkan 1.3.243 or Adreno840 effective. Fake capability beyond actual Adreno730 GPU may crash shader/renderer.
4. For gaming, check split APK + native ARM64 libs + Android NativeActivity/Unreal + launch intent + Google Play login/integrity + RAM/thermal/FPS. Logs scrub account identifiers. APK archives can contain proprietary assets; don't publish them.
5. A virtual guest can still violate game policy, introduce ban/anti-virtualization risk; avoid production account testing before documented authorization and verifiable stability.

## Recommendation / time allocation
**Not found:** any reproducibly verified, fully open-source, no-root, near-zero-overhead Android15 VM/container already known to run *Outlanders* and spoof both GLES and Vulkan GPU strings in its game process.
- If owner accepts **experimental engineering**, scrutinize/build **Renjana** first (most lightweight classloader-based proof) purely with our own test APK; verify launch actually stays in host stub rather than direct fallback, then inspect native API path. Do **not** install login/game yet. NEXTVM is a secondary engineering source for native property/hooks but CI/launcher problems mean no reliable out-of-box solution.
- If owner insists on **a ready APK now**, only Ananbox has openly published APK + ROM among these most relevant projects, but because Android15 black screen and instability make it a poor choice for phone gaming, **do not recommend it for Outlanders**. Twoyi legacy builds are not appropriate Android15.
- If minimizing overhead and staying within stock device restrictions matters most, there is no promising full Android system emulator: use normal original game and research permitted stock driver or official graphics settings instead. A normal launcher alone cannot change target process hardware identity.
- **No game changes or app build in this research session.** Source-read-only and public citations, no proprietary iUnlocker code imported.
