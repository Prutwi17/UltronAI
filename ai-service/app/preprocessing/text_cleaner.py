import re
from typing import List
from app.schemas.nlp import EntityResult

def normalize_text(text: str) -> str:
    if not text:
        return ""
    text = text.lower()
    text = re.sub(r'[^\w\s]', ' ', text)
    text = re.sub(r'\s+', ' ', text)
    return text.strip()

def extract_entities(text: str) -> List[EntityResult]:
    entities: List[EntityResult] = []
    if not text:
        return entities

    # 1. Extract Order ID
    order_id_patterns = [
        (r'\b(?:order|ord|parcel|package)\s*#?\s*([a-zA-Z0-9-]{1,20})\b', "order_id"),
        (r'\b#([a-zA-Z0-9-]{1,20})\b', "order_id")
    ]
    for pattern, entity_type in order_id_patterns:
        for match in re.finditer(pattern, text, re.IGNORECASE):
            val = match.group(1) if match.lastindex else match.group(0)
            if not any(e.type == entity_type and e.value == val for e in entities):
                entities.append(EntityResult(
                    type=entity_type,
                    value=val,
                    confidence=0.98,
                    start_pos=match.start(),
                    end_pos=match.end()
                ))

    # 2. Extract Email
    email_pattern = r'\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}\b'
    for match in re.finditer(email_pattern, text):
        val = match.group(0)
        if not any(e.type == "email" and e.value == val for e in entities):
            entities.append(EntityResult(
                type="email",
                value=val,
                confidence=0.99,
                start_pos=match.start(),
                end_pos=match.end()
            ))

    # 3. Extract Date
    date_pattern = r'\b(?:\d{4}-\d{2}-\d{2}|\d{1,2}/\d{1,2}/\d{2,4})\b'
    for match in re.finditer(date_pattern, text):
        val = match.group(0)
        if not any(e.type == "date" and e.value == val for e in entities):
            entities.append(EntityResult(
                type="date",
                value=val,
                confidence=0.95,
                start_pos=match.start(),
                end_pos=match.end()
            ))

    # 4. Extract Standalone Numbers if no order_id already matched it
    number_pattern = r'\b\d{4,10}\b'
    for match in re.finditer(number_pattern, text):
        val = match.group(0)
        if not any(e.value == val for e in entities):
            entities.append(EntityResult(
                type="number",
                value=val,
                confidence=0.90,
                start_pos=match.start(),
                end_pos=match.end()
            ))

    return entities
