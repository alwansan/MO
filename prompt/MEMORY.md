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

## New evidence — MO-20261107-CHAT-STATIC-002
- User personally installed extracted iUnlocker APK. It displays revision `null` and ROOT NOT GRANTED; root error confirmed by screenshots; precise backend call not established.
- Additional source snapshots uploaded `iUnlockerGL-main.zip` and `iUnlockerGL-1.1.5-r4.tar.gz`; all 3 contain 98 actual files. Critical APK and Zygisk binary identical.
- Installer script's static NEEDED list includes four paths missing from all archives: sepolicy.rule, system.prop, plugin_flasher.sh, service.sh; potential bug, not tested installation.
- AOSP documents rootless per-app external GLES/Vulkan layers if target debuggable or target 30+ manifest opts into com.android.graphics.injectLayers.enable. Shizuku shell CAN help set debug-layer settings but does NOT override the eligibility gate. We can test with **our own** debuggable sample; don't promise external injection into stock commercial games.
- Do not enable global ANGLE for all apps: AOSP documents boot risk. Use per-app only with rollback.
- License unchanged. User says personal use but alwansan/MO is public, so copying assets there constitutes publishing. Keep analysis results and original independent tools only.
- Local static Python experiment passed 2/2 tests. No phone operations, no app built.

## Additional ecosystem findings (MO-20261107-CHAT-ECOSYSTEM-003)
- GAMA is close Shizuku precedent: `setprop debug.hwui.renderer` with readback; **changes HWUI/Skia**, not game GPU identifiers/direct Vulkan HAL.
- KaosGFX rootless mode is more relevant but it modifies APK+AppComponentFactory/native hooks, re-signs all splits, then uses Shizuku to install and stage drivers. It does **not** hook stock unmodified app. README claims Adreno 750 test; not proven on POCO 15.
- KaosGFX code auto-uninstalls on signature mismatch and only backs up /sdcard/Android/data and Android/obb, thus can destroy /data/user/0 app state. MO forbids auto-uninstall and primary game patching.
- CatSmoker Shizuku edits game files/Game Mode; native GLES/GL spoof there uses rooted LSPosed channel.
- LSPatch rootless APK repatching, original archived 2023; fork A15 exists but no guaranteed compatibility. Libadrenotools works only in target that uses it.
- VirtualXposed old, BlackBoxReborn fork experimental; neither is proof of rootless Zygisk into arbitrary protected installed game.
- Android Game Mode interventions are official if OEM supports; can adjust game FPS/downscale only, not create GPU features.
- Three uploaded analysis archives were inspected statically and are not in public MO; REA v6.8.0 MIT (npm), JADX headless Java JAR, Node 24.22.2 Linux x64 (not phone arm64).
- Reference matrix `research/ROOTLESS_ECOSYSTEM_COMPARISON_AR.md`; test plan `research/POC_TEST_MATRIX_AR.md`; tool hashes `research/UPLOADED_TOOLING_INVENTORY.md`.

## New user goal refined MO-20261107-CHAT-GAME-PROFILES-004
- Target is **unlock hidden graphics/FPS options by profile/CPU/GPU identity**, not fundamentally replace GPU drivers or just improve frame time.
- Two distinct viable approaches: per-game config overrides (Shizuku may access allowed Android/data), vs true in-process spoof (normal closed game blocked without root or repack/consent).
- Direct Game-Unlocker source (rushiranpise) only changes Java Build via Xposed; code maps PUBG com.tencent.ig -> OnePlus 8 Pro. ULTRA-EXTREME-UNLOCKER only ro.product.model variants. Proprince11 and princezakirx rootless GFX tools modify UE4 .sav/.ini files; not GL/Vulkan strings. yadavnikhil03 mixed Xposed/Zygisk marketing needs runtime verification.
- **Hardware correction:** POCO F5 Pro = Snapdragon **8+ Gen 1** (Xiaomi official), 120Hz panel; Adreno 730 from published specs. This corrects any earlier shorthand '8 Gen 1'.
- PUBG Mobile v4.4 (2026) chipset-based first-batch 120 fps Snapdragon 8 Gen 3+ plus evaluated exceptions, final availability by game; 120Hz does not force 120fps. MH Now has official 60fps hunting option and rooted devices unsupported.
- Official PUBG ban policy: unauthorized external app/game file modifications may permanently ban account; no changes to live games in tests.
- New complete files: `research/GAME_PROFILE_SPOOFING_AR.md`, `research/POCO_GAME_COMPATIBILITY_AR.md`, latest session MO-20261107-CHAT-GAME-PROFILES-004.

