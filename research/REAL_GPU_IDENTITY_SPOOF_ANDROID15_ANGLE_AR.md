# MO — الهدف الحقيقي: GPU/Device Identity Spoof لتطبيقات أخرى، لا مجرد محاكاة FPS
التاريخ: 2026-11-07 | جلسة `MO-20261107-CHAT-P13-REAL-SPOOF-024` | origin=chat
الحالة: **بحث فني جديد؛ لم يحدث تزييف فعلي في لعبة مغلقة، ولم تُجرَ كتابة على الهاتف**.

## تصحيح المستخدم للهدف الأساسي
قال مالك المشروع صراحةً إنه يريد أن ترى التطبيقات/الألعاب مواصفات GPU وهاتف أعلى من الحقيقي، مثل أن تقرأ `Adreno 840` بدل `Adreno (TM) 730`، **على أمل** فتح إعدادات Graphics/FPS التي تعتمد على قوائم توافق أو تعريف أجهزة. الفحوص P10/P11/P12 تظل أدوات تشخيص/إثبات/تراجع فقط، **وليست إنجازًا للهدف الرئيسي**. يجب ألا تُعطى أولوية فوق محاولة الوصول إلى قناة تزييف حقيقية في نطاق الصلاحيات المسموح.

## معيار نجاح الهدف الفعلي
1. **Observation boundary:** تطبيق مستهدف **مختلف عن MO** ويملك المستخدم إذن اختباره، يرى داخل عملية التطبيق نفسه نص GPU بديلًا من API فعلية (مثلاً `glGetString(GL_RENDERER)`). مجرد `MO.mock_combined120=true` أو تغيير قيمة في TextView أو shell ليس نجاحًا.
2. **Surface breadth:** تحديد كل قناة من Java Build.MODEL / system properties / OpenGL ES GL_VENDOR/GL_RENDERER / Vulkan VkPhysicalDeviceProperties (deviceName, IDs, API/version/features) على حدة؛ نختبر صراحة ما تغيّر وما لم يتغيّر. لا نخترع capabilities، extensions أو compute features لا تدعمها الأجهزة.
3. **App behavior:** إن كان التطبيق المستهدف يعتمد على تعريفات GPU لفتح الخيارات، يتغير الخيار **داخل تطبيقه** وليس قائمة MOCK داخل MO. لا نتعهد أن ذلك سيحدث؛ قد يعتمد على server whitelist / game profile / native device IDs / Vulkan / device model أو غيرها.
4. **Performance:** خيار واجهة 120 FPS ليس دليلًا على 120 FPS فعلي ولا تحديث الشاشة 120Hz هو معدل اللعبة. نحتاج قياسًا منفصلًا فقط بعد إثبات تغير قراءة التطبيق للهوية.
5. **Safety:** لا روت، لا Bootloader Unlock، لا Zygisk/Magisk، لا VM، لا إعادة توقيع/تعديل APK لعبة أو تعطيل مكافحة الغش؛ Shizuku يعمل عبر ADB UID 2000 بلا صلاحية دخول عملية غير debuggable. أية إعدادات نظام قابلة للتراجع بموافقة صريحة وقراءة before/after؛ لا تجربة جديدة على Outlanders دون دليل آلية قابل للعمل.

## الاكتشاف الجديد المثبت من شيفرة ANGLE الرسمية
**Google ANGLE** أضاف دعم override لسلاسل OpenGL عبر خصائص debug:
- توثيق `DebuggingTips.md` في AOSP يسرد إعداد `debug.angle.gl_vendor` و`debug.angle.gl_renderer` و`debug.angle.gl_version` لتبديل القيم التي يعلنها ANGLE للتطبيق.
  https://android.googlesource.com/platform/external/angle/+/HEAD/doc/DebuggingTips.md
- كود `src/libANGLE/Context.cpp` يحتوي ثابت `kAndroidRendererString = "debug.angle.gl_renderer"`؛ يقرأ `GetEnvironmentVarOrAndroidProperty(...)` في `Context::initRendererString()`، ويستخدم نص override غير الفارغ بدل الاسم الافتراضي.
  https://chromium.googlesource.com/angle/angle/+/refs/heads/chromium/7610/src/libANGLE/Context.cpp
