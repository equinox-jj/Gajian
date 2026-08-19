# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Gajian — a lightweight HRIS (attendance, leave, overtime, reimbursement, payroll, notifications) for Indonesian SMEs (UMKM). Stack: Spring Boot 4.1 / Java 21 backend (this directory) + PostgreSQL, Flutter mobile frontend (`../frontend`).

**Current state:** the backend is a fresh Spring Initializr skeleton (`GajianApplication.java` only) — no feature code is committed yet on `feat/auth`. Everything below the "Planned architecture" heading describes the target design from `docs/`, not code that exists yet. Do not assume any package under `com.myproject.gajian` exists until you've checked `src/main/java`.

## Workflow

Before writing or running any code for a feature, present a short plan (approach, files/packages touched, any invented schema or open questions) and get explicit sign-off first — don't implement silently.

## Commands

Run all Maven commands from `backend/` via the wrapper:

- Build: `./mvnw clean install`
- Run app: `./mvnw spring-boot:run`
- Run all tests: `./mvnw test`
- Run one test class: `./mvnw test -Dtest=ClassName`
- Run one test method: `./mvnw test -Dtest=ClassName#methodName`

## Docs (read these before implementing a feature)

- `docs/02-PRD.md` — product requirements, one section per module, in Indonesian.
- `docs/04-openapi-spec.yaml` — authoritative for endpoint paths, verbs, field names, and status codes. **Not** authoritative for the top-level response body shape — see Response envelope below.
- `docs/05-backend-implementation-notes.md` — bridges PRD/DDL/OpenAPI to actual Spring Boot design decisions (refresh token rotation, payroll engine, geofencing, notification event flow, testing priorities). Read the section for the module you're touching before writing code.
- `docs/06-ERD.md` — 6 Mermaid diagrams, one per domain (Auth/Org, Attendance/Overtime, Leave, Reimbursement, Payroll/Tax, Notifications), plus a cross-reference table of how domains connect.
- `docs/03-database-schema.sql` (the DDL the ERD claims is the source of truth) **does not exist in the repo**. Derive Flyway migrations from the ERD instead. Don't invent tables the ERD doesn't list — ask first. The ERD omits some columns the code needs (e.g. `app_user` has no password column); adding those is fine, but call out any invented column in the PR/commit description.

## Response envelope

All endpoints must respond through a shared `ApiResponse` wrapper, not the bare schemas in `docs/04-openapi-spec.yaml`. Treat `docs/04` as authoritative for endpoint paths/verbs/fields only — its `LoginResponse`/`ErrorResponse` schemas are stale and describe a pre-envelope shape.

**Success, single resource:**

```json
{
  "success": true,
  "message": "User retrieved successfully",
  "data": { "id": "usr_123", "name": "Joshua Pardede", "email": "joshua@example.com" },
  "meta": null
}
```

**Success, paginated list** — pagination nests under `meta.pagination` (leaves room for future metadata like `request_id` without cluttering the root):

```json
{
  "success": true,
  "message": "Users retrieved successfully",
  "data": [{ "id": "usr_123", "name": "Joshua Pardede" }],
  "meta": {
    "pagination": {
      "current_page": 1,
      "per_page": 10,
      "total_items": 57,
      "total_pages": 6,
      "has_next_page": true,
      "has_prev_page": false
    }
  }
}
```

