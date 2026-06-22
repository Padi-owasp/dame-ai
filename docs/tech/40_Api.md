# API

## Conventions

- All HTTP/REST endpoints use the **`/api`** path prefix (e.g. `@Path("/api/health")`), keeping
  them clear of the SPA routes served from `/`.
- JSON is the default representation.

## Endpoints

### `GET /api/health`

Health / smoke endpoint backed by `ai.dame.business.health.HealthService`.

- **Response** — `200 OK`, `application/json`:
  ```json
  { "status": "UP" }
  ```

> The realtime game protocol (WebSocket message contract for moves, turn changes and game state)
> will be documented here — or in a dedicated realtime topic (`60_`) — once it is introduced.
