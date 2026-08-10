# 🤖 UltronAI

### Intelligent Conversational AI & Automation Platform

> **Build. Automate. Converse.**

UltronAI is an enterprise-oriented conversational AI and automation platform designed to build intelligent **chat and voice agents** that can understand user intent, execute business workflows, integrate with external systems, and seamlessly hand conversations over to human support agents.

The platform is designed around the same core engineering concepts used in modern conversational automation systems:

* Conversational AI
* NLP and intent detection
* Workflow automation
* REST API integrations
* Real-time communication
* Asynchronous processing
* Human-agent handoff
* Multi-tenant architecture
* Analytics and monitoring

---

# 🚀 Project Vision

Businesses receive thousands of repetitive customer requests every day.

Examples:

> "Where is my order?"

> "Cancel my order."

> "I haven't received my refund."

> "My payment failed."

> "Connect me to an agent."

UltronAI aims to automate these conversations while still providing a reliable path to human support when automation is unable to resolve the customer's problem.

### Core Flow

```text
Customer
   │
   ▼
Chat / Voice
   │
   ▼
Conversation Engine
   │
   ▼
NLP / AI Engine
   │
   ▼
Intent Detection
   │
   ▼
Workflow Engine
   │
   ├──────────────┬──────────────┐
   ▼              ▼              ▼
Order API     Payment API    Ticket API
   │              │              │
   └──────────────┴──────────────┘
                  │
                  ▼
            AI Response
                  │
                  ▼
             Customer
```

If the AI cannot confidently resolve the request:

```text
AI
 │
 ▼
Confidence Check
 │
 ▼
Human Handoff
 │
 ▼
Support Agent
```

---

# ✨ Key Features

## 🤖 Conversational AI

* Natural-language customer conversations
* Intent detection
* Entity extraction
* Confidence scoring
* Context-aware conversations
* Fallback handling

## 💬 Real-Time Chat

* WebSocket-based communication
* Real-time AI responses
* Typing indicators
* Conversation history
* Agent takeover
* Conversation status tracking

## ⚡ Workflow Automation

Build automated workflows such as:

```text
User Request
     ↓
Intent Detection
     ↓
Collect Required Data
     ↓
Validate Data
     ↓
Call Business API
     ↓
Process Result
     ↓
Generate Response
```

## 🔌 Business Integrations

UltronAI is designed to integrate with external business systems such as:

* Customer management
* Orders
* Payments
* Support tickets
* Notifications
* Custom REST APIs

## 👨‍💻 Human Agent Handoff

When AI cannot resolve a conversation:

```text
AI
 ↓
Escalation
 ↓
Agent Queue
 ↓
Support Agent
 ↓
Real-Time Conversation
```

## 📊 Analytics

The platform provides metrics such as:

* Total conversations
* Automated conversations
* Human escalations
* Resolution rate
* Average response time
* Intent distribution
* Agent performance
* Failed conversations

## 🏢 Multi-Tenant Architecture

Multiple businesses can use the same UltronAI platform while maintaining isolated:

* Users
* AI agents
* Conversations
* Workflows
* Integrations
* Analytics

## 🎙️ Voice AI

Planned/advanced functionality:

```text
Voice Input
    ↓
Speech-to-Text
    ↓
NLP / AI
    ↓
Intent
    ↓
Workflow
    ↓
Response
    ↓
Text-to-Speech
    ↓
Voice Output
```

---

# 🏗️ Architecture

```text
                         ┌──────────────────┐
                         │      USERS       │
                         └────────┬─────────┘
                                  │
                         Chat / Voice
                                  │
                                  ▼
                     ┌──────────────────────┐
                     │    React Frontend    │
                     └──────────┬───────────┘
                                │
                    REST API / WebSocket
                                │
                                ▼
                     ┌──────────────────────┐
                     │   Spring Boot API    │
                     └──────────┬───────────┘
                                │
              ┌─────────────────┼─────────────────┐
              │                 │                 │
              ▼                 ▼                 ▼
       Conversation        Workflow          Integration
          Engine            Engine              Layer
              │                 │                 │
              └─────────────────┼─────────────────┘
                                │
                                ▼
                      ┌──────────────────┐
                      │   AI / NLP       │
                      │     Service      │
                      └────────┬─────────┘
                               │
                 ┌─────────────┼─────────────┐
                 ▼             ▼             ▼
              MySQL         Redis        RabbitMQ
```

