# MO-20261107-CHAT-RELEASE-010
origin=chat, 2026-11-07 Asia/Baghdad
Requested: publish APKS directly in https://github.com/alwansan/MO/releases; no ZIP packaging of the APKs in user download channel.
Prior state: no releases, workflow uploaded artifacts zipped by GitHub.
Changes: workflow contents:write, build APKs (MO and owned GLES test), verify with unzip/sha256, create unique prerelease via gh release create with two raw APK attachments, idempotent rerun upload. Removed action upload-artifact ZIP step. Updated README/status/memory/restore/docs. GitHub Actions run must be checked for actual success and release attachment names/URLs.
Note: no game modified, optional GPU debugging snapshot must be restored before user deletes/updates MO.

## Release publishing confirmed — 2026-11-07
- GitHub Actions run https://github.com/alwansan/MO/actions/runs/42311901394: **completed success**. Commit `58de082ab63a797087f827633ba4dbd35fb5aa2f`.
- GitHub prerelease https://github.com/alwansan/MO/releases/tag/mo-v0.4.0-alpha.4-run9 created, id 437174044, public (draft=false), two raw APK assets, state=uploaded:
  1. `MO-Graphics-Probe-v0.4.0-alpha.4.apk` (1,118,830 bytes) https://github.com/alwansan/MO/releases/download/mo-v0.4.0-alpha.4-run9/MO-Graphics-Probe-v0.4.0-alpha.4.apk
  2. `MO-Layer-Test-v0.1.0.apk` (17,463 bytes) https://github.com/alwansan/MO/releases/download/mo-v0.4.0-alpha.4-run9/MO-Layer-Test-v0.1.0.apk
- No ZIP Release asset. GitHub automatically provides Source code ZIP/tar links; separate from APK release assets.
- On future source/workflow pushes, building both APKs and publishing a unique experimental release is automatic after CI success. Actions Artifacts ZIP step removed.
- Debug signature remains unstable: **restore P4 debug GPU settings before uninstalling/updating MO**. No actual Outlanders FPS unlock or P4 device hook proven.
