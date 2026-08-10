# UltronAI — Database Schema

## 1. Database Overview

* **Database Engine**: MySQL 8.0+
* **Database Name**: `ultronai`
* **Character Set**: `utf8mb4`
* **Collation**: `utf8mb4_unicode_ci`

---

## 2. Table Specifications

### 2.1 `tenants`
Stores isolated client organizations using the UltronAI platform.

| Column | Data Type | Constraints | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | — | Unique tenant identifier |
| `name` | `VARCHAR(255)` | `NOT NULL` | — | Organization name |
| `slug` | `VARCHAR(100)` | `NOT NULL, UNIQUE` | — | URL-friendly unique tenant slug |
| `status` | `VARCHAR(50)` | `NOT NULL` | `'ACTIVE'` | Account status (`ACTIVE`, `SUSPENDED`, `INACTIVE`) |
| `created_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP` | Record creation timestamp |
| `updated_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | Record last update timestamp |

* **Indexes**: `UNIQUE INDEX idx_tenants_slug (slug)`

---

### 2.2 `users`
Stores user accounts across all roles (Platform Admins, Tenant Admins, Support Agents, and Registered Customers).

| Column | Data Type | Constraints | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | — | Unique user identifier |
| `tenant_id` | `BIGINT` | `NULLABLE, FK -> tenants(id)` | `NULL` | Tenant isolation ID (NULL for PLATFORM_ADMIN) |
| `full_name` | `VARCHAR(255)` | `NOT NULL` | — | User's full name |
| `email` | `VARCHAR(255)` | `NOT NULL, UNIQUE` | — | User login email address |
| `password_hash` | `VARCHAR(255)` | `NOT NULL` | — | BCrypt password hash |
| `role` | `VARCHAR(50)` | `NOT NULL` | — | Role (`PLATFORM_ADMIN`, `TENANT_ADMIN`, `SUPPORT_AGENT`, `CUSTOMER`) |
| `active` | `BOOLEAN` | `NOT NULL` | `TRUE` | Account active flag |
| `created_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP` | Record creation timestamp |
| `updated_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | Record last update timestamp |

* **Foreign Keys**: `FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE`
* **Indexes**: `UNIQUE INDEX idx_users_email (email)`, `INDEX idx_users_tenant_role (tenant_id, role)`

---

### 2.3 `agents`
Stores AI Bot Agent configurations owned by specific tenants.

| Column | Data Type | Constraints | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | — | Unique agent identifier |
| `tenant_id` | `BIGINT` | `NOT NULL, FK -> tenants(id)` | — | Tenant isolation ID |
| `name` | `VARCHAR(255)` | `NOT NULL` | — | AI Agent name |
| `description` | `TEXT` | `NULLABLE` | `NULL` | Agent description / purpose |
| `status` | `VARCHAR(50)` | `NOT NULL` | `'DRAFT'` | Agent status (`DRAFT`, `ACTIVE`, `ARCHIVED`) |
| `language` | `VARCHAR(10)` | `NOT NULL` | `'en'` | Primary language code |
| `confidence_threshold` | `DECIMAL(3,2)` | `NOT NULL` | `0.75` | Default fallback confidence threshold (0.00 - 1.00) |
| `created_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP` | Record creation timestamp |
| `updated_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | Record last update timestamp |

* **Foreign Keys**: `FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE`
* **Indexes**: `INDEX idx_agents_tenant_id (tenant_id)`

---

### 2.4 `intents`
Defines NLP intents associated with an AI Agent.

| Column | Data Type | Constraints | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | — | Unique intent identifier |
| `tenant_id` | `BIGINT` | `NOT NULL, FK -> tenants(id)` | — | Direct tenant isolation ID |
| `agent_id` | `BIGINT` | `NOT NULL, FK -> agents(id)` | — | Parent AI Agent ID |
| `name` | `VARCHAR(100)` | `NOT NULL` | — | Intent key name (e.g. `ORDER_TRACKING`) |
| `description` | `TEXT` | `NULLABLE` | `NULL` | Intent description |
| `confidence_threshold` | `DECIMAL(3,2)` | `NULLABLE` | `NULL` | Custom intent confidence threshold override |
| `created_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP` | Record creation timestamp |
| `updated_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | Record last update timestamp |

* **Foreign Keys**: `FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE`, `FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE CASCADE`
* **Indexes**: `UNIQUE INDEX idx_intents_agent_name (agent_id, name)`, `INDEX idx_intents_tenant_id (tenant_id)`

---

### 2.5 `intent_examples`
Training phrases/examples used by the NLP service for intent classification.

