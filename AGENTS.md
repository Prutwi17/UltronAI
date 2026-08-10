# UltronAI — AI Coding Agent Instructions

## 1. Project Overview

You are working on **UltronAI**, an enterprise-oriented Conversational AI and Automation Platform.

UltronAI is designed to provide:

* AI-powered conversational chat
* Voice AI
* NLP-based intent detection
* Entity extraction
* Context-aware conversations
* Workflow automation
* External API integrations
* Real-time communication
* Asynchronous processing
* Human-agent handoff
* Multi-tenant architecture
* Analytics and monitoring

The objective is to build a **complete, secure, scalable, tested, production-quality application**.

Do not treat UltronAI as a simple chatbot demo or college-level CRUD application.

---

# 2. Repository Structure

The project follows this structure:

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
├── backend/
├── ai-service/
├── docker/
│
├── docker-compose.yml
└── .gitignore
```

Do not create random top-level directories without a clear architectural reason.

---

# 3. Documentation Is the Source of Truth

Before implementing a feature, inspect the relevant documentation.

Required documentation:

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
```

Use the documentation to determine:

* Product requirements
* Architecture
* Application flows
* Database structure
* UI/UX behavior
* Security requirements
* Implementation sequence
* Development status

Never invent major functionality when it is already defined in the documentation.

If the code and documentation disagree, investigate the difference before making a large change.

---

# 4. Mandatory Workflow Before Coding

Before changing code:

### Step 1 — Understand

Inspect the existing project structure.

### Step 2 — Locate

Find the relevant:

* Controller
* Service
* Repository
* Entity
* DTO
* Component
* Hook
* API
* Configuration

### Step 3 — Read

Read the relevant documentation.

### Step 4 — Plan

Determine:

* What needs to change
* Which files are affected
* Whether database changes are required
* Whether APIs change
* Whether security is affected
* Whether existing functionality could break

### Step 5 — Implement

Make the smallest correct implementation.

### Step 6 — Test

Test the changed functionality and related functionality.

### Step 7 — Verify

Check:

* Backend
* Frontend
* Database
* APIs
* WebSocket
* Security
* Error handling

### Step 8 — Document

Update documentation and `Tracker.md` when necessary.

---

# 5. Never Modify Code Blindly

Before modifying an existing file:

* Read the file.
* Understand its purpose.
* Check its dependencies.
* Search for usages.
* Check related tests.

Do not overwrite existing code simply because another implementation appears cleaner.

Preserve working functionality unless there is a documented reason to change it.

---

# 6. Technology Stack

## Frontend

```text
React
TypeScript
Vite
Tailwind CSS
React Router
WebSocket
```

## Backend

```text
Java 17
Spring Boot
Spring Security
Spring Data JPA
Hibernate
REST APIs
WebSocket
Maven
```

## AI/NLP

```text
Python
FastAPI
scikit-learn
spaCy
```

## Infrastructure

```text
MySQL
Redis
RabbitMQ
Docker
```

Do not introduce alternative technologies without a clear reason.

---

# 7. Backend Architecture

Follow layered architecture:

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

## Controllers

Controllers must remain thin.

Controllers should:

* Receive requests
* Validate input
* Call services
* Return responses

Controllers must not contain complex business logic.

## Services

Services contain:

* Business rules
* Validation logic
* Workflow decisions
* Transaction boundaries
* Integration orchestration

## Repositories

Repositories handle database access.

Do not place business logic inside repositories.

---

# 8. API Rules

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

Use appropriate HTTP status codes.

Use DTOs for API communication.

Never expose JPA entities directly through public APIs.

Use:

```text
Request DTO
Response DTO
```

Maintain consistent API response and error structures.

---

# 9. API Versioning

Do not introduce breaking API changes casually.

Before changing an existing endpoint:

1. Search frontend usage.
2. Search backend usage.
3. Search tests.
4. Check documentation.
5. Determine compatibility impact.

If an API must change, update:

* Backend
* Frontend
* Tests
* Swagger/OpenAPI
* Documentation

---

# 10. Frontend Architecture

Organize frontend code logically:

```text
src/
├── components/
├── pages/
├── layouts/
├── services/
├── hooks/
├── store/
├── types/
├── utils/
└── assets/
```

