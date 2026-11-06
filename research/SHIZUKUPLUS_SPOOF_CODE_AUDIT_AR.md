# Shizuku+ device spoof — code-level limitation
Date: 2026-11-07, MO-20261107-CHAT-BUY-VS-BUILD-005. Read-only GitHub source inspection; no phone test.

Repo https://github.com/thejaustin/ShizukuPlus
UI feature toggle: manager/src/main/java/af/shizuku/manager/settings/DeveloperOptionsFragment.kt lines 111–128.
Settings: manager/src/main/java/af/shizuku/manager/ShizukuSettings.java records spoof target and feature.
Execution: server/src/main/java/rikka/shizuku/server/ShizukuService.java lines 1979–2060.
When Shizuku+ client executes a command via server newProcess and baseCmd equals getprop, if prop starts ro.product.* or ro.build.* and spoof_device enabled, the server chooses a Pixel/Samsung/OnePlus/Nothing profile and returns fake output with newProcessInternal(new String[]{"echo", spoofValue}, env, dir).
This changes the **output of Shizuku+ mediated getprop calls only**. It does not patch global kernel properties, does not modify Android Build.MODEL in unmodified third-party game processes and does not intercept glGetString or vkGetPhysicalDeviceProperties. Any blog saying Shizuku+ spoofs entire device for games omits this decisive scope distinction. Virtual root simulation via SU bridge still operates at actual UID, not root if Shizuku started from ADB.
For original Monster Hunter Outlanders without modification: not a verified unlock solution.
