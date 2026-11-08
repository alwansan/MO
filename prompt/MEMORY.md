# MO memory — 2026-11-07

## P23 — first actual Renjana owned-app Android15 guest evidence (2026-11-08)
- Owner screenshots verified P22 Renjana experimental APK running MO-owned `com.alwansan.mo.glesprobe` *inside* `com.fesu.renjana:p0` PID17591 with Java `Build.MODEL=RMX3491`, while separately original package PID14458 has model `23013PC75G`. Rish ps confirms exact PIDs. Renjana original `StubActivity.kt` invokes `DeviceFingerprint.applyDeviceSpoofToProcess` conditional on enableFingerprint and `DeviceFingerprint.kt` mutates per-process Build.MODEL; UI create screenshot shows fingerprint/signature/anti-detection ON, GMS OFF. Guest **GL_RENDERER Java+native+eglGetProcAddress still Adreno730**, no confirmed Vulkan spoof/Outlanders FPS. Host original MO screenshot showed synthetic NDK GLES layer; check four global GPU keys, not inherited into Renjana guest. No account, games or system mods from this work.
- Owner `MO_RENJANA_LAB_LOG.txt` 600 lines (304111B, SHA256 eefc93541ef784c219bd3780ae003b381dc757df9aff789df3d22eb883af56eb) mostly MIUI Recents, first 03:40:47 last 03:43:17; no hook debug messages in final tail, no clear Renjana fatal crash. `logcat -d | grep | tail` can be unbounded despite tail, resulting file delayed until pipeline finishes/stops. `MO_RENJANA_MEMINFO.txt` only main PID14699 PSS77021 kB, RSS158380 kB, SwapPSS47886 kB; **not** guest p0, so guest performance unknown.
- Added original `tools/renjana_owned_lab_capture.sh` and `tools/test_renjana_owned_lab_capture.py` (2 offline mocked tests PASS, sh syntax valid); safe Rish UID2000 `logcat -t 12000` (auto-exits), exact three process types and separate meminfo by pid, read-only GPU global four-key snapshot. Writes `Download/MO_RENJANA_OWNED_CAPTURE_YYYYMMDD_HHMMSS.txt`; user must download script via raw GitHub main first; no phone run yet. Evidence `docs/DEVICE_FEEDBACK_RENJANA_P23_2026-11-08_AR.md`, `docs/P22_RENJANA_OWNED_LAB_INSTALL_AR.md`, session `prompt/sessions/MO-20261108-CHAT-P23-RENJANA-RUNTIME-036.md`. No new APK/Workflow. Next user uploads capture while guest actively running; evaluate guest PSS/GPU keys and then consider controlled native GLES/Vulkan experimentation only in owned guest, not production game.


## P22 closeout — verified Renjana experimental APK release (2026-11-08)
- User requested an actual lightweight rootless Renjana Android app install with exact Termux/Rish test commands. Created separate workflow `.github/workflows/mo-renjana-owned-lab.yml` (manually and path-push scoped). Fixed first build-only-success/checksum publish failure in commit `77867f4ec178fadfb25719692be859f1333f2300`. First run `42429358721` built/signed APK but release job failed on checksum directory; second run `42429759178` **all jobs passed**, public prerelease `renjana-owned-lab-run2`. Raw APK: `Renjana-v0.2.0-arm64-OWNED-LAB-debug.apk` 55,348,455B, SHA256 `7b62bfad247050e03bcdcefd693cb47b14d45b4c5ac945ca74fa58f092349b63`, https://github.com/alwansan/MO/releases/download/renjana-owned-lab-run2/Renjana-v0.2.0-arm64-OWNED-LAB-debug.apk . Apache2 license and provenance assets uploaded. Upstream pinned SHA `14302a57cd66114c6979acf7ca56c97f845a6841`, no thirdparty source/binaries copied into public MO repo (only workflow downloads fixed source at CI).
- Phone Android15 install/guest run, GPU Build/GLES/Vulkan measurements, Google login and MHO game compat are **not tested**. Test only `com.alwansan.mo.glesprobe` existing from MO prerelease v0.11 run59; set GMS, signature spoof, fingerprint, anti-detection **OFF** first. Critical Renjana `InstanceLauncher` can **fall back to ordinary no-isolation external launch**, so process PID and logs required; only guest sandbox proven if `com.fesu.renjana:pN` and logged stub. Instructions `docs/P22_RENJANA_OWNED_LAB_INSTALL_AR.md`, full session P22. No game changes, Root/VMOS or phone modification.


## Latest P22 — MO-20261108-CHAT-P22-RENJANA-OWNED-LAB-035
- User requested steps and APK for practical minimal overhead rootless Android container test. Renjana upstream was verified live at main commit `14302a57cd66114c6979acf7ca56c97f845a6841`, Apache2, app version0.2.0 minSdk29, targetSdk34, split arm64. No official upstream Release. 
- Added standalone, pinned-source unmodified build workflow `.github/workflows/mo-renjana-owned-lab.yml`, commit `00b40cc7b8000b23edcf67c14487e4ed0d5b01d6`. Actions run `42429358721` in progress after checkout, license check, SDK/JDK/Gradle setup; require verified build/test + actual experimental prerelease before providing a direct APK URL. No imported source, repo/core app changes or original game modifications. Test license/provenance and signed ARM64 debug APK only, no accounts.
- `docs/P22_RENJANA_OWNED_LAB_INSTALL_AR.md` gives safe user workflow: install existing own MO Layer Test v0.6.0 `mo-v0.11.0-alpha.11-run59`, baseline host PID/GLES/Vulkan, then clone **only owned test app** in Renjana with all sensitive options OFF; verify `StubActivity` sandbox launch versus `FallbackNoIsolation` direct-app open. Rish logcat filtered to `Download/MO_RENJANA_LAB_LOG.txt` if fails. P22 session under prompt/sessions.


## Latest P21 — MO-20261108-CHAT-P21-LIGHT-CONTAINERS-034 (2026-11-08)
- Goal: find minimal-overhead FOSS non-root Android-on-Android guest/container that runs MHO Global and changes guest GL/Vulkan/CPU/Build identity without VMOS+ or changing original game. Surveyed live 2026 GitHub repo source, commits, releases/CI and issues. **No ready verified solution**.
- Renjana Apache2 pushed 2026-08-25; native app-host container via Pine/StubActivity, README advertises split APK/GMS. Source `InstanceLauncher.kt` fallback launches real app directly **without sandbox or spoof**; no release APK, zero Actions runs, issues ask for binaries; no glGetString/Vulkan hook found. First R&D candidate **only for owned GL/VK probe**, not production game.
- NEXTVM Apache2 native `__system_property_get` and libc I/O GOT hooks, Java Build identity but no direct GL/VkPhysicalDeviceProperties replacement in code inspected; no releases, failed/action_required CI, launcher bug PR. BlackBoxReborn already failed on owner phone; pause.
- Twoyi archived Apr20 2023 Android host 8.1-12, ROM not buildable; Ananbox rootless PRoot guest WIP with Android15 black screen issue #13, public 11.7MB APK +190MB ROM, not lightweight enough; Shelter Work Profile native host but no spoof; DroidVM requires root/Gunyah/KVM and Snapdragon8Gen3+, excluded; ZCore Android15 complete features paid source, not fully public.
- Full `research/P21_LIGHTWEIGHT_ROOTLESS_ANDROID_CONTAINER_2026-11-08_AR.md`; `prompt/sessions/MO-20261108-CHAT-P21-LIGHT-CONTAINERS-034.md`. No APK build, original game install/data changes, root, VM or account experiments. Unverified performance and compatibility stated explicitly.


