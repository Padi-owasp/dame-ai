# Feature0001 — Project Scaffolding & Build Foundation — Implementation Plan

**Status: DONE**

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking. Mark each item done in this file immediately after completing it.

**Goal:** Stand up a buildable, runnable skeleton — Quarkus multi-module backend + React frontend — wired together by one vertical slice (a `/api/health` endpoint the frontend calls and displays), with unit / integration / e2e test infrastructure in place and `./gradlew build` green.

**Architecture:** Gradle multi-module. `:business` (plain CDI bean library) ← `:rest` (JAX-RS resources, library) ← `:app` (the only Quarkus-plugin module; assembles and runs the app). `:frontend` is a separate Gradle subproject (Vite + React + TypeScript) driven via the gradle-node-plugin; its production build is served by Quarkus as static resources from `META-INF/resources`. Library modules are Jandex-indexed so Quarkus discovers their beans/resources. Dependency direction is strictly `:app → :rest → :business`; no cycles.

**Tech Stack:** Quarkus 3.36 (`quarkus-rest`, `quarkus-rest-jackson`, `quarkus-arc`), Java 17, Gradle 9.5.1 (wrapper — the version Quarkus 3.36 ships/tests against; Gradle 8.x is incompatible), JUnit 5 + AssertJ + RestAssured/Hamcrest (backend), React 19 + TypeScript + Vite 8, Vitest + Testing Library (frontend unit), Cypress (e2e).

## Global Constraints

- Java **17** for all backend modules (`sourceCompatibility`/`targetCompatibility = 17`).
- Quarkus platform & plugin version **3.36.3** (latest 3.36 patch as of mid-2026; keep BOM and plugin identical, and pin to the exact patch the team installs). From `CLAUDE.md` ("Quarkus 3.36").
- **Gradle 9.5.1** (wrapper). Quarkus 3.36 targets Gradle 9.x; Gradle 8.x causes hard build failures with this Quarkus line — do not downgrade.
- Base Java package: **`ai.dame`** (`ai.dame.business.*`, `ai.dame.rest.*`, `ai.dame.app.*`).
- REST paths are prefixed **`/api`** so they never collide with static frontend routes served from `/`.
- All documentation, code, properties and plan text in **English** (project directive).
- Test placement per project directive: unit tests in `src/test/java`; integration tests (anything touching the HTTP server / built artifact) in **`src/integrationTest/java`** run as `@QuarkusIntegrationTest` via the Quarkus `quarkusIntTest` task — this is how the project's "integration-test folder" directive maps onto the Quarkus Gradle convention (camelCase `integrationTest` is the source-set name the plugin expects); Cypress e2e under `frontend/cypress/e2e`. A `contextLoads()`-style test alone is never sufficient — every behavior gets an assertion on real output.
- **Git commits are performed by the developer, never by the executing agent** (project directive). "Commit checkpoint" steps below stage files and state the suggested message; they do not run `git commit`.
- Shell: commands are shown in Git-Bash form (`cd /c/dev/dame-ai`). In PowerShell use `cd C:\dev\dame-ai` and `./gradlew.bat`. Run Gradle commands from the repo root; `cd frontend` blocks are explicit where needed.
- Plugin/tool versions below (gradle-node-plugin 7.1.0, kordamp Jandex 2.1.0, Gradle 9.5.1, Node 20 LTS) are concrete starting points; if a version is unavailable on the build machine, pin to the nearest compatible release — the verification command in each task will reveal a mismatch.

---

## File Structure

