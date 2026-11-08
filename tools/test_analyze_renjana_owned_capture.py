"""Offline fixtures for deterministic, read-only P23 capture analysis."""
import unittest
from analyze_renjana_owned_capture import analyze


class CaptureAnalysisTest(unittest.TestCase):
    def test_p1_memory_and_settings_failure(self):
        txt = """[PROCESSES AT CAPTURE TIME]
14458 com.alwansan.mo.glesprobe
14699 com.fesu.renjana
23140 com.fesu.renjana:p1
[GPU DEBUG SETTINGS — READ ONLY]
enable_gpu_debug_layers = cmd: Failure calling service settings: Failed transaction (2147483646)
gpu_debug_app = null
gpu_debug_layers_gles = (unavailable)
gpu_debug_layer_app = null
[PER-PROCESS MEMINFO SUMMARIES — kB]
Process com.fesu.renjana PID=14699
           TOTAL PSS: 83127 TOTAL RSS: 175340 TOTAL SWAP PSS: 22181
Process com.fesu.renjana:p1 PID=23140
           TOTAL PSS: 106509 TOTAL RSS: 231020 TOTAL SWAP PSS: 164
[RELEVANT RECENT LOGCAT — bounded]
11-08 03:56:55.951 23140 23140 I Renjana:StubActivity: StubActivity[1] onCreate (pid=23140) → launching guest com.alwansan.mo.glesprobe.ProbeActivity
11-08 03:56:56.284 23140 23140 I Renjana:StubActivity: Guest Application started: com.alwansan.mo.glesprobe.MOTestApplication
11-08 03:56:56.384 23140 29653 D nativeloader: Load /mock/libMOProbeRenderer.so using isolated ns clns-9 (caller=stub): ok
11-08 03:56:56.265 23140 23140 I Renjana:DeviceFingerprint: Applied device spoofing to process (pid=23140)
11-08 03:56:55.946 23140 29646 I AdrenoGLES-0: Driver Path: /vendor/lib64/egl/libGLESv2_adreno.so
11-08 03:56:55.886 23140 23140 E Pine    : Symbol x not found
11-08 03:56:56.297 23140 29652 W Renjana:VirtualCtx: Could not create virtual directory: /private
[END OF DIAGNOSTIC]
"""
        r = analyze(txt)
        self.assertEqual(r["main_guest_pss_kib"], 189636)
        self.assertEqual(r["settings"]["enable_gpu_debug_layers"], "UNVERIFIED")
        self.assertEqual(r["settings"]["gpu_debug_app"], "null")
        self.assertTrue(r["events"]["stub_guest_launch"])
        self.assertTrue(r["events"]["guest_native_library_loaded"])
        self.assertEqual(r["events"]["pine_missing_symbol_count"], 1)
        self.assertEqual(r["events"]["virtual_directory_warning_count"], 1)

    def test_no_guest_does_not_invent_total(self):
        r = analyze("[PROCESSES AT CAPTURE TIME]\n14699 com.fesu.renjana\n")
        self.assertIsNone(r["main_guest_pss_kib"])
        self.assertEqual(r["settings"]["enable_gpu_debug_layers"], "NOT_CAPTURED")
        self.assertFalse(r["events"]["stub_guest_launch"])

    def test_guest_without_meminfo_does_not_invent_total(self):
        r = analyze("[PROCESSES AT CAPTURE TIME]\n14699 com.fesu.renjana\n23140 com.fesu.renjana:p1\n")
        self.assertIsNone(r["main_guest_pss_kib"])


if __name__ == "__main__":
    unittest.main()
