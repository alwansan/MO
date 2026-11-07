# MO session MO-20261107-CHAT-ZYGISK-COPG-AUDIT-016
Date: 2026-11-07 Asia/Baghdad
origin=chat | repository https://github.com/alwansan/MO | branch main
phase: **P9 / research & device evidence closeout**, no new runtime Android APK stage.
Initial main HEAD `d5da2910ab333f44fe12d2bbd8b71d138308d96c`; previous session `MO-20261107-CHAT-P9-AUDIT-015`.

## Owner request
Review and compare public live source and project files for ZygiskNext, ReZygisk, NeoZygisk, COPG, its MMRL listing, and iUnlockerGL; search additional options for MO's real goal: game FPS/graphics eligibility on POCO F5 Pro without root, bootloader unlock, VMOS or APK clone. Analyze four new device screenshots and two pasted v0.9 reports. No implicit permission to run root ZIPs or edit original game.

## Evidence read and results
- Source commit refs and detailed architecture/licensing comparison in [research/ZYGISK_COPG_NO_ROOT_FEASIBILITY_AR.md](../../research/ZYGISK_COPG_NO_ROOT_FEASIBILITY_AR.md).
- Owner device observations in [docs/DEVICE_FEEDBACK_ALPHA9_2026-11-07.md](../../docs/DEVICE_FEEDBACK_ALPHA9_2026-11-07.md).
- Zygisk Next `README.md`: root prerequisites and **post v4-0.9.2 restrictive all-rights-reserved**, no components may be copied. `docs/hyos_runtime.md` HyperOS Rust runtime remains an injected-framework feature, not Shizuku cross-process API.
- ReZygisk `README.md` + `module/src/post-fs-data.sh` + `zygiskd/src/main.c`: active root module startup, ptrace/daemon/root and mount-namespace. Its README says AGPL-3.0 while repository `LICENSE` contains GPL-3.0 text; flag rather than silently resolve.
- NeoZygisk README describes ptrace Zygote injection plus root-controlled namespaces; GPL-3.0 LICENSE. Not rootless through shell.
- COPG live `JSON` branch: root required, package/profile schema + GPU manifest examples. Public `build.sh` refers to `src/spoof_module.cpp` and `src/unified_controller.cpp`, but its root directory listing has no `src/`; cannot independently prove the native implementation from this public revision. `service.sh` runs `/data/adb/modules/COPG/controller` after boot. MMRL lists v7.6.1, Apache-2.0 label but an inconsistent generic GPL footer; do not infer license for unpublished binary.
- iUnlockerGL README/LICENSE consulted only, no disassembly, proprietary-code copying or root ZIP execution. Previous original ZIP and KernelSU APK hashes remain in `prompt/SOURCE_INVENTORY.md`, not re-inspected.
- Official Android GLES external layers still require debuggable or target opt-in; owner Outlanders targetSdk36 debuggable NO opt-in NO. Android Game Mode `--fps` intervention is a throttle/limit when supported, not native GPU spoof or in-game unlocked 120 FPS; libadrenotools explicitly requires cooperating application.

## Last owner v0.9 experiment
- The final pasted report records `enable_gpu_debug_layers=0` and three exact MO markers **not present**; both `[SHIZUKU SYSTEM READ-ONLY]` and `[READBACK AFTER ACTION]` agree. v0.9 cached readback fix is therefore **device-verified at that point**. No need to re-run cleanup.
- Earlier pasted report and screenshots show `enable_gpu_debug_layers=1` and all three MO markers during layer activation; correct transient test stage; not current state as reported by final pasted block.
- Owned test screenshot around 04:10 shows process PID16719 with NDK/Java/eglGetProcAddress renderer Adreno730, Mock FPS LOCKED. A 04:12 activation screenshot shows enabled settings; **no proof that PID16719 was a process started after final cleanup**. Fresh post-cleanup renderer/PID remains pending. Avoid wrongly assigning this screenshot to final rollback step.
- Neither report proves opening any graphics/FPS settings in original Outlanders; stock eligibility remains NO.

## Session output / actions
- Added independent research + on-device evidence docs, and updated STATUS, MEMORY, DECISIONS, RESTORE, RESEARCH index/links accordingly.
- **No phone access or settings writes**, no changes to game or APKs, no root/bootloader/Zygisk/patch/VM, no new app sources or workflow build, no original third-party binaries copied.
- Any future work should keep owned-only controls, explicit rollback and verified device evidence.

## Next step
1. Keep v0.9 current app installed until confirmed rollback; optionally use **read-only** four-key diagnosis to ensure final clean state persists.
2. Force-stop/reopen **only disposable MO Layer Test** and copy new PID and Java/NDK/eglGetProcAddress renderer after final cleanup; confirm Adreno baseline.
3. Build separate plan for **Game Identity Laboratory**, a self-owned cooperative test app with falsifiable Build/GL/Vulkan/CPU/display decision surfaces and mock tier gate, as next authorized scoped implementation decision. No execution on stock Outlanders.
4. Explore only read-only real-game Game Mode/ANGLE/driver reporting until evidence supports a supported, reversible action. Don't advertise unsupported rootless GPU spoof.

## Evidence URLs
- https://github.com/Dr-TSNG/ZygiskNext
- https://github.com/PerformanC/ReZygisk
- https://github.com/JingMatrix/NeoZygisk
- https://github.com/AlirezaParsi/COPG
- https://mmrl.dev/repository/zguectZGR/COPG
- https://github.com/i-Taylo/iUnlockerGL
- https://developer.android.com/ndk/guides/rootless-debug-gles
- https://developer.android.com/games/optimize/adpf/gamemode/gamemode-interventions
- https://github.com/bylaws/libadrenotools

## Closeout and repository verification
- Branch main HEAD immediately before this final session-file edit: `868cd13be2a5295c67269a2086f80c9d7928e5c9`.
- GitHub Actions latest observed workflow run remains `42316925749` (`completed/success`, source `968e13d19a956a0016c4e57609ca744c253da3db`), because documentation-only paths are excluded from the workflow push build filters. **No new build initiated**.
- Public Release still `mo-v0.9.0-alpha.9-run16` with individual raw APK files `MO-Graphics-Probe-v0.9.0-alpha.9.apk and MO-Layer-Test-v0.3.0.apk`.
- Added documentation links in prompt/README and current status/memory/research/decisions/restore; all updates limited to documentation and research directories.
