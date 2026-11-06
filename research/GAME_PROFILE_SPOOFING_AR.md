# MO — بحث متخصص: تزييف هوية الجهاز لفتح إعدادات الألعاب
**تاريخ القراءة 2026-11-07 | session=MO-20261107-CHAT-GAME-PROFILES-004 | بحث فقط، لا تجارب على الهاتف**

## تعريف الهدف الحقيقي
المطلوب من المستخدم ليس تبديل درايفر GPU ولا تسريع أداء الشريحة؛ بل أن تسمح PUBG Mobile وMonster Hunter Now وألعاب مشابهة بإعدادات FPS/graphics مخفية عندما ترى هوية جهاز/معالج/GPU مدعومة. الشرط الصارم: POCO F5 Pro / Android 15 / locked bootloader / **بدون root** / Shizuku عبر wireless debugging.

إثبات توفر خيار في قائمة اللعبة يختلف عن قياس FPS فعلي، وكلاهما يختلف عن دعم العتاد والاستقرار.

## أين تقرأ اللعبة هويتها؟
1. Java: `Build.MODEL`, `Build.DEVICE`, `Build.BRAND`, `Build.MANUFACTURER`, `Build.FINGERPRINT`; Xposed can change static `Build` fields **inside a scoped process** after entering it.
2. Native: `__system_property_get`, `__system_property_read_callback`; reads `ro.product.*`, `ro.hardware`, `ro.soc.*` if available. Spoofing Java Build alone does not automatically change native values.
3. OpenGL ES: `glGetString(GL_VENDOR|GL_RENDERER|GL_VERSION)` and GL extensions/limits. These are returned by graphics userspace drivers and **not writable with a Shizuku settings command**. Official source: https://github.com/KhronosGroup/OpenGL-Refpages/blob/main/es3/glGetString.xml .
4. Vulkan: `vkGetPhysicalDeviceProperties` exposes `deviceName`, vendor/device IDs, driver/API versions and other capabilities. Requires a proper in-process hook or cooperative layer to alter reported results. Do not fabricate incompatible extension/feature support.
5. CPU: `/proc/cpuinfo`, system properties, hardware feature queries; game engines may choose tiers differently.
6. UI display: physical/supported refresh rate, modes and current rate (120 Hz screen ≠ 120 rendered FPS).
7. Game-owned data: UE4/Unity DeviceProfiles, config `Engine.ini`, `UserCustom.ini`, `Active.sav`, `SettingConfig_C.sav`; game version may reject/reset overrides.
8. Online: remote allowlist, account-side/region/feature flags and anti-tamper checks; not controllable via Android settings and may change by version.

**No single global Shizuku command updates all eight paths.** A game using just Build.MODEL might be influenced by a single in-process change; a game using native GL or server validation will not.

## Directly related upstreams, evidence after reading live source
| Repo/product | Root requirement | How it *really* acts | Direct relevance and confidence |
|---|---|---|---|
| iUnlockerGL https://github.com/i-Taylo/iUnlockerGL | Root + Magisk/KernelSU Zygisk | Root-installed native Zygisk lib and companion APK; advertises GLES/Vulkan/Build/CPU | High relevance, does not run via shell UID2000; license forbids modification/reverse-engineering/derivatives |
| COPG https://github.com/AlirezaParsi/COPG | Root + ZygiskNext/ReZygisk/NeoZygisk | Per-app Build/CPU/GL/Vulkan profiles and native hooks, GPU spoof PRO | Highest reference for *feature surface*; claims of game success not independently tested; some features proprietary/paid |
| Game-Unlocker https://github.com/rushiranpise/Game-Unlocker | LSPosed framework/root in normal setup | `GAMEUNLOCKER.java` implements `IXposedHookLoadPackage`, per-game package list then **reflection on Build fields**. PUBG package `com.tencent.ig` mapped to OnePlus 8 Pro, CODM another profile etc. | Excellent small, easily audited example of **Java-only per-app model spoof**. **Not proof that GL_RENDERER was replaced**. MIT license |
| GameUnlocker https://github.com/yadavnikhil03/GameUnlocker | Magisk/KSU/Zygisk per README; also Xposed hooks in source | README advertises native property and GL interception; source `GameUnlockerXposed.kt` hooks system_server GameManagerService etc.; `FrameRateHook.kt` hooks Surface frame rate and ADPF hints | Research interesting but **mixed implementation**: don't equate Xposed service hooks with working GPU spoof; needs reproducible proof |
| ULTRA-EXTREME-UNLOCKER https://github.com/shakilxt/ULTRA-EXTREME-UNLOCKER | Root Magisk/KSU | Actual `system.prop` lists only `ro.product.{model,odm.model,system.model,vendor.model,system_ext.model}=NX789J` | Strong example of **prop-only global spoof**, not actual GL/Vulkan changes. Small code, root boot module |
| PixelSpoof https://github.com/klab7/PixelSpoof | Xposed/LSPosed | Device `Build`/prop identity for scoped apps | General-purpose Java/system identity spoof; no evidence of GPU native intercept |
| SpoofMyDevice https://modules.lsposed.org/module/com.spoofmydevice/ | LSPosed/root | App-scoped Build/system property/telephony/screen values | Reference for profile selection; not OpenGL GPU runtime |
| CatSmoker https://github.com/catsmoker/catsmoker-app | Shizuku config mode; LSPosed root native mode | Game file manipulation/Gamemode without root; internal native hooks incl glGetString with LSPosed | Crucial to separate two distinct backends; see docs/SPOOF_DEVICE.md |
| KaosGFX https://github.com/ulolol/Ka0SGFX | Shizuku APK patch mode, root unmodified APK mode | Rootless: edits APK `AppComponentFactory`, injects DEX/native hooks, re-signs splits, Shizuku stages/installs | Relevant to an **owned APK proof**, not arbitrary original unmodified game. Data loss on signature mismatch path |
| GFX Tool by tsoml https://play.google.com/store/apps/details?id=eu.tsoml.graphicssettings | Normal app | Marketed for graphics/FPS settings; specific proprietary implementation unverified | A real mainstream reference; don't assume it spoofs model/GPU; may use game settings |
| BG GFX Tool https://github.com/princezakirx/battlegrounds_gfx | Shizuku or SAF | `MainActivity2.java` loads `Active_60.sav`, `Active_90.sav`, `Active_120.sav`; copies/deletes game config via Shizuku shell | **Config overwrite**, not GPU spoof; can break saves or violate game rules. Shizuku backend file access may be OEM restricted |
| BGMI GFX Tool Pro https://github.com/Proprince11/gfxtool | Shizuku shell, claims nonroot HyperOS | `GfxConfig.kt` creates `Engine.ini`+FPS CVars and `UserCustom.ini`, plus prebuilt sav; `FileWriter.kt` tries multiple Android/data paths | Best open code example of **config-tier override**, not glGetString. `/mnt/pass_through/0` access on user's firmware **unproven** |
| PGT+ (Pro Graphics Toolkit) https://shizukuapps.com/apps/pgt-pro-graphics-toolkit/ | Nonroot claims Shizuku | Aggregator claims applies /Android/data config | App source/integration **not independently verified**; low-confidence until first-party repo source |
| LSPatch https://github.com/LSPosed/LSPatch | Repacked APK can run without root | Re-sign application with embedded Xposed hooks | Can affect instrumented copy; *not* an invisible injection of the already installed signed game |
| libadrenotools https://github.com/bylaws/libadrenotools | No root for cooperating target | Application explicitly chooses a custom Vulkan driver | This is **driver swapping**, distinct from fake device identity; needs target integration |
| GAMA https://github.com/palincat/gama | Shizuku | `setprop debug.hwui.renderer` to select Android UI HWUI/Skia | Does not modify GPU device names queried by games |
| akiHz https://github.com/anlaki-py/akihz | Shizuku | Display refresh-rate selection and FPS counter | Supports measurement/120Hz, but does not unlock game FPS policy |

