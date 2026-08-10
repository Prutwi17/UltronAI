# UltronAI — Security Specification

## 1. Security Principles

UltronAI follows:

- Least privilege
- Defense in depth
- Secure by default
- Zero trust
- Tenant isolation
- Input validation
- SSRF prevention

---

## 2. Authentication

Use JWT authentication.

Access tokens must be short-lived (e.g., 15 minutes).

Refresh tokens must be handled securely (stored HTTP-only, secure cookies or encrypted database tokens).

Passwords must be hashed using BCrypt (work factor >= 12).

Never store plaintext passwords.

---

## 3. Authorization

Use Role-Based Access Control (RBAC).

Roles:

* `PLATFORM_ADMIN`: Global system management across all tenants.
* `TENANT_ADMIN`: Administrative management of a specific tenant (Agents, Workflows, Integrations, Users).
* `SUPPORT_AGENT`: Handles escalated conversations and customer tickets.
* `CUSTOMER`: Interacts with AI agents via web chat widget or voice.

Every protected API endpoint must verify authorization at both controller and service levels.

---

## 4. Tenant Isolation

Tenant ID must be validated for every tenant-specific request.

Never trust tenant ID supplied directly by the client payload.

Derive tenant identity from authenticated user context (JWT claims) where possible.

Flow:

```text
JWT
 ↓
Authenticated User Context
 ↓
Extract Verified Tenant ID
 ↓
Inject into Database Query Filter
```

---

## 5. Password Security

Password requirements:

- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one number
- At least one special character

Passwords must never appear in application logs or exception stack traces.

---

## 6. API Security

Implement:

- HTTPS (TLS 1.3 preferred, TLS 1.2 minimum)
- Authentication via JWT Authorization headers
- Role-based authorization checks
- Input validation via Java Bean Validation (`@NotNull`, `@Valid`, `@Pattern`)
- Rate limiting per user and per tenant
- Restricted CORS configuration
- Security headers (`Content-Security-Policy`, `X-Frame-Options`, `X-Content-Type-Options`, `Strict-Transport-Security`)

---

## 7. Rate Limiting

Rate limit critical operations:

* Login / Token Refresh: 5 requests / minute per IP
* Password Reset: 3 requests / 15 minutes per email
* Public Chat / WebSocket Messages: 60 requests / minute per session
* Authenticated REST APIs: 100 requests / minute per user

---

## 8. SSRF (Server-Side Request Forgery) Protection for Dynamic Workflows & Integrations

Because UltronAI allows dynamic `API_CALL` workflow nodes and configurable `integrations` base URLs, strict Server-Side Request Forgery (SSRF) protection is mandatory.

The system MUST prevent any workflow execution or external API integration from targeting internal services, local networks, cloud metadata endpoints, or non-HTTP protocols.

### 8.1 Scheme & Protocol Restrictions
* **Allowed Schemes**: Only `http://` and `https://` are permitted.
* **Blocked Schemes**: Immediately reject requests using `file://`, `gopher://`, `dict://`, `ftp://`, `sftp://`, `ldap://`, `tftp://`, `jar://`, or `netdoc://`.

### 8.2 IP & Hostname Blacklisting
Outbound requests targeting the following IP ranges or hostnames MUST be blocked at the HTTP client resolver level:

```text
Target Blacklist Range               Description
─────────────────────────────────────────────────────────────────────────────
127.0.0.0/8, ::1                      IPv4 / IPv6 Loopback (localhost)
10.0.0.0/8                            RFC 1918 Private Class A
172.16.0.0/12                         RFC 1918 Private Class B
192.168.0.0/16                        RFC 1918 Private Class C
0.0.0.0/8                             Broadcast / Current Network
169.254.0.0/16, fe80::/10             IPv4 / IPv6 Link-Local / AWS/GCP/Azure Metadata
169.254.169.254                       Cloud Instance Metadata Endpoint
100.64.0.0/10                         Carrier-Grade NAT Range
224.0.0.0/4                           Multicast
metadata.google.internal              GCP Metadata Hostname
instance-data                         AWS Metadata Hostname
```

