# Insurance Integration Hub — Architecture

## 1. System Overview

Insurance Integration Hub is a two-service Spring Boot microservices demo for an insurance domain.

The project demonstrates:

- REST-based communication between services
- A clean layered Spring Boot architecture
- Docker-based local orchestration
- Kubernetes deployment awareness
- AI-augmented querying using a controlled RAG-style flow
- Awareness of production concerns such as security, resilience, observability, and PII handling

The main system flow is intentionally simple:

```text
Client
  ↓
User Service + AI Agent
  ↓
Policy Service
```

The `User Service` is the main entry point. It owns the AI Agent flow and calls the `Policy Service` when policy data is needed.

```text
┌─────────────┐     HTTP/REST      ┌──────────────────────────┐
│   Client    │ ──────────────────▶│      User Service        │ :8081
│  (Postman)  │                    │   + AI Agent (RAG)       │
└─────────────┘                    └────────────┬─────────────┘
                                                │ WebClient
                                                ▼
                                   ┌────────────────────────┐
                                   │    Policy Service      │ :8082
                                   └────────────────────────┘
```

## 2. Communication Model

### Primary Flow: User Service → Policy Service

The primary production-like flow is one-directional:

```text
User Service → Policy Service
```

The AI Agent lives inside the User Service. When it receives a question, it fetches user data locally and fetches policy data from the Policy Service.

### Demo Flow: Policy Service → User Service

The Policy Service also exposes a `/details` endpoint that calls the User Service.

This endpoint exists only to demonstrate bidirectional inter-service communication.

```text
Policy Service → User Service
```

The AI Agent does **not** depend on this endpoint. This avoids a circular call chain such as:

```text
User Service → Policy Service → User Service
```

In a larger production system, I would be careful with bidirectional service calls because they increase coupling and can create cascading failures. For larger systems, I would consider:

- A dedicated orchestration service
- API Gateway / BFF pattern
- Event-driven communication using Kafka or RabbitMQ

---

## 3. API Surface

### User Service — Port 8081

```text
GET    /users                          — get all users
GET    /users/{id}                     — get user by id
GET    /users/by-policy/{policyNumber} — get users by policy number
POST   /users                          — create user
PUT    /users/{id}                     — update user
DELETE /users/{id}                     — delete user
POST   /agent/query                    — AI-augmented query using RAG
GET    /actuator/health                — health check
```

### Policy Service — Port 8082

```text
GET    /policies                         — get all policies
GET    /policies/{id}                    — get policy by id
GET    /policies/user/{userId}           — get policies by user
GET    /policies/user/{userId}/details   — combined data endpoint;
                                           demo-only bidirectional call to User Service
POST   /policies                         — create policy
PUT    /policies/{id}                    — update policy
DELETE /policies/{id}                    — delete policy
GET    /actuator/health                  — health check
```

---

## 4. Service Responsibilities

### User Service

The User Service is responsible for:

- Managing user data
- Exposing user CRUD APIs
- Hosting the AI Agent endpoint
- Fetching policy context from the Policy Service
- Building the RAG prompt
- Calling Claude API
- Returning a natural-language answer to the client

### Policy Service

The Policy Service is responsible for:

- Managing policy data
- Exposing policy CRUD APIs
- Returning policies by user ID
- Demonstrating a combined `/details` endpoint that calls User Service

---

## 5. Design Patterns Used

| Pattern | Where | Purpose |
|---|---|---|
| Repository | `UserRepository`, `PolicyRepository` | Isolates data access from business logic |
| Service Layer | `UserService`, `PolicyService`, `AgentService` | Keeps business logic out of controllers |
| MVC | Controller → Service → Repository | Provides a clean layered architecture |
| Dependency Injection | Constructor injection / `@RequiredArgsConstructor` | Lets Spring manage dependencies |
| Interface | Service interfaces and clients | Decouples contracts from implementations |
| Mapper | `UserMapper`, `PolicyMapper` | Converts between entities and DTOs |
| DTO | Request and response objects | Separates API contracts from DB entities |
| Builder | DTO builders | Makes object creation readable |
| Spring Singleton Scope | `@Service`, `@Repository` beans | Spring creates one shared bean instance by default |
| Orchestrator | `AgentService` | Coordinates a multi-step AI workflow |

---

## 6. Architecture Decisions

| Decision | Reason |
|---|---|
| Two services | Keeps the demo focused while still showing microservice communication |
| Primary one-way AI flow | Avoids circular service calls in the main use case |
| Demo-only bidirectional endpoint | Shows cross-service integration while keeping the main flow clean |
| WebClient for inter-service calls | Modern Spring HTTP client; used here for REST communication and can support non-blocking flows |
| OkHttp for Claude API | Simple synchronous external API call for demo purposes |
| H2 database | Fast local demo with no database installation required |
| Docker Compose | Runs the full local system with one command |
| No `depends_on` in Compose | Avoids circular startup dependencies; services handle unavailability in code |
| Kubernetes manifests | Demonstrates deployment awareness |
| Claude Haiku model | Cost-effective model suitable for structured demo queries |

