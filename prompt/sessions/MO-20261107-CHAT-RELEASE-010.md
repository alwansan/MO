# MO-20261107-CHAT-RELEASE-010
origin=chat, 2026-11-07 Asia/Baghdad
Requested: publish APKS directly in https://github.com/alwansan/MO/releases; no ZIP packaging of the APKs in user download channel.
Prior state: no releases, workflow uploaded artifacts zipped by GitHub.
Changes: workflow contents:write, build APKs (MO and owned GLES test), verify with unzip/sha256, create unique prerelease via gh release create with two raw APK attachments, idempotent rerun upload. Removed action upload-artifact ZIP step. Updated README/status/memory/restore/docs. GitHub Actions run must be checked for actual success and release attachment names/URLs.
Note: no game modified, optional GPU debugging snapshot must be restored before user deletes/updates MO.
