# Commands Reference

A practical command reference for the Insurance Integration Hub project,
split into two parts:

- **Part 1 — Essential Commands:** Clean workflow for running, testing,
  building, and deploying the project as if everything goes smoothly.
- **Part 2 — Troubleshooting Commands:** Real debugging commands used
  during development. These show the actual journey — not every path
  was straight.

> ⚠️ **Safety note:** Some commands are destructive — such as `rm -rf`,
> `git push --force`, and `kubectl delete`. Read the description before running.

> 🔐 **Secrets note:** Never commit real API keys or tokens.
> Use placeholders like `your_api_key_here` in all documentation.

## How to Use This File

Use **Part 1** for normal development commands — running services, testing,
building Docker images, deploying to Kubernetes, and querying the API.

Use **Part 2** only when debugging specific problems such as Git history
issues, Docker/Kubernetes image-pull errors, Claude API failures, or
Java package-structure errors.

---

## Part 1 — Essential Commands

---

### Quick Command Cheat Sheet

| Goal | Command |
|------|---------|
| Run User Service locally | `cd user-service && ./mvnw spring-boot:run` |
| Run Policy Service locally | `cd policy-service && ./mvnw spring-boot:run` |
| Run both services with Docker Compose | `docker-compose up --build` |
| Run Compose in background | `docker-compose up -d` |
| Stop Compose services | `docker-compose down` |
| Run User Service tests | `cd user-service && ./mvnw test` |
| Run Policy Service tests | `cd policy-service && ./mvnw test` |
| Check User Service health | `curl http://localhost:8081/actuator/health` |
| Check Policy Service health | `curl http://localhost:8082/actuator/health` |
| Apply Kubernetes manifests | `kubectl apply -f k8s/` |
| Check Kubernetes pods | `kubectl get pods` |
| Check Kubernetes services | `kubectl get services` |
| Query the AI Agent | See [AI Agent curl examples](#api-testing-with-curl) |
| Start Claude Code CLI | `cd user-service && claude` |

---

### Environment Checks

| Command | What it does |
|---------|-------------|
| `java -version` | Check Java version — should say 17.x |
| `java --version` | Alternative syntax — useful in WSL/Linux |
| `git --version` | Check Git version |
| `code --version` | Check VS Code version |
| `node --version` | Check Node.js version |
| `npm --version` | Check npm version |
| `docker --version` | Check Docker version |
| `kubectl version --client` | Check kubectl version |
| `claude --version` | Check Claude Code CLI version |

---

### Running the Services

#### Run User Service

```bash
cd user-service
export ANTHROPIC_API_KEY=your_api_key_here
./mvnw spring-boot:run
```

#### Run Policy Service

```bash
cd policy-service
./mvnw spring-boot:run
```

#### Health Checks

```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
```

> Always start User Service and Policy Service in separate terminals.
> Both must be running for the AI Agent and inter-service endpoints to work.

---

### Maven

| Command | What it does |
|---------|-------------|
| `./mvnw spring-boot:run` | Start the Spring Boot app |
| `./mvnw clean spring-boot:run` | Clean compiled files then start |
| `./mvnw test` | Run all tests |
| `./mvnw test -Dtest=UserControllerTest` | Run one specific test class |
| `./mvnw package -DskipTests` | Build JAR without running tests |
| `./mvnw clean` | Delete `target/` build output |
| `./mvnw dependency:go-offline -B` | Download all dependencies locally |

> `./mvnw` is the Maven wrapper bundled with the project.
> No separate Maven installation needed.

---

### Testing

#### Run All Tests

```bash
# User Service — 16 tests
cd user-service && ./mvnw test

# Policy Service — 17 tests
cd policy-service && ./mvnw test
```

#### Test Types Used

| Type | Tooling | Purpose |
|------|---------|---------|
| Unit tests | JUnit 5 + Mockito | Test service logic with mocked dependencies |
| Integration tests | WebMvcTest + MockMvc | Test controllers without loading full context |
| Context load test | Spring Boot test | Verify application context starts cleanly |

---

### Git — Daily Use

```bash
git status                    # show current state
git add .                     # stage all changes
git commit -m "message"       # commit with message
git push                      # push to GitHub
git log --oneline             # compact commit history
```

#### Initial Repository Setup

```bash
git init
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/repo.git
git push -u origin main
```

#### Git Configuration

```bash
git config --global user.name "Your Name"
git config --global user.email "you@example.com"
git config pull.rebase false          # use merge strategy for pull
git config --list | grep user         # verify config
```

---

### Docker

#### Build Images

```bash
cd user-service && docker build -t user-service:1.0 .
cd policy-service && docker build -t policy-service:1.0 .
```

#### Inspect Images

```bash
docker images
docker images | grep -E "user-service|policy-service"
```

#### Run a Container

```bash
docker run -d -p 8081:8081 --name user-service user-service:1.0
```

#### Manage Containers

```bash
docker ps                      # list running containers
docker logs user-service       # show container logs
docker logs -f user-service    # follow logs in real time
docker stop user-service       # stop container
docker rm user-service         # remove stopped container
```

---

### Docker Compose

```bash
docker-compose up --build      # build images and start services
docker-compose up -d           # start in background
docker-compose down            # stop and remove containers
docker-compose ps              # list running services
docker-compose logs            # show logs from all services
docker-compose logs user-service    # logs from one service
```

> Run all docker-compose commands from the folder containing `docker-compose.yml`

---

### Kubernetes

```bash
kubectl get nodes                                       # confirm K8s is running
kubectl apply -f k8s/                                   # deploy all manifests
kubectl get pods                                        # list pods
kubectl get services                                    # list services
kubectl get deployments                                 # list deployments
kubectl logs <pod-name>                                 # show pod logs
kubectl logs -f <pod-name>                              # follow pod logs
kubectl describe pod <pod-name>                         # full pod details
kubectl scale deployment user-service --replicas=3      # scale up
kubectl delete -f k8s/                                  # remove all resources
kubectl config current-context                          # show active context
```

> ⚠️ `kubectl delete -f k8s/` removes all deployed resources. Use carefully.

---

### API Testing with curl

#### User Service

```bash
# Get all users
curl http://localhost:8081/users

# Get user by id
curl http://localhost:8081/users/1

# Create a user
curl -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "policyNumber": "POL-001"
  }'

# Delete a user
curl -X DELETE http://localhost:8081/users/1
```

#### Policy Service

```bash
# Get all policies
curl http://localhost:8082/policies

# Create a policy
curl -X POST http://localhost:8082/policies \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "policyType": "Life Insurance",
    "premium": 250.00,
    "startDate": "2026-01-01",
    "status": "ACTIVE"
  }'

# Get combined user + policy details
curl http://localhost:8082/policies/user/1/details
```

#### AI Agent

```bash
# Query with specific user
curl -X POST http://localhost:8081/agent/query \
  -H "Content-Type: application/json" \
  -d '{
    "question": "What policies does this user have?",
    "userId": 1
  }'

# Query across all users
curl -X POST http://localhost:8081/agent/query \
  -H "Content-Type: application/json" \
  -d '{
    "question": "Who is the highest paying user?",
    "userId": null
  }'
```

---

### Claude Code CLI

#### Install and Start

```bash
npm install -g @anthropic-ai/claude-code   # install once
claude --version                            # verify
cd user-service && claude                   # start session
```

#### Example Session — Feature Built with 8 Prompts

This is the exact session used during development to add a complete
`findByPolicyNumber` feature across 5 files using plain English.

**What was built:**
- `findByPolicyNumber()` added to `UserRepository.java`
- `getUsersByPolicyNumber()` added to `UserServiceInterface.java`
- `getUsersByPolicyNumber()` implemented in `UserService.java`
- `GET /users/by-policy/{policyNumber}` added to `UserController.java`
- 2 new tests written in `UserControllerTest.java`
- Claude Code ran the test commands — all 33 passing
- Committed with descriptive Co-Authored-By message

**The 8 prompts:**

```text
1. what does this project do?
   → Claude Code scanned the codebase and described the full architecture

2. add a method to UserRepository that finds users by policyNumber
   → Added: List<User> findByPolicyNumber(String policyNumber)

3. add a service method to use it
   → Added to UserServiceInterface + UserService implementation

4. expose it via the controller
   → Added: GET /users/by-policy/{policyNumber} to UserController

5. add a test for the new endpoint
   → Wrote 2 test cases in UserControllerTest

6. run all tests
   → Ran ./mvnw test automatically — all 16 passing

7. yes run policy-service tests too
   → Ran policy-service tests — all 17 passing, 33 total

8. commit this
   → Created commit:
      "Expose GET /users/by-policy/{policyNumber} backed by a derived
       Spring Data query, with controller tests covering match and
       empty result cases.
       Co-Authored-By: Claude Opus 4.7 <noreply@anthropic.com>"
```

**What this demonstrates:**

| Agentic concept | How it appeared |
|----------------|----------------|
| Orchestration | Claude Code decided which files to edit and in what order |
| Tool use | Read files, wrote code, ran bash commands, made git commits |
| Context awareness | Understood existing architecture before making changes |
| Agentic loop | Read → Plan → Act → Verify → Commit |
| AI-assisted dev | 30-minute task completed from 8 plain English instructions |

**Interview talking point:**

> *"I used Claude Code CLI to add an entire feature — repository, service
> interface, implementation, controller, and tests — across 5 files from
> 8 plain English instructions. Claude Code ran the test commands
> automatically to verify correctness, then committed with a descriptive
> message. That's the AI-assisted development workflow this role requires."*

---

### Environment Variables

```bash
# Set for current session
export ANTHROPIC_API_KEY=your_api_key_here

# Persist across sessions
echo 'export ANTHROPIC_API_KEY=your_api_key_here' >> ~/.bashrc
source ~/.bashrc

# Verify
echo $ANTHROPIC_API_KEY
```

> 🔐 Never paste real API keys into documentation, commits, or screenshots.

---

### Spring Boot application.properties

| Property | What it does |
|----------|-------------|
| `server.port=8081` | Set service port |
| `spring.application.name=user-service` | Set service name |
| `spring.datasource.url=jdbc:h2:mem:insurancedb` | H2 in-memory DB URL |
| `spring.datasource.driver-class-name=org.h2.Driver` | H2 JDBC driver |
| `spring.datasource.username=sa` | H2 default username |
| `spring.jpa.database-platform=org.hibernate.dialect.H2Dialect` | H2 Hibernate dialect |
| `spring.jpa.hibernate.ddl-auto=create-drop` | Create tables on start, drop on stop |
| `spring.h2.console.enabled=true` | H2 web console at `/h2-console` |
| `management.endpoints.web.exposure.include=health,info` | Expose Actuator endpoints |
| `anthropic.api.key=${ANTHROPIC_API_KEY}` | Read API key from environment |
| `services.policy-service.url=http://localhost:8082` | Policy Service base URL |
| `services.user-service.url=http://localhost:8081` | User Service base URL |

---

### Key Spring Boot Annotations

| Annotation | What it does |
|-----------|-------------|
| `@SpringBootApplication` | Entry point — auto-config + component scan |
| `@RestController` | Handles HTTP requests, returns JSON |
| `@RequestMapping("/users")` | Base URL path for controller |
| `@GetMapping` / `@PostMapping` / `@PutMapping` / `@DeleteMapping` | Map HTTP methods |
| `@PathVariable` | Bind URL segment to parameter |
| `@RequestBody` | Bind JSON body to parameter |
| `@Valid` | Trigger Bean Validation |
| `@Service` | Business logic bean |
| `@Repository` | Data access bean |
| `@Component` | Generic Spring bean |
| `@Bean` | Declare Spring-managed object |
| `@Configuration` | Class contains `@Bean` methods |
| `@Value("${property}")` | Inject value from properties |
| `@RequiredArgsConstructor` | Lombok — constructor injection for final fields |
| `@Data` | Lombok — getters, setters, toString, equals, hashCode |
| `@Builder` | Lombok — builder pattern |
| `@NoArgsConstructor` / `@AllArgsConstructor` | Lombok constructors |
| `@Slf4j` | Lombok — injects logger |
| `@RestControllerAdvice` | Global exception handler |
| `@ExceptionHandler` | Handle specific exception type |
| `@Transactional` | Wrap in DB transaction — rollback on failure |
| `@Entity` | JPA entity — maps to DB table |
| `@Table(name="users")` | Map to specific table name |
| `@Id` | Primary key field |
| `@GeneratedValue` | Auto-generate primary key |
| `@Column(unique=true)` | Unique DB constraint |
| `@Enumerated(EnumType.STRING)` | Store enum as string in DB |
| `@WebMvcTest` | Load only web layer for tests |
| `@ExtendWith(MockitoExtension.class)` | Enable Mockito in unit tests |
| `@Mock` | Create mock object |
| `@InjectMocks` | Inject mocks into tested class |
| `@MockitoBean` | Mock Spring bean in WebMvcTest |
| `@BeforeEach` | Run before each test |
| `@DisplayName` | Human-readable test name |
| `@Nested` | Group related tests |

---

## Part 2 — Troubleshooting Commands

> These commands were used during real debugging sessions.
> They show the actual development journey — not every path was straight.
> This section is kept as an honest record of what real development looks like.

---

### Git Troubleshooting

| Command | What it does | When used |
|---------|-------------|-----------|
| `git pull origin main --allow-unrelated-histories --no-rebase` | Merge unrelated histories | Combining separate service repos into mono-repo |
| `git config pull.rebase false` | Set merge as pull strategy | Fixing "Need to specify how to reconcile" error |
| `git merge --abort` | Cancel in-progress merge | When merge went wrong mid-way |
| `git push origin main --force` | ⚠️ Force push | Used once to fix mono-repo structure — use only when certain |
| `git remote set-url origin https://USER:TOKEN@github.com/...` | Embed PAT in URL | Auth troubleshooting — prefer SSH or GitHub CLI normally |
| `git config --global --unset credential.helper` | Remove credential helper | Fixing VS Code git auth conflict |
| `git config --global credential.helper store` | Store credentials | After removing conflicting helper |

---

### File System Troubleshooting

| Command | What it does | When used |
|---------|-------------|-----------|
| `find src -name "*.java" \| head -20` | Find all Java files | Diagnosing package structure errors |
| `find src/test -name "*.java"` | Find all test files | Finding duplicate test file |
| `head -1 filename.java` | Print first line | Checking package declaration |
| `head -5 filename.java` | Print first 5 lines | Checking annotations |
| `sed -n '1,15p' filename.java` | Print lines 1–15 | Inspecting specific section |
| `grep -A2 "lombok" pom.xml` | Find Lombok in pom.xml | Verifying dependency exists |
| `grep -A20 "getUserPolicyDetails" PolicyService.java` | Find method | Reviewing implementation |
| `rm filename.java` | ⚠️ Delete a file | Removing duplicate test file |
| `rm -rf .git` | ⚠️ Delete git repo | Resetting nested git repos to fix mono-repo |
| `rm -rf src .mvn mvnw mvnw.cmd pom.xml .gitattributes` | ⚠️ Delete project files | Cleaning accidental root-level duplicates |
| `mv oldpath newpath` | Move or rename file | Moving UserServiceClient to correct folder |
| `unzip /mnt/c/Users/USERNAME/Downloads/user-service.zip` | Extract zip | Unpacking Spring Initializr download |

---

### Maven Troubleshooting

```bash
# Run test quietly and show last 40 lines
./mvnw -q test -Dtest=UserControllerTest 2>&1 | tail -40

# Run test and filter for important result lines
./mvnw test -Dtest=UserControllerTest 2>&1 | \
  grep -E "(Tests run|BUILD|ERROR|FAIL)" | tail -10
```

---

### Docker Troubleshooting

| Command | What it does | When used |
|---------|-------------|-----------|
| `docker context ls` | List Docker contexts | Diagnosing ErrImageNeverPull |
| `docker context use desktop-linux` | Switch to desktop-linux context | Attempting K8s fix (failed on WSL2) |
| `docker context use default` | Switch back to default | After failed context switch |
| `docker run -d -p 5000:5000 --name registry registry:2` | Run local Docker registry | Attempting K8s image fix |
| `docker tag user-service:1.0 localhost:5000/user-service:1.0` | Tag for local registry | K8s troubleshooting |
| `docker push localhost:5000/user-service:1.0` | Push to local registry | K8s troubleshooting |
| `docker save user-service:1.0 -o /tmp/user-service.tar` | Save image to file | K8s troubleshooting attempt |
| `curl http://localhost:5000/v2/_catalog` | Check local registry | Verifying images were pushed |

> Note: The local registry approach was used to work around a Docker Desktop /
> WSL2 context issue where Kubernetes could not access locally built images.
> This is an environmental limitation, not a code issue.

---

### Kubernetes Troubleshooting

```bash
# Show only the Events section of a pod
kubectl describe pod <pod-name> | grep -A5 "Events"
```

#### K8s Image Pull Policy Changes

```bash
# Switch from Never to IfNotPresent
sed -i 's/imagePullPolicy: Never/imagePullPolicy: IfNotPresent/' \
  k8s/user-service-deployment.yaml

# Revert back to Never
sed -i 's/imagePullPolicy: IfNotPresent/imagePullPolicy: Never/' \
  k8s/user-service-deployment.yaml
```

> These were used while diagnosing `ErrImageNeverPull` and `ErrImagePull`
> errors in the local K8s cluster. The root cause was a Docker Desktop /
> WSL2 context mismatch — a known local environment limitation.

---

### Anthropic API Troubleshooting

```bash
# Test API key and model directly
curl https://api.anthropic.com/v1/messages \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01" \
  -H "content-type: application/json" \
  -d '{
    "model": "claude-haiku-4-5-20251001",
    "max_tokens": 100,
    "messages": [{"role": "user", "content": "say hello"}]
  }'

# List all available models (use when getting 404 on model name)
curl https://api.anthropic.com/v1/models \
  -H "x-api-key: $ANTHROPIC_API_KEY" \
  -H "anthropic-version: 2023-06-01"
```

> The model name `claude-sonnet-4-20250514` returned a 404 error.
> Running `/v1/models` revealed the correct model ID: `claude-haiku-4-5-20251001`
> Use `/v1/models` to confirm the currently available model ID for your account.

---

### Full Troubleshooting Reference

| Error | Cause | Fix |
|-------|-------|-----|
| `COMPILATION ERROR: cannot find symbol` | Wrong package declaration | Check first line of file |
| `duplicate class: main.java.com...` | Files in wrong folder — double `main/java` path | `find src -name "*.java"` to check paths |
| `BeanDefinitionOverrideException` | Two beans with same name | Rename one `@Bean` method |
| `NoClassDefFoundError: UserService` | Duplicate test in wrong package | Find and delete with `find src/test -name "*.java"` |
| `bad source file` | File path doesn't match package | Move file to correct folder |
| `Claude API error: 404` | Wrong model name | Run `/v1/models` to get correct ID |
| `connect ECONNREFUSED 127.0.0.1:8082` | Policy Service not running | Start policy-service in second terminal |
| `AI service temporarily unavailable` | Claude API call failed | Check API key, model name, logs |
| `SQL Error: 23505 Unique index violation` | Duplicate email in H2 | Use different email — H2 resets on restart |
| `version is obsolete` in docker-compose | Old `version:` field | Remove `version: '3.8'` line |
| `ErrImageNeverPull` | K8s can't find local image | WSL2/Docker Desktop context issue |
| `ErrImagePull` | K8s trying to pull from Docker Hub | Tag and push to accessible registry |
| `git push rejected non-fast-forward` | Remote has commits local doesn't | Pull first, then push |
| `Need to specify how to reconcile` | Git pull strategy not set | `git config pull.rebase false` |
| `Missing or invalid credentials` | VS Code git helper conflict | `git config --global --unset credential.helper` |
| `protocol not available` | Wrong Docker context in WSL2 | `docker context use default` |