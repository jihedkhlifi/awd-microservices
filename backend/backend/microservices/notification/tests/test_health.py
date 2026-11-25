"""Atelier Observabilité : /health, /info et paramètres Eureka. Run with: pytest"""
from fastapi.testclient import TestClient

from app.eureka import eureka_settings
from app.main import app

client = TestClient(app)  # sans "with" : le lifespan (enregistrement Eureka) n'est pas exécuté


def test_health_returns_up():
    res = client.get("/health")
    assert res.status_code == 200
    assert res.json() == {"status": "UP"}


def test_info_returns_name_and_version():
    res = client.get("/info")
    assert res.status_code == 200
    assert res.json()["app"]["name"] == "notification"
    assert res.json()["app"]["version"] == "1.0.0"


def test_eureka_settings_declare_health_and_status_urls():
    s = eureka_settings(9094)
    assert s["app_name"] == "NOTIFICATION"
    assert s["instance_port"] == 9094
    assert s["instance_id"] == "localhost:notification:9094"
    assert s["eureka_server"] == "http://localhost:8761/eureka"
    assert s["health_check_url"] == "http://localhost:9094/health"
    assert s["status_page_url"] == "http://localhost:9094/info"
