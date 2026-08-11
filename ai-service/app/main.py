from fastapi import FastAPI
from app.api.nlp import router as nlp_router

app = FastAPI(
    title="UltronAI AI & NLP Microservice",
    version="1.0.0",
    description="Local zero-cost NLP intent detection, entity extraction, and machine learning pipeline"
)

app.include_router(nlp_router)

@app.get("/health")
def health_check():
    return {
        "status": "healthy",
        "service": "UltronAI NLP Service",
        "version": "1.0.0"
    }
