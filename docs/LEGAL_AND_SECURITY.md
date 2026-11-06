# Legal and security policy

## Third-party rights
**Shizuku** use official published SDK and comply with upstream license and attribution when implemented. Review exact dependency at P1.

## Threat model
- ZIP inputs may contain traversal paths, symlinks, zip bombs, malformed manifests, embedded executables. Treat all data as untrusted.
- Never execute external shell scripts/native binaries extracted from user input; never `chmod +x` + execute ZIP content.
- Shizuku is powerful relative to sandboxed apps but **not root**. Confirm UID and each specific permission. No arbitrary unbounded script executor or elevation claim.
- Do not alter /data/adb, kernel, verified boot, SELinux, system partitions or silently toggle debugging.
- Any global settings write must require preview, consent, capability probe, and full rollback journal.
- Logs are device-private, scrub identities and packages by default, user initiates export. No analytics by default.
- Signed/reproducible build and pinned deps required before publishing.
