# dame-ai — frontend

React + TypeScript + Vite SPA for the dame-ai checkers game.

Part of the dame-ai Gradle multi-module project. Built and served by the Quarkus
backend (`:app`); see `docs/tech/0001-build-and-structure.md` for the build and dev workflow.

## Scripts
- `npm run dev` — Vite dev server (proxies `/api` to the backend on :8080)
- `npm run build` — production build to `dist/`
- `npm test` — Vitest unit tests
- `npm run e2e` — Cypress end-to-end tests
