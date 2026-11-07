# MO — مراجعة ZygiskNext / ReZygisk / NeoZygisk / COPG وإمكانات Shizuku دون Root
2026-11-07 | session `MO-20261107-CHAT-ZYGISK-COPG-AUDIT-016` | origin=chat
**Mode:** live README/source/manifest and official Android documentation review; NO archived binary execution, no rooted installation, no third-party source imported, no app/game/settings writes.

## Scope and pinned upstream repository tips
- Zygisk Next (`Dr-TSNG/ZygiskNext`, `main`): `cddf38d5b1501d0cbf08393767afed09ac478b14`.
- ReZygisk (`PerformanC/ReZygisk`, `main`): `2c1d0df3b0d6e40d064927523c4dd1116cb8e832`.
- NeoZygisk (`JingMatrix/NeoZygisk`, `master`): `666140e4bf5bb8c9803b6c4092010faa45aacbca`.
- COPG (`AlirezaParsi/COPG`, `JSON`): `a029ee8742d095977dfd4be311ce5938c9a9cf1c`.
- iUnlockerGL (`i-Taylo/iUnlockerGL`, `main`): `a80f281db2f655d7cb08f4d2a6d241a53a114bf9` (public README/license only; independent prior static inventory in MO).
- MO at session start `d5da2910ab333f44fe12d2bbd8b71d138308d96c`.

## Mechanism map: why a new Zygisk implementation does not grant Shizuku more privileges
1. **Bootstrap/injection into Zygote or child process:** normally depends on a root/module framework or an equivalent system integration, not on the name of the Zygisk reimplementation. ReZygisk's `module/src/post-fs-data.sh` launches `zygisk-ptrace64 monitor` from root-owned module paths after boot; `zygiskd/src/main.c` calls mount-namespace/root-implementation helpers.
2. **NeoZygisk:** its README identifies a `ptrace`-based Zygote injection module, APatch/KernelSU/Magisk root integration, mount namespace controls; `loader/src/ptracer/monitor_impl.cpp` implements a monitoring/injection status state machine. This is not an unprivileged shell-to-game API. Reading source does not authorize trying to attach to an unowned production game.
3. **Zygisk Next:** README describes standalone Zygisk support for KernelSU/Magisk root solutions; `zygisk_next_api.h` includes process-local hook and symbol resolver APIs. A hook API is useful only **after** the library was lawfully loaded into that target's address space. New `docs/hyos_runtime.md` documents Xiaomi HyperOS Rust-runtime specialization callbacks, still requiring the underlying injected Zygisk environment; it does not bypass stock Android's package/process privileges.
4. **COPG:** README explicitly requires rooted Android and one of Magisk/KernelSU/APatch plus supported Zygisk. `module/service.sh` launches its `/data/adb/modules/COPG/controller` after boot. This is root module infrastructure, not a Shizuku-supported service.
5. **iUnlockerGL:** official README requires root Magisk or KernelSU and Zygisk; publicly described features include Build, GLES, Vulkan, CPU/RAM/display profile changes. Its ZIP/APK and root module architecture have already been inventoried with checksums in `prompt/SOURCE_INVENTORY.md`. Do not reverse engineer, decompile, copy or make a derivative of its proprietary code in MO; read-only publicly published descriptive docs only this cycle.
**Result:** none of these introduce an ordinary Shizuku UID2000 endpoint capable of substituting `Build.MODEL`, `glGetString`, `vkGetPhysicalDeviceProperties` inside a closed non-debuggable game's process. Do NOT suggest rooting, bootloader unlocking, ptrace-injection attempts, bypassing app trust, module ZIP scripts or proprietary reuse.

