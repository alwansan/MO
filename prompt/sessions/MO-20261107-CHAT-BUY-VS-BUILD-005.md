Session MO-20261107-CHAT-BUY-VS-BUILD-005, origin chat, 2026-11-07, alwansan/MO main, P0 research.
Objective: existing ready-app check for Monster Hunter Outlanders com.tencent.mhadv (NOT Monster Hunter Now), locked graphics and FPS, no host root/bootloader unlock.
Dated evidence: Capcom Oct29 launch, global com.tencent.mhadv, independent guides show Graphics Quality, High Frame Rate, separate Field/Arena FPS padlocks. Outlanders Android v1.0.9 reported from store mirrors, owner installation not inspected.
Ready candidates: App Cloner v4.0.12 with Nov5 CPU/GPU matching profile (must re-sign cloned game, game compatibility unknown). VMOS+ Nov1 1.1.4 ARM64 Android15 guest 20013 supports Magisk/Zygisk Next/LSPosed; host unrooted, but guest-root route outside strict Shizuku-only and game performance/safety unverified. GFX Tool tsoml/PGT+ PUBG config only; modern LSPatch changes original APK, high risk.
Crucial code inspection: ShizukuPlus spoiler 'Spoof Device Identity' implemented as fake getprop response in ShizukuPlus newProcess shell client, NOT global Java/GLES/Vulkan game spoof. See docs.
No user data accessed, no app built/installed, no game files/settings changed; no new app to publish.
Decision: prioritize ready apps comparison and harmless proof before building MO; no root guest without explicit approval; no live game account experiments.
