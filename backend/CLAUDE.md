# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Gajian — a lightweight HRIS for Indonesian SMEs (UMKM). This directory is the Spring Boot 4.1 / Java 21 backend; `../frontend` is the Flutter mobile client that consumes the same API. HR Admin and Manager personas are assumed to use a web admin panel (out of scope) against the same endpoints.

The backend is currently a skeleton: `GajianApplication` plus an empty test. The design is fully specified in `docs/` — read those before writing code.

## Commands

```bash
./mvnw spring-boot:run                    # run app (default port 8080)
./mvnw clean package                      # build jar
./mvnw test                               # all tests
./mvnw test -Dtest=Pph21CalculatorTest    # single test class
./mvnw test -Dtest=Pph21CalculatorTest#calculatesTerForTk0   # single test method
./mvnw flyway:info                        # migration status
```

There is no linter configured. Local JDK is 25; the project targets Java 21 (`java.version` property), so language features newer than 21 will not compile.

## Docs are the source of truth

| File | Role |
|---|---|
| `docs/02-PRD.md` | Modules, user flows, non-functional requirements, out-of-scope list |
| `docs/04-openapi-spec.yaml` | API contract — paths, schemas, status codes. Implement to match exactly |
| `docs/05-backend-implementation-notes.md` | Design decisions and known-tricky logic; the most important file |
| `docs/06-ERD.md` | 31 tables across 5 domains, Mermaid diagrams |

Written in Indonesian. Referenced but **not present** in this repo: `01-BRD` and `03-database-schema.sql` (the ERD calls that DDL the schema source of truth). If schema-level detail is needed and the DDL is still missing, ask rather than inventing tables.

## API response envelope

Every endpoint returns the same envelope shape. Build it with a shared `ApiResponse` helper in `common/response/` — never construct these maps ad hoc in a controller.

**Success — single resource**
```json
{
  "success": true,
  "data": {}
}
```

**Success — collection**
```json
{
  "success": true,
  "data": []
}
```

**Success — paginated collection**
```json
{
  "success": true,
  "data": {
    "items": [],
    "pagination": {
      "page": 1,
      "per_page": 20,
      "total": 100,
      "total_pages": 5
    }
  }
}
```

**Error**
```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "Human-readable error message"
  }
}
```

**Validation error** — `code` is always `VALIDATION_ERROR`; `details` maps field name to a list of messages for that field:
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "One or more fields are invalid",
    "details": {
      "email": ["Email is required"],
      "password": ["Password must be at least 8 characters"]
    }
  }
}
```

`GlobalExceptionHandler` in `common/exception/` is responsible for turning exceptions (validation failures, `ResourceNotFoundException`, `BadRequestException`, etc.) into this error shape — controllers and services should throw exceptions, not build error responses themselves.

## Architecture

**Package by feature, not by layer.** Each feature package is self-contained with its own `controller/`, `service/`, `repository/`, `entity/`, `dto/`, and `mapper/` sub-packages. Cross-feature reusable code (exception handling, the response envelope, shared utilities, base entities) lives in `common/`.

```
src/
└── main/
    ├── java/
    │   └── com/myproject/gajian/
    │       │
    │       ├── GajianApplication.java
    │       │
    │       ├── config/
    │       │   ├── SecurityConfig.java
    │       │   ├── JacksonConfig.java
    │       │   └── OpenApiConfig.java
    │       │
    │       ├── common/
    │       │   ├── exception/
    │       │   │   ├── GlobalExceptionHandler.java
    │       │   │   ├── ResourceNotFoundException.java
    │       │   │   └── BadRequestException.java
    │       │   │
    │       │   ├── response/
    │       │   │   └── ApiResponse.java
    │       │   │
    │       │   └── util/
    │       │       └── DateTimeUtil.java
    │       │
    │       ├── auth/
    │       │   ├── controller/
    │       │   ├── service/
    │       │   ├── repository/
    │       │   ├── entity/
    │       │   ├── dto/
    │       │   └── mapper/
    │       │
    │       ├── employee/
    │       │   ├── controller/
    │       │   │   └── EmployeeController.java
    │       │   ├── service/
    │       │   │   ├── EmployeeService.java
    │       │   │   └── EmployeeServiceImpl.java
    │       │   ├── repository/
    │       │   │   └── EmployeeRepository.java
    │       │   ├── entity/
    │       │   │   └── Employee.java
    │       │   ├── dto/
    │       │   │   ├── CreateEmployeeRequest.java
    │       │   │   ├── UpdateEmployeeRequest.java
    │       │   │   └── EmployeeResponse.java
    │       │   └── mapper/
    │       │       └── EmployeeMapper.java
    │       │
    │       ├── attendance/
    │       ├── overtime/
    │       ├── leave/
    │       ├── reimbursement/
    │       │
    │       ├── payroll/
    │       │   ├── controller/
    │       │   ├── service/
    │       │   ├── repository/
    │       │   ├── entity/
    │       │   ├── dto/
    │       │   ├── mapper/
    │       │   ├── engine/
    │       │   └── config/
    │       │
    │       ├── report/
    │       │
    │       └── notification/
    │           ├── inapp/
    │           └── push/
    │
    └── resources/
        ├── application.yaml
        ├── application-dev.yaml
        ├── application-prod.yaml
        └── db/
            └── migration/
                └── V1__...sql
