# ADR / decisions

## ADR-001 (2026-11-07) — Respect non-root owner constraints
**Accepted.** No root, no bootloader unlock, kernel flash, Magisk, or Zygisk. If feature needs these, mark unsupported rather than simulate.

## ADR-002 — Don't copy iUnlockerGL source or binary into MO
**Accepted.** Restrictive license prohibits modifications/derivative use; reference original release and file hashes. Only unmodified redistribution with attribution may be allowed, but is unnecessary and omitted.

## ADR-003 — Original module ZIP is not installable with Shizuku ADB as root module
**Accepted.** It requires privileged module infrastructure and injection at process start; APK extraction is a distinct ordinary install activity.

## ADR-004 — Separate UI from privileged optional capabilities
**Accepted.** Basic ZIP analyzer must work without Shizuku. Additional diagnostics and supported graphics settings require explicit Shizuku permission and runtime feature detection.

## ADR-005 — ANGLE as conditional experiment, not guaranteed GPU spoof
**Accepted.** Device compatibility, OEM settings permissions, and full rollback required before enablement.

## ADR-006 — Planning phase only
**Accepted.** This session creates research/plans and recovery files; source/build/release requires later direction.

## ADR-007 — Source mirroring strategy
**Accepted.** No wholesale import of third-party repositories at P0: link to immutable upstream revisions, snapshot checksums/licensing; consider GPLv3 external reference/fork only after a demonstrated use case and proper compliance.

## ADR-008 (MO-20261107-CHAT-STATIC-002) — Native external GLES/Vulkan layers for eligible targets
**Accepted for research/next controlled POC.** Support only our own debuggable app or game explicitly opted into external layers. Shizuku may set documented debug-layer settings but cannot make uncooperative nondebuggable games eligible.

## ADR-009 — Never force ANGLE system-wide
**Accepted.** The `angle_gl_driver_all_angle=1` setting is forbidden due to documented boot failure risks. Any supported ANGLE experiment must be per package and reversible.

## ADR-010 — Public GitHub != private binary storage
**Accepted.** User's intention is personal but alwansan/MO visibility is public; store original research reports only, no third-party proprietary code/assets.

## ADR-011 (MO-20261107-CHAT-ECOSYSTEM-003) — Layered rootless strategies
**Accepted.** Classify every mechanism: developer graphics settings, eligible GLES/VK layer, app-owned library, APK rewrite, virtual container, root-only hook. Show different prerequisites. Shizuku is a shell bridge only.

## ADR-012 — Never automatically uninstall a user's app
**Accepted.** Upstream KaosGFX includes uninstall on signature mismatch, with inadequate preservation of app-private data. Our implementation must stop on mismatch and only experiment with own disposable test packages.

## ADR-013 — No accidental third-party publication
**Accepted.** Keep original third-party binaries, REA/JADX/Node uploads and future private APKs out of public MO. External MIT/BSD/GPL/AGPL reference links only until an actual license-compliant import is necessary and reviewed.

## ADR-014 — Hardware-specific evidence before claims
**Accepted.** Adreno 750 rootless README results do not prove Adreno 730/HyperOS Android 15 success. Require on-device Vulkan renderer proof for every driver change.

## ADR-015 — Prioritize independent APK-internal bootstrap research
**Accepted as next-phase design only.** Own Android test app first; never silent patch of stock third-party game. Test first-party GLES/VK layers, then optional app-owned component factory without root.

## ADR-016 MO-20261107-CHAT-GAME-PROFILES-004 — Reframe as selective game capability visibility
**Accepted.** End-user objective is expose hardware-gated FPS/graphics presets, not general driver replacement. Separate game-menu eligibility from actual rendered FPS and hardware limits.
## ADR-017 — Multi-surface identity model
**Accepted.** Build/props, GLES, Vulkan, CPU, display, app configs and server policy are separate read channels. No generic Shizuku 'set GPU model' command. Build-field Xposed hooks do not prove native spoof.
## ADR-018 — Live competitive game safety
**Accepted.** No automatic modification/patching of installed PUBG/online games; official account penalties possible. Use independent mock game before any user-owned optional tests.

## ADR-019 MO-20261107-CHAT-BUY-VS-BUILD-005: Ready app before build
Accepted. Compare off-the-shelf options before committing to full MO app.
## ADR-020: ShizukuPlus spoof is process-command scoped
Accepted. Its newProcess getprop replacement does not rewrite target game native GPU/Java properties.
## ADR-021: Virtual root is distinct from host root
VMOS+ guest-root can leave physical phone unrooted, but is NOT a Shizuku-only solution and requires separate explicit approval to try. Game compatibility unverified.
## ADR-022: Monster Hunter Outlanders identity
Use global com.tencent.mhadv (regional com.garena.game.mho differs), not Monster Hunter Now.


## ADR-023 (MO-20261107-CHAT-P9-AUDIT-015) — P9 readback and device rollback gate
**Accepted.** Treat last v0.8 guarded cleanup success as evidence of an internally verified transaction only; the cached same-report shell panel was stale, and **fresh user-triggered phone readback is still missing**. Prioritize read-only four-key inspection and a fresh-process owned-test renderer check before any further activation or APK upgrade/uninstall. Never infer exact historical values of missing backup keys. No new APK produced merely to reconcile docs.

## ADR-024 — Phase precedence and narrow continuation
**Accepted.** Earlier P0 planning statements are historical; owner explicitly authorized P1–P9 implementation. GitHub CI and releases confirm code maturity, but do not substitute for device runtime tests. This audit documents and preserves current evidence without modifying the phone, game, or app source. Potential cold Shizuku service-connection snapshot race is a **code-review hypothesis**, not a confirmed defect. Investigate only as a focused follow-up after safety gate.