**Error, validation** — `errors` is an array so multi-field validation failures come back in one response:

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": [
    { "field": "email", "message": "Email format is invalid" },
    { "field": "password", "message": "Password must be at least 8 characters" }
  ],
  "data": null
}
```

**Error, generic / not found / server** — `error_code` is a stable machine-readable string (`USER_NOT_FOUND`, `TOKEN_EXPIRED`, ...) so client error handling never depends on parsing `message` text; omit `errors` for non-validation errors:

```json
{
  "success": false,
  "message": "User not found",
  "error_code": "USER_NOT_FOUND",
  "data": null
}
```

- `success` is always present so clients can branch without inspecting the HTTP status alone.
- `data` is always present as a key — `null` on empty/error — so clients don't need optional-chaining for a field that sometimes doesn't exist.
- `204 No Content` responses stay body-less (no envelope).

Build this as shared infrastructure (`common/response/ApiResponse`, `common/response/PaginatedResponse`, `common/response/PageMeta`, `common/exception/GlobalExceptionHandler`, `common/exception/ApiException`, `common/exception/ErrorCode`) so every feature module reuses it rather than rolling its own response shape. `GlobalExceptionHandler` is the single place that maps exceptions to `error_code` values and the validation-error array.

## Planned architecture

Package-by-feature, not package-by-layer (from `docs/05-backend-implementation-notes.md`), with a shared `config/`, `common/`, and `security/` per the conventions below:

```
com.myproject.gajian
 ├── GajianApplication.java
 │
 ├── config/                       # cross-cutting configuration (SecurityConfig, OpenApiConfig, WebConfig, ...)
 │
 ├── common/                       # shared kernel — used across features
 │    ├── exception/
 │    │    ├── GlobalExceptionHandler.java
 │    │    ├── ApiException.java
 │    │    └── ErrorCode.java
 │    ├── response/
 │    │    ├── ApiResponse.java
 │    │    ├── PaginatedResponse.java
 │    │    └── PageMeta.java
 │    ├── util/                    # reusable stateless helper methods shared across feature modules
 │    └── constant/
 │
 ├── security/                      # JWT/auth plumbing (not feature business logic)
 │    ├── JwtTokenProvider.java
 │    ├── JwtAuthFilter.java
 │    └── CustomUserDetailsService.java
 │
 ├── auth/                          # feature module: login, refresh token rotation + reuse detection
 │    ├── AuthController.java
 │    ├── AuthService.java
 │    ├── dto/                      # request/response records + MapStruct mapper
 │    └── exception/
 ├── employee/
 ├── attendance/                    # GPS geofence check-in/out
 ├── overtime/
 ├── leave/
 ├── reimbursement/                 # independent of payroll — no FK to payroll_run/payslip
 ├── payroll/
 │    ├── engine/                   # PPh21Calculator, BpjsCalculator, OvertimePayCalculator, PayrollRunService
 │    └── config/                   # TerRateBracketService, Pph21AnnualBracketService, BpjsRateConfigService, OvertimeRateConfigService
 ├── report/                        # read-only aggregate queries, no separate report tables
 ├── notification/
 │    ├── inapp/
 │    └── push/                     # Firebase Cloud Messaging
 └── integration/                   # external API clients (payment gateways, email, etc.)
