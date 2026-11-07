# MO-20261108-CHAT-P20-REMOTECONFIG-PATTERN-033 — pattern comparison of user-owned RemoteConfig snapshots
Date: 2026-11-08 Asia/Baghdad; origin=chat; branch `main` in `alwansan/MO`.
Context: owner followed prior P19 instructions in Termux. `curl` downloaded `inspect_mho_remote_config.py` and both identical invocations reported `Saved report: REMOTE_CONFIG_PATTERN.txt`; the owner uploaded the generated text file to this chat. No game or device changes requested.

## Real supplied evidence
- Generated from archives `MHO_AFTER_GRAPHICS_LOW.tar`, `MHO_AFTER_GRAPHICS_MEDIUM.tar`, `MHO_MEDIUM_CONTROL.tar`, using exact archive member `com.tencent.mhadv/cache/RemoteConfig.config`.
- Three distinct SHA256: `92ecbacfa35d3063351d30f58385f349d6077602c1f6bcdf87195b7d0544faee`, `5079a43e3a7db49f4e9041a21389efc88ac7b884d1296f59408cd6d45c3d0a29`, `16b7614e98f0b1303e08e9113cfe8339d08eed74f1531a154865361f95abf706`.
- All files exactly **146 bytes**, `opaque/binary-like bytes; no protocol inferred`.
- Pair Low→Medium and Pair Medium→Medium control **both** change exactly **128 bytes** at byte offsets **16–79** and **82–145**, i.e. two 64-byte spans, and leave **18 bytes** unchanged (0–15 and 80–81). The three samples share those constant 18 bytes. Percent changed: `128/146=87.67%`.
- Strong negative evidence for using "RemoteConfig changed" or its changed-offset mask as a **unique marker** for accepted Graphics Quality: same mask exists in fixed selected Medium control. Cannot conclude the data has no graphics fields, that it is encrypted, that it comes from server, or that overwriting it would unlock anything; only fixed-layout/churn is shown.
- Earlier screenshot of game's High/Ultra/Concept Art settings says `This setting isn't currently supported on your device.` This supports gating behavior but does not identify whitelist input (GPU identity, Build, Vulkan, packed UE config, server).

## Work this turn
- Read supplied text report already exposed in chat. Python and container attempt to open mounted `/mnt/data/REMOTE_CONFIG_PATTERN.txt` returned runtime `ClientError` despite valid path; no direct byte-level independent re-extraction was claimed. GitHub project sources/status reread from live main.
- Added `docs/P20_MHO_REMOTECONFIG_BYTE_PATTERN_2026-11-08_AR.md` with exact ranges, SHA and limits. Updated STATUS/MEMORY/RESTORE and handoff. User's private original TAR archives and configuration bytes were not added to public GitHub.
- No further phone commands, app APK changes, CI builds, Root/KernelSU, Shizuku writes or game-file modifications. Existing comparator and reader tools from previous P19 CI remain unchanged.

## Next decision
- Stop repeating broad 230MB Android/data backups solely to seek 146-byte RemoteConfig graphics preset marker. The current pattern changes identically for accepted and unchanged quality. No defensible `DeviceProfiles.ini`/remote binary patch can be supplied from this evidence.
- Highest-value next source of evidence for owner's **primary objective** is the P13 read-only ANGLE capability report on POCO (MO v0.11). It only tests ANGLE presence/config, not actual Adreno840 spoof; end-to-end success requires another cooperating app's true GL/Vulkan readback and a reversible independent A/B if permitted.
- If the owner still wishes to pursue file-based config, seek new concrete evidence that the game loads a permitted external settings file; the existing uploaded archives do not demonstrate one.
