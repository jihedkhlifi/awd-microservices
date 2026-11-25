# notification microservice

Python / FastAPI microservice, **no database**, with Swagger documentation generated automatically.
It exposes one endpoint for now; the notification logic is a TODO for students (see [TODO.md](TODO.md)).

## Run
```bash
cd chemin\vers\notification

# 1. Créer et activer l'environnement virtuel (une seule fois)
python -m venv venv
.\venv\Scripts\Activate.ps1

# 2. Installer les dépendances
python -m pip install -r requirements.txt

# 3. Lancer le microservice
python -m app.main          # port 8084 (variable PORT)
```
- API: http://localhost:8084/api/notifications/hello
- Swagger UI: http://localhost:8084/swagger-ui
- OpenAPI JSON: http://localhost:8084/v3/api-docs
- ReDoc: http://localhost:8084/redoc

Port `8084` (candidat = 8081, job = 8082, meeting = 8083).

## Endpoints
| Method | Path | Response |
|---|---|---|
| GET | /api/notifications/hello | `200 {"message": "hello I'm microservice notification"}` |
| GET | /health | `200 {"status": "UP"}` (health_check_url Eureka) |
| GET | /info | `200 {"app": {"name": "notification", "version": "1.0.0", ...}}` (status_page_url Eureka) |

## Eureka
Au démarrage (lifespan FastAPI), le service s'enregistre sous **NOTIFICATION** dans http://localhost:8761/eureka
(`app/eureka.py`) et se désinscrit à l'arrêt. Vérifier : `GET http://localhost:8761/eureka/apps/NOTIFICATION`.

| Variable | Défaut | Rôle |
|---|---|---|
| `PORT` | 8084 | port de l'instance (aussi publié dans Eureka) |
| `EUREKA_SERVER` | http://localhost:8761/eureka | serveur Eureka |
| `EUREKA_ENABLED` | true | `false` pour lancer sans Eureka |

Deuxième instance (PowerShell) : `$env:PORT=8094; python -m app.main`.
Avec `uvicorn --port X`, définir aussi `PORT=X` pour que le bon port soit publié dans Eureka.

## Tests
```bash
pip install -r requirements-dev.txt
pytest
```

## Structure
```
notification/
├── app/
│   ├── main.py                    FastAPI app + Swagger config + lifespan Eureka
│   ├── eureka.py                  client py-eureka-client (NOTIFICATION)
│   ├── routers/health.py          GET /health, GET /info
│   ├── schemas.py                 Pydantic models (validation + Swagger schemas)
│   └── routers/notification.py    /api/notifications routes (hello + TODO)
├── tests/test_hello.py, tests/test_health.py
├── TODO.md                        work for students
├── requirements.txt / requirements-dev.txt
└── pytest.ini
```
