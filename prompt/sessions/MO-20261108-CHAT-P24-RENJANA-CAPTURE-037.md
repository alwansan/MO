# MO-20261108-CHAT-P24-RENJANA-CAPTURE-037
**2026-11-08 Asia/Baghdad | origin=chat | branch=main | owner device POCO F5 Pro Android15**
**Starting HEAD:** 7cc3439b95821bee60b845c50fec35a5b3b93513

## استعادة المصادر
- قُرئت ملفات AGENTS.md، RESTORE_AR.md، README/MEMORY/STATUS/PLAN/DECISIONS/ARCHITECTURE، LEGAL_AND_SECURITY، تقرير P23 وP22، وجلستا P22/P23.
- GitHub main HEAD عند الاستعادة 7cc3439b...؛ أحدث ملف جلسة كان P23؛ تشغيل Actions 42429759178 SUCCESS وRelease renjana-owned-lab-run2. لا دليل build أحدث.
- تمت مراجعة upstream Renjana pinned SHA 14302a57cd66114c6979acf7ca56c97f845a6841: StubActivity.kt، VirtualClassLoader.kt، VirtualContext.kt، DeviceFingerprint.kt، PineHookManager.kt، CoreHooks.kt. لا نقل مصدر خارجي.

## دليل الهاتف الذي سُلّم الآن
- المستخدم نفذ فعليًا سكربت P23: Saved MO_RENJANA_OWNED_CAPTURE_20261108_035734.txt. الملف خاص، 42866 bytes / 336 lines / SHA256 7f18d75643390c8a33952bc0bec46ae4251e7293a8ea509a7c6965cf401191a0؛ لم يُرفع raw إلى repo.
- UID2000، Android API35. process IDs: host MO 14458، Renjana 14699، Guest Renjana:p1 23140. p1 جديد ولا يساوي p0=17591 من تقرير P23.
- Logcat p1: StubActivity_1 launch وguest Application started، native MOProbeRenderer.so loaded clns-9، DeviceFingerprint logged realme RMX3491، vendor libGLESv2_adreno.so loaded. ليس قياس API مباشرًا جديدًا في p1 ولا إثبات Vulkan spoof أو FPS.
- PSS/RSS/Swap PSS kB: host 40820/99424/34697؛ Renjana main 83127/175340/22181؛ p1 106509/231020/164. Combined Renjana main+guest PSS=189636kB (~185.19MiB) بلا baseline ولا FPS benchmark.
- 4 GPU Debug key readbacks كلها FAILED (settings service transaction 2147483646) => UNVERIFIED. Pine hiddenapi missing symbols twice؛ 15 VirtualCtx create-dir warnings؛ الأسباب غير مثبتة. لا Root، لا game access، لا settings writes.

## العمل الهندسي
1. Created tools/analyze_renjana_owned_capture.py — local-only summary and explicit UNKNOWN / PSS cautions; commit 5bbd9728de658d8803333163f1a21f5d1a7037e0.
2. Created tools/test_analyze_renjana_owned_capture.py — 3 offline fixture tests PASS with local Python, plus successfully analyzed owner's real file; commit 36dbfc14271c2383c0556ac3852bf2387b6cd748.
3. Updated tools/renjana_owned_lab_capture.sh to mark failed Settings reads UNVERIFIED; commit b5d0fb3be000d2c0bbeb344da824e8906d2ac614.
4. Added failure regression fixture to tools/test_renjana_owned_lab_capture.py; commit 8afcf44fc36577003cdbba1491625d8a067fe931 (needs CI/runtime verification; new shell version NOT run on device).
5. Created docs/DEVICE_FEEDBACK_RENJANA_P24_2026-11-08_AR.md; commit dff2957eeff84878fd283bd429b5732a196899da.
- No APK files modified. No new build triggered. No game, system, settings or other apps touched.

## قرار P24 والخطوة التالية
- Keep Settings.Global UNKNOWN; no GPU Layer activation or cleanup without reliable fresh readback and user consent. Source evidence proves Build Java spoof path but does not support native graphics identity spoof in Renjana. Do not misreport Pine READY as all hooks confirmed or startup log as Vulkan/GL API result.
- Focus on owned-app p1 proof for Java/NDK/EGL/Vulkan and VirtualContext save reliability. For overhead need Renjana idle baseline under comparable conditions. Native GPU spoof feasibility remains R&D for owned test only, with a real guest API before/after acceptance gate; Outlanders untouched.
- See detailed private-evidence synopsis in docs/DEVICE_FEEDBACK_RENJANA_P24_2026-11-08_AR.md. Later GitHub state/decision updates in this cycle.