## Source-pinned snapshots for follow-up
- rushiranpise/Game-Unlocker `aefdef8bd1d28a25293ad7e59226b264ff9fbe07` (2024-03-30) — https://github.com/rushiranpise/Game-Unlocker/blob/main/app/src/main/java/io/github/rushiranpise/gameunlocker/GAMEUNLOCKER.java
- yadavnikhil03/GameUnlocker `1bffe6f231ac24f69f415feebb8e63e958b3efe3` (2026-09-30) — https://github.com/yadavnikhil03/GameUnlocker/blob/main/app/src/main/java/com/yadavnikhil03/gameunlocker/xposed/GameUnlockerXposed.kt
- shakilxt/ULTRA-EXTREME-UNLOCKER `25af9c290b9fe5bb290ca33eda7e8f3761c8df77` (2026-10-22) — https://github.com/shakilxt/ULTRA-EXTREME-UNLOCKER/blob/main/system.prop
- princezakirx/battlegrounds_gfx `94f775a8947725f466fd97d197d02727d58665d2` (2026-09-01) — https://github.com/princezakirx/battlegrounds_gfx/blob/main/app/src/main/java/com/BattleGrounds/GfxTool/MainActivity2.java
- Proprince11/gfxtool `800e75a5abc1d67d8813a9f15db16f7f32557707` (2026-07-17) — https://github.com/Proprince11/gfxtool/blob/main/app/src/main/java/com/bgmigfxtool/pro/GfxConfig.kt
- COPG branch JSON `a029ee8742d095977dfd4be311ce5938c9a9cf1c` (2026-11-05) — https://github.com/AlirezaParsi/COPG

## Explicit non-root feasibility
- Without repack/consent: Shizuku can toggle supported settings and perhaps write **permitted** game config paths, not replace Java Build fields or fake GL/VK responses inside unrelated process.
- With **owned/repackaged** test APK: bootstrap instrumentation possible, requires signature change and separate app lifecycle. Do not automatically patch/remove original PUBG or Monster Hunter.
- With official GLES/VK layers: only debuggable/manifest-opted-in apps; does not open arbitrary closed game.
- Root/Zygisk/LSPosed: would meet direct goal technically but prohibited by owner.
- OEM support and game updates are version-specific. No independent device test has proven any game tier unlock.

## Risks and policy
PUBG Mobile help says externally modified client/app files may trigger **permanent account ban**: https://pubgmobile.helpshift.com/hc/en/3-pubg-mobile/faq/18-what-happens-to-player-who-violate-rules-use-of-illegal-software-abnormal-behavior-etc/
Therefore MO's first tests must use a self-owned, fully disposable test app (and fake game-tier allowlist), **not** a live online game account, tampered game binary, integrity bypass, or anti-cheat evasion. No copy of third-party proprietary source into public MO.