```
dame-ai/
├── settings.gradle                  Module includes + plugin management
├── gradle.properties                Quarkus platform/plugin versions
├── build.gradle                     Root: shared backend config (group/version)
├── gradlew / gradlew.bat / gradle/  Gradle wrapper (generated)
├── business/
│   ├── build.gradle                 java-library + arc + jandex; junit5/assertj
│   └── src/
│       ├── main/java/ai/dame/business/health/HealthService.java
│       └── test/java/ai/dame/business/health/HealthServiceTest.java
├── rest/
│   ├── build.gradle                 java-library + quarkus-rest + jandex; depends :business
│   └── src/
│       ├── main/java/ai/dame/rest/health/HealthResource.java
│       ├── main/java/ai/dame/rest/health/HealthStatus.java
│       └── test/java/ai/dame/rest/health/HealthResourceTest.java
├── app/
│   ├── build.gradle                 io.quarkus plugin; depends :rest; serves frontend dist
│   └── src/
│       ├── main/resources/application.properties
│       └── integrationTest/java/ai/dame/app/HealthEndpointIT.java
├── frontend/
│   ├── build.gradle                 gradle-node-plugin: build + test tasks
│   ├── package.json                 React/Vite/Vitest/Cypress + scripts
│   ├── vite.config.ts               Vite + Vitest config, /api proxy, outDir=dist
│   ├── tsconfig.json
│   ├── index.html
│   ├── cypress.config.ts
│   ├── src/
│   │   ├── main.tsx
│   │   ├── App.tsx
│   │   ├── setupTests.ts
│   │   ├── HealthStatus.tsx
│   │   └── HealthStatus.test.tsx
│   └── cypress/e2e/health.cy.ts
└── docs/
    ├── glossar.md                   (stub created here, filled from Feature0002 on)
    └── tech/0001-build-and-structure.md   Architecture decision record for this slice
```

---

## Task 1: Gradle multi-module skeleton

**Files:**
- Create: `settings.gradle`, `gradle.properties`, `build.gradle`
- Create (generated): `gradlew`, `gradlew.bat`, `gradle/wrapper/*`
- Create (empty for now): `business/build.gradle`, `rest/build.gradle`, `frontend/build.gradle`

**Interfaces:**
- Produces: four Gradle subprojects `:business`, `:rest`, `:app`, `:frontend`; `:app` declared in a later step. The Quarkus platform version lives in `gradle.properties` as `quarkusPlatformVersion`.

- [ ] **Step 1: Generate the Gradle wrapper**

Run (requires a local Gradle 9.x; install via SDKMAN `sdk install gradle 9.5.1` or use the Quarkus CLI if absent):

```bash
cd /c/dev/dame-ai
gradle wrapper --gradle-version 9.5.1
```
Verify: `./gradlew --version` reports Gradle 9.5.1.
Expected: creates `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`, `gradle/wrapper/gradle-wrapper.properties`.

- [ ] **Step 2: Write `gradle.properties`**

```properties
quarkusPlatformGroupId=io.quarkus.platform
quarkusPlatformArtifactId=quarkus-bom
quarkusPlatformVersion=3.36.3
quarkusPluginId=io.quarkus
quarkusPluginVersion=3.36.3
org.gradle.parallel=true
org.gradle.caching=true
```

- [ ] **Step 3: Write `settings.gradle`**

```groovy
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id "${quarkusPluginId}" version "${quarkusPluginVersion}"
    }
}

rootProject.name = 'dame-ai'
include 'business', 'rest', 'app', 'frontend'
```

- [ ] **Step 4: Write root `build.gradle`**

```groovy
// Root project: shared coordinates only. Each module applies its own plugins.
allprojects {
    group = 'ai.dame'
    version = '0.1.0-SNAPSHOT'
}
```

- [ ] **Step 5: Create the module directories so the projects resolve**

`business/build.gradle`, `rest/build.gradle` — create each as an empty file for now (filled in later tasks). `app/build.gradle` is created in Task 4. For `frontend`, create an **empty directory** only (e.g. `mkdir frontend`) — do **not** add a `build.gradle` or any file yet, so the Vite scaffold in Task 5 runs into an empty directory without an interactive "directory not empty" prompt. A subproject directory with no build script is valid (it just has no tasks).

- [ ] **Step 6: Write `.gitignore`**

