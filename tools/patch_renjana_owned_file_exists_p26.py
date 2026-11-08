#!/usr/bin/env python3
"""P26: apply a minimal, source-pinned MO-owned-only Renjana File.exists experiment.

Requires independently verified upstream Renjana SHA 14302a57cd66114c6979acf7ca56c97f845a6841.
The patch is experimental, not a general-purpose anti-detection bypass or storage fix.
It changes one upstream Kotlin call site only, and fails closed on source drift.
"""
from pathlib import Path
import argparse
import sys

RELATIVE = Path("app/src/main/java/com/fesu/renjana/hooks/PineHookManager.kt")
NEEDLE = """                hookFileConstructors()
                hookFileExists()
                hookSystemGetProperty()
"""
REPLACEMENT = """                hookFileConstructors()
                // MO P26 experimental owned-guest only. Native/stat and Java File I/O
                // worked on Android 15 but File.exists was falsely hidden, breaking
                // VirtualContext.openFileInput's existence gate. Do not change
                // anti-detection behavior for any other guest package.
                if (packageName == "com.alwansan.mo.glesprobe") {
                    RenjanaLog.i(TAG, "MO P26 owned storage lab: skipping File.exists cloaking hook")
                } else {
                    hookFileExists()
                }
                hookSystemGetProperty()
"""


def patched(source: str) -> str:
    if source.count(NEEDLE) != 1:
        raise ValueError("Pinned Renjana source mismatch: expected one File.exists hook site")
    if "MO P26 owned storage lab" in source:
        raise ValueError("P26 patch already applied; refuse second application")
    return source.replace(NEEDLE, REPLACEMENT, 1)


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("renjana_root", type=Path, help="checked-out pinned upstream source")
    args = parser.parse_args()
    target = args.renjana_root / RELATIVE
    if not target.is_file():
        parser.error(f"Pinned upstream source is missing: {target}")
    original = target.read_text(encoding="utf-8")
    target.write_text(patched(original), encoding="utf-8")
    print(f"Applied P26 MO-owned-only File.exists hypothesis test: {target}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
