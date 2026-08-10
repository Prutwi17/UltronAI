# UltronAI — Technology Specification

## 1. System Architecture Overview

UltronAI utilizes a modular, multi-tenant, service-oriented architecture with a hybrid Synchronous/Asynchronous communication pipeline.

```text
                                 ┌──────────────────────┐
                                 │    React Frontend    │
                                 │   (Vite/TypeScript)  │
                                 └──────────┬───────────┘
                                            │
                               REST API     │     WebSocket (STOMP)
                             (/api/v1/*)    │     (/ws)
                                            │
                                            ▼
                                 ┌──────────────────────┐
                                 │  Spring Boot Backend │
                                 │      (Java 17)       │
                                 └──────────┬───────────┘
                                            │
                  ┌─────────────────────────┼─────────────────────────┐
                  │                         │                         │
                  ▼                         ▼                         ▼
       ┌────────────────────┐    ┌────────────────────┐    ┌────────────────────┐
       │   FastAPI AI/NLP   │    │      RabbitMQ      │    │    MySQL / Redis   │
       │    (Python 3.11)   │    │  (Message Broker)  │    │  (Database/Cache)  │
       └────────────────────┘    └──────────┬─────────┘    └────────────────────┘
                                            │
                                            ▼
                                 ┌──────────────────────┐
                                 │   Workflow Worker    │
                                 │  (External REST APIs)│
                                 └──────────────────────┘
```

---

## 2. Component Technologies & Responsibilities

### 2.1 React Frontend
* **Tech**: React 18, TypeScript, Vite, Tailwind CSS, React Router v6, Axios, `@stomp/stompjs`, Zustand.
* **Responsibilities**: Authentication, chat interface, visual workflow builder (`React Flow`), agent handoff dashboard, platform administration.

### 2.2 Spring Boot Backend
* **Tech**: Java 17, Spring Boot 3.5, Spring Security (JWT), Spring Data JPA, Hibernate, Spring WebSocket (STOMP), Bean Validation, Maven.
* **Responsibilities**: Authentication/authorization, tenant isolation, conversation management, workflow orchestration, RabbitMQ event dispatching, WebSocket gateway management.

### 2.3 Python AI/NLP Service
* **Tech**: Python 3.11, FastAPI, Uvicorn, scikit-learn, spaCy (`en_core_web_sm`), pydantic.
* **Responsibilities**: Intent classification, entity extraction, confidence scoring, tenant NLP model training.
* **Communication**: REST API over HTTP (`POST /api/v1/nlp/predict`, `POST /api/v1/nlp/train`).

### 2.4 Infrastructure & Storage
* **MySQL 8.0**: Persistent relational database (Schema defined in `docs/Schema.md`).
* **Redis 7.0**: Session context cache, rate-limiting tokens, transient workflow state.
* **RabbitMQ 3.12**: Asynchronous task message broker for long-running workflows, external API calls, and agent notifications.

---

## 3. Communication Architecture: Sync vs. Async Model

WebSocket acts as the real-time gateway transport. RabbitMQ handles asynchronous background processing.

### 3.1 Synchronous Flow (Interactive / Low-Latency Operations)
Operations: Greetings, quick replies, simple text generation, instant slot-filling questions, local MySQL state reads.
Latency target: < 500ms.

```text
React Chat UI ──(STOMP /app/chat.sendMessage)──> Spring Boot Gateway ──(REST)──> FastAPI NLP
                                                                                       │
React Chat UI <──(STOMP /topic/conversations/{id})── Spring Boot Gateway <──────────────┘
```

### 3.2 Asynchronous Flow (Long-Running Operations & External Integrations)
Operations: Dynamic `API_CALL` workflow execution, third-party integrations (Orders, Payments, Tickets), email/SMS notifications, background analytics.

