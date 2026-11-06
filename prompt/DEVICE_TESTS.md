# POCO F5 Pro test protocol — before any release

Tests are **planned, not yet run**. Device evidence from user screenshots: Android 15, HyperOS OS3.0.5.0.VMNMIXM, KernelSU Manager displays Not installed.

## Stage 0 baseline, read-only
- Confirm official `Settings > About phone` version and architecture; Shizuku version/service connected; obtain server effective UID safely (expect 2000).
- Record device-reported Vulkan/GLES and available developer graphics options. Do not request full fingerprint/log dumps by default.

## Archive
- Import supplied iUnlocker ZIP using SAF; verify ZIP sha256, module.prop, 123 entries, contained APK sha256.
- Test malicious ZIP names (`../`, absolute paths), duplicate filenames, giant decompression, truncated file; no output escapes target directory.
- Ensure **no ZIP script is ever run**. Install only extracted APK with system consent; verify PackageInstaller returned status. App UI may still require root service.

## Shizuku
- Negative test with server stopped; not rooted fallback; deny permission; service killed; permission revoked; reboot requires reinitialization.
- On HyperOS check official Shizuku guide for OEM debugging limitation; never request bypass by force-enabling adb security.

## ANGLE optional (if supported)
- Record original settings `angle_gl_driver_selection_pkgs`, `angle_gl_driver_selection_values` including unset states.
- Test harmless GLES app then target game only after user approves.
- Show screenshots and relevant Render/GLES evidence rather than assume setting worked.
- Restore exactly previous settings, even on crash, package uninstall, reboot. If external changes conflict, warn and avoid overwriting.

## Security
- No boot/system partition changes, no root requests, no SELinux weakening, no background network connections by default.
- CI tests/lint/compile only after planning phase complete; collect apk hash and install evidence.
- Test passes require device owner confirmation. Never claim test completed from desktop static analysis.
