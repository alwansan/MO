# POCO F5 Pro — why 120 Hz ≠ unlocked 120 FPS
2026-11-07 | MO-20261107-CHAT-GAME-PROFILES-004

## Verified hardware
Official Xiaomi POCO F5 Pro product specifications: Snapdragon **8+ Gen 1** (not plain 8 Gen 1), up to 120 Hz AMOLED display, max 12 GB RAM. Third-party published GPU specification: **Adreno 730**; validate actual Vulkan/OpenGL strings on the device in a future probe.
- Xiaomi: https://www.mi.com/sa-ar/product/poco-f5-pro/specs/
- POCO: https://www.po.co/global/product/poco-f5-pro/
- GPU listing: https://www.phonearena.com/phones/Xiaomi-POCO-F5-Pro_id12137
- Device-specific Android 15 HyperOS fingerprint was shown by owner in previous screenshots; do not infer this from product website.

## PUBG Mobile / BGMI
PUBG Mobile 4.4 update announcement (dated May 11, 2026) explicitly describes chipset-performance based 120 FPS gating, with **Snapdragon 8 Gen 3+** first-batch Qualcomm reference and individual evaluation for other chips; device scope can change dynamically. This POCO has 8+ Gen 1, older than first-batch reference. **Do not claim 120 unsupported in every future build**; final test is the in-game graphics menu and official version-specific compatibility.
https://event.bittopup.com/news/v4-4-update-announcement (mirrored PUBG Mobile announcement)
BGMI help states 120 FPS is only available when Ultra Extreme appears in the game's Graphics menu on eligible devices; 120 Hz panel setting is necessary in cases but not sufficient:
https://bgmi.helpshift.com/hc/en/6-bgmi/faq/710-guide-on-enabling-120-fps-in-bgmi/
The support classification can use chipset + GPU + real capabilities + device model + in-game whitelist, and isn't known to reduce to one variable.

## Monster Hunter Now
Official supported-device criteria Android 9+, 64-bit, Snapdragon 855+ reference, RAM 3 GB, 720×1280 and Play Protect certification. **Rooted devices explicitly unsupported**. Its own settings include **60 fps While Hunting**, so do not classify that as an externally locked 120 FPS feature without evidence.
- https://scopelyexplore.helpshift.com/hc/en/15-monster-hunter-now/faq/2392-supported-devices/
- https://scopelyexplore.helpshift.com/hc/en/15-monster-hunter-now/faq/2405-troubleshooting-lags-and-freezes/

## What MO should measure per game
- Installed app package, app version, renderer actually used (OpenGL ES vs Vulkan), native capabilities where accessible.
- Screenshot of graphics presets **as actually shown** (user may share voluntarily, not uploaded automatically).
- Display's current vs supported refresh, Game Mode, real FPS and frame-time history before/after *supported* settings changes.
- Direct machine identity of **MO's own probe**, never claim this proves which exact API closed PUBG/MH Now uses. Identify exact game's detection path only with lawful source, official documentation, or authorized cooperation.
- GPU strings like Adreno 730 are **identifiers**, not evidence of 120 stable FPS capability.
- No claims that a config file or device rename bypasses game policy; no safe account-ban guarantee.

## Risk in online games
PUBG Mobile official help warns external unsupported programs modifying the application can lead to permanent ban:
https://pubgmobile.helpshift.com/hc/en/3-pubg-mobile/faq/18-what-happens-to-player-who-violate-rules-use-of-illegal-software-abnormal-behavior-etc/
MO must not auto-edit live competitive game settings or patch original game binaries without reviewing rules, permissions, backup/data loss, and informed user choice. Begin in independent testing.
