"""Unit tests for the read-only Android/data archive differ.

Run in Termux or GitHub Actions:
    python -m unittest discover -s tools -p 'test_compare_android_data_snapshots.py'
"""
import io
import pathlib
import sys
import tarfile
import tempfile
import unittest
import zipfile

sys.path.insert(0, str(pathlib.Path(__file__).parent))
import compare_android_data_snapshots as diff


class ArchiveDiffTests(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.addCleanup(self.tmp.cleanup)
        self.root = pathlib.Path(self.tmp.name)

    def make_zip(self, filename, values):
        target = self.root / filename
        with zipfile.ZipFile(target, "w") as output:
            for name, contents in values.items():
                output.writestr(name, contents)
        return target

    def make_tar(self, filename, values):
        target = self.root / filename
        with tarfile.open(target, "w") as output:
            for name, contents in values.items():
                contents = contents.encode()
                member = tarfile.TarInfo(name)
                member.size = len(contents)
                member.mtime = 100
                output.addfile(member, io.BytesIO(contents))
        return target

    def test_zip_and_tar_detect_same_size_content_change(self):
        first = self.make_zip("before.zip", {
            "com.tencent.mhadv/files/Saved/Config/Android/GameUserSettings.ini": "FPS=30",
            "com.tencent.mhadv/files/Logs/sample.log": "before",
        })
        second = self.make_tar("after.tar", {
            "com.tencent.mhadv/files/Saved/Config/Android/GameUserSettings.ini": "FPS=60",
            "com.tencent.mhadv/files/Logs/sample.log": "after!",
        })
        old_files, new_files, candidates = diff.compare(first, second)
        self.assertEqual((2, 2), (old_files, new_files))
        self.assertEqual(1, len(candidates))
        self.assertEqual("MODIFIED", candidates[0][0])
        self.assertIn("GameUserSettings.ini", candidates[0][1])
        self.assertEqual(("6", "6"), candidates[0][2:])
        _, _, all_changes = diff.compare(first, second, include_all=True)
        self.assertEqual(2, len(all_changes))

    def test_identical_archives_do_not_claim_changes(self):
        entries = {"com.tencent.mhadv/files/Settings.ini": "a=b"}
        first = self.make_zip("a.zip", entries)
        second = self.make_tar("b.tar", entries)
        self.assertEqual([], diff.compare(first, second, include_all=True)[2])

    def test_rejects_tar_traversal_or_duplicate_entries(self):
        bad = self.make_tar("bad.tar", {"../outside.ini": "x"})
        with self.assertRaises(ValueError):
            diff.records(bad)
        bad_zip = self.make_zip("bad.zip", {"com.tencent.mhadv/file.ini": "x"})
        with zipfile.ZipFile(bad_zip, "a") as output:
            output.writestr("com.tencent.mhadv/file.ini", "y")
        with self.assertRaises(ValueError):
            diff.records(bad_zip)

    def test_rejects_nonarchives(self):
        path = self.root / "not_an_archive.txt"
        path.write_text("text")
        with self.assertRaises(ValueError):
            diff.records(path)


if __name__ == "__main__":
    unittest.main()
