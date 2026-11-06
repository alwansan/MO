# Session MO-20261107-CHAT-P3-VULKAN-008
- origin: chat, 2026-11-07 Asia/Baghdad
- branch: main, repo alwansan/MO.
- Owner sent full v0.2.0-alpha.2 diagnostic. Shizuku service UID2000 GRANTED, com.tencent.mhadv 1.0.9.0.3308842; Android game modes [standard,custom], current standard; no game interventions. ANGLE driver selections null. GPU debug layers 0. ro.board.platform taro, ro.hardware.vulkan adreno, SoC SM8475. PackageManager declared Vulkan 1.1/compute0/level1.
- Device P2 successful. No proof of FPS unlock; no writes to system or game.
- New v0.3 source: pinned NDK and CMake, own-process Vulkan enumeration/native VkPhysicalDeviceProperties (read only), fixes literal \\n report, increment versionCode3.
- Tests/CI: inspect GitHub Actions result after commit; APK cannot be called working before successful build and user device trial.
- Caution: ephemeral debug signing changes across CI runs, so uninstall MO only if upgrade fails; never uninstall Shizuku or games.