```text
React Chat UI ──(STOMP)──> Spring Boot ──(Publish Event)──> RabbitMQ Exchange (`ultronai.direct`)
                                                                     │
React Chat UI <──(STOMP Broadcast)── Spring Boot Gateway <──(Worker Result)── Worker Execution
```

---

## 4. Real-Time WebSocket (STOMP) Specification

* **Connection Endpoint**: `ws://localhost:8080/ws`
* **Authentication**: Token passed in STOMP `CONNECT` frame header (`X-Authorization: Bearer <jwt>`).
* **Inbound Destination**: `/app/chat.sendMessage` (Publish message payload).
* **Outbound Subscriptions**:
  - `/topic/conversations/{conversationId}`: Public conversation channel (AI messages, customer messages, agent messages).
  - `/user/queue/notifications`: Private user channel (Agent escalation notifications, system alerts).
  - `/topic/conversations/{conversationId}/typing`: Typing status indicators.

---

## 5. RabbitMQ Queue & Exchange Architecture

* **Exchanges**:
  - `ultronai.direct` (Direct Exchange)
  - `ultronai.dlx` (Dead Letter Exchange)
* **Queues**:
  - `ultronai.workflow.tasks`: Async workflow node execution (`API_CALL`).
  - `ultronai.handoff.queue`: Human agent escalation requests.
  - `ultronai.notifications`: Asynchronous notification delivery.
  - `ultronai.dlq`: Dead-letter queue for failed event execution retries (Max 3 retries, exponential backoff).

---

## 6. Redis Key Naming Strategy

To enforce multi-tenant isolation and prevent cache collision:

```text
Key Pattern                                  Purpose                            TTL
─────────────────────────────────────────────────────────────────────────────────────────────
tenant:{tenantId}:conv:{convId}:context      Active conversation context state  24 Hours
tenant:{tenantId}:rate:{userId}              Rate-limiting request counts       1 Minute
tenant:{tenantId}:session:{sessionId}        Guest / User Session Token         1 Hour
```

---

## 7. AI/NLP Python Service REST Specification

### 7.1 Predict Intent & Entities
* **Endpoint**: `POST /api/v1/nlp/predict`
* **Request**:
  ```json
  {
    "tenant_id": 1,
    "agent_id": 2,
    "text": "Where is my order ORD10245?"
  }
  ```
* **Response**:
  ```json
  {
    "intent": "ORDER_TRACKING",
    "confidence": 0.96,
    "entities": [
      {
        "name": "orderId",
        "value": "ORD10245",
        "type": "REGEX"
      }
    ]
  }
  ```

### 7.2 Train Model
* **Endpoint**: `POST /api/v1/nlp/train`
* **Model Storage**: Saved on disk at `/app/models/tenant_{tenant_id}/agent_{agent_id}.pkl`.

---

## 8. Environment Variables Matrix

```text
Variable                     Default / Example                      Description
─────────────────────────────────────────────────────────────────────────────────────────────
SERVER_PORT                  8080                                   Spring Boot HTTP Port
SPRING_DATASOURCE_URL        jdbc:mysql://localhost:3306/ultronai   MySQL JDBC URL
SPRING_DATASOURCE_USERNAME   ultron_user                            MySQL Username
SPRING_DATASOURCE_PASSWORD   ultron_password                        MySQL Password
REDIS_HOST                   localhost                              Redis Host
REDIS_PORT                   6379                                   Redis Port
RABBITMQ_HOST                localhost                              RabbitMQ Host
RABBITMQ_PORT                5672                                   RabbitMQ Port
FASTAPI_NLP_URL              http://localhost:8000                  Python NLP Service Base URL
JWT_SECRET                   c2VjcmV0LWtleS1mb3ItdWx0cm9uYWktand0   JWT Signing Secret (Base64)
JWT_EXPIRATION_MS            900000                                 15 Minutes Access Token TTL
ALLOWED_ORIGINS              http://localhost:5173                  Frontend CORS Origin
```