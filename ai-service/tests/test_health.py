from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)

def test_health_endpoint():
    response = client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "healthy"
    assert data["service"] == "UltronAI NLP Service"

def test_predict_endpoint():
    response = client.post("/api/v1/nlp/predict", json={
        "tenant_id": 1,
        "agent_id": 1,
        "text": "hello"
    })
    assert response.status_code == 200
    data = response.json()
    assert "intent" in data
    assert "confidence" in data
