from fastapi import APIRouter, HTTPException, status
from app.schemas.nlp import NlpRequest, NlpResponse, TrainRequest
from app.services.nlp_service import nlp_service

router = APIRouter(prefix="/api/v1/nlp", tags=["nlp"])

@router.post("/analyze", response_model=NlpResponse)
@router.post("/predict", response_model=NlpResponse)
def analyze_text(request: NlpRequest) -> NlpResponse:
    if not request.text or not request.text.strip():
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Text payload cannot be empty or whitespace"
        )
    return nlp_service.analyze(request.text)

@router.post("/train")
def train_model(request: TrainRequest):
    if not request.examples:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Training examples list cannot be empty"
        )
    examples_dict = [{"text": e.text, "intent": e.intent} for e in request.examples]
    return nlp_service.train(examples_dict)
