import importlib.util
import tempfile
import unittest
import zipfile
from pathlib import Path

p = Path(__file__).resolve().parents[1] / "tools" / "module_audit.py"
s = importlib.util.spec_from_file_location("module_audit", p)
m = importlib.util.module_from_spec(s)
s.loader.exec_module(m)

class ModuleAuditTests(unittest.TestCase):
    def test_missing_literal_file(self):
        with tempfile.TemporaryDirectory() as d:
            p = Path(d) / "fixture.zip"
            with zipfile.ZipFile(p, "w") as f:
                f.writestr("installer.sh", 'NEEDED=(\n"module.prop"\n"service.sh"\n"iunlocker/*"\n"$MODID.dat"\n)')
                f.writestr("module.prop", "version=1")
            self.assertEqual(m.audit(p)["missing_installer_literal_files"], ["service.sh"])
    def test_suspicious_path_is_reported(self):
        with tempfile.TemporaryDirectory() as d:
            p = Path(d) / "fixture.zip"
            with zipfile.ZipFile(p, "w") as f:
                f.writestr("../bad", "X")
            self.assertEqual(m.audit(p)["unsafe_paths"], ["../bad"])
            self.assertFalse(m.audit(p)["scripts_executed"])

if __name__ == "__main__":
    unittest.main()
