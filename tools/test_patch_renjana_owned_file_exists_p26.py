"""Offline tests: P26 patch must stay narrow, deterministic, and fail closed."""
import unittest

from patch_renjana_owned_file_exists_p26 import NEEDLE, REPLACEMENT, patched


class RenjanaOwnedFileExistsPatchTest(unittest.TestCase):
    def test_only_owned_app_skips_file_exists_hook(self):
        source = "before\n" + NEEDLE + "after\n"
        result = patched(source)
        self.assertIn("if (packageName == \"com.alwansan.mo.glesprobe\")", result)
        self.assertIn("} else {\n                    hookFileExists()\n", result)
        self.assertEqual(result, "before\n" + REPLACEMENT + "after\n")

    def test_reject_upstream_drift_or_extra_call_site(self):
        with self.assertRaises(ValueError):
            patched("hookFileExists()")
        with self.assertRaises(ValueError):
            patched(NEEDLE + NEEDLE)

    def test_refuses_second_application(self):
        first = patched("before\n" + NEEDLE)
        with self.assertRaises(ValueError):
            patched(first)


if __name__ == "__main__":
    unittest.main()