```gitignore
# Build output
build/
**/build/
.gradle/
.quarkus/

# Frontend
node_modules/
frontend/dist/

# Cypress artifacts
frontend/cypress/videos/
frontend/cypress/screenshots/

# IDE / OS
.idea/
*.iml
.DS_Store
```

- [ ] **Step 7: Verify the multi-module layout resolves**

Run: `./gradlew projects`
Expected: output lists `Project ':business'`, `Project ':rest'`, `Project ':app'`, `Project ':frontend'` with no configuration errors.

- [ ] **Step 8: Commit checkpoint**

Good point to commit. Stage and report — the developer performs the commit (project directive: the agent never runs `git commit`):
```bash
git add settings.gradle gradle.properties build.gradle gradlew gradlew.bat gradle .gitignore
# suggested message: "build: gradle multi-module skeleton with wrapper"
```
(`frontend/` is empty and not yet tracked — it is added in Task 5.)

---

## Task 2: `:business` HealthService (TDD)

**Files:**
- Create: `business/src/main/java/ai/dame/business/health/HealthService.java`
- Test: `business/src/test/java/ai/dame/business/health/HealthServiceTest.java`
- Modify: `business/build.gradle`

**Interfaces:**
- Produces: `ai.dame.business.health.HealthService` — `@ApplicationScoped` bean with `String status()` returning `"UP"`. Consumed by `:rest`.

- [ ] **Step 1: Write `business/build.gradle`**

```groovy
plugins {
    id 'java-library'
    id 'org.kordamp.gradle.jandex' version '2.1.0' // index beans for Quarkus discovery
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories { mavenCentral() }

dependencies {
    implementation enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}")
    implementation 'io.quarkus:quarkus-arc' // CDI annotations (@ApplicationScoped)

    testImplementation 'org.junit.jupiter:junit-jupiter:5.11.3'
    testImplementation 'org.assertj:assertj-core:3.26.3'
}

tasks.named('test') { useJUnitPlatform() }
```

- [ ] **Step 2: Write the failing test**

`business/src/test/java/ai/dame/business/health/HealthServiceTest.java`
```java
package ai.dame.business.health;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HealthServiceTest {

    @Test
    void statusIsUp() {
        assertThat(new HealthService().status()).isEqualTo("UP");
    }
}
```

- [ ] **Step 3: Run the test to verify it fails**

Run: `./gradlew :business:test`
Expected: FAIL — compilation error, `HealthService` does not exist.

- [ ] **Step 4: Write the minimal implementation**

`business/src/main/java/ai/dame/business/health/HealthService.java`
```java
package ai.dame.business.health;

import jakarta.enterprise.context.ApplicationScoped;

/** Provides the application's health status for monitoring and the UI smoke check. */
@ApplicationScoped
public class HealthService {

    /**
     * Returns the current health token.
     *
     * @return {@code "UP"} when the service is operational.
     */
    public String status() {
        return "UP";
    }
}
```

- [ ] **Step 5: Run the test to verify it passes**

Run: `./gradlew :business:test`
Expected: PASS (1 test).

- [ ] **Step 6: Commit checkpoint**

Good point to commit (developer performs it):
```bash
git add business
# suggested message: "feat(business): add HealthService returning UP"
```

---

## Task 3: `:rest` HealthResource (TDD)

**Files:**
- Create: `rest/src/main/java/ai/dame/rest/health/HealthStatus.java`
- Create: `rest/src/main/java/ai/dame/rest/health/HealthResource.java`
- Test: `rest/src/test/java/ai/dame/rest/health/HealthResourceTest.java`
- Modify: `rest/build.gradle`

**Interfaces:**
- Consumes: `ai.dame.business.health.HealthService.status()` from Task 2.
- Produces: `GET /api/health` (JAX-RS) returning `HealthStatus(String status)` serialized as `{"status":"UP"}`. `HealthResource` has a package-private field `HealthService healthService` (field injection, settable from same-package tests).

- [ ] **Step 1: Write `rest/build.gradle`**