```

```
src/
└── test/
    └── java/
        └── com/myproject/gajian/
            ├── auth/
            ├── employee/
            ├── attendance/
            ├── overtime/
            ├── leave/
            ├── reimbursement/
            ├── payroll/
            ├── report/
            └── notification/
```

Persistence is JPA + Flyway (`src/main/resources/db/migration`, currently empty) on PostgreSQL with PostGIS for geofencing. `application.yaml` has only the app name — datasource, JWT, and Firebase config still need adding.

### Coding conventions

- **DTOs at the boundary, entities never leave the service layer.** Controllers accept and return request/response DTOs only (`dto/`); repositories and services work with entities. Never serialize an entity directly as an API response.
- **Mapping is MapStruct.** Each feature's `mapper/` package holds a `@Mapper` interface (e.g. `EmployeeMapper`) that converts entity ↔ DTO. Don't hand-write mapping boilerplate in the service.
- **No business logic in controllers.** A controller's job is: deserialize the request DTO, call one service method, wrap the result in `ApiResponse`. All decisions, validation beyond bean-validation annotations, and orchestration belong in the service layer.
- **Reusable code goes in `common/`.** If a util, exception type, base class, or response helper is used by more than one feature, it belongs in `common/`, not duplicated per feature.
- **Comments are minimal.** Prefer clear naming and small methods over explanatory comments. Only comment where the *why* isn't obvious from the code itself (e.g. a non-obvious tax rule, a workaround for a library quirk) — not to narrate what the code already says.
- **TDD, Arrange-Act-Assert.** Write the failing test before the implementation. Structure every test in three clearly separated sections — Arrange, Act, Assert — and keep to that order and naming (comments or blank-line separation) even in short tests.

### Invariants that shape the code

These come from `docs/05` and are the reason the design looks the way it does. Do not simplify them away:

- **Refresh tokens** are stateful, single-use, rotated on every refresh, stored as SHA-256 hash (not BCrypt — tokens are already high-entropy). Reusing a revoked token revokes *all* of that user's sessions. `rotate()` must be `@Transactional`. Access JWTs (15 min) are never blacklisted; no Redis in v1.
- **PPh21** rates come from DB tables (`ter_rate_bracket`, `pph21_annual_bracket`), never hardcoded if-else. Annual final tax applies brackets *progressively per layer* — flat top-bracket rate is the classic bug. December/resign month uses the annual-final path, not TER.
- **Overtime pay** loops per hour against `overtime_rate_config`; a single request can span multiple multipliers (1.5x then 2x). Hourly rate = `baseSalary / 173`, computed once per employee per run.
- **Payroll runs** are idempotent per period (DB unique constraint on `payroll_run.payroll_period_id`). DRAFT/CALCULATED may be recalculated; FINALIZED must be explicitly voided first.
- **Notifications are event-driven only.** Business services publish domain events; listeners run `@Async` + `@TransactionalEventListener(AFTER_COMMIT)`. Never call `NotificationService`/`PushNotificationService` from a business service. Push is best-effort and must never throw into the listener or break the in-app notification.
- **Reports** (`/reports/*`) are read-only aggregate queries computed on demand. No generated report tables, no cron-built snapshots.
- **Leave balance** decrement requires `SELECT ... FOR UPDATE`; the audit-log row is written in the same transaction as the status change, not via an async event.
- **Tax/BPJS config tables are versioned** by `effective_date`/`effective_year` so historical payroll stays reconstructible.
- **Reimbursement** has deliberately no FK to payroll — Finance pays it independently of the payroll cycle. `PAID` is reachable only from `APPROVED`.
- Selfie photos and receipts go to S3-compatible object storage; only the URL is stored in Postgres.

`docs/05` §8 has a per-endpoint validation table and §9 a prioritized test list (RefreshTokenService, Pph21Calculator, PayrollRunService, geofence, leave-balance concurrency first) — follow that ordering when adding tests.