---

# 🛠️ Technology Stack

### Frontend

* React
* TypeScript
* Vite
* Tailwind CSS
* React Router
* WebSocket

### Backend

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* REST APIs
* WebSocket
* Maven

### AI / NLP

* Python
* FastAPI
* scikit-learn
* spaCy

### Infrastructure

* MySQL
* Redis
* RabbitMQ
* Docker

### Development Tools

* Git
* GitHub
* Postman
* Swagger / OpenAPI
* IntelliJ IDEA
* VS Code

---

# 📁 Project Structure

```text
UltronAI/
│
├── AGENTS.md
├── README.md
│
├── docs/
│   ├── PRD.md
│   ├── TechSpec.md
│   ├── AppFlow.md
│   ├── Design.md
│   ├── Schema.md
│   ├── ImplementationPlan.md
│   ├── Tracker.md
│   ├── Rules.md
│   └── Security.md
│
├── frontend/
│
├── backend/
│
├── ai-service/
│
├── docker/
│
├── .gitignore
└── docker-compose.yml
```

---

# 👥 User Roles

## Platform Admin

Manages the entire UltronAI platform.

## Tenant Admin

Manages a business's:

* AI agents
* Intents
* Workflows
* Integrations
* Users
* Analytics

## Support Agent

Handles conversations escalated from AI.

## Customer

Interacts with AI through chat or voice.

---

# 🔐 Security

UltronAI follows a security-first architecture.

Implemented/planned security mechanisms include:

* JWT authentication
* BCrypt password hashing
* Role-based access control
* Tenant isolation
* Input validation
* API authorization
* Rate limiting
* CORS restrictions
* Security headers
* Audit logging
* Secure secret management
* WebSocket authorization
* AI prompt-injection protection

Sensitive credentials must never be committed to Git.

---

# 🚦 Getting Started

## Prerequisites

Install:

```text
Java 21
Node.js
npm
Python 3.x
MySQL
Redis
RabbitMQ
Docker
Git
```

---

# Clone Repository

```bash
git clone <repository-url>
cd UltronAI
```

---

# Start Infrastructure

```bash
docker compose up -d mysql redis rabbitmq
```

---

# Start Backend

```bash
cd backend
./mvnw spring-boot:run
```

Windows:

```cmd
mvnw.cmd spring-boot:run
```

---

# Start AI Service

```bash
cd ai-service

python -m venv venv
```

Windows:

```cmd
venv\Scripts\activate
```

Install dependencies:

```bash
pip install -r requirements.txt
```

Start:

```bash
uvicorn app.main:app --reload
```

---

# Start Frontend

```bash
cd frontend
npm install
npm run dev
```

---

# 🔗 Local Services

Development environment:

```text
Frontend:
http://localhost:5173

Backend:
http://localhost:8080

AI Service:
http://localhost:8000

Swagger:
http://localhost:8080/swagger-ui/index.html

MySQL:
localhost:3306

Redis:
localhost:6379

RabbitMQ:
localhost:5672
```

Only expose the services that are actually configured and running.

---

# 🧪 Testing

Backend:

```bash
./mvnw test
```

Windows:

```cmd
mvnw.cmd test
```

Frontend:

```bash
npm test
```

AI service:

```bash
pytest
```

API testing can be performed using Postman or Swagger.

---

# 📚 Documentation

The complete engineering documentation is available under:

```text
docs/
```

| Document              | Purpose                       |
| --------------------- | ----------------------------- |
| PRD.md                | Product requirements          |
| TechSpec.md           | Technology and architecture   |
| AppFlow.md            | Application workflows         |
| Design.md             | UI/UX specification           |
| Schema.md             | Database design               |
| ImplementationPlan.md | Development phases            |
| Tracker.md            | Development progress          |
| Rules.md              | Coding and architecture rules |
| Security.md           | Security requirements         |

These documents form the project's engineering source of truth.

---

# 🗺️ Development Roadmap

### Phase 1

Project foundation

