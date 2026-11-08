#!/usr/bin/env python3
"""Analyze a MO P23 Renjana owned-app capture locally, without changing the device.

Prints only a compact summary; never uploads or republishes raw device logs.
"""
import argparse
import hashlib
from pathlib import Path
import re

TARGETS = {"com.fesu.renjana", "com.alwansan.mo.glesprobe"}
GPU_KEYS = (
    "enable_gpu_debug_layers", "gpu_debug_app",
    "gpu_debug_layers_gles", "gpu_debug_layer_app",
)
METRIC = re.compile(r"TOTAL (PSS|RSS|SWAP PSS):\s*([0-9]+)")
PROCESS = re.compile(r"^Process (\S+) PID=(\d+)$")


def analyze(raw: str) -> dict:
    lines = raw.splitlines()
    process_names = {}
    metrics = {}
    active_pid = None
    settings = {}
    for line in lines:
        hit = re.fullmatch(r"(\d+) (\S+)", line)
        if hit and (hit.group(2) in TARGETS or re.fullmatch(r"com\.fesu\.renjana:p\d+", hit.group(2))):
            process_names[int(hit.group(1))] = hit.group(2)
        hit = PROCESS.fullmatch(line)
        if hit:
            active_pid = int(hit.group(2))
            metrics[active_pid] = {"name": hit.group(1)}
            continue
        if line.startswith("[RELEVANT RECENT LOGCAT"):
            active_pid = None
        if active_pid is not None and "TOTAL PSS:" in line:
            for kind, n in METRIC.findall(line):
                metrics[active_pid][kind] = int(n)
        for key in GPU_KEYS:
            if line.startswith(key + " = "):
                value = line.split(" = ", 1)[1].strip()
                settings[key] = (
                    "UNVERIFIED" if "Failure calling service" in value or
                    "(unavailable)" in value or value.startswith("UNVERIFIED")
                    else value
                )

    guests = [(pid, name) for pid, name in process_names.items() if re.fullmatch(r"com\.fesu\.renjana:p\d+", name)]
    main = [pid for pid, name in process_names.items() if name == "com.fesu.renjana"]
    main_guest_pss = [metrics.get(pid, {}).get("PSS") for pid in main + [pid for pid, _ in guests]]
    total = sum(main_guest_pss) if main and guests and all(x is not None for x in main_guest_pss) else None
    log = raw.partition("[RELEVANT RECENT LOGCAT")[2]
    return {
        "processes": [(pid, name, metrics.get(pid, {})) for pid, name in sorted(process_names.items())],
        "main_guest_pss_kib": total,
        "settings": {key: settings.get(key, "NOT_CAPTURED") for key in GPU_KEYS},
        "events": {
            "stub_guest_launch": "onCreate (pid=" in log and "launching guest com.alwansan.mo.glesprobe" in log,
            "guest_application_started": "Guest Application started: com.alwansan.mo.glesprobe" in log,
            "guest_native_library_loaded": "libMOProbeRenderer.so" in log and " using isolated ns " in log,
            "build_spoof_logged": "Applied device spoofing to process" in log,
            "qualcomm_driver_path_logged": "/vendor/lib64/egl/libGLESv2_adreno.so" in log,
            "pine_missing_symbol_count": len(re.findall(r"^.* E Pine\s+: Symbol .* not found", log, re.M)),
            "virtual_directory_warning_count": log.count("Could not create virtual directory"),
        },
        "capture_complete": "[END OF DIAGNOSTIC]" in raw,
    }


def render(result: dict, sha256: str) -> str:
    out = ["MO Renjana owned-app capture — local summary (no raw log)", f"Input SHA256: {sha256}",
           f"Capture complete: {'YES' if result['capture_complete'] else 'NO / PARTIAL'}", "Processes (PSS/RSS/SWAP PSS, KiB):"]
    for pid, name, metrics in result["processes"]:
        fmt = ", ".join(f"{key}={metrics.get(key, 'N/A')}" for key in ("PSS", "RSS", "SWAP PSS"))
        out.append(f"  {pid} {name}: {fmt}")
    total = result["main_guest_pss_kib"]
    out.append(f"Renjana main + active guest PSS: {total if total is not None else 'NOT MEASURABLE'} KiB")
    out.append("CAUTION: not a baseline-adjusted container overhead; no FPS or thermal conclusion.")
    out.append("GPU Debug settings (read-only; UNVERIFIED is NOT null/disabled):")
    out.extend(f"  {key}: {value}" for key, value in result["settings"].items())
    out.append("Log evidence (not equivalent to independent in-guest API readback):")
    for key, value in result["events"].items():
        out.append(f"  {key}: {value}")
    out.append("Vulkan/GLES spoof: NOT PROVEN by this capture; inspect app-owned API screenshots separately.")
    return "\n".join(out)


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("capture", type=Path, help="P23 report already exported from owned app Rish")
    args = parser.parse_args()
    payload = args.capture.read_bytes()
    raw = payload.decode("utf-8", errors="replace")
    print(render(analyze(raw), hashlib.sha256(payload).hexdigest()))


if __name__ == "__main__":
    main()
