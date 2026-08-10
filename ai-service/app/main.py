from fastapi import FastAPI
from pydantic import BaseModel
from typing import List, Optional

app = FastAPI(
    title="UltronAI NLP Service",
    description="FastAPI service for Intent Classification and Entity Extraction",
    version="0.1.0"
)

class PredictRequest(BaseModel):
    tenant_id: int
    agent_id: int
    text: str

class EntityResult(BaseModel):
    name: str
    value: str
    type: str

class PredictResponse(BaseModel):
    intent: str
    confidence: float
    entities: List[EntityResult]

@app.get("/health")
def health_check():
    return {
        "status": "healthy",
        "service": "UltronAI NLP Service",
        "version": "0.1.0"
    }

@app.post("/api/v1/nlp/predict", response_model=PredictResponse)
def predict_intent(request: PredictRequest):
    # Initial placeholder response until model training phase
    return PredictResponse(
        intent="UNKNOWN",
        confidence=0.0,
        entities=[]
    )

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)