| Column | Data Type | Constraints | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | — | Unique example identifier |
| `tenant_id` | `BIGINT` | `NOT NULL, FK -> tenants(id)` | — | Direct tenant isolation ID |
| `intent_id` | `BIGINT` | `NOT NULL, FK -> intents(id)` | — | Parent Intent ID |
| `example_text` | `TEXT` | `NOT NULL` | — | Training text sentence |
| `created_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP` | Record creation timestamp |

* **Foreign Keys**: `FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE`, `FOREIGN KEY (intent_id) REFERENCES intents(id) ON DELETE CASCADE`
* **Indexes**: `INDEX idx_intent_examples_intent_id (intent_id)`, `INDEX idx_intent_examples_tenant_id (tenant_id)`

---

### 2.6 `entities`
Named entities extracted from customer text (e.g. Order ID, Product Name).

| Column | Data Type | Constraints | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | — | Unique entity identifier |
| `tenant_id` | `BIGINT` | `NOT NULL, FK -> tenants(id)` | — | Direct tenant isolation ID |
| `agent_id` | `BIGINT` | `NOT NULL, FK -> agents(id)` | — | Parent AI Agent ID |
| `name` | `VARCHAR(100)` | `NOT NULL` | — | Entity key name (e.g. `orderId`) |
| `type` | `VARCHAR(50)` | `NOT NULL` | — | Extraction type (`REGEX`, `DICTIONARY`, `SYSTEM_DATE`, `SYSTEM_NUMBER`, `SYSTEM_EMAIL`) |
| `extraction_rule` | `TEXT` | `NULLABLE` | `NULL` | Regex string or JSON dictionary map |
| `description` | `TEXT` | `NULLABLE` | `NULL` | Entity description |
| `required` | `BOOLEAN` | `NOT NULL` | `FALSE` | Slot-filling requirement flag |
| `created_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP` | Record creation timestamp |
| `updated_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | Record last update timestamp |

* **Foreign Keys**: `FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE`, `FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE CASCADE`
* **Indexes**: `UNIQUE INDEX idx_entities_agent_name (agent_id, name)`, `INDEX idx_entities_tenant_id (tenant_id)`

---

### 2.7 `workflows`
Automation workflow graph containers for AI agent execution.

| Column | Data Type | Constraints | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | — | Unique workflow identifier |
| `tenant_id` | `BIGINT` | `NOT NULL, FK -> tenants(id)` | — | Direct tenant isolation ID |
| `agent_id` | `BIGINT` | `NOT NULL, FK -> agents(id)` | — | Associated AI Agent ID |
| `name` | `VARCHAR(255)` | `NOT NULL` | — | Workflow name |
| `description` | `TEXT` | `NULLABLE` | `NULL` | Workflow description |
| `status` | `VARCHAR(50)` | `NOT NULL` | `'DRAFT'` | Status (`DRAFT`, `PUBLISHED`, `ARCHIVED`) |
| `version` | `INT` | `NOT NULL` | `1` | Incrementing version number |
| `created_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP` | Record creation timestamp |
| `updated_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | Record last update timestamp |

* **Foreign Keys**: `FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE`, `FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE CASCADE`
* **Indexes**: `INDEX idx_workflows_agent_id (agent_id)`, `INDEX idx_workflows_tenant_id (tenant_id)`

---

### 2.8 `workflow_nodes`
Nodes representing distinct operational steps in a visual workflow graph.

| Column | Data Type | Constraints | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | — | Unique node identifier |
| `tenant_id` | `BIGINT` | `NOT NULL, FK -> tenants(id)` | — | Direct tenant isolation ID |
| `workflow_id` | `BIGINT` | `NOT NULL, FK -> workflows(id)` | — | Parent Workflow ID |
| `node_key` | `VARCHAR(100)` | `NOT NULL` | — | React Flow visual node key |
| `node_type` | `VARCHAR(50)` | `NOT NULL` | — | Type (`START`, `INTENT`, `ENTITY`, `API_CALL`, `CONDITION`, `MESSAGE`, `HANDOFF`, `END`) |
| `node_config` | `JSON` | `NOT NULL` | — | Structured JSON configuration (API URL, templates, condition evaluation parameters) |
| `position_x` | `DOUBLE` | `NOT NULL` | `0.0` | Visual builder UI X coordinate |
| `position_y` | `DOUBLE` | `NOT NULL` | `0.0` | Visual builder UI Y coordinate |
| `created_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP` | Record creation timestamp |
| `updated_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | Record last update timestamp |

* **Foreign Keys**: `FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE`, `FOREIGN KEY (workflow_id) REFERENCES workflows(id) ON DELETE CASCADE`
* **Indexes**: `INDEX idx_workflow_nodes_workflow_id (workflow_id)`, `INDEX idx_workflow_nodes_tenant_id (tenant_id)`

---

### 2.9 `workflow_edges`
Directed edges connecting workflow nodes to support branching and conditional logic.

```text
Workflow Graph Structure:

     [START]
        │
     [INTENT]
        │
    [CONDITION]
    ├── YES (condition_expression: "order.status == 'SHIPPED'") ──> [API CALL] ──> [MESSAGE] ──> [END]
    └── NO  (condition_expression: "default")                  ──> [MESSAGE]  ──> [HANDOFF] ──> [END]
