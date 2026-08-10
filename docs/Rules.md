# UltronAI — Development Rules

These rules are mandatory for every implementation.

---

# 1. Architecture Rules

Do not randomly change the architecture.

Follow:

Frontend
↓
Backend
↓
AI Service
↓
Database / External Services

Use asynchronous processing where appropriate.

---

# 2. Code Quality

Write:

- Clean code
- Small methods
- Single responsibility
- Meaningful names
- Reusable components

Avoid:

- Duplicate code
- Huge classes
- Huge methods
- Hardcoded values
- Dead code

---

# 3. Backend Rules

Use:

- Controller
- Service
- Repository
- DTO
- Entity
- Mapper

Controllers must not contain business logic.

Business logic belongs in services.

Database operations belong in repositories.

---

# 4. API Rules

All APIs must use:

/api/v1

Use proper HTTP methods.

GET
POST
PUT
PATCH
DELETE

Use proper HTTP status codes.

---

# 5. DTO Rules

Never expose database entities directly through APIs.

Use request and response DTOs.

---

# 6. Validation

Validate every external input.

Use Bean Validation.

Examples:

@NotNull
@NotBlank
@Email
@Size

---

# 7. Exception Handling

Never expose stack traces to users.

Use centralized exception handling.

All API errors must use a consistent response structure.

---

# 8. Frontend Rules

Do not put API calls directly into UI components when reusable service layers are appropriate.

Keep:

components
pages
services
hooks
types
utils

separated.

---

# 9. State Management

Use centralized state management where necessary.

Avoid unnecessary global state.

---

# 10. AI Rules

AI responses must never directly perform destructive operations without validation.

For actions such as:

- Cancel order
- Refund
- Delete account

the workflow must validate the operation before execution.

---

# 11. Confidence Rules

Every AI intent classification should produce a confidence score.

If confidence is below configured threshold:

Ask for clarification.

If repeated failure:

Escalate to human.

---

# 12. Conversation Rules

Every message must belong to a conversation.

Every conversation must belong to a tenant.

Never allow cross-tenant conversations.

---

# 13. Async Rules

Use asynchronous processing for:

- Long-running operations
- External API calls
- Notifications
- Analytics events
- Background processing

Do not make every operation asynchronous unnecessarily.

---

# 14. Database Rules

Use transactions for multi-step operations.

Avoid N+1 queries.

Create indexes for frequently queried fields.

Never delete critical records without explicit business rules.

---

# 15. Security Rules

Never hardcode:

- Passwords
- API keys
- JWT secrets
- Database credentials

Use environment variables or secret management.

---

# 16. Git Rules

Branch naming:

feature/*
bugfix/*
hotfix/*
refactor/*

Commit format:

feat:
fix:
refactor:
test:
docs:
chore:

---

# 17. Testing Rules

Every important business feature must have tests.

Do not mark a task DONE without testing it.

---

# 18. Documentation Rules

Update documentation when architecture or API behavior changes.

Do not allow documentation to become outdated.

---

# 19. AI Coding Agent Rules

Before modifying code:

1. Read PRD.md
2. Read TechSpec.md
3. Read AppFlow.md
4. Read Design.md
5. Read Schema.md
6. Read ImplementationPlan.md
7. Read Tracker.md
8. Read Rules.md
9. Read Security.md

Understand the existing implementation before changing it.

Never rewrite working code unnecessarily.

Never create duplicate implementations.

Never invent architecture without checking existing documentation.

---

# 20. Completion Rule

A feature is complete only when:

- Code implemented
- Tests written
- Tests pass
- API verified
- UI verified
- Error cases handled
- Security checked
- Tracker updated

---

# 21. Bug Fix Rule

When fixing a bug:

1. Reproduce it
2. Identify root cause
3. Fix root cause
4. Test the fix
5. Check related functionality
6. Update documentation if necessary

Do not hide bugs with temporary workarounds.

---

# 22. No Fake Functionality

Do not create fake buttons that do nothing.

Do not create placeholder APIs that appear functional.

Every implemented UI action must connect to working functionality.

---

# 23. Production Mindset

Build the application as if real customers will use it.

Prioritize:

Correctness
Security
Reliability
Maintainability
Performance
User experience