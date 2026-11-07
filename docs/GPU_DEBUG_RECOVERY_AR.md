# GPU Debug recovery after losing the original MO snapshot (v0.8)
2026-11-07 / MO-20261107-CHAT-P8-RECOVERY-013

## Why this exists
GitHub CI debug APK signer changes per run. Uninstalling MO to install a new APK can delete its private SharedPreferences, including exact original GPU Debug settings. If a new version activates while the **old settings are still active**, v0.7 could save the active MO test state as "original" and restore it to enabled=1.

## Verify BEFORE installing newer MO
1. Open your **currently installed MO v0.7** and select **استعادة إعدادات GPU Debug القديمة** (if a backup exists).
2. Tap **تشخيص GLES (قراءة فقط)** and check FOUR keys. The previous recorded state before MO's original GLES test included `enable_gpu_debug_layers=0`, `gpu_debug_app=null` (settings get global; this does not prove key presence), but the initial values of the two remaining keys were never recorded in the owner-shared report.
3. If values are now 0 and owned package/library names have disappeared, stop. No further cleanup is required.
4. If all FOUR keys are still exactly MO test values after restoration or there is no valid backup, don't activate test again.
5. V0.8 offers **تنظيف طبقة MO العالقة (عند فقدان الأصل)**. It is a one-time cleanup with separate confirmation, not exact prior-state restoration.

## v0.8 guarded cleanup
The Shizuku UID2000 service:
- captures all four current key values including whether each exists; it must save the snapshot in MO private storage before changing anything;
- refuses to write if ANY value doesn't exactly match:
  `enable_gpu_debug_layers=1`,
  `gpu_debug_app=com.alwansan.mo.glesprobe`,
  `gpu_debug_layers_gles=libMOGlesProofLayer.so`,
  `gpu_debug_layer_app=com.alwansan.mo.glesprobe`.
- explicitly sets `enable_gpu_debug_layers=0`; deletes the three keys whose current values are the MO-specific marker strings; verifies 0 and absence;
- if any step fails, attempts restoring the exact *pre-cleanup MO test state*. The pre-cleanup snapshot is retained in app private storage and the error must be reported.
- refuses cleaning another application's settings, arbitrary package names, root commands, non-Shizuku UID, or wildcard configuration.

**Not the same as restoring exact baseline**: if there were non-MO preexisting values of those three keys before the very first experiment, they cannot be reconstructed after the old snapshot is lost. Historical user reports showed the former enable flag = 0 and `gpu_debug_app` reported `null`, but absence/presence of that key was not proved; original other keys unknown. We avoid inventing them.

## Preventing recurrence
- App will reject activation when any of the GPU debug keys are already provisioned/active (even for MO) and refuse treating a snapshot of all four MO test values as a valid original state.
- Debug signing is still not stable. Never uninstall an app while depending on its exact rollback snapshot. Recovery cleanup requires explicit user permission after in-place normal Restore is checked.
- Only tests inside self-owned disposable GLES app. This does NOT make Outlanders eligible for official GLES layering.
