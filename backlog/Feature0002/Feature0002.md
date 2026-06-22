# Feature0002 — Checkers Rules Engine (Deutsche Dame)

**Status:** Planned
**Depends on:** Feature0001
**Modules:** `:business`
**Owner / parallelization:** Dev A. Pure logic, no I/O — ideal for parallel work and heavy TDD.

## Goal

A self-contained, fully tested domain model and rules engine for **Deutsche Dame** that is
the single source of truth for what is legal. No networking, no persistence, no UI.

## Scope (in)

- Domain model: `Board` (8×8, 32 dark squares), `Square`/position, `Piece` (color, man vs
  Dame), player color, immutable `GameState` with side-to-move.
- Initial setup: 12 men per player on the dark squares of the first three rows.
- Legal move generation:
  - Men move one square diagonally forward.
  - Men capture forward only; capture jumps a single adjacent enemy into the empty square
    beyond.
  - Dame (flying king) slides any distance diagonally in all directions, and captures a
    single enemy from a distance, landing on any empty square beyond it.
  - **Schlagzwang**: if any capture exists, only captures are legal.
  - **Multi-capture**: a started capture must continue while further captures are possible
    from the landing square.
- Move application producing a new `GameState`; **promotion** to Dame on reaching the back
  row.
- Game-end detection: win when the side to move has no legal move; draw by agreement
  (engine exposes the state needed for the UI/session layer to offer/accept a draw).

## Out of scope

- Turn ownership across two network players, sessions, time (handled in 0003/0004).
- Persistence, WebSocket, REST.
- No maximal-capture (Mehrheitsschlag) rule — not used in this variant.

## Key requirements / acceptance criteria

- Comprehensive unit tests covering: legal/illegal simple moves, forced single and
  multi-captures, men-cannot-capture-backward, promotion (including landing on back row),
  flying-king movement and distance capture, win/no-move detection.
- The engine never allows an illegal transition; given a state it can enumerate exactly the
  legal moves.
- Deterministic and side-effect free (same input → same output); no dependency on `:rest`
  or `:app`.

## Notes / decisions

- Keep the engine framework-agnostic (plain Java) so it can be tested without Quarkus.
- Decide and document the move representation (e.g. from-square + ordered list of landing
  squares for multi-jumps) in `docs/tech`; this becomes part of the API contract used by
  Feature0004.
- Update `docs/glossar.md` with domain terms (Stein/Man, Dame/King, Schlagzwang, etc.).