```groovy
plugins {
    id 'java-library'
    id 'org.kordamp.gradle.jandex' version '2.1.0'
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories { mavenCentral() }

dependencies {
    implementation enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}")
    api project(':business')
    implementation 'io.quarkus:quarkus-rest'
    implementation 'io.quarkus:quarkus-rest-jackson'

    testImplementation 'org.junit.jupiter:junit-jupiter:5.11.3'
    testImplementation 'org.assertj:assertj-core:3.26.3'
}

tasks.named('test') { useJUnitPlatform() }
```

- [ ] **Step 2: Write the failing test**

`rest/src/test/java/ai/dame/rest/health/HealthResourceTest.java`
```java
package ai.dame.rest.health;

import static org.assertj.core.api.Assertions.assertThat;

import ai.dame.business.health.HealthService;
import org.junit.jupiter.api.Test;

class HealthResourceTest {

    @Test
    void healthReturnsServiceStatus() {
        HealthResource resource = new HealthResource();
        resource.healthService = new HealthService();

        assertThat(resource.health().status()).isEqualTo("UP");
    }
}
```

- [ ] **Step 3: Run the test to verify it fails**

Run: `./gradlew :rest:test`
Expected: FAIL — `HealthResource` / `HealthStatus` do not exist.

- [ ] **Step 4: Write the minimal implementation**

`rest/src/main/java/ai/dame/rest/health/HealthStatus.java`
```java
package ai.dame.rest.health;

/**
 * Health response payload.
 *
 * @param status health token, e.g. {@code "UP"}.
 */
public record HealthStatus(String status) {}
```

`rest/src/main/java/ai/dame/rest/health/HealthResource.java`
```java
package ai.dame.rest.health;

import ai.dame.business.health.HealthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/** REST endpoint exposing the application health status under {@code /api/health}. */
@Path("/api/health")
public class HealthResource {

    /** Domain service providing the status token. */
    @Inject
    HealthService healthService;

    /**
     * Reports application health.
     *
     * @return a {@link HealthStatus} serialized as {@code {"status":"UP"}}.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public HealthStatus health() {
        return new HealthStatus(healthService.status());
    }
}
```

- [ ] **Step 5: Run the test to verify it passes**

Run: `./gradlew :rest:test`
Expected: PASS (1 test).

- [ ] **Step 6: Commit checkpoint**

Good point to commit (developer performs it):
```bash
git add rest
# suggested message: "feat(rest): add /api/health resource backed by HealthService"
```

---

## Task 4: `:app` Quarkus assembly + integration test

**Files:**
- Create: `app/build.gradle`
- Create: `app/src/main/resources/application.properties`
- Test: `app/src/integrationTest/java/ai/dame/app/HealthEndpointIT.java`

**Interfaces:**
- Consumes: `:rest` (which transitively brings `:business`).
- Produces: a runnable Quarkus application serving `GET /api/health` over HTTP on port 8080, and `@QuarkusIntegrationTest` coverage run via the plugin's `quarkusIntTest` task (wired into `check`).

- [ ] **Step 1: Write `app/build.gradle`**

```groovy
plugins {
    id 'java'
    id 'io.quarkus'
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories { mavenCentral() }

dependencies {
    implementation enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}")
    implementation project(':rest')
    implementation 'io.quarkus:quarkus-rest'
    implementation 'io.quarkus:quarkus-rest-jackson'
    implementation 'io.quarkus:quarkus-arc'

    testImplementation 'io.quarkus:quarkus-junit5'
    testImplementation 'io.rest-assured:rest-assured'
}

// Integration tests live in src/integrationTest/java and run black-box as
// @QuarkusIntegrationTest via the plugin-provided quarkusIntTest task (which depends on
// quarkusBuild). Do NOT register a hand-rolled Test task — that would bypass the Quarkus
// augmentation and the runtime would never start.
sourceSets {
    integrationTest {
        compileClasspath += sourceSets.main.output + sourceSets.test.output
        runtimeClasspath += sourceSets.main.output + sourceSets.test.output
    }
}

configurations {
    integrationTestImplementation.extendsFrom testImplementation
    integrationTestRuntimeOnly.extendsFrom testRuntimeOnly
}

tasks.named('test') { useJUnitPlatform() }
tasks.named('check') { dependsOn 'quarkusIntTest' }
```

