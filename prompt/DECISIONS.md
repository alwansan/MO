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
