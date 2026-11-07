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


## ADR-025 (MO-20261107-CHAT-ZYGISK-COPG-AUDIT-016) — Separate P9 restored-key proof from new-process renderer proof
**Accepted.** The final user v0.9 pasted report provides directly refreshed Shizuku readback (enable=0; three MO owned markers absent). This confirms the bounded MO cleanup at that moment. It does **not** reconstruct unknown old key values. The screenshot PID16719 showing Adreno730 predates or cannot be proven to follow the last cleanup; keep post-cleanup new-process renderer check as pending and avoid new activation.

## ADR-026 — Zygisk implementations are research references only on stock phone
**Accepted.** No root/bootloader/VM/clone/patch/ptrace to a third-party process. ZygiskNext, ReZygisk, NeoZygisk and COPG all require rooted/bootstrap integration for cross-process modules; Shizuku UID2000 remains a strictly limited broker. COPG JSON schema may inspire an original, **unprivileged owned-app-only** Game Identity Laboratory, not a claim of rootless stock Outlanders GPU spoof.

## ADR-027 — License completeness & provenance gate
**Accepted.** New Zygisk Next README has post-v4-0.9.2 proprietary no-redistribution/no-picking conditions; ReZygisk README claims AGPL-3 while LICENSE is GPL-3 text; COPG top-level Apache-2.0 license does not establish rights to any unpublished PRO binary or missing native sources; iUnlockerGL prohibits reverse engineering/derivatives. Do not import source/binaries until all provenance and license conditions are clear. See `research/ZYGISK_COPG_NO_ROOT_FEASIBILITY_AR.md`.

## ADR-028 (MO-20261107-CHAT-P10-IDENTITY-LAB-017) — Safe educational identity lab
**Accepted.** Implement independent read-only identity measurements plus pure local mock gates inside MO-owned disposable test APK, not stock game's process. Show Java Build, CPU, Java/NDK GLES and Vulkan as distinct channels. The UI fake 120 FPS menu is explicitly simulated and cannot prove real game settings unlocked.

## ADR-029 — P10 shipment must not risk GPU Debug rollback in main MO
**Accepted.** Deliver P10 using `probe` v0.4.0 only, leave `app` main v0.9 source unchanged. CI rebuilds both APKs with per-run debug certs; avoid uninstall/updating installed main MO, whose private GPU rollback snapshot can matter. Verify four GPU keys clean before replacing **only owned disposable test**. GitHub build success is not device runtime proof.

## ADR-030 (MO-20261107-CHAT-P10-DEVICE-018) — P10 real-phone identity lab accepted within simulation scope
**Accepted.** Owner screenshots of process PID5783 verify own-process Build/SoC, Java/NDK GLES and native Vulkan Adreno730 readings, mode0 fake combined 120 FPS LOCKED and mode6 fake combined 120 FPS UNLOCKED using local synthetic identifiers. Real graphics properties remain unchanged and no FPS or Outlanders capability was unlocked. CI validates code; other mock modes need separate device evidence only if useful.

## ADR-031 — Newest GPU Debug readback overrides earlier clean report
**Accepted.** Latest user-supplied v0.9 report is **enable=1** with three owned markers and fresh matching `[READBACK AFTER ACTION]`, even though an older report showed 0/absent. Do not assert rollback complete merely because PID5783 renders Adreno730. Preserve installed MO's private backup and attempt ordinary exact restore first. If backup absent/unsafe, request error and only use explicitly consented four-marker guarded stale cleanup as bounded alternative, never reconstruct unknown baseline. Require new-process physical renderer observation **after** confirmed four-key rollback.

## ADR-032 (MO-20261107-CHAT-P11-FRAME-PACING-019) — Count real owned GL callbacks without misreporting game FPS
**Accepted.** After P10 confirmed fake `UNLOCKED` mock menu, implement a separate owned GLES2 rendering view with cadence 1/2/4 on Choreographer callbacks, and log draw callback counts independently from display Hz. Do NOT call either value displayed-FPS or infer engine FPS of Outlanders. Stop/suspend rendering with Activity lifecycle, use no Shizuku, system writes or game hooks.

## ADR-033 — Respect owner-reported restore and ambiguous app uninstall
**Accepted.** User says GPU Debug was restored and an app deleted, but package identity and exact four-key readback weren't included. Record as **self-reported**, do not demand reinstating main MO for optional owned lab, and do not claim verified clean state. A future global GLES layer activation requires independent current-state check and user consent, not permitted implicitly by "Continue".

