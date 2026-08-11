import pytest
from fastapi.testclient import TestClient
from app.main import app
from app.preprocessing.text_cleaner import normalize_text, extract_entities

client = TestClient(app)

def test_health_endpoint():
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json()["status"] == "healthy"

def test_text_normalization():
    assert normalize_text("  TRACK My   Order!  ") == "track my order"
    assert normalize_text("") == ""

def test_entity_extraction_order_id():
    entities = extract_entities("Please track my order 12345")
    assert len(entities) >= 1
    assert any(e.type == "order_id" and e.value == "12345" for e in entities)

def test_entity_extraction_email():
    entities = extract_entities("Send confirmation to user@domain.com")
    assert len(entities) >= 1
    assert any(e.type == "email" and e.value == "user@domain.com" for e in entities)

def test_intent_classification_order_tracking():
    response = client.post("/api/v1/nlp/analyze", json={"text": "Where is my order?"})
    assert response.status_code == 200
    data = response.json()
    assert data["intent"]["name"] == "ORDER_TRACKING"
    assert data["intent"]["confidence"] >= 0.55
    assert data["fallback"] is False

def test_intent_classification_cancel_order():
    response = client.post("/api/v1/nlp/analyze", json={"text": "Cancel my order #999"})
    assert response.status_code == 200
    data = response.json()
    assert data["intent"]["name"] == "CANCEL_ORDER"
    assert data["fallback"] is False
    assert any(e["type"] == "order_id" and e["value"] == "999" for e in data["entities"])

def test_low_confidence_fallback():
    response = client.post("/api/v1/nlp/analyze", json={"text": "qwertyuiop zxcvbnm completely unrelated gibberish text"})
    assert response.status_code == 200
    data = response.json()
    assert data["intent"]["name"] == "UNKNOWN"
    assert data["fallback"] is True

def test_empty_input_validation():
    response = client.post("/api/v1/nlp/analyze", json={"text": "   "})
    assert response.status_code == 400