### Phase 2

Authentication & authorization

### Phase 3

Real-time chat

### Phase 4

NLP and intent detection

### Phase 5

Workflow automation

### Phase 6

Business API integrations

### Phase 7

Asynchronous processing

### Phase 8

Human-agent handoff

### Phase 9

Analytics

### Phase 10

Voice AI

### Phase 11

Security hardening

### Phase 12

Testing and production deployment

---

# 📌 Current Status

> 🚧 **Under Active Development**

UltronAI is being developed incrementally following the implementation plan defined in `docs/ImplementationPlan.md`.

---

# 🎯 Engineering Goals

UltronAI is being built with emphasis on:

* Clean architecture
* Scalable backend services
* Asynchronous programming
* Real-time communication
* AI/NLP integration
* Secure multi-tenant architecture
* Reliable API integrations
* Production-quality code
* Automated testing
* Maintainable documentation

---

# 📄 License

License information will be added when the project is ready for public release.

---

# 👨‍💻 Author

**Pruthibiraj Swain**

UltronAI — Intelligent Conversational AI & Automation Platform

````

### `AGENTS.md`

This one should be much more strict. **Don't make it a second README.** Its job is to control the coding agent.

:::writing{variant="document" id="84216"}
# UltronAI — AI Coding Agent Instructions

## 1. Project Identity

You are working on **UltronAI**, an enterprise-oriented conversational AI and automation platform.

UltronAI provides:

- AI-powered chat
- Voice AI
- NLP-based intent detection
- Entity extraction
- Workflow automation
- External API integrations
- Real-time communication
- Human-agent handoff
- Multi-tenant architecture
- Analytics

The goal is to build a complete, reliable, secure, production-quality application.

Do not treat UltronAI as a simple chatbot demo.

---

# 2. Source of Truth

Before implementing or modifying functionality, read the relevant documentation.

Mandatory documentation:

```text
docs/PRD.md
docs/TechSpec.md
docs/AppFlow.md
docs/Design.md
docs/Schema.md
docs/ImplementationPlan.md
docs/Tracker.md
docs/Rules.md
docs/Security.md
````

Priority order:

```text
Security.md
    ↓
Rules.md
    ↓
PRD.md
    ↓
TechSpec.md
    ↓
Schema.md
    ↓
AppFlow.md
    ↓
Design.md
    ↓
ImplementationPlan.md
    ↓
Tracker.md
```

If documentation conflicts with existing code, do not blindly choose one.

Determine the correct behavior, then update the relevant documentation before making a large architectural change.

---

# 3. Before Changing Code

Always:

1. Inspect the repository.
2. Identify the affected module.
3. Read the relevant documentation.
4. Understand existing implementations.
5. Search for existing reusable functionality.
6. Identify dependencies.
7. Determine possible side effects.
8. Make the smallest appropriate change.
9. Test the change.

Never modify code blindly.

---

# 4. Architecture

Follow the documented architecture.

Primary structure:

```text
frontend/
backend/
ai-service/
docker/
docs/
```

Backend architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

Supporting layers:

```text
DTO
Mapper
Exception
Security
Configuration
Integration
Messaging
WebSocket
```

Do not bypass established layers without a valid architectural reason.

---

# 5. Backend Rules

Backend:

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* REST APIs
* WebSocket
* Maven

Controllers must remain thin.

Controllers should:

* Receive requests
* Validate requests
* Call services
* Return responses

Controllers must not contain complex business logic.

Business logic belongs in services.

Database access belongs in repositories.

---

# 6. API Rules

All APIs must use:

```text
/api/v1
```

Use correct HTTP methods:

```text
GET
POST
PUT
PATCH
DELETE
```

Use correct status codes.

Do not expose entities directly.

Use:

```text
Request DTO
Response DTO
```

API responses must be consistent.

---

# 7. Frontend Rules

Frontend:

* React
* TypeScript
* Vite

Organize code logically:

```text
components/
pages/
layouts/
services/
hooks/
store/
types/
utils/
```

Do not place large amounts of business logic inside React components.

Reusable functionality must be extracted into appropriate hooks, services, or utilities.

Never duplicate components when an existing reusable component can be extended.

---

# 8. AI/NLP Rules

The AI layer is responsible for:

* Intent detection
* Entity extraction
* Confidence scoring
* NLP processing

AI output must never bypass backend authorization.

AI-generated decisions must not directly execute sensitive operations.

For example:

```text
AI says:
"Cancel order"

