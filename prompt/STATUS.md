# Current status

## CURRENT — MO P22 CLOSEOUT: Renjana own-app experimental APK built and raw GitHub prerelease VERIFIED (2026-11-08)
- Upstream `josskixg/renjana` pinned SHA `14302a57cd66114c6979acf7ca56c97f845a6841`, Apache-2.0. P22 owned-app CI first run `42429358721` passed upstream tests, APK signing and build but final publish check failed due SHA256SUMS path moved from `renjana-output/`. Fixed workflow at commit `77867f4ec178fadfb25719692be859f1333f2300` with portable checksum list.
- Second run **`42429759178` SUCCESS** incl. unit tests/ARM64 build/signature and publication. Public prerelease `renjana-owned-lab-run2`, direct signed debug APK `Renjana-v0.2.0-arm64-OWNED-LAB-debug.apk` **55,348,455 bytes**, SHA256 `7b62bfad247050e03bcdcefd693cb47b14d45b4c5ac945ca74fa58f092349b63`. https://github.com/alwansan/MO/releases/tag/renjana-owned-lab-run2 ; https://github.com/alwansan/MO/actions/runs/42429759178 . Apache LICENSE/provenance/checksum separately uploaded. No source changes to MO main app or game.
- **Not on-device validated**; first install Renjana and existing `MO-Layer-Test-v0.6.0.apk` only for owned disposable experiment, baseline host Build/Java+NDK GLES/Vulkan, then guest with GMS/signature/anti-detection/fingerprint OFF. Inspect guest vs external fallback from `ps -A -o PID,NAME` or Renjana logs. Rish filtered `logcat` and `dumpsys meminfo` available. No original Outlanders, no Google account or game file mods. Exact steps: `docs/P22_RENJANA_OWNED_LAB_INSTALL_AR.md`; session `prompt/sessions/MO-20261108-CHAT-P22-RENJANA-OWNED-LAB-035.md`.


## CURRENT — MO-20261108-CHAT-P22-RENJANA-OWNED-LAB-035: standalone Renjana ARM64 debug build for owned test app
- Owner requested an actual installation path, allowed continuation of the P21 lightweight no-root container research. P22 source audit pinned `josskixg/renjana` at `14302a57cd66114c6979acf7ca56c97f845a6841` (Apache-2.0), not yet verified on POCO/Android15. Upstream `InstanceLauncher.kt` has fallback **direct external launch with NO isolation**, so this is not GPU spoofing evidence.
- Added separate `.github/workflows/mo-renjana-owned-lab.yml` (only initial push/manual) to fetch *unmodified* fixed upstream source, run tests, build/test APK for arm64 and publish raw **experimental** Renjana APK/Apache license/checksums **only on success**. P22 Actions run `42429358721` started; tests were still `in_progress` on initial check. DO NOT claim an APK ready without successful CI and Release verification. No game or phone files altered and no MO core source changed.
- Added installation guide `docs/P22_RENJANA_OWNED_LAB_INSTALL_AR.md` and session `prompt/sessions/MO-20261108-CHAT-P22-RENJANA-OWNED-LAB-035.md`. Owned existing test APK available at `mo-v0.11.0-alpha.11-run59`: `MO-Layer-Test-v0.6.0.apk`. Initial experiment toggles GMS/Signature Spoof/Anti-Detection/Fingerprint **OFF**, no Google account or Outlanders. After a verified container launch compare Build, Java/NDK GLES, Vulkan and process PID; return filtered logcat if failure; reject direct-launch fallback as success.


## CURRENT — MO-20261108-CHAT-P21-LIGHT-CONTAINERS-034: lightweight rootless Android container ecosystem / no ready game solution
- User requests **tiny open-source Android emulator/virtualized launcher** to run original MHO with fake CPU/GPU device identity without Root, bootloader or heavy VMOS+. Surveyed real sources GitHub, exact code paths, releases, issues and Android15 compatibility.
- Best **lightweight engineering candidate** is Renjana (`josskixg/renjana`; Apache2), but its source says if stub sandbox launch fails it **falls back to regular direct launch with NO isolation**; no direct GL/Vulkan interception found; **no APK Release, no successful CI runs**, MHO untested. NEXTVM (`TanvirHossain2/NEXTVM`, Apache2) implements Build/system-property and file IO hooks but **not glGetString/VkPhysicalDeviceProperties**; no Releases, CI failing/action required and guest-launcher PR #5 open.
- Real rootless guest Android alternatives: Twoyi **abandoned Apr2023, host Android8.1-12**, ROM not fully open build; Ananbox **WIP**, PRoot, issue #13 Android15 black screen, v0.0.2 binary+~190MB ROM. Shelter is efficient work-profile isolation but **cannot spoof GPU/Build**. DroidVM requires Root/hypervisor node and newer Snapdragon; incompatible. BlackBoxReborn already failed MHO on owner's phone and is paused.
- No **verified** OSS low-overhead Android15 VM known that both runs live `com.tencent.mhadv` and makes guest's native GLES/Vulkan read fake Adreno840. Building/running Renjana must start with **owned disposable EGL/Vulkan tester**, confirm sandbox (not no-isolation fallback), memory/frame costs and split/native load; **no original-game cloning, signing, login, device writes or source imports yet**.
- Report: `research/P21_LIGHTWEIGHT_ROOTLESS_ANDROID_CONTAINER_2026-11-08_AR.md`; session `prompt/sessions/MO-20261108-CHAT-P21-LIGHT-CONTAINERS-034.md`. P13 stock ANGLE read-only inspection remains independent non-invasive option. No new APK or CI initiated.


## CURRENT — MO-20261108-CHAT-P20-REMOTECONFIG-PATTERN-033: RemoteConfig 146B structured churn, NOT identified graphics unlock
- Owner ran Termux `inspect_mho_remote_config.py` successfully on 3 previously captured TARs and uploaded `REMOTE_CONFIG_PATTERN.txt`. All three `com.tencent.mhadv/cache/RemoteConfig.config` snapshots are **146 bytes and have different SHA256**; tool reports `opaque/binary-like; no protocol inferred`.
- Crucially **both Low→Medium and Medium→Medium control changed the exact SAME 128 byte offsets**: `16–79` and `82–145` (two 64-byte spans), with unchanged `0–15` and `80–81` (18 bytes). This is evidence of a structured fixed-length binary-like file with repeated churn **even without an accepted graphics quality change**. It is NOT a unique Graphics Quality marker, not proof of encryption, backend device whitelist or supported external override.
- Owner's game UI still explicitly rejects High/Ultra/Concept Art as `This setting isn't currently supported on your device.` No evidence Android/data file changes GPU API strings or unlocks those settings. Do **not** generate speculative `RemoteConfig` patch, `DeviceProfiles.ini` or PAK modifications.
- Detailed finding `docs/P20_MHO_REMOTECONFIG_BYTE_PATTERN_2026-11-08_AR.md`, session `prompt/sessions/MO-20261108-CHAT-P20-REMOTECONFIG-PATTERN-033.md`. No further 230MB TAR rounds needed for same question. Original private data not shared publicly; no code/APK/game changes. Prior MO v0.11 P13 **read-only ANGLE capability** test remains untried on POCO and the next logical separate GPU identity research gate.


## CURRENT — MO-20261108-CHAT-P19-MEDIUM-CONTROL-032: Medium unchanged but 146-byte RemoteConfig and shader cache still change
- Owner uploaded real game screenshot and `MEDIUM_CONTROL.txt` after **Medium → Medium** with Rish `TAR_EXIT=0`; screenshot shows **High, Ultra, Concept Art locked** and explicit popup `This setting isn't currently supported on your device.` Owner tapped these unsupported options, so this was a constant **selected quality** control, not totally idle UI. Top `Device Load: High` is not unlocked preset High.
- Uploaded comparator output **55 files →55 files; 11 changes = 1 added, 9 modified, 1 deleted**. The 146-byte `cache/RemoteConfig.config` hash changes and `GLSL_ES3_1_ANDROID PrecacheBinaries` same-sized member hash changes **even though accepted quality remains Medium**. Across three sessions Medium→Low / Low→Medium / Medium→Medium, RemoteConfig changed in all cases. This **refutes using simple RemoteConfig-did-change as a graphics-setting marker**; its layout/content and any game whitelist role remain unknown. Other changes mostly GCloud, Gamelet, centauri and rotated MHMobile logs; no plaintext GameUserSettings/DeviceProfiles INI evidenced.
- New read-only `tools/inspect_mho_remote_config.py` examines just the 146-byte member inside already-existing TAR/ZIP backups, outputs hashes/format clues/changed offsets without printing raw data, and cannot alter game. Synthetic JUnit-equivalent Python `unittest` tests added under `tools/test_inspect_mho_remote_config.py`; repo CI workflow updated (run `42426253058`, awaiting final outcome). User can generate `REMOTE_CONFIG_PATTERN.txt` in Termux from existing three TARs, no new game launch or archive needed.
- Raw local TAR access attempts with Python/container both returned `ClientError` this session; conclusions grounded in user's textual `MEDIUM_CONTROL.txt` and screenshot only. Do not claim decoded RemoteConfig! Report `docs/P19_MHO_MEDIUM_CONTROL_2026-11-08_AR.md`, session `prompt/sessions/MO-20261108-CHAT-P19-MEDIUM-CONTROL-032.md`.
- Primary MO goal remains **actual independent-game GPU identity spoof / unlocked settings**; unsupported device message strengthens UI eligibility evidence but doesn't prove GLES vs Vulkan vs server policy. No blind Android/data patch, root, shader cache modifications, or target app injection.