- ANGLE Settings يتيح اختيار driver لكل تطبيق من `angle` أو `native` حيث تتوافر الميزة؛ الإعدادات العامة ذات الصلة `angle_gl_driver_selection_pkgs` و`angle_gl_driver_selection_values`:
  https://github.com/google/angle/blob/main/src/android_system_settings/README.md
  https://android.googlesource.com/platform/packages/modules/ANGLE/+/51ed50098f5f67d70778e6d91da2963647654d64/Readme.md

**الاستنتاج الفني المحدود:** إذا كان ANGLE يعمل كـGLES driver لتطبيق مستقل وتسمح بنية النظام له بقراءة override property، فمن الممكن **من حيث تصميم ANGLE** أن تعرض `glGetString(GL_RENDERER)` نصًا غير GPU الحقيقي **داخل ذلك التطبيق فعلاً**. هذا أكثر ارتباطًا بهدف MO من المحاكاة الداخلية. لكنها **فرضية تنفيذ على جهاز المستخدم غير مثبتة** لأن:
- لم نثبت أن ANGLE متوفر ومفعل وقابل للاختيار على POCO F5 Pro/HyperOS Android15 لهذا التطبيق أو تلك اللعبة. قيم اختيار ANGLE السابقة في تقرير MO كانت `null` = عدم وجود اختيار **مضبوط**، وليس برهانًا على عدم وجود ANGLE.
- `setprop debug.angle.gl_renderer` قد يكون محظورًا على shell UID2000 في نسخة النظام الإنتاجية، أو يختلف تنفيذ ANGLE المثبت، وقد لا يقرأ التطبيق/اللعبة ANGLE أصلًا. التوثيق وحده لا يثبت قبول أمر الكتابة أو استخدامه في اللعبة.
- إذا كانت اللعبة ترسم عبر **Vulkan native مباشرة**، فإن خاصية ANGLE المتعلقة بـGLES `GL_RENDERER` لا تغيّر `VkPhysicalDeviceProperties.deviceName` / vendorID/deviceID، ولا تبدل Java Build.MODEL.
- حتى النص الذي يقرؤه التطبيق لا يضيف وحدات معالجة حقيقية أو extensions أو تحسينًا تلقائيًا لعتاد GPU.

## القيود الرسمية المقابلة
- Android NDK `rootless-debug-gles` يجيز الطبقات الخارجية عند debuggable أو explicit manifest `com.android.graphics.injectLayers.enable=true` أو دعم userdebug/root. Outlanders `com.tencent.mhadv` الموثقة: targetSdk36 و`Debuggable NO` و`External GLES layer opt-in NO`. إعدادات layer target بواسطة Shizuku لا تتجاوز هذا الشرط.
  https://developer.android.com/ndk/guides/rootless-debug-gles
- AOSP Vulkan loader يمنع تحميل كود خارجي داخل عملية غير debuggable على production nonrooted device؛ Vulkan layer خارج اللعبة لا يقدم حلاً عامًا.
  https://source.android.com/docs/core/graphics/implement-vulkan
- Shizuku UserService عند backend ADB تعمل `UID 2000`، لا تتمتع بـroot ولا يمكنها قراءة ملفات العمليات الأخرى الخاصة.
  https://github.com/skyskym30/Shizuku/blob/master/README.md
- ZygiskNext / ReZygisk / NeoZygisk / COPG تعتمد على root/bootstrap، وiUnlockerGL محمي بترخيص مقيّد. راجع `research/ZYGISK_COPG_NO_ROOT_FEASIBILITY_AR.md`. لا ننسخ الشيفرة أو ملفات .so لهذه المشاريع.

