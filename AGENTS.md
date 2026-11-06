# AGENTS.md — MO agents' operating contract

1. Read `prompt/RESTORE_AR.md`, `prompt/MEMORY.md`, `prompt/STATUS.md`, `prompt/PLAN_AR.md`, `docs/LEGAL_AND_SECURITY.md` BEFORE acting.
2. Respect owner's immutable limits: **no root, no bootloader unlocking, no kernel flashing, no Zygisk, no PC dependency**, Shizuku as sole optional privileged broker, POCO F5 Pro Android 15.
3. Respect phase: currently **PLANNING ONLY**. Do not create source builds, binaries, releases, or Android settings changes until phase explicitly changes.
4. Source truth: live GitHub files + primary vendor docs + device test evidence. Separate observed, documented, suspected and unverified. Never invent hardware capabilities or successful actions.
5. Never label a ZIP 'installed' merely because it was extracted. Root-module install is unavailable without root infrastructure; report missing permissions candidly.
6. Do not run third-party ZIP shell scripts, native binaries, updater commands, or network-supplied executable code. Treat archives as untrusted.
7. Avoid reverse engineering, modifying, or incorporating iUnlockerGL proprietary components; see license. KernelSU GPLv3 is a distinct dependency and must not be copied unnecessarily.
8. Keep changes narrow; before writes inspect current repo HEAD and files, write to the requested branch, use non-destructive commits, verify the result.
9. Each work session: create a unique session_id; update `prompt/STATUS.md`, `prompt/MEMORY.md` and a log under `prompt/sessions/`; include evidence, decisions, blockers, next steps.
10. Language: user communication and restore in Arabic (Iraqi Arabic is fine); technical terms and code may remain English. Never solicit secrets or personally identifying info.
11. Do not promise automatic restart of Shizuku after every reboot. Wireless debugging on non-root devices ordinarily requires manual start again.
12. Acceptance always includes clean rollback for any optional change, explicit confirmation for writes, and tests against stock device without elevated privilege.
