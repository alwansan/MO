#!/usr/bin/env python3
"""Read-only module ZIP / GitHub tar snapshot auditor. Never executes files."""
import argparse
import hashlib
import json
import re
import tarfile
import zipfile
from pathlib import Path

MAX_ENTRIES = 4096
MAX_SCRIPT = 128 * 1024

def digest_file(path):
    h = hashlib.sha256()
    with open(path, "rb") as f:
        for chunk in iter(lambda: f.read(1024 * 1024), b""):
            h.update(chunk)
    return h.hexdigest()

def audit(path):
    path = Path(path)
    if zipfile.is_zipfile(path):
        archive = zipfile.ZipFile(path)
        entries = {i.filename: i.file_size for i in archive.infolist() if not i.is_dir()}
        def read(n):
            return archive.read(n)
    elif tarfile.is_tarfile(path):
        archive = tarfile.open(path, "r:*")
        members = {i.name: i for i in archive.getmembers() if i.isfile()}
        entries = {n: i.size for n, i in members.items()}
        def read(n):
            return archive.extractfile(members[n]).read()
    else:
        raise ValueError("Only ZIP/tar supported")
    try:
        if len(entries) > MAX_ENTRIES:
            raise ValueError("Too many entries")
        first = {n.split("/", 1)[0] for n in entries if "/" in n}
        snapshot = len(first) == 1 and all("/" in n for n in entries)
        paths = {n.split("/", 1)[-1] if snapshot else n: n for n in entries}
        unsafe = sorted(n for n in entries if n.startswith("/") or ".." in n.split("/") or "\\" in n)
        expected = []
        if "installer.sh" in paths and entries[paths["installer.sh"]] <= MAX_SCRIPT:
            source = read(paths["installer.sh"]).decode("utf-8", "replace")
            block = re.search(r"\bNEEDED=\((.*?)\)", source, re.S)
            if block:
                expected = re.findall(r'^\s*["\']([^"\']+)["\']', block.group(1), re.M)
        static = [n for n in expected if "$" not in n and "*" not in n]
        return {
            "file_name": path.name,
            "sha256": digest_file(path),
            "file_count": len(entries),
            "zygisk": sorted(n for n in paths if n.startswith("zygisk/") and n.endswith(".so")),
            "apk": sorted(n for n in paths if n.endswith(".apk")),
            "missing_installer_literal_files": [n for n in static if n not in paths],
            "unsafe_paths": unsafe,
            "scripts_executed": False,
            "on_device_experiment": False
        }
    finally:
        archive.close()

if __name__ == "__main__":
    p = argparse.ArgumentParser()
    p.add_argument("archive", nargs="+")
    args = p.parse_args()
    print(json.dumps([audit(f) for f in args.archive], ensure_ascii=False, indent=2))
