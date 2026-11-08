# MO-20261108-CHAT-P25-GUEST-IO-038
**2026-11-08 Asia/Baghdad | origin=chat | branch=main | role=MO owned-guest engineering**
**Source HEAD at start:** 5343f4d22707885f5fdbadb621e0373835294bfb

## Restored baseline
Read live main state, P24 docs/sessions, current Actions and Releases. No session newer than P24 at start; previous run 42429759178 confirmed Renjana pinned build success. P24 user device capture is retained as measured evidence; no fresh P25 phone experiment exists.

## P25 source audit
Renjana source pinned at 14302a57cd66114c6979acf7ca56c97f845a6841. Inspect:
- PineHookManager.installGuestHooks registers package, initializes AntiDetection when enabled, unconditionally attempts hookFileExists.
- AntiDetection.initialize registers guest dataPath and /data/{data,user/0}/com.fesu.renjana as hidden; shouldHidePath additionally hides strings containing renjana.
- CoreHooks.createFileExistsHook returns false for hidden paths. VirtualContext.ensureDir calls File.exists()/mkdirs()/File.exists(). Thus an AntiDetection-on guest may report directories nonexistent even when libc can stat/use them. This is a strong **source-backed hypothesis**, not device-confirmed filesystem failure/fix. Fifteen old P24 VirtualCtx warnings have not yet been experimentally isolated.
- Android15 Pine missing symbol logs were also seen in P24; the presence of other successfully installed hooks does not prove all hooks work.
- The guest ClassLoader resolves Java and guest JNI but by itself does not override native Qualcomm GLES/Vulkan driver functions.

## P25 implementation, limited to disposable owned probe
- New FilePathCheck.java performs unique temporary private-file write/read/delete while reporting File.exists, isDirectory, canWrite independently. No foreign file removal.
- New OwnedStorageProbe.java, explicit user-button-only, compares Activity/Application Context.openFileOutput/openFileInput, direct File streams, canonical private path equality and native libc checks; output hides private absolute paths and logs only owned app result.
- New nativeStorageRoundTrip in MOProbeRenderer.so: stat/mkstemp/write/read/unlink on Context.getFilesDir only, rejects non-/data private paths. This tests whether File.exists false is Java-hook visibility vs physical failure in the same process.
- ProbeActivity displays Vulkan deviceName with Java/NDK/EGL in the *same PID*, plus P25 opt-in button. Probe version bumped from 0.6.0 to 0.7.0. FilePathCheckTest has three JUnit tests for cleanup, missing directory, and non-directory.
- No Renjana upstream source change, no system-wide debug settings, ANGLE, Root, bootloader, game/account/private data changes. Only app-owned few-byte ephemeral files when button pressed.

## Commits
- FilePathCheck.java 82faf5ef6843fd59890ff587a3c3f41f2cfe0cff
- OwnedStorageProbe.java ef3aa0e70a7d0acbbeef37cf304479b082241914
- Native libc helper 61933b106d988ea3191cd4076eb085b9256e0f7a
- JNI bridge 520fa23b8948ee1a0345db4e92d7b9ef1f2a4272
- UI/Vulkan d365586b2709e968e1d060c05733f1dcdce3b04e
- FilePathCheckTest a8a2243d89699ade2455e89f94b1d20d54cb5ac6
- Probe version 0195d5def3888a24d78b89cf879679e35e2199ef
- docs/P25_RENJANA_GUEST_STORAGE_DIAGNOSTIC_AR.md 7936f0c598d45eb2c4349772b9b785226691d80b

## CI and acceptance
- GitHub Actions on probe/** runs app/probe JUnit, native Gradle builds and publishes two raw APKs. Run 42433494794 (#66) started for probe version commit 0195d5def3888a24d78b89cf879679e35e2199ef; verify completed state before claiming Release. Earlier run 42433451471 (#63) success was for incomplete source (only through JNI bridge) and does NOT prove P25 v0.7 build.
- P25 source has **not been run on phone**; do not infer storage PASS/FAIL or new Vulkan renderer identity. If CI success, user may test **only new disposable MO Layer Test** in Renjana and collect same-PID outputs and optional P25 private IO. Do NOT replace main MO (risk its debug signing/rollback snapshot). Do not reactivate any four GPU Debug settings; P24 Rish readback was UNKNOWN. No FPS/Outlanders claim.
- See docs/P25_RENJANA_GUEST_STORAGE_DIAGNOSTIC_AR.md. Next: confirm final CI+Release, then owner's one controlled P25 owned-app observation. Only after this evidence consider a bounded Renjana source fix for AntiDetection versus VirtualContext, and separate native GL/Vk identity interception R&D with own guest app.

## CI and Release verified closeout
- Run #66 https://github.com/alwansan/MO/actions/runs/42433494794 completed **SUCCESS** for P25 source 0195d5def3888a24d78b89cf879679e35e2199ef: JUnit, native/debug builds, and public release.
- https://github.com/alwansan/MO/releases/tag/mo-v0.11.0-alpha.11-run66 has owned disposable MO-Layer-Test-v0.7.0.apk, 230156 bytes, SHA256 0ebe84f65242036802db2e9e0817902c783f1089096518dec07d684f8b5d4462.
- Main MO v0.11.0-alpha.11 was also rebuilt by existing workflow; do not install it solely for P25. Only owned test APK is needed, after protecting exported logs from previous debug-signed version.
- On-phone P25 storage/Vulkan still **not tested**; CI success does not prove false File.exists, actual Guest I/O, GPU spoof or real FPS. Do not rerun P23 Rish for the same data.
