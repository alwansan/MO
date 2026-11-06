# User-supplied analysis tool archives — static metadata
2026-11-07 | MO-20261107-CHAT-ECOSYSTEM-003

**Read-only local inspection only; no downloaded executable was run.** These inputs are not committed to MO (public GitHub repository).

| supplied archive | SHA-256 (of supplied bytes) | observed format | proposed use |
|---|---|---|---|
| `rea-agents-6.8.0.tgz` | `8d542ec76a9cb350584b60a6c9c088706ff812d5bb5b094c20c95b6dc25698bb` | npm package `rea-agents` v6.8.0, package MIT, 2398 tar entries; `bin: rea`, Node version `^22.23.4 || ^24.22.1 || >=26.12.1` | Multi-format reverse-engineering workflows in isolated future environment; validate dependencies/sandbox first, and obey third-party license |
| `jadx-headless-mcp-0.7.1-all.jar` | `6e5eacf500b64292bfb73c49797c1958f6ee44646e43e868039ae7feb573ff75` | executable Java JAR manifest `com.atxx.jhmcp.MainKt`; thousands of classes | APK DEX inspection for our own apps or permitted code; not automatically allowed to decompile proprietary iUnlocker binaries |
| `node-v24.22.2-linux-x64.tar.gz` | `361718c444e49af2ca5a110c83ba6a8901143f23ab285ebbef0341f391f482a9` | official-style Node Linux x64 distribution archive | Possibly GitHub Actions ubuntu x64 build tool; **not executable natively on Android ARM64 POCO** |

No commands from these packages were executed, no APK installed and no code altered on device. Treat package metadata as claims, not supply-chain attestation. Future use requires source integrity check, upstream official signatures/checksums when available, and isolated CI permissions.