## ADR-034 (MO-20261107-CHAT-P12-LOG-EXPORT-020) — Separate owned-app evidence archives without global logcat
**Accepted.** Add a timestamped process-local diagnostic log to main MO and disposable MO Layer Test; persist bounded history (3×3MiB), errors and own-action reports, permit explicit user export to Download/log.txt and clipboard. Never claim that ordinary Android app privilege or Shizuku UID2000 can read all apps' protected logs, and never auto-upload. App-private history is lost at uninstall. Main and probe each export their *own* histories.

## ADR-035 — Use Android MediaStore and report actual output filename
**Accepted.** Android 10+ apps can create their own Download records through `MediaStore.Downloads` with `RELATIVE_PATH` / `IS_PENDING`, no all-files permission. Reuse only self-owned URI from private preferences on repeat; don't overwrite files created by another app; on collision, the OS may choose a different filename, which the UI reports. Clipboard full only under bounded size; truncation explicitly communicated. Both apps must pass CI, real device file/clipboard remains a separate evidence gate. No new GPU layer activation is needed.

## ADR-036 (MO-20261107-CHAT-P12-DEVICE-LOGS-021) — Accept real P12 app-log export and four-key guarded cleanup
**Accepted with scope boundaries.** Two owner-uploaded actual Android15 text exports (35 main events, 119 owned test) prove runtime logging and user-accessible file export, not clipboard success or full system logs. Main log has explicit owner action1 enabled layers, action4 guarded cleanup, repeat action3 diagnoses twice verifying enable=0 and removal of 3 exact MO markers. This supersedes earlier uncertain device-state claims; **known-safe MO marker cleanup is not exact original settings restoration**. No new activation for routine experiments.

## ADR-037 — P11 callback rate versus real presented FPS and loader new-process limitation
**Accepted.** 21 stride1 samples from owned app average 120.47 Choreographer/draw callbacks per second; stride2/4 not yet tested, and neither rate is SurfaceFlinger-presented frames or Outlanders FPS. Owned probe PID23395 started before user enabled developer layer, therefore later same-process `intercepted NO` does not prove failure for a new process. Require new PID after any relevant toggle for loader conclusions, but **do not request new activation** given completed cleanup.

## ADR-038 (MO-20261107-CHAT-P11-STRIDE-022) — Accept owned P11 cadence 1/2/4 on actual Android15
**Accepted.** Owner supplied third private log with 45 `FRAME_SAMPLE` entries: stride1 119.632 Choreographer/draw calls/s; stride2 119.127 Choreographer and 59.563 own draws/s; stride4 120.316 and 30.079 own draws/s. The observed request ratio is 1, 0.5, 0.25 respectively, and no Java error/crash was logged in this captured run. All three **application-local** P11 pacing modes are accepted; no code change/new APK needed solely for this result. `onDrawFrame` does not prove compositor presentation, FPS or behavior of Outlanders.

## ADR-039 — Do not infer present GPU Debug key values from a probe-only log
**Accepted.** New owned test PID31111 started at 05:38 after main MO had independently confirmed last safe four-key cleanup at 05:26. New own-process GL/Vulkan reads show physical Adreno730/no intercept, but this upload contains no new Settings.Global readback. Do not claim exact four-key state at 05:38 without evidence, and do not reactivate developer layers just to test the already verified frame-cadence feature.

## ADR-040 (MO-20261107-CHAT-P11-REPEAT-023) — Treat same-named uploaded diagnostic archives as versioned cumulative evidence
**Accepted.** The new `log (2).txt` is a 41724-byte SHA256 `dbdfe2ecfa429012378459ae212e34c3181619ebe699dba055e30676d5bb7415` file, not the earlier 15982-byte upload. It contains **201 cumulative events** split as 85 prior PID31111 (now with export-result/Frame Lab pause evidence) + 116 new PID3604 events. Separate new and previously counted events; do not count 201 as new or publish the raw user log. In-app clipboard return message is not independent paste verification.

## ADR-041 — Accept repeated P11 and all seven P10 synthetic modes on real POCO only within owned-app scope
**Accepted.** New PID3604 repeated P11 stride1 (120.061/s own render), stride2 (59.834/s), stride4 (30.037/s), while UI callbacks stayed ~120/s. This replicates earlier PID31111 and completes the intended local render-request cadence validation. All 7 synthetic identity menu modes now device-observed; **only** ALL_SIMULATED unlocks the fictional combined gate, unchanged physical GL/Vulkan Adreno730. No actual SurfaceFlinger presentation, stock Outlanders FPS unlock, global spoof or power saving is demonstrated. No new APK/source change warranted.