Do not place large business logic inside UI components.

Use reusable:

* Components
* Hooks
* Services
* Utilities

Avoid duplicated UI implementations.

Maintain consistent:

* Spacing
* Typography
* Colors
* Components
* Error states
* Loading states

Follow `docs/Design.md`.

---

# 11. Conversational AI Architecture

The conversational pipeline should follow:

```text
User Message
      ↓
Message Processing
      ↓
Intent Detection
      ↓
Entity Extraction
      ↓
Confidence Evaluation
      ↓
Conversation Context
      ↓
Workflow Selection
      ↓
Business Action
      ↓
Response Generation
      ↓
User
```

Do not allow AI output to directly bypass backend business rules.

---

# 12. AI Is Not the Final Authority

The AI layer may determine:

```text
Intent
Entity
Confidence
Suggested Action
```

But the backend must remain the final authority for business operations.

Correct:

```text
AI
 ↓
Intent
 ↓
Backend Authorization
 ↓
Business Validation
 ↓
Service
 ↓
Database / External API
```

Incorrect:

```text
AI
 ↓
Direct Database Operation
```

---

# 13. AI Confidence Rules

Every intent prediction should provide a confidence score.

Example:

```text
Intent: ORDER_TRACKING
Confidence: 0.94
```

If confidence is high:

```text
Intent
 ↓
Workflow
```

If confidence is low:

```text
Intent
 ↓
Clarification
 ↓
Retry
 ↓
Human Handoff
```

Never perform sensitive actions based on uncertain intent.

---

# 14. Sensitive AI Actions

The following operations require backend validation:

* Order cancellation
* Refund
* Account deletion
* Password changes
* Payment operations
* Permission changes
* Administrative actions

AI-generated instructions must never override backend authorization.

---

# 15. Workflow Engine Rules

Workflows must be explicit and deterministic.

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

Every workflow must define:

* Success path
* Failure path
* Timeout behavior
* Retry behavior where appropriate
* Fallback behavior

---

# 16. External API Integration

External integrations must be isolated behind dedicated services/adapters.

Example:

```text
Conversation Service
        ↓
Integration Service
        ↓
Order API Adapter
        ↓
External Order System
```

Do not scatter external API calls throughout controllers or unrelated services.

Every external API call must consider:

* Timeout
* Error handling
* Retry policy
* Authentication
* Logging
* Response validation

Never expose third-party credentials to the frontend.

---

# 17. Asynchronous Processing

Use asynchronous architecture where appropriate.

RabbitMQ may be used for:

* Background jobs
* Notifications
* Analytics events
* Long-running workflows
* Integration events
* Event-driven processing

Typical flow:

```text
Application
    ↓
Event
    ↓
RabbitMQ
    ↓
Consumer
    ↓
Processing
    ↓
Result
```

Every asynchronous process must have a failure strategy.

Where appropriate, implement:

* Retry
* Dead-letter queue
* Idempotency
* Error logging

Do not make simple operations unnecessarily asynchronous.

---

# 18. WebSocket Rules

WebSocket is used for:

* Real-time chat
* Typing indicators
* Agent communication
* Notifications
* Conversation updates

Every WebSocket connection must be authenticated.

Before allowing access, verify:

```text
User
Tenant
Conversation
Permission
```

Never trust conversation IDs supplied by clients without authorization checks.

---

# 19. Multi-Tenant Architecture

Tenant isolation is mandatory.

Tenant-specific resources include:

```text
Users
Agents
Conversations
Messages
Workflows
Intents
Integrations
Tickets
Analytics
```

A user belonging to Tenant A must never access Tenant B's data.

Prefer deriving tenant identity from authenticated security context rather than trusting client-supplied tenant IDs.

Every tenant-specific database query must enforce tenant boundaries.

---

# 20. Database Rules

Database:

```text
MySQL
```

Follow:

* Normalized schema
* Foreign keys
* Appropriate indexes
* Transactions
* Pagination
* Efficient queries

Avoid N+1 queries.

Avoid unnecessary database calls.

Do not directly modify production database structures without an approved migration/change process.

When schema changes are required:

1. Update `docs/Schema.md`.
2. Update entities.
3. Update repositories.
4. Update services.
5. Update DTOs if required.
6. Update tests.
7. Verify existing functionality.