## MO-20261107-CHAT-BUY-VS-BUILD-005
User corrected exact title: Monster Hunter Outlanders com.tencent.mhadv, NOT Monster Hunter Now. Need decide if ready apps eliminate build. App Cloner 4.0.12 Nov6 added CPU/GPU device profile matching in 4.0.11 Nov5 but clones/re-signs apps; games may reject. VMOS+ 1.1.4 Nov1 guest Android15 OS20013 supports Magisk/ZygiskNext/LSPosed inside virtual machine; host unrooted but guest rooted, not Shizuku-only. ShizukuPlus spoof_device by code only fakes getprop ro.product/ro.build responses to ShizukuPlus newProcess clients; no original game's Build/GL/Vulkan spoof. GFX Tool tsoml and PGT+ specialize in PUBG config, no Outlanders proof. In-game MHO options include Graphics Quality and High Frame Rate, independent Field/Arena frame rate padlocks. No verified off-the-shelf no-root no-repack full solution. See research/READY_APP_DECISION_AR.md and research/MHO_OUTLANDERS_SPECIFIC_AR.md and research/SHIZUKUPLUS_SPOOF_CODE_AUDIT_AR.md.

## Implementation MO-20261107-CHAT-IMPLEMENT-006
- User switched from planning to development explicitly, disallowing VMOS/App Cloner and insisting on no root/unlock/game cloning.
- Implemented original Android Java source as `com.alwansan.mo`: Shizuku API/provider 13.1.5 UID probe, EGL14 GLES glGetString in own process, Android device identity + 4 target package versions, local report, no network/manifest storage/privileged shell commands; P1 is a probe not a game unlocker.
- AGP8.9.2/Gradle8.11.1/Java17, API35, official Shizuku deps; GitHub Actions workflow.
- First CI failed in setup-android@v3 unavailable SDK package 'tools' before source compilation; workflow changed to use hosted runner SDK.

## P1 CI result 2026-11-07
GitHub Actions first run 42305774716 failed because setup-android@v3 called missing SDK package 'tools'. Fixed via hosted Android SDK. Successful run 42305983786 commit 817960c40d97ad5216224920ff1221ccc602c9f8 built 112,063-byte app-debug.apk, SHA256 13c72cbb626e4ec6238df7e5c9262c5781b1f819487753c568c90befe2a2fba2; artifact ID 13545813166, name MO-Graphics-Probe-debug-2. Unit tests passed. APK archive locally verified, not on-device. Owner can retrieve from run and install on POCO. Next task: read user's diagnostics before claiming any game spoof.

## User device proof and P2 MO-20261107-CHAT-P2-DEVICE-007
MO v0.1.0-alpha.1 worked on POCO F5 Pro. Report: Shizuku API13 UID2000 permission GRANTED; Snapdragon8+Gen1 SM8475 Adreno730 GLES3.2 driver V@0615.95, display120Hz; Outlanders global com.tencent.mhadv version1.0.9.0.3308842. New v0.2.0-alpha.2 source adds AIDL UserService for *strict fixed read-only* GameMode and ANGLE settings, and Vulkan feature declarations. Wait for CI/P2 real-device test; no root, app clones or game edits.

## P2 CI success 2026-11-07
MO alpha2 built successfully, GitHub Actions run42307946638 artifact MO-Graphics-Probe-debug-3 ID13547166555 APK125919 bytes sha256 7a98dbbb0393256210b76249c79505df34b89e34e98c3677545cd0d6b5e23d5b. Read-only P2 app has limited Shizuku AIDL UserService and Vulkan declared capability screen; not yet tested on POCO. Direct APK shared in current chat, but future session should retrieve build artifact while retained. User P1 success evidence in docs/DEVICE_FEEDBACK_2026-11-07.md. Do not misreport FPS/quality unlocked in Outlanders.

