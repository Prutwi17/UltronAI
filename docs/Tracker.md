# UltronAI — Development Tracker

Status Options:
* `TODO`
* `IN_PROGRESS`
* `BLOCKED`
* `DONE`

---

# Phase 1 — Project Foundation

| Task | Status |
| :--- | :--- |
| Create Git repository & structure | `TODO` |
| Setup frontend Vite project | `TODO` |
| Setup backend Spring Boot project | `TODO` |
| Setup AI FastAPI service | `TODO` |
| Configure MySQL container | `TODO` |
| Configure Redis container | `TODO` |
| Configure RabbitMQ container | `TODO` |
| Configure Docker Compose & .env | `TODO` |

---

# Phase 2 — Authentication

| Task | Status |
| :--- | :--- |
| User entity & Tenant entity | `TODO` |
| User registration endpoint | `TODO` |
| User login & JWT generation | `TODO` |
| Refresh token handling | `TODO` |
| BCrypt password hashing | `TODO` |
| Spring Security RBAC filters | `TODO` |
| Centralized exception handling | `TODO` |

---

# Phase 3 — Database

| Task | Status |
| :--- | :--- |
| All JPA Entities & Repositories | `TODO` |
| `workflow_edges` & dynamic graph mapping | `TODO` |
| DTOs & Mappers | `TODO` |
| Service interfaces & implementations | `TODO` |
| Database migration scripts (Flyway/Liquibase) | `TODO` |

---

# Phase 4 — Chat

| Task | Status |
| :--- | :--- |
| Chat UI components | `TODO` |
| Conversation API & Message API | `TODO` |
| WebSocket STOMP endpoint `/ws` | `TODO` |
| Message persistence | `TODO` |
| Typing indicator handling | `TODO` |

---

# Phase 5 — NLP

| Task | Status |
| :--- | :--- |
| Text preprocessing & normalization | `TODO` |
| Intent classification engine | `TODO` |
| Entity extraction (Regex & spaCy) | `TODO` |
| Confidence scoring & threshold check | `TODO` |
| Fallback & clarification handling | `TODO` |

---

# Phase 6 — Workflow Engine

| Task | Status |
| :--- | :--- |
| Workflow & node execution model | `TODO` |
| Graph traversal (`workflow_edges`) | `TODO` |
| Condition evaluation engine | `TODO` |
| REST `API_CALL` node execution | `TODO` |
| Message generation nodes | `TODO` |

---

# Phase 7 — Integrations

| Task | Status |
| :--- | :--- |
| Customer API integration adapter | `TODO` |
| Order API integration adapter | `TODO` |
| Payment API integration adapter | `TODO` |
| Ticket API integration adapter | `TODO` |

---

# Phase 8 — Async Processing

| Task | Status |
| :--- | :--- |
| RabbitMQ queues & exchange setup | `TODO` |
| Event publisher service | `TODO` |
| Async workflow task consumer | `TODO` |
| Retry mechanism & dead-letter queue (`ultronai.dlq`) | `TODO` |

---

# Phase 9 — Human Handoff

| Task | Status |
| :--- | :--- |
| Agent escalation queue | `TODO` |
| Support agent assignment algorithm | `TODO` |
| WebSocket STOMP notification dispatch | `TODO` |
| Support Agent dashboard UI | `TODO` |
| AI response suppression state machine | `TODO` |

---

# Phase 10 — Admin Dashboard

| Task | Status |
| :--- | :--- |
| Agent management UI | `TODO` |
| Intent & Training example UI | `TODO` |
| React Flow Visual Workflow Builder | `TODO` |
| Integration configuration UI | `TODO` |
| User & Tenant management UI | `TODO` |

---

# Phase 11 — Analytics

| Task | Status |
| :--- | :--- |
| Conversation metrics aggregation | `TODO` |
| Intent distribution analytics | `TODO` |
| Automation rate & deflection metrics | `TODO` |
| Support agent performance analytics | `TODO` |
| Response time metrics dashboard | `TODO` |

---

# Phase 12 — Voice

| Task | Status |
| :--- | :--- |
| Speech-to-Text integration | `TODO` |
| Voice session management | `TODO` |
| Text-to-Speech audio streaming | `TODO` |
| Voice conversation pipeline | `TODO` |

---

# Phase 13 — Security Hardening

| Task | Status |
| :--- | :--- |
| SSRF DNS rebinding & IP blocking | `TODO` |
| Rate limiting filter (Bucket4j/Redis) | `TODO` |
| Input validation & XSS sanitization | `TODO` |
| Security headers & CORS hardening | `TODO` |
| Audit logging listener | `TODO` |

---

# Phase 14 — Testing

| Task | Status |
| :--- | :--- |
| Backend unit tests (JUnit 5 & Mockito) | `TODO` |
| Backend integration tests (Testcontainers) | `TODO` |
| Frontend component tests (Vitest & RTL) | `TODO` |
| NLP service unit tests (pytest) | `TODO` |
| E2E chat & handoff scenario tests | `TODO` |

---

# Phase 15 — Deployment

| Task | Status |
| :--- | :--- |
| Production Dockerfiles (Multi-stage builds) | `TODO` |
| Production Docker Compose configuration | `TODO` |
| Nginx reverse proxy configuration | `TODO` |
| CI/CD GitHub Actions workflow | `TODO` |

---

# Current Sprint

## Sprint Goal
Build the project foundation and infrastructure configuration.

## Current Phase
Phase 1 — Project Foundation

## Current Task
Repository and Infrastructure Setup

## Blockers
None

## Notes
Update this file after completing every meaningful task.