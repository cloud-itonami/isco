# hermes/ — the resident bots that act for this repository

This directory is the **source of truth** for the Hermes profiles listed below
(ADR-2609241200). The host's `~/.hermes/profiles/<profile>` is materialized
from `hermes/profiles/<profile>/` and checked against it:

```
kbb --backend sci scripts/hermes-profile-repo.cljk materialize <profile>   # repo -> host
kbb --backend sci scripts/hermes-profile-repo.cljk check <profile>         # 0 agree / 1 drift / 2 could not compare
kbb --backend sci scripts/hermes-profile-repo.cljk export <profile>        # host -> repo, then commit
```

(run from the com-junkawasaki/root superproject; registry
`manifest/hermes-profile-repos.edn`.)

Each profile directory holds SOUL.md, profile.yaml, config.yaml (host-local
blocks removed), cron/jobs.json (definitions only), scripts/ and the skills the
profile owns. **Never here:** `.env` or any secret value, workspace/ledgers,
sessions, memories, logs, caches, run state.

## Profiles

| profile | description |
|---|---|
| `itonami-isco-1` | itonami ISCO major 1 Managers occupation bot |
| `itonami-isco-2` | itonami ISCO major 2 Professionals occupation bot |
| `itonami-isco-3` | itonami ISCO major 3 Technicians and Associate Professionals occupation bot |
| `itonami-isco-4` | itonami ISCO major 4 Clerical Support Workers occupation bot\ndescription_auto: false |
| `itonami-isco-5` | itonami ISCO major 5 Services And Sales Workers occupation bot |
| `itonami-isco-6` | itonami ISCO major 6 Skilled Agricultural, Forestry and Fishery Workers occupation bot |
| `itonami-isco-7` | itonami ISCO major 7 Craft and Related Trades Workers occupation bot |
| `itonami-isco-8` | itonami ISCO major 8 Plant and Machine Operators and Assemblers occupation bot |
| `itonami-isco-9` | itonami ISCO major 9 Elementary Occupations occupation bot |