## CURRENT — MO-20261108-CHAT-P18-LOW-MEDIUM-031: Outlanders Low→Medium diff confirms 10 file changes; no identifiable graphics override file
- Owner restored in-game **Graphics Quality Medium** from Low (screenshot shows Medium selected, High/Ultra still padlocked), exited, created `MHO_AFTER_GRAPHICS_MEDIUM.tar` with Rish `TAR_EXIT=0` and ran Termux comparator `--all`. User-uploaded `LOW_TO_MEDIUM.txt` says **51 → 55 archive files**, 10 changes = **4 added (3 GCloud logs + MHMobile backup), 6 modified (RemoteConfig146 bytes changed contents, Gamelet log, +56,603 bytes GLSL_ES3_1 shader precache, MHMobile log rotated, centauri mmap and txt logs), 0 deleted**.
- `cache/RemoteConfig.config` **changed in both** previous Medium→Low and new Low→Medium experiments while length remained 146 bytes. Both comparisons ALSO involved game startup, so **no proof** this is the Graphics Quality setting. Prior 8 hotfix resource deletions did not reverse, consistent with maintenance rather than selected graphics level. No new `.ini` or proven editable GPU/FPS setting in external Android/data.
- **Disclosure:** attempted independent read of raw newly uploaded TAR through Python/container; runtime returned `ClientError`. Findings are **grounded in user supplied text diff and screenshot only**; cannot claim binary decoding or identifying RemoteConfig layout. Full report `docs/P18_MHO_LOW_TO_MEDIUM_DIFF_2026-11-08_AR.md`, session `prompt/sessions/MO-20261108-CHAT-P18-LOW-MEDIUM-031.md`; no private game binary published.
- **Next controlled experiment, no game-data writes:** keep Medium unchanged, restart/exit game, snapshot `MHO_MEDIUM_CONTROL.tar`, compare with `MHO_AFTER_GRAPHICS_MEDIUM.tar` using `--all --output MEDIUM_CONTROL.txt`. This checks whether RemoteConfig changes from normal startup even with no quality toggle. Still no proof of Adreno840 spoof or unlocked FPS.
- No Android source changes, new APK, firmware/Root/KernelSU, protected game file edits or build run this chat.


## CURRENT VERIFIED — MO-20261108-CHAT-P17-GRAPHICS-030: Outlanders Medium→Low external-data diff found 18 changes, no GPU/FPS patch
- Owner uploaded `differences_graphics_low.txt` plus `MHO_AFTER_GRAPHICS_LOW.tar` after running stock MHO Global and selecting Graphics Quality **Medium → Low**. Rish TAR_EXIT=0, archive reported 237M; earlier BEFORE.tar 230M at 01:43. User-generated SHA256 comparator report: **BEFORE 53 files / AFTER 51 / 18 changes = 6 added + 8 deleted + 4 modified**.
- Principal modified files: `cache/RemoteConfig.config` 146 bytes unchanged size but different SHA; `RHICache/.../GLSL_ES3_1_ANDROID.../PrecacheBinaries` 55,635,081 bytes unchanged size but different SHA; `MHMobile.log` 9,477,983→13,283,940; `centauriLog.mmap` unchanged 153600 bytes but new SHA. 8 version3392342 HotRes resources deleted, 6 GCloud/Unreal/centauri/Gamelet logs added. Net file payload delta **7,148,030 bytes (6.817 MiB)** reconciled with archive size increase. **No new .ini graphics config**, no proven medium/low-specific persisted file, no evidence RemoteConfig stores GPU whitelist.
- **Causality caveat:** between snapshots the game launched, rotated logs and cleaned/updated HotRes, therefore changes are not necessarily from graphics selection. Uploaded binary TAR could not be directly parsed by Python/container this turn due environment ClientError; all findings are grounded in user-generated text diff; do not falsely claim inspecting RemoteConfig bytes.
- Controlled next step (if user wants): leave existing `MHO_AFTER_GRAPHICS_LOW.tar`, switch Low→Medium using game's own UI, close and Rish-snapshot `MHO_AFTER_GRAPHICS_MEDIUM.tar`, then optional Medium→Low and snapshot `MHO_AFTER_GRAPHICS_LOW2.tar`, compare text reports both directions. **Do not paste guessed INI or alter hotfix PAK/cache**. Detail `docs/P17_MHO_GRAPHICS_LOW_TAR_DIFF_2026-11-08_AR.md`; session `prompt/sessions/MO-20261108-CHAT-P17-GRAPHICS-030.md`. No APK, system or game writes.


## CURRENT DEVICE CONFIRMATION — MO-20261108-CHAT-P16-RISH-DEVICE-029
- Owner sent **verified Rish ADB Shell output:** `uid=2000(shell)`, `ext_data_rw` group and `ls` works in **external** `/storage/emulated/0/Android/data/com.tencent.mhadv` (contains `cache` and `files`). `/system/bin/tar` exists. **No root or private /data/user/0 access**.
- First TAR backup `/storage/emulated/0/Download/MHO_BEFORE.tar` created successfully, immediate `echo $?` = **0**, size **230M**. **MHO_AFTER.tar does not exist yet**, so running the comparator gives the expected "Two real archive files are required"; Python and the downloaded 6626-byte script are working.
- **Next device step:** Keep BEFORE unchanged; change only an allowed in-game UI setting, close game, use Rish `tar -cf .../MHO_AFTER.tar` and confirm exit0, then **exit Rish** and Termux Python compare both `--all --output all_differences.txt`. User uploads just the text diff, not huge archives. This is file-change discovery only, not proof of GPU identity spoof or unlocked FPS.
- Evidence report `docs/P16_TERMUX_RISH_ANDROID_DATA_SNAPSHOTS_AR.md`, session `prompt/sessions/MO-20261108-CHAT-P16-RISH-DEVICE-029.md`. No source code/APK/game file changes in this feedback turn.


