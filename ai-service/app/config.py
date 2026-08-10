import os
from pydantic import BaseModel

class Settings(BaseModel):
    app_name: str = "UltronAI NLP Service"
    environment: str = os.getenv("ENVIRONMENT", "development")
    host: str = os.getenv("HOST", "0.0.0.0")
    port: int = int(os.getenv("PORT", "8000"))

settings = Settings()
