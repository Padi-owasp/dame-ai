# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
- **dame-ai** is a checkers (Draughts) game (german: Dame) for 2 browser based online players hosted by a web application.
- **Tech-Stack:** Quarkus 3.36 · Java 17 · React · Gradle multi-module
- **Dev-Team:** 2 Developers working in parallel. Plan Stories accordingly.

## Build & Run Commands

```bash
# Build everything (backend + frontend): compiles, unit tests, integration tests, frontend build+tests
./gradlew build

# Run the Quarkus app in dev mode (hot reload; also rebuilds frontend bundle)
./gradlew :app:quarkusDev

# Run the React frontend dev server with Vite hot-reload (proxies /api to :8080)
cd frontend && npm install && npm run dev

# Run backend unit tests only
./gradlew test

# Run backend integration tests (builds artifact, launches app, tests over HTTP)
./gradlew :app:quarkusIntTest

# Run frontend tests / Cypress e2e (e2e requires the app to be running on :8080)
cd frontend && npm test
cd frontend && npm run e2e
```

## Module Architecture

```
dame-ai/                                           (repository root)
├── app                 → :app                     Quarkus entry point
├── business            → :business                Business logic services
├── rest                → :rest                    REST controllers
├── frontend            → :frontend                React SPA
├── docs                                           Documentation for the current implementation (no gradle modules)
│   ├──glossar.md                                  Glossar
│   ├──handbook                                    End-user documentation
│   ├──tech                                        Technical/ developer doku. It is structured by topic in different md.files- example: 00_DeveloperEnvironment.md, 10_Architecture.md, 20_DomainModel.md, 40_Api.md, 99_ArchitecturalDesicions.md)
│   └──business                                    Business functionality
├── backlog                                        Features to be implemented (no gradle module)
│   └──Feature0001                                 Feature folder (can have n)
│      ├──Feature0001.md                           Feature description
│      └──UserStory0001_01.md                      UserStory 1 of Feature 1 (can have n UserStories)
├── Features_Done                                  Implemented Features and stories are moved here when done.
├── implementation-progress                        Iteration plans (5-digit-numbered .md files) (no gradle module)
└── 
```
(to be updated when progressing with the project)

## Working Directives
- Use English in Documentation, Code, Properties/Preferences, Plan and implementation-progress.
- **Work in small increments.** Always create a Plan for the next small increment — store the `iteration-plan` for each iteration in folder `implementation-progress` as a file `5DigitIterationNumber-NameOfFeature.md`. Then let it review and improve the plan. Only then start the implementation. Track the implementation progress in the plan. Mark each checklist item as done in the plan file immediately after completing it.
- **Always use TDD.** Create unit tests (in folder `test`), integration tests (in folder `integration-test` — it is an integration test as soon as other processes are involved, like file system, Mock or real servers, HTTP server, etc. A `contextLoads()` test alone is never sufficient — every implemented behavior must have a test that verifies its actual output), E2E tests (in folder `e2e`, Cypress tests for use cases).
- Prefer clean code. Short, easy to read.
- Where reasonable use patterns.
- Only add comments to explain complicated code.
- Add documentation to all public, protected, package private classes, methods and fields.
- Use existing libraries when it saves 30 or more lines of code.
- For diagrams use mermaid
- Never perform git commit. Just tell me, that you think it would be a good time to do it.
- **When reviewing:** Verify that there are
    - No cyclic dependencies between modules and packages
    - Classes are named according to specification/glossar
    - Classes are in proper packages
    - Classes are not to big
    - Clean architecture practices are followed.
    - Tests have been implemented and cover relevant functionality.
    - The handbook has been updated to reflect changes.
    - The tech docu has been updated to reflect changes and decisions.
    - The glossar has been updated when required.

## Definition of Done
A task is done when:
- The iteration-plan is marked as finished in the plan.
- Tests (Unit, Integration, E2E) are written and pass.
- The task's requirements are implemented.
- All documentation is updated
- The task has been reviewed.