## Latest P20 — MO-20261108-CHAT-P20-REMOTECONFIG-PATTERN-033 (2026-11-08)
- User executed targeted Termux reader successfully and uploaded `REMOTE_CONFIG_PATTERN.txt`: from three MHO TARs, `cache/RemoteConfig.config` is exactly146B with distinct SHA. Both Low→Medium and Medium→Medium selected-quality control show 128 byte differences at exactly `16–79` and `82–145`; 18 positions `0–15`, `80–81` are identical. Two 64-byte variable spans/16byte header/2byte separator is purely a structural hypothesis; format unknown, cannot call it encrypted or a graphics profile.
- Since same pattern occurs with quality unchanged, data changed does NOT uniquely signify a graphics selection; no known external editable config to unlock the explicit device unsupported High/Ultra/Concept Art UI. No file patch created or warranted. Full `docs/P20_MHO_REMOTECONFIG_BYTE_PATTERN_2026-11-08_AR.md` and session `prompt/sessions/MO-20261108-CHAT-P20-REMOTECONFIG-PATTERN-033.md`. Python/container attempt to reread uploaded text returned ClientError; relied on text already loaded into chat and previously CI-tested reader.
- Goal remains making separate app **actually** see alternate GPU identity (GLES/Vulkan/Build), not tweaking a cache. Next productive evidence: user MO v0.11 P13 ANGLE read-only scan (not an unlock by itself). No further game TAR loops or app build this session.


## Latest P19 — MO-20261108-CHAT-P19-MEDIUM-CONTROL-032
- Owner verified MHO Global Graphics preset Medium stays selected, but clicking High/Ultra/Concept Art displays `This setting isn't currently supported on your device.`; top Device Load High is separate. Their Rish Medium control TAR exit0 compared with previous Medium TAR and gave **55->55 files, 11 differences**: 1 added MHMobile backup, 1 deleted old log segment, 9 modified including GCloud, Gamelet, centauri logs, `RemoteConfig.config` 146->146 with distinct SHA, shader `PrecacheBinaries` 55,691,684->55,691,684 distinct SHA, MHMobile.log. User tapped unsupported presets, so control unchanged accepted quality but still interactions.
- Across Medium->Low, Low->Medium, and Medium->Medium, RemoteConfig changed, ruling out its **change alone** as a specific graphics quality indicator. Its internal semantics not decoded. No new .ini file or proof external Android/data can unlock high graphics. User-uploaded raw TAR could not be read by container/Python (ClientError); do not pretend binary inspection succeeded.
- Created targeted safe `tools/inspect_mho_remote_config.py` to compare exact 146B member in pre-existing TAR snapshots, output only offset ranges/hashes/format hints and no private raw bytes. Added synthetic tests and CI run `42426253058` awaiting confirmation. Full `docs/P19_MHO_MEDIUM_CONTROL_2026-11-08_AR.md`; session `prompt/sessions/MO-20261108-CHAT-P19-MEDIUM-CONTROL-032.md`. User can run on existing LOW/MEDIUM/CONTROL TARs in Termux; no repeated 230MB game backups needed.
- Main objective actual game-process higher GL/Vulkan/Build identity and unlocked FPS still unproven; no root/KernelSU, game modification or app APK build this session.


## P18 owner evidence — MO-20261108-CHAT-P18-LOW-MEDIUM-031
- Latest user screenshot shows MHO Global Graphics Quality restored to **Medium** after Low, while High/Ultra graphics and higher Field FPS remain locked. Rish archived `MHO_AFTER_GRAPHICS_MEDIUM.tar` with TAR_EXIT=0; Termux SHA256 comparator produced `LOW_TO_MEDIUM.txt` and user uploaded both.
- Text diff **51 before / 55 after / 10 changes (4 added, 6 modified)**: 3 GCloud logs + rotated MHMobile backup added; `RemoteConfig.config` changed content but stayed 146 bytes (also changed in prior Medium→Low), `RHICache/.../PrecacheBinaries` grew 56603 bytes, Gamelet log/centauri logs changed, MHMobile.log rotated 13283940→8437368 with matching 13283940 backup. No new graphics `.ini` or proof of GPU/FPS whitelist setting in external folder.
- Every comparison so far also involves starting/closing the game, so **RemoteConfig may vary independently of quality**. Best next test = **Medium→Medium control**: keep quality unchanged, launch/close normally, archive `MHO_MEDIUM_CONTROL.tar`, compare with current Medium snapshot before further speculation. No game file modifications.
- Binary TAR direct inspection failed due container/Python ClientError; cannot claim content of RemoteConfig was read. Documentation `docs/P18_MHO_LOW_TO_MEDIUM_DIFF_2026-11-08_AR.md`, session `prompt/sessions/MO-20261108-CHAT-P18-LOW-MEDIUM-031.md`; raw uploads private. No app release or new CI this turn.

## Latest P17 — MO-20261108-CHAT-P17-GRAPHICS-030: first real graphics Medium→Low TAR comparison
- Current uploaded `differences_graphics_low.txt` reports user Rish BEFORE 53 files (230M) vs AFTER 51 files (237M) after stock Outlanders was launched and Graphics Quality set Medium→Low. SHA256 comparator enumerated **18 changes**: 6 added logs, 8 deleted `MHHotRes` version3392342 resource files, 4 modified: `RemoteConfig.config` (146→146B), OpenGL ES `PrecacheBinaries` (55,635,081→sameB), MHMobile.log (9,477,983→13,283,940B), centauriLog.mmap (153600→sameB). Net size delta **7,148,030B** is accounted for by logs + HotRes deletion. No new plain INI or device profile.
- No evidence that `RemoteConfig.config` is game graphics whitelist or altered *because* of quality option; game launch and resource cleanup are confounders. Shader cache changes do not spoof real GL/Vulkan identity. Android/data archives exclude `/data/user/0`; need A/B reverse toggle for causality.
- Attempted direct binary `/mnt/data/MHO_AFTER_GRAPHICS_LOW.tar` reading via python/container/python_user_visible failed with ClientError (runtime issue). **No claim to have parsed TAR or 146B config bytes.** Source data is supplied textual diff. Do not publish raw game archive.
- Next controlled test Low→Medium and optionally Medium→Low with game closed before Rish tar capture; upload only compact reports. No raw file patch created, no new Android APK build. Detailed doc `docs/P17_MHO_GRAPHICS_LOW_TAR_DIFF_2026-11-08_AR.md` and `prompt/sessions/MO-20261108-CHAT-P17-GRAPHICS-030.md`.


## Latest P16 — MO-20261108-CHAT-P16-RISH-028
- User can access external Android/data path but cannot reach `/data/user/0/com.tencent.mhadv`; tried Termux Python script from wrong cwd `~`, then from Download with non-existent placeholder BEFORE.zip/AFTER.zip, then entered `sh rish` Android shell where Termux `python` and `pkg` are absent. Need to explain that Rish ADB UID2000 is not root; private app folder still protected.
- Repaired archive diff helper to support ZIP, TAR, TAR.GZ with streamed SHA256 member comparison; test workflow `mo-archive-diff-tests.yml` succeeded run `42421428529`. Source `tools/compare_android_data_snapshots.py`, tests under `tools/test_compare_android_data_snapshots.py`; no APK build.
- Safe workflow: Rish shell only for reading allowed external game folder and creating `MHO_BEFORE.tar` / `MHO_AFTER.tar` into Download if `ls` and `tar` succeed, check tar exit code; Termux `python` after exiting Rish. Compare around change to a **legitimate in-game setting**. No game modification, root, private /data/user/0 or guaranteed GPU whitelist unlock.
- Guide `docs/P16_TERMUX_RISH_ANDROID_DATA_SNAPSHOTS_AR.md`; session `prompt/sessions/MO-20261108-CHAT-P16-RISH-028.md`.


