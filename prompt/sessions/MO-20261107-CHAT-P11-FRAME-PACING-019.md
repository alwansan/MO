# MO-20261107-CHAT-P11-FRAME-PACING-019 — owned GLES frame callback measurement
- Date: 2026-11-07 Asia/Baghdad
- origin: chat
- repository https://github.com/alwansan/MO
- writable branch: main
- initial HEAD: `f09687ed6b815d421f5e8ca95f8e1f01ec46f86d`.
- owner requested: "لقد بمت ب استعد إعدادات GPU Debug وحذفت التطبيق لذالك اكمل العمل Continue" (owner says they restored GPU Debug settings and uninstalled an app; they did **not** specify which app).
- **Do not assume** exact four settings values or which package was uninstalled. Prior P10 report had enable=1 and three MO markers; user now self-reports restoration+uninstall, but there is **no new independent readback**. Treat rollback as owner-reported, not device-verified; preserve safety no active layer experiments.
- Work plan: implement limited new P11 **real callback instrumentation inside disposable MO test** to distinguish mock FPS unlocked text from measured FPS-like counters, without claim of actual display FPS or commercial-game gameplay FPS. No source alteration to `app/` main, game packages, system settings or Shizuku service.

## Live baseline checks
- Re-read AGENTS, STATUS, MEMORY, RESTORE, prior P10 phone evidence and owned lab code/manifest/build/CI. Main initial SHA above.
- P10 proven on POCO in owned lab: actual Build/SoC/GLES/Vulkan Adreno730, display 120Hz, virtual menu modes 0 and 6; neither is FPS proof.
- Latest pre-P11 successful run `42324225397`, owned probe v0.4.0 and main MO v0.9.0-alpha.9. Debug signatures differ per run.

## Implemented in this cycle
1. `probe/src/main/java/com/alwansan/mo/glesprobe/FramePacingWindow.java`: pure Java monotonic 1-second window computing separate UI Choreographer callback/s and GL renderer onDrawFrame callback/s, with reset/invalid count handling. Does not claim compositor output.
2. `probe/src/test/java/com/alwansan/mo/glesprobe/FramePacingWindowTest.java`: 5 JUnit tests verifying independent callbacks vs draws, interval math, reset/short samples and negative input rejection.
3. `probe/src/main/java/com/alwansan/mo/glesprobe/FramePacingLabActivity.java`: drawable color-changing GLES2 surface; 3 cadence modes (request render per 1/2/4 Choreographer callbacks), onPause removes callback and pauses EGL, onResume restarts; display physical Hz setting separately from measured callback rates, labeled NOT MEASURED for displayed FPS/other game.
4. `probe/src/main/java/com/alwansan/mo/glesprobe/GameIdentityLabActivity.java`: navigation button and Android 15 top/bottom WindowInsets padding to fix observed clipped P10 title.
5. `probe/src/main/AndroidManifest.xml`: non-exported frame lab. `probe/build.gradle`: versionCode5/versionName0.5.0; MO main version not touched.
6. `docs/OWNED_FRAME_PACING_LAB_AR.md`: scope, experiment protocol and Android official refs.
- No root/bootloader, no privileged writes, no arbitrary process injection, no game data access, no third-party proprietary binaries/code imported, no APK run on device, no external shell scripts.

## CI status
- Newest complete-source build auto-started by GitHub on commit `9abd11a3cbc3062836ceef6f2facd968286faa6f`: https://github.com/alwansan/MO/actions/runs/42325865139.
- Early CI work may overlap/cancel intermediate commits. Only final complete-source build should be counted.
- Append run conclusion + Release assets + hashes after verification. Do not claim APK exists or P11 on-device success before verified.

## Next gate
- Verify latest workflow passes `app/probe testDebugUnitTest`, `app/probe assembleDebug`, APK ZIP test and Release upload; repair any CI failure before concluding.
- Optional owner phone test on the new separate disposable probe, if desired, without restoring/reinstalling main MO automatically. Verify P11 draw callbacks at stride 1/2/4; never equate callbacks with true presented FPS.
- For GPU Debug cleanup question, ask for independent **read-only** four-key values only if necessary before enabling a future layer feature (not needed for P11).
