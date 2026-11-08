# P28 — مراجعة NEXTVM وقرار تغيير مسار MO بعد نتائج P27

التاريخ: 2026-11-08 Asia/Baghdad
Session: MO-20261108-CHAT-P28-NEXTVM-STRATEGY-041
Working branch: main
Start HEAD: 24892a396c10c12227427fcc20e00790a418e363
Scope: source research, no APK build or phone changes in this session.

## 1. Actual P27 owner-device observations

Eight owner-supplied screenshots: 1006531226.jpg through 1006531233.jpg. Private raw screenshots were NOT committed (some display Android ID, serial number, and instance UUID). Their SHA256 values, in filename order:
707baca31c225288acab5a3084ac9a46c4f084a440381f78ae1903e1109b66ec
2bc6ff6408a7fb738e809eae9e82acd816a3cb1e8889d083754a8a6fa4b1274e
9572d4758b4f45b96a12db96ffaf0cb9cc5dc6b7327006da297c0a13fdbe82f8
26494caaa5fa30588a57f575bee580c7b73da25691a366ddc4520b75731b1524
5902b87c495ce054a35bab3eeaa1d06419332b5bf38df44a4718bd5664aa28a8
c8521c0961a272ea4ddf0184bb6e88123bd17bffaf4cae1b91023298f918dc67
5e66ac1e763f56e4b22f4a56de2b36e9bd721c6426f516f350b5754c439a1478
71ba21ba7319d854476c9ac2bab08ce3ae11822fe324ed2efd11748a4d55375d

- Renjana UI displays MO Layer Test v0.8.0 Instance with a OnePlus NE2215 Android13 identity and profile customization. Config screenshot displays GMS, signature spoof, anti-detection, fingerprint spoof ON; this is NOT evidence any account was entered. Do not copy identifiers or paths into public repo. GMS is unnecessary for owned lab and can be disabled by owner in a future comparison (no change from MO).
- MO test UI says PID 23440; no new Rish process-name mapping for this PID. P26 earlier independently mapped PID17047 to Renjana:p0, which must NOT be silently reused for PID23440. A Renjana Instance UI alone is not a runtime process proof.
- Build.MODEL NE2215, GL_VENDOR Qualcomm, Java GL_RENDERER, NDK GL_RENDERER, eglGetProcAddress renderer and Native Vulkan deviceName all Adreno (TM) 730. Java and Native interception NO. FPS tier LOCKED is a UI simulation, not frame rate evidence.
- P27 symbol report in owned PID: linked glGetString, eglGetProcAddress glGetString, and RTLD_DEFAULT all resolve to libGLESv2.so; linked vs EGL equality YES, linked vs RTLD_DEFAULT equality YES. Linked vkGetPhysicalDeviceProperties and RTLD_DEFAULT both resolve libvulkan.so; pointer equality YES. Public loader symbol module naming is NOT proof of final Qualcomm driver execution or impossibility of hooking it.
- Repeated storage check for PID23440: Activity/Application File.exists=true, isDirectory/canWrite=true, Java direct file roundTrip/cleanup PASS, Activity/Application Context streams and cleanup PASS, native libc stat YES and native I/O/cleanup PASS, canonical filesDir same=true. P26 storage fix remains operationally supported. This is NOT proof that native GPU values are spoofed.

## 2. NEXTVM live source review (not first research, see P21)

Repo: https://github.com/TanvirHossain2/NEXTVM
Default branch HEAD at review: f581a6642596db396a6fe606addd735979403b6b, Apache-2.0, 4 commits in default main; its latest default commit is 2026-03-07. README calls project alpha and says unit coverage about 0%.
- Source core/hook/src/main/cpp/native-hook.cpp implements ELF PLT/GOT-patching and dl_iterate_phdr; this is meaningful native code. HOWEVER g_hook_entries covers open, openat, access, stat, lstat, readlink, fopen, __system_property_get. It does not cover glGetString, eglGetProcAddress, vkGetPhysicalDeviceProperties or vkGetInstanceProcAddr.
- IdentitySpoofingEngine.kt mutates Java Build fields and other Android device identifiers, not actual GLES/Vulkan driver APIs. HookEngine.kt implements reflection/proxy and describes Dobby/bhook/LSPlant as future paths.
- nativeInit may return true when PLT hooks fail and record fallback; "initialized" alone would not prove a hooked GPU API or even a hooked PLT entry.
- No GitHub Releases for NEXTVM. Recent workflow runs include action_required/failure; an older success belongs to a PR head, not proof default main builds. Open issue #5 describes failed guest launches (ActivityTaskManager routing, ABI matching, launcher resolution), and its acceptance boxes were not checked at review. https://github.com/TanvirHossain2/NEXTVM/issues/5
- Thus NEXTVM is a research reference, NOT a proven replacement for existing Renjana P26 on POCO. Do not switch without a separate owned-app Android15 ARM64 Guest+storage+GLES/Vulkan regression result.

