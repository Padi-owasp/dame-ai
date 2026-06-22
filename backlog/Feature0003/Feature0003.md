# Feature0003 — Game Session & Matchmaking (In-Memory, First-Two-Players)

**Status:** Planned
**Depends on:** Feature0001, Feature0002
**Modules:** `:business`
**Owner / parallelization:** Can be split from Feature0004 against an agreed API contract.

## Goal

Manage the lifecycle of a single live game in memory and bind it to exactly two players:
the **first two clients** that open the app. The game stays bound to them until it finishes
or a player aborts.

## Scope (in)

- In-memory game registry/service holding the current game and its two player slots.
- **Matchmaking (MVP):** first client to connect becomes player 1, second becomes player 2
  and the game starts. Further clients are rejected/queued or shown "game in progress".
- Player identity for the MVP without accounts (e.g. a server-issued session/player token
  per connection) — enough to tie a connection to a color and to enforce turn ownership.
- Game lifecycle: `WAITING_FOR_OPPONENT → IN_PROGRESS → FINISHED/ABORTED`.
- Turn ownership: track whose turn it is; expose "is it player X's turn".
- Abort/resign: a player can abort; the game transitions to a terminal state and frees the
  slots for a new game.
- Draw handling: offer/accept draw (uses engine state from Feature0002).

## Out of scope

- WebSocket/REST endpoints and serialization (Feature0004).
- Persistence beyond process memory; reconnect after server restart.
- Accounts, multiple concurrent games, lobby (Feature0007).

## Key requirements / acceptance criteria

- Applying a move goes through the engine; the session rejects moves from the wrong player
  or when it is not that player's turn.
- Exactly two players can be active at once; lifecycle transitions are correct and tested.
- Abort/resign by either player ends the game and resets the registry for a new pairing.
- Behavior covered by tests (integration tests where in-memory state/coordination is
  involved, not only unit tests).

## Notes / decisions

- Design the player-identity mechanism so it can later be replaced by real accounts
  (Feature0007) without rewriting the session core.
- Decide what happens to the second-game pairing after a game ends (auto-reset vs explicit
  "new game") and document it.
