#!/usr/bin/env python3
"""Inspect the same small RemoteConfig.config member across owner TAR/ZIP backups.

Read-only and designed for Termux: no root, extractions, patches, network,
secrets/contents printed, or access to /data/user/0. Outputs only file sizes,
SHA256 digests, format clues, and ranges of byte offsets that changed. Compare
at least two archives created before/after normal in-game UI activity.

Usage:
  python inspect_mho_remote_config.py MHO_AFTER_GRAPHICS_LOW.tar \
      MHO_AFTER_GRAPHICS_MEDIUM.tar MHO_MEDIUM_CONTROL.tar \
      --output REMOTE_CONFIG_PATTERN.txt

Identical/different bytes do not prove an app loads a graphics profile.
"""
import argparse
import hashlib
import pathlib
import sys
import tarfile
import zipfile

MEMBER = "com.tencent.mhadv/cache/RemoteConfig.config"
MAX_MEMBER_SIZE = 1024 * 1024


def load_member(path):
    """Read only exactly named regular archive member; never extract it."""
    if zipfile.is_zipfile(path):
        with zipfile.ZipFile(path) as archive:
            candidates = [info for info in archive.infolist()
                          if info.filename.lstrip("./") == MEMBER
                          and not info.is_dir()]
            if len(candidates) != 1:
                raise ValueError("Expected exactly one RemoteConfig in " + path.name)
            item = candidates[0]
            if item.file_size > MAX_MEMBER_SIZE:
                raise ValueError("Unexpected RemoteConfig size in " + path.name)
            with archive.open(item, "r") as data:
                value = data.read(MAX_MEMBER_SIZE + 1)
            if len(value) != item.file_size:
                raise ValueError("Truncated RemoteConfig in " + path.name)
            return value

    if tarfile.is_tarfile(path):
        with tarfile.open(path, "r:*") as archive:
            items = [item for item in archive
                     if item.name.lstrip("./") == MEMBER]
            if len(items) != 1 or not items[0].isfile():
                raise ValueError("Expected one regular RemoteConfig in " + path.name)
            item = items[0]
            if item.size > MAX_MEMBER_SIZE:
                raise ValueError("Unexpected RemoteConfig size in " + path.name)
            stream = archive.extractfile(item)
            if stream is None:
                raise ValueError("Cannot read RemoteConfig in " + path.name)
            with stream:
                value = stream.read(MAX_MEMBER_SIZE + 1)
            if len(value) != item.size:
                raise ValueError("Truncated RemoteConfig in " + path.name)
            return value
    raise ValueError("Not a TAR/ZIP archive: " + path.name)


def ranges(indices):
    """Combine consecutive 0-based byte offsets; print no underlying bytes."""
    if not indices:
        return []
    runs = []
    start = last = indices[0]
    for i in indices[1:]:
        if i != last + 1:
            runs.append((start, last))
            start = i
        last = i
    runs.append((start, last))
    return runs


def changed_offsets(first, second):
    end = max(len(first), len(second))
    return [i for i in range(end)
            if i >= len(first) or i >= len(second) or first[i] != second[i]]


def probable_format(value):
    if value.startswith(b"\x1f\x8b"):
        return "gzip signature (not decompressed)"
    if value.startswith(b"PK\x03\x04"):
        return "ZIP signature (not unpacked)"
    if value.startswith(b"SQLite format 3"):
        return "SQLite signature (not queried)"
    if value[:2] in (b"\x78\x01", b"\x78\x9c", b"\x78\xda"):
        return "possible zlib envelope (not decompressed)"
    if value.strip().startswith((b"{", b"[")):
        return "JSON-like start; validate separately, no schema assumed"
    ratio = (sum(b == 9 or b == 10 or b == 13 or 32 <= b <= 126
                 for b in value) / max(1, len(value)))
    if ratio >= 0.95:
        return "mostly printable ASCII-like bytes, encoding unknown"
    return "opaque/binary-like bytes; no protocol inferred"


def report(paths):
    datasets = [(path.name, load_member(path)) for path in paths]
    lines = [
        "MO P19 — READ-ONLY RemoteConfig CONFIG BYTE-PATTERN ANALYSIS",
        "Member: " + MEMBER,
        "No raw config bytes, tokens or decoded contents are included.",
        "Archive members read in memory only; no game files modified.",
        "",
    ]
    for name, value in datasets:
        lines.append(f"SAMPLE {name}: bytes={len(value)} "
                     f"sha256={hashlib.sha256(value).hexdigest()} "
                     f"format_clue={probable_format(value)}")

    lines.append("")
    for (first_name, first), (next_name, second) in zip(datasets, datasets[1:]):
        offsets = changed_offsets(first, second)
        groups = ranges(offsets)
        preview = ", ".join(
            str(start) if start == end else f"{start}-{end}"
            for start, end in groups[:35]
        )
        if len(groups) > 35:
            preview += f", ... ({len(groups) - 35} more ranges)"
        lines += [
            f"PAIR {first_name} -> {next_name}",
            f"  total_changed_byte_offsets={len(offsets)} "
            f"of max_length={max(len(first), len(second))}",
            f"  changed_offset_ranges_0_based={preview if preview else '(none)'}",
            "  Interpretation: a changed offset is NOT evidence of a graphics "
            "quality field or device whitelist.",
        ]
    lines += [
        "",
        "NOTE: All copies may change because the game starts or a server "
        "refreshes its config. Compare the no-quality-change control.",
        "No direct evidence from this report that Android/data unlocks "
        "High/Ultra/Concept Art or that GL/Vulkan reports Adreno840.",
    ]
    return "\n".join(lines) + "\n"


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("archives", type=pathlib.Path, nargs="+",
                        help="Two or more real TAR/ZIP snapshots")
    parser.add_argument("--output", type=pathlib.Path)
    args = parser.parse_args()
    if len(args.archives) < 2:
        parser.error("At least TWO real archive paths are required")
    if any(not path.is_file() for path in args.archives):
        parser.error("One or more archives do not exist; use filenames in Download")

    try:
        result = report(args.archives)
    except (OSError, ValueError, EOFError, RuntimeError,
            tarfile.TarError, zipfile.BadZipFile) as err:
        print("ERROR: " + str(err), file=sys.stderr)
        return 1
    if args.output:
        args.output.write_text(result, encoding="utf-8")
        print("Saved report: " + str(args.output))
    else:
        print(result, end="")
    return 0


if __name__ == "__main__":
    sys.exit(main())