```

| Column | Data Type | Constraints | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | — | Unique edge identifier |
| `tenant_id` | `BIGINT` | `NOT NULL, FK -> tenants(id)` | — | Direct tenant isolation ID |
| `workflow_id` | `BIGINT` | `NOT NULL, FK -> workflows(id)` | — | Parent Workflow ID |
| `edge_key` | `VARCHAR(100)` | `NULLABLE` | `NULL` | React Flow visual edge key |
| `source_node_id` | `BIGINT` | `NOT NULL, FK -> workflow_nodes(id)` | — | Origin node ID |
| `target_node_id` | `BIGINT` | `NOT NULL, FK -> workflow_nodes(id)` | — | Destination node ID |
| `condition_label` | `VARCHAR(100)` | `NULLABLE` | `NULL` | Visual branch label (e.g. "YES", "NO", "MATCH") |
| `condition_expression` | `TEXT` | `NULLABLE` | `NULL` | Spring EL / JSONPath expression evaluated at runtime |
| `created_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP` | Record creation timestamp |

* **Foreign Keys**: `FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE`, `FOREIGN KEY (workflow_id) REFERENCES workflows(id) ON DELETE CASCADE`, `FOREIGN KEY (source_node_id) REFERENCES workflow_nodes(id) ON DELETE CASCADE`, `FOREIGN KEY (target_node_id) REFERENCES workflow_nodes(id) ON DELETE CASCADE`
* **Indexes**: `INDEX idx_workflow_edges_workflow_id (workflow_id)`, `INDEX idx_workflow_edges_source (source_node_id)`, `INDEX idx_workflow_edges_target (target_node_id)`, `INDEX idx_workflow_edges_tenant_id (tenant_id)`

---

### 2.10 `conversations`
Tracks chat and voice conversation sessions between end-users and AI / Support Agents.

| Column | Data Type | Constraints | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | — | Unique conversation identifier |
| `tenant_id` | `BIGINT` | `NOT NULL, FK -> tenants(id)` | — | Tenant isolation ID |
| `agent_id` | `BIGINT` | `NOT NULL, FK -> agents(id)` | — | Target AI Agent ID |
| `customer_id` | `BIGINT` | `NULLABLE, FK -> users(id)` | `NULL` | Customer User ID (NULL if guest visitor) |
| `guest_session_id` | `VARCHAR(255)` | `NULLABLE` | `NULL` | Unique session token for unauthenticated widget visitors |
| `assigned_agent_id` | `BIGINT` | `NULLABLE, FK -> users(id)` | `NULL` | Assigned Human Support Agent ID |
| `channel` | `VARCHAR(50)` | `NOT NULL` | `'WEB_CHAT'` | Channel (`WEB_CHAT`, `VOICE`) |
| `status` | `VARCHAR(50)` | `NOT NULL` | `'ACTIVE'` | Lifecycle status (`NEW`, `ACTIVE`, `WAITING`, `ESCALATED`, `AGENT_ASSIGNED`, `RESOLVED`, `CLOSED`) |
| `started_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP` | Conversation start timestamp |
| `ended_at` | `TIMESTAMP` | `NULLABLE` | `NULL` | Conversation closure timestamp |
| `last_message_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP` | Timestamp of most recent activity |

* **Foreign Keys**: `FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE`, `FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE CASCADE`, `FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE SET NULL`, `FOREIGN KEY (assigned_agent_id) REFERENCES users(id) ON DELETE SET NULL`
* **Indexes**: `INDEX idx_conversations_tenant_status (tenant_id, status)`, `INDEX idx_conversations_customer (customer_id)`, `INDEX idx_conversations_guest_session (guest_session_id)`, `INDEX idx_conversations_assigned_agent (assigned_agent_id)`

---

### 2.11 `messages`
Individual chat messages sent within a conversation.

| Column | Data Type | Constraints | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | — | Unique message identifier |
| `tenant_id` | `BIGINT` | `NOT NULL, FK -> tenants(id)` | — | Direct tenant isolation ID |
| `conversation_id` | `BIGINT` | `NOT NULL, FK -> conversations(id)` | — | Parent Conversation ID |
| `sender_type` | `VARCHAR(50)` | `NOT NULL` | — | Sender role (`CUSTOMER`, `AI`, `AGENT`, `SYSTEM`) |
| `sender_id` | `BIGINT` | `NULLABLE` | `NULL` | Sender user ID if human agent or logged-in customer |
| `message_type` | `VARCHAR(50)` | `NOT NULL` | `'TEXT'` | Type (`TEXT`, `QUICK_REPLY`, `CARD`, `FILE`, `SYSTEM`) |
| `content` | `TEXT` | `NOT NULL` | — | Message payload text / JSON |
| `intent` | `VARCHAR(100)` | `NULLABLE` | `NULL` | Detected intent name for AI/Customer messages |
| `confidence` | `DECIMAL(3,2)` | `NULLABLE` | `NULL` | NLP classification confidence score |
| `created_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP` | Record creation timestamp |

* **Foreign Keys**: `FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE`, `FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE`
* **Indexes**: `INDEX idx_messages_conversation_id (conversation_id)`, `INDEX idx_messages_tenant_id (tenant_id)`, `INDEX idx_messages_created_at (created_at)`

---

### 2.12 `tickets`
Support tickets created automatically by workflows or manually by agents.

| Column | Data Type | Constraints | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | — | Unique ticket identifier |
| `tenant_id` | `BIGINT` | `NOT NULL, FK -> tenants(id)` | — | Tenant isolation ID |
| `conversation_id` | `BIGINT` | `NULLABLE, FK -> conversations(id)` | `NULL` | Originating Conversation ID |
| `customer_id` | `BIGINT` | `NULLABLE, FK -> users(id)` | `NULL` | Customer User ID |
| `assigned_agent_id` | `BIGINT` | `NULLABLE, FK -> users(id)` | `NULL` | Assigned Support Agent ID |
| `title` | `VARCHAR(255)` | `NOT NULL` | — | Ticket summary title |
| `description` | `TEXT` | `NOT NULL` | — | Detailed problem description |
| `priority` | `VARCHAR(50)` | `NOT NULL` | `'MEDIUM'` | Priority (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`) |
| `status` | `VARCHAR(50)` | `NOT NULL` | `'OPEN'` | Status (`OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`) |
| `created_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP` | Record creation timestamp |
| `updated_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | Record last update timestamp |

* **Foreign Keys**: `FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE`, `FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE SET NULL`, `FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE SET NULL`, `FOREIGN KEY (assigned_agent_id) REFERENCES users(id) ON DELETE SET NULL`
* **Indexes**: `INDEX idx_tickets_tenant_status (tenant_id, status)`, `INDEX idx_tickets_assigned_agent (assigned_agent_id)`

---

### 2.13 `integrations`
Configured external REST services and API credentials for third-party systems.

| Column | Data Type | Constraints | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | — | Unique integration identifier |
| `tenant_id` | `BIGINT` | `NOT NULL, FK -> tenants(id)` | — | Tenant isolation ID |
| `name` | `VARCHAR(255)` | `NOT NULL` | — | Integration name (e.g. "Order Fulfillment API") |
| `type` | `VARCHAR(50)` | `NOT NULL` | `'REST_API'` | Integration type (`REST_API`, `WEBHOOK`, `CUSTOM`) |
| `base_url` | `VARCHAR(500)` | `NOT NULL` | — | External base URL (Subject to SSRF allowlist rules) |
| `configuration` | `JSON` | `NULLABLE` | `NULL` | Headers & Auth specs (Secrets stored AES-256 encrypted) |
| `status` | `VARCHAR(50)` | `NOT NULL` | `'ACTIVE'` | Status (`ACTIVE`, `INACTIVE`) |
| `created_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP` | Record creation timestamp |
| `updated_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | Record last update timestamp |

