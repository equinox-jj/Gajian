# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Gajian — a lightweight HRIS (attendance, leave, overtime, reimbursement, payroll, notifications) for Indonesian SMEs (UMKM). This directory is the **Flutter mobile client**; the API lives in `../backend` (Spring Boot 4.1 / Java 21 + PostgreSQL) and has its own `CLAUDE.md` — read it before writing anything that touches the API contract.

Per `docs/GajianDocument/02-PRD.md` §1, mobile is the **employee-facing** surface (check-in/out, submit leave/overtime/reimbursement, view payslips and leave balance, notifications). HR Admin work — payroll runs, master data, tax config — belongs to a separate web admin panel consuming the same API; the prototype reflects this ("Finalize from the web admin panel"). Manager approval flows on mobile are optional for v1.

**Current state:** a bare `flutter create` scaffold. `lib/main.dart` is a 16-line Hello World, there is no `test/` directory, and `pubspec.yaml` has no dependencies beyond `flutter` and `flutter_lints` — no routing, state management, HTTP client, or secure storage yet. Everything described below is target design from `docs/`, not code. Do not assume any file under `lib/` exists until you have checked.

## Workflow

Before writing or running code for a feature, present a short plan (approach, files touched, packages you intend to add, open questions) and get explicit sign-off — don't implement silently. Adding a pub dependency is a decision to raise, not a default.

## Commands

Toolchain: Flutter 3.47.0 stable / Dart 3.13 (`pubspec.yaml` pins `sdk: ^3.13.0`).

