# Testing

## Test types, locations and runners

| Test type | Location | Runner |
|-----------|----------|--------|
| Backend unit | `<module>/src/test/java/` | `./gradlew test` |
| Backend integration | `app/src/integrationTest/java/` (`@QuarkusIntegrationTest`) | `./gradlew :app:quarkusIntTest` |
| Frontend unit | `frontend/test/**/*.test.tsx` (Vitest) | `./gradlew :frontend:testFrontend` or `cd frontend && npm test` |
| Frontend test type-check | `frontend/test` + `frontend/src` via `tsconfig.test.json` | `cd frontend && npm run test:types` |
| E2E | `frontend/cypress/e2e/` (Cypress) | `cd frontend && npm run e2e` (needs the app running) |

`./gradlew build` runs the backend unit tests, the `@QuarkusIntegrationTest` suite (via
`quarkusIntTest`, wired into `:app:check`) and the frontend unit tests. Cypress e2e is **not** part
of `./gradlew build` because it needs a running server:

```
1. Start: ./gradlew :app:quarkusDev
2. Run:   cd frontend && npm run e2e
```

A `contextLoads()`-style test alone is never sufficient — every behavior must be asserted against
real output.

## Integration tests are black-box (`@QuarkusIntegrationTest`)

Integration tests live in `app/src/integrationTest/java` and run via the Quarkus `quarkusIntTest`
task, which builds the application artifact (`quarkusBuild`), launches it as a process, and runs the
tests against it over HTTP. A hand-rolled `Test` task on that source set would **not** boot the
Quarkus runtime and must not be used. (ADR 0006 in
[99_ArchitecturalDecisions.md](99_ArchitecturalDecisions.md).)

## Frontend: production code and tests are separated

Production code lives in `frontend/src`, unit tests in `frontend/test` — mirroring Java's
`src/main` vs `src/test`. The separation is enforced mechanically, not only by convention:

- `vite.config.ts` sets `test.include: ['test/**/*.{test,spec}.{ts,tsx}']`, so Vitest only discovers
  tests under `frontend/test/`; a test accidentally placed in `src/` simply will not run, which
  surfaces the mistake.
- The production build (`tsc -b` via `tsconfig.app.json`, whose `include` is `["src"]`) compiles and
  bundles only `src/`; tests never enter the production artifact.
- `tsconfig.test.json` type-checks the tests (plus the `src` they import) separately
  (`npm run test:types`).
