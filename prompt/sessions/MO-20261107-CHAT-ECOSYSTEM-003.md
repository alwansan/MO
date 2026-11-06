# Session MO-20261107-CHAT-ECOSYSTEM-003

- origin: chat
- date Asia/Baghdad: 2026-11-07
- repository: https://github.com/alwansan/MO
- branch: main; phase: P0 research/planning (not app build)
- instruction: research more projects akin to iUnlockerGL/KernelSU/Shizuku no-root GPU spoofing and related injection; inspect supplied research tool archives.
- primary reviewed sources (selected source code and docs): palincat/gama, ulolol/Ka0SGFX, catsmoker/catsmoker-app, LSPosed/LSPatch, konglongbt/LSPatch-A15, bylaws/libadrenotools, K11MCH1/AdrenoToolsDrivers, fdavids77/BlackBoxReborn, android-hacker/VirtualXposed, Frida/OWASP, ZygiskNext, COPG, Render-Switcher, Android GLES/VK layers, ANGLE, Game Mode.
- confirmed GAMA Shizuku `setprop debug.hwui.renderer` route only affects HWUI renderer; does not forge game GPU capabilities. Restart handling potentially hazardous on HyperOS.
- confirmed KaosGFX source rootless mode patches base APK using AppComponentFactory and native libs, re-signs splits and uses Shizuku installer; not a zero-modification injection. README claims nonroot Adreno 750 test, **not independently verified** on user's device.
- critical risk: KaosGFX PrivilegedService.kt has default signature-mismatch automatic `pm uninstall --user 0` and only backs up Android/data and Android/obb, not protected internal app data. Do not use on user primary game.
- confirmed CatSmoker Shizuku method mostly game-data/files/settings; LSPosed required for actual in-app spoof native hooks.
- independent libadrenotools works only for app that integrates it; GLES layers official for eligible apps; ANGLE conditional.
- uploaded tools metadata inspected without executing: `rea-agents-6.8.0.tgz`, `jadx-headless-mcp-0.7.1-all.jar`, `node-v24.22.2-linux-x64.tar.gz`; SHA-256 and notes in tool inventory.
- output: `research/ROOTLESS_ECOSYSTEM_COMPARISON_AR.md`, `research/POC_TEST_MATRIX_AR.md`, `research/UPLOADED_TOOLING_INVENTORY.md`; updates to MEMORY, STATUS, PLAN, RESTORE, DECISIONS, AGENTS.
- not performed: compile or build APK, install app, execute privileged operation, device tests, CI workflow, repack real user's games.
- next: no-root debug probe in new build phase (only after user permission); preserve personal games and signed data.