## APK signing compatibility 2026-11-07
Android v0.1 to v0.2 debug certificates differ due to per-run GitHub Actions debug signing. v1 cert SHA256 6ea43d29063eae454fa7769c20392cd58effe4653585d79df5c5384b3ce7a2da, v2 4d2e87fa998d2c8c901c6e8f21417fbe28ce6b2f8109e35cb8dd7ef46f1be365. User must uninstall MO (only MO) before installing alpha2; no game data touched. Long-term stable signing requires private keystore via repo secret; never store signing key in public repository. Document in docs/IMPLEMENTATION_PHASES.md.

## P2 confirmed and P3 native Vulkan MO-20261107-CHAT-P3-VULKAN-008
- POCO test v0.2: Shizuku UserService worked UID2000. Outlanders com.tencent.mhadv v1.0.9.0.3308842: `cmd game list-modes` current standard available [standard,custom]; `list-configs` no interventions. ANGLE pkgs/values null, debug gpu layers 0, ro.board.platform taro, Vulkan hardware adreno, SoC SM8475. Android advertises Vulkan1.1. No actual game tier unlocked.
- v0.3 source adds actual vkEnumeratePhysicalDevices/vkGetPhysicalDeviceProperties **only in MO**, NDK native ARM64; fixes report formatting and versions. Verify CI before reporting success.
- Game Mode custom ≠ FPS unlock. ANGLE null means no driver selection, not absence of ANGLE.

## P3 CI proof 2026-11-07
Run42308837332 finished success. Artifact MO-Graphics-Probe-debug-4 (ID13547731335) includes ARM64 libmo_vulkan.so, APK1,102,410 bytes, SHA256 203b8c47d3da67cca1e6ffe212682824d67a864a556b6cd12d7717b7f14dbe92. Original C++ reads vkGetPhysicalDeviceProperties only in MO; v0.3 new sections fixed \n formatting. User has direct APK, runtime test not done yet. Earlier v0.2 read-only Shizuku UserService proven on POCO, Game Mode standard/custom no interventions, ANGLE unset. Build success does not imply FPS unlock.

## v0.3 P3 PASS and P4 probe MO-20261107-CHAT-P4-LAYER-009
Owner on-device MO v0.3 proves Vulkan Adreno730 API1.1.128 vendor0x5143 device0x7030002 driver0x8026705F; native Vulkan query works only inside MO; doesn't show Outlanders detection policy. P4 proof designed using **separate** MO-owned debuggable `com.alwansan.mo.glesprobe` app and original libMOGlesProofLayer.so intercepting glGetString(GL_RENDERER) for simulated value, explicitly fake mock 120fps option. Code doesn't change target games, root, unlock or enable system debug settings. Need build and safe per-app apply/restore controls.

## P4 v0.4 original safe mutation controller
- Shizuku UserService UID2000-only methods capture/enable/restore 4 GPU developer settings exclusively for com.alwansan.mo.glesprobe; no arbitrary shell, no user-selectable target. Captures global setting presence and value via settings list global, stores snapshot in MO SharedPreferences via synchronous commit BEFORE first write. Refuses active debugger of different app; verifies and rolls back; refuses restoring if another app modified values.
- Owned layer intercepts glGetString renderer in disposable test app. Changes must be explicitly approved. Always press restore before uninstalling/updating MO; debug CI signs each run with different key.
- No proof of actual layer intercept until user device report; absolutely not Outlanders FPS unlocked.

## P4 CI delivery success
2026-11-07 source 00558159502af56d8451ee03dac57e04d9bd34d3 built successfully run42310714620. Artifact 13547164678 contains APKs: MO v0.4.0-alpha.4 1118830 bytes SHA256 74e50eece4eae020d7a9808f97552cbc5d843bf37142b8818282ab106c71b8f7; disposable com.alwansan.mo.glesprobe v0.1.0 17463 bytes SHA256 180c32ab62a06f8e26686d651ad274cf316eb2828a1e52a73a29bca98670b4d0 with native libMOGlesProofLayer.so. AIDL Shizuku service only allows four GPU debug settings for the **owned** test package, saves baseline (presence/values) in MO prefs BEFORE changes and restores. No real device P4 test completed; not a gameplay FPS unlock. Restore before uninstall/update MO or the saved baseline is lost; debug keys still unstable per run.

