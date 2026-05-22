# Financial Lab - Engineering Skills

## Backend Engineering

### Objective
Build production-grade backend services with clear boundaries, strong domain modeling, and maintainable code.

### Rules
- Use Java 21 and Spring Boot 4
- Prefer immutable objects
- Avoid business logic in controllers
- Keep domain layer free from framework dependencies
- Use explicit names and behavior-oriented methods

### Deliverables
- Thin controllers
- Application services
- Domain entities and value objects
- Validation and exception handling
- Unit and integration tests

---

## Distributed Systems

### Objective
Design services that are independently deployable and loosely coupled.

### Rules
- Each service owns its database
- Prefer async communication when possible
- Avoid distributed transactions
- Use eventual consistency for cross-service flows
- Define clear contracts between services

### Deliverables
- Service boundaries
- API contracts
- Async integration flows
- Idempotency strategy
- Retry and compensation design

---

## Event-Driven Architecture

### Objective
Model business processes through events that are traceable and replayable.

### Rules
- Events must be immutable
- Every event must contain metadata
- Use correlationId and traceId
- Document every event in the catalog
- Consumers must be idempotent

### Deliverables
- Event schemas
- Producer/consumer design
- DLQ strategy
- Event versioning rules
- Event catalog documentation

---

## Financial Domain Modeling

### Objective
Represent financial operations with high fidelity and business correctness.

### Rules
- Use ubiquitous language
- Model business capabilities, not technical details
- Use BigDecimal for monetary values
- Make flows auditable and traceable
- Prefer explicit lifecycle states

### Deliverables
- Aggregates, entities, and value objects
- Domain services
- Business rules and invariants
- Lifecycle state machines
- Audit-friendly domain events

---

## API Design

### Objective
Expose stable, predictable, and well-documented REST APIs.

### Rules
- Follow RESTful conventions
- Controllers must return ResponseEntity
- Use RFC7807 for errors
- Validate request payloads
- Never expose entities directly

### Deliverables
- OpenAPI specification
- Request/response DTOs
- Validation rules
- Error response contracts
- Versioning strategy

---

## Database Engineering

### Objective
Design relational and NoSQL persistence that supports financial consistency.

### Rules
- Use Flyway migrations
- Never use ddl-auto update
- Prefer UUID primary keys
- Include created_at and updated_at
- Model indexes intentionally

### Deliverables
- Migration scripts
- Schema design
- Index strategy
- Transaction boundaries
- Query optimization decisions

---

## Cloud & Infrastructure

### Objective
Run services in a secure, repeatable, production-like environment.

### Rules
- External secrets come from AWS Secrets Manager
- Infrastructure must be versioned
- Prefer immutable deployments
- Separate environments clearly

### Deliverables
- Terraform modules
- Environment configs
- IAM policies
- Deployment definitions
- Cloud resource documentation

---

## Observability

### Objective
Make every critical flow visible, measurable, and diagnosable.

### Rules
- Use structured JSON logs
- Propagate correlationId
- Propagate traceId in async flows
- Expose Prometheus metrics
- Create dashboards for critical flows

### Deliverables
- Log standardization
- Metrics definitions
- Dashboards
- Alerts
- Tracing instrumentation

---

## Resilience Engineering

### Objective
Prevent cascading failures and make external dependencies safe to use.

### Rules
- External calls must have timeout
- External calls must have retries
- Use exponential backoff
- Consumers must be idempotent
- Critical flows need DLQ support

### Deliverables
- Timeout and retry policies
- Circuit breaker configuration
- DLQ handling
- Idempotency strategy
- Failure recovery design

---

## Testing Strategy

### Objective
Prove critical business behavior with fast and reliable tests.

### Rules
- Prefer integration tests for critical flows
- Use Testcontainers
- Mock only external dependencies
- Use pure unit tests for domain rules
- Target high coverage on core logic

### Deliverables
- Unit tests
- Integration tests
- Contract tests
- Testcontainers setup
- Coverage reports

---

## Security Engineering

### Objective
Protect sensitive financial data and reduce operational risk.

### Rules
- Never log sensitive data
- Mask documents and tokens
- Use least privilege
- Use Secrets Manager for secrets
- Audit important actions

### Deliverables
- Secret handling strategy
- Logging masking rules
- IAM policies
- Security checks
- Audit trail design

---

## CI/CD Engineering

### Objective
Ensure consistent validation, packaging, and deployment.

### Rules
- Pipeline must run tests and checks
- Quality gates must block bad changes
- Deployments must be reproducible
- Fail fast on rule violations

### Deliverables
- GitHub Actions workflows
- Build and test pipelines
- Quality gates
- Deployment automation
- Release versioning

---

## Performance Engineering

### Objective
Keep services fast, scalable, and cost-efficient.

### Rules
- Avoid premature optimization
- Measure before changing
- Optimize critical paths only
- Prefer async processing for long workflows

### Deliverables
- Bottleneck analysis
- Query tuning
- Load test results
- Performance budgets
- Capacity assumptions

---

## Developer Experience

### Objective
Make the codebase easy to understand, change, and verify.

### Rules
- Favor explicit naming
- Keep modules small and cohesive
- Document important decisions
- Avoid generic util classes

### Deliverables
- README files
- Architecture notes
- Runbooks
- Local development setup
- Templates and conventions