## CURRENT — MO-20261108-CHAT-P16-RISH-028: Rish is UID2000 shell, not root; ZIP/TAR comparator repaired and CI passed
- User's Python errors are from **wrong directory and non-existent placeholder snapshots**: script exists in `Download`, not Termux `~`; `BEFORE.zip` and `AFTER.zip` never created. Running `sh rish` switched to Android shell `mondrian:/ $` where Termux `python`/`pkg` are absent. This does **not** require root to fix.
- `/storage/emulated/0/Android/data/com.tencent.mhadv` external data may be accessible to Rish ADB shell UID2000 **only if on-phone `ls` succeeds**; `/data/user/0/com.tencent.mhadv` is private and remains off-limits to normal Termux/Rish. No game files need writing.
- Fixed `tools/compare_android_data_snapshots.py` to accept TAR/TAR.GZ and ZIP, compare incremental SHA256 of archive file contents without extraction, and explain missing placeholder files; added Python tests and dedicated CI workflow. **[run42421428529](https://github.com/alwansan/MO/actions/runs/42421428529) SUCCESS**. No APK rebuilt.
- Practical backup: while game closed, in Rish `tar -cf /storage/emulated/0/Download/MHO_BEFORE.tar -C /storage/emulated/0/Android/data com.tencent.mhadv` only if permitted; after a legitimate in-game option change, repeat `MHO_AFTER.tar`. **Exit Rish**, then Termux runs refreshed Python diff on two actual archive filenames. Details `docs/P16_TERMUX_RISH_ANDROID_DATA_SNAPSHOTS_AR.md` and `prompt/sessions/MO-20261108-CHAT-P16-RISH-028.md`.
- This is **config storage discovery**, not spoofing Adreno840 or unlocking MHO; do not invent Android/data ini/PAK patch. iUnlocker rooted Zygisk integration is not reproducible by copying strings into external data.


## CURRENT — MO-20261108-CHAT-P15-IUNLOCKER-DATA-027: rooted iUnlocker proof vs Android/data no-root goal
- Owner shared 8 TikTok screenshots showing manager fields **OpenGL GPU Adreno840**, **Vulkan GPU Adreno840**, **Vulkan Version 1.3.243**, **Manufacturer RedMagic 11S Pro+**, **Model NX809J** for **`com.garena.game.mho` Garena**; owner's previously installed game `com.tencent.mhadv` Global is a DIFFERENT package. Screenshots are **UI config, not actual independent in-game API readback** or confirmed unlock in the images; video MP4/TikTok couldn't be decoded in this environment, so don't claim watched it.
- Confirmed official **iUnlockerGL v1.1.5-r4 is Magisk/KernelSU + Zygisk module**, intercepts target-app Java/Native GLES/Vulkan at runtime; settings are NOT a simple `Android/data` file. Shizuku UID2000/no root cannot emulate this injection on stock nondebuggable MHO.
- Previous uploaded `com.tencent.mhadv.zip` analysis: **87 ZIP entries, 53 files, no .ini/DeviceProfiles/GameUserSettings plain config**; encoded Unreal logs, binary HotRes resources and GL shader cache. No evidence user-created config in external data will be read; **do not fabricate an FPS-unlock patch** or corrupt resources. PUBG's UserCustom.ini workaround is specific to PUBG and can't be transplanted without proof.
- New safe tool `tools/compare_android_data_snapshots.py` compares owner-exported BEFORE and AFTER Android/data ZIPs around legitimate settings changes **without modifying/extracting private game files**; code authored but **not runtime-tested here** due container tool failure.
- Current core goal remains truly alternate GPU readback **inside a separate game/process**, not MO mock UI. Best next low-risk gate P13 read-only ANGLE scan on POCO; only after true evidence consider reversible supported per-app driver option. Data/archive research `research/P15_IUNLOCKER_KERNELSU_VS_MHO_DATA_2026-11-08_AR.md` and session `prompt/sessions/MO-20261108-CHAT-P15-IUNLOCKER-DATA-027.md`. No APK build, phone, root or game-file changes.


## CURRENT — MO-20261108-CHAT-P14-ARCH-026: archive audited and three rootless strategies compared
- Owner requests serious investigation of (1) launcher that starts original Outlanders after spoofing GPU/CPU identity, (2) very lightweight full Android VM with Google Play account/service support, and (3) edited files inside `Android/data/com.tencent.mhadv`; user uploaded `com.tencent.mhadv.zip`. Research only, not blanket authorization to install VM/clone game or change protected files.
- **USER ZIP audited read-only:** SHA256 `7fbda67c3366a5639f54790860cdeab479a82b5619ed827f1919828dc0756911`, **87 entries =53 files+34 directories**, 241130830 bytes uncompressed. Unreal MHMobile logs are Base64-like encoded, HotResDesc '.json' is binary/nontext, .pak/.ucas/.utoc hotfix data, GLSL_ES3_1_ANDROID PrecacheBinaries ~55.6MB. **No .ini/XML/.properties/DB files** in this partial Android/data archive; private PufferRes referenced but absent. No evidence of editable GameUserSettings.ini or device profile here. Do not publish raw owner data or modify caches/paks.
- **Decision:** launcher-only cannot change separately sandboxed app's native GLES/Vulkan/Java identity; user-space App Virtualization container architecture is *theoretically* closer to goal than full Android VM, but Android15/ARM64/Unreal/GMS/PlayIntegrity/ToS compatibility remains **unproven**. BlackBoxReborn claims Android15 support; README license GPL3 **disagrees with its actual Apache2 LICENSE**, so review provenance before using code. Legacy VA is outdated or unstable. VMOS+ promotes Android15 and GMS plugin, but is heavy and no verified MHO results; Microdroid too stripped to run games.
- **Immediate low-risk next gate unchanged:** installed MO v0.11 P13 ANGLE read-only capabilities from user, no settings writes. Alternative nonroot sandbox should first validate *separate cooperating app* actual GLES/Vulkan API readback, not copy MO fake strings or patch Outlanders. Official Google Play game developer replied 2026-11-06 it plans to expand device compatibility; request POCO support if keeping original app.
- **Account/legal caution:** official MHO EULA addresses unauthorized emulation/mods, detection/termination; Play Integrity can flag virtual/modified environment. No live account VM sign-in or anti-cheat bypass authorized. Detailed report `research/MHO_LAUNCHER_CONTAINER_VM_DATA_2026-11-08_AR.md`; session `prompt/sessions/MO-20261108-CHAT-P14-ARCH-026.md`. No code build/phone or game changes.


## CURRENT VERIFIED DELIVERY — P13 ANGLE READ-ONLY probe v0.11.0-alpha.11
- **CI completed SUCCESS**: [run 42332224614](https://github.com/alwansan/MO/actions/runs/42332224614) at full source `5666cb45c6f8601acf14aeaca02a8b626979ff63`, new strict ANGLE UID2000 read-only allowlist JUnit passes, both debug APKs built and published.
- Public [Release run59](https://github.com/alwansan/MO/releases/tag/mo-v0.11.0-alpha.11-run59): `MO-Graphics-Probe-v0.11.0-alpha.11.apk` **1,167,998 bytes**, SHA256 `4372a3b077632925df8c51161843f110c53777d9009254c6c51c2ffd6fe5925f`; owned test v0.6.0 unchanged source, newly debug-signed. **This does not prove real ANGLE availability or Adreno840 spoof on user's phone**.
- Main app contains 05B P13 zero-write ANGLE package/driver selection/property reading and logs. Shizuku UID2000 and GRANTED required. **P13 on-phone report pending**; first actual cross-app spoof proof is still outstanding. Don't set global `angle_gl_driver_all_angle` due to AOSP boot risk.
- Device guide `docs/P13_ANGLE_READONLY_DEVICE_TEST_AR.md`, session `prompt/sessions/MO-20261107-CHAT-P13-ANGLE-READONLY-025.md`. Last verified P12 four-key cleanup was prior-time, not a fresh reading after install. Per-CI debug signatures may require reinstall and lose app-private history; do not delete active GPU Debug backup.


## CURRENT — MO-20261107-CHAT-P13-ANGLE-READONLY-025: P13 read-only ANGLE capability probe implemented, cross-app spoof still UNPROVEN
- Original project goal stays **real GPU identity observed by an independent app**, e.g. `glGetString(GL_RENDERER)` reading a chosen `Adreno 840` rather than physical `Adreno 730`; original Outlanders graphics/FPS unlocking is not demonstrated. P10 local synthetic menu, P11 own draw callbacks and P12 logs are diagnostics only.
- **P13 main MO v0.11.0-alpha.11 source implemented:** new **05B / P13 ANGLE CAPABILITY — READ ONLY** screen button. Shizuku UID2000 `getAngleSnapshot()` with a 14-command strict `AngleReadOnlyCommandPolicy` for `pm list/path` ANGLE packages, `settings get global` driver selection and dangerous all-app switch (read only), `getprop debug.angle.gl_renderer/vendor/version` and build debug flags. All readbacks are included in MO's timestamped log and copied report; **no system writes or driver activation**.
- Official AOSP ANGLE documents `debug.angle.gl_renderer` overriding GL_RENDERER **only when target runs ANGLE**; its availability/accessibility on POCO remains **unknown** until on-device P13 readback. It cannot automatically replace Vulkan physical device / Java Build model or improve GPU hardware.
- Source complete at `5666cb45c6f8601acf14aeaca02a8b626979ff63`. CI [42332224614](https://github.com/alwansan/MO/actions/runs/42332224614) unit and APK builds **passed**, publishing asset stage pending final verification. Interim runs may cancel from successive pushes; only newest complete-source build matters.
- **Critical safety:** AOSP warns `angle_gl_driver_all_angle=1` may prevent boot; new code only `settings get`, no switch. No new Shizuku changes, root/bootloader/VM/clone/game modification or unconsented device experiment. Device report guide `docs/P13_ANGLE_READONLY_DEVICE_TEST_AR.md`, session `prompt/sessions/MO-20261107-CHAT-P13-ANGLE-READONLY-025.md`. Later actual independent-app readback A/B requires explicit user consent and reversible snapshot.


## CURRENT — MO-20261107-CHAT-P13-REAL-SPOOF-024: owner reset primary mission to genuine cross-app GPU identity spoof
- **Owner correction is authoritative:** MO's primary goal is that **an independent app/game reads a higher reported GPU identity**, e.g. `Adreno840` instead of real `Adreno730`, hoping to unlock graphics/FPS options. Previous P10 fictional selection, P11 owned callback rates and P12 logs are supporting diagnostics **not mission success**.
- **New credible, conditional candidate:** AOSP ANGLE documents `debug.angle.gl_renderer` / `debug.angle.gl_vendor` overrides and its source `Context::initRendererString` reads these properties; system ANGLE supports per-app driver selection where OEM enables it. This *can* affect a real target app's GLES `glGetString(GL_RENDERER)` **only if** that app runs ANGLE and the property override is accepted/read. **No POCO device eligibility or shell write proved**, and it cannot change native Vulkan or Java Build automatically.
- Next investigation: **read-only system ANGLE availability** and property/driver selection probe on user device via optional Shizuku; no blind global writes. With explicit consent only, reversible owned independent-app GL A/B proving actual API readback before/after/rollback; do not test stock Outlanders first.
- Android official GLES/Vulkan loader gate remains: stock `com.tencent.mhadv` targetSdk36, Debuggable NO, external layer opt-in NO; Shizuku ADB UID2000 cannot inject into that process. Do not advertise `Adreno840 applied` or `120 FPS unlocked`.
- Engineering focus `research/REAL_GPU_IDENTITY_SPOOF_ANDROID15_ANGLE_AR.md` and `prompt/sessions/MO-20261107-CHAT-P13-REAL-SPOOF-024.md`. No new APK, code, device settings writes, or builds this research cycle. Existing v0.10/v0.6 release remains last tested.


## CURRENT — MO-20261107-CHAT-P11-REPEAT-023: P10 all local modes and P11 pacing replicated; no new APK needed
- Latest **new** user-uploaded `log (2).txt` is a **41,724-byte cumulative export** (SHA256 `dbdfe2ecfa429012378459ae212e34c3181619ebe699dba055e30676d5bb7415`), NOT the prior 15,982-byte same-named attachment. **201 logged events** = 85 retained older PID31111 events (including follow-up export/pause) + **116 newly observed** PID3604 events at 05:45:19–05:47:32. Original raw log stays private; no public GitHub attachment.
- **P10 now all 7 mock identity modes device-verified:** ACTUAL and each MODEL/CPU/JAVA_GLES/NATIVE_GLES/VULKAN single simulation return `mock_combined120=false`; only ALL_SIMULATED `true`, reversible on changing mode. Repeated actual Java/NDK GLES, eglGetProcAddress, native Vulkan remain **Adreno(TM)730**, actual SM8475/120Hz, so **no real device spoof or commercial-game FPS unlock**.
- **P11 independent second Android15 pacing PASS:** new PID3604 `stride1` 13 one-second samples mean UI/draw 120.061/120.061 /s; `stride2` 16 samples 119.667/59.834 /s; `stride4` 9 samples 120.478/30.037 /s. Draw request ratios **1, 1/2, 1/4**, reproducing prior separate PID31111 test. These are **own onDrawFrame callbacks, not presented/displayed FPS** or Outlanders FPS.
- P12 cumulative retention across process restart **verified**. Earlier PID31111 follow-up includes `EXPORT_RESULT` stating saved Download/log (2).txt and clipboard copy returned success for 15943 chars, plus `FRAME_LAB_PAUSE` at 05:39:37. This is **in-app copy report**, not independent clipboard paste proof. New snapshot ends at export USER_ACTION before its own completion can be recorded. No explicitly logged Java crash/ERROR in 201 records. `TRIM_MEMORY=20` UI hidden, not proof of OOM.
- No latest **Shizuku four-key readback** in test-only log. Previous main app's 05:26 cleanup remains last directly verified global state; no layer activation needed. Detailed report `docs/DEVICE_FEEDBACK_P11_REPEAT_AND_IDENTITY_2026-11-07.md`, session `prompt/sessions/MO-20261107-CHAT-P11-REPEAT-023.md`.
- Code/source and workflow **unchanged**; latest successful CI remains `42327821781`, existing v0.6 test APK sufficient. No new build, root, bootloader, game changes or system writes.


## CURRENT — MO-20261107-CHAT-P11-STRIDE-022: P11 owned frame-request cadence COMPLETE on device
- New owner-uploaded private `log (2).txt` from **MO Layer Test v0.6.0** on POCO F5 Pro Android15 contains 76 timestamped events (05:38:31–05:39:35, PID31111), **45 P11 FRAME_SAMPLE windows** covering all stride modes. Original raw file was **not** added to public MO.
- **P11 PASS:** stride1 (14 windows) average Choreographer/own GLES draw callbacks **119.632/119.632 per second**; stride2 (19 windows) **119.127/59.563 per second**; stride4 (12 windows) **120.316/30.079 per second**. Draw/request ratios approximately **1 : 1/2 : 1/4**, so all three app-side pacing modes are **device-validated**. This is NOT real display-present FPS, Outlanders FPS, GPU performance or an unlocked game menu.
- New test process **PID31111** started at 05:38:31, after prior MAIN MO guarded GPU Debug cleanup (05:25:51, two subsequent clean readbacks through 05:26:04). Its real Java/NDK/eglGetProcAddress GL and native Vulkan name remain **Adreno730**; `Layer intercepted NO`. New test-only log did **not** read current four global GPU keys; do not claim independent Shizuku confirmation at 05:38.
- No explicit `ERROR`/`UNCAUGHT_CRASH` events in this ~65s log. The frame lab was active when export snapshot was taken, so absence of a later pause entry does not imply failed lifecycle cleanup.
- Detailed evidence: `docs/DEVICE_FEEDBACK_P11_STRIDE_2026-11-07.md` and `prompt/sessions/MO-20261107-CHAT-P11-STRIDE-022.md`. P12 Download export previously confirmed. **No new APK, new GPU layer activation or privileged changes required**. Latest successful release remains run53 / GitHub Actions 42327821781.


## CURRENT — MO-20261107-CHAT-P12-DEVICE-LOGS-021: P12 log export verified on POCO / P11 stride1 passed / GPU cleanup confirmed
- Owner uploaded **two real Android15 logs** from main `com.alwansan.mo` v0.10.0-alpha.10 (35 events, PID22510, 05:23:23–05:26:04) and disposable test `com.alwansan.mo.glesprobe` v0.6.0 (119 events, PID23395, 05:24:09–05:25:37), **154 parseable timestamped events total**. Android Downloads export to real text files is device-proven; clipboard copy, crash traces and 3×3MiB rotation not independently tested. Raw private logs **not** uploaded to public MO.
- **IMPORTANT VERIFIED GPU Debug cleanup:** main log at 05:24:29 shows **action=1 activation** of owned layer (enable=1 and 3 MO markers). At 05:25:51 owner action=4 **guarded MO-owned stale cleanup** succeeds, reads `enable_gpu_debug_layers=0` and 3 MO markers **not present**; actions=3 at 05:25:55 and 05:26:02 plus latest main UI snapshot reconfirm. This is bounded cleanup, **not exact historical baseline restore**, but the latest verified four-key state was clean. Do not reactivate for routine P11 logging.
- P11 device: all **21 FRAME_SAMPLE windows** are `stride=1`, mean owned Choreographer/GL draw callback rates both **120.47/sec**, range 118.597–121.383; no evidence of real SurfaceFlinger-presented FPS nor game FPS. `stride=2` and `stride=4` NOT TESTED. P10 actual/Vulkan-only fake gate false, ALL_SIMULATED true, physically Adreno730.
- Owned probe PID23395 began **before** user enabled layer; renderer reads after enable were from the **same process**, not a newly started loader process. `Layer intercepted NO` thus does not settle new-process loader behavior. No probe fresh PID **after cleanup** in files. `TRIM_MEMORY level20` means UI hidden, not OOM. No logged Java crashes/errors in these intervals.
- Next: optional fresh clean owned-test process & stride2 then stride4 for ~5sec each, export a fresh `log.txt`. No new APK/build needed: last P12 Actions run `42327821781` succeeded. Detailed evidence `docs/DEVICE_FEEDBACK_P12_LOGS_2026-11-07.md`; session `prompt/sessions/MO-20261107-CHAT-P12-DEVICE-LOGS-021.md`.


## CURRENT — MO-20261107-CHAT-P12-LOG-EXPORT-020: P12 log export APKs built; phone test pending
- User asked for one-tap full available event log + clipboard copy and direct `/storage/emulated/0/Download/log.txt` save before testing P11. Implemented in **both MO apps** with app-private asynchronous bounded event history (3×3MiB), timestamps in local time including milliseconds+timezone, uptime, PID/thread, lifecycle, owned GLES/Vulkan/Identity/P11 frame counters, main MO Shizuku output, errors/uncaught Java crashes (best effort). Neither can access third-party game/system-wide logs.
- User-visible export/copy buttons use MediaStore.Downloads without broad storage permission. Clipboard copies full ≤180000 chars, otherwise the newest 180000 (explicitly marked), while Download contains full currently retained history; OS filename collisions may rename log.txt and UI shows actual name. No automatic remote upload.
- **CI PASSED:** [42327821781](https://github.com/alwansan/MO/actions/runs/42327821781) at complete source `d40d0b6ec70d14ede1731eac2eae4a4f08e7ff81`, both module tests and APK builds/releases. Public https://github.com/alwansan/MO/releases/tag/mo-v0.10.0-alpha.10-run53: `MO-Graphics-Probe-v0.10.0-alpha.10.apk` 1,151,614 bytes SHA256 `a8e227174036732eb31ab7a27e667981e8f6da16102c9f25fe1d737a85fe86ce`; `MO-Layer-Test-v0.6.0.apk` 208,508 bytes SHA256 `ce3771295b2728fad25342ca4c5c86f21904673df55115d56a207615da02ae1f`.
- **No phone validation** of new export UI/MediaStore/clipboard or P11 drawing yet. Guide `docs/DIAGNOSTIC_LOG_EXPORT_AR.md`; session `prompt/sessions/MO-20261107-CHAT-P12-LOG-EXPORT-020.md`. GitHub builds are not device proof.
- Prior user self-reported restoring GPU Debug and deleting one app, but did not identify package or provide independent 4-key readback; do not claim exact final system state. New log buttons themselves never write developer GPU settings. No root/bootloader/game modification.


## CURRENT — MO-20261107-CHAT-P11-FRAME-PACING-019 (supersedes earlier P10 GPU Debug status)
- Owner states **they restored GPU Debug and deleted an app** after P10. They did not specify whether MO Graphics Probe or the disposable Layer Test was deleted, and provided no post-restore four-key readback. Treat it as **owner-reported rollback, not independently verified system settings**. Do not require installing the main MO solely for P11; do not reactivate GPU debug layers.
- P11 source: **measured frame callback lab** added only to the disposable `probe/` app, version `0.5.0`. `FramePacingLabActivity` measures Choreographer callbacks/s and GLES Renderer.onDrawFrame callbacks/s under stride 1,2,4, **not SurfaceFlinger-presented FPS or other-game FPS**. `GameIdentityLabActivity` gets an internal entry button and Android15 inset correction. App main stays source v0.9.0-alpha.9.
- Unit test code covers sample arithmetic, independent rates, clock/count resets. Evidence/guide `docs/OWNED_FRAME_PACING_LAB_AR.md`; session `prompt/sessions/MO-20261107-CHAT-P11-FRAME-PACING-019.md`.
- **CI verified success:** GitHub Actions https://github.com/alwansan/MO/actions/runs/42325865139 at `9abd11a3cbc3062836ceef6f2facd968286faa6f` passed app/probe JUnit tests, both APK builds and Release upload. Public raw APK https://github.com/alwansan/MO/releases/tag/mo-v0.9.0-alpha.9-run32 : `MO-Layer-Test-v0.5.0.apk`, 182484 bytes, SHA256 `5aca59d8dc61ec0a1e3763963de96974f02c62f60d15d7041233d774afae37d8`. **P11 phone test still pending.**
- No on-device P11 validation, game FPS unlock, root, APK clone, global Android settings writes, or proprietary code import. P10 true/fictional identity result on POCO remains as documented.


## CURRENT — MO-20261107-CHAT-P10-DEVICE-018 (supersedes older P10 and P9 clean-state notes)
- **P10 owned identity lab ON-DEVICE PASS (limited)** from three owner screenshots: PID `5783`, Build.MODEL `23013PC75G`, SoC `SM8475`, Java GLES, native GLES and native Vulkan `Adreno (TM) 730`, panel `120.0 Hz`. Mode0 actual-only **fake combined FPS LOCKED**; mode6 all-local-synthetic **fake combined FPS UNLOCKED**; real readings unchanged. This proves mock UI only, **not Outlanders FPS/graphics unlock**. Individual modes 1–5 lack phone screenshots.
- **CRITICAL: newer owner MO v0.9 report supersedes prior clean-state evidence:** `enable_gpu_debug_layers=1` with `gpu_debug_app=com.alwansan.mo.glesprobe`, `gpu_debug_layers_gles=libMOGlesProofLayer.so`, `gpu_debug_layer_app=com.alwansan.mo.glesprobe`, corroborated by both SHIZUKU SYSTEM and `[READBACK AFTER ACTION]`. Latest **reported** GPU Debug settings are active despite PID5783 showing no intercepted renderer. The cause/time of activation was not established; mock dropdown cannot activate system settings.
- Immediate safety priority: owner should preserve installed MO main/app data and use its valid normal saved-setting restore if present, then obtain fresh four-key readback; if restore cannot proceed, collect the exact error and use only a separately consented, narrowly guarded MO-marker cleanup as fallback. No new layer activation, reinstall, upgrade, or game modifications while active.
- A fresh **post-restoration** owned test PID and physical renderer is still pending; PID5783 with active keys is not post-rollback proof.
- Evidence: `docs/DEVICE_FEEDBACK_P10_2026-11-07.md` and `prompt/sessions/MO-20261107-CHAT-P10-DEVICE-018.md`. No new source APK or Actions build; last verified P10 CI 42324225397.


## CURRENT — MO-20261107-CHAT-P10-IDENTITY-LAB-017 (supersedes older status below)
- P10 **original, owned-only Game Identity Laboratory** source implemented inside `probe` v0.4.0; `app` main version remains v0.9.0-alpha.9.
- Success: [GitHub Actions 42324225397](https://github.com/alwansan/MO/actions/runs/42324225397) at source `4166210d4fb5e62b391b759f3b4508eb50d02e97`, passed Java unit tests, NDK native ARM64 builds, both APK builds and release upload.
- Public [run25 prerelease](https://github.com/alwansan/MO/releases/tag/mo-v0.9.0-alpha.9-run25): `MO-Layer-Test-v0.4.0.apk` 172148 bytes SHA-256 `6fdc64c09d76bd0b071d46f5e10f850347a79212256a846a745ac9e8263095dd`. **Do NOT upgrade/uninstall current MO main** to use the owned lab: debug signing changes per run.
- Lab: pure Java fake identity menu gates by Build.MODEL, CPU, Java GLES, native GLES, Vulkan and real screen Hz, uses own-process read-only EGL/NDK/Vulkan measurements. Mock mode changes only strings in activity, not OS or games. Docs `docs/OWNED_GAME_IDENTITY_LAB_AR.md`.
- Previous owner device report v0.9 verified last post-cleanup GPU Debug `enable=0` with three MO keys absent. No **new PID recorded after final cleanup** yet, and no P10 phone test. Before probe APK replacement, re-confirm four keys clean; do not trigger a new GPU layer experiment.
- Stock Outlanders `com.tencent.mhadv` is non-debuggable and opt-in NO; no rootless official layer injection or real FPS unlock. New session `prompt/sessions/MO-20261107-CHAT-P10-IDENTITY-LAB-017.md`, origin chat.


## CURRENT — MO-20261107-CHAT-ZYGISK-COPG-AUDIT-016 (supersedes older audit entries)
- Stage: **P9 device evidence verified partially + research extension**. No new Android build or live game modification performed this session.
- Owner's **last v0.9.0-alpha.9 pasted report** contains consistent `[SHIZUKU SYSTEM READ-ONLY]` and `[READBACK AFTER ACTION]`: `enable_gpu_debug_layers=0`; `gpu_debug_app`, `gpu_debug_layers_gles`, `gpu_debug_layer_app` **not present**. The v0.9 post-action UI refresh and MO-owned cleanup both worked at the reported moment. This cleanup is not guaranteed exact restoration of the unknown historical baseline.
- Earlier screenshot PID16719 shows real Adreno730 in all three owned test paths (Java/NDK/eglGetProcAddress), but was around 04:10, before a later 04:12 layer activation screenshot; therefore **post-final-cleanup fresh-process renderer is still unverified**, not a full A/B closure. No further cleanup is needed while readback remains 0/absent.
- Live upstream analysis added: `research/ZYGISK_COPG_NO_ROOT_FEASIBILITY_AR.md`; phone analysis: `docs/DEVICE_FEEDBACK_ALPHA9_2026-11-07.md`; new session: `prompt/sessions/MO-20261107-CHAT-ZYGISK-COPG-AUDIT-016.md`.
- Zygisk Next, ReZygisk, NeoZygisk and COPG all depend on rooted process/bootstrap infrastructure; no stock Shizuku injection into Outlanders. Zygisk Next newer code restrictions, ReZygisk README/license mismatch, COPG source completeness caveat documented.
- Next: optional read-only four-key recheck and **new PID after final cleanup** from disposable MO Layer Test only, then clean-room Game Identity Laboratory design (owned app) with no stock game patch. P9 release remains v0.9, latest previously verified run 42316925749.

## Historical P9 audit (2026-11-07)

## CURRENT — 2026-11-07 (supersedes historical P0 summary below)
- Phase: **P9 implementation / waiting for real-device clean GPU Debug readback**. Source and release v0.9 exist; this is **not** Planning-only.
- Audit session: `MO-20261107-CHAT-P9-AUDIT-015`; verified starting main HEAD `3c3c20a51ce976c64984ea3a076cf5bd9a5bec5b`.
- Latest verified APK CI: [run 42316925749](https://github.com/alwansan/MO/actions/runs/42316925749), completed success at source `968e13d19a956a0016c4e57609ca744c253da3db`.
- Latest verified direct raw APK prerelease: [mo-v0.9.0-alpha.9-run16](https://github.com/alwansan/MO/releases/tag/mo-v0.9.0-alpha.9-run16). Latest documentation-only HEAD is newer than the built source.
- Last **device** evidence is from installed v0.8: guarded cleanup returned OK, but shell text within the copied report was stale; **fresh independent four-key readback and test-app fresh-process renderer are still unverified**.
- NEXT SAFE ACTION: on installed v0.8 press `تشخيص GLES (قراءة فقط)`, share all four key values; force-stop/reopen *owned MO Layer Test only* and send new PID + Java/NDK/eglGetProcAddress renderer. **Do not re-enable experiment, uninstall, or upgrade MO before cleanup is verified**.
- No new source changes, new APK build, privileged commands, or device tests in audit session. `docs/DEVICE_FEEDBACK_ALPHA8_2026-11-07.md` documents last actual phone evidence.

## Historical P0 starting snapshot (retained for provenance)

- Date (Asia/Baghdad): 2026-11-07
- Phase: **P0 PLANNING / RESEARCH**
- Source tree: GitHub alwansan/MO main
- Session ID: `MO-20261107-PLANNING-001`
- Deliverables: project plan, architecture, research, privilege matrix, licensing, restore prompt, source evidence, tests, ADR, session log.
- **No Android application source or APK built. No bootloader/root/ROM changes.**
- Confirmed blocking constraint: iUnlockerGL Zygisk behavior cannot be replicated by merely installing its ZIP with non-root Shizuku.
- Confirmed opportunity: standalone Android app with ZIP inspection/extraction; Shizuku optional ADB diagnostics; conditional ANGLE testing if device supports it.
- Blockers for full original functionality: Android privilege boundary and iUnlockerGL derivative-use license.
- Device tests: not run (no direct device access).
- Next phase proposed: P1 Kotlin skeleton + permission UI + non-destructive diagnostics **after user explicitly moves out of planning**.
- Open questions for actual device validation: Shizuku active UID, ANGLE availability/selection on HyperOS, ability to install extracted app UI normally, effects of OEM security restrictions; none required to complete P0.
- Resume: `prompt/RESTORE_AR.md`.

## Update from MO-20261107-CHAT-STATIC-002 (verified)
- Latest verified session: `MO-20261107-CHAT-STATIC-002`, read-only local archive tests, **no build/on-device experiment**.
- New screenshots: original iUnlocker app runs but reports `No root permission`, `revision null`, `NOT GRANTED`.
- Latest archive comparison: same critical APK/Zygisk/install scripts; 98 actual files in each of 3 archives; four missing paths declared by installer. See `research/reports/2026-11-07-static-audit.md`.
- **New viable conditional research path:** Android **external GLES/Vulkan layer** for **debuggable or explicit opt-in** applications; stock nondebuggable closed games do not permit general external injection. Details in `docs/ROOTLESS_GRAPHICS_LAYERS_AR.md`.
- 2/2 local Python synthetic unit tests passed; archive checks executed (3/3). There was **no real-device integration test**.
- Next engineering proof: original self-built GLES probe and own passive layer, invoked via Shizuku-permitted settings with exact rollback. The owner requests no root/unlock.
- CAUTION: `angle_gl_driver_all_angle=1` excluded because AOSP warns it can make device unbootable.

## Latest update MO-20261107-CHAT-ECOSYSTEM-003 — expanded ecosystem research
- Phase remains **P0 RESEARCH / NO DEVICE CHANGES / NO APK BUILD**.
- Reviewed published source and docs: GAMA, KaosGFX, CatSmoker, LSPatch, libadrenotools, root Zygisk equivalents, containers, Android ANGLE/Game Mode.
- New promising path: **APK-embedded bootstrap on a disposable app we own**, inspired by KaosGFX + libadrenotools; works conceptually without root because app loads its own library. Shizuku assists with install/staging but cannot inject unmodified games.
- CRITICAL: upstream KaosGFX source automatically uninstalls on certificate mismatch; backups do not include app internal data. Never expose this risk to owner's original games.
- GAMA mechanism actually tweaks HWUI, not GPU capabilities; CatSmoker rootless only game configs/settings.
- Tool uploads inspected as archive metadata only; no execution, see research/UPLOADED_TOOLING_INVENTORY.md.
- Deliverables: research/ROOTLESS_ECOSYSTEM_COMPARISON_AR.md, research/POC_TEST_MATRIX_AR.md, research/UPLOADED_TOOLING_INVENTORY.md, and this session log.
- Next (only after authorized implementation phase): instrument independent debug GLES and Vulkan probe; Shizuku read-only UID; official layers; self-owned APK bootstrap with unique signature; rollback verification.

## MO-20261107-CHAT-GAME-PROFILES-004 — focused gaming identity/FPS investigation
- Goal clarified: per-game device/CPU/GPU spoof to unlock disabled FPS/graphics tiers on stock unrooted phone.
- New read-only upstream source comparisons: Xposed Game-Unlocker, mixed-architecture GameUnlocker, ULTRA model props, two Shizuku PUBG GFX config editors. Documented real code vs advertised capabilities.
- Latest research docs `research/GAME_PROFILE_SPOOFING_AR.md` and `research/POCO_GAME_COMPATIBILITY_AR.md`. Device specs corrected (8+ Gen1).
- Verified public announcement PUBG 4.4 first-batch 120 FPS Snapdragon 8 Gen3+, with exceptions/device expansions; not proof of actual in-game current 4.6/4.7 status on POCO.
- New risk: official PUBG policy warns of permanent bans for unauthorized game modifications. No patching user games or account tests.
- Status remains P0 planning/research. No Android APK built or Shizuku device experiment run in this chat.

## Latest MO-20261107-CHAT-BUY-VS-BUILD-005
P0 research only. Game clarified Outlanders com.tencent.mhadv, not Monster Hunter Now. Ready app comparison finished and source-level ShizukuPlus audit. App Cloner 4.0.12 promising for cloned apps but signature changes; VMOS+ 1.1.4 ARM64 Android15 system20013 supports virtual Magisk/ZygiskNext/LSPosed without rooting physical host, but guest root outside Shizuku-only and games unproven. ShizukuPlus's spoof only intercepts shell getprop commands and is NOT a universal game spoofer. No verified working solution for original Outlanders on stock host. Do not build until ready alternatives evaluated safely; require separate permission for any guest-root setup. No phone action or APK build. Read new research documents.

## P1 development underway — MO-20261107-CHAT-IMPLEMENT-006
- Owner explicitly authorized implementation ("ابدأ بالصنع التطبيق ... Continue") on 2026-11-07. No cloning, VMOS, root or unlock.
- Source commit `ca3378fae3b2d5ae9f488486d7af0332856f869a` adds native Android read-only app + Shizuku 13.1.5, EGL probe, package inventory, JUnit, GitHub Action.
- First CI attempt: https://github.com/alwansan/MO/actions/runs/42305774716 FAILED **before compilation** in android-actions/setup-android@v3 because it tried unavailable SDK package 'tools'. This follow-up corrects workflow to use preinstalled runner SDK. Check new run status; don't claim APK built until actual success.
- Next: repair any compiler/dependency issues from CI; publish only debug artifact, ask user for on-device feedback.

## P1 build verification — same session MO-20261107-CHAT-IMPLEMENT-006
- **CI success confirmed**: https://github.com/alwansan/MO/actions/runs/42305983786 (push SHA 817960c40d97ad5216224920ff1221ccc602c9f8).
- JUnit `testDebugUnitTest` success; `assembleDebug` success; APK hash step success; artifact uploaded.
- Artifact name `MO-Graphics-Probe-debug-2`, artifact ID 13545813166. Installable APK `app-debug.apk`, 112,063 bytes, SHA256 `13c72cbb626e4ec6238df7e5c9262c5781b1f819487753c568c90befe2a2fba2`.
- Connector artifact downloaded and exact bytes rechecked locally (APK ZIP CRC good, 3 DEX files). This is **not** a real-device UI test.
- **Next waiting on POCO device evidence**: install debug APK, authorize Shizuku through its normal UI, confirm binder UID 2000/GLES renderer/game version, copy report.
- No root, no game patches. Current P1 is diagnostics only. Further P2 code should build against this successful baseline.

## Latest MO-20261107-CHAT-P2-DEVICE-007
User on-device proof confirms MO P1 success: Shizuku UID2000 permission GRANTED API13, Qualcomm Adreno730 GLES3.2 V@0615.95, Outlanders global com.tencent.mhadv v1.0.9.0.3308842, 120Hz. P2 source implements read-only Shizuku UserService + Android Vulkan declared features, version0.2.0-alpha.2. Build validation required from GitHub Actions. No on-device P2 test, no game spoof/unlock.

## P2 CI verified 2026-11-07
- GitHub Actions run https://github.com/alwansan/MO/actions/runs/42307946638 **COMPLETED SUCCESS** for commit 598fc91eb3324762765ef9e7a38fc2c7410e23ae.
- JUnit tests including read-only allowlist passed; Android build and artifact upload passed.
- Artifact `MO-Graphics-Probe-debug-3` ID `13547166555`, APK 125919 bytes, SHA256 `7a98dbbb0393256210b76249c79505df34b89e34e98c3677545cd0d6b5e23d5b`.
- APK ZIP and artifact CRC checked after download; direct v0.2.0-alpha2 APK provided in current chat.
- **Still untested on physical device:** Shizuku UserService connection, HyperOS cmd game list-modes/list-configs, ANGLE reads, Vulkan feature reporting. Need owner report before planning write operations.

## Signing caveat alpha1 → alpha2
- Verified v2 APK signature certificate SHA256 `4d2e87fa998d2c8c901c6e8f21417fbe28ce6b2f8109e35cb8dd7ef46f1be365` differs from v1 `6ea43d29063eae454fa7769c20392cd58effe4653585d79df5c5384b3ce7a2da` because GitHub Actions uses ephemeral per-run debug keystore. Android in-place update therefore fails with signature mismatch.
- Owner told to **uninstall MO Graphics Probe only**, then install alpha2; neither Shizuku nor games should be removed. P1 app had no persistent user/game data.
- Future stable update signing must use private signing key stored in GitHub Actions Secrets (never commit private signing key to public MO). Add workflow support after secure credential setup; no key can be recovered from unsigned release bundles or APK.

## Latest MO-20261107-CHAT-P3-VULKAN-008 — P2 device report verified; P3 native Vulkan source
- User-supplied MO v0.2 report proves Shizuku UID2000 GRANTED, Game Mode standard/custom no intervention; ANGLE pkgs/values null; GPU debug layer 0; ro.board.platform taro and ro.hardware.vulkan adreno. Android declares Vulkan 1.1.
- Stored evidence docs/DEVICE_FEEDBACK_ALPHA2_2026-11-07.md. **No game identity modification or FPS unlock has occurred**.
- New source v0.3.0-alpha.3: JNI Vulkan native GPU properties in own process, ARM64 NDK/CMake, fixed report literal \\n. Check latest GitHub Actions and device before claiming full success.
- Still debug APK ephemeral signing; uninstall only MO to install update if signature mismatch, never game or Shizuku.

## P3 build success — 2026-11-07
- GitHub Actions https://github.com/alwansan/MO/actions/runs/42308837332 SUCCESS on source commit `eef8fb46f8ab49412ab4ab04554128e56b0dad36`.
- `testDebugUnitTest`, `assembleDebug`, SHA-256, artifact upload successful. Native ARM64 `libmo_vulkan.so` present in APK.
- Artifact `MO-Graphics-Probe-debug-4` ID `13547731335`; APK SHA-256 `203b8c47d3da67cca1e6ffe212682824d67a864a556b6cd12d7717b7f14dbe92`, APK 1,102,410 bytes. Downloaded, ZIP CRC and native library checked.
- Direct user APK provided in chat as `MO-Graphics-Probe-v0.3.0-alpha3.apk`. **Device Vulkan runtime test still pending**; v0.3 compile success is not native runtime success.
- CI per-run debug signing may require reinstall of MO only. Do not uninstall Shizuku or game. Next: user's copied Vulkan Native GPU section, plus entire report.

## Latest MO-20261107-CHAT-P4-LAYER-009: v0.3 native Vulkan confirmed; P4 owned-app layer proof in source
User report proves native Vulkan API1.1.128 GPU Adreno730 vendor0x5143 device0x7030002 driver0x8026705F in MO process, P3 real device PASS. See docs/DEVICE_FEEDBACK_ALPHA3_2026-11-07.md.
P4 owns disposable debug app `com.alwansan.mo.glesprobe`, original GLES interception library for glGetString(GL_RENDERER) and clearly marked synthetic frame-tier profile. No system settings or games changed yet; next controlled Shizuku per-app settings and rollback, source in probe/. Need CI compile + on-device test before declaring injection success. No root/boot unlock/VM/clone.

## P4 v0.4 UI and controller completed in source
- Typed Shizuku Service now has captureLayerSettings/activateOwnedLayer/restoreOwnedLayer, fixed only to disposable com.alwansan.mo.glesprobe. Four exact Settings.Global keys with before/after presence/value and rollback backup in MO app data.
- UI explicit opt-in, restore and launch test app. No changes on user's phone yet, test app does not touch Outlanders.
- Need verify latest GitHub Actions run; then test both APKs on POCO and restore settings before updating/removing MO.

## P4 build complete — 2026-11-07
- Final source commit 00558159502af56d8451ee03dac57e04d9bd34d3; GitHub Actions https://github.com/alwansan/MO/actions/runs/42310714620 SUCCESS (tests and both APK builds).
- Artifact MO-Graphics-Probe-debug-8 ID 13547164678; contains app/build/outputs/apk/debug/app-debug.apk and probe/build/outputs/apk/debug/probe-debug.apk.
- Main APK v0.4.0-alpha.4 1,118,830 bytes SHA256 74e50eece4eae020d7a9808f97552cbc5d843bf37142b8818282ab106c71b8f7.
- Separate MO Layer Test v0.1.0 17,463 bytes SHA256 180c32ab62a06f8e26686d651ad274cf316eb2828a1e52a73a29bca98670b4d0, contains lib/arm64-v8a/libMOGlesProofLayer.so.
- Both APKs extracted and ZIP CRC checked. **No real-device P4 activation/injection test yet.**
- P4.0 first Actions run 42309871683 failed due missing probe JUnit dependency. Corrected at cdb8f29f99985b4e0893db2466990856107cbc5f; later run 42309995020 succeeded probe build, 42310714620 succeeded full v0.4 controls.
- Owner instructions: uninstall old MO v0.3 if signature mismatch (no active layer in old version), install v0.4 + disposable test APK; open test before enabling for baseline. Under Shizuku UID2000, opt in, observe layer, RESTORE SETTINGS IN MO BEFORE UNINSTALL/UPGRADE, and verify original renderer returned. Do not modify Outlanders.

## Latest MO-20261107-CHAT-RELEASE-010 — User requested direct APK GitHub Releases
- Changed MO build workflow: no more upload-artifact ZIP. On successful two-module tests/build, package two **raw APK Release assets**, prerelease tagged by version and run number; include SHA256 in release notes. GitHub CLI `gh release create` with workflow contents:write and token, or upload --clobber on rerun.
- Current repo Releases was empty before this change. First build triggered by workflow update **must be checked** before claiming published URLs.
- Important owner safety: P4 GLES developer layer settings snapshot is in MO app data. **Restore original settings before uninstall/update**, especially with CI per-run debug signing mismatch. No real game modified.
- Docs: `docs/RELEASE_DISTRIBUTION_AR.md`.

## Release publishing confirmed — 2026-11-07
- GitHub Actions run https://github.com/alwansan/MO/actions/runs/42311901394: **completed success**. Commit `58de082ab63a797087f827633ba4dbd35fb5aa2f`.
- GitHub prerelease https://github.com/alwansan/MO/releases/tag/mo-v0.4.0-alpha.4-run9 created, id 437174044, public (draft=false), two raw APK assets, state=uploaded:
  1. `MO-Graphics-Probe-v0.4.0-alpha.4.apk` (1,118,830 bytes) https://github.com/alwansan/MO/releases/download/mo-v0.4.0-alpha.4-run9/MO-Graphics-Probe-v0.4.0-alpha.4.apk
  2. `MO-Layer-Test-v0.1.0.apk` (17,463 bytes) https://github.com/alwansan/MO/releases/download/mo-v0.4.0-alpha.4-run9/MO-Layer-Test-v0.1.0.apk
- No ZIP Release asset. GitHub automatically provides Source code ZIP/tar links; separate from APK release assets.
- On future source/workflow pushes, building both APKs and publishing a unique experimental release is automatic after CI success. Actions Artifacts ZIP step removed.
- Debug signature remains unstable: **restore P4 debug GPU settings before uninstalling/updating MO**. No actual Outlanders FPS unlock or P4 device hook proven.

## Latest MO-20261107-CHAT-P4-FEEDBACK-011 — owned GLES real device FAIL, diagnostics implemented
- Owner supplied v0.4 owned `MO Layer Test` baseline+after screenshots. With Shizuku `enable_gpu_debug_layers=1`, `gpu_debug_app=com.alwansan.mo.glesprobe`, app still `GL_RENDERER=Adreno (TM) 730`, mock FPS LOCKED, `Layer intercepted: NO`. Settings write PASSED, actual layer injection NOT CONFIRMED/failed in observed trial. See docs/DEVICE_FEEDBACK_ALPHA4_2026-11-07.md.
- **Owner has not shown restoration**. First priority: use currently installed v0.4 to restore original GPU Debug keys BEFORE uninstall/update. Avoid losing SharedPreferences snapshot due changing signing key.
- New source v0.5 app+probe: diagnostic read-only Shizuku method, exact keys/logcat filtered, native vs Java GLES, PID, explicit ELF loadability test. GitHub Actions run must be checked and user must test after safe restore.
- No root/unlock/VM/clone, no games modified. Distinguish settings activated from GLES layer actually intercepting.

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
