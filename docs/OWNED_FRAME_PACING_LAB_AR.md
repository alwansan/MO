# P11 — MO Owned Frame Pacing Lab (no-root, no game instrumentation)
Date 2026-11-07 Asia/Baghdad | Session `MO-20261107-CHAT-P11-FRAME-PACING-019`

## Why this is separate from the P10 fake 120 FPS menu
P10 demonstrated that a *fictional* game menu can show UNLOCKED when its local Java identity strings were changed. It did not count frames. P11 deliberately measures two **different real callbacks** from an actual `GLSurfaceView` running **inside MO's own disposable test package** `com.alwansan.mo.glesprobe`:
- `Choreographer.FrameCallback.doFrame` callbacks per second, sampled on UI thread (not guaranteed same as physical refresh, especially when throttled).
- `GLSurfaceView.Renderer.onDrawFrame` calls per second, sampled from the GL renderer thread. This is the number of GLES draw callbacks, **NOT SurfaceFlinger-presented FPS**, GPU completion or actual photons shown.
It also prints the physical display refresh **setting** separately. None of these counters measure or change Monster Hunter Outlanders, PUBG, a native game's FPS, refresh throttling, GPU identity or physical features.

## Source
- `probe/src/main/java/com/alwansan/mo/glesprobe/FramePacingWindow.java`: pure Java, monotonic 1+ second sample window with separate callback/draw counts and reset handling.
- `probe/src/test/java/com/alwansan/mo/glesprobe/FramePacingWindowTest.java`: test independent rates, rollover, reset, invalid times, not assuming 120Hz.
- `probe/src/main/java/com/alwansan/mo/glesprobe/FramePacingLabActivity.java`: self-owned GLES2 `GLSurfaceView` showing changing colors. `RENDERMODE_WHEN_DIRTY`, requested once per each/each second/each fourth `Choreographer` callback. Sample rates ~once per second using `SystemClock.elapsedRealtimeNanos` and per-thread atomic counters; GL renderer started/stopped with Activity lifecycle.
- `probe/src/main/java/com/alwansan/mo/glesprobe/GameIdentityLabActivity.java`: button to enter frame pacing lab and Android15 status/navigation insets to fix header overlap seen in P10 phone screenshots.
- `probe/src/main/AndroidManifest.xml`: frame lab registered as `android:exported=false` (only internal code can launch).
- `probe/build.gradle`: owned test v0.5.0, versionCode5. **Main MO Graphics Probe unchanged v0.9.0-alpha.9**.

## Experimental protocol in disposable app ONLY
1. User previously **reported** restoring GPU Debug and removing an app. The user did not specify which package was removed, and no independently verified four-key readback was supplied after removal. **Do not infer exact current system settings.** Do not activate any new layer; P11 requires no Shizuku.
2. After successful CI and raw APK Release verification, **install only MO Layer Test v0.5.0** if the user wants this measurement; do not reinstall the main MO merely for P11. If the installed test APK has a different debug certificate, user may need to uninstall **only disposable MO Layer Test**, provided the GPU Debug layer settings are already clean. Never uninstall Shizuku or original games.
3. In disposable probe, open **مختبر هوية الجهاز** then **قياس وتيرة رسم GLES المملوك لـMO**. The display shows screen-Hz setting and two measured callbacks/sec. Try stride 1, 2, 4; after at least a second each, record callbackRate, drawRate and reported display Hz.
4. **Expected relationship only, not guaranteed numbers:** Choreographer count depends on foreground scheduling; render callbacks may be lower, especially with stride2/stride4, thermal limits, background pause, compositor/driver scheduling or other load. An apparent draw callback above a target fps is not proof of frames displayed.
5. Exit activity: rendering thread and recurring Choreographer callback are paused/removed. No settings or game files changed.
6. A game can cap its own FPS even when display Hz is120 and an unrelated owned test draws faster. Do not assert any unlock in the stock `com.tencent.mhadv` title.

## Tests, gates and warnings
- CI must pass `:app:testDebugUnitTest`, `:probe:testDebugUnitTest` and both `assembleDebug`, including GLES JNI ARM64, before raw APK claim. Latest successful run/Release recorded in `prompt/sessions/MO-20261107-CHAT-P11-FRAME-PACING-019.md`.
- On-device P11 correctness is **pending** until user runs it.
- The user says their GPU Debug settings were restored and an app was deleted. This is an **owner statement**, not a fresh readback. Avoid pretending persistence is verified; do not block ordinary read-only owned lab work, but don't re-enable global GPU layers.
- No third-party source imported, no proprietary ZIP execution, no root, bootloader, clone, VM or game re-signing.

## Primary Android references
- GLSurfaceView official: https://developer.android.com/reference/android/opengl/GLSurfaceView (when-dirty mode, activity onPause/onResume)
- Choreographer callback official: https://developer.android.com/reference/android/view/Choreographer.FrameCallback
- Android OpenGL starter: https://developer.android.com/develop/ui/views/graphics/opengl/environment

## نتيجة CI والنشر — مؤكدة
- GitHub Actions [42325865139](https://github.com/alwansan/MO/actions/runs/42325865139) عند source commit `9abd11a3cbc3062836ceef6f2facd968286faa6f`: **completed/success**. تضمن الاختبارات `:app:testDebugUnitTest` و`:probe:testDebugUnitTest` وبناء `:app:assembleDebug` و`:probe:assembleDebug` وفحص صحة حزمة APK ورفع Release.
- الإصدار التجريبي المباشر: [mo-v0.9.0-alpha.9-run32](https://github.com/alwansan/MO/releases/tag/mo-v0.9.0-alpha.9-run32)، draft=false، وحالته prerelease.
- [MO-Layer-Test-v0.5.0.apk](https://github.com/alwansan/MO/releases/download/mo-v0.9.0-alpha.9-run32/MO-Layer-Test-v0.5.0.apk): 182484 bytes، SHA-256 `5aca59d8dc61ec0a1e3763963de96974f02c62f60d15d7041233d774afae37d8`.
- Main `MO-Graphics-Probe-v0.9.0-alpha.9.apk` also rebuilt and shipped automatically (same source functionality, different debug certificate), 1135214 bytes، SHA-256 `5ab29995235bdd6c5c896192af3aaac4477b0b6e40c9e20373ef29c0928ae3e2`. لا حاجة لتثبيته لتجربة P11.
- **اختبار P11 على الهاتف لم يصل بعد.** CI/build success does not verify renderer runtime, device safety state or display presentation.