---

## 7. AI Agent Architecture

### What This Project Implements

This project implements a **controlled, orchestrated AI flow**.

It is not a fully autonomous agent. The flow is predefined, predictable, and easy to debug.

```text
POST /agent/query  { "question": "...", "userId": 1 }
        ↓
AgentController
        ↓
AgentService
        ├── Step 1: Fetch user data from User Service DB
        ├── Step 2: Fetch policy data from Policy Service
        ├── Step 3: Combine user + policy context
        ├── Step 4: Build a RAG prompt
        └── Step 5: Call Claude API
        ↓
Natural-language answer
```

The AI Agent calls:

```text
GET /policies/user/{userId}
```

It does **not** call:

```text
GET /policies/user/{userId}/details
```

This keeps the AI flow clean and avoids a circular call chain.

---

## 8. RAG — Retrieval Augmented Generation

RAG means retrieving relevant data from application systems before asking the model to generate an answer.

### In This Project

```text
Retrieve:
- User data from the User Service database
- Policy data from the Policy Service

Augment:
- Inject the retrieved data into the Claude prompt

Generate:
- Claude answers based on the provided context
```

### Why RAG Matters

- The model answers based on real application data
- It reduces hallucinations
- It avoids the need for fine-tuning
- It keeps answers fresh because the system reads live data
- It fits enterprise integration scenarios

### Implemented vs Future

| Feature | Status |
|---|---|
| Fetch real data from DB | Implemented |
| Inject retrieved data into prompt | Implemented |
| Call Claude API | Implemented |
| Cross-service context enrichment | Implemented |
| PII masking before Claude call | Future improvement |
| Long-term memory / vector database | Future improvement |
| Session memory across calls | Future improvement |

For demo simplicity, the prompt is built from application data as-is. In a production life/health insurance environment, sensitive fields should be minimized or masked before calling any external LLM.

---

## 9. Agentic AI Concepts

### Orchestrator

The orchestrator controls the workflow.

In this project:

```text
AgentService = Orchestrator
```

It decides what data to fetch, builds the context, and calls the model.

### Skills

A skill is a reusable capability the agent can use.

In this project, skills are implemented as regular service methods:

| Skill | Implementation |
|---|---|
| Fetch user details | `UserService` |
| Fetch policy details | `fetchPoliciesForUser()` in `AgentService` |
| Build prompt | `AgentService` |
| Call Claude API | Claude client logic |
| Handle missing data | Validation and fallback logic |

### SubAgents

This project does not implement independent subagents.

Instead, it demonstrates the concept through separated responsibilities inside `AgentService`.

A larger system could split these into independent subagents:

```text
Orchestrator Agent
  ├── User Data SubAgent
  ├── Policy Data SubAgent
  ├── Error Analysis SubAgent
  └── Documentation SubAgent
```

### MCP — Model Context Protocol

MCP is a standard way for AI applications to connect to external tools and data sources.

This project does not implement a real MCP server.

Instead, `fetchPoliciesForUser()` manually demonstrates the same idea at a simpler level: the AI flow retrieves data from an external system before generating an answer.

In a future version, these capabilities could be exposed as MCP tools:

```text
get_user_by_id
get_policies_by_user_id
summarize_policy_data
analyze_integration_error
```

### Context Window Management

LLMs have limited context. Context window management means sending only the information that is relevant, safe, and useful.

Good context:

- User question
- Relevant user fields
- Relevant policies
- Clear instruction to answer only from provided data
- Expected output format

Bad context:

- Unrelated records
- Raw logs
- Duplicated data
- Sensitive data that is not needed
- Large unfiltered payloads

### Short Memory vs Long Memory

| Area | Short Memory | Long Memory |
|---|---|---|
| Meaning | Current prompt/request context | Persistent data stored outside the model |
| Lifespan | One API call or conversation | Across sessions |
| Project status | Implemented through prompt context | Future improvement |
| Example | User + policies for current question | pgvector, Pinecone, Redis, Elasticsearch |

---

## 10. Inter-Service Communication

### Main AI Flow

```text
AgentService.fetchPoliciesForUser(userId)
    → HTTP GET http://policy-service:8082/policies/user/{userId}
        → PolicyController.getPoliciesByUserId()
            → PolicyService.getPoliciesByUserId()
                → PolicyRepository.findByUserId()
```

### Demo Bidirectional Flow

```text
GET /policies/user/{userId}/details
    → PolicyService.getUserPolicyDetails()
        → UserServiceClient.getUserById()
            → HTTP GET http://user-service:8081/users/{userId}
                → UserController.getUserById()
```

### Resilience Behavior

Both services handle downstream failures gracefully.

