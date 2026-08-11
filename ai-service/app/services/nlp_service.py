from app.schemas.nlp import NlpResponse, IntentResult, EntityResult
from app.preprocessing.text_cleaner import normalize_text, extract_entities
from app.models.intent_classifier import intent_classifier
from typing import List, Dict

CONFIDENCE_THRESHOLD = 0.55

class NlpService:

    def analyze(self, text: str) -> NlpResponse:
        cleaned_text = normalize_text(text)
        if not cleaned_text:
            return NlpResponse(
                intent=IntentResult(name="UNKNOWN", confidence=0.0),
                entities=[],
                fallback=True
            )

        # Predict intent
        intent_name, confidence = intent_classifier.predict(cleaned_text)

        # Evaluate fallback threshold
        fallback = False
        if confidence < CONFIDENCE_THRESHOLD:
            fallback = True
            intent_name = "UNKNOWN"

        # Extract entities
        entities = extract_entities(text)

        return NlpResponse(
            intent=IntentResult(name=intent_name, confidence=round(confidence, 4)),
            entities=entities,
            fallback=fallback
        )

    def train(self, examples: List[Dict[str, str]]) -> Dict[str, str]:
        intent_classifier.train_custom(examples)
        return {"status": "SUCCESS", "message": f"Successfully trained model on {len(examples)} examples"}

nlp_service = NlpService()
