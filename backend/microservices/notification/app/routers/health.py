"""Routes de santé (atelier Observabilité) : déclarées dans Eureka comme
health_check_url (/health) et status_page_url (/info)."""
from fastapi import APIRouter, Request

router = APIRouter(tags=["Health"])


@router.get("/health", summary="Santé du service (health_check_url Eureka)")
def health() -> dict:
    return {"status": "UP"}


@router.get("/info", summary="Nom et version du service (status_page_url Eureka)")
def info(request: Request) -> dict:
    app = request.app
    return {"app": {"name": "notification", "version": app.version, "description": app.title}}
