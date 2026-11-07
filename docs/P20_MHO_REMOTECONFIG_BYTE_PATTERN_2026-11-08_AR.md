# MO P20 — RemoteConfig 146-byte patterns from three game sessions
Date: 2026-11-08 Asia/Baghdad | Session `MO-20261108-CHAT-P20-REMOTECONFIG-PATTERN-033` | origin=chat.

## Source and privacy limits
Owner ran `tools/inspect_mho_remote_config.py` in Termux and privately uploaded `REMOTE_CONFIG_PATTERN.txt`, generated from three user-owned TAR snapshots of `/storage/emulated/0/Android/data/com.tencent.mhadv`. Only metadata was output. **No game modifications, Root, Shizuku writes, or executable extraction occurred.** The uploaded text report is user-provided direct evidence, not evidence of decoded remote-config data. Attempts to access the local attachment with Python and container in this chat returned `ClientError`; this report relies on the already-exposed parsed uploaded text, not independent re-extraction of TAR binary.

## Exact measured pattern (all byte offsets zero-based)
- File: `com.tencent.mhadv/cache/RemoteConfig.config`, **146 bytes in every snapshot**; formatter classified contents `opaque/binary-like bytes; no protocol inferred`.
- Distinct SHA-256 fingerprints:
  - `MHO_AFTER_GRAPHICS_LOW.tar`: `92ecbacfa35d3063351d30f58385f349d6077602c1f6bcdf87195b7d0544faee`.
  - `MHO_AFTER_GRAPHICS_MEDIUM.tar`: `5079a43e3a7db49f4e9041a21389efc88ac7b884d1296f59408cd6d45c3d0a29`.
  - `MHO_MEDIUM_CONTROL.tar`: `16b7614e98f0b1303e08e9113cfe8339d08eed74f1531a154865361f95abf706`.
- **Low→Medium**: `128/146` byte offsets changed, exact ranges `16–79` (64 bytes) and `82–145` (64 bytes). Unchanged `0–15` (16 bytes) and `80–81` (2 bytes).
- **Medium→Medium control**: **identically 128/146 changed**, exact ranges `16–79` and `82–145`; same stable offsets. By pairwise transitivity, all three share the same 18 constant byte values at those offsets, but report does not reveal them.
- Bytes modified proportion `128/146 = 87.67%`. Identical regions suggest structured fixed-layout data with two variable 64-byte fields separated by two static bytes and 16-byte prefix, but **this is structural observation only**: no schema, encoding, cryptography, hash, nonce, graphics preset, server policy, or fields can be identified from offsets alone.
- The control kept selected Graphics Quality **Medium**, although the user interacted with unsupported High/Ultra/Concept Art buttons; the game displayed `This setting isn't currently supported on your device.`. Because same region changes without accepted quality change, the changed-offset pattern is **not a unique signature for graphics quality selection**.

## Reasonable engineering interpretation
- A fixed 146-byte length and two entire 64-byte variable spans can arise from opaque transient binary fields or encoded content; **not enough evidence to conclude encryption/signing, specific key/value or a graphics-unlock profile**. Do not invent a replacement `RemoteConfig.config`, paste Adreno840 strings, or change Unreal/HotRes/GL shaders; such edits might be ignored, reset, corrupt game content or violate game integrity rules.
- Data-driven whitelist logic might exist internally or server-side; the screenshot supports a device-eligibility gate, **not its source**. Android GPU string API results (GLES and Vulkan) are not changed by writing ordinary external data files.
- **Decision:** the three-session RemoteConfig-diff experiment has reached its evidence limit. No new large 230MB archive or Rish capture is needed solely to repeat this question. Do not declare a graphics/FPS fix. Pursue only targeted, read-only format research if new reason/evidence arises; otherwise prioritize owner's main goal of authentic app-observed graphics identity substitution via the **P13 ANGLE read-only capability audit**, and/or a consenting separate test app if warranted.

## Supporting project material
- `docs/P17_MHO_GRAPHICS_LOW_TAR_DIFF_2026-11-08_AR.md`
- `docs/P18_MHO_LOW_TO_MEDIUM_DIFF_2026-11-08_AR.md`
- `docs/P19_MHO_MEDIUM_CONTROL_2026-11-08_AR.md`
- `tools/inspect_mho_remote_config.py`
- `research/REAL_GPU_IDENTITY_SPOOF_ANDROID15_ANGLE_AR.md`
