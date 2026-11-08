#!/system/bin/sh
# MO P23 — strictly read-only Renjana owned-app diagnostics, Android shell/Rish.
# NO root, Android settings writes, logcat clears, game access, or guest modifications.
set -u
OUTDIR="${MO_RENJANA_OUTPUT_DIR:-/storage/emulated/0/Download}"
if [ ! -d "$OUTDIR" ] || [ ! -w "$OUTDIR" ]; then
  echo "ERROR: Output folder is missing or not writable: $OUTDIR" >&2
  exit 1
fi
STAMP="$(date '+%Y%m%d_%H%M%S')"
OUTFILE="$OUTDIR/MO_RENJANA_OWNED_CAPTURE_$STAMP.txt"
if [ -e "$OUTFILE" ]; then
  echo "ERROR: Refusing to overwrite existing report: $OUTFILE" >&2
  exit 1
fi
PROCINFO="$(ps -A -o PID,NAME 2>/dev/null | awk '$2 == "com.fesu.renjana" || $2 == "com.alwansan.mo.glesprobe" || $2 ~ /^com\.fesu\.renjana:p[0-9]+$/ { print $1 " " $2 }')"
PIDCSV="$(printf '%s\n' "$PROCINFO" | awk '$1 ~ /^[0-9]+$/ {printf "%s%s",(n++ ? "," : ""),$1} END{print ""}')"
{
  echo "MO RENJANA OWNED-APP DIAGNOSTIC — READ ONLY"
  echo "Captured local time: $(date '+%Y-%m-%d %H:%M:%S %z')"
  echo "UID: $(id 2>/dev/null)"
  echo "Device (real OS property): $(getprop ro.product.model 2>/dev/null)"
  echo "Android API: $(getprop ro.build.version.sdk 2>/dev/null)"
  echo "Scope: com.fesu.renjana, com.alwansan.mo.glesprobe only; NOT Outlanders"
  echo
  echo '[PROCESSES AT CAPTURE TIME]'
  if [ -n "$PROCINFO" ]; then printf '%s\n' "$PROCINFO"; else echo '(no target processes)'; fi
  echo 'Note: Compare in-app PID to process name; Renjana:pN implies stub guest, original package means host.'
  echo
  echo '[GPU DEBUG SETTINGS — READ ONLY; DO NOT CHANGE AUTOMATICALLY]'
  for KEY in enable_gpu_debug_layers gpu_debug_app gpu_debug_layers_gles gpu_debug_layer_app; do
    # A failed Settings service transaction is UNKNOWN, never a clean/disabled state.
    SETTING_VALUE="$(settings get global "$KEY" 2>&1)"
    SETTING_RC=$?
    case "$SETTING_VALUE" in
      ''|*'Failure calling service'*|*'Exception'*) SETTING_RC=1 ;;
    esac
    if [ "$SETTING_RC" -eq 0 ]; then
      printf '%s = %s\n' "$KEY" "$SETTING_VALUE"
    else
      printf '%s = UNVERIFIED (read-only settings get failed; rc=%s)\n' "$KEY" "$SETTING_RC"
    fi
  done
  echo
  echo '[PER-PROCESS MEMINFO SUMMARIES — kB]'
  if [ -z "$PROCINFO" ]; then echo '(no target processes currently running)'; fi
  printf '%s\n' "$PROCINFO" | while read -r PID PROCNAME; do
    case "$PID" in ''|*[!0-9]*) continue ;; esac
    echo "Process $PROCNAME PID=$PID"
    dumpsys meminfo "$PID" 2>&1 | grep -E '^\*\* MEMINFO|TOTAL PSS:|TOTAL RSS:|TOTAL SWAP PSS:|^.*TOTAL[[:space:]]+[0-9]+' || true
  done
  echo
  echo '[RELEVANT RECENT LOGCAT — bounded last 12000 messages; no global log clear]'
  echo "Selected process PID(s): ${PIDCSV:-none}"
  echo 'If no messages appear: app may use internal Renjana Error Logs or buffer already rotated.'
  # -t N implies -d: bounded output and automatic exit on Android 15.
  # Avoid broad log dumps: keep only target PIDs or exact Renjana component tags.
  logcat -t 12000 -v threadtime -b main -b crash 2>&1 | awk -v pids="$PIDCSV" '
    BEGIN {n=split(pids,a,","); for (i=1;i<=n;i++) if (a[i]!="") allowed[a[i]]=1}
    ($3 in allowed) || / [VDIWEF] (InstanceLauncher|StubActivity|PineHookManager|CoreHooks|DeviceFingerprint|RenjanaLog):/ {print}
  ' | tail -n 1000
  echo
  echo '[END OF DIAGNOSTIC]'
} > "$OUTFILE"
if [ -s "$OUTFILE" ]; then
  echo "Saved: $OUTFILE"
  ls -lh "$OUTFILE"
else
  echo "ERROR: Empty capture file" >&2
  exit 1
fi
