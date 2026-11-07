#!/usr/bin/env python3
"""Read-only report of an Android .apks ZIP and its nested APKs.

This script does not install, sign, patch, execute, or extract game code.
Nested APKs are streamed to a temporary anonymous file for ZIP metadata
inspection; no APK bytes or private session data are printed in the report.

Usage:
    python inspect_apks_readonly.py /path/to/game.apks > MO_APKS_REPORT.json
Requires Python 3.9+, standard library only.
"""
import argparse
import json
import os
import re
import sys
import tempfile
import zipfile
from pathlib import Path

MAX_OUTER_MEMBER = 700 * 1024 * 1024
MAX_TOTAL_NESTED = 1200 * 1024 * 1024
MAX_MEMBER_COUNT = 10000
INTERESTING = re.compile(
    r"(?:^|/)(?:default|base|android)?deviceprofiles?\.ini$"
    r"|(?:^|/)(?:gameusersettings|defaultengine|engine|defaultgame|androidengine"
    r"|androidgame|scalability|androidscalability|defaultdeviceprofiles)\.ini$"
    r"|(?:^|/)android.*config.*rules(?:\.xml|\.txt|\.bin)?$"
    r"|(?:^|/)(?:assets/)?(?:.*(?:gpu|graphics|profile|device|fps|frame.?rate|quality)"
    r".*\.(?:ini|cfg|json|xml|properties|txt))$",
    re.I,
)
TOP_LEVEL_LIMIT = 60
LIBRARY_LIMIT = 80
HINT_LIMIT = 80
STREAM_CHUNK = 1024 * 1024


def limited_copy(source, dest, limit):
    copied = 0
    while True:
        piece = source.read(min(STREAM_CHUNK, limit - copied + 1))
        if not piece:
            break
        copied += len(piece)
        if copied > limit:
            raise ValueError("Nested member exceeds 700 MiB guard")
        dest.write(piece)
    dest.flush()
    dest.seek(0)
    return copied


def describe_apk(stream, bundle_name):
    result = {"split_filename": bundle_name}
    with zipfile.ZipFile(stream) as inner:
        names = inner.infolist()
        result["entries"] = len(names)
        result["uncompressed_bytes"] = sum(e.file_size for e in names)
        libs = [e.filename for e in names
                if e.filename.startswith("lib/") and e.filename.endswith(".so")]
        abis = sorted({e.filename.split("/")[1] for e in names
                       if e.filename.startswith("lib/") and len(e.filename.split("/")) >= 3})
        hints = [e.filename for e in names if INTERESTING.search(e.filename)]
        unreal = [e.filename for e in names if
                  ("libUE4.so" in e.filename or "libUnreal.so" in e.filename
                   or "libUnrealAndroid.so" in e.filename)]
        result["abi_directories"] = abis
        result["native_library_count"] = len(libs)
        result["native_library_names"] = libs[:LIBRARY_LIMIT]
        result["native_library_names_truncated"] = len(libs) > LIBRARY_LIMIT
        result["unreal_native_library_paths"] = unreal
        result["config_filename_hints"] = hints[:HINT_LIMIT]
        result["config_hints_truncated"] = len(hints) > HINT_LIMIT
        result["has_AndroidManifest_xml"] = "AndroidManifest.xml" in inner.namelist()
        result["has_classes_dex"] = any(
            e.filename == "classes.dex" or re.fullmatch(r"classes\d+\.dex", e.filename)
            for e in names
        )
    return result


def inspect_bundle(path, scan_nested=True):
    report = {
        "inspector": "MO .apks read-only metadata inspector v1",
        "source_filename": path.name,
        "source_size_bytes": path.stat().st_size,
        "scope": "ZIP metadata only; no game-data contents, credentials, or signatures published",
        "warning": (
            "Filenames and native libraries do not reveal the real game FPS lock logic. "
            "Missing config filename does not prove the setting does not exist."
        ),
        "inner_apks": [],
        "skipped_members": [],
    }
    total_spooled = 0
    with zipfile.ZipFile(path, "r") as outer:
        entries = outer.infolist()
        report["outer_entries"] = len(entries)
        if len(entries) > MAX_MEMBER_COUNT:
            raise ValueError("Unusually large ZIP entry count; refusing to inspect")
        report["outer_members"] = [
            {"name": e.filename, "compressed_bytes": e.compress_size,
             "uncompressed_bytes": e.file_size}
            for e in entries[:TOP_LEVEL_LIMIT]
        ]
        report["outer_members_truncated"] = len(entries) > TOP_LEVEL_LIMIT
        report["all_nested_apk_names"] = [e.filename for e in entries
                                          if e.filename.lower().endswith(".apk")]
        if not scan_nested:
            return report
        for info in entries:
            if info.is_dir() or not info.filename.lower().endswith(".apk"):
                continue
            if info.flag_bits & 1:
                report["skipped_members"].append(
                    {"name": info.filename, "reason": "encrypted ZIP entry"})
                continue
            if info.file_size > MAX_OUTER_MEMBER or total_spooled + info.file_size > MAX_TOTAL_NESTED:
                report["skipped_members"].append(
                    {"name": info.filename, "reason": "safety/space limit",
                     "uncompressed_bytes": info.file_size})
                continue
            try:
                with tempfile.TemporaryFile() as temp:
                    with outer.open(info) as source:
                        copied = limited_copy(source, temp, MAX_OUTER_MEMBER)
                    total_spooled += copied
                    report["inner_apks"].append(describe_apk(temp, info.filename))
            except (OSError, RuntimeError, zipfile.BadZipFile, ValueError) as exc:
                report["skipped_members"].append(
                    {"name": info.filename, "reason": type(exc).__name__ + ": " + str(exc)[:180]})
    report["nested_bytes_examined"] = total_spooled
    report["findings"] = {
        "multiple_apk_files_detected": len(report["all_nested_apk_names"]) > 1,
        "any_arm64_libs_in_scanned_splits": any(
            "arm64-v8a" in e.get("abi_directories", []) for e in report["inner_apks"]
        ),
        "arm64_libs_outside_base_filename": any(
            "arm64-v8a" in e.get("abi_directories", [])
            and not re.search(r"(?:^|/)(?:base|base-master)(?:\.apk|[-_.])", e["split_filename"], re.I)
            for e in report["inner_apks"]
        ),
        "config_filename_hints_found": sum(
            len(e.get("config_filename_hints", [])) for e in report["inner_apks"]
        ),
        "note": (
            "The base-vs-split diagnostic is heuristic: filename conventions vary. "
            "Determine actual split relationship from AndroidManifest and installed package "
            "metadata separately. No source was patched."
        ),
    }
    return report


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("bundle", help="local .apks path; never sent over network")
    parser.add_argument("--outer-only", action="store_true",
                        help="fast ZIP inventory without opening nested APKs")
    args = parser.parse_args()
    filename = Path(os.path.expanduser(args.bundle))
    if not filename.is_file():
        parser.error("File does not exist: " + str(filename))
    try:
        result = inspect_bundle(filename, not args.outer_only)
    except (OSError, ValueError, zipfile.BadZipFile) as exc:
        print(json.dumps({"inspector_error": type(exc).__name__ + ": " + str(exc)},
                         ensure_ascii=False, indent=2))
        return 2
    print(json.dumps(result, ensure_ascii=False, indent=2))
    return 0


if __name__ == "__main__":
    sys.exit(main())