NOT:

AI → Database

Correct:

AI
 ↓
Intent
 ↓
Backend Authorization
 ↓
Business Validation
 ↓
Order Service
 ↓
Database
```

The backend is always the final authority.

---

# 9. Confidence Handling

Every intent prediction should provide a confidence score.

Example:

```text
Intent: ORDER_TRACKING
Confidence: 0.94
```

If confidence is below the configured threshold:

```text
AI
 ↓
Clarification
 ↓
Retry
 ↓
Human Handoff
```

Do not confidently execute an uncertain action.

---

# 10. Workflow Engine

Workflows must be deterministic and validated.

Typical workflow:

```text
START
 ↓
INTENT
 ↓
COLLECT ENTITY
 ↓
VALIDATE
 ↓
API CALL
 ↓
CONDITION
 ↓
RESPONSE
 ↓
END
```

Workflows must support failure handling.

Every external API call must have:

* Timeout
* Error handling
* Appropriate retry policy
* Logging
* Safe fallback

---

# 11. Asynchronous Processing

Use asynchronous architecture where it provides real value.

RabbitMQ should be used for suitable background/event-driven tasks such as:

* Notifications
* Analytics events
* Long-running workflows
* Integration events
* Background processing

Do not introduce asynchronous complexity into simple synchronous operations without a reason.

Every asynchronous operation must have a failure strategy.

---

# 12. WebSocket Rules

WebSocket is used for:

* Chat
* Agent communication
* Notifications
* Typing indicators
* Conversation updates

Every WebSocket connection must be authenticated.

Every subscription must verify:

```text
User
Tenant
Conversation
Permission
```

Never trust client-provided conversation ownership.

---

# 13. Multi-Tenant Rules

Tenant isolation is mandatory.

Every tenant-specific resource must be associated with a tenant.

Examples:

```text
users
agents
conversations
messages
workflows
integrations
tickets
analytics
```

Never allow:

```text
Tenant A → Tenant B data
```

Tenant ID should preferably come from the authenticated security context rather than blindly trusting request parameters.

---

# 14. Database Rules

Use MySQL.

Follow:

* Normalized schema
* Foreign keys
* Appropriate indexes
* Transactions
* Pagination
* Efficient queries

Avoid N+1 queries.

Do not use destructive database operations without explicit requirements.

Do not modify the schema casually.

If schema changes are required:

1. Update `docs/Schema.md`.
2. Implement the migration/change.
3. Update affected entities.
4. Update repositories/services.
5. Update tests.

---

# 15. Security Rules

Security is mandatory.

Never hardcode:

```text
Passwords
JWT secrets
API keys
Database credentials
OAuth secrets
Third-party credentials
```

Use environment variables or secret management.

Never commit `.env` files containing secrets.

Never log:

```text
Passwords
JWT tokens
API keys
Authorization headers
Sensitive customer data
```

Follow `docs/Security.md`.

---

# 16. Input Validation

Validate all external input.

This includes:

* REST requests
* Query parameters
* Path variables
* WebSocket messages
* File uploads
* Chat messages
* Integration configuration

Never assume frontend validation is sufficient.

Backend validation is mandatory.

---

# 17. Error Handling

Use centralized exception handling.

Never expose:

* Stack traces
* SQL errors
* Internal file paths
* Secrets
* Internal implementation details

to clients.

Return safe and meaningful error responses.

---

# 18. Logging

Use structured logging.

Useful fields:

```text
requestId
userId
tenantId
operation
status
executionTime
errorCode
```

Never log secrets or sensitive customer information.

---

# 19. Testing

Every meaningful feature must include tests.

Backend:

```text
Unit Tests
Integration Tests
Security Tests
```

Frontend:

```text
Component Tests
Integration Tests
```

AI service:

```text
NLP Tests
Intent Tests
Entity Extraction Tests
```

Critical workflows require end-to-end testing.

---

# 20. Definition of Done

A feature is NOT complete merely because the code compiles.

A feature is DONE only when:

```text
Code implemented
      ↓
