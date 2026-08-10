# UltronAI — Application Flow

## 1. Core Conversational Architecture (Sync vs Async Model)

WebSocket is the real-time transport gateway. RabbitMQ is the asynchronous background task broker.

```text
Customer
   ↓
React Chat UI
   ↓
WebSocket Gateway
   ↓
Conversation Service
   ↓
Intent / AI Processing
   ↓
Workflow Decision
   │
   ├── Fast interactive operation (Greetings, Slot-filling, Local Queries)
   │       ↓
   │   Synchronous response
   │       ↓
   │   WebSocket Gateway ──> Customer
   │
   └── Long-running / background operation (External APIs, Payments, Escalations)
           ↓
        RabbitMQ Exchange (`ultronai.workflow.events`)
           ↓
        Async Worker Queue
           ↓
      Business Integration Service
           ↓
       Event Result Published
           ↓
      WebSocket Gateway Broadcast ──> Customer
```

---

## 2. Customer Chat Execution Flow

### 2.1 Fast Interactive Operations (Synchronous)
Used for: Greetings, clarifying questions, slot collection, and internal status checks.

1. **Customer** sends message via React Chat UI.
2. **WebSocket Gateway** validates connection authorization token.
3. **Conversation Service** persists message in MySQL (`messages` table).
4. **NLP Service** performs intent detection & entity extraction.
5. **Workflow Engine** determines target node is a fast/interactive node (`MESSAGE` or `ENTITY`).
6. **Conversation Service** generates response payload directly.
7. **WebSocket Gateway** pushes response back to customer topic `/topic/conversations/{id}`.

### 2.2 Long-Running / External Operations (Asynchronous)
Used for: External `API_CALL` integration nodes, payment requests, ticket creation, and notifications.

1. **Customer** provides required workflow information (e.g. Order ID).
2. **Workflow Engine** evaluates node as an asynchronous operation (`API_CALL` or `HANDOFF`).
3. **Conversation Service** pushes a typing indicator to WebSocket and publishes a `WORKFLOW_TASK_SUBMITTED` message to **RabbitMQ**.
4. **RabbitMQ Worker** consumes the task event asynchronously.
5. **Business Service / Integration Adapter** executes the external HTTP API call (enforcing SSRF checks and timeouts).
6. **Worker** publishes `WORKFLOW_TASK_COMPLETED` with the payload result.
7. **WebSocket Gateway** receives event and broadcasts final AI response message to `/topic/conversations/{id}`.

---

## 3. Order Tracking Workflow Flow

```text
User: "Where is order ORD10245?"
  ↓
Intent: ORDER_TRACKING (Confidence: 0.96)
  ↓
Entity Extracted: orderId = "ORD10245"
  ↓
Workflow Decision: API_CALL required (Asynchronous path)
  ↓
Publish Event to RabbitMQ (`ultronai.workflow.events`)
  ↓
Worker calls Order API Adapter
  ↓
Order Status Retrieved: "SHIPPED - ETA Tomorrow"
  ↓
Worker publishes result to WebSocket Gateway
  ↓
Customer receives real-time response: "Your order ORD10245 has shipped and will arrive tomorrow!"
```

If Order ID entity is missing:
```text
User: "Where is my order?"
  ↓
Intent: ORDER_TRACKING
  ↓
Entity Extracted: orderId = NULL
  ↓
Workflow Decision: Slot-filling required (Synchronous path)
  ↓
AI Response: "Please provide your 8-character Order ID."
```

---

## 4. Human Agent Handoff Flow

Trigger conditions:
- Customer explicitly clicks "Talk to Agent" or types request.
- AI intent confidence < configured threshold after 2 consecutive retries.
- Workflow node `HANDOFF` executed.

```text
Customer Request / AI Low Confidence
  ↓
Conversation Status updated to ESCALATED
  ↓
AI Auto-Response Suppressed (NLP engine ignored for subsequent incoming messages)
  ↓
Publish Handoff Event to RabbitMQ (`ultronai.handoff.queue`)
  ↓
Agent Queue Worker checks active SUPPORT_AGENT availability
  ↓
Assign Support Agent & Update `conversations.assigned_agent_id`
  ↓
Push WebSocket Notification to `/user/queue/notifications` (Agent Dashboard)
  ↓
Support Agent joins conversation topic `/topic/conversations/{id}`
  ↓
Human-to-Customer real-time conversation proceeds
```

---

## 5. Conversation State Lifecycle

```text
       ┌───────────┐
       │    NEW    │
       └─────┬─────┘
             │
             ▼
       ┌───────────┐
       │  ACTIVE   │ ◄────────────────────────┐
       └─────┬─────┘                          │
             │                                │
      (Handoff Triggered)             (Agent Re-assigns / Resolves)
             │                                │
             ▼                                │
       ┌───────────┐                          │
       │ ESCALATED │                          │
       └─────┬─────┘                          │
             │                                │
    (Agent Accepts Queue)                     │
             │                                │
             ▼                                │
  ┌────────────────────┐                      │
  │   AGENT_ASSIGNED   ├──────────────────────┘
  └──────────┬─────────┘
             │
     (Issue Resolved)
             │
             ▼
       ┌───────────┐
       │ RESOLVED  │
       └─────┬─────┘
             │
      (Session Closed)
             │
             ▼
       ┌───────────┐
       │  CLOSED   │
       └───────────┘
```