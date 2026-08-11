from sklearn.feature_extraction.text import CountVectorizer
from sklearn.naive_bayes import MultinomialNB
from app.preprocessing.text_cleaner import normalize_text
from typing import List, Tuple, Dict
import numpy as np

DEFAULT_TRAINING_DATA = [
    # ORDER_TRACKING
    ("where is my order", "ORDER_TRACKING"),
    ("track my order 12345", "ORDER_TRACKING"),
    ("what is my order status", "ORDER_TRACKING"),
    ("check delivery status for order", "ORDER_TRACKING"),
    ("when will my package arrive", "ORDER_TRACKING"),
    ("track shipment 9876", "ORDER_TRACKING"),
    ("shipping update", "ORDER_TRACKING"),
    ("order delivery info", "ORDER_TRACKING"),

    # CANCEL_ORDER
    ("cancel my order", "CANCEL_ORDER"),
    ("i want to cancel order 12345", "CANCEL_ORDER"),
    ("stop my order shipment", "CANCEL_ORDER"),
    ("abort purchase", "CANCEL_ORDER"),
    ("cancel shipment", "CANCEL_ORDER"),
    ("cancel my order 999", "CANCEL_ORDER"),

    # GREETING
    ("hello", "GREETING"),
    ("hi there", "GREETING"),
    ("good morning", "GREETING"),
    ("hey ultron", "GREETING"),
    ("greetings", "GREETING"),

    # ACCOUNT_HELP
    ("reset my password", "ACCOUNT_HELP"),
    ("change account email", "ACCOUNT_HELP"),
    ("update user profile", "ACCOUNT_HELP"),
    ("cannot login to account", "ACCOUNT_HELP"),

    # REFUND_REQUEST
    ("request a refund for order", "REFUND_REQUEST"),
    ("i want my money back", "REFUND_REQUEST"),
    ("return item for refund", "REFUND_REQUEST"),
    ("order refund status", "REFUND_REQUEST")
]

class IntentClassifier:

    def __init__(self):
        self.vectorizer = CountVectorizer(ngram_range=(1, 2), min_df=1)
        self.model = MultinomialNB(alpha=0.1)
        self.is_trained = False
        self._fit_default_model()

    def _fit_default_model(self):
        texts = [normalize_text(t[0]) for t in DEFAULT_TRAINING_DATA]
        labels = [t[1] for t in DEFAULT_TRAINING_DATA]
        X = self.vectorizer.fit_transform(texts)
        self.model.fit(X, labels)
        self.is_trained = True

    def train_custom(self, examples: List[Dict[str, str]]):
        if not examples:
            return
        texts = [normalize_text(e["text"]) for e in examples]
        labels = [e["intent"] for e in examples]
        X = self.vectorizer.fit_transform(texts)
        self.model.fit(X, labels)
        self.is_trained = True

    def predict(self, text: str) -> Tuple[str, float]:
        cleaned = normalize_text(text)
        if not cleaned or not self.is_trained:
            return ("UNKNOWN", 0.0)

        X = self.vectorizer.transform([cleaned])
        probs = self.model.predict_proba(X)[0]
        max_idx = np.argmax(probs)
        confidence = float(probs[max_idx])
        predicted_class = str(self.model.classes_[max_idx])

        return (predicted_class, confidence)

intent_classifier = IntentClassifier()