* **Foreign Keys**: `FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE`
* **Indexes**: `INDEX idx_integrations_tenant_id (tenant_id)`

---

### 2.14 `conversation_events`
Audit trail of internal lifecycle events emitted during conversation processing.

| Column | Data Type | Constraints | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | — | Unique event identifier |
| `tenant_id` | `BIGINT` | `NOT NULL, FK -> tenants(id)` | — | Direct tenant isolation ID |
| `conversation_id` | `BIGINT` | `NOT NULL, FK -> conversations(id)` | — | Parent Conversation ID |
| `event_type` | `VARCHAR(100)` | `NOT NULL` | — | Event type (`MESSAGE_RECEIVED`, `INTENT_DETECTED`, `WORKFLOW_STARTED`, `API_CALLED`, `WORKFLOW_COMPLETED`, `ESCALATED`, `AGENT_ASSIGNED`) |
| `payload` | `JSON` | `NULLABLE` | `NULL` | Structured event details / state snapshot |
| `created_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP` | Event creation timestamp |

* **Foreign Keys**: `FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE`, `FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE`
* **Indexes**: `INDEX idx_conv_events_conversation_id (conversation_id)`, `INDEX idx_conv_events_tenant_id (tenant_id)`

---

### 2.15 `notifications`
Real-time system notifications dispatched to Tenant Admins and Support Agents.