## Latest P15 — MO-20261108-CHAT-P15-IUNLOCKER-DATA-027 (2026-11-08)
- User saw TikTok iUnlockerGL+KernelSU example for Garena `com.garena.game.mho`, screenshot config OpenGL/Vulkan Adreno (TM)840, Vulkan1.3.243, retail Manufacturer "RedMagic 11S Pro+", Model NX809J, asks if same values can be pasted into Global `com.tencent.mhadv` Android/data to unlock graphics/FPS.
- Crucial mismatch: **iUnlocker official v1.1.5-r4 requires ROOT via KernelSU/Magisk and Zygisk runtime hooking** of GLES/Vulkan/Build/CPU; screenshot UI fields are not proof target has read these values. Old existing uploaded Global data ZIP **no .ini** or clear editable device profile (87 entries total, 53 files); user-created filename alone cannot spoof hardware APIs. RedMagic NX809J is Nubia, retail name entered in Manufacturer field is inaccurate.
- TikTok/video MP4 and old APKS binary could not be decoded/re-read due Python/CaaS outage, screenshots/docs/previous verified ZIP audit used. Be transparent. No proprietary iUnlocker reuse (LICENSE forbids reverse engineering).
- Authored standalone read-only Termux helper `tools/compare_android_data_snapshots.py` (not tested yet in runtime) to diff two user-exported Android/data ZIPs before/after a normal in-game option; no patch made. Full report `research/P15_IUNLOCKER_KERNELSU_VS_MHO_DATA_2026-11-08_AR.md`, session `prompt/sessions/MO-20261108-CHAT-P15-IUNLOCKER-DATA-027.md`.
- Best path still main MO v0.11 P13 **read-only ANGLE** presence report from user, or separate owned-app rootless container test; no evidence stock MHO will accept external config to alter hardware identity. No game/system writes, APK build or root.


## Latest user exploration — MO-20261108-CHAT-P14-ARCH-026 (2026-11-08)
- Owner brainstormed making MO a launcher for the existing original installed MHO, a lightweight hidden full Android VM with Google Play services/sign-in, or adding/altering game resources in `Android/data`, and uploaded `com.tencent.mhadv.zip` for inspection.
- Read-only ZIP proof: 87 entries/53 files/34 dirs, 241130830 uncompressed bytes, SHA256 `7fbda67c3366a5639f54790860cdeab479a82b5619ed827f1919828dc0756911`. Contains 9 Unreal MHMobile Base64-like encoded logs, 11 GCloud SDK logs, 17 hotfix resources/descriptors, 1 OpenGL ES3.1 shader precache 55.6 MB, other caches. No visible .ini/AndroidDeviceProfiles.ini/GameUserSettings.ini; hotfix json descriptors binary, no clear editable graphics profile; referenced `/data/user/0/` PufferRes files absent. User archive stays private, no file modification/disassembly.
- Android UID/process isolation makes simple launcher insufficient to spoof another app. App Virtualization container can theoretically host app with modified Java/native views but commercial game Android15 ARM64/Unreal and Google Play Integrity remain unverified. BlackBoxReborn claimed Android15, README GPL3 vs LICENSE Apache2 mismatch; no code import before audit. VMOS+ vendor claims Android15+GMS but full VM not resource-free, cannot automatically re-use host Google login. AOSP Microdroid lacks GUI/graphics/zygote. Old VirtualApp support outdated. MHO EULA Section7 bans unauthorized mods/emulation/interference; account risk.
- Official Unreal docs Device Profiles can use model and GPU family; no evidence which MHO-specific mechanism controls quality/FPS. Official Google Play response Nov6 says compatibility expanding. System Game Mode FPS throttling cannot raise 60 to120. Recommend P13 installed MO **read-only ANGLE audit** next; separate sandbox reader lab only if owner wants to prioritize container, with no account/game modifications yet. Docs `research/MHO_LAUNCHER_CONTAINER_VM_DATA_2026-11-08_AR.md`, session `prompt/sessions/MO-20261108-CHAT-P14-ARCH-026.md`. Latest source CI run 42332224614 remains success; no new build.


## Latest P13 CI and download — 2026-11-07
- Main MO `v0.11.0-alpha.11` implements P13 **read-only ANGLE capability audit**. Source `5666cb45c6f8601acf14aeaca02a8b626979ff63`, GitHub Actions `42332224614` completed success for both app/probe tests, APK builds and Release upload. Public https://github.com/alwansan/MO/releases/tag/mo-v0.11.0-alpha.11-run59, main APK **1167998 bytes / SHA256 4372a3b077632925df8c51161843f110c53777d9009254c6c51c2ffd6fe5925f**.
- UI 05B P13 section and `getAngleSnapshot()` Shizuku UID2000 AIDL endpoint read known package names, driver selection settings, `debug.angle.gl_renderer` and ro.debuggable with **14 allowlisted read-only commands**, no `settings put` or `setprop`. Global all-angle switch `angle_gl_driver_all_angle` is only read because AOSP warns setting it to1 may make device unbootable.
- P13 has **not run on user's POCO**; no confirmed ANGLE driver availability, write permission or independent game/application `glGetString` spoof. User original goal is Adreno840 actually returned by other apps, not fake MO labels. Read `docs/P13_ANGLE_READONLY_DEVICE_TEST_AR.md`, session `prompt/sessions/MO-20261107-CHAT-P13-ANGLE-READONLY-025.md`.
- Main CI debug signature differs across runs; advise preserving any GPU Debug rollback data before uninstall/reinstall; previous P12 main user log last proved clean 4 keys at 05:26:04 only. Probe source still v0.6, optionally rebuilt but no additional feature.


## Latest — MO-20261107-CHAT-P13-ANGLE-READONLY-025: first real GPU spoof feasibility gate (ANGLE, read-only)
- User's foundational MO goal is **cross-app reported GPU identity substitution** such as Adreno840 read by a separate app's GLES API, aiming to discover if its graphics/FPS whitelist changes. Not MO fake menus/FPS callbacks. Conditional source insight: system ANGLE `debug.angle.gl_renderer` string override exists upstream, but no proof this POCO provides a usable ANGLE driver, shell can set property, or Outlanders uses GLES/ANGLE. No Vulkan/Java GPU model global spoof demonstrated.
- Implemented main MO `v0.11.0-alpha.11` **read-only P13 ANGLE discovery**: AIDL `getAngleSnapshot()` in strict UID2000 Shizuku service, 14 allowlisted `pm list/path`, `settings get`, `getprop` commands, explicit all-app boot-risk warning. UI button `فحص قدرة ANGLE (قراءة فقط)`, normal clipboard report, `Download/log.txt` DiagnosticLog events; UserService version5. JUnit `AngleReadOnlyCommandPolicyTest` rejects `setprop`, `settings put`, arbitrary commands.
- Early P13 CI run `42332224614` at source SHA `5666cb45c6f8601acf14aeaca02a8b626979ff63`: both module tests/assemble passed, final release publishing yet to verify. `probe` remains v0.6.0, no new experiment on external games. New source docs `docs/P13_ANGLE_READONLY_DEVICE_TEST_AR.md`; session `prompt/sessions/MO-20261107-CHAT-P13-ANGLE-READONLY-025.md`.
- Next: verified Release then owner P13 read-only report on real POCO. Only consider a separate owner-consented, reversible ANGLE native/API GL A/B on a distinct cooperating app later. Current official rootless external GLES loader cannot inject Outlanders (nondebuggable/no opt-in). Never activate all-app ANGLE.


