# Upstreams and local artifacts — evidence ledger

Analyzed 2026-11-07 using read-only inspection of the **files supplied by the user** (not installed/run).

| Item | Version/reference | SHA-256 | Notes |
|---|---|---|---|
| iUnlocker-GLTool-for-android-v1.1.5-r4.zip | release v1.1.5-r4 | `c40a742aba1907d915881ccfb74c639387caa8669fdca234e36bb5420e9130f7` | 25,969,823 bytes, 123 ZIP entries |
| embedded iUnlockerGL.apk | same ZIP | `621b631a7441d7ba491b662da83540fb7996cb8414ac6787ac77bb65ffa9bc46` | 7,346,905 bytes |
| embedded zygisk/arm64-v8a.so | same ZIP | `636369b8544031bc5a5de2fb33efd505d2d2f397471a18fe1671194885fb5dcf` | 1,075,160 bytes |
| KernelSU_v3.3.1_32680-release.apk | v3.3.1 | `b4a059ace71de2fa65cc7f12b1c58d55fc23ab25e057ee4230ce779742abe450` | 13,637,206 bytes |

Original links:
- https://github.com/i-Taylo/iUnlockerGL/releases/tag/v1.1.5-r4
- https://github.com/i-Taylo/iUnlockerGL/releases/download/v1.1.5-r4/iUnlocker-GLTool-for-android-v1.1.5-r4.zip
- https://github.com/tiann/KernelSU/releases/download/v3.3.1/KernelSU_v3.3.1_32680-release.apk
- https://github.com/RikkaApps/Shizuku

Upstream GitHub commits observed during analysis:
- i-Taylo/iUnlockerGL main: `a80f281db2f655d7cb08f4d2a6d241a53a114bf9` (not necessarily release tag)
- i-Taylo/iUnlockerGL v1.1.5-r4 tag: `d954f0d8c6c9884aeb22e9a93e6eb5552362992e`
- tiann/KernelSU main: `019a3c0103a8f8d56675546016c4073d0fd18352` (moving upstream, later updates expected)

Licenses:
- iUnlockerGL custom license: **no modification, decompilation, reverse-engineering, derivative works; only redistribution original unmodified with attribution**. See `docs/LEGAL_AND_SECURITY.md`.
- KernelSU `LICENSE`: GPLv3. Source not needed to implement rootless Shizuku application.
- Original third-party APK/ZIP files were **not pushed into MO**. If a future session needs binary inspection, user can supply them again; do not pretend they are in the repo.

Do not confuse sha256 (content checksum) with Git SHA1 for repository blobs/commits.
