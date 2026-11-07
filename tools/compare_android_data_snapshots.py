#!/usr/bin/env python3
"""Compare two user-exported Android/data archives without changing game files.

Accepts ZIP, uncompressed TAR, and compressed TAR archives (.tar.gz/.tgz).
Uses the Python standard library only, and reads byte-level SHA256 of regular
archive members for meaningful same-size content change detection. NEVER
extracts, modifies, writes to, or executes anything inside the game folder.

Termux example:
  python compare_android_data_snapshots.py MHO_BEFORE.tar MHO_AFTER.tar \
      --output differences.txt
  python compare_android_data_snapshots.py MHO_BEFORE.tar MHO_AFTER.tar \
      --all

Use rish/ADB shell for making archives only where Android permits access.
OS-private /data/user/0 data is not included by exporting Android/data.
"""
import argparse
import hashlib
import pathlib
import sys
import tarfile
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
BLOCK_SIZE = 1024 * 1024


def normalize(name):
    """Normalize archive paths; no extraction is ever performed."""
    name = name.replace("\\", "/")
    while name.startswith("./"):
        name = name[2:]
    name = name.lstrip("/")
    if any(x == ".." for x in name.split("/")) or not name:
        raise ValueError("Unsafe archive member name: " + repr(name))
    return name


def fingerprint(reader):
    """Hash member contents incrementally; do not buffer whole game assets."""
    digest = hashlib.sha256()
    while True:
        data = reader.read(BLOCK_SIZE)
        if not data:
            return digest.hexdigest()
        digest.update(data)


def records(filename):
    """Return mapping path -> (uncompressed byte length, SHA256)."""
    entries = {}
    if zipfile.is_zipfile(filename):
        with zipfile.ZipFile(filename, "r") as src:
            for info in src.infolist():
                if info.is_dir():
                    continue
                name = normalize(info.filename)
                if name in entries:
                    raise ValueError("Duplicate entry: " + name)
                with src.open(info, "r") as stream:
                    entries[name] = (info.file_size, fingerprint(stream))
        return entries

    if tarfile.is_tarfile(filename):
        with tarfile.open(filename, "r:*") as src:
            for info in src:
                if not info.isfile():  # Skip directories, symlinks, device nodes
                    continue
                name = normalize(info.name)
                if name in entries:
                    raise ValueError("Duplicate entry: " + name)
                stream = src.extractfile(info)
                if stream is None:
                    raise ValueError("Cannot read TAR entry: " + name)
                with stream:
                    entries[name] = (info.size, fingerprint(stream))
        return entries

    raise ValueError("Not a supported ZIP/TAR/TAR.GZ archive: " + str(filename))


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
    cli.add_argument("before", type=pathlib.Path, help="First ZIP/TAR snapshot")
    cli.add_argument("after", type=pathlib.Path, help="Second ZIP/TAR snapshot")
    cli.add_argument("--all", action="store_true",
                     help="Include all changes, including caches/logs")
    cli.add_argument("--output", type=pathlib.Path, help="Optional report file")
    args = cli.parse_args()
    if not args.before.is_file() or not args.after.is_file():
        cli.error("Two real archive files are required; BEFORE.zip / AFTER.zip "
                  "are examples, not automatically created.")

    try:
        print("Reading first archive (hashing file contents)...", file=sys.stderr)
        old = records(args.before)
        print("Reading second archive (hashing file contents)...", file=sys.stderr)
        new = records(args.after)
        changes = []
        for name in sorted(old.keys() | new.keys()):
            a, b = old.get(name), new.get(name)
            if a == b:
                continue
            if not args.all and not is_candidate(name):
                continue
            action = "ADDED" if a is None else "DELETED" if b is None else "MODIFIED"
            changes.append((action, name,
                            "-" if a is None else str(a[0]),
                            "-" if b is None else str(b[0])))
    except (OSError, ValueError, EOFError, tarfile.TarError,
            zipfile.BadZipFile, RuntimeError) as exc:
        print("ERROR: archive could not be read: " + str(exc), file=sys.stderr)
        return 1

    rows = [
        "MO — READ-ONLY ANDROID/DATA ARCHIVE DIFF",
        f"BEFORE: {args.before.name} ({len(old)} files)",
        f"AFTER: {args.after.name} ({len(new)} files)",
        f"FILTER: {'all' if args.all else 'candidate settings files only'}",
        f"CHANGES SHOWN: {len(changes)}",
        "Compared byte SHA256 hashes of ZIP/TAR members, did not extract files.",
        "A changed file does NOT prove the game trusts or loads a GPU/FPS override.",
        "Private /data/user/0 files are absent from external Android/data backups.",
        "",
        "ACTION | PATH | OLD_BYTES | NEW_BYTES",
    ]
    rows.extend(" | ".join(c) for c in changes)
    result = "\n".join(rows) + "\n"
    if args.output:
        args.output.write_text(result, encoding="utf-8")
        print("Saved report: " + str(args.output))
    else:
        print(result, end="")
    return 0


if __name__ == "__main__":
    sys.exit(main())
