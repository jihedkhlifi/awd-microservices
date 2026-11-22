"""Notification microservice - entry point.

Run (le port est lu dans la variable PORT, aussi utilisée pour l'enregistrement Eureka) :
    python -m app.main                       # port 8084
    PORT=8094 python -m app.main             # 2e instance (PowerShell : $env:PORT=8094; python -m app.main)
  or: uvicorn app.main:app --port 8084       # si vous changez --port, définissez aussi PORT
"""
import os
from contextlib import asynccontextmanager

from fastapi import FastAPI

from app import eureka
from app.routers import health, notification

PORT = int(os.getenv("PORT", "8084"))


@asynccontextmanager
async def lifespan(app: FastAPI):
    # Démarrage : enregistrement dans Eureka sous le nom NOTIFICATION
    if eureka.eureka_enabled():
        await eureka.register(PORT)
    yield
    # Arrêt : désinscription d'Eureka
    if eureka.eureka_enabled():
        await eureka.unregister()


app = FastAPI(
    title="Notification Microservice API",
    version="1.0.0",
    description=(
        "Notification microservice (Python / FastAPI, no database). "
        "Only the hello endpoint is implemented; the notification logic is to be developed by students."
    ),
    contact={"name": "Badia Abouhdid"},
    servers=[{"url": f"http://localhost:{PORT}", "description": "Local"}],
    # Same URLs as the other microservices of the project
    docs_url="/swagger-ui",       # Swagger UI
    openapi_url="/v3/api-docs",   # OpenAPI JSON
    redoc_url="/redoc",           # alternative documentation
    lifespan=lifespan,
)

app.include_router(health.router)
app.include_router(notification.router)

# TODO (students): if you add other routers, include them here.


if __name__ == "__main__":
    import uvicorn

    uvicorn.run("app.main:app", host="0.0.0.0", port=PORT, reload=True)
