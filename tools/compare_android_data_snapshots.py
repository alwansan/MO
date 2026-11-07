#!/usr/bin/env python3
"""Read-only diff of two user-exported Android/data ZIP snapshots.

Designed for Termux (Python standard library only). It NEVER modifies the
game or extracts/executess APK/PAK files. It reports ZIP-entry metadata only
(path, size, CRC) to help locate files saved by a legitimate in-game setting.

Example:
  python compare_android_data_snapshots.py BEFORE.zip AFTER.zip --all
  python compare_android_data_snapshots.py BEFORE.zip AFTER.zip --output differences.txt

Note: OS-private /data/user/0 is not covered by external Android/data ZIPs.
"""
import argparse
import pathlib
import sys
import zipfile

CANDIDATE_TERMS = (
    "config", "setting", "graphic", "deviceprofile", "scalab", "usercustom",
    "quality", "fps", "framerate", "resolution", "render", "profile", ".ini",
    "savedgames", ".json", ".xml", ".properties", ".db", ".sqlite", ".sav",
    ".dat",
)
NOISY_TERMS = (
    "/logs/", "/log/", "/cache/", "/rhicache/", "/temp/", "/tmp/",
    "/crash", "/shader", "/analytics", "/telemetry", "/screenshots/",
)


def records(filename):
    """Return ZIP entry's byte size and CRC, not its data."""
    entries = {}
    with zipfile.ZipFile(filename, "r") as source:
        for member in source.infolist():
            if member.is_dir():
                continue
            name = member.filename.replace("\\", "/").lstrip("/")
            if name.startswith("../") or "/../" in name:
                raise ValueError("Potential ZIP path traversal: " + name)
            if name in entries:
                raise ValueError("Duplicate ZIP entry; can't compare safely: " + name)
            entries[name] = (member.file_size, member.CRC)
    return entries


def is_candidate(name):
    low = "/" + name.lower()
    return any(term in low for term in CANDIDATE_TERMS) and not any(
        term in low for term in NOISY_TERMS
    )


def compare(before, after, include_all=False):
    old, new = records(before), records(after)
    changes = []
    for name in sorted(old.keys() | new.keys()):
        lhs, rhs = old.get(name), new.get(name)
        if lhs == rhs:
            continue
        action = "ADDED" if lhs is None else "DELETED" if rhs is None else "MODIFIED"
        if not include_all and not is_candidate(name):
            continue
        old_size = "-" if lhs is None else str(lhs[0])
        new_size = "-" if rhs is None else str(rhs[0])
        changes.append((action, name, old_size, new_size))
    return len(old), len(new), changes


def main():
    cli = argparse.ArgumentParser(description=__doc__)
    cli.add_argument("before", type=pathlib.Path, help="First user-exported ZIP")
    cli.add_argument("after", type=pathlib.Path, help="Second user-exported ZIP")
    cli.add_argument("--all", action="store_true",
                     help="Include cache/log/temp changes, not only candidates")
    cli.add_argument("--output", type=pathlib.Path, help="Optional report text file")
    args = cli.parse_args()

    if not args.before.is_file() or not args.after.is_file():
        cli.error("Both ZIP snapshot paths must exist")
    try:
        old_count, new_count, entries = compare(
            args.before, args.after, include_all=args.all
        )
    except (OSError, ValueError, zipfile.BadZipFile) as problem:
        print(f"ERROR reading ZIP: {problem}", file=sys.stderr)
        return 1

    rows = [
        "MO — READ-ONLY ANDROID/DATA SNAPSHOT DIFF",
        f"BEFORE ZIP: {args.before.name} ({old_count} files)",
        f"AFTER ZIP: {args.after.name} ({new_count} files)",
        f"FILTER: {'all changes' if args.all else 'possible settings only'}",
        f"CHANGES SHOWN: {len(entries)}",
        "No files were extracted or changed. Changed ZIP metadata isn't proof",
        "that the game loaded a graphics config or that an option was unlocked.",
        "",
        "ACTION | PATH | OLD_BYTES | NEW_BYTES",
    ]
    rows.extend(" | ".join(entry) for entry in entries)
    result = "\n".join(rows) + "\n"
    if args.output:
        args.output.write_text(result, encoding="utf-8")
        print(f"Saved local report to: {args.output}")
    else:
        print(result, end="")
    return 0


if __name__ == "__main__":
    sys.exit(main())
