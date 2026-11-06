# Static archive audit report — MO-20261107-CHAT-STATIC-002

**Read-only offline test**, not an on-device test. Tool: `research/tools/module_audit.py`, test specification `research/tests/test_module_audit.py`.

| archive | byte-level SHA256 | actual files | suspicious paths | installer literal missing |
|---|---|---:|---:|---|
| release ZIP | `c40a742aba1907d915881ccfb74c639387caa8669fdca234e36bb5420e9130f7` | 98 | 0 | 4 |
| main ZIP | `e90c8d8b30d5cbc08bf2262fb13a8b1bd35856b0368c9ca5a90aaf070b5b00da` | 98 | 0 | 4 |
| tag tar.gz | `65059a131d29728dbcaf1142987bffe5e7912aa2ddf44acf3a4294f6251b9620` | 98 | 0 | 4 |

Missing in all: `sepolicy.rule`, `system.prop`, `plugin_flasher.sh`, `service.sh`.

Original session test run locally: **2/2 unit tests passed**, audit of three archives executed; no scripts, binaries or mobile device run.

**Interpretation:** static script dependencies require investigation; cannot conclusively prove install failure without observing root installer. This isn't a route to rootless Zygisk.

How to reproduce (when original archives available locally):
```bash
python3 -m unittest discover -s research/tests -v
python3 research/tools/module_audit.py /path/to/module.zip /path/to/source.zip
```

No original iUnlockerGL source, binaries, copied scripts or proprietary assets committed.
