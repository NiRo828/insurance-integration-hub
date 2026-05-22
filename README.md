# Insurance Integration Hub

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen)
![Docker](https://img.shields.io/badge/Docker-ready-blue)
![Kubernetes](https://img.shields.io/badge/Kubernetes-manifests-blue)
![AI](https://img.shields.io/badge/AI-RAG%20%2B%20Claude-purple)
![Tests](https://img.shields.io/badge/Tests-33%20passing-success)

A two-service **Spring Boot microservices** project for an insurance domain, built to demonstrate Java backend development, REST-based system integration, Docker/Kubernetes deployment awareness, and AI-augmented querying using a controlled RAG-style flow.

---

## Why I Built This

This project was built as an interview-ready portfolio project for a **Junior Java / Integration Developer** role.

The goal was to demonstrate practical experience with:

- Spring Boot microservices architecture
- REST-based integration between services
- Service-to-service communication using WebClient
- Docker and Kubernetes deployment basics
- AI-assisted development using Claude Code CLI
- Agentic AI concepts: RAG, orchestration, context management, and tool-style data retrieval

---

## Interview Value

This project maps directly to common integration-development responsibilities:

| Role Requirement | Demonstrated In This Project |
|---|---|
| Java Spring Boot | Two Spring Boot 3.5 services with layered architecture |
| REST APIs | Full CRUD endpoints for users and policies |
| Microservices | Independent User Service and Policy Service |
| System integration | User Service calls Policy Service over HTTP via WebClient |
| Design patterns | Repository, Service Layer, Interface, Mapper, DTO, Builder, DI |
| Configuration management | Docker Compose, environment variables, Kubernetes manifests |
| Kubernetes awareness | Deployment and Service YAML with probes and resource limits |
| AI / Agentic AI | RAG-style AI Agent endpoint using Claude API |
| AI coding tools | Claude Code CLI used across the development workflow |
| Testing | Mockito unit tests and WebMvcTest integration tests — 33 passing |

---

## What This Project Demonstrates

- REST microservices with Spring Boot 3.5
- Clean layered architecture: Controller → Service → Repository
- Service interfaces decoupling controllers from implementations
- Inter-service HTTP communication via WebClient
- Input validation, custom exceptions, and global error handling
- DTOs, mappers, and domain entities kept separate
- Unit tests with Mockito and integration tests with WebMvcTest
- Two-stage Docker builds — smaller, more secure runtime images
- Docker Compose for local orchestration with health checks and resource limits
- Kubernetes deployment and service manifests with readiness/liveness probes
- AI-augmented querying using RAG — Retrieval Augmented Generation
- Claude Code CLI as part of the active development workflow

---

## Architecture

```text
┌─────────────┐     HTTP/REST      ┌──────────────────────────┐
│   Client    │ ──────────────────▶│      User Service        │
│  Postman/UI │                    │   User CRUD + AI Agent   │
└─────────────┘                    └────────────┬─────────────┘
                                                │ WebClient
                                                ▼
                                   ┌──────────────────────────┐
                                   │      Policy Service      │
                                   │      Policy CRUD         │
                                   └──────────────────────────┘
```

**Primary flow:** User Service → Policy Service (one-directional for the main AI use case)

The Policy Service also includes a demo-only `/details` endpoint that calls
the User Service to illustrate bidirectional service communication.
The AI Agent does **not** depend on that endpoint — this avoids a circular
call chain (`User → Policy → User`).

In production, bidirectional service dependencies would be replaced with
an orchestration layer, API gateway, or event-driven communication.

For full architecture details see [Architecture Guide](./ARCHITECTURE.md)

---

## Services

| Service | Port | Responsibilities |
|---|---:|---|
| `user-service` | 8081 | User CRUD, AI Agent endpoint, RAG prompt orchestration |
| `policy-service` | 8082 | Policy CRUD, policy lookup by user, demo combined-details endpoint |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5 |
| Database | H2 in-memory (demo/local development) |
| ORM | Spring Data JPA + Hibernate |
| HTTP Clients | WebClient (inter-service), OkHttp (Claude API) |
| Validation | Jakarta Bean Validation |
| Testing | JUnit 5, Mockito, AssertJ, WebMvcTest |
| Containerization | Docker (two-stage builds), Docker Compose |
| Orchestration | Kubernetes manifests |
| AI Model | Claude Haiku — Anthropic |
| AI Development Tool | Claude Code CLI |
| Build Tool | Maven |

---

## Quick Start

### Prerequisites

- Java 17+
- Docker Desktop
- Maven wrapper included (`./mvnw`)
- `ANTHROPIC_API_KEY` for the AI Agent endpoint

### Option 1 — Docker Compose (recommended)

```bash
git clone https://github.com/NiRo828/insurance-integration-hub.git
cd insurance-integration-hub
export ANTHROPIC_API_KEY=your_key_here
docker-compose up --build
```

```text
User Service:   http://localhost:8081
Policy Service: http://localhost:8082
Health checks:
  http://localhost:8081/actuator/health
  http://localhost:8082/actuator/health
```

### Option 2 — Run Locally

**Terminal 1 — User Service:**
```bash
cd user-service
export ANTHROPIC_API_KEY=your_key_here
./mvnw spring-boot:run
```

**Terminal 2 — Policy Service:**
```bash
cd policy-service
./mvnw spring-boot:run
```

---

## API Highlights

### Create a User
```bash
curl -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "policyNumber": "POL-001"
  }'
```

### Create a Policy
```bash
curl -X POST http://localhost:8082/policies \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "policyType": "Life Insurance",
    "premium": 250.00,
    "startDate": "2026-01-01",
    "status": "ACTIVE"
  }'
```

### Query the AI Agent
```bash
curl -X POST http://localhost:8081/agent/query \
  -H "Content-Type: application/json" \
  -d '{
    "question": "Who is the highest paying user?",
    "userId": null
  }'
```

**Example response:**
```json
{
  "answer": "User 1 (John Doe) is the highest paying user with a total premium of $250.00 across 1 active policy.",
  "context": "...",
  "userId": null
}
```

---

## AI Agent — RAG Flow

The `POST /agent/query` endpoint demonstrates **Retrieval Augmented Generation**:

```text
POST /agent/query  { "question": "Who is the highest paying user?" }
        ↓
AgentController → AgentService
        ↓
Step 1: Retrieve user data from User Service DB
Step 2: Retrieve policy data from Policy Service
        ↓
Step 3: Combine into prompt context
        ↓
Step 4: Call Claude API with injected real data
        ↓
Natural-language answer grounded in your actual data
```

**Why RAG matters:**
- Claude answers using YOUR live data — not stale training knowledge
- Reduces hallucination — model can only use what you inject (when prompt and validation are strict)
- No fine-tuning or retraining needed
- Answers stay fresh because they read from a live database

**Example questions:**
- *"Who is the highest paying user?"*
- *"What policies does user 1 have?"*
- *"What is the total premium for user 2?"*

> ⚠️ **Production note:** This demo sends application data to an external LLM as-is.
> In a real life/health insurance system, sensitive fields must be masked or
> minimized before calling any external AI API.

---

## Running Tests

```bash
# user-service — 16 tests
cd user-service && ./mvnw test

# policy-service — 17 tests
cd policy-service && ./mvnw test
```

Test coverage includes:
- **Unit tests** — service layer with Mockito-mocked dependencies
- **Integration tests** — controller layer with WebMvcTest and MockMvc
- **Context load test** — Spring application context starts cleanly

```text
Total: 33 tests, 0 failures
```

---

## Project Structure

```text
insurance-integration-hub/
├── user-service/                 # User CRUD + AI Agent
│   ├── src/main/java/com/insurance/user_service/
│   │   ├── controller/           # REST endpoints
│   │   ├── service/              # Business logic + interfaces
│   │   ├── repository/           # Spring Data JPA repositories
│   │   ├── model/                # JPA entities
│   │   ├── dto/                  # Request/response DTOs + Agent DTOs
│   │   ├── mapper/               # Entity ↔ DTO conversion
│   │   ├── exception/            # Custom exceptions + global handler
│   │   └── config/               # WebClient, OkHttpClient beans
│   ├── src/test/                 # Unit + integration tests
│   └── Dockerfile                # Two-stage build
│
├── policy-service/               # Policy CRUD + bidirectional demo
│   ├── src/main/java/com/insurance/policy_service/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── model/                # Policy entity + PolicyStatus enum
│   │   ├── dto/
│   │   ├── mapper/
│   │   ├── exception/
│   │   ├── client/               # UserServiceClient (demo /details endpoint)
│   │   └── config/               # WebClient bean
│   ├── src/test/
│   └── Dockerfile
│
├── k8s/                          # Kubernetes manifests
│   ├── user-service-deployment.yaml
│   ├── user-service-service.yaml
│   ├── policy-service-deployment.yaml
│   └── policy-service-service.yaml
├── docker-compose.yml            # Local orchestration
├── ARCHITECTURE.md               # Full architecture guide
└── README.md
```

---

## Environment Variables

| Environment Variable | Spring Property | Service | Description |
|---|---|---|---|
| `ANTHROPIC_API_KEY` | `anthropic.api.key` | `user-service` | Claude API key for the AI Agent |
| `SERVICES_POLICY_SERVICE_URL` | `services.policy-service.url` | `user-service` | Policy Service base URL (default: http://localhost:8082) |
| `SERVICES_USER_SERVICE_URL` | `services.user-service.url` | `policy-service` | User Service base URL for the `/details` demo endpoint (default: http://localhost:8081) |

---

## Current Limitations

This is a demo project, not a production-ready insurance system:

- H2 in-memory database — data lost on restart
- No authentication or authorization
- No PII masking before Claude API calls
- No circuit breaker, retry, or timeout policy
- No distributed tracing or centralized logging
- No real MCP server or long-term AI memory
- No CI/CD pipeline

---

## Production Improvements

For production I would add:

- PostgreSQL, Oracle, or SQL Server instead of H2
- Spring Security with JWT or OAuth2
- Service-to-service authentication tokens
- Resilience4j — circuit breakers, retries, timeouts
- OpenTelemetry — distributed tracing and metrics
- Structured JSON logs with correlation IDs
- Kubernetes Secrets or Vault for API keys
- PII masking before sending context to external LLMs
- GitHub Actions CI/CD pipeline
- API Gateway or Ingress for external traffic routing
- Rate limiting for AI API usage

---

## AI-Assisted Development

[Claude Code CLI](https://docs.anthropic.com/en/docs/claude-code) was used
during development as an active part of the workflow:

- Generating and reviewing boilerplate
- Implementing features across multiple files from plain English instructions
- Running tests automatically after changes
- Suggesting refactoring and architecture improvements
- Committing changes with descriptive messages

All AI-generated code was reviewed, understood, and tested before committing.
AI-assisted commits are tagged:
```text
Co-Authored-By: Claude
```

---

## Interview Pitch

> I built Insurance Integration Hub to demonstrate the skills required for
> a Junior Java Integration Developer role. It has two Spring Boot services —
> User Service and Policy Service — communicating over REST using WebClient.
>
> The User Service hosts an AI Agent that uses RAG: it retrieves user and
> policy data from both services, injects it into a Claude prompt, and returns
> a natural-language answer grounded in real application data.
>
> I used Claude Code CLI throughout development — it helped me implement
> features across multiple files, write and run tests, and refactor — all
> from plain English instructions. That's exactly the AI-assisted workflow
> the role requires.
>
> The project is intentionally demo-scope. I documented all the gaps clearly:
> production database, authentication, PII masking, circuit breakers,
> observability, and CI/CD — so I can speak to what production would require.

---

## License

This project is intended for learning, interview preparation, and portfolio demonstration.