| Scenario | Expected Behavior |
|---|---|
| Policy Service down | Agent responds with user data only and explains policy data is unavailable |
| User Service down during `/details` call | Policy Service returns a meaningful error |
| User not found | API returns a clear not-found message |
| No policies for user | AI answer explains that no policy data is available |
| Claude API unavailable | User Service returns “AI service temporarily unavailable” |
| Invalid request payload | API returns `400 Bad Request` with validation details |

Production improvement:

- Add timeouts
- Add retries with limits
- Add circuit breakers with Resilience4j
- Add correlation IDs
- Add structured logs
- Add distributed tracing with OpenTelemetry

---

## 11. DevOps Architecture

### Docker

Each service uses a two-stage Dockerfile.

```text
Stage 1 — Builder:
- Uses JDK image
- Compiles source code
- Produces executable JAR

Stage 2 — Runtime:
- Uses smaller JRE image
- Runs only the compiled JAR
- Reduces image size and attack surface
```

### Docker Compose

Docker Compose is used for local orchestration.

```text
insurance-network
    ├── user-service   :8081
    └── policy-service :8082
```

There is no hard `depends_on` rule.

Reason:

> Because the project includes bidirectional communication, a strict startup dependency can create a circular startup problem. Instead, both services start independently and handle temporary downstream unavailability in code.

### Kubernetes

```text
k8s/
    ├── user-service-deployment.yaml
    ├── user-service-service.yaml
    ├── policy-service-deployment.yaml
    └── policy-service-service.yaml
```

Recommended service exposure:

| Service | Kubernetes Service Type | Reason |
|---|---|---|
| User Service | LoadBalancer or Ingress-facing Service | Client-facing entry point |
| Policy Service | ClusterIP | Internal service only |

Each deployment should include:

- Readiness probe
- Liveness probe
- CPU and memory requests
- CPU and memory limits
- Environment variables for service URLs
- Secret references for API keys

---

## 12. Security Considerations

This is a demo project. For production, especially in a life/health insurance environment, the following would be required:

- Replace H2 with PostgreSQL, Oracle, or SQL Server
- Add authentication with Spring Security, JWT, or OAuth2
- Add service-to-service authentication
- Mask PII before calling external LLM APIs
- Store Claude API keys in Kubernetes Secrets or Vault
- Enable HTTPS/TLS
- Validate and sanitize all request payloads
- Validate and sanitize all prompt inputs
- Add audit logs for data access
- Avoid logging sensitive user or policy data
- Add rate limiting for AI API calls

---

## 13. Production Improvements

| Area | Current | Production |
|---|---|---|
| Database | H2 in-memory | PostgreSQL / Oracle / SQL Server |
| Authentication | None | Spring Security + JWT/OAuth2 |
| Authorization | None | Role-based access control |
| Service auth | None | Service-to-service tokens |
| Observability | Spring Actuator | OpenTelemetry + Jaeger |
| Logging | Console logs | Structured JSON logs + ELK |
| Resilience | Try/catch | Resilience4j circuit breaker and retries |
| Secrets | Environment variables | Kubernetes Secrets / Vault |
| CI/CD | Manual push | GitHub Actions pipeline |
| API Gateway | None | Spring Cloud Gateway |
| Long Memory | None | pgvector / Pinecone / Elasticsearch |
| Correlation IDs | None | MDC + request tracing |
| PII masking | None | Mask before Claude API calls |

---

## 14. AI-Assisted Development

Claude Code CLI was used during development for:

- Generating boilerplate suggestions
- Reviewing and improving architecture
- Assisting with multi-file feature implementation
- Assisting with test generation and running test commands
- Suggesting refactoring improvements

All AI-generated code was reviewed, understood, and tested before committing.

Commits that used Claude Code can be tagged with:

```text
Co-Authored-By: Claude
```

---

## 15. Architecture Summary

The main system flow is intentionally simple and easy to reason about:

```text
Client → User Service + AI Agent → Policy Service
```

The AI Agent uses a RAG-style flow: it retrieves user data locally, retrieves policy data from the Policy Service, builds a prompt with that context, and sends it to Claude.

The `/details` endpoint demonstrates bidirectional service communication, but it is not part of the main AI flow. This keeps the core architecture cleaner while still showing cross-service integration capability.

---

## 16. What Is and Is Not Implemented

### Implemented

- REST CRUD microservices for users and policies
- Primary one-way inter-service communication: User Service → Policy Service
- Demo-only bidirectional endpoint: Policy Service → User Service
- DTOs, mappers, service interfaces, and custom exceptions
- Input validation and global exception handling
- RAG-style AI Agent endpoint: `POST /agent/query`
- Cross-service context enrichment
- Unit tests with Mockito
- Web MVC tests
- Two-stage Dockerfiles
- Docker Compose local orchestration
- Kubernetes deployment and service manifests
- Claude Code CLI used during development

### Not Implemented Yet

- Real MCP server
- Independent subagents
- Long-term memory or vector database
- PII masking before Claude API calls
- Authentication and authorization
- Production database
- Full observability stack
- Circuit breaker and retry policies
- CI/CD pipeline