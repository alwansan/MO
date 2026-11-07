"""Synthetic archive tests only. No game APKs are needed or distributed."""
import importlib.util
import io
import json
import sys
import tempfile
import unittest
import zipfile
from pathlib import Path

SCRIPT = Path(__file__).resolve().parents[1] / "inspect_apks_readonly.py"
spec = importlib.util.spec_from_file_location("inspect_apks_readonly", SCRIPT)
m = importlib.util.module_from_spec(spec)
spec.loader.exec_module(m)


def make_apk(entries):
    out = io.BytesIO()
    with zipfile.ZipFile(out, "w", compression=zipfile.ZIP_DEFLATED) as z:
        for name, content in entries.items():
            z.writestr(name, content)
    return out.getvalue()


class InspectorTest(unittest.TestCase):
    def test_split_abi_found_outside_base_without_extracting_game(self):
        base = make_apk({
            "AndroidManifest.xml": b"\x03\x00\x08\x00fake manifest test fixture",
            "classes.dex": b"dex\nnot executable",
        })
        arm64 = make_apk({
            "AndroidManifest.xml": b"\x03\x00\x08\x00fake config test fixture",
            "lib/arm64-v8a/libUnreal.so": b"fake never executed",
            "assets/Config/Android/AndroidDeviceProfiles.ini": b"[test]\n"
        })
        with tempfile.TemporaryDirectory() as td:
            f = Path(td) / "synthetic.apks"
            with zipfile.ZipFile(f, "w") as outer:
                outer.writestr("base.apk", base)
                outer.writestr("split_config.arm64_v8a.apk", arm64)
                outer.writestr("meta.sai_v1.json", "{}")
            r = m.inspect_bundle(f)
            self.assertEqual(3, r["outer_entries"])
            self.assertTrue(r["findings"]["multiple_apk_files_detected"])
            self.assertTrue(r["findings"]["any_arm64_libs_in_scanned_splits"])
            self.assertTrue(r["findings"]["arm64_libs_outside_base_filename"])
            self.assertEqual(1, r["findings"]["config_filename_hints_found"])
            self.assertEqual(
                ["lib/arm64-v8a/libUnreal.so"],
                r["inner_apks"][1]["unreal_native_library_paths"],
            )
            self.assertNotIn("fake never executed", json.dumps(r))
            self.assertEqual([], r["skipped_members"])

    def test_outer_only_and_single_apk(self):
        with tempfile.TemporaryDirectory() as td:
            f = Path(td) / "basic.apks"
            with zipfile.ZipFile(f, "w") as out:
                out.writestr("base.apk", make_apk({"AndroidManifest.xml": b"test"}))
            shallow = m.inspect_bundle(f, scan_nested=False)
            self.assertEqual([], shallow["inner_apks"])
            self.assertEqual(["base.apk"], shallow["all_nested_apk_names"])
            deep = m.inspect_bundle(f, scan_nested=True)
            self.assertFalse(deep["findings"]["multiple_apk_files_detected"])
            self.assertFalse(deep["findings"]["any_arm64_libs_in_scanned_splits"])

    def test_nonzip_rejected(self):
        with tempfile.TemporaryDirectory() as td:
            f = Path(td) / "invalid.apks"
            f.write_bytes(b"not a zip")
            with self.assertRaises(zipfile.BadZipFile):
                m.inspect_bundle(f)


if __name__ == "__main__":
    unittest.main()
