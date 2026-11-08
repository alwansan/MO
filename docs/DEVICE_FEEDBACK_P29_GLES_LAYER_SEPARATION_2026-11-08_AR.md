# P29 device feedback — GLES legacy-layer confound and verified Guest PID (2026-11-08)

**Session:** MO-20261108-CHAT-P29-FEEDBACK-LAYER-SEPARATION-043  
**Origin:** chat | **branch:** main  
**Baseline source HEAD at start:** 50b70d841e6319c837721461925c1426fda3112d

## Evidence source and privacy

Owner supplied two screenshots from MO Layer Test containing current device/readout/status, and two read-only Rish \`id\`/\`ps\` results. Raw screenshots remain in the private conversation, **not published in GitHub**. Audit fingerprints of supplied screenshots:
- 1006531235.jpg — 258292 bytes, SHA256 b543552eced2740214b99f5b21c7eb842b11920b825adc6f543b7af10f722b8d
- 1006531236.jpg — 246466 bytes, SHA256 94a263c5632695a81ca0026eefc6f902a51ab1595c455cb93b9d4f26cbd78f0c

## Independent process mapping

Read-only Rish output (repeated and consistent):
\`\`\`
uid=2000(shell) gid=2000(shell) context=u:r:shell:s0
414  com.alwansan.mo.glesprobe
3201 com.fesu.renjana
3585 com.fesu.renjana:p0
\`\`\`

- Host screenshot displays PID414 and real Build.MODEL=23013PC75G.
- Renjana screenshot displays PID3585 and spoofed Build.MODEL=ASUS_AI2302. Unlike P27, its PID now directly matches **com.fesu.renjana:p0**, proving this sample came from the container guest, not from the separately installed MO APK.
- This validates that Shizuku is ADB shell UID2000, **not Root UID0**. No new Shizuku privilege, process injection or system setting change is shown.

## What the screenshots prove

| Parameter | Direct host-owned APK, PID414 | Renjana Guest, PID3585 |
| --- | --- | --- |
| Android Build.MODEL | 23013PC75G | ASUS_AI2302 |
| GL_VENDOR | Qualcomm | Qualcomm |
| Java GL_RENDERER | Adreno (TM) 730 | Adreno (TM) 730 |
| Native NDK GL_RENDERER | **MO-GLES-LAYER-PROOF (SIMULATED)** | Adreno (TM) 730 |
| glGetString via eglGetProcAddress | **MO-GLES-LAYER-PROOF (SIMULATED)** | Adreno (TM) 730 |
| Vulkan deviceName | Adreno (TM) 730 | Adreno (TM) 730 |
| App's legacy Native layer intercepted | YES | NO |
| App's legacy Java layer intercepted | NO | NO |
| Mock FPS tier | UNLOCKED (SIMULATION) | LOCKED (SIMULATION) |
| Activity/Application File exists + I/O + cleanup | PASS | PASS |
| Native libc stat + I/O + cleanup | PASS | PASS |
| P29 opt-in test output | **NOT RUN on this process** | **NOT RUN on this process** |

**Critical attribution:** The literal \`MO-GLES-LAYER-PROOF (SIMULATED)\` corresponds exactly to \`probe/src/main/cpp/mo_gles_layer.cpp\` constant \`kRenderer\`, whose \`AndroidGLESLayer_GetProcAddress\` replaces the \`glGetString\` function pointer. The UI's \`MockGraphicsTier.isProofLayerRenderer()\` checks this legacy constant; it does **not** check P29 ByteHook. Host screenshot therefore demonstrates an effect compatible with the **existing official GLES proof layer**, while Renjana Guest does not show that layer's effect.

The P29 ByteHook experiment in \`probe/src/main/cpp/mo_p29_gles_hook.cpp\` uses **a different string** \`MO_P29_OWNED_GLES_HOOK_ONLY\`, registers only upon the P29 button, tracks callback counts/hits and explicit unhook, and prints its own A/B/A report to the separate P29 result view. Since both screenshots say \`NOT RUN on this process\`, **there is no P29 functional interception result yet**, even though its APK build/release succeeded.

\`libGLESv2.so\` (linked/EGL/dlsym), \`libvulkan.so\` (linked/dlsym) and equality YES reproduce earlier P27 loader-symbol evidence. This does not identify Qualcomm's ultimate driver implementation. The report does not prove which Android debug-layer key or mechanism was active on the host at the time; **do not change global settings as an unverified cleanup step**.

## Decision and next safe test

- Keep Renjana P26 unchanged as the controlled guest with baseline Adreno730 and functioning I/O. Because host PID414 is visibly affected by an earlier GLES proof layer, **run P29 button in Guest PID3585 first** to obtain the cleaner ByteHook experiment without touching system settings. If PID changes, use the same read-only Rish \`ps\` to map it.
- P29 click should produce: \`Baseline linked NDK\`, \`Baseline EGL proc direct\`, \`Hook stub registered\`, \`Hook callback OK\`, \`Hook callbacks total\`, \`Last hook status code\`, \`Linked NDK while active\`, \`EGL direct pointer while active\`, \`Owned hook hits\`, \`Unhook status\`, \`Linked NDK after unhook\`, \`P29 result\`. Do not infer success from a legacy \`Layer intercepted (Native): YES\`.
- An optional second P29 click outside Renjana is informative **under legacy GLES layering**, but baseline there can be \`MO-GLES-LAYER-PROOF (SIMULATED)\`, not Adreno730. Record this as a separate layered environment rather than interpreting it as bare hardware. Do not disable or enable global layers merely to clean up an experiment.
- Do not rebuild APK until test evidence identifies a real bug. No Outlanders/game/account/modification/root/bootloader/shader driver injection/global GPU Debug or ANGLE operations; no P29 benchmark of real FPS or Vulkan feature replacement.

## Existing artifact still adequate
- Owned MO Layer Test v0.9.0 from Actions https://github.com/alwansan/MO/actions/runs/42437618103, release https://github.com/alwansan/MO/releases/tag/mo-v0.11.0-alpha.11-run78 . Compile succeeded, phone P29 hook acceptance **PENDING**, owner is already running the correct test interface.