## Most recent owner goal correction — MO-20261107-CHAT-P13-REAL-SPOOF-024: cross-app GPU spoof is primary
- User explicitly clarified **not** aiming merely for FPS measurement or mock settings; wants independent games/apps to **see fake higher hardware** (e.g. string `Adreno 840` instead of `Adreno (TM) 730`) to potentially unlock graphics or FPS presets. This restores original primary acceptance. P10 local fictional profile, P11 own onDrawFrame callbacks, P12 logs are tools only, NOT a working game spoof.
- NEW upstream candidate: ANGLE source `src/libANGLE/Context.cpp` uses Android property `debug.angle.gl_renderer` and vendor/version aliases to override the GLES renderer string exposed *by ANGLE*; AOSP docs show per-app ANGLE/native driver choice. Crucially **unknown if stock POCO HyperOS has usable system ANGLE, if nonroot ADB can write/read property, if target uses ANGLE or Vulkan**. No cross-app effect was demonstrated.
- Android officially blocks external GLES/Vulkan layers for nondebuggable non-opt-in stock Outlanders; Shizuku ADB UID2000 doesn't alter this. ANGLE may only cover OpenGL GLES string, not `VkPhysicalDeviceProperties`, Java `Build.MODEL`, actual GPU capabilities, or engine whitelist.
- Proposed strict proof: read-only ANGLE capability audit → consented reversible A/B **independent cooperating app** reading `glGetString(GL_RENDERER)` in its own process → exact rollback before any discussion of gaming usage. Do not turn on MO debug layer or modify stock game for routine proof. All upstream refs `research/REAL_GPU_IDENTITY_SPOOF_ANDROID15_ANGLE_AR.md` and session `prompt/sessions/MO-20261107-CHAT-P13-REAL-SPOOF-024.md`.
- Previous Android test output remains valid but doesn't satisfy primary goal; no new app build this correction cycle.


## Latest — MO-20261107-CHAT-P11-REPEAT-023: newer cumulative diagnostic file adds P10 + repeated P11 PASS
- A **new** owner-uploaded file named `log (2).txt` is 41724 bytes SHA256 `dbdfe2ecfa429012378459ae212e34c3181619ebe699dba055e30676d5bb7415`, **different from** earlier same-named 15982-byte upload. It preserves PID31111 (85 events including previously missing export and pause) and adds PID3604 (116 events); **201 total are cumulative, not 201 new**.
- New PID3604 05:45–05:47 device-run validated **all seven local P10 mode choices** with real model23013PC75G/SM8475 and GLES/NDK/eglGetProcAddress/Vulkan Adreno730/120Hz unchanged. Combined fake 120 menu false on ACTUAL or any single mock, true only on ALL_SIMULATED and returns false when switched away. No commercial game spoof.
- Repeated independent P11 stride tests: 38 new windows; 13 stride1 mean own draw120.061/s, 16 stride2 mean59.834/s, 9 stride4 mean30.037/s. Choreographer UI stayed around 120/s. This confirms 1/2/4 callback pacing **twice**, not SurfaceFlinger/game FPS.
- Earlier process's `EXPORT_RESULT` inside cumulative history reports successful app copy of 15943 chars and Download/log (2).txt output; subsequent `FRAME_LAB_PAUSE` records normal stop. Clipboard *contents* not independently pasted; newer snapshot ends before its own export result. `TRIM_MEMORY=20` is UI hidden; no ERROR/UNCAUGHT_CRASH in retained 201.
- Test-only upload has no new GPU Debug settings readback; last independent four-key data remain the main MO 05:26 guarded-cleanup result. No re-activation needed.
- New source evidence `docs/DEVICE_FEEDBACK_P11_REPEAT_AND_IDENTITY_2026-11-07.md` and session `prompt/sessions/MO-20261107-CHAT-P11-REPEAT-023.md`; documents only, no APK rebuild/source patch/game write.

## Latest — MO-20261107-CHAT-P11-STRIDE-022: P11 stride 1/2/4 real-device acceptance
- Third owner uploaded `log (2).txt` (SHA256 `66a24cae18b578d22624e15439dbbf7693ef9a50c6b4b5c8d7f72bfa34d76cff`) from owned `com.alwansan.mo.glesprobe` v0.6.0 Android15, PID31111 new at 05:38:31. 76 event records, 45 one-second pacing windows, no explicit Java crash or ERROR logs; private raw file kept in chat, not public GitHub.
- All P11 request strides **tested and PASSED**: stride1 14 windows mean 119.632 Choreographer vs 119.632 renderer callbacks/s; stride2 19 windows mean 119.127 vs 59.563; stride4 12 windows mean 120.316 vs 30.079. Aggregate UI/draw counts: 1680/1680, 2270/1135, 1448/362. Renderer callback rate dropped ~50.21% and ~74.86% vs stride1. Do not claim SurfaceFlinger frames, energy savings or real Outlanders 120 FPS from these.
- New PID after older 05:26 confirmed MO-only GPU Debug cleanup shows all own Java/NDK GLES and native Vulkan renderer **Adreno730** / no intercept, but newer log does **not** independently check four Settings.Global keys. Prior main Shizuku last known clean values remain last measured at 05:26:04.
- P11 basic lab benchmark can be closed; no further APK or new Shizuku layer experiment necessary. P12 clipboard, crash diagnostics, 3×3MiB rotation still not demonstrated but Download file export works.
- Report `docs/DEVICE_FEEDBACK_P11_STRIDE_2026-11-07.md`, session `prompt/sessions/MO-20261107-CHAT-P11-STRIDE-022.md`. Documentation only; source/release unchanged.


## Latest device evidence — MO-20261107-CHAT-P12-DEVICE-LOGS-021 (owner uploaded both P12 diagnostic files)
- Uploaded two owner-generated Download text files from real POCO Android15: `log (1).txt` is **MO main v0.10.0-alpha.10**, PID22510, 35 events; `log.txt` is **owned MO Layer Test v0.6.0**, PID23395, 119 events. The two separate app-private local logs show timestamps, lifecycle, measured GL/GLES/Vulkan, Shizuku outputs and UI actions. Download/export on phone PASS; clipboard copy, rotation, crash catching not yet verified. Never copy the full user logs into public GitHub.
- **Last four-key GPU debug state is now evidence-backed clean at 05:26:04+0300:** main action1 activated MO debug layers at 05:24:29, action4 guarded cleanup at 05:25:51 disabled enable=0 and removed all three MO markers; action3 at 05:25:55 and 05:26:02 independently reconfirm. It was **guarded cleanup**, not necessarily exact restoration of unknown historical values.
- Owned probe process started 05:24:09 **before** layer activation. Later Adreno730/no intercept in same PID after activation is not a proper new-process loader test, and no new PID after cleanup shown. A fresh clean-start PID is optional; do not re-enable layers.
- Three P11 Frame Pacing Lab sessions all used only stride1. **21 sample windows** averaged **120.4749** callbacks/s for Choreographer and own GLES draws, range **118.597–121.383**; no stride2/4 evidence, no SurfaceFlinger presented FPS or Outlanders FPS. Identity lab actual/virtual gate behavior consistent with earlier P10 phone proof.
- No `ERROR`/`UNCAUGHT_CRASH` in exported histories; `TRIM_MEMORY level20` means UI hidden, not an OOM.
- Full analysis `docs/DEVICE_FEEDBACK_P12_LOGS_2026-11-07.md`; chat session `prompt/sessions/MO-20261107-CHAT-P12-DEVICE-LOGS-021.md`. No app code/build/device writes this session. Next owner action: stride2/stride4 ~5 sec each, re-export P12 log; no new APK required.


