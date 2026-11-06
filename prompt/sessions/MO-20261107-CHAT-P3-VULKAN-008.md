# Session MO-20261107-CHAT-P3-VULKAN-008
- origin: chat, 2026-11-07 Asia/Baghdad
- branch: main, repo alwansan/MO.
- Owner sent full v0.2.0-alpha.2 diagnostic. Shizuku service UID2000 GRANTED, com.tencent.mhadv 1.0.9.0.3308842; Android game modes [standard,custom], current standard; no game interventions. ANGLE driver selections null. GPU debug layers 0. ro.board.platform taro, ro.hardware.vulkan adreno, SoC SM8475. PackageManager declared Vulkan 1.1/compute0/level1.
- Device P2 successful. No proof of FPS unlock; no writes to system or game.
- New v0.3 source: pinned NDK and CMake, own-process Vulkan enumeration/native VkPhysicalDeviceProperties (read only), fixes literal \\n report, increment versionCode3.
- Tests/CI: inspect GitHub Actions result after commit; APK cannot be called working before successful build and user device trial.
- Caution: ephemeral debug signing changes across CI runs, so uninstall MO only if upgrade fails; never uninstall Shizuku or games.

## CI closeout / delivery
- GitHub Actions run `42308837332` succeeded all steps, source commit `eef8fb46f8ab49412ab4ab04554128e56b0dad36`.
- Artifact ID13547731335 `MO-Graphics-Probe-debug-4`. APK is 1,102,410 bytes. SHA256 `203b8c47d3da67cca1e6ffe212682824d67a864a556b6cd12d7717b7f14dbe92`.
- Downloaded and checked APK compressed data, confirmed lib/arm64-v8a/libmo_vulkan.so. User given direct APK in chat.
- Native device Vulkan call not yet tested; report needed from user. No game modifications/root/unlock/VM/clones.