---

# 21. Redis Rules

Redis may be used for:

* Temporary conversation context
* Caching
* Rate limiting
* Short-lived state
* Performance optimization

Do not use Redis as the primary permanent source of business data unless explicitly documented.

---

# 22. Security Rules

Follow `docs/Security.md`.

Never hardcode:

```text
Passwords
JWT secrets
Database credentials
API keys
OAuth secrets
Third-party credentials
```

Use environment variables or secure secret management.

Never commit secrets to Git.

Never log:

```text
Passwords
JWT tokens
API keys
Authorization headers
Sensitive customer information
```

---

# 23. Authentication

Use:

```text
JWT
BCrypt
RBAC
```

Roles:

```text
PLATFORM_ADMIN
TENANT_ADMIN
SUPPORT_AGENT
CUSTOMER
```

Every protected endpoint must enforce authorization.

Authentication is not authorization.

Always verify both where required.

---

# 24. Input Validation

Validate all external input.

Including:

* REST requests
* Query parameters
* Path variables
* WebSocket messages
* Chat messages
* File uploads
* Integration configuration

Never rely only on frontend validation.

Backend validation is mandatory.

---

# 25. Error Handling

Use centralized exception handling.

Never expose:

* Stack traces
* SQL errors
* Internal service details
* File paths
* Secrets

to users.

Return clear and safe error responses.

Example:

```json
{
  "timestamp": "...",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Invalid order ID",
  "path": "/api/v1/orders"
}
```

---

# 26. Logging

Use structured logs.

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

Never log secrets.

Never log sensitive customer information unnecessarily.

---

# 27. Testing Requirements

Every meaningful feature must have tests.

## Backend

```text
Unit Tests
Integration Tests
Security Tests
```

## Frontend

```text
Component Tests
Integration Tests
```

## AI Service

```text
Intent Tests
Entity Extraction Tests
Confidence Tests
Fallback Tests
```

## Critical Flows

Test end-to-end:

```text
Login
Chat
Intent Detection
Workflow Execution
External API Integration
Human Handoff
Tenant Isolation
```

---

# 28. Definition of Done

A feature is NOT complete just because it compiles.

A feature is complete only when:

```text
Implementation
     ↓
Validation
     ↓
Error Handling
     ↓
Security Review
     ↓
Tests
     ↓
Tests Passing
     ↓
API Verification
     ↓
UI Verification
     ↓
Documentation
     ↓
Tracker Updated
```

---

# 29. Bug-Fixing Protocol

When a bug is reported:

### 1. Reproduce

Confirm the problem.

### 2. Investigate

Find the root cause.

### 3. Check Dependencies

Determine what other functionality may be affected.

### 4. Fix

Implement the smallest correct solution.

### 5. Test

Add or update regression tests.

### 6. Verify

Test the complete affected workflow.

### 7. Document

Update documentation if behavior changed.

Never hide a bug with a temporary workaround.

Never claim a bug is fixed without verification.

---

# 30. No Fake Functionality

Never create:

* Fake APIs
* Fake database operations
* Fake success messages
* Non-functional buttons
* Hardcoded business results
* Placeholder functionality presented as completed

If functionality is not implemented, clearly mark it as:

```text
TODO
NOT IMPLEMENTED
```

Do not pretend it works.

---

# 31. No Unnecessary Rewrites

Do not rewrite working modules unnecessarily.

Before replacing code:

* Understand existing implementation.
* Identify the actual problem.
* Check dependencies.
* Preserve working behavior.
* Confirm that the replacement improves the system.

Avoid regressions.

---

# 32. Dependency Rules

Before adding a dependency:

1. Check whether an existing dependency can solve the problem.
2. Check compatibility.
3. Check security.
4. Check maintenance status.
5. Add only when justified.

Do not introduce libraries for trivial functionality.

---

# 33. Git Rules

Branches:

```text
main
develop
feature/*
bugfix/*
hotfix/*
refactor/*
```

Commit prefixes:

```text
feat:
fix:
refactor:
test:
docs:
chore:
```

Each commit should represent one logical change.

Never commit:

```text
.env
credentials
API keys
build artifacts
node_modules
IDE configuration
temporary files
```

---

# 34. Documentation Rules

