# Developer Environment

How to set up, build, run and develop dame-ai locally.

## Prerequisites

- **Java 17** — the project toolchain.
- **Node.js 20.19+ / npm** — for the frontend. The Gradle build downloads its own Node via the
  gradle-node-plugin, but running `npm`/`npx` directly requires Node on the `PATH`.
- **Gradle** — *not* required globally. The repository ships a Gradle **wrapper** pinned to
  **9.5.1**; always use `./gradlew` (Git-Bash) or `.\gradlew.bat` (PowerShell). Gradle 8.x is
  incompatible with Quarkus 3.36 (see [99_ArchitecturalDecisions.md](99_ArchitecturalDecisions.md)).

## Build & run

```bash
# Build everything: backend compile + unit tests, the integration test, frontend build + unit tests
./gradlew build

# Run the app (builds the frontend bundle, then serves API + SPA on http://localhost:8080)
./gradlew :app:quarkusDev
```

## Developer workflow

### Option A — Full-stack dev with live frontend hot-reload

```
Terminal 1: ./gradlew :app:quarkusDev                  # Quarkus on :8080; API hot-reloads
Terminal 2: cd frontend && npm install && npm run dev  # Vite dev server on :5173
```

Vite proxies `/api/*` requests to `http://localhost:8080` (configured in `vite.config.ts`).
Open `http://localhost:5173` in the browser.

### Option B — Bundled preview (frontend served by Quarkus)

```
./gradlew :app:quarkusDev   # builds the frontend dist and serves everything at http://localhost:8080
```

For test commands and conventions, see [70_Testing.md](70_Testing.md).
