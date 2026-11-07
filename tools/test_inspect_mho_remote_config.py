"""Read-only RemoteConfig archive inspection tests (synthetic data only)."""
import io
import pathlib
import sys
import tarfile
import tempfile
import unittest
import zipfile

sys.path.insert(0, str(pathlib.Path(__file__).parent))
from inspect_mho_remote_config import (
    MEMBER, changed_offsets, load_member, probable_format, ranges, report
)


class ConfigArchiveTests(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.addCleanup(self.tmp.cleanup)
        self.root = pathlib.Path(self.tmp.name)

    def tar(self, name, raw):
        path = self.root / name
        with tarfile.open(path, "w") as archive:
            info = tarfile.TarInfo(MEMBER)
            info.size = len(raw)
            archive.addfile(info, io.BytesIO(raw))
        return path

    def zip(self, name, raw):
        path = self.root / name
        with zipfile.ZipFile(path, "w") as archive:
            archive.writestr(MEMBER, raw)
        return path

    def test_compares_tar_zip_without_printing_private_bytes(self):
        a = b"PRIVATE-TOKEN" + bytes([0]) * 133
        b = b"PRIVATE-TOKEN" + bytes([1]) * 133
        t1 = self.tar("low.tar", a)
        t2 = self.zip("medium.zip", b)
        self.assertEqual(a, load_member(t1))
        self.assertEqual(b, load_member(t2))
        output = report([t1, t2])
        self.assertIn("total_changed_byte_offsets=133", output)
        self.assertIn("changed_offset_ranges_0_based=13-145", output)
        self.assertNotIn("PRIVATE-TOKEN", output)

    def test_range_coalescing(self):
        self.assertEqual([(2, 4), (8, 8), (11, 12)],
                         ranges([2, 3, 4, 8, 11, 12]))
        self.assertEqual([], changed_offsets(b"x", b"x"))
        self.assertEqual([1], changed_offsets(b"x", b"xy"))

    def test_rejects_absent_remote_config(self):
        path = self.root / "missing.tar"
        with tarfile.open(path, "w") as archive:
            info = tarfile.TarInfo("other.txt")
            info.size = 1
            archive.addfile(info, io.BytesIO(b"x"))
        with self.assertRaises(ValueError):
            load_member(path)

    def test_format_clues_do_not_decode_or_advertise_schema(self):
        self.assertIn("gzip", probable_format(b"\x1f\x8b\x08stuff"))
        self.assertIn("opaque", probable_format(b"\0\xff\x01\x02"))


if __name__ == "__main__":
    unittest.main()