- Install deps: `flutter pub get`
- Run app: `flutter run` (add `-d <device>`; `flutter devices` to list)
- Static analysis: `flutter analyze` (rules: `package:flutter_lints/flutter.yaml`, platform dirs excluded)
- Format: `dart format .`
- All tests: `flutter test` (currently reports no test files — `test/` doesn't exist yet)
- One test file: `flutter test test/path/to/file_test.dart`
- One test by name: `flutter test --plain-name "test name"`
- Release build: `flutter build apk --release` / `flutter build ipa`

## Docs

`docs/` is **gitignored** — it exists locally only, so a fresh clone won't have it. Don't assume a teammate or CI can see these paths.

- `docs/GajianDocument/02-PRD.md` — features per module and the key user flows (payroll run, check-in, leave/overtime/reimbursement submission, refresh-token rotation), in Indonesian. §5 carries the two client-side non-functional requirements: refresh token in Keychain/Keystore secure storage (never `SharedPreferences`), and offline-tolerant check-in that queues and retries.
- `docs/GajianDocument/01-BRD.md` — business rules (PPh 21, BPJS, attendance, leave, overtime) behind the numbers the UI displays.
- `docs/GajianDocument/04-openapi-spec.yaml` — authoritative for endpoint paths, verbs, field names, and status codes. **Not** authoritative for the top-level response shape (see below).
- `docs/GajianDocument/{05-backend-implementation-notes.md,06-ERD.md}` — server-side design; useful for understanding field semantics.
- `docs/Gajian HRIS Mobile App Design/Gajian Mobile App.dc.html` — interactive prototype; open it in a browser. It is the reference for screen structure and, importantly, for **states**: the scenario switch drives happy / loading / empty / error / offline / queued / geofence-rejected / session-expired, and it toggles role (employee / manager / admin), theme (light / dark), and viewport (390 / 480 / 1024). Screens: home, checkin, requests (+ leave / overtime / reimbursement forms and review), leave, payslips, payslip detail, inbox, profile, password.
- `docs/Gajian HRIS Mobile App Design/Gajian Design Spec.dc.html` and `_ds/modernist-*/styles.css` — the design system.

## API contract

Base URL: `http://localhost:8080/v1` local, `https://api.gajian.app/v1` production. `/v1` is a path prefix baked into each controller mapping, not a servlet context path.

**Response envelope** — every JSON body is wrapped; the bare schemas in `04-openapi-spec.yaml` and `docs/Gajian HRIS Mobile App Design/uploads/Gajian_API.md` are stale on this point (they still describe `ErrorResponse{timestamp,status,error,message,path}`).

```json
{ "success": true, "message": "Login berhasil", "data": { "accessToken": "...", "refreshToken": "...", "expiresIn": 900, "role": "EMPLOYEE" }, "meta": null }
{ "success": false, "message": "User not found", "error_code": "USER_NOT_FOUND", "data": null }
{ "success": false, "message": "Validation failed", "errors": [{ "field": "email", "message": "..." }], "data": null }
```

- `error_code` is the only snake_case key; everything else, including payload fields, is camelCase. Branch on `error_code`, never on `message` — messages are Indonesian display text.
- `success` and `data` are always present (`data` is `null` on errors), so client models don't need optional-chaining for missing keys.
- `204 No Content` responses are body-less, no envelope — logout, logout-all, and change-password all return 204.
- Pagination: no paginated endpoint exists on the backend yet, so the `meta.pagination` key names are unsettled. Check `../backend/src/main/java/com/myproject/gajian/common/response/` before writing a paging model rather than trusting `PageMeta` in the API docs.

**What is actually implemented server-side:** only `/v1/auth/*` — `login`, `refresh`, `logout`, `logout-all`, `change-password`. Employees, attendance, leave, overtime, reimbursement, payroll, and notifications have JPA entities but no endpoints. Build those screens against the OpenAPI spec with mocked data; there is no live server to hit.

**Auth flow.** Access token is a 15-minute JWT (`expiresIn` is seconds); the refresh token is single-use and rotated on every `/auth/refresh`. Reusing an already-revoked refresh token revokes **every** session for that user, so concurrent refreshes will log the user out: serialize refresh behind a single in-flight future and queue the 401'd requests behind it. Store the refresh token in secure storage (Keychain / Keystore). Role arrives both in the login payload (`role`) and as a `role` claim on the JWT.

## Architecture

Clean architecture, organized **by feature**, not by layer-at-the-top. Each feature under `lib/features/<feature>/` owns its own `data/` (models, remote/local data sources, repository impl), `domain/` (entities, repository interface, usecases), and `presentation/` (pages, widgets, riverpod providers). Anything reused across features — widgets, theme, constants, base classes, utils — lives in `lib/core/` or `lib/shared/`, never duplicated per feature.

- **No circular dependencies.** A feature may depend on `core`/`shared`; `core`/`shared` never depend on a feature; features don't depend on each other directly — factor shared needs into `shared/` instead.
- **No business logic in UI.** Pages/widgets read state and dispatch events; they never branch on domain rules, transform API data, or make decisions. That logic belongs in a riverpod notifier or a usecase in `domain/`.

### State management — Riverpod + riverpod_generator

- One riverpod provider (generator-based, `@riverpod`) per page, holding a `freezed` state class updated via `copyWith`. Bottom sheets and dialogs that carry their own state get their own provider too, scoped to that sheet/dialog rather than piggybacking on the parent page's.
- Widgets are dumb: they watch the provider and call methods on the notifier. All async orchestration, validation, and mapping to UI state happens in the notifier.

### Data modeling — freezed

- Entities, DTOs/models, and UI state classes are `freezed`. Use union/sealed variants for UI state (e.g. `initial/loading/data/error`) instead of nullable-flag soup.

### Functional error handling — fpdart

- Usecases and repositories return `Either<Failure, T>` (fpdart) rather than throwing across layer boundaries. Data sources are the only place allowed to throw/catch raw exceptions (network, DB); the repository layer converts those into `Failure` values.

### Base classes (DRY)

- `BaseRepository`, `BaseRemoteDataSource`, `BaseLocalDataSource` in `core/` centralize the try/catch → `Either<Failure, T>` conversion so individual repositories/data sources don't re-implement error handling.
- A shared error handler in `core/error/` maps network exceptions (bad request, unauthorized, forbidden, not found, timeout, no connectivity, server error, etc. — including the backend's `error_code` envelope) and local-database exceptions (Drift errors, secure-storage failures) to a common `Failure` type. Every data source/repository routes through it instead of hand-rolling status-code checks.

### Local persistence

- **Drift** for local database needs and complex/structured data (offline check-in queue, cached lists, etc.).
- **Secure storage** (Keychain/Keystore-backed) for simple key-value data: access/refresh tokens, theme preference, and similar small settings. Never `SharedPreferences` for tokens (per §API contract above).

### Navigation

- **go_router** for all navigation; routes declared centrally, not ad-hoc `Navigator.push` scattered through features.

### Connectivity

- Network reachability checks go through a connectivity-checker package (e.g. `connection_checker`), used by the offline-tolerant check-in flow and any other feature that needs to distinguish "no connectivity" from "server error." Exact package choice is still an open decision to raise per the Workflow section.

### Widgets

- No widget-returning methods (`Widget _buildFoo()`) — extract a private `class _Foo extends StatelessWidget` (or `StatefulWidget`) instead, so each piece is independently rebuildable and testable.
- Keep widget trees shallow: split nested widgets into their own small private classes rather than nesting builders inside a parent widget.
- Anything reusable across features goes in `core/components/`, not left local to one feature.
- No `.forEach` in pages/screens for rendering lists — use `ListView.builder`/`SliverList`/`CustomScrollView` so lists stay lazily built.
- Never hardcode sizes, spacing, font sizes, colors, radii — pull from `core/theme/` (see Design system below), so the theme stays the single source of truth.

### Constants

- All constants (route names, storage keys, API paths not already in an env config, durations, magic numbers, etc.) live under `core/constants/`, not inline in feature code.

## Design system

Tokens live in `docs/Gajian HRIS Mobile App Design/_ds/modernist-*/styles.css`; mirror them into a single Flutter theme rather than hardcoding values at call sites.

- Type: **Archivo** for heading and body; headings at weight 800. Fixed type scale — density changes spacing, not sizes.
- Color (light): bg `#f3f2f2`, surface `#eae9e9`, text `#201e1d`, accent `#ec3013`, secondary accent `#e15b47`, dividers are text at 40% alpha. Neutral and accent ramps run 100–900. Mono scheme — one accent voice; status tags tint from the ramps rather than introducing new hues.
- Spacing: 4 / 8 / 12 / 16 / 24 / 32.
- **Radius is 0 everywhere** — this is a deliberate modernist look. Don't round corners. Three shadow levels only (`sm` / `md` / `lg`).
- Icons: Lucide, inline on `currentColor`. Imagery is grayscale.
- Both light and dark are in scope (the prototype ships a theme toggle).

## Conventions

- **TDD**: write the failing test first, then the minimum implementation, then refactor.
- **Arrange-Act-Assert**: explicit `// Arrange`, `// Act`, `// Assert` comments in every test body; one `// Act` per test — split multi-behavior tests.
- **Comments**: default to none. Only where the *why* isn't obvious — a non-obvious constraint or workaround. Never narrate what a well-named method already says.
- Secrets and environment-specific values (API base URL, FCM config) never go in committed source — `.env` is gitignored; use `--dart-define` or a gitignored config for anything sensitive.