## Latest P12 — MO-20261107-CHAT-P12-LOG-EXPORT-020
- User wants complete *available* app event log with timestamps copied to clipboard and saved straight to Download/log.txt, to upload for deeper debugging **before P11 phone test**. Implemented in **both** independent packages: main `com.alwansan.mo` v0.10.0-alpha.10 and disposable `com.alwansan.mo.glesprobe` v0.6.0. No global logcat/other games: Android app sandboxes remain in force.
- Pure Java `LogLineFormatter` with local timezone offset, millisecond wall time, monotonic elapsed_ms, PID/thread and sanitized event details; bounded asynchronous app-private logger rotating 3 × 3MiB; Application lifecycle, memory trim and uncaught stack handlers; captures owned identity/GLES/Vulkan, frame stride and measured callback reports; main Shizuku results and GPU Debug readbacks. Clipboard max 180k chars; full retained log exported via MediaStore.Downloads with exact final basename reported (may auto-rename collisions). Private logs persist across sessions but not uninstall.
- [CI 42327821781](https://github.com/alwansan/MO/actions/runs/42327821781) **SUCCESS**, code SHA `d40d0b6ec70d14ede1731eac2eae4a4f08e7ff81`, unit/native/APK checks passed; Release https://github.com/alwansan/MO/releases/tag/mo-v0.10.0-alpha.10-run53. Main APK SHA256 `a8e227174036732eb31ab7a27e667981e8f6da16102c9f25fe1d737a85fe86ce`; owned probe SHA256 `ce3771295b2728fad25342ca4c5c86f21904673df55115d56a207615da02ae1f`.
- **Not device tested**: real Download file creation/clipboard, P11 pacing; owner should install disposable v0.6 and run P11, tap export then upload text file. For Shizuku main diagnostics, v0.10 separate file. User said GPU Debug restored and app deleted, no verified readback or app identity; do not infer.
- Details: `docs/DIAGNOSTIC_LOG_EXPORT_AR.md`; session `prompt/sessions/MO-20261107-CHAT-P12-LOG-EXPORT-020.md`. No root, game modifications, or auto-upload.

## Latest — MO-20261107-CHAT-P11-FRAME-PACING-019: user self-reported restore/delete; owned-frame P11
- User stated GPU Debug settings were restored and an app was deleted; app identity **unspecified**, no exact Shizuku 4-key post-action readback. Do not assume any particular package is installed/removed or GPU settings have been independently verified. Prior P10 report of active keys is earlier-time evidence. No new GPU Debug activation planned.
- Implemented independent owned-app GLSurfaceView Frame Pacing Lab (version `probe v0.5.0`) after P10 mock UI proof: Choreographer callbacks/sec vs GLES Renderer.draw callbacks/sec, stride 1/2/4. Not FPS displayed/composited by system, not Outlanders FPS. Android15 insets fix for clipped identity lab title. Main MO source remains v0.9; no Shizuku or game changes.
- Android official references: `GLSurfaceView` onPause/onResume/RENDERMODE_WHEN_DIRTY and `Choreographer.FrameCallback`; `docs/OWNED_FRAME_PACING_LAB_AR.md` describes limits. Pure Java JUnit coverage `FramePacingWindowTest`.
- **P11 CI succeeded** on complete source `9abd11a3cbc3062836ceef6f2facd968286faa6f`, GitHub Actions run `42325865139` passed all tests, ARM64 build and Release upload. Public raw v0.5 probe APK under https://github.com/alwansan/MO/releases/tag/mo-v0.9.0-alpha.9-run32 ; 182484 bytes SHA256 `5aca59d8dc61ec0a1e3763963de96974f02c62f60d15d7041233d774afae37d8`. Main rebuilt debug APK unchanged in source feature; user need not install main. P11 device runtime pending.
- Session `prompt/sessions/MO-20261107-CHAT-P11-FRAME-PACING-019.md`; future user device check optional for P11 test app. No direct phone operations performed in chat.


## Latest — MO-20261107-CHAT-P10-DEVICE-018: P10 phone PASS; GPU Debug newly reported ENABLED
- Three owner screenshots prove MO Layer Test v0.4.0 + Game Identity Lab run on POCO F5 Pro under PID5783. Real Build `23013PC75G`, SoC `SM8475`, Java/NDK GLES and native Vulkan Adreno730, display120Hz. Actual mode0 mock combined 120 menu LOCKED; all synthetic mode6 mock combined UNLOCKED. This is simulated UI only, not real game performance. Modes1–5 not yet device tested.
- **Newest owner report** from MO v0.9 shows Shizuku UID2000 and all four MO-owned GPU Debug keys **active**, `enable_gpu_debug_layers=1` plus exact three markers, confirmed in system panel and after-action readback. Supersedes previous report 0/absent valid only at earlier instant. Cause unknown; synthetic dropdown itself performs no settings write.
- Immediate gate: preserve installed MO main and its private rollback; normal restore if valid, read all 4 keys; if absent/refused share exact error, then only separately consented guarded MO-marker cleanup as appropriate. Do not uninstall/update main MO, re-enable layer or modify game. Fresh test PID after verified rollback remains pending.
- Detailed evidence `docs/DEVICE_FEEDBACK_P10_2026-11-07.md`, session `prompt/sessions/MO-20261107-CHAT-P10-DEVICE-018.md`. No new app code/build/device writes performed this cycle.


## P10 — MO-20261107-CHAT-P10-IDENTITY-LAB-017 (2026-11-07)
- User's "Continue" after Zygisk feasibility research authorized continued **owned-only** engineering. Created `IdentityLabPolicy` with immutable real signals and 7 mock modes (actual, single Build.MODEL, CPU, Java GLES, NDK GLES, Vulkan, or all synthetic); mock combined 120 menu gate requires all flags and physical panel >=120Hz. No real system property or graphics API hook in this lab.
- `GameIdentityLabActivity` in existing disposable `com.alwansan.mo.glesprobe` reads own process Java Build, GLES20, NDK GLES, native Vulkan GPU name, display Hz; uses non-exported Activity and simulation-only UI. Probe bumped to v0.4.0; MO main remains v0.9.0-alpha.9. Docs `docs/OWNED_GAME_IDENTITY_LAB_AR.md`.
- GitHub CI [42324225397](https://github.com/alwansan/MO/actions/runs/42324225397) at source `4166210d4fb5e62b391b759f3b4508eb50d02e97` PASSED app and probe JUnit, ARM64 native builds, both APKs and Release. Public [run25 prerelease](https://github.com/alwansan/MO/releases/tag/mo-v0.9.0-alpha.9-run25): `MO-Layer-Test-v0.4.0.apk` 172148 bytes SHA256 `6fdc64c09d76bd0b071d46f5e10f850347a79212256a846a745ac9e8263095dd`. Rebuilt main APK SHA256 `42fdb05b4256feb1ff6024f43f99b9206bbbd17c40f70ff925cbd719a9965282` has a different debug signature.
- **Never uninstall or update installed main MO just to use new lab.** Only replace disposable test APK after confirming GPU Debug four keys remain 0/absent (last owner report confirmed this momentarily); signature mismatch may require uninstall of **probe only**. New post-cleanup PID and P10 phone result remain unverified. Neither rootless stock Outlanders spoof nor real FPS improvement is achieved.
- Session: `prompt/sessions/MO-20261107-CHAT-P10-IDENTITY-LAB-017.md`. No game, system, root or Shizuku settings changes in this cycle.


## Latest device and upstream evidence — MO-20261107-CHAT-ZYGISK-COPG-AUDIT-016
- User provided four screenshots and two full MO v0.9 reports. **Final pasted post-cleanup** report independently shows `enable_gpu_debug_layers=0` and all three MO layer markers **absent** in `[READBACK AFTER ACTION]`; independent shell panel now also shows 0/null. This directly confirms P9 refresh behavior and bounded cleanup at that point, unlike earlier stale v0.8 report.
- Screenshots also show **prior** enabled four-key state and owned test PID16719 reporting physical Adreno730 in Java/NDK/eglGetProcAddress; cannot establish PID16719 ran **after** the final cleanup, so the new-process post-cleanup renderer step remains pending. Do not re-enable solely to test.
- Source research: `research/ZYGISK_COPG_NO_ROOT_FEASIBILITY_AR.md`. ZygiskNext restrictive since v4-0.9.2; ReZygisk README says AGPL3 but LICENSE is GPL3 text; NeoZygisk GPL3; COPG Apache2 top-level visible repository but its `JSON` branch lacks native `src/` referenced by `build.sh`, so cannot audit/replicate native binary from this tree; MMRL 7.6.1 page has conflicting generic footer.
- Root/Zygisk bootstrap confirmed distinct from Shizuku UID2000; no new route to spoof GL/VK/Build in stock nondebuggable Outlanders. Android Game Mode FPS overrides are throttles/interventions, not guaranteed game unlock. Candidate next proof is **owned Game Identity Laboratory**, not production-game injection.
- Source list and precise device evidence: `docs/DEVICE_FEEDBACK_ALPHA9_2026-11-07.md`, `prompt/sessions/MO-20261107-CHAT-ZYGISK-COPG-AUDIT-016.md`. This cycle is docs/research only. No APK build, third-party binary execution, game edit, root or settings writes.


## Current authoritative continuation — MO-20261107-CHAT-P9-AUDIT-015
- **Active phase P9** (the 2026-11-07 initial Planning-only entries below are historical). Confirmed main starting HEAD `3c3c20a51ce976c64984ea3a076cf5bd9a5bec5b`, documentation child of P9 code `968e13d19a956a0016c4e57609ca744c253da3db`.
- Verified GitHub Actions [42316925749](https://github.com/alwansan/MO/actions/runs/42316925749) success and [v0.9 direct APK prerelease](https://github.com/alwansan/MO/releases/tag/mo-v0.9.0-alpha.9-run16), **not on-device v0.9 validation**.
- Current owner device proof only v0.8 guarded cleanup OK, with stale cached shell panel; need fresh independent four-key readback and new process Native/eglGetProcAddress renderer after force-stop of **only owned test app**. No new activation/upgrade before confirmation.
- Stock Outlanders `com.tencent.mhadv` targetSdk36, Debuggable NO, injectLayers opt-in NO; cannot use official external rootless GLES layers on stock build. Owned NDK interception/mock FPS is not a commercial game FPS unlock.
- Original proprietary iUnlockerGL ZIP/KernelSU APK were not available or required during this audit; `SOURCE_INVENTORY` hashes/licensing remain canonical. No original binaries/code in public MO.
- Source review follow-up **hypothesis only**: pending Shizuku service-connection action and initial asynchronous snapshot could race; investigate after phone state verified, not a proven device defect.
- Audit session [log](sessions/MO-20261107-CHAT-P9-AUDIT-015.md); no source, game, GPU settings or CI builds changed during this documentation-only cycle.


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

## MO-20261107-CHAT-P5-PARTIAL-NATIVE-012: v0.5 partial GLES proof on-device
- Owner screenshots show MO Layer Test v0.2 Android15: native NDK GL_RENDERER **MO-GLES-LAYER-PROOF (SIMULATED)**, native YES, **Java GLES20 GL_RENDERER remains Adreno(TM)730, Java NO**, synthetic mock 120 FPS UNLOCKED. Shizuku four GPU debug layer settings correct and currently enabled. Logcat layer messages not found; manual ELF library loadability cannot independently prove automatic loader init.
- This is a PARTIAL PASS limited to disposable debuggable com.alwansan.mo.glesprobe. **NOT** actual FPS improvement, not Outlanders compatibility or global spoof.
- No confirmed restoration yet. **Priority restore from current MO v0.5 BEFORE uninstall/update** and read back enable_gpu_debug_layers=baseline, gpu_debug_app=baseline, plus two layer keys. Backup is in MO app-private data and CI debug signatures are not stable.
- Grounding: docs/DEVICE_FEEDBACK_ALPHA5_2026-11-07.md; upcoming test should separate Java/NDK/eglGetProcAddress. Android official debug layers only debuggable/opt-in targets.

## P6 owned test path isolation (source, device test pending)
- Probe v0.3 separates Java GLES20, NDK direct glGetString, and eglGetProcAddress lookup under SAME EGL context, records EGL_ANDROID_GLES_layers extension support and process PID.
- Replaced manually loading the layer (System.loadLibrary) with ordinary re-query so results won't be muddied by developer-forced library load. Existing LayerLibrarySelfCheck class is unused and must not be treated as injection proof.
- Mock tier explicitly marked **Mock Native FPS tier (simulation)** based on NDK only, NOT real game FPS; no ambiguous aggregate "Layer intercepted YES" when Java is still unaffected.
- Main MO v0.6 updated for versioned Release. **Do NOT install before restoring GPU Debug settings via CURRENT MO v0.5** — private rollback snapshot will be lost if debug signing requires uninstall. Device P6 runtime proof pending.

## P7 manifest eligibility (2026-11-07, read only)
- Source adds Android PackageManager GET_META_DATA scan for game's installed ApplicationInfo: targetSdkVersion, FLAG_DEBUGGABLE, and manifest meta-data `com.android.graphics.injectLayers.enable`.
- Shows **manifest prerequisites only**, not guarantee hook works. Debuggable or opted-in+targetSdk30+ is candidate; otherwise official external GLES debugging layers cannot be assumed available on stock nonroot Android. Actual com.tencent.mhadv flag values are UNKNOWN until owner's device report.
- Scans package-visible installed games from explicit queries; no game file modifications or privacy-sensitive access.
- Released after CI verification, and **user must restore GPU debug settings from v0.5 before any install**.

## P7 CI and raw APK release closeout — 2026-11-07
- Built source commit `0111eb311d32305de0ed83db016a4fed1252e115` with GitHub Actions run `42314365656`. Java unit tests, both APK builds, native NDK build, APK verification and GitHub Release upload have all **passed**.
- Published prerelease: https://github.com/alwansan/MO/releases/tag/mo-v0.7.0-alpha.7-run14 .
- Confirmed uploaded raw assets:
  - `MO-Graphics-Probe-v0.7.0-alpha.7.apk` (1,118,830 bytes).
  - `MO-Layer-Test-v0.3.0.apk` (155,196 bytes).
- New capabilities: user-visible installed-game debug/opt-in/target-SDK flags (read only); owned test separates Java/native/eglGetProcAddress and EGL_ANDROID_GLES_layers extension; removes explicit System.loadLibrary layer manual button to avoid conflating loader evidence.
- **No on-device P7 result yet**. The last observed owner's phone state was GPU Debug layers enabled. First action remains restoration from **existing MO v0.5** before uninstall/upgrade. Do not claim stock Outlanders FPS unlock.

## MO-20261107-CHAT-P8-RECOVERY-013 — P7 real device and P8 safety fix
- User v0.7 report proved native and eglGetProcAddress renderer interception in owned debuggable MO Layer Test, PID 20827; Java path remains Adreno730. EGL_ANDROID_GLES_layers declared YES; synthetic FPS tier only, not real FPS gain.
- **Outlanders** com.tencent.mhadv version1.0.9.0.3308842, targetSdk36, Debuggable NO, opt-in `com.android.graphics.injectLayers.enable` NO: stock app not eligible for official external rootless GLES layer. Do not claim Shizuku can override this.
- **Urgent persistent settings:** Shizuku reports 4 MO debug keys still enabled. Earlier v0.7 could save already-active test state as original baseline, and user may have lost v0.5 backup across APK reinstall. Check normal Restore in currently installed v0.7 then re-read keys; a success string alone is insufficient.
- P8 source v0.8.0-alpha.8 adds `OwnedLayerRecoveryPolicy` and JUnit tests, refusal to activate when any debug config already exists, refusal to restore from a snapshot of active MO settings, and separately consented strictly guarded **MO-owned stale cleanup** (sets enable=0, deletes 3 exact owned markers only, captures pre-cleanup snapshot before writing, verifies, partial failure rollback).
- This cleanup is **not** reconstruction of unknown old baseline values. Keep original games untouched; need CI build result and user device confirmation.
- Evidence docs/DEVICE_FEEDBACK_ALPHA7_2026-11-07.md; recovery guide docs/GPU_DEBUG_RECOVERY_AR.md.

## P8 CI and Release verified (2026-11-07)
- GitHub Actions https://github.com/alwansan/MO/actions/runs/42315468593 finished **SUCCESS**, commit `2e2ddb7cc4ba29731bddf1f612955339ac9931da`; app and probe JUnit tests, NDK builds and release upload passed.
- Raw APK release: https://github.com/alwansan/MO/releases/tag/mo-v0.8.0-alpha.8-run15.
  - `MO-Graphics-Probe-v0.8.0-alpha.8.apk` (1,135,214 bytes), raw .apk verified uploaded.
  - `MO-Layer-Test-v0.3.0.apk` (155,196 bytes), unchanged probe.
- On-device P8 emergency cleanup **NOT tested**; user still must test existing v0.7 Restore/readback first. Do not force an upgrade or claim full prior-state restoration. If v0.7 restore fails and exact four owned markers persist, v0.8 offers explicit guarded cleanup, only with owner consent. Shizuku UID2000 is mandatory.
- New run results are not evidence original Outlanders FPS/graphics options unlocked; original game's manifest flags still exclude official GLES external layers.

## MO-20261107-CHAT-P9-REFRESH-014 (2026-11-07): v0.8 cleanup success, stale cached report fix
- User v0.8 screenshot shows guarded cleanup returned OK (backend checks 0 and missing owned markers); retry activation protection also correctly refused already configured MO developer GPU layer settings.
- Copied report still contained `enable_gpu_debug_layers=1` in independently cached shell readout; source code confirmed it never refreshed `shellOutput` after cleanup. Do not construe this as proof cleanup failed; require fresh readback from user.
- v0.9 source refreshes shell panel and exact Shizuku-owned four key diagnostic after each operation, includes readback in report and invalidates stale data on refresh failure.
- User should re-diagnose GPU settings and force-stop/reopen the owned test app, verify Native GL_RENDERER returns Adreno730 before updating. Do not enable a new experiment.
- Outlanders game still Debuggable NO, external layer opt-in NO; no rootless official GLES hook possible via Shizuku alone.
- Evidence: docs/DEVICE_FEEDBACK_ALPHA8_2026-11-07.md.

## v0.9 Release closeout (2026-11-07)
- Source commit `968e13d19a956a0016c4e57609ca744c253da3db` build via GitHub Actions https://github.com/alwansan/MO/actions/runs/42316925749; Java tests, ARM64 native build, both APK builds and Release asset upload passed.
- Public experimental Release: https://github.com/alwansan/MO/releases/tag/mo-v0.9.0-alpha.9-run16.
- Uploaded direct assets: `MO-Graphics-Probe-v0.9.0-alpha.9.apk` (1,135,214 bytes) and unchanged `MO-Layer-Test-v0.3.0.apk` (155,196 bytes).
- Fix: after every Shizuku operation UI re-reads four exact GPU keys and refreshes the cached shell panel; report appends `[READBACK AFTER ACTION]`. This addresses contradictory stale `enable_gpu_debug_layers=1` after a cleanup verified by the backend.
- **On-device v0.9 not yet tested**. Owner v0.8 cleanup reported OK. First action is to verify with v0.8 read-only diagnosis and restart owned test app; do NOT reinstall/update unless no active GPU debug state and no rollback needed.


## MO-20261108-CHAT-P24-RENJANA-CAPTURE-037 — verified P23 phone script, guest memory and UNKNOWN settings
- Owner ran P23 read-only capture 2026-11-08 03:57:34+0300 in Rish UID2000. Exact file MO_RENJANA_OWNED_CAPTURE_20261108_035734.txt 42866 bytes/336 lines/SHA256 7f18d75643390c8a33952bc0bec46ae4251e7293a8ea509a7c6965cf401191a0 (private; not in GitHub). Guest PID23140 is com.fesu.renjana:p1, distinct from P23 prior p0 PID17591; external MO PID14458; Renjana main PID14699.
- Renjana main PSS83127 kB; Guest p1 PSS106509 kB; external MO PSS40820 kB. Main+guest PSS=189636kB (~185.19MiB) only a contemporaneous combined snapshot. Do not infer overhead without idle baseline, or FPS/thermal/surface presentation.
- Log proves Guest StubActivity_1/Application started, native MO probe library loaded in isolated linker namespace; DeviceFingerprint logs Java realme RMX3491 spoof; vendor Qualcomm GLES driver path still used. No new p1 renderer/Vulkan independent API readback; P23 p0 old screenshots show Adreno730 in Java/NDK/EGL. Pine 2 Android15 missing symbol warnings, VirtualContext 15 directory warnings. These are engineering blockers/hypotheses, not a confirmed crash or fixed issue.
- Rish readings of ALL FOUR GPU Debug keys failed transaction 2147483646. State remains UNVERIFIED, not clean. No new Settings activation/cleanup until reliable independent readback and consent.
- P24 committed read-only tools/analyze_renjana_owned_capture.py and 3/3 local fixtures, error-classification update to tools/renjana_owned_lab_capture.sh and new test fixture, Arabic P24 doc and session. Modified shell not device tested; no new APK/Actions build. Preserve owned test-only scope, no Root/Bootloader/Zygisk/Outlanders/ANGLE-global modifications.


## P25 MO-20261108-CHAT-P25-GUEST-IO-038 — VirtualContext false-negative hypothesis and owned probe v0.7
- At main HEAD 5343f4d22707885f5fdbadb621e0373835294bfb P25 resumed after real P24 capture. Reviewed pinned Renjana AntiDetection.kt/CoreHooks.kt/VirtualContext.kt/PineHookManager.kt: with anti-detection enabled, hidden paths include com.fesu.renjana/private guest dataPath; File.exists hooks can report false while filesystem still exists. Source evidence explains a **possible** cause for P24's 15 VirtualCtx warnings, NOT confirmed storage failure. No code copied from upstream or iUnlockerGL.
- P25 original probe v0.7 source adds user-triggered own-private-dir File/Context/native-libc roundtrip with ephemeral unique files only, and same-PID native Vulkan to the main GLES readout. FilePathCheckTest 3 JUnit cases. CI run 42433494794 must be inspected for final APK Release; no on-device P25 test yet.
- Results must distinguish File.exists false vs libc stat true/usable files vs real I/O failure. Never infer native GPU spoof, full guest isolation, game FPS unlock, or settings state. P24 four GPU Debug readbacks FAILED/UNKNOWN; keep non-root, no global writes and owned-app-only lab.

### P25 v0.7 build verified — phone experiment pending
- CI run 42433494794 SUCCESS on P25 source 0195d5def3888a24d78b89cf879679e35e2199ef; public https://github.com/alwansan/MO/releases/tag/mo-v0.11.0-alpha.11-run66 has owned MO-Layer-Test-v0.7.0.apk 230156B SHA256 0ebe84f65242036802db2e9e0817902c783f1089096518dec07d684f8b5d4462. This proves JUnit/build/release, NOT native GPU/VirtualContext phone behavior. Avoid changing installed main MO; use disposable test APK only. See session 038 and docs/P25_RENJANA_GUEST_STORAGE_DIAGNOSTIC_AR.md.

## MO-20261108-CHAT-P26-RENJANA-STORAGE-PATCH-039 — P25 device proof and narrow File.exists P26 fork
- Owner screenshots 1006531219.jpg (SHA256 b46f9c75186ddca99e320fa05fbd492b27759b401c1d726dc18d9b69ed7e3eec) and 1006531220.jpg (SHA256 1ad86da503740a09a9c117e375825c9965c972b90184d0bbbd6e227e4c204e99), not public. PID8544 displayed in MO v0.7, but no process-name ps snapshot for that PID. Java Build.MODEL XT2251-1, Java/NDK/EGL GLES Adreno730 and native Vulkan Adreno730; no GPU interception. Synthetic tier LOCKED only.
- Activity/Application File.exists=false but isDirectory=true, canWrite=true, direct Java File/native libc I/O PASS and clean PASS; Context streams FAIL(FileNotFoundException) and deleteFile cleanup NOT_FOUND_OR_FAILED. Source VirtualContext.openFileInput checks File.exists before FileInputStream; screenshot's grouped test cannot attribute failure to openFileOutput vs openFileInput.
- Pinned Renjana SHA14302a57 AntiDetection.shouldHidePath hides renjana paths; CoreHooks.createFileExistsHook overwrites File.exists false; VirtualContext.ensureDir, openFileInput, deleteFile rely on File.exists. This is strongly suggested Android15 guest storage bug; phone root-cause/repair unproven.
- P26 adds tools/patch_renjana_owned_file_exists_p26.py (+ offline tests) and independent CI .github/workflows/mo-renjana-owned-storage-p26.yml that changes only PineHookManager call site and skips File.exists hook for **owned MO Layer Test package**, not other guests. Preserves original Renjana Release, includes Apache-2.0 provenance and patch diff. Build run 42434382187 must be verified and on-phone trial still pending. No Root/bootloader/GMS/game/Settings writes; no GPU spoof claim.

### P26 owned Renjana APK build published; phone experiment pending
- Verified run 42434382187 SUCCESS, source commit 20627f8a6d5317c7137c3c119fd3a9324456d6fb. Separate modified Apache-2.0 Renjana P26 Release https://github.com/alwansan/MO/releases/tag/renjana-owned-storage-p26-run1 contains arm64 debug APK 55348455 bytes SHA256 d277ce4f9975af84fbc8b78c051f29dbe0b580c120f957d4e0fc158e1551280e and exact patch diff/license/provenance/checksum. Distinct from original unmodified Renjana run2.
- Patch only skips PineHookManager.hookFileExists() registration for owned guest package com.alwansan.mo.glesprobe. Does not touch Vulkan/GLES, other guests, game/system settings. No real P26 phone storage A/B yet. Future user installation may require uninstall old Renjana due per-run debug signing; loss of disposable instance data is possible, never assume in-place upgrade. See session 039 and docs/DEVICE_FEEDBACK_RENJANA_P25_2026-11-08_AR.md.

## MO-20261108-CHAT-P27-P26-DEVICE-PASS-NATIVE-DISPATCH-040 — P26 verified and P27 packaged
- P26 **actually tested by owner**: raw screenshot SHA256s db502548697c684ed7075597853ac7dfe52338eee49989ebe43eefa16ce93ba7 and 985510b4e53ff12ef8e89d5cd94327110ce08638bd15dfd717a6e3258b855190; Rish ps maps guest PID17047 com.fesu.renjana:p0, external own test PID4548, main Renjana PID16543. P26 after File.exists hook skip for own guest: Activity and Application File.exists=true, direct Java File, Context streams, native stat/IO and all cleanups PASS; strongly contrasts with P25 Context FAIL and exists=false. Caveat: P25 and P26 used different Guest fake models and Instances (XT2251-1 vs RMX3740), not perfect A/B. Do not claim general container storage fixed for arbitrary apps.
- P26 Build.MODEL RMX3740 but Java/NDK/EGL gl renderer and Vulkan deviceName Adreno730; Java/Native layer intercepted NO; no GPU spoof/FPS proof. No new PSS reading. docs/DEVICE_FEEDBACK_RENJANA_P26_2026-11-08_AR.md.
- P27 adds native symbol origin read-only dladdr/dlsym RTLD_DEFAULT, GLES linked/egl pointer comparison and Vulkan linked symbol in owned MO Layer Test. Displays **module basenames only** and pointer equality, not address/path; no changes to GPU APIs/layers/settings. Version MO Layer Test v0.8.0; see docs/P27_NATIVE_GLES_VULKAN_DISPATCH_PROBE_AR.md and session 040.
- P27 GitHub Actions 42435412976 **SUCCESS** commit 80a08dd7c3af7288a1d8edb3c35a1bd0812a0139. Release mo-v0.11.0-alpha.11-run71 includes raw MO-Layer-Test-v0.8.0.apk 232400B SHA256 d0279836d1e70b3696f413342a6afaefc69ca13a025587fd13113728c637d8d5. **P27 not device-tested.** Keep working Renjana P26 baseline and original MO main; no Outlanders or global Settings actions.

## MO P28 (2026-11-08) — pause container swapping; new actual P27 results and native-first gate
- User gave 8 P27 images (private; SHA in research/P28_NATIVE_FIRST_CONTAINER_STRATEGY_AR.md). Renjana UI owns MO Layer Test v0.8.0, app says PID23440 but not independently Rish mapped this turn. Build.MODEL NE2215, Java/NDK/EGL GL_RENDERER and native Vulkan all Adreno730; no intercepted layers; native symbol module observations glGetString direct/EGL/RTLD_DEFAULT libGLESv2.so and Vulkan linked/RTLD_DEFAULT libvulkan.so, all pointer equal YES. P27 storage tests all PASS, consistent with P26 previous real guest success. No real FPS/Outlanders proof.
- NEXTVM user link was previously compared in P21; current upstream main f581a664 still lacks any glGetString/vkGetPhysicalDeviceProperties hook in C++ hook table (has libc path/property GOT patcher), and its Java identity spoof cannot reach native GPU. No GitHub Releases and issue #5 unresolved guest launch path; don't treat it as proven Android15 POCO replacement. Owner previously tried BlackBoxReborn with Outlanders and it failed. No reason to keep swapping full virtualizers.
- New pivot: retain Renjana P26 untouched as controlled owned Guest, use pinned ByteHook MIT PLT candidate to prove reversible glGetString GPU text interception within owned MO app (P29), then separate Vulkan owned dispatch (P30), then Renjana-delivered native helper into owned Guest before JNI (P31) only if prior gates work. Fail closed on real API proof; no game/root/debug-global/ANGLE or GMS account. No P28 APK/Actions. See new research report and session 041.


## P29 2026-11-08 — Shizuku ADB shell not root, first owned GL native hook candidate
- User allows Shizuku as "simulated root". Explicit capability boundary: Rish via Wireless Debugging ADB shell UID2000, not Root UID0; can inspect own Guest processes, ps/meminfo/logcat but cannot bypass production game SELinux/UID isolation, inject arbitrary .so, or claim Zygisk/root. Never write global GPU/ANGLE settings in this phase.
- P28 native-first gate implemented for disposable MO Layer Test v0.9.0 using ByteHook MIT **1.1.0** (pinned due compileSdk35) only on libMOProbeRenderer.so's native linked glGetString PLT reference. Opt-in P29 OFF/ON/OFF test under actual EGL, thread-local fixed owned mock GL_RENDERER only, preserves other enums/threads, tracks hook callback/hit/unhook status. Java/EGL direct proc may still return physical Adreno730; Vulkan untouched.
- First ByteHook v1.1.2 CI run #76 FAILED at AAR metadata requiring compileSdk37; does not reflect native hook correctness. Pin v1.1.0 upstream compileSdk35, ShadowHook1.1.1. Run #77 passed source and APK build, run #78 includes final fixed report string and must be verified separately. **P29 not device tested**.
- Details docs/P29_OWNED_GLES_BYTEHOOK_AR.md, session 042. Do not treat compiled code as a real intercept, and do not replace working Renjana P26 or original MO main for P29. Only owner disposable test APK.

### P29 packaged success, still no device interception proof
- Corrected ByteHook1.1.0 CI #78 completed SUCCESS and released disposable **MO-Layer-Test-v0.9.0.apk** 342024B SHA256 3718601e4e26b31da697a4cdb04f50585fd4405e18af4dc6f1d32c1e73606041 at https://github.com/alwansan/MO/releases/tag/mo-v0.11.0-alpha.11-run78 . Source sha ead4f202ea5b9aa957d6edc9a9532004538836c8. CI only confirms build/tests, P29 actual native A/B/A interception NOT phone tested. Test independent owned installed app first; only then existing Renjana P26 guest. Shizuku ADB shell (UID2000) is read-only aid for ps/meminfo, not Root/closed-app injection. Do not change MO main or original game.