## COPG's valuable independent design ideas (not a no-root injection proof)
- The public `module/COPG.json` defines per-package **profiles** and action identifiers: `PACKAGES_<PROFILE>`, corresponding `_DEVICE` mappings with `BRAND/MODEL/MANUFACTURER/FINGERPRINT/DEVICE/PRODUCT`, and option strings like `:cpu=sd8elite`, `:gpu=adreno830`, `:blocked`. The checked JSON has **16 device detail groups** and **32 `PACKAGES_` keys** (including paired device detail groups); these describe user intent, not the actual runtime replacement.
- `module/GPU/manifest.json` publishes example device identity strings for `adreno830`, `adreno840`. A GPU *name* or vendor_id spoof does not implement new real hardware capabilities, extensions or performance; never fake a Vulkan feature that callers may then use.
- `build.sh` references `src/spoof_module.cpp` and `src/unified_controller.cpp`, but there is **no root `src/` directory in the checked `JSON` branch listing**. That exact branch is therefore not enough to independently reproduce/audit its native implementation from source; release binaries and marketing claims are not runtime proof. Do not assume all COPG binaries are distributed under the top-level license.
- MMRL page `https://mmrl.dev/repository/zguectZGR/COPG` lists v7.6.1 (761) dated 2026-10-25, labels license Apache-2.0, but also displays a generic footer referring to GPL3. Treat that web page as distribution metadata, **not** proof of host-rootless support or coherent binary licensing.
- A *safe clean-room MO design* can separately store editable `GameProfile` records: package, observed surfaces (Java Build / native properties / GLES / Vulkan / display / Game Mode), planned model identifiers, evidence and reproducible signature/license notes. Profile application may only be enabled in an **owned, cooperating test APK**; for stock Outlanders, render read-only diagnostics and explicitly show unavailable paths.

## Licensing findings
- **Zygisk Next** README states starting from `v4-0.9.2` it is no longer GPL-3.0; modification, redistribution and extracting components into other projects are prohibited. Do not copy its code, API implementation or binaries to MO.
- **ReZygisk** README says **AGPL-3.0**, but the current `LICENSE` file begins `GNU GENERAL PUBLIC LICENSE Version 3` (GPL-3.0). This is a **repository inconsistency**, not permission to choose the convenient interpretation. Clarify with maintainers and audit per-file terms before any import. This review imports none.
- **NeoZygisk** `LICENSE` contains GPL-3.0. Technical relevance is root-only, so no need to import.
- **COPG** `LICENSE` and `module.yaml` identify Apache-2.0 for the visible repository material. Do not extend license coverage to any PRO/private or non-published native component without separate evidence.
- **iUnlockerGL** custom LICENSE forbids reverse engineering, modification and derivatives; original third-party archive, .so and APK are not in MO.
- **MMRL** listing/footer metadata is not itself an independent license grant.

## Rootless mechanism comparison: promising vs impossible
| Approach | Changes commercial game's device identity without app cooperation? | Rootless actionable research |
|---|---|---|
| Zygisk/NeoZygisk/ReZygisk/COPG root modules | Possible only within their supported rooted environment, not on current stock phone | **No** under owner's immutable limits |
| Official external GLES layer | No for stock `com.tencent.mhadv` (targetSdk36; Debuggable NO; opt-in NO) | Already observed Native/eglGetProcAddress simulation **only** in owned `com.alwansan.mo.glesprobe` |
| Developer GPU settings with Shizuku | Selects developer layer **target**, does not grant target eligibility | Owned test only; guaranteed user-consented rollback |
| Android Game Mode custom intervention | Can request supported downscale/FPS **throttling**, not forge GPU/Build/Vulkan values and not necessarily raise a game's internal FPS ceiling | Read-only capability/overlay diagnosis first, owned game PoC if future approval |
| ANGLE per-app | Changes selected GLES driver where supported; not device identity spoof nor universal Vulkan intercept | Probe availability and real driver use only with clean pre/post journal |
| libadrenotools in app cooperating with developer | App explicitly chooses alternative Vulkan driver; does not modify unrelated apps | Owned test or cooperating emulator only |
| Existing manufacturer Game Turbo / in-game settings | Official supported game/display choices, no fake identity | Document actual options/metrics before further engineering |
| APK clone/re-sign, guest VM/root, patch live game | Changes target package or runtime; not allowed by owner's no-clone/no-VM/no-root directions | **Excluded** (data, signature, anti-cheat risks) |
| Direct Shizuku `getprop` simulation | Only response in Shizuku-mediated shell; not Java Build/native GPU inside another app | Do not mislabel as global spoof |

