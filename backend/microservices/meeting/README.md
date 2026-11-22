# meeting microservice

Node.js / Express microservice, **no database**, documented with Swagger (OpenAPI 3).
It exposes one endpoint for now; the meeting logic is a TODO for students (see [TODO.md](TODO.md)).

## Run
```bash
npm install
npm start          # or: npm run dev  (auto-restart on changes)
```
- API: http://localhost:8083/api/meetings/hello
- Swagger UI: http://localhost:8083/swagger-ui
- OpenAPI JSON: http://localhost:8083/v3/api-docs

Port: `8083` by default (candidat = 8081, job = 8082). Change with `PORT=9000 npm start`.

## Endpoints
| Method | Path | Response |
|---|---|---|
| GET | /api/meetings/hello | `200 {"message": "hello I'm microservice meeting"}` |
| GET | /health | `200 {"status": "UP"}` (healthCheckUrl Eureka) |
| GET | /info | `200 {"app": {"name": "meeting", "version": "1.0.0", ...}}` (statusPageUrl Eureka) |

## Eureka
Au démarrage, le service s'enregistre sous **MEETING** dans http://localhost:8761 (`src/config/eureka.js`)
et se désinscrit à l'arrêt (Ctrl+C).

| Variable | Défaut | Rôle |
|---|---|---|
| `PORT` | 8083 | port de l'instance |
| `EUREKA_HOST` / `EUREKA_PORT` | localhost / 8761 | serveur Eureka |
| `EUREKA_ENABLED` | true | `false` pour lancer sans Eureka |

Deuxième instance : `PORT=8093 npm start` (PowerShell : `$env:PORT=8093; npm start`).

## Tests
```bash
npm test
```
Uses Node's built-in test runner (`node:test`), Node 18+.

## Structure
```
meeting/
├── src/
│   ├── server.js                      starts the HTTP server
│   ├── app.js                         Express app: JSON, Swagger, routes, 404, errors
│   ├── config/swagger.js              OpenAPI spec (document new routes here)
│   ├── config/eureka.js               Eureka client (MEETING, /health, /info)
│   ├── routes/health.routes.js        GET /health, GET /info
│   ├── routes/meeting.routes.js       URL -> controller mapping
│   └── controllers/meeting.controller.js   request handling (hello + TODO)
├── test/hello.test.js, test/health.test.js
├── TODO.md                            work for students
└── package.json
```