When implementation changes behavior, update the relevant documentation.

Possible files:

```text
PRD.md
TechSpec.md
AppFlow.md
Design.md
Schema.md
ImplementationPlan.md
Tracker.md
Rules.md
Security.md
README.md
```

Documentation must remain consistent with the actual implementation.

---

# 35. Tracker Rules

Update:

```text
docs/Tracker.md
```

after meaningful development work.

Statuses:

```text
TODO
IN_PROGRESS
BLOCKED
DONE
```

Never mark a task `DONE` unless it has been implemented and tested.

---

# 36. Implementation Sequence

Follow the canonical 15-phase implementation plan defined in `docs/ImplementationPlan.md`.

```text
Phase 1: Project Foundation
        ↓
Phase 2: Authentication
        ↓
Phase 3: Database
        ↓
Phase 4: Chat
        ↓
Phase 5: NLP
        ↓
Phase 6: Workflow Engine
        ↓
Phase 7: Integrations
        ↓
Phase 8: Async Processing
        ↓
Phase 9: Human Handoff
        ↓
Phase 10: Admin Dashboard
        ↓
Phase 11: Analytics
        ↓
Phase 12: Voice
        ↓
Phase 13: Security Hardening
        ↓
Phase 14: Testing
        ↓
Phase 15: Deployment
```

Do not randomly jump between phases unless a dependency or bug requires it.

---

# 37. Reuse Existing Code

Before creating a new:

```text
Controller
Service
Repository
Component
Hook
Utility
Entity
DTO
API
```

search the repository first.

If suitable functionality already exists:

* Reuse it
* Extend it
* Refactor it carefully

Do not create duplicate implementations.

---

# 38. UI/UX Rules

Follow `docs/Design.md`.

The application must feel like a professional enterprise SaaS product.

Avoid:

* Random UI styles
* Excessive animations
* Inconsistent spacing
* Unnecessary gradients
* Placeholder screens
* Broken responsive layouts

Every interactive element must have:

```text
Loading State
Success State
Error State
Disabled State where appropriate
```

---

# 39. Performance Rules

Avoid unnecessary:

* Database queries
* API requests
* React re-renders
* Large payloads
* Blocking operations

Use:

* Pagination
* Caching where appropriate
* Database indexes
* Async processing where justified
* Lazy loading where appropriate

Do not optimize prematurely without evidence.

---

# 40. Accessibility

Frontend must support:

* Keyboard navigation
* Focus states
* Semantic HTML
* Accessible labels
* Appropriate ARIA attributes
* Sufficient color contrast

Accessibility must not be treated as optional polish.

---

# 41. AI Prompt-Injection Protection

Treat all user-provided content as untrusted.

Never allow user input to:

* Override system rules
* Bypass authorization
* Reveal secrets
* Execute arbitrary code
* Execute arbitrary SQL
* Access another tenant
* Modify security configuration

AI-generated output must be treated as untrusted data until validated.

---

# 42. Production Mindset

Always assume UltronAI will eventually be used by real customers.

Prioritize:

```text
1. Correctness
2. Security
3. Reliability
4. Maintainability
5. Performance
6. User Experience
7. Scalability
```

Do not sacrifice security or correctness simply to implement a feature faster.

---

# 43. When Requirements Are Ambiguous

Do not invent major requirements.

If an ambiguity affects:

* Security
* Database architecture
* Public APIs
* Authentication
* Multi-tenancy
* Core workflow
* System architecture

stop and identify the ambiguity before making a large architectural change.

For minor UI or implementation details, choose the simplest solution consistent with the existing documentation.

---

# 44. Final Rule

Before declaring any task complete, ask:

```text
Did I understand the existing code?
Did I follow the documentation?
Did I preserve existing functionality?
Did I implement the actual requirement?
Did I handle errors?
Did I handle security?
Did I test the change?
Did I check related functionality?
Did I update documentation?
Did I update Tracker.md?
```

If the answer to any applicable question is **NO**, the task is not complete.

---

# 45. Final Objective

Build UltronAI as a:

**Complete**

**Secure**

**Scalable**

**Reliable**

**Tested**

**Maintainable**

**Production-quality**

Conversational AI and Automation Platform.

Do not optimize for "code generated."

Optimize for **software that actually works**.
