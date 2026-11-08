# MO-20261108-CHAT-P27-P26-DEVICE-PASS-NATIVE-DISPATCH-040

**2026-11-08 Asia/Baghdad | origin=chat | branch=main**  
**HEAD at start:** 480d721229c4a88bc270cb3645c564ca4f5486c8

## Live restore
Read current main HEAD, P26 Actions run 42434382187 SUCCESS and release renjana-owned-storage-p26-run1; no newer session than P26-039.

## P26 new actual device evidence
Owner provided two screenshots. SHA256 for 1006531223.jpg: db502548697c684ed7075597853ac7dfe52338eee49989ebe43eefa16ce93ba7; 1006531224.jpg: 985510b4e53ff12ef8e89d5cd94327110ce08638bd15dfd717a6e3258b855190. Raw screenshots remain in chat, not public GitHub.
Rish process list: externally installed MO Layer Test PID4548, Renjana host PID16543 and **Renjana virtual guest com.fesu.renjana:p0 PID17047**; screenshot in-app PID=17047, confirming correct process (not host fallback).
After P26 Java File.exists true for Activity and Application filesDir, Java direct File, Context open/read/cleanup and native libc stat/IO/cleanup all PASS. This contrasts with P25 false File.exists, Context FAIL(FileNotFoundException), NOT_FOUND_OR_FAILED cleanup while Java direct/native IO PASS. This is **real on-device recovery for the owned app** and strongly supports P26 File.exists cloak-collision diagnosis. A/B used different fake models and instances (P25 XT2251-1 and P26 RMX3740), so cannot claim all confounders eliminated, a universal storage fix, or other-guest compatibility.
Build.MODEL RMX3740, but Java GLES, NDK GLES, EGL function pointer and native Vulkan all report **Adreno (TM) 730**. EGL_ANDROID_GLES_layers YES, Java interception NO, native interception NO, mock FPS tier LOCKED. No GPU spoof, FPS improvement or game evidence.
Full documented observations: docs/DEVICE_FEEDBACK_RENJANA_P26_2026-11-08_AR.md. No new memory benchmark.

## P27 work completed in MO source
Researched official Android rootless GLES/Vulkan debug layer requirements and Bionic linker namespaces; externally loaded layers require debuggable or opt-in targets, not universal process spoof without root.
- Added probe/src/main/cpp/mo_dispatch_evidence.cpp, commit 96d487b266f405158974036265c4081be6197971. Uses NDK link symbols, EGL proc lookup, and public dladdr/dlsym(RTLD_DEFAULT) to report basename-only GLES/Vulkan module origins plus symbolic pointer equality (no raw addresses). No library injection, dlopen, root, settings writes or vendor spoof.
- CMake integration with libdl commit 653ec11e7822ba4fdab2bc6d379861bc2fd1e473.
- Owned JNI bridge commit c266f4cc4c82a969f66c7298b069d77c4ebcba19; UI in same process as real Java/NDK/EGL/Vulkan readings commit 4f63078fee784adc6b6e2f43804bea47cdc38a9a.
- Bumped disposable MO Layer Test from v0.7.0 to v0.8.0 (commit 80a08dd7c3af7288a1d8edb3c35a1bd0812a0139) to publish a distinct test artifact, without requiring main MO app update.
- Created docs/P27_NATIVE_GLES_VULKAN_DISPATCH_PROBE_AR.md, commit 77041435404743b307ec5c90515d82c3ca1c4890, including acceptance criteria and official sources.
- GitHub Actions auto run #71 / 42435412976 for probe source at 80a08dd7c3af7288a1d8edb3c35a1bd0812a0139 was running at first check; final success and Release need separate verification.

## Scope limits and next
Keep verified Renjana P26 unchanged. P27 native symbol module name does **not** prove actual driver ICD implementation or ability to globally hook. No on-device P27 result. Owner may test only disposable MO Layer Test v0.8 with same-PID screenshot after a verified release; no Outlanders files, signature or accounts, no Root/Bootloader/KernelSU/Zygisk, no global GLES/ANGLE/Settings changes. Original and modified Renjana releases stay separate.