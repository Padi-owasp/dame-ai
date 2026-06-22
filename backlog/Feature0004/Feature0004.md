# Feature0004 — Realtime Communication (REST + WebSocket) & Server-side Move Validation

**Status:** Planned
**Depends on:** Feature0002, Feature0003
**Modules:** `:rest`
**Owner / parallelization:** Can be split from Feature0003; both work against the agreed
message contract.

## Goal

Expose the game over the network and make the server the **single authority**: clients
submit move intentions, the server validates them with the engine, applies them through the
session, and pushes the authoritative state to both players in real time. This is the core
of the anti-cheat guarantee.

## Scope (in)

- WebSocket endpoint per player connection: on connect, the player is placed via
  matchmaking (Feature0003) and receives the current game state and their color.
- Server → client messages: full/updated board state, whose turn it is, legal-move hints
  for the side to move (optional), game-end (win/loss/draw), opponent connected/disconnected,
  move rejected.
- Client → server messages: submit move (from-square + landing path), resign, offer/accept
  draw.
- **Server-side validation:** a submitted move is applied only if it comes from the player
  whose turn it is and is legal per the engine (incl. Schlagzwang/multi-capture). Illegal or
  out-of-turn moves are rejected and the authoritative state is re-sent.
- DTOs / serialization for board, pieces, moves, and events; a documented message contract.
- Minimal REST surface as needed (e.g. health, or join/state fallback).

## Out of scope

- Rendering (Feature0005/0006).
- Persistence/reconnect across server restart; accounts.
- Multiple concurrent games.

## Key requirements / acceptance criteria

- A move from the wrong player or an illegal move is **never** applied; the server re-sends
  the correct state (verified by integration tests driving a real WebSocket).
- A legal move by the side to move is applied once and both clients receive the updated
  state.
- Game-end and resign/draw events are delivered to both players.
- Integration tests use a real HTTP/WebSocket server (not only `contextLoads()`), asserting
  actual message payloads.

## Notes / decisions

- Lock down the **message contract** (event names, DTO shapes) at the start and record it in
  `docs/tech`; frontend (0005/0006) depends on it.
- Trust nothing from the client beyond the move intention; never send a client information it
  should not have or accept client-computed legality.
- Decide reconnect/disconnect behavior within a running process (e.g. brief grace period vs
  immediate abort) and document it.
