# Session MO-20261107-CHAT-P2-DEVICE-007
Origin chat, 2026-11-07 Asia/Baghdad, branch main.
Verified P1 phone proof from screenshots/report: Shizuku UID2000 API13 GRANTED, Adreno730 GLES3.2 V@0615.95, Outlanders global com.tencent.mhadv v1.0.9.0.3308842, device POCO F5 Pro Android15.
P2 code: independent Android Shizuku UserService via AIDL (read-only fixed allowlist of cmd game list-modes/list-configs, settings get global ANGLE and GPU debug keys, getprop selected hardware fields), only UID2000; Vulkan feature declaration. No app game file modifications, no root, no APK cloning/VM.
Version 0.2.0-alpha.2; JUnit allowlist tests. CI runs automatically on push.
Check actual GitHub Actions result before claiming delivered APK; user runtime validation of UserService pending.

## Build closeout
- Source commit 598fc91eb3324762765ef9e7a38fc2c7410e23ae.
- GitHub Actions run 42307946638 completed SUCCESS, testDebugUnitTest, assembleDebug, SHA, artifact upload all successful.
- Artifact ID 13547166555 name MO-Graphics-Probe-debug-3, APK 125919 bytes, SHA256 7a98dbbb0393256210b76249c79505df34b89e34e98c3677545cd0d6b5e23d5b. APK downloaded and tested ZIP CRC.
- User direct APK link supplied in chat. Not yet tested P2 on physical phone, pending user copy report.

## Additional package certificate inspection
APK v2 signer X.509 digests verified from signed APK blocks offline; v1 cert 6ea43d29063eae454fa7769c20392cd58effe4653585d79df5c5384b3ce7a2da, alpha2 cert 4d2e87fa998d2c8c901c6e8f21417fbe28ce6b2f8109e35cb8dd7ef46f1be365. Different certificates; Android cannot update in place. User notified to uninstall MO only first. Follow-up: implement user-controlled stable signing with GitHub Secret when safe.
