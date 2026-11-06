# MO architecture (proposed; not implemented)

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
