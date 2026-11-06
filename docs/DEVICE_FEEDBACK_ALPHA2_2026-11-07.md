# MO P2 real-device test: Outlanders / Shizuku ADB shell
Date: 2026-11-07 Asia/Baghdad. Session: MO-20261107-CHAT-P3-VULKAN-008. Source: user's direct full diagnostic report for v0.2.0-alpha.2. No external device access.

## Confirmed on POCO F5 Pro
- Android 15 API35, Xiaomi mondrian / SM8475 / Adreno730, GLES3.2 driver V@0615.95, screen 120Hz.
- Shizuku API13: binder connected, permission GRANTED, UserService UID **2000**.
- com.tencent.mhadv version **1.0.9.0.3308842** installed.
- `cmd game list-modes com.tencent.mhadv` -> `current mode: standard, available modes: [standard,custom]`.
- `cmd game list-configs com.tencent.mhadv` -> **No intervention found for package com.tencent.mhadv**.
- ANGLE global selected package list, value list -> both `null`. Not evidence of unavailable ANGLE binary; just unset per-app selection.
- GPU debug layers -> `enable_gpu_debug_layers=0`, `gpu_debug_app=null`.
- `ro.board.platform=taro`, `ro.hardware.vulkan=adreno`, `ro.soc.model=SM8475`.
- Android PackageManager advertises Vulkan API **1.1**, compute=0, level=1. This is system-declared feature, not native physical-device API properties.

## Interpretation
- P2 Shizuku UserService real device integration **PASS** with UID2000; fixed read-only commands accepted on HyperOS.
- `custom` is listed as possible system Game Mode, NOT a game graphics tier unlock nor documented 120FPS support. No game-mode intervention configured.
- ANGLE null -> no currently selected overrides, not proof that installing/configuring ANGLE will unlock options.
- Vulkan loader/device properties still unknown; next native read in P3 within MO's process only, never in the game.
- The report had literal `\\n` in Vulkan and Shizuku sections. It was a formatting bug in MainActivity.copyReport and is fixed in v0.3 source.
- No game settings written, no app files accessed, no root and no bootloader unlock.

## Next steps
Build v0.3 with NDK Vulkan vkEnumeratePhysicalDevices/vkGetPhysicalDeviceProperties in MO process. Confirm difference between Android advertised Vulkan 1.1 and VkPhysicalDevice API version/vendor/driver. Do not claim this exposes Outlanders internal feature whitelist.