The Quarkus Gradle plugin contributes the `quarkusIntTest` task and consumes the
`integrationTest` source set (`src/integrationTest/java`). `@QuarkusIntegrationTest` runs
**black-box against the built artifact** — it cannot `@Inject` beans, but RestAssured calls
over HTTP work, which is exactly what this health test needs.

- [ ] **Step 2: Write `application.properties`**

`app/src/main/resources/application.properties`
```properties
quarkus.http.port=8080
quarkus.application.name=dame-ai
```

- [ ] **Step 3: Write the integration test**

`app/src/integrationTest/java/ai/dame/app/HealthEndpointIT.java`
```java
package ai.dame.app;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
class HealthEndpointIT {

    @Test
    void healthEndpointReturnsUp() {
        given()
            .when().get("/api/health")
            .then()
            .statusCode(200)
            .body("status", is("UP"));
    }
}
```

- [ ] **Step 4: Run the integration test**

Run: `./gradlew :app:quarkusIntTest`
This builds the application artifact (via `quarkusBuild`) and launches it black-box.
Expected: PASS — the running app serves `/api/health` with 200 and `status == "UP"`.
If it returns **404**, the `:rest` beans were not indexed — confirm the kordamp Jandex plugin
is applied in `:business` and `:rest` (Tasks 2/3) and re-run. Fallback: add to
`application.properties`:
```properties
quarkus.index-dependency.rest.group-id=ai.dame
quarkus.index-dependency.rest.artifact-id=rest
quarkus.index-dependency.business.group-id=ai.dame
quarkus.index-dependency.business.artifact-id=business
```

- [ ] **Step 5: Smoke-run the app manually (optional sanity)**

Run: `./gradlew :app:quarkusDev` then in another shell `curl http://localhost:8080/api/health`
Expected: `{"status":"UP"}`. Stop with Ctrl+C.

- [ ] **Step 6: Commit checkpoint**

Good point to commit (developer performs it):
```bash
git add app
# suggested message: "feat(app): assemble Quarkus app and integration-test /api/health"
```

---

## Task 5: `:frontend` scaffold + HealthStatus component (TDD)

**Files:**
- Create: `frontend/package.json`, `frontend/tsconfig.json`, `frontend/index.html`, `frontend/vite.config.ts`
- Create: `frontend/src/main.tsx`, `frontend/src/App.tsx`, `frontend/src/setupTests.ts`
- Create: `frontend/src/HealthStatus.tsx`
- Test: `frontend/src/HealthStatus.test.tsx`
- Modify: `frontend/build.gradle`

**Interfaces:**
- Produces: a `HealthStatus` React component that fetches `GET /api/health` and renders `Backend status: <status>`; a Vite production build emitting to `frontend/dist`; npm scripts `dev`, `build`, `test`, `e2e`; Gradle tasks `assembleFrontend` (vite build) and `testFrontend` (vitest) wired into `:frontend` `assemble`/`check`.

- [ ] **Step 1: Scaffold the Vite React TS app and add test deps**

Run:
```bash
cd /c/dev/dame-ai
npm create vite@latest frontend -- --template react-ts
cd frontend
npm install
npm install -D vitest @testing-library/react @testing-library/jest-dom jsdom cypress
```
The `frontend/` directory created in Task 1 is empty, so create-vite scaffolds without a
"directory not empty" prompt. If you do get the prompt (e.g. a stray file is present), choose
"Ignore files and continue" — never "Remove existing files".

- [ ] **Step 2: Write `vite.config.ts`**

