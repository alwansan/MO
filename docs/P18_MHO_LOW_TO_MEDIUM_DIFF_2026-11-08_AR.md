# MO P18 — Outlanders Low → Medium: real external Android/data comparison
Date: 2026-11-08 Asia/Baghdad | chat origin | session `MO-20261108-CHAT-P18-LOW-MEDIUM-031`

## Evidence and scope
Owner changed **Graphics Quality: Low → Medium** using the game's ordinary menu (image supplied showing Medium selected, High and Ultra still locked), completely closed the game, and using Shizuku/Rish UID2000 created `MHO_AFTER_GRAPHICS_MEDIUM.tar` with `TAR_EXIT=0`. Termux's SHA256 archive comparator saved `LOW_TO_MEDIUM.txt`; both report and new TAR were uploaded privately in this chat. No game files were edited.
- **Private user diff report:** `LOW_TO_MEDIUM.txt`, 21 lines, before archive `MHO_AFTER_GRAPHICS_LOW.tar` **51 files**, after `MHO_AFTER_GRAPHICS_MEDIUM.tar` **55 files**, **10 changed records** (4 added, 6 modified, 0 deleted). The comparator hashes member byte contents, not only sizes.
- **Important evidence limit:** Python, container and visible Python execution returned `ClientError` when attempting to access `/mnt/data/MHO_AFTER_GRAPHICS_MEDIUM.tar` and `/mnt/data/LOW_TO_MEDIUM.txt`. Therefore this public report is strictly grounded in **the user-uploaded text diff available directly in the conversation**, the supplied screenshot, and prior P17 report. **It does not claim independently opening TAR member bytes or decoding the 146-byte file**. The raw TAR and image are not published to the public MO repo.
- The old `MHO_AFTER_GRAPHICS_LOW.tar` was 51 files after the previous session; no claim that private `/data/user/0/com.tencent.mhadv` was sampled.

## 10 exact changed file entries, from user diff
**ADDED (4):**
1. `cache/GCloudSDKLog/GCloud/GCloud_2026110802.log` — 661174 bytes.
2. `cache/GCloudSDKLog/GCloudCore/GCloudCore_2026110802.log` — 49732 bytes.
3. `cache/GCloudSDKLog/GPMSDK/GPMSDK_2026110802.log` — 6598 bytes.
4. `files/UnrealGame/MHMobile/MHMobile/Saved/Logs/MHMobile-backup-2026.11.08-01.54.20.log` — 13283940 bytes.

**MODIFIED (6):**
1. `cache/RemoteConfig.config` — **146 → 146 bytes**, contents differ according to SHA256.
2. `files/GameletLog/gamelet_20261108.log` — 232 → 464 bytes.
3. `files/RHICache/ProgramBinaryCache/GLSL_ES3_1_ANDROID_C4871B9B02B3495594C52BA6E2338E686D1782F1/PrecacheBinaries` — **55635081 → 55691684 bytes (+56603)**. This is shader/program binary cache, **not a confirmed preference file**.
4. `files/UnrealGame/MHMobile/MHMobile/Saved/Logs/MHMobile.log` — **13283940 → 8437368 bytes**, plus new backup above. Strong *log rotation* evidence.
5. `files/centauri_log/com.tencent.mhadv/centauriLog.mmap` — 153600 → 153600 bytes, SHA changed.
6. `files/centauri_log/com.tencent.mhadv/٢٠٢٦١١٠٨_1.txt` — 25856 → 51904 bytes.

No `DELETED` entries in this second comparison. In the previous Medium→Low run, 8 HotRes items were deleted during game start/resource maintenance, but there is **no matching reverse restoration** on Low→Medium. This strongly supports avoiding interpretation of those hotfix deletions as a quality setting selector.

## Interpretation with causality caveats
- **`RemoteConfig.config` is only a hypothesis:** it changed in both P17 and P18, and remained a constant **146-byte length** each time. It could contain session/config timestamps, remote rollout state, identifiers, or graphics options. Because both experiments also involved a full game launch, **changes correlated with a toggle are not yet causal**.
- The shader `PrecacheBinaries` increased by 56603 bytes after Medium; this is compatible with shader cache activity, but also with different gameplay/scene/shader compilation. **Editing such a binary is not a demonstrated way to raise GPU whitelist or FPS limits**, and could corrupt game state.
- The `MHMobile.log` previous size exactly matches the newly added backup's byte length (13283940). Current `MHMobile.log` then contains 8437368 bytes of new logs; this is consistent with log rotation rather than an option-specific config update.
- In the provided **Medium** screenshot, Graphics Quality Medium selected; High/Ultra remain padlocked; Field FPS High and Very High remain padlocked. Current image alone cannot prove game configuration persistence across app relaunch, or that the FPS ceiling is solely GPU-name based.
- **No new plaintext `.ini` settings file** appears among added files. An external Android/data dataset may omit app-private preferences under `/data/user/0` and/or server/cooked config. Do not invent `DeviceProfiles.ini` to paste.

## Stronger next experiment to control for routine game startup
Before more Low↔Medium cycles, run a **same-setting control** to check the confound:
1. Keep the existing `MHO_AFTER_GRAPHICS_MEDIUM.tar` intact. Launch the game **without changing Graphics Quality from Medium**; close it normally.
2. Capture `MHO_MEDIUM_CONTROL.tar` in Download via the same read-only Rish `tar -cf` procedure, checking `TAR_EXIT=0`.
3. In Termux, compare `MHO_AFTER_GRAPHICS_MEDIUM.tar` vs `MHO_MEDIUM_CONTROL.tar` with `--all --output MEDIUM_CONTROL.txt`. If `RemoteConfig.config` also changes on an identical Medium→Medium run, it is **less likely** to directly encode quality. This alone will not prove absence: both general refresh and settings may coexist.
4. Only if more evidence is needed, test a clean controlled Medium→Low→Medium round trip and look for **repeatable reversibility** of a specific config file. Do not patch binary HotRes/Cache or use unverified game-specific GFX files.

### Why inspect no private files yet
ADB shell UID2000 can read accessible `/storage/emulated/0/Android/data/com.tencent.mhadv`, but **cannot claim access to** `/data/user/0/com.tencent.mhadv`. The game has a protected storage boundary; do not ask user to root device to defeat it. The cross-app GPU identity spoof original MO goal remains **unproved** from these file comparisons.

## Related resources
- `docs/P17_MHO_GRAPHICS_LOW_TAR_DIFF_2026-11-08_AR.md`
- `docs/P16_TERMUX_RISH_ANDROID_DATA_SNAPSHOTS_AR.md`
- `tools/compare_android_data_snapshots.py`
- `research/MHO_LAUNCHER_CONTAINER_VM_DATA_2026-11-08_AR.md`
