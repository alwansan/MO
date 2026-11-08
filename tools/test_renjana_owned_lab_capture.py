"""Offline mock tests: rootless MO Renjana owned-app capture is bounded/read-only."""
from pathlib import Path
import os
import subprocess
import tempfile
import unittest

SCRIPT = Path(__file__).with_name("renjana_owned_lab_capture.sh")


class CaptureTest(unittest.TestCase):
    def test_guest_and_host_processes_settings_and_filtered_log(self):
        with tempfile.TemporaryDirectory() as tmp:
            p = Path(tmp)
            bindir = p / "bin"
            bindir.mkdir()

            def mock(name, body):
                f = bindir / name
                f.write_text("#!/bin/sh\n" + body + "\n")
                f.chmod(0o700)

            mock("ps", "printf '%s\\n' 'PID NAME' '14458 com.alwansan.mo.glesprobe' '14699 com.fesu.renjana' '17591 com.fesu.renjana:p0' '100 unrelated.app'")
            mock("settings", "printf 'null\\n'")
            mock("getprop", "printf 'SM8475\\n'")
            mock("dumpsys", "printf '** MEMINFO in pid %s **\\nTOTAL PSS: 77021 TOTAL RSS: 158380 TOTAL SWAP PSS: 47886\\n' \"$2\"")
            mock("logcat", "echo '11-08 03:40:00.000 17591 17591 D Guest: marker'; echo '11-08 03:40:01.000 100 100 D Other: should_not_appear'; echo '11-08 03:40:02.000 100 100 D StubActivity: stub marker'")

            env = dict(os.environ, PATH=str(bindir) + ":" + os.environ["PATH"], MO_RENJANA_OUTPUT_DIR=tmp)
            res = subprocess.run(["sh", str(SCRIPT)], env=env, capture_output=True, text=True, timeout=8)
            self.assertEqual(res.returncode, 0, res.stderr)
            artifacts = list(p.glob("MO_RENJANA_OWNED_CAPTURE_*.txt"))
            self.assertEqual(len(artifacts), 1)
            txt = artifacts[0].read_text()
            self.assertIn("17591 com.fesu.renjana:p0", txt)
            self.assertIn("PID=17591", txt)
            self.assertIn("TOTAL PSS: 77021", txt)
            self.assertIn("17591 17591 D Guest: marker", txt)
            self.assertIn("StubActivity: stub marker", txt)
            self.assertNotIn("should_not_appear", txt)

    def test_failed_settings_read_is_unverified_not_clean(self):
        with tempfile.TemporaryDirectory() as tmp:
            p = Path(tmp)
            bindir = p / "bin"
            bindir.mkdir()

            def mock(name, body):
                f = bindir / name
                f.write_text("#!/bin/sh\n" + body + "\n")
                f.chmod(0o700)

            mock("ps", "printf '%s\n' '14699 com.fesu.renjana' '23140 com.fesu.renjana:p1'")
            mock("settings", "echo 'cmd: Failure calling service settings: Failed transaction (2147483646)'; exit 1")
            mock("getprop", "printf '35\n'")
            mock("dumpsys", "printf 'TOTAL PSS: 123 TOTAL RSS: 456 TOTAL SWAP PSS: 0\n'")
            mock("logcat", "exit 0")
            env = dict(os.environ, PATH=str(bindir) + ":" + os.environ["PATH"], MO_RENJANA_OUTPUT_DIR=tmp)
            res = subprocess.run(["sh", str(SCRIPT)], env=env, capture_output=True, text=True, timeout=8)
            self.assertEqual(res.returncode, 0, res.stderr)
            reports = list(p.glob("MO_RENJANA_OWNED_CAPTURE_*.txt"))
            self.assertEqual(len(reports), 1)
            txt = reports[0].read_text()
            self.assertIn("enable_gpu_debug_layers = UNVERIFIED", txt)
            self.assertNotIn("enable_gpu_debug_layers = 0", txt)
            self.assertIn("23140 com.fesu.renjana:p1", txt)

    def test_missing_output_directory_refused(self):
        with tempfile.TemporaryDirectory() as tmp:
            env = dict(os.environ, MO_RENJANA_OUTPUT_DIR=str(Path(tmp) / "missing"))
            res = subprocess.run(["sh", str(SCRIPT)], env=env, capture_output=True, text=True, timeout=8)
            self.assertNotEqual(res.returncode, 0)
            self.assertIn("not writable", res.stderr)


if __name__ == "__main__":
    unittest.main()