```ts
/// <reference types="vitest" />
import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  build: { outDir: 'dist' },
  server: {
    port: 5173,
    proxy: { '/api': 'http://localhost:8080' },
  },
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: './src/setupTests.ts',
  },
})
```

- [ ] **Step 3: Write `src/setupTests.ts` and add test scripts to `package.json`**

`src/setupTests.ts`
```ts
import '@testing-library/jest-dom'
```

In `package.json`, set the `scripts` block to:
```json
{
  "scripts": {
    "dev": "vite",
    "build": "tsc -b && vite build",
    "preview": "vite preview",
    "test": "vitest run",
    "test:watch": "vitest",
    "e2e": "cypress run"
  }
}
```

- [ ] **Step 4: Write the failing component test**

`frontend/src/HealthStatus.test.tsx`
```tsx
import { render, screen } from '@testing-library/react'
import { beforeEach, expect, test, vi } from 'vitest'
import HealthStatus from './HealthStatus'

beforeEach(() => {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
    ok: true,
    json: async () => ({ status: 'UP' }),
  }))
})

test('renders the backend status returned by /api/health', async () => {
  render(<HealthStatus />)
  expect(await screen.findByText('Backend status: UP')).toBeInTheDocument()
})
```

- [ ] **Step 5: Run the test to verify it fails**

Run: `cd frontend && npm test`
Expected: FAIL — cannot resolve `./HealthStatus`.

- [ ] **Step 6: Write the minimal component**

`frontend/src/HealthStatus.tsx`
```tsx
import { useEffect, useState } from 'react'

/** Fetches the backend health status from /api/health and displays it. */
export default function HealthStatus() {
  const [status, setStatus] = useState<string>('…')

  useEffect(() => {
    fetch('/api/health')
      .then((response) => response.json())
      .then((data: { status: string }) => setStatus(data.status))
      .catch(() => setStatus('unreachable'))
  }, [])

  return <p>Backend status: {status}</p>
}
```

- [ ] **Step 7: Wire the component into the app shell**

`frontend/src/App.tsx`
```tsx
import HealthStatus from './HealthStatus'

/** Application root shell. */
export default function App() {
  return (
    <main>
      <h1>dame-ai</h1>
      <HealthStatus />
    </main>
  )
}
```
(Leave `src/main.tsx` as generated by Vite — it renders `<App />` into `#root`.)

- [ ] **Step 8: Run the test to verify it passes**

Run: `cd frontend && npm test`
Expected: PASS (1 test).

- [ ] **Step 9: Write `frontend/build.gradle` (gradle-node-plugin)**

```groovy
plugins {
    id 'base'
    id 'com.github.node-gradle.node' version '7.1.0'
}

node {
    version = '20.18.0'
    download = true
}

tasks.register('assembleFrontend', com.github.gradle.node.npm.task.NpmTask) {
    dependsOn tasks.named('npmInstall')
    args = ['run', 'build']
    inputs.dir('src')
    inputs.files('package.json', 'vite.config.ts', 'tsconfig.json', 'index.html')
    outputs.dir(layout.projectDirectory.dir('dist'))
}

tasks.register('testFrontend', com.github.gradle.node.npm.task.NpmTask) {
    dependsOn tasks.named('npmInstall')
    args = ['run', 'test']
}

tasks.named('assemble') { dependsOn 'assembleFrontend' }
tasks.named('check') { dependsOn 'testFrontend' }
```

- [ ] **Step 10: Verify the Gradle frontend build and tests run**

Run: `./gradlew :frontend:assembleFrontend :frontend:testFrontend`
Expected: `frontend/dist` is produced; vitest reports 1 passing test.

- [ ] **Step 11: Commit checkpoint**

Good point to commit (developer performs it):
```bash
git add frontend
# suggested message: "feat(frontend): React shell with HealthStatus component and Gradle node build"
```

---

## Task 6: Wire frontend into Quarkus + Cypress e2e

**Files:**
- Modify: `app/build.gradle` (serve `frontend/dist` as static resources)
- Create: `frontend/cypress.config.ts`
- Test: `frontend/cypress/e2e/health.cy.ts`