### 8.3 DNS Rebinding Protection
To prevent DNS Rebinding attacks where a public domain dynamically resolves to a private IP:
1. The HTTP client MUST resolve the target hostname to an IP address **prior to establishing a TCP connection**.
2. The resolved IP address MUST be validated against the IP Blacklist before the socket connection is initiated.
3. The socket connection MUST connect directly to the verified IP address rather than re-resolving the hostname.

### 8.4 Redirect Validation
* Automatic HTTP redirects (`301`, `302`, `307`, `308`) MUST be disabled by default.
* If redirects are allowed, each redirect location URL MUST be re-validated through the full DNS and IP Blacklist verification before following the location header.
* Maximum redirect depth MUST NOT exceed 3 hops.

### 8.5 Domain Allowlisting
* Tenants may configure a Domain Allowlist in Tenant Settings.
* If a domain allowlist is defined for a tenant, workflow `API_CALL` nodes and `integrations` may ONLY make requests to domains explicitly present on the allowlist.

### 8.6 Outbound Request Timeouts & Payload Limits
* **Connect Timeout**: Maximum 3000ms (3 seconds).
* **Read / Socket Timeout**: Maximum 5000ms (5 seconds).
* **Response Body Size Limit**: Maximum 1 MB payload response. Any response exceeding 1 MB must be aborted immediately to prevent resource exhaustion.

### 8.7 Network Egress Filtering
* At the container and network firewall level (Docker / AWS Security Group), outgoing egress traffic from the application layer to internal cluster networks (MySQL 3306, Redis 6379, RabbitMQ 5672) MUST be restricted. Dynamic API integration calls MUST route through a dedicated egress proxy gateway.

---

## 9. Input Validation & Injection Protection

Validate:

- Request body
- Query parameters
- Path parameters
- File uploads
- Chat messages

Protect against:

- SQL injection (Parameterized JPA queries)
- XSS (HTML escaping user content)
- Command injection (No OS shell execution)
- Path traversal (Strict filename sanitization)

---

## 10. CORS Policy

Only allow trusted frontend origins configured via environment variables (e.g., `ALLOWED_ORIGINS=https://app.ultronai.com`).

Wildcard origins (`*`) are strictly forbidden in production environments.

---

## 11. Secret Management

Never commit secrets to Git repository.

Secrets (Database passwords, JWT keys, API tokens) must be injected exclusively via environment variables or secret vaults.

---

## 12. External Integrations Security

Integration credentials must be stored AES-256 encrypted at rest in the database.

Third-party API keys must never be returned to frontend clients.

---

## 13. WebSocket Security

Authenticate WebSocket connections during the initial STOMP connection handshake:
1. Token MUST be passed in the STOMP `CONNECT` frame `X-Authorization` header.
2. Validate user identity, tenant access, and target conversation authorization before allowing subscription to `/topic/conversations/{id}`.
3. Cross-tenant subscriptions MUST be rejected immediately.

---

## 14. AI Security & Prompt Injection Protection

Treat all customer messages as untrusted input:
- AI prompt templates must demarcate untrusted input using strict system delimiters.
- User input MUST NOT override system rules or security constraints.
- Sensitive business actions (cancellation, refund, ticket closure) MUST be enforced by deterministic backend Java business logic, not AI generation.

---

## 15. Audit Logging

Record security-sensitive events in `audit_logs`:

- Login / Logout / Failed Login
- Role changes / Password changes
- Workflow updates / Integration updates
- Handoff requests & Agent assignments

Never store passwords, secrets, or raw JWT tokens in audit log payloads.

---

## 16. Security Definition of Done

A feature is complete only when:

- Authentication & RBAC checked
- Input validated & sanitized
- Tenant isolation verified
- SSRF checks enforced on external HTTP calls
- Sensitive data encrypted / masked
- Security tests passing