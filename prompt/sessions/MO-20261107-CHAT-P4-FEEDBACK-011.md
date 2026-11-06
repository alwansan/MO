# MO-20261107-CHAT-P4-FEEDBACK-011
- origin=chat, 2026-11-07 Asia/Baghdad; repository alwansan/MO branch main.
- Owner screenshots/reports from MO v0.4: Shizuku UID2000 ADB granted. GPU debugging settings successfully switched to enable_gpu_debug_layers=1, gpu_debug_app=com.alwansan.mo.glesprobe, activation returns OK. Disposable GLES test still shows GL_RENDERER Adreno(TM)730, Mock 120 FPS LOCKED and Layer intercepted NO.
- No confirmation of user hitting Restore yet. **Must restore v0.4 exact four keys before uninstall/upgrade**, since backup is in private app data and debug signatures change between builds.
- Android official GLES layer documentation verified. Target must be debuggable or opt-in; code has those features. Cannot conclude failure is rooted in HyperOS or an improperly named key without logs.
- New v0.5 source includes native NDK glGetString comparison vs Java GLES20, process PID, user-triggered System.loadLibrary ELF test (not proof of OS layer injection), Shizuku service read-only exact settings and filtered EGL loader log messages. No non-owned app targets.
- CI run 42312793163 SUCCESS, Release https://github.com/alwansan/MO/releases/tag/mo-v0.5.0-alpha.5-run11, two raw APKs main 1118826 bytes and probe 23952 bytes.
- Pending: owner Restore success confirmation from installed v0.4; only after that, v0.5 optional install and PID/Java/Native/filtered log evidence. No FPS or graphics unlock has occurred in Outlanders.