## خطة اختبار عملية بأقل مخاطرة (P13 candidate, NOT YET EXECUTED)
**Gate A — read-only ANGLE environment (no global writes):**
- عبر Shizuku UID2000 في MO، إن كان التطبيق الرئيسي متاحًا ومتصلاً، افحص `pm path com.android.angle` أو أسماء حزم ANGLE المعروفة عبر PackageManager و`cmd package list packages`، `settings get global angle_gl_driver_selection_pkgs` و`...values`، `getprop debug.angle.gl_renderer`، `getprop debug.angle.gl_vendor`، `getprop ro.debuggable`؛ سجّل فقط ما يسمح به النظام، ولا تستنتج عدم وجود ANGLE من حزمة واحدة غائبة. لا تفحص/تغيّر ملفات لعبة محمية.
- تحقّق هل يوجد ANGLE نظامي مثبت أو driver switch لكل تطبيق، وليس ANGLE APK تطويريًا يحتاج root/debuggable. فرّق بين ANGLE-built-in وANGLE-debug development package.

**Gate B — actual independent-app GL surface test (requires explicit opt-in to reversible settings changes):**
- استخدم **تطبيق اختبار ثانٍ منفصل مملوكًا لنا أو تطبيقًا متعاونًا** يسجل `glGetString(GL_VENDOR/GL_RENDERER)` من داخل عمليته قبل وبعد، ولا يقرأ أو يعتمد على UI MOCK داخل MO. فحص حقيقي عبر JNI + GL، لا مجرد string spoof للواجهة. اعمل snapshot قبل أي كتابة على خيارات ANGLE أو `debug.angle.gl_renderer`، فحص حالة الدعم والصلاحيات، توقف فوري إذا الكتابة رُفضت، ثم إعادة تشغيل العملية وإثبات reader.
- إذا قُبلت الخاصية وأصبح ANGLE driver فعليًا، افحص ما يقرؤه التطبيق في GL مع مقارنة Java Build وVulkan الحقيقي؛ **استعد إعدادات ANGLE والخاصية إلى قيمها السابقة بدقة** واثبت عودة Adreno. لا تترك global debug properties معدلة.
- **لا تنفذ هذا الاختبار في `com.tencent.mhadv`** أو أي لعبة مغلقة أولًا. قد تكون محمية بمكافحة الغش أو تعتمد على Vulkan.
- إذا لم يتوفر ANGLE النظامي أو لم يقبل shell الخاصية أو لم يُستخدم driver في التطبيق، اعتبر هذا المسار غير متاح **على هذا الجهاز/التهيئة** بدل إصدار زر وهمي.

**Gate C — target app decision only after A/B proof:**
- لا تعرض زر "Adreno840 لألعابك" إلا إذا امتلكنا دليلًا صارمًا على أن هدفًا موافقًا يرى التبديل من داخل عمليته، وأن آلية التطبيق تسمح به، مع rollback وفحص أثر أي تغيّر في إعدادات الرسوميات. لعبة Outlanders تحديدًا لم تُختبر بهذا السيناريو.

## الفرق بين ما أثبتناه وبين ما نبحث عنه
| MO capability | Device evidence | Relevance to main goal |
|---|---|---|
| P9 external GLES layer in owned opted-in app | Native/eglGetProcAddress may read a substitute; main rollback proven | Proof a hook can alter one surface only **inside eligible targets** |
| P10 local fake identity profiles | All 7 modes on POCO; physical values remain Adreno730 | Teaches game-style checks, **not** cross-process spoof |
| P11 frame request cadence 120/60/30 | Own callback counts replicated across 2 PIDs | Measurement tool, **not** a cross-app GPU spoof |
| P12 log export | Device persistent logs and Download export proven | Evidence acquisition, **not** a game unlock |
| **P13 ANGLE override** | **Only upstream documented code; no POCO effect proof** | Candidate genuine cross-app GLES `GL_RENDERER` string substitution **if** driver+property work |
| Native Vulkan + Build spoof to stock Outlanders | Unsupported by Shizuku/Android production sandbox today | Remains **not demonstrated** |

## Prohibition on false success claims
Do not report "Adreno840 applied", "Outlanders 120FPS unlocked", "Vulkan spoof succeeded", or "globally changed GPU" from developer setting write success alone. The proof is the *target app's own* API readback after process restart. Surface-specific, reversible and reproducible.