| Column | Data Type | Constraints | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | — | Unique notification identifier |
| `tenant_id` | `BIGINT` | `NOT NULL, FK -> tenants(id)` | — | Direct tenant isolation ID |
| `user_id` | `BIGINT` | `NOT NULL, FK -> users(id)` | — | Target User ID |
| `type` | `VARCHAR(50)` | `NOT NULL` | — | Notification type (`HANDOFF_REQUEST`, `SYSTEM_ALERT`, `TICKET_ASSIGNED`) |
| `title` | `VARCHAR(255)` | `NOT NULL` | — | Notification subject |
| `message` | `TEXT` | `NOT NULL` | — | Notification message body |
| `is_read` | `BOOLEAN` | `NOT NULL` | `FALSE` | Read status flag |
| `created_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP` | Record creation timestamp |

* **Foreign Keys**: `FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE`, `FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE`
* **Indexes**: `INDEX idx_notifications_user_read (user_id, is_read)`, `INDEX idx_notifications_tenant_id (tenant_id)`

---

### 2.16 `audit_logs`
Security and administrative compliance log entries.

| Column | Data Type | Constraints | Default | Description |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY, AUTO_INCREMENT` | — | Unique log entry identifier |
| `tenant_id` | `BIGINT` | `NULLABLE, FK -> tenants(id)` | `NULL` | Tenant isolation ID (NULL for platform events) |
| `user_id` | `BIGINT` | `NULLABLE, FK -> users(id)` | `NULL` | Performing User ID |
| `action` | `VARCHAR(100)` | `NOT NULL` | — | Action performed (e.g. `USER_LOGIN`, `WORKFLOW_UPDATE`) |
| `resource` | `VARCHAR(100)` | `NOT NULL` | — | Target entity/resource name |
| `resource_id` | `VARCHAR(100)` | `NULLABLE` | `NULL` | Target resource key/ID |
| `ip_address` | `VARCHAR(45)` | `NULLABLE` | `NULL` | Client IP address |
| `created_at` | `TIMESTAMP` | `NOT NULL` | `CURRENT_TIMESTAMP` | Action timestamp |

* **Foreign Keys**: `FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE SET NULL`, `FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL`
* **Indexes**: `INDEX idx_audit_logs_tenant_id (tenant_id)`, `INDEX idx_audit_logs_user_id (user_id)`, `INDEX idx_audit_logs_created_at (created_at)`

---

## 3. Relationships & ERD Summary

```text
tenants (1) ───< users (N)
tenants (1) ───< agents (N) ───< intents (N) ───< intent_examples (N)
tenants (1) ───< agents (N) ───< entities (N)
tenants (1) ───< agents (N) ───< workflows (N) ───< workflow_nodes (N) ───< workflow_edges (N)
tenants (1) ───< conversations (N) ───< messages (N)
tenants (1) ───< conversations (N) ───< conversation_events (N)
tenants (1) ───< conversations (N) ───< tickets (N)
tenants (1) ───< integrations (N)
tenants (1) ───< notifications (N)
tenants (1) ───< audit_logs (N)
```

---

## 4. Multi-Tenant Data Isolation Enforcement Strategy

To guarantee 100% tenant isolation:
1. **Direct `tenant_id` Primary Columns**: Every tenant-specific table contains an explicit `tenant_id` column indexed for fast lookup.
2. **Spring Data JPA Automatic Filtering**: Backend entity queries leverage `@FilterDef` and `@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")` or Hibernate Discriminator columns to automatically inject `WHERE tenant_id = ?` into generated SQL queries.
3. **Database Security Context Enforcement**: Controllers extract `tenantId` strictly from the validated Spring Security JWT context, never trusting unverified request parameter payloads.