**Interfaces:**
- Consumes: `:frontend` `assembleFrontend` output (`frontend/dist`) and the `:app` `/api/health` endpoint.
- Produces: the Quarkus app serving the React build at `/`; a Cypress e2e proving the full slice (frontend loads, calls backend, shows `Backend status: UP`).

- [ ] **Step 1: Serve the built frontend from `:app`**

Append to `app/build.gradle`:
```groovy
// Bundle the built React app so Quarkus serves it from "/".
// Static files under META-INF/resources are served at "/" by Quarkus.
tasks.named('processResources') {
    dependsOn ':frontend:assembleFrontend'
    from(project(':frontend').layout.projectDirectory.dir('dist')) {
        into 'META-INF/resources'
    }
}
// Ensure the bundle is in place before Quarkus reads resources (dev run and packaging).
tasks.named('quarkusDev') { dependsOn 'processResources' }
tasks.named('quarkusBuild') { dependsOn 'processResources' }
```

- [ ] **Step 2: Verify the static bundle is served**

Run: `./gradlew :app:quarkusDev` then `curl -s http://localhost:8080/ | grep -o '<title>.*</title>'`
Expected: the Vite `index.html` title is returned (frontend served by Quarkus). Stop with Ctrl+C.

- [ ] **Step 3: Write `cypress.config.ts`**

`frontend/cypress.config.ts`
```ts
import { defineConfig } from 'cypress'

export default defineConfig({
  e2e: {
    baseUrl: 'http://localhost:8080',
    supportFile: false,
  },
})
```

- [ ] **Step 4: Write the e2e test**

`frontend/cypress/e2e/health.cy.ts`
```ts
describe('health vertical slice', () => {
  it('loads the app and shows the backend status from /api/health', () => {
    cy.visit('/')
    cy.contains('h1', 'dame-ai')
    cy.contains('Backend status: UP')
  })
})
```

- [ ] **Step 5: Run the e2e against the running app**

In terminal 1: `./gradlew :app:quarkusDev` (serves frontend + API on 8080).
In terminal 2:
```bash
cd frontend && npx cypress run --spec cypress/e2e/health.cy.ts
```
Expected: 1 passing e2e spec; `Backend status: UP` is asserted in a real browser against the running Quarkus app. Stop the app afterwards.

- [ ] **Step 6: Commit checkpoint**

Good point to commit (developer performs it):
```bash
git add app frontend/cypress.config.ts frontend/cypress
# suggested message: "feat(app): serve React build as static resources + Cypress health e2e"
```

---

## Task 7: Documentation + full green build + finish plan

**Files:**
- Create: `docs/tech/0001-build-and-structure.md`
- Create: `docs/glossar.md` (stub)
- Modify: `.claude/CLAUDE.md` (only if build commands drifted from reality)
- Modify: this plan file (mark all items done)

- [x] **Step 1: Write the architecture decision record**