Validation implemented
      ↓
Error handling implemented
      ↓
Security checked
      ↓
Tests written
      ↓
Tests passing
      ↓
API verified
      ↓
UI verified
      ↓
Documentation updated
      ↓
Tracker updated
```

---

# 21. Bug-Fixing Protocol

When a bug is reported:

### Step 1

Reproduce the bug.

### Step 2

Identify the root cause.

### Step 3

Check related functionality.

### Step 4

Implement the smallest correct fix.

### Step 5

Add a regression test.

### Step 6

Run related tests.

### Step 7

Verify the complete flow.

### Step 8

Update documentation if behavior changed.

Never hide a bug with a temporary workaround.

Never declare a bug fixed without verification.

---

# 22. No Fake Functionality

Never create:

* Fake API responses
* Fake database operations
* Non-functional buttons
* Placeholder success messages
* Hardcoded production data
* Mock functionality presented as real functionality

If something is not implemented, clearly mark it as TODO or not implemented.

---

# 23. No Unnecessary Rewrites

Do not rewrite working modules just because another implementation looks cleaner.

Before changing existing code:

* Understand it.
* Identify the actual problem.
* Preserve working behavior.
* Modify only what is necessary.

Avoid introducing regressions.

---

# 24. Dependency Rules

Before adding a dependency:

1. Check whether an existing dependency already provides the functionality.
2. Verify compatibility with the project stack.
3. Consider security implications.
4. Add only when necessary.

Do not add libraries for trivial functionality.

---

# 25. Git Rules

Use branches:

```text
main
develop
feature/*
bugfix/*
hotfix/*
refactor/*
```

Commit conventions:

```text
feat:
fix:
refactor:
test:
docs:
chore:
```

Commits should describe one logical change.

Do not commit:

```text
.env
secrets
credentials
build artifacts
node_modules
IDE files
```

---

# 26. Documentation Rules

When functionality changes, update documentation.

Possible documents requiring updates:

```text
PRD.md
TechSpec.md
AppFlow.md
Schema.md
Design.md
ImplementationPlan.md
Rules.md
Security.md
Tracker.md
README.md
```

Do not allow implementation and documentation to drift apart.

---

# 27. Tracker Rules

After completing a meaningful task:

Update:

```text
docs/Tracker.md
```

Use:

```text
TODO
IN_PROGRESS
BLOCKED
DONE
```

Never mark work as DONE if it has not been tested.

---

# 28. Implementation Order

Follow:

```text
Phase 1
Foundation

↓

Phase 2
Authentication

↓

Phase 3
Chat

↓

Phase 4
NLP

↓

Phase 5
Workflow Engine

↓

Phase 6
Integrations

↓

Phase 7
Async Processing

↓

Phase 8
Human Handoff

↓

Phase 9
Analytics

↓

Phase 10
Voice

↓

Phase 11
Security Hardening

↓

Phase 12
Testing & Deployment
```

Do not jump randomly between phases unless there is a clear dependency or bug requiring it.

---

# 29. Working With Existing Code

Before creating a new:

* Service
* Controller
* Component
* Hook
* Utility
* Entity
* Repository

search the project first.

Reuse existing implementations where appropriate.

Do not create duplicate functionality.

---

# 30. Communication Rule

When an implementation decision is ambiguous:

Prefer the existing documentation and architecture.

If the ambiguity materially affects architecture, security, database design, or public APIs:

STOP before making a large change.

Explain:

1. What is ambiguous.
2. What the current architecture says.
3. What decision is required.
4. What the impact will be.

Do not silently invent a major architectural decision.

---

# 31. Final Agent Objective

Your objective is not simply to make the application compile.

Your objective is to build:

```text
A complete
        ↓
Secure
        ↓
Scalable
        ↓
Tested
        ↓
Maintainable
        ↓
Production-quality
        ↓
Conversational AI platform
```

for UltronAI.

Prioritize:

1. Correctness
2. Security
3. Reliability
4. Maintainability
5. Performance
6. User experience

Always inspect first.

Plan before modifying.

Implement incrementally.

Test before declaring completion.
