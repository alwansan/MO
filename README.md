# MO — Shizuku-first Graphics & Module Inspector

**State: Planning / research only (2026-11-07). No build or root changes.**

MO is an original Android utility designed for a **locked-bootloader, non-rooted** phone. It inspects Android module ZIPs safely, explains privilege requirements, can extract normal APKs without executing installer scripts, and plans opt-in diagnostics and supported per-app graphics settings using Shizuku.

**Hard reality:** Shizuku running as ADB shell (UID 2000) is not root (UID 0). It cannot install KernelSU into the kernel, run Zygisk, hook unrelated games, load untrusted kernel modules, or turn a Magisk/KernelSU ZIP into a functioning root module. MO must never claim otherwise.

**Target test device:** POCO F5 Pro (mondrian), Android 15, HyperOS OS3.0.5.0.VMNMIXM, kernel 5.10.236-android12-9 (as shown in supplied screenshots; recheck on device). No PC, no bootloader unlock, no rooting; Shizuku via wireless debugging; GitHub Actions for CI.

## Start here
- [Project plan — Arabic](prompt/PLAN_AR.md)
- [Research and feasibility](prompt/RESEARCH_AR.md)
- [Architecture](prompt/ARCHITECTURE.md)
- [Capability matrix](docs/SHIZUKU_CAPABILITY_MATRIX.md)
- [Licensing and safety](docs/LEGAL_AND_SECURITY.md)
- [Current memory / decisions](prompt/MEMORY.md)
- [Status / next action](prompt/STATUS.md)
- **[Restore instructions for a new session](prompt/RESTORE_AR.md)**
- [Agent instructions](AGENTS.md)

Upstream references (not vendored or modified): [iUnlockerGL](https://github.com/i-Taylo/iUnlockerGL), [KernelSU](https://github.com/tiann/KernelSU), [Shizuku](https://github.com/RikkaApps/Shizuku). Original third-party licenses remain with their owners.

## Phase gates
P0 research & planning -> P1 independent Kotlin skeleton -> P2 Shizuku permission/status probe -> P3 ZIP inspector and safe APK extraction -> P4 opt-in ANGLE per-app experiment with rollback -> P5 real-device validation and CI release. **P0 only is authorized in this session.**
