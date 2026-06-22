# Feature0007 — User Accounts & Matchmaking Evolution (Goal State)

**Status:** Planned (future / stretch — after the 2-player MVP is solid)
**Depends on:** Feature0003, Feature0004
**Modules:** `:business`, `:rest`, `:frontend`

## Goal

Evolve matchmaking from "first two clients auto-join" toward the stated goal: real **user
accounts** with login, so players are identified across sessions and can choose whom they
play (invitations and/or a lobby of open games).

## Scope (in)

- User accounts: registration and login; an authenticated player identity.
- Replace the anonymous per-connection token (Feature0003) with the account identity, reusing
  the session core designed to allow this swap.
- Matchmaking beyond first-two-players: at minimum a lobby of open games and/or direct
  invitations, allowing more than one pairing to exist.
- Authentication/authorization on REST + WebSocket so moves are bound to the authenticated
  player.

## Out of scope (for the initial MVP, revisit when this feature is scheduled)

- Persistent match history / ratings / leaderboards.
- Social features beyond inviting/joining a game.
- Anything that would block delivering the 2-player MVP first.

## Key requirements / acceptance criteria

- A player can register, log in, and is identified by their account in games.
- Two authenticated players can be matched (lobby or invite) and play a full game.
- The turn-ownership and anti-cheat guarantees from Feature0004 still hold, now keyed on
  account identity.
- Likely requires revisiting the **in-memory** decision (persisting accounts, and probably
  games) — to be decided when this feature is planned.

## Notes / decisions

- This feature captures the **target** matchmaking; the MVP (Feature0003) deliberately ships
  the simpler first-two-players model first.
- Design now (in 0003/0004) so identity is pluggable; do not over-build accounts before the
  MVP plays end-to-end.
