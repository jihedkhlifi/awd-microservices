"""Enregistrement du microservice Notification dans Eureka.

- Atelier Eureka, partie 3 - tâche 2 : nom NOTIFICATION, port lu dans l'environnement, arrêt propre.
- Atelier Observabilité, partie 5 - tâche 2 : health_check_url et status_page_url.

Variables d'environnement :
    PORT             port de l'instance (8084 par défaut) -> plusieurs instances possibles
    INSTANCE_HOST    nom d'hôte publié dans Eureka (localhost par défaut)
    EUREKA_SERVER    URL du serveur (http://localhost:8761/eureka par défaut)
    EUREKA_ENABLED   "false" pour ne pas s'enregistrer (tests, développement isolé)
"""
import logging
import os

import py_eureka_client.eureka_client as eureka_client

logger = logging.getLogger("notification.eureka")

APP_NAME = "NOTIFICATION"


def eureka_settings(port: int) -> dict:
    """Paramètres passés à eureka_client.init_async (séparés pour pouvoir les tester)."""
    host = os.getenv("INSTANCE_HOST", "localhost")
    base_url = f"http://{host}:{port}"
    return {
        "eureka_server": os.getenv("EUREKA_SERVER", "http://localhost:8761/eureka"),
        "app_name": APP_NAME,
        "instance_id": f"{host}:notification:{port}",  # unique par instance
        "instance_host": host,
        "instance_ip": "127.0.0.1",
        "instance_port": port,
        "vip_adr": "notification",
        "secure_vip_addr": "notification",
        "home_page_url": f"{base_url}/",
        "status_page_url": f"{base_url}/info",       # lien de l'instance dans le dashboard
        "health_check_url": f"{base_url}/health",
    }


def eureka_enabled() -> bool:
    return os.getenv("EUREKA_ENABLED", "true").lower() != "false"


async def register(port: int) -> None:
    settings = eureka_settings(port)
    try:
        await eureka_client.init_async(**settings)
        logger.warning("registered in Eureka as %s (instance %s)", APP_NAME, settings["instance_id"])
    except Exception as exc:  # le service reste utilisable même si Eureka est arrêté
        logger.error("Eureka registration failed: %s", exc)


async def unregister() -> None:
    try:
        await eureka_client.stop_async()
    except Exception as exc:
        logger.error("Eureka unregistration failed: %s", exc)
