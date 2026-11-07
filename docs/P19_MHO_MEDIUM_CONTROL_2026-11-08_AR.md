# MO P19 — Medium → Medium control, graphics unsupported device message

Date: 2026-11-08 Asia/Baghdad
Session: `MO-20261108-CHAT-P19-MEDIUM-CONTROL-032`
Owner evidence: new screenshot `1006531195.jpg`, uploaded `MEDIUM_CONTROL.txt` report, and `MHO_MEDIUM_CONTROL.tar` created via Rish with `TAR_EXIT=0`.

**Data scope disclosure:** This report grounds file-level conclusions in the owner-uploaded text diff. Attempts to directly open the raw newly mounted TAR using Python and container failed with `ClientError`. Therefore it **does not** claim a direct binary decode of `RemoteConfig.config`, the Unreal cache or any archive member. Do not publish raw private archives.

## Confirmed app UI evidence
- Screenshot: Outlanders Global in-game Settings/Graphics shows **Medium selected**, padlock on **High**, **Ultra**, **Concept Art**. Toast after tapping a locked setting: **“This setting isn't currently supported on your device.”** This was explicitly confirmed for each High/Ultra/Concept Art by owner.
- Field frame rate at Medium with higher entries visibly locked; Free Hunt/Arena has a High option visible but unselected, and further Very High choices padlocked. **Device Load: High** appears as a separate load label at top of game UI; it is not proof the graphics quality preset High is supported.
- Current evidence shows in-game gating/eligibility logic is active on this device. It does **not** isolate a single check to Vulkan, GLES, CPU, build model, server-side whitelist or Unreal DeviceProfile. The screenshot does not demonstrate effect from changing files.
- Owner controlled quality Medium for the latest session (no accepted quality change), while tapping unsupported locked options. Thus this is **same selected graphics setting**, but not a literal zero-interaction idle session: any button tap/startup may generate telemetry or refresh remote config.

## Stronger three-way comparison
Archive diff `MHO_AFTER_GRAPHICS_MEDIUM.tar` → `MHO_MEDIUM_CONTROL.tar` reads SHA256 of member contents; **55 files before, 55 after, 11 changes = 1 added, 9 modified, 1 deleted**.
| Entry | Before bytes | After bytes | Observation |
|---|---:|---:|---|
| `cache/GCloudSDKLog/GCloud/GCloud_2026110802.log` | 661174 | 1461043 | MODIFIED SDK log |
| `cache/GCloudSDKLog/GCloudCore/GCloudCore_2026110802.log` | 49732 | 100622 | MODIFIED SDK log |
| `cache/GCloudSDKLog/GPMSDK/GPMSDK_2026110802.log` | 6598 | 13347 | MODIFIED SDK log |
| `cache/RemoteConfig.config` | 146 | 146 | MODIFIED: same length, different SHA256 bytes |
| `files/GameletLog/gamelet_20261108.log` | 464 | 696 | MODIFIED |
| `files/RHICache/ProgramBinaryCache/GLSL_ES3_1_ANDROID_.../PrecacheBinaries` | 55691684 | 55691684 | MODIFIED: same size/different bytes |
| `files/UnrealGame/MHMobile/MHMobile/Saved/Logs/MHMobile-backup-2026.11.08-02.13.03.log` | — | 8437368 | ADDED backup of Unreal log |
| `files/UnrealGame/MHMobile/MHMobile/Saved/Logs/MHMobile.log` | 8437368 | 10982786 | MODIFIED |
| `files/UnrealGame/MHMobile/MHMobile/Saved/Logs/MHMobile_2026.11.03-21.25.43_30115_segment_1.log` | 20972279 | — | DELETED during log rotation |
| `files/centauri_log/com.tencent.mhadv/centauriLog.mmap` | 153600 | 153600 | MODIFIED |
| `files/centauri_log/com.tencent.mhadv/٢٠٢٦١١٠٨_1.txt` | 51904 | 77952 | MODIFIED |

All source paths above are under `com.tencent.mhadv/` in TAR. Source: owner-uploaded `MEDIUM_CONTROL.txt` lines 1–21.

### RemoteConfig interpretation update
Three separate comparisons demonstrated **same-sized 146-byte `RemoteConfig.config` content changed**:
1. Medium → Low (P17; run opened game and selected new quality).
2. Low → Medium (P18; run opened game and selected new quality).
3. **Medium → Medium** (P19; selected quality unchanged, user tapped locked High/Ultra/Concept Art options and saw unsupported-device message).

**Conclusion:** `RemoteConfig.config` modification is **not uniquely dependent on an accepted Graphics Quality change**. It could be an updated session/service/config blob or contain multiple fields; the file could still carry graphics information but has not been decoded. No justification to modify/copy forged bytes, declare it a whitelist or link it to unlocked FPS. The same-size `RHICache PrecacheBinaries` changed in the control run, so GPU shader cache changes are also **not sufficient evidence of a selected quality change**.

No newly recognized `GameUserSettings.ini`, `DeviceProfiles.ini`, `UserCustom.ini` appears in this 55-file snapshot. This absence in Android/data does **not** rule out internal protected `/data/user/0` settings, cooked configuration inside game APK/PAK, Android API identity checks or remote compatibility decisions.

## Evidence hierarchy and next discriminating step
- **P19 control has done its job.** Repeating more full 230+MB game TAR runs just to compare caches/logs is low value. Rather than spending phone storage/repetitive uploads, perform **minimal targeted format inspection** of existing three stored `RemoteConfig.config` instances from the 3 user-created TAR archives. The user already has TAR files under `Download`.
- Safe procedure: Termux Python standard library `tarfile` can read the exact `com.tencent.mhadv/cache/RemoteConfig.config` member *from each archive* without accessing/replacing any in-game file. Compare byte length, SHA256, printable headers, and which offsets differ; report **format/offsets**, not secrets/tokens. If only session marker rotates and no meaningful graphic state pattern appears, deprioritize external data path.
- If requested, owner can privately upload three 146B extracted snapshots (after inspecting for sensitive identifiers); do not commit originals to public MO.
- For core MO goal (a **different app** seeing Adreno840), **a writable Android/data file by itself cannot change graphics API return values**. Even if a valid graphics quality config existed, that would not necessarily spoof GLES/Vulkan/Build hardware. The screenshot's lock could depend on device whitelist/client/server.
- Existing P13 **read-only** ANGLE capability probe in MO v0.11 remains the lowest-impact independent-system test. Do not enable global driver settings, flash KernelSU or patch game resources for this experiment.

## Reference
- P18 preceding report: `docs/P18_MHO_LOW_TO_MEDIUM_DIFF_2026-11-08_AR.md`.
- P17: `docs/P17_MHO_GRAPHICS_LOW_TAR_DIFF_2026-11-08_AR.md`.
- `tools/compare_android_data_snapshots.py`.
