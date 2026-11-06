# MO v0.3 actual Vulkan on-device proof (user report)
Date 2026-11-07 Asia/Baghdad, session MO-20261107-CHAT-P4-LAYER-009.

**Shizuku server** UID 2000, API 13, permission GRANTED; all previous P2 reads retained.
**GLES** GL_VENDOR Qualcomm; GL_RENDERER Adreno(TM)730; GLES3.2 V@0615.95; max texture16384.
**Vulkan** native vkEnumeratePhysicalDevices succeeded in MO own process: 1 physical GPU, name Adreno (TM) 730; Vulkan API 1.1.128, vendorID 0x5143 (Qualcomm), deviceID 0x7030002, driverVersion raw 0x8026705F, deviceType1, maxImageDimension2D16384, maxComputeWorkGroupInvocations1024, samplerAnisotropy yes.
**Outlanders** original com.tencent.mhadv version1.0.9.0.3308842 detected.
**Game Mode** standard/custom available, no configured interventions.
**ANGLE** driver selections null, debug layers 0.

Meaning: P3 real native Vulkan loader functionality on POCO success. Values belong to MO, NOT Outlanders internal game GPU detection or server gating. No FPS unlock, game profile change, root, bootloader unlock, VM or clone occurred.

Next P4 target: disposable owned application `com.alwansan.mo.glesprobe` to demonstrate real API-level GLES layer interception only for debuggable/opt-in apps. Never target Outlanders using this controlled test.
