# Technical Documentation

Developer/technical docs for **dame-ai**, organized **by topic** as numbered Markdown files.

## Conventions

- One file per topic; the number prefix sets reading order. Gaps of 10 leave room to insert new topics.
- `99_` is the running **Architectural Decision** log — one dated entry per decision.
- Extend the matching topic file when a feature touches it — do **not** create a per-feature document.
- If a change does not fit any existing topic, **propose a new topic file** (pick an unused number in the appropriate range), add it to the table below, and mention it in the relevant pull request / iteration plan rather than forcing the content into an ill-fitting doc.

## Topics

| File | Topic |
|------|-------|
| [00_DeveloperEnvironment.md](00_DeveloperEnvironment.md) | Prerequisites, build & run commands, developer workflow |
| [10_Architecture.md](10_Architecture.md) | Module structure, dependencies, frontend integration |
| [20_DomainModel.md](20_DomainModel.md) | Domain model (checkers / Deutsche Dame) |
| [40_Api.md](40_Api.md) | HTTP/REST API surface and conventions |
| [70_Testing.md](70_Testing.md) | Test strategy, types, locations and runners |
| [99_ArchitecturalDecisions.md](99_ArchitecturalDecisions.md) | Architectural Decision Records (ADR log) |

Reserved for future topics (numbers free): `30_` (state / persistence), `50_` (frontend), `60_` (realtime / WebSocket).
