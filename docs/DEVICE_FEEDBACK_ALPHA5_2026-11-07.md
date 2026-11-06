# MO v0.5 on-device — partial native GLES interception
Session: MO-20261107-CHAT-P5-PARTIAL-NATIVE-012 | origin chat | 2026-11-07 Asia/Baghdad
User supplied 6 screenshots and complete MO v0.5.0-alpha.5 report. No direct device access.

## Concrete observations
- Shizuku UserService API13 / shell UID2000, GRANTED. Owner's original POCO F5 Pro Android15/SM8475/Adreno730 still intact. Monster Hunter Outlanders global com.tencent.mhadv v1.0.9.0.3308842 unchanged.
- Exact four Global developer GPU debug settings:
  - enable_gpu_debug_layers=1
  - gpu_debug_app=com.alwansan.mo.glesprobe
  - gpu_debug_layers_gles=libMOGlesProofLayer.so
  - gpu_debug_layer_app=com.alwansan.mo.glesprobe
- MO Layer Test separate debuggable/opt-in APK: Android Build.MODEL=23013PC75G; GL_VENDOR Qualcomm; `GLES20.glGetString(GL_RENDERER)` via Android Java still `Adreno (TM) 730`.
- JNI own NDK `glGetString(GL_RENDERER)` returns `MO-GLES-LAYER-PROOF (SIMULATED)`. Shown PID 6957; Native YES, Java NO, aggregate YES; synthetic "Mock 120 FPS: UNLOCKED" (simulation only).
- The self-check dialogs report the layer library can be dlopen-ed (and on one image already loadable). **Manual System.loadLibrary is not proof of automatic EGL loader activation**. Filtered Shizuku logcat did not show matching layer logs, inconclusive.
- Missing evidence: user has NOT yet reported successful restoration/rollback of four settings; current values still 1 and test app, which persist across reboots. **Urgent first action is restore using installed MO v0.5 and verify baseline**. Do not uninstall or update MO before success, else its private backup may be erased.

## Interpretation
P4 result must be updated from FAIL to **PARTIAL PASS**: native NDK path observes simulated renderer; Java Android GLES20 path does not. We have a real behavioral difference in own disposable test app; not proof all native code paths intercepted nor a global GPU identity change.
The synthetic FPS state is coded as Java OR Native, so it unlocks on NDK alone; this is not measurement of frame rate or actual game graphics unlock.
Official GLES debug layers require a debuggable or explicitly opted-in app, per Android NDK docs (https://developer.android.com/ndk/guides/rootless-debug-gles). Shizuku UID2000 does not allow bypass of third-party app debuggability or signature boundaries.
AOSP Java GLES20.glGetString is a native JNI wrapper around glGetString, but the observed divergence suggests separate linkage/dispatch paths; no implementation-level root cause verified yet. Do not assert exact cause as fact.
- https://android.googlesource.com/platform/frameworks/base/+/a49fb9831e2133f7fb3452bcc62c0e6802fbd36d/core/jni/android_opengl_GLES20.cpp
- https://android.googlesource.com/platform/frameworks/native/+/refs/heads/master/opengl/libs/EGL/GLES_layers.md

## Next engineering gates
1. Restore GPU Debug settings using current MO and read back four keys; maintain prior snapshot. No updates until restore.
2. In a separate owned test APK, compare Java GLES20, NDK direct glGetString and EGL eglGetProcAddress against same EGL context, record PID and EGL_ANDROID_GLES_layers feature.
3. Do not conflate library ELF loadability with successful Android layer loader initialization.
4. Prove test after restoring baseline/enable/restore across different PIDs, then determine where layer works; no changes to original Outlanders.
