# MO P15 — BlackBoxReborn vs NewBlackbox and the user's MHO APKS
2026-11-08 Asia/Baghdad | Session MO-20261108-CHAT-P15-BLACKBOX-APKS-027

## Main finding: installed game can appear in BlackBox but fail to launch
The publicly available virtual installation path from BOTH ALEX5402/NewBlackbox and fdavids77/BlackBoxReborn uses a single baseCodePath. Android normally treats the base and all installed configuration splits together. If a large game stores its ARM64 Unreal engine in an ABI-specific split, the reviewed BlackBox code could miss that native library and fail to launch the hosted package.

Source-traced evidence from GitHub (read directly, not guessed):
- Bcore/src/main/java/top/niunaijun/blackbox/BlackBoxCore.java: installPackageAsUser(packageName,userId) obtains getPackageInfo(...).applicationInfo.sourceDir only; not splitSourceDirs.
- Bcore/src/main/java/top/niunaijun/blackbox/core/system/pm/BPackage.java holds baseCodePath and no splitNames or splitSourceDirs fields found.
- Bcore/src/main/java/top/niunaijun/blackbox/core/system/pm/PackageManagerCompat.java adds only ps.pkg.baseCodePath to AssetManager.
- Bcore/src/main/java/top/niunaijun/blackbox/core/system/pm/installer/CopyExecutor.java calls NativeUtils.copyNativeLib(new File(ps.pkg.baseCodePath), ...).
- Bcore/src/main/java/top/niunaijun/blackbox/utils/AbiUtils.java tests ZipFile(apkFile) for arm64-v8a libraries from one APK; isEmptyAib returns true even when no library found. NativeUtils.java only extracts lib/<ABI>/*.so from that one APK.
- Bcore/src/main/java/top/niunaijun/blackbox/core/system/pm/BPackageManagerService.java installs one apkFile with PackageManager.getPackageArchiveInfo; parserApkCompat still reflects Class.forName("android.content.pm.PackageParser$Package"), an additional potential compatibility issue on Android 15.
- Android official split packages must be installed together: https://developer.android.com/guide/app-bundle/app-bundle-format and https://developer.android.com/reference/android/content/pm/PackageInstaller .

The above is a structural **hypothesis** for the user's launch failure, not a proven stack trace. Other possibilities include unsupported hidden APIs, GMS, Play Integrity, native UE loading, graphics driver, launcher activity or app-specific protections. BlackBoxReborn explicitly does NOT certify Outlanders compatibility.

## Actual spoofing in BlackBox repositories
- The two repos' Bcore/src/main/cpp/Utils/VirtualSpoof.cpp files have IDENTICAL git blob SHA 752384da6da8f02c2abe63d852c12b351fdc8f5b and are included in Android.mk.
- They hook libc __system_property_get using Dobby and hardcode ro.product.model = Pixel 6, brand = google, ro.hardware.egl = adreno, etc.
- This module contains NO glGetString(GL_RENDERER), eglGetProcAddress or vkGetPhysicalDeviceProperties hook. A property ro.hardware.egl = adreno is a driver selector hint, not a GL_RENDERER GPU MODEL. Thus this inspected code does not prove an Adreno 730 -> Adreno 840 spoof.
- This does not categorically exclude all other hooks elsewhere in the repositories. Success must be verified by a *hosted app's own API readbacks*, not a fake UI.
- README device spoof claims and Android 15 compatibility do NOT establish Outlanders native, GMS or multiplayer support.
- BlackBoxReborn README describes license GPL-3.0, while its actual LICENSE file contains Apache License 2.0. Resolve provenance and dependency rights before importing/releasing source.

## Google services and resource cost
- Bcore GmsCore.java attempts to host installed Google packages; it does NOT automatically gain host Google login sessions or hardware-backed Play Integrity. BlackBoxReborn itself documents underlying SO_PEERCRED kernel UID mismatch and limits of device attestation.
- A no-root application virtualization host is lighter than a full Android guest, but the hosted copy requires its own data, resources and native libraries. A normal Intent launcher can't change identity inside a separate original game process.

## User-uploaded APKS: what was and wasn't inspected
- Conversation Files metadata confirms the exact owner upload MHOutlanders_1.0.9.0.3308842.apks, size 428,838,769 bytes, mounted under /mnt/data/MHOutlanders_1.0.9.0.3308842.apks.
- Repeated Python and container sandbox read attempts failed with ClientError/SystemError before Python could open the file. Files.read returned no extracted text for this binary archive. **We did not unpack or inspect its nested APKs in this turn**. No guessed split filenames, graphics config, libUnreal.so or hash values should be passed off as real findings.
- Separate owner file com.tencent.mhadv.zip WAS audited in prior P14: Unreal MHMobile logs, binary hotfix pak/utoc/ucas assets, GLES3.1 shader cache; **no plain .ini in that partial Android/data archive**. That does not establish there are no settings inside the new APKs, nor prove a working override.

## Concrete read-only inspection tool delivered in MO
- tools/inspect_apks_readonly.py: Python3 standard-library tool for Termux. It lists the actual nested APK filenames and sizes, then streams each APK to a temporary file and enumerates ARM64 native lib paths, libUnreal/libUE4 indicators and config filename hints. No install/execute/patch/sign, no remote upload, no asset content extraction or credential access. Output JSON, binary byte scans not performed.
- Limits: 700 MiB per nested APK, total 1.2 GiB, 10,000 outer members. Oversize parts are reported as skipped.
- tools/tests/test_inspect_apks_readonly.py: synthetic split fixtures only (fake ABI library in config split, one base and a corrupt archive). .github/workflows/mo-apks-metadata-selftest.yml is isolated, runs Python 3.12. No user game binaries are uploaded to GitHub.
- CI run 42418222211 first failed due a coding bug (ZipInfo.split vs ZipInfo.filename.split), then corrected in commit 38a9d354d049f295e5cc558887541747afb69af3.
- CI run https://github.com/alwansan/MO/actions/runs/42418273221 **completed success**. All 3 tests and --help command passed. Actual user APKS scan remains unverified.

## Termux instructions (no PC, no root)
Source review: https://github.com/alwansan/MO/blob/main/tools/inspect_apks_readonly.py
1. Install standard tools: pkg install python curl ; run termux-setup-storage if first time.
2. Save our source into the Termux home directory with:
   curl -fL https://raw.githubusercontent.com/alwansan/MO/main/tools/inspect_apks_readonly.py -o "$HOME/inspect_apks_readonly.py"
3. If your APKS is in Android Downloads, run:
   python "$HOME/inspect_apks_readonly.py" "/storage/emulated/0/Download/MHOutlanders_1.0.9.0.3308842.apks" > "/storage/emulated/0/Download/MO_APKS_REPORT.json"
4. Upload only MO_APKS_REPORT.json. Script only outputs metadata and never writes inside game or .apks. --outer-only is a low-space alternative; a full scan needs enough temporary storage for the largest nested APK.

## Proposed decision after actual metadata + launch-failure logs
1. If split ABI libraries are absent from base.apk but present in separate config split, **fix/validate split-awareness in an independent authorized test fixture first**, not a live game. Use the existing official installation outside the host until its container is genuinely split-aware. Never simply merge and re-sign a protected game's APK; signatures and integrity can break.
2. If splits are fully present, investigate native loader error, Android PackageParser reflection and GMS from a narrow logcat / BlackBox own crash report. Avoid moving personal Google account into untrusted virtualization.
3. For Android/data config: generic Unreal DeviceProfiles can depend on GPU family/model, but prior ZIP has no obvious GameUserSettings.ini or AndroidDeviceProfiles.ini. Changing a shader cache or dropping a guessed Engine.ini cannot be considered a working unlock; may cause corruption/revalidation. Only create a reversible user-config edit if an actual plaintext file and respected load path are verified on this precise game build.
4. MO P13 existing ANGLE **read-only** device scan is still lowest-risk system path. No all-app ANGLE enable. User's core target remains real GPU identity read inside a different cooperating process; not P10 fake menus or P11 own frame callbacks.
5. The game's EULA warns about unauthorized client changes, virtual environments and account restrictions. Do not test a live account or bypass anti-cheat/attestation safeguards as part of diagnostics: https://monsterhunteroutlanders.com/useragreement/en/index.html .

## Exact links
https://github.com/fdavids77/BlackBoxReborn
https://github.com/ALEX5402/NewBlackbox
https://developer.android.com/guide/app-bundle/app-bundle-format
https://developer.android.com/reference/android/content/pm/PackageInstaller
https://dev.epicgames.com/documentation/unreal-engine/customizing-device-profiles-and-scalability-in-unreal-engine-projects-for-android