`docs/tech/0001-build-and-structure.md` — record: the module graph (`:app → :rest → :business`, `:frontend` separate), why library modules use the Jandex plugin, that `:frontend` is a gradle-node-plugin subproject whose `dist` is served by Quarkus as static resources (no Quinoa), the `/api` prefix convention, dev workflow (Vite dev server on 5173 proxying `/api` to 8080, or `quarkusDev` serving the bundled build), the **Gradle 9.5.1 / Quarkus 3.36 pairing** (8.x is incompatible), and the test-folder convention — unit in `src/test/java`, integration as `@QuarkusIntegrationTest` in `src/integrationTest/java` run via `quarkusIntTest` (mapping the directive's "integration-test" concept onto the Quarkus convention), Cypress e2e in `frontend/cypress/e2e`. Use a mermaid module diagram.

- [x] **Step 2: Create the glossar stub**

`docs/glossar.md` — heading plus a note that domain terms (Stein/Man, Dame/King, Schlagzwang, …) are added from Feature0002 onward. Keeps the documented docs structure real.

- [x] **Step 3: Run the complete build**

Run: `./gradlew clean build`
Expected: SUCCESS — compiles all backend modules, builds the frontend (`assembleFrontend`), runs backend unit tests, `:app:quarkusIntTest`, and `:frontend:testFrontend`, all green. (Cypress e2e is run separately per Task 6 Step 5, as it needs the app server running; note this in the ADR.)

- [x] **Step 4: Confirm no module cycles and correct dependency direction**

Run: `./gradlew :app:dependencies --configuration runtimeClasspath`
Expected: `:app` depends on `:rest` depends on `:business`; `:business` depends on neither; `:frontend` appears in no backend classpath.

- [x] **Step 5: Reconcile `CLAUDE.md` build commands with reality**

Verify the commands in `.claude/CLAUDE.md` ("Build & Run Commands") match what now works (`./gradlew build`, `./gradlew :app:quarkusDev`, frontend `npm run dev` / `npm test`, Cypress run). Fix any drift.

- [x] **Step 6: Mark this plan finished — commit checkpoint**

Tick every checkbox in this file, add a `**Status: DONE**` line under the title. Then (developer performs the commit):
```bash
git add docs .claude/CLAUDE.md implementation-progress/00001-project-scaffolding.md
# suggested message: "docs: record build/structure decisions and finish Feature0001 scaffolding"
```

---

## Definition of Done (Feature0001)

- [x] `./gradlew clean build` is green (backend unit + integration tests, frontend build + unit tests).
- [x] Cypress e2e passes against the running app (Task 6 Step 5 — verified in Task 6).
- [x] Quarkus dev mode serves the React shell, which displays the backend status from `/api/health`.
- [x] No module cycles; dependency direction `:app → :rest → :business` verified.
- [x] `docs/tech/0001-build-and-structure.md` and `docs/glossar.md` exist; `CLAUDE.md` build commands are accurate.
- [x] This plan is marked finished.

## Self-Review Notes

- **Spec coverage (Feature0001):** multi-module Gradle (Task 1), Quarkus/Java 17 bootstrap (Task 4), React shell (Task 5), vertical slice `/api/health` proven by integration + e2e (Tasks 4 & 6), test scaffolding for unit/integration/e2e (Tasks 2–6), docs/tech entry + CLAUDE.md reconciliation (Task 7), no cycles + dependency direction (Task 7 Step 4). All covered.
- **Type consistency:** `HealthService.status()` → `"UP"` (Task 2) is consumed unchanged by `HealthResource` (Task 3), asserted over HTTP as `status == "UP"` (Task 4), rendered as `Backend status: UP` by `HealthStatus` (Task 5), and asserted in Cypress (Task 6). Consistent end to end.
- **Known execution risks (not placeholders — concrete fallbacks given):** exact Quarkus 3.36 patch (3.36.3 at time of writing) and the gradle-node-plugin / Jandex plugin versions must match what is published; the wrapper must be Gradle 9.5.1 (8.x breaks with Quarkus 3.36); multi-module Quarkus bean discovery depends on the Jandex plugin being applied in `:business` and `:rest` — verified in Task 4 Step 4 with a documented `quarkus.index-dependency` fallback if `@Inject` fails; integration tests use the plugin's `quarkusIntTest` task with `@QuarkusIntegrationTest` (black-box, no `@Inject`) — a hand-rolled `Test` task would not boot the Quarkus runtime.
- **Revision note:** this plan was revised after a sourced technical review. Key corrections vs. the first draft: Gradle 8.11.1 → **9.5.1**; the custom `integrationTest` `Test` task was replaced by `@QuarkusIntegrationTest` + `quarkusIntTest` in `src/integrationTest/java`; Quarkus pinned to 3.36.3; frontend scaffolding reordered so Vite runs into an empty directory; `processResources` cross-project copy hardened; commit steps changed to "commit checkpoints" per the project's no-auto-commit directive.
