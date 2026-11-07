# MO architecture (initial proposal with P9 implementation addendum)

## Module boundaries
- **app/** Kotlin + Jetpack Compose. No device modifications without explicit user action.
- **core-archive/**: ZIP read-only parser, safe extract of embedded APK, SHA-256, API-safe streaming and size limits, no script execution.
- **core-device/**: public Android APIs for GLES/Vulkan/ABI/kernel/build info, annotated 'reported' vs 'verified'.
- **core-shizuku/**: official Shizuku API binder/user-service bridge, permission handshake, UID check, liveness, constrained operations (no raw unrestricted command console).
- **feature-graphics/**: ANGLE capability probe, per-package preview, transaction journal, apply/verify/rollback; feature gated on support.
- **feature-installer/**: default system PackageInstaller and optional explicit Shizuku ADB fallback if allowed. Shell install != module install.
- **feature-report/**: local evidence export, sanitized logs, explicit redaction and user-initiated sharing.
- **docs/**: independent upstream links/licensing, matrix, troubleshooting and tests.

## Flow
1. User selects ZIP via Storage Access Framework (persist permissions only when needed).
2. Archive parses metadata, rejects path traversal/symlink extraction and zip bombs.
3. UI labels original module 'Requires root/Zygisk' with 'Extract APK' optional action.
4. Shizuku capability screen reports server running, effective UID, allowed operations; never elevates privilege itself.
5. User picks one installed GLES app; optional ANGLE probe; preview and rollback; no action on unsupported systems.
6. Logs stored private and share only by consent. No telemetry by default.

## Technical invariants
- No `su`, `insmod`, `fastboot`, `dd`, `/data/adb` writes, direct boot partition access, unsupported Zygisk bridge, SELinux changes, or third-party ZIP commands.
- Explicit allowlist for broker operations. Prefer typed binder APIs to concatenated shell. If using settings shell, validate package names and use fixed commands/args.
- Rollback journal records pre-write states and verification. Handle process death and user-modified settings safely.
- No hidden auto-apply, no persistent accessibility service required.
- No vendor's proprietary classes/assets/scripts copied into app.
- Trust model: untrusted ZIP bytes, untrusted installed app metadata, controlled Shizuku broker with identity check.

## Future controlled proof-of-concept: GraphicsProbe + external layer
- `feature-probe/` (new, original code in future implementation phase): debuggable test GLES target, measures glGetString GL_VENDOR/RENDERER/VERSION and logs actual layer presence.
- `graphics-layer-gles/` (future, original NDK C/C++): passive layer passes through all calls at first. May intercept in **own** eligible app only after proven.
- Shizuku `GpuLayerSettingsBroker`: read-only capability probe; consent; exact settings snapshot; per-app write; logcat check; rollback on failure.
- Only whitelisted settings keys: `enable_gpu_debug_layers`, `gpu_debug_app`, `gpu_debug_layers_gles`, `gpu_debug_layer_app` (and Vulkan `gpu_debug_layers` separately). Never unbounded shell injection.
- Eligibility check: app debuggable OR manifest opt-in on API/target-supported versions. No attempt to bypass nondebuggable process policy.
- This POC does NOT provide a universal GLTool replacement for closed third-party games.


## Actual implementation as of MO-20261107-CHAT-P9-AUDIT-015 (supersedes historical future-tense proposal)
- Android Java `app/`: MO Graphics Probe, Shizuku v13 UserService with command allowlist, read-only native Vulkan probe, game manifest eligibility, and owned-layer settings control. Kotlin/Compose, archive inspector, and general installer in original proposal are **not implemented** merely because they were planned.
- Separate `probe/`: disposable debuggable `com.alwansan.mo.glesprobe` with independently written GLES proof layer and Java/NDK/eglGetProcAddress reports. Native simulation was observed on the user's POCO; not a FPS change in Outlanders.
- `OwnedLayerSettings` allows exactly four Global developer GPU debug keys with snapshot, restore, guarded MO-only stale cleanup. v0.9 `MainActivity` rereads the layers and shell snapshot after Shizuku action. This does **not** authorize hooks into stock nondebuggable games.
- Packaging: `.github/workflows/mo-android-debug.yml` builds both APKs on source-path changes and publishes experimental direct APK assets. Debug signing varies; do not uninstall/upsert while a rollback snapshot is needed.
- **Open safety gate**: fresh read-only phone values post-v0.8 cleanup, followed by newly launched owned test app showing physical Adreno 730 (NDK/eglGetProcAddress). Main project issue remains stock Outlanders non-opt-in.
- Code-review hypothesis for later: avoid possible simultaneous initial Shizuku snapshot and pending action refresh on cold service connection. This was not device reproduced.
