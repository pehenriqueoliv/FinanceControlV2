# Financial Lab - Engineering Guidelines

## Project Overview

This project simulates real-world financial systems with production-grade architecture.

Main goals:
- High fidelity financial workflows
- Event-driven architecture
- Strong domain modeling
- Clean architecture
- Realistic observability and resiliency

---

# Tech Stack

- Java 21
- Spring Boot 4
- PostgreSQL
- Docker
- AWS
- Redis
- DynamoDB
- Github Actions
- Testcontainers
- JUnit 5
- Mockito
- Terraform
- Grafana
- Prometheus

---

# Architecture

The project follows Clean Architecture.

Layers:

- domain
- application
- infrastructure
- entrypoints

Rules:
- Domain layer must not depend on Spring
- Controllers should be thin
- Business rules belong to domain/application
- Infrastructure should contain adapters only

---

# Coding Standards

## General

- Prefer immutability
- Prefer composition over inheritance
- Use records for immutable DTOs
- Avoid utility classes
- Avoid static state

## Naming

- Use explicit names
- Avoid abbreviations
- Method names must describe behavior

Good:
- calculateSettlementFee
- validatePixTransaction

Bad:
- calcFee
- processData

---

# REST API Standards

- Use RESTful conventions
- Controllers must return ResponseEntity
- Use RFC7807 for errors
- Always validate request payloads
- Never expose entities directly

---

# Database Standards

- Use Flyway migrations
- Never use hibernate ddl-auto update
- Prefer UUID as primary key
- Tables must include:
    - created_at
    - updated_at

---

# Security Rules

- Never log sensitive data
- Mask documents and tokens
- Use least privilege principle
- External secrets must come from AWS Secrets Manager

---

# Testing Strategy

- Prefer integration tests for critical flows
- Use Testcontainers
- Mock only external dependencies
- Domain rules should have pure unit tests

---

# Anti-Patterns

Avoid:
- Fat controllers
- God services
- Business logic inside repositories
- Shared mutable state
- Generic "Util" classes

---

# Development Workflow

Before implementing features:
1. Understand domain impact
2. Define boundaries
3. Define contracts
4. Create tests
5. Implement incrementally

---

# Important Financial Rules

- Monetary values must use BigDecimal
- Never use double for money
- All operations must be traceable
- Settlement flows must be auditable

---

# Microservice Rules

- Each service must be independently deployable
- Each service owns its database
- Avoid synchronous communication when possible
- Prefer eventual consistency
- Services communicate through contracts
- Avoid distributed transactions
- Prefer choreography over orchestration
- Every service must expose health checks
- Every service must expose metrics

---

# Domain Rules

- Services must represent business capabilities
- Avoid technical microservices
- Use bounded contexts
- Shared kernel must be minimal
- Ubiquitous language is mandatory
- Each microservice owns its data

---

# Observability Standards

- All logs must be structured JSON
- Every request must contain correlationId
- Every async event must propagate traceId
- Use OpenTelemetry standards
- All services must expose Prometheus metrics
- Define business metrics and technical metrics
- Create dashboards for every critical flow
- Create alerts for error rate and latency

---

# Resilience Standards

- External calls must have timeout
- External calls must have retry policies
- Use exponential backoff
- Critical consumers must support DLQ
- Consumers must be idempotent
- Avoid cascading failures
- Prefer async communication for long-running flows

---

# Definition of Done

A feature is only considered complete if:

- Tests are implemented
- Coverage >= 95%
- OpenAPI updated
- Metrics exposed
- Logs standardized
- Flyway migration created
- Events documented
- CI pipeline passing
- Architecture rules respected

---

# Event Standards

Every event must contain:
- eventId
- occurredAt
- producer
- correlationId
- payload

Events must be documented in the event catalog.
