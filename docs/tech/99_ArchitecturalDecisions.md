# Architectural Decisions

Running log of architectural decisions (ADRs). Each entry: context → decision → rationale /
consequences. Newest decisions are appended at the bottom.

---

## ADR 0001 — Gradle 9.5.1 + Quarkus 3.36.3
**Date:** 2026-06-22 · **Status:** Accepted

Quarkus 3.36 targets Gradle 9.x; Gradle 8.x is incompatible and causes hard build failures (plugin
API incompatibilities). The Gradle wrapper is pinned to **9.5.1**; the Quarkus platform BOM and
Gradle plugin are pinned to **3.36.3** (latest patch of the 3.36 stream as of mid-2026).

## ADR 0002 — Kordamp Jandex plugin for library modules
**Date:** 2026-06-22 · **Status:** Accepted

Quarkus performs bean discovery via a Jandex index. Library modules (`:business`, `:rest`) that are
not built with the Quarkus plugin produce no index by default, so Quarkus cannot discover their
`@ApplicationScoped` beans or `@Path` resources. The `org.kordamp.gradle.jandex` plugin (2.1.0) is
applied to both, generating a `META-INF/jandex.idx` that Quarkus reads at startup.

Fallback if `@Inject` fails at runtime — add to `app/src/main/resources/application.properties`:
```properties
quarkus.index-dependency.rest.group-id=ai.dame
quarkus.index-dependency.rest.artifact-id=rest
quarkus.index-dependency.business.group-id=ai.dame
quarkus.index-dependency.business.artifact-id=business
```
**Consequence:** each new backend library module must apply the Jandex plugin.

## ADR 0003 — Frontend served as static resources (no Quinoa)
**Date:** 2026-06-22 · **Status:** Accepted

The Vite `dist/` output is copied into `META-INF/resources` via a `processResources` hook in `:app`;
Quarkus serves it at `/`. Chosen over Quinoa to keep the toolchain simple and avoid Quinoa's
additional Vite-configuration constraints. **Trade-off:** `:app:quarkusDev` rebuilds the frontend
bundle before starting; live frontend hot-reload requires the Vite dev server (see
[00_DeveloperEnvironment.md](00_DeveloperEnvironment.md)).

## ADR 0004 — `/api` REST prefix
**Date:** 2026-06-22 · **Status:** Accepted

All JAX-RS resources use the `/api` prefix so REST paths never collide with SPA routes served from
`/`. **Consequence:** new endpoints must use the prefix.

## ADR 0005 — Explicit `@QuarkusMain` entry point
**Date:** 2026-06-22 · **Status:** Accepted

`:app` contains an explicit `@QuarkusMain` class `ai.dame.app.Application` delegating to
`Quarkus.run(args)`. Although Quarkus can auto-generate a main for standard JAR packaging, an
explicit Java source file is required for `./gradlew :app:quarkusDev` to treat `:app` as a Java
project in this Gradle multi-module setup.

## ADR 0006 — Integration tests via `@QuarkusIntegrationTest` / `quarkusIntTest`
**Date:** 2026-06-22 · **Status:** Accepted

Integration tests live in `app/src/integrationTest/java` and run as black-box
`@QuarkusIntegrationTest` via the plugin's `quarkusIntTest` task (builds the artifact, launches it,
tests over HTTP), wired into `:app:check`. A hand-rolled `Test` task on that source set would not
boot the Quarkus runtime. This maps the project's "integration-test folder" directive onto the
Quarkus Gradle convention.

## ADR 0007 — Test dependency versions managed by the BOM where possible
**Date:** 2026-06-22 · **Status:** Accepted

`junit-jupiter` is managed by the Quarkus BOM (declared without an explicit version). `assertj-core`
is **not** in the Quarkus BOM 3.36.3, so it stays explicitly pinned (`3.26.3`) with an inline
comment. `junit-platform-launcher` is added (unversioned, BOM-managed) so JUnit Platform tests
execute under Gradle 9.

## ADR 0008 — Frontend production code and tests in separate directories
**Date:** 2026-06-22 · **Status:** Accepted

`frontend/src` (production) and `frontend/test` (Vitest unit tests) are separate directories,
mirroring Java's `src/main` vs `src/test`. Enforced via the vitest `test.include` glob, the
`src`-only production tsconfig, and a separate `tsconfig.test.json`. Details:
[70_Testing.md](70_Testing.md).
