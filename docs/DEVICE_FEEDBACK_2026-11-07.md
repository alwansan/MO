# MO P1 real-device confirmation — 2026-11-07
Owner supplied 3 screenshots and full local diagnostics from installed v0.1.0-alpha.1.
- Device POCO F5 Pro, Xiaomi 23013PC75G/mondrian, Android15 API35, SoC SM8475, 8 visible cores, arm64-v8a, 120Hz.
- Shizuku binder connected, API13, permission GRANTED, effective **ADB shell UID2000**.
- OpenGL ES process query in MO: Qualcomm, Adreno(TM)730, GLES3.2, driver V@0615.95 GIT@c1fe339905, max texture 16384.
- Monster Hunter Outlanders global com.tencent.mhadv installed version1.0.9.0.3308842.
- UI and copying report work, no crash in screenshots.
- These are MO-owned process queries, not proof of game process spoof. No game settings/bootloader/root changed.
- P2 next: Shizuku read-only UserService Game Mode and ANGLE data, Android-declared Vulkan API; test on device after successful CI.
