# Feature0005 — Game Board UI (React)

**Status:** Planned
**Depends on:** Feature0001 (mocked state), Feature0004 (live state)
**Modules:** `:frontend`
**Owner / parallelization:** Dev B. Can start against a static/mock state while the backend
(0002–0004) is built in parallel.

## Goal

A React UI that always shows the current board exactly as the server reports it, and lets the
player make moves by interacting with the board. The board is purely presentational — it
renders authoritative state and submits move intentions.

## Scope (in)

- 8×8 board rendering with the 32 dark playable squares, men and Damen for both colors,
  correctly oriented per player.
- Render from a board-state model matching the Feature0004 contract (works against mock data
  first, then live WebSocket state).
- Move interaction: select a piece, see legal targets (highlighting), click/drag to a target;
  for multi-captures, build the landing path. Submit the move intention to the server.
- Always reflect server-pushed state updates (opponent's moves appear live).
- Connection/turn affordances: clearly show "your turn" vs "waiting for opponent", and
  connection status.

## Out of scope

- Rules logic / legality decisions (server-authoritative; UI only hints using server data).
- End-of-game screens, resign/draw/rematch controls (Feature0006).
- Accounts/lobby UI (Feature0007).

## Key requirements / acceptance criteria

- The rendered board matches the given state model for any valid position (including Damen).
- A player can only attempt moves on their turn; the UI submits intentions and updates from
  the server's authoritative response (including rejection → revert to server state).
- Opponent moves received over WebSocket update the board without a reload.
- Covered by component tests and a Cypress e2e for the core move-and-update flow.

## Notes / decisions

- Build against the Feature0004 message contract; if it isn't final yet, code to a documented
  mock that mirrors it.
- The UI must never assume a move succeeded until the server confirms — render optimistic
  state cautiously or wait for confirmation; document the choice.
