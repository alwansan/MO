# MO — apps first, build only if necessary
Date 2026-11-07 | MO-20261107-CHAT-BUY-VS-BUILD-005 | P0 research only. No phone changes.

## Goal
The exact primary game is Monster Hunter Outlanders global Android com.tencent.mhadv, not Monster Hunter Now. Goal: unlock FPS/graphics settings shown with padlock on stock POCO F5 Pro Android 15, without unlocking bootloader or rooting host phone.

## Decision table
| Existing option | How it works | Will it work for original unmodified Outlanders? | Risk |
|---|---|---|---|
| App Cloner (AppListo) v4.0.12 | Clones/re-signs app; Nov 5 v4.0.11 added CPU/GPU profile matching selected device profile | Unverified; operates in clone, not original game | New certificate may break Play login, integrity, purchases, save paths |
| VMOS+ 1.1.4 virtual Android15 system 20013 | Root/Magisk/Zygisk Next/LSPosed in isolated virtual Android without root/unlock of host | Unverified. Game must run inside VM, not original host instance | Vulkan passthrough, GPU performance, VM detection, account restrictions; not Shizuku-only |
| ShizukuPlus device spoof | Source code intercepts getprop calls via Shizuku+ newProcess and echoes forged ro.product/ro.build strings to clients | NO evidence of changing original game's Android Build, native GL or Vulkan values | UI label exaggerates scope for this goal |
| GFX Tool tsoml, PGT+ | Uses Shizuku/file access for supported PUBG configuration files | No verified Monster Hunter Outlanders support | game configs overwritten, patch/version compatibility, game policy |
| LSPatch / JingMatrix / LSPatch-A15 | Repackage and sign target APK to embed Xposed runtime | Unverified; original signature changes | installation, integrity and save/account risks |
| COPG / iUnlockerGL / Game-Unlocker | Zygisk/LSPosed hooks inside target game | Technically relevant but NOT compatible with no-root host | requires kernel/root infrastructure |
| GAMA | Shizuku sets HWUI renderer Android UI | Does not spoof GPU inside game's EGL/Vulkan calls | UI instability if restart system components |
| Virtual containers | Runs game inside controlled process/environment | No proven Outlanders compat | 3D overhead, anti-virtualization, login |

## Critical ShizukuPlus source audit
Official https://github.com/thejaustin/ShizukuPlus .
Server implementation https://github.com/thejaustin/ShizukuPlus/blob/master/server/src/main/java/rikka/shizuku/server/ShizukuService.java around lines 1979–2060.
When a Shizuku+ client invokes getprop ro.product.* or ro.build.* via newProcess, spoof_device substitutes a preset string returned by newProcessInternal with echo. This is NOT an Android system property rewrite and does NOT hook Java Build fields or GLES/Vulkan APIs inside unrelated com.tencent.mhadv. The SU bridge reroutes commands to existing shell privileges; it is not root escalation.

## App Cloner evidence
Official https://appcloner.app/ ; release 4.0.12 Nov 6, 2026, notes Nov 5 profile option Randomize CPU / GPU profile to match selected device profile. Developer itself warns Google Play/Play Games, Play Integrity, signatures, Google logins and purchases can fail in cloned apps. Source is proprietary, so precise GL/VK interception is UNVERIFIED. This is a ready-made app only for modified copies, not unmodified client. Test owned disposable app first.

## VMOS+ evidence
Official https://www.vmos.app/en/ ; first-party release notes https://docs.vmos.app/en/releases/history and https://docs.vmos.app/en/releases/roms.
Nov 1, 2026: client 1.1.4, Android15 ARM64 virtual OS 20013 (963 MB). Says Magisk, Zygisk Next, LSPosed/Vector in virtual OS; imports APKS, APKM, XAPK, ZIP. The real phone remains unrooted, but guest does have root. Thus outside strict Shizuku-only and needs separately explicit scope authorization. There is NO measured game success, native GPU passthrough proof, or demonstration of Outlanders working in guest. Do not run game with primary account as first proof.

## GFX tools
Official tsoml Google Play https://play.google.com/store/apps/details?id=eu.tsoml.graphicssettings . Official update channel https://t.me/s/Gfxtool says Shizuku support, also acknowledges that PUBG 1.7 blocked most public-config option access. PGT+ listing https://shizukuapps.com/apps/pgt-pro-graphics-toolkit/ is a community directory, not proof of current Outlanders support. These are PUBG-oriented, not arbitrary GPU identity spoofers.

## Pre-build no-harm validation gate
1. Confirm exact installed com.tencent.mhadv version. Photograph original Graphics page and which padlock applies: Graphics Quality, High Frame Rate Mode, Frame Rate (Field) and Frame Rate (Free Hunt/Arena).
2. Try only original in-game settings and normal HyperOS display/game mode; collect real frame timing. No scripts or APK modifications.
3. Verify source/version and harmless owned diagnostics app for any external app being evaluated.
4. For optional VM path, require clear approval because guest root is not Shizuku-only; validate guest GLES/Vulkan and graphics performance before any game/account.
5. Do not automatically uninstall/replace live games or patch originals; risks to account/data and game anti-tamper rules.
6. If these do not solve the goal, build narrow independent MO diagnostics/test layers. A custom Shizuku app cannot bypass Android's normal process boundary.

**Conclusion**: no verified ready app that universally unlocks graphics/FPS in unmodified original Outlanders on stock non-root phone. App Cloner and VMOS+ are the closest alternatives but each changes the application/runtime context.
