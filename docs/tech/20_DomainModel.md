# Domain Model

> **Status:** stub. The checkers (Deutsche Dame) domain model is introduced from **Feature0002**
> onward (board, squares, pieces, moves, capture/Schlagzwang rules, king/Dame). Domain terms are
> defined in [../glossar.md](../glossar.md).

## Current model

The scaffold contains only a minimal health-check service:

- `ai.dame.business.health.HealthService` — `@ApplicationScoped` CDI bean exposing
  `String status()` (returns `"UP"`).

This file grows with the game domain (e.g. Board, Square, Piece/Stein, Dame/King, Move, and the
rule engine governing forward/king moves and Schlagzwang).
