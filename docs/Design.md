# UltronAI — Design Specification

## 1. Design Philosophy

UltronAI must look like a modern enterprise AI SaaS platform.

Design principles:

- Clean
- Professional
- Futuristic
- Minimal
- Responsive
- Accessible
- Fast

Avoid:

- Excessive animations
- Cluttered interfaces
- Random gradients
- Overly colorful UI
- Unnecessary cards
- Inconsistent spacing

---

# 2. Brand

Product:

UltronAI

Tagline:

"Intelligence That Automates."

Alternative:

"Build. Automate. Converse."

---

# 3. Color System

Primary:

Dark / near-black

Secondary:

White / neutral gray

Accent:

Electric violet / blue

Success:

Green

Warning:

Amber

Error:

Red

Use colors consistently through design tokens.

---

# 4. Typography

Use:

Inter

Font hierarchy:

H1
H2
H3
Body
Caption

---

# 5. Main Pages

## Public

/
 /login
 /register

## Customer

/chat
/conversations
/profile

## Agent

/agent
/agent/conversations

## Tenant Admin

/admin
/admin/agents
/admin/workflows
/admin/intents
/admin/integrations
/admin/analytics

## Platform Admin

/platform
/platform/tenants
/platform/users
/platform/system

---

# 6. Chat Interface

Layout:

Left:

Conversation list

Center:

Chat

Right:

Conversation information

Chat must support:

- User messages
- AI messages
- Typing indicator
- Loading state
- Error state
- File attachment
- Quick replies
- Agent takeover
- Conversation status

---

# 7. Dashboard

Dashboard components:

- KPI cards
- Conversation graph
- Automation rate
- Intent distribution
- Agent performance
- Recent conversations
- System status

---

# 8. Agent Dashboard

Agent dashboard must prioritize active conversations.

Layout:

Conversation queue
↓
Active conversation
↓
Customer details
↓
Conversation history
↓
Action panel

---

# 9. Workflow Builder

Provide a visual workflow builder.

Example:

START
 ↓
Intent
 ↓
Collect Information
 ↓
API Call
 ↓
Condition
 ↓
Response
 ↓
END

Use React Flow or equivalent.

---

# 10. Responsive Design

Support:

Desktop
Tablet
Mobile

Desktop is the primary target.

---

# 11. Accessibility

Implement:

- Keyboard navigation
- Proper labels
- ARIA attributes
- Color contrast
- Focus states
- Screen reader friendly components

---

# 12. UX Rules

Every asynchronous operation must show:

- Loading
- Success
- Error

Never leave users wondering whether an operation completed.

Destructive operations require confirmation.

---

# 13. Empty States

Every list must have a meaningful empty state.

Example:

"No conversations yet."

Provide an appropriate action when possible.

---

# 14. Error States

Errors must be understandable.

Bad:

"Error 500"

Good:

"Unable to load conversations. Please try again."

---

# 15. Chat UX

The chat should feel fast.

Use:

- Optimistic UI where safe
- Typing indicators
- Streaming response support when available
- Auto-scroll
- Message timestamps
- Delivery status