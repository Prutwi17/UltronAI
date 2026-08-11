from pydantic import BaseModel, Field
from typing import List, Optional

class NlpRequest(BaseModel):
    text: str = Field(..., min_length=1, max_length=10000, description="Raw user input text to analyze")

class IntentResult(BaseModel):
    name: str = Field(..., description="Predicted intent name")
    confidence: float = Field(..., ge=0.0, le=1.0, description="Confidence score between 0.0 and 1.0")

class EntityResult(BaseModel):
    type: str = Field(..., description="Entity type identifier e.g. order_id, email, number")
    value: str = Field(..., description="Extracted entity string value")
    confidence: float = Field(default=1.0, ge=0.0, le=1.0)
    start_pos: Optional[int] = None
    end_pos: Optional[int] = None

class NlpResponse(BaseModel):
    intent: IntentResult
    entities: List[EntityResult] = Field(default_factory=list)
    fallback: bool = Field(default=False, description="True if prediction confidence is below threshold")

class TrainExample(BaseModel):
    text: str
    intent: str

class TrainRequest(BaseModel):
    examples: List[TrainExample]
