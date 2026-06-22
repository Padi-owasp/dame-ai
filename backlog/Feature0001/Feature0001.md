# Feature0001 — Project Scaffolding & Build Foundation

**Status:** Planned
**Depends on:** —
**Modules:** `:app`, `:business`, `:rest`, `:frontend` (all)
**Owner / parallelization:** Both developers together; foundation for everything else.

## Goal

A buildable, runnable skeleton of the whole system: a Quarkus multi-module Gradle
backend and a React frontend, wired together, with a green build and the test
infrastructure (unit / integration / e2e) in place.

## Scope (in)

- Gradle multi-module setup: `:app`, `:business`, `:rest`, `:frontend`.
- Quarkus 3.36 / Java 17 bootstrap in `:app`; `:business` and `:rest` as libraries.
- React SPA shell in `:frontend`, built and served by/alongside the Quarkus app.
- A trivial end-to-end vertical slice to prove the wiring: e.g. a `/health` (or similar)
  REST endpoint that the frontend calls and displays.
- Test scaffolding: unit (`test`), integration (`integration-test`), e2e (`e2e`,
  Cypress) folders with one passing example test each.
- Initial `docs/tech` entry recording the chosen project structure and build commands;
  update `CLAUDE.md` build/run commands to match reality (Quarkus, not Spring Boot).

## Out of scope

- Any game logic, board, sessions, or WebSocket.
- Persistence, accounts.

## Key requirements / acceptance criteria

- `./gradlew build` builds backend + frontend and runs all tests green.
- The Quarkus app starts in dev mode and serves the React shell.
- The frontend successfully calls the sample REST endpoint and renders its response
  (proven by an integration/e2e test, not only a `contextLoads()`).
- No cyclic dependencies between modules; dependency direction is
  `:app → :rest → :business` (frontend separate).

## Notes / decisions

- Resolve the **React vs Angular** contradiction in `CLAUDE.md` before starting; this
  plan assumes React.
- Agree on how the frontend is delivered (Quarkus Quinoa, static resources, or separate
  dev server with proxy) and record it in `docs/tech`.
