# Feature0006 — Game Lifecycle UI (Turns, End States, Resign/Abort, Rematch)

**Status:** Planned
**Depends on:** Feature0004, Feature0005
**Modules:** `:frontend`
**Owner / parallelization:** Follows the board UI; polishes the full game loop end-to-end.

## Goal

Complete the player-facing game loop around the board: clear turn flow, end-of-game
outcomes, and controls to resign/abort, offer/accept a draw, and start a new game — all
driven by the server's authoritative events.

## Scope (in)

- Turn flow UI: prominent indicator of whose turn it is and what the player should do next.
- Waiting/lobby states: "waiting for opponent to join", opponent connected/disconnected.
- End states: win / loss / draw screens triggered by server events.
- Controls: resign/abort, offer draw, accept/decline draw.
- New game / rematch after a finished or aborted game (re-enters matchmaking from
  Feature0003).
- User feedback for rejected moves and connection loss.

## Out of scope

- Game rules and validation (server-side).
- Accounts, profiles, history, lobby of multiple games (Feature0007).

## Key requirements / acceptance criteria

- Win/loss/draw are shown correctly to both players based on server events.
- Resign/abort and draw offer/accept work end-to-end and reset to a new pairing.
- A full game can be played from join → moves → end → new game in the browser.
- Covered by Cypress e2e tests for the main use cases (full game, resign, draw, opponent
  disconnect).

## Notes / decisions

- All outcomes come from server events — the UI never declares a winner on its own.
- Confirm the post-game flow (auto new pairing vs explicit rematch) matches the decision in
  Feature0003.