```

Each feature module (`employee/`, `attendance/`, `leave/`, ...) follows the same internal shape as `auth/` above: `Controller` (thin, delegates to service — no business logic), `Service`/`ServiceImpl`, `Repository`, an `Entity`, a `dto/` package holding `*Request`/`*Response` records plus a MapStruct `*Mapper` interface, and an optional `exception/` for module-specific exceptions that still render through the shared `GlobalExceptionHandler`. Entities never cross the controller boundary — controllers and clients only ever see DTOs.

Key design decisions to preserve when implementing (details in `docs/05-backend-implementation-notes.md`):

- **Auth**: access JWT is stateless, 15 min TTL; refresh token is stateful, single-use, rotated on every use, hashed with SHA-256 (not BCrypt — it's already high-entropy). Reusing a revoked refresh token revokes *all* sessions for that user (theft mitigation). `RefreshTokenService.rotate()` must be `@Transactional`. Access tokens are never blacklisted on logout (accepted trade-off, no Redis in v1).
- **Payroll/tax**: PPh21 TER/PTKP/BPJS rates come from queried config tables (`ter_rate_bracket`, `pph21_annual_bracket`, `bpjs_rate_config`), never hardcoded if/else — these tables intentionally have no FK to `payslip` (lookup by category+year, not a permanent relation). December/resign-month payroll runs use the progressive annual bracket calculation, not TER.
- **Overtime pay**: hourly rate (`baseSalary / 173`) computed once per employee per run; multiplier looked up per hour against `overtime_rate_config` since a single request can span multiple multiplier brackets.
- **Notifications**: business services never call `NotificationService`/`PushNotificationService` directly — always via `ApplicationEventPublisher`, consumed by an `@Async @TransactionalEventListener(phase = AFTER_COMMIT)` listener, so notification failures can't roll back business transactions and un-committed data can't trigger notifications. Push is best-effort (never throws); invalid FCM tokens (`UNREGISTERED`) are deleted from `user_device_token` on the same request.
- **Reports** (`/reports/*`): always on-demand aggregate queries over operational tables — never a separate synced report table.
- **Reimbursement**: no FK to payroll — it's paid out independently of the payroll cycle.

## Conventions

- **DTOs, not entities, at the boundary**: request/response bodies are Java `record`s in each feature's `dto/` package (`UserRequest`, `UserResponse`, ...). Controllers and service return types never expose JPA entities directly.
- **MapStruct** does entity↔DTO mapping (`@Mapper(componentModel = "spring")`, one mapper interface per feature in `dto/`). Not yet in `pom.xml` — add `mapstruct` + `mapstruct-processor` (annotation processor path, alongside the existing Lombok one) when the first feature needing mapping lands.
- **Controllers stay thin**: routing, request validation triggers, delegating to the service layer, and wrapping the result in `ApiResponse`. No business logic in a `@RestController`.
- **JWT/auth plumbing lives in `security/`** (`JwtTokenProvider`, `JwtAuthFilter`, `CustomUserDetailsService`), separate from the `auth/` feature module, which owns the login/refresh *business logic* (`AuthController`, `AuthService`, token rotation).
- **`common/util`** holds reusable, stateless helper methods/logic genuinely shared across feature modules — not a dumping ground for module-specific logic that belongs in that module's own service.
- **Constants**: all constants (magic strings/numbers, header names, claim names, regex patterns, default values, etc.) live in `common/constant/` as `public final class`es with private constructors and `public static final` fields — never as inline literals or scattered `private static final` fields inside a feature's own classes. Group related constants into their own class (e.g. `JwtConstants`, `SecurityConstants`) rather than one catch-all class.
- **Comments**: default to none. Only write one where the *why* isn't obvious from the code — a non-obvious constraint, invariant, or workaround. Never narrate what a well-named method already says.
- **Performance**: be deliberate about the cost of each line — avoid N+1 queries, unnecessary intermediate allocations, and unindexed lookups on hot paths (attendance check-in, payroll run).

## Testing

- **TDD**: write the failing test first, then the minimum implementation to pass it, then refactor. Don't write implementation code before a failing test exists for it.
- **Arrange-Act-Assert**: structure every test body with explicit `// Arrange`, `// Act`, `// Assert` comments — arrange inputs/mocks, act by invoking the single unit under test, assert the outcome. One `// Act` per test; split multi-behavior tests instead of asserting unrelated things in one test.

## Configuration & secrets

Sensitive config values (DB credentials, JWT signing secret, FCM service account, etc.) never go directly in `application.yaml` — reference them via `${VAR_NAME}` placeholders and define the actual values in a local, gitignored `.env`. `pom.xml` doesn't yet include a dotenv loader (e.g. `me.paulschwarz:spring-dotenv`) — add one when the first secret-bearing config is wired up (likely alongside `auth`/JWT setup).

## Spring Boot 4.1 / Spring Security 7 gotchas

This project pins `spring-boot-starter-parent` 4.1.0, which relocates some familiar APIs:

- Jackson is 3.x: `ObjectMapper` is `tools.jackson.databind.ObjectMapper`, not `com.fasterxml.jackson.databind`. Annotations (`@JsonProperty`, `@JsonInclude`) are unchanged, still on `com.fasterxml.jackson.annotation`.
- `@AutoConfigureMockMvc` is at `org.springframework.boot.webmvc.test.autoconfigure`, not `org.springframework.boot.test.autoconfigure.web.servlet`.
- `NimbusJwtEncoder` with an HMAC `ImmutableSecret` fails ("Failed to select a JWK signing key") unless the JWS header is explicit — build claims with `JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)`; the default header algorithm is RS256.
- Starters are modular: `spring-boot-starter-webmvc`, `-security`, `-flyway`, `-data-jpa`, `-validation`, each with a matching `-test` starter (see `pom.xml`). JWT support needs `spring-security-oauth2-jose` and `spring-security-oauth2-resource-server` added explicitly — they're not pulled in by `-security`.
- H2 (used in tests) rejects the `timestamptz` shorthand even under `MODE=PostgreSQL` — write `timestamp with time zone` in migrations so they parse under both H2 and Postgres.