Official Android rootless GLES docs: https://developer.android.com/ndk/guides/rootless-debug-gles
Android game interventions: https://developer.android.com/games/optimize/adpf/gamemode/gamemode-interventions
Android 15 shell game commands (source): https://android.googlesource.com/platform/frameworks/base/+/refs/heads/android15-release/services/core/java/com/android/server/app/GameManagerShellCommand.java
libadrenotools scope: https://github.com/bylaws/libadrenotools

## Proposed MO next research and test gates (no immediate game changes)
1. **P9 safety closeout**: owner v0.9 latest cleanup output shows `enable_gpu_debug_layers=0`, three owned markers absent; separate fresh owned-test process PID 16719 showed real Adreno730 across Java/NDK/eglGetProcAddress and Mock tier locked. User also shared an earlier *active* screenshot/report showing enable=1, so preserve order and require fresh read-only recheck if further actions happened after final cleanup.
2. New original **Game Identity Laboratory** (owned disposable app only): compare the exact decisions from Java Build, native props, GLES, Vulkan, CPU, supported display modes and synthetic app-controlled tier lookup. Demonstrate surface-vs-menu/real FPS differences, never assert it represents proprietary Outlanders' internal logic.
3. **Stock Outlanders capability report**: preserve package version, targetSdk36, Debuggable NO/opt-in NO, Game Mode standard/custom and missing interventions, ANGLE null, real Adreno730; improve the evidence labels, not package contents.
4. **Read-only Game Mode analysis** with no `cmd game set` applied to Outlanders. Official `--fps` intervention means FPS throttle and has OEM/game prerequisites; 120 value in a config is not an unlocked in-game 120 FPS option.
5. **Controlled owned-A/B layer lab** if owner wants: baseline → activate owned layer → force-stop own probe → compare fresh PID Java/NDK/eglGetProcAddress → restore exact keys → fresh PID baseline. Never activate while old rollback unresolved; avoid public prototype sharing under misleading titles.
6. App signing: investigate stable GitHub secret-backed keystore (do not publish private key), and export/journal safety, but no new APK/build in this research cycle.

## What was NOT performed
No iUnlockerGL restricted binary analysis or proprietary derivative, no execution of COPG/ReZygisk/NeoZygisk scripts, no download or execution of ROOT ZIPs, no Android root, no ptrace target injection, no game edits, no firmware/boot/settings modifications, no APK or CI build started.

## Source links
- https://github.com/Dr-TSNG/ZygiskNext/blob/main/README.md
- https://github.com/Dr-TSNG/ZygiskNext/blob/main/zygisk_next_api.h
- https://github.com/Dr-TSNG/ZygiskNext/blob/main/docs/hyos_runtime.md
- https://github.com/PerformanC/ReZygisk/blob/main/README.md
- https://github.com/PerformanC/ReZygisk/blob/main/LICENSE
- https://github.com/PerformanC/ReZygisk/blob/main/module/src/post-fs-data.sh
- https://github.com/JingMatrix/NeoZygisk/blob/master/README.md
- https://github.com/JingMatrix/NeoZygisk/blob/master/LICENSE
- https://github.com/JingMatrix/NeoZygisk/blob/master/loader/src/ptracer/monitor_impl.cpp
- https://github.com/AlirezaParsi/COPG/blob/JSON/module/COPG.json
- https://github.com/AlirezaParsi/COPG/blob/JSON/module/GPU/manifest.json
- https://github.com/AlirezaParsi/COPG/blob/JSON/build.sh
- https://github.com/AlirezaParsi/COPG/blob/JSON/module/service.sh
- https://github.com/AlirezaParsi/COPG/blob/JSON/LICENSE
- https://mmrl.dev/repository/zguectZGR/COPG
- https://github.com/i-Taylo/iUnlockerGL/blob/main/README.md
- https://github.com/i-Taylo/iUnlockerGL/blob/main/LICENSE