## Release workflow MO-20261107-CHAT-RELEASE-010
Owner requests GitHub Releases direct APK attachments instead of GitHub Actions ZIP. Workflow changed to publish experimental prereleases on successful builds (only two APK assets, descriptive filenames, SHA256 text in release notes). Trigger by push and workflow_dispatch; GitHub CLI with contents:write. Verify actual first release, not just commit. No need for user to package ZIP. Crucial: restore owned GLES GPU Debug settings before uninstall/upgrade MO due backup stored locally and ephemeral CI debug signing.

## Release publishing confirmed — 2026-11-07
- GitHub Actions run https://github.com/alwansan/MO/actions/runs/42311901394: **completed success**. Commit `58de082ab63a797087f827633ba4dbd35fb5aa2f`.
- GitHub prerelease https://github.com/alwansan/MO/releases/tag/mo-v0.4.0-alpha.4-run9 created, id 437174044, public (draft=false), two raw APK assets, state=uploaded:
  1. `MO-Graphics-Probe-v0.4.0-alpha.4.apk` (1,118,830 bytes) https://github.com/alwansan/MO/releases/download/mo-v0.4.0-alpha.4-run9/MO-Graphics-Probe-v0.4.0-alpha.4.apk
  2. `MO-Layer-Test-v0.1.0.apk` (17,463 bytes) https://github.com/alwansan/MO/releases/download/mo-v0.4.0-alpha.4-run9/MO-Layer-Test-v0.1.0.apk
- No ZIP Release asset. GitHub automatically provides Source code ZIP/tar links; separate from APK release assets.
- On future source/workflow pushes, building both APKs and publishing a unique experimental release is automatic after CI success. Actions Artifacts ZIP step removed.
- Debug signature remains unstable: **restore P4 debug GPU settings before uninstalling/updating MO**. No actual Outlanders FPS unlock or P4 device hook proven.

## P4 device failure MO-20261107-CHAT-P4-FEEDBACK-011
User tested two APKs v0.4 on POCO. Shizuku UID2000 successfully wrote GPU Debug enable=1 and gpu_debug_app=com.alwansan.mo.glesprobe. Original `GL_RENDERER=Adreno (TM) 730` persisted inside owned debug test; `Layer intercepted: NO`; no observed mock 120fps unlock. **Status = layer not proven, diagnosis required**. Backup stored in MO private SharedPreferences; user has NOT confirmed Restore. NEVER instruct upgrading/deleting MO before pressing Restore GPU Debug and getting OK. New v0.5 source compares Java/NDK GLES same process and PID, offers explicit layer library ELF load test and Shizuku read-only filtered logcat diagnostics. Evidence docs/DEVICE_FEEDBACK_ALPHA4_2026-11-07.md and docs/GLES_LAYER_TROUBLESHOOTING_AR.md.

## v0.5 CI build verification (2026-11-07)
- Source commit `79ae93392461f03b0fe5a5d33b2c59be328a3345` from ongoing P5 diagnostic effort.
- GitHub Actions run [42312793163](https://github.com/alwansan/MO/actions/runs/42312793163): **completed SUCCESS**, all tests and builds and Release upload.
- Direct GitHub prerelease: https://github.com/alwansan/MO/releases/tag/mo-v0.5.0-alpha.5-run11
- Raw assets: `MO-Graphics-Probe-v0.5.0-alpha.5.apk` (1,118,826 bytes) and `MO-Layer-Test-v0.2.0.apk` (23,952 bytes).
- P5 adds native-vs-Java renderer comparisons, PID, explicit self-test of layer library loadability, and Shizuku read-only inspection of exact GPU debug keys and filtered layer-related logs.
- **Not yet device tested.** Most recent P4 owner evidence still layer intercepted NO; do not claim FPS unlock or hooking succeeded.
- **Critical**: the user still has not confirmed restoring GPU Debug settings in installed MO v0.4. The prior backup is stored in v0.4 private app data. User must press Restore and confirm original values BEFORE uninstalling/updating MO or installing v0.5. If restore fails, retain v0.4 and ask for exact error instead of overwriting settings.