## 3. Comparison with alternatives and earlier research

Renjana P26: actual Rish-mapped owned Guest launched and read/write/delete tests PASS, but Native GPU API identity still real Adreno730. It is a known-good laboratory, NOT proven heavy-game container.
NEXTVM: Java identity and native libc hooking candidate, no gl/vk hook table, open guest-launch issues, no release or phone confirmation.
BlackBoxReborn: official fork claims modern Android compatibility, but owner already tried it and failed to launch Outlanders (recorded P21); do not repeat without new relevant evidence. https://github.com/fdavids77/BlackBoxReborn
ByteHook https://github.com/bytedance/bhook: MIT licensed Android PLT hook library, supports arm64 and Android15 and hooks newly loaded shared libraries. Candidate for *inside owned process* experiment. It cannot independently inject into a non-debuggable third-party process.
ShadowHook https://github.com/bytedance/android-inline-hook: MIT inline hook alternative if PLT is proven insufficient, with greater crash/stability risk.
Work Profile/Shelter: isolates data but does not change GL/Vulkan device identity. Full Android VM / AVF guest does not solve practical no-root device constraints or light overhead.
Official Android GLES debug layers are restricted to debuggable or opted-in apps (or root/userdebug). Android Vulkan policy prohibits arbitrary external code inside non-debuggable production processes. Shizuku UID2000 is not Root.
Official 120Hz display is not 120 actual frames per second; Android15 FPS behavior depends on FrameRate API/system and game decisions, thermal and battery factors.
Game graphics gating remains unknown: prior P20 showed same 128-byte change pattern in both graphics-changing and control RemoteConfig snapshots, not a unique fingerprint of accepted quality. No basis to say GPU text alone unlocks Outlanders.

## 4. Decision: Native-first pivot instead of container replacement

1. Preserve original Renjana and working P26 modified owned-app release as *frozen baseline*. Do not widen File.exists hook bypass to other packages or change GMS/anti-detection without consent. Stop building more full container versions simply to test device model spoof.
2. P29: create reversible, explicitly user-activated Native GLES hook experiment inside the **MO-owned disposable APK** first. Pin an audited MIT PLT hook dependency (ByteHook candidate); capture original glGetString(GL_RENDERER), intercepted return inside actual Native/Java/EGL calls, hook count/registration result, disable/unhook restoration, crash/CPU overhead. A UI text simulation is NOT success. Modify only dummy renderer string for owned app, never actual GPU capabilities/extensions or FPS.
3. P30: separate Vulkan dispatch experiment with vkGetPhysicalDeviceProperties plus lookup paths (vkGetInstanceProcAddr as appropriate) inside owned app; verify a function-call interception, not UI-only spoof. No fake physical features, memory sizes, extensions, or unsafe dispatch handles.
4. P31: only if own-process intercept works, investigate whether **Renjana P26** can load controlled native instrumentation in owned Guest before MO's JNI library, without MO itself explicitly doing the helper load. Verify on-device :pN PID via read-only ps, actual gl/vk before/after, rollback, memory PSS/RSS and crashes. This distinguishes in-app hook from genuinely host-container-delivered hook.
5. Gate: if Guest library delivery is unavailable on non-root Android15, if native dispatch cannot be intercepted reliably, or if overhead/compatibility become impractical, report constraint and stop speculative rebrands. Do not test or modify game binaries, files, signature, accounts or game installation; no guarantee of unlocked settings even on successful owned app proof.

No root, Bootloader unlock, Zygisk, KernelSU, global GPU Debug/ANGLE setting, Google/game account, iUnlockerGL source reuse. No new APK or Actions run needed in this P28 research/documentation-only session.
