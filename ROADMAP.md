# Parking Android — Open Source Modernization Roadmap

This roadmap turns the existing Kotlin prototype into a professional, maintainable and openly auditable Android project. Every phase has an evidence gate; no phase is marked complete until the corresponding source/tests/CI exist.

## Phase 0 — Open-source baseline and repository hygiene

**Goal:** establish a trustworthy public baseline without changing product behavior.

- [x] Reclassify repository as active open-source revival
- [x] Publish modernization roadmap
- [x] Add Apache-2.0 license
- [x] Add contribution policy
- [x] Remove tracked IDE-only metadata (`.idea/`)
- [x] Harden `.gitignore`
- [x] Align Gradle root project identity to `ParkingAndroid`
- [x] Add `SECURITY.md`
- [x] Add Code of Conduct
- [x] Add issue/PR templates
- [x] Add Dependabot strategy
- [x] Document baseline build requirements
- [x] Establish first GitHub Actions workflow (`lintDebug + testDebugUnitTest + assembleDebug`)
- [x] Verify first green CI run

**Exit gate:** complete — clean repository + reproducible local build + first green CI run verified.

---

## Phase 1 — Identity, configuration and network security

**Goal:** remove prototype-only technical assumptions.

- [x] Rename namespace/applicationId from `com.example.parcial` to `io.github.javierquinan.parking`
- [x] Rename generic Activities/features using domain terminology
- [x] Remove hardcoded `http://10.0.2.2/Parcial/auto.php` from application code
- [x] Introduce environment-aware API base URL configuration
- [x] Disable broad cleartext traffic
- [x] Add Android Network Security Configuration only for explicit development cases
- [ ] Define typed API request/response models
- [ ] Centralize errors and API result handling
- [ ] Add input validation for plate, dates and times
- [x] Document local/demo backend strategy

**Exit gate:** pending typed contracts, centralized error handling and input validation. Identity/configuration/network boundary is implemented and must remain CI-verified.

---

## Phase 2 — Kotlin architecture modernization

**Goal:** make the project credible native-Android engineering evidence.

Target structure:

```text
app/
core/
  network/
  database/
  common/
feature/
  parking/
  vehicles/
  rates/
```

- [ ] Introduce MVVM + use-case/domain boundaries
- [ ] Move networking out of Activities
- [ ] Repository interfaces in domain layer
- [ ] Retrofit + OkHttp for typed HTTP APIs
- [ ] Coroutines for asynchronous work
- [ ] Flow / StateFlow for observable state
- [ ] Hilt dependency injection
- [ ] ViewModel-driven screen state
- [ ] Define domain error/result model
- [ ] Remove business logic from Android framework classes

**Exit gate:** presentation layer does not know transport/database implementation details; core domain logic has unit tests.

---

## Phase 3 — Modern UI / UX

**Goal:** replace prototype UI with a consistent modern Android experience.

- [ ] Jetpack Compose
- [ ] Material 3 design system
- [ ] Navigation Compose
- [ ] responsive layouts
- [ ] loading / empty / error states
- [ ] accessibility semantics and content descriptions
- [ ] form validation and user feedback
- [ ] light/dark theme
- [ ] localization baseline: English + Spanish
- [ ] screenshots/demo media for README

**Exit gate:** representative user journey can be demonstrated entirely through the modern UI.

---

## Phase 4 — Offline-first data layer

**Goal:** make parking operations resilient to connectivity interruptions.

- [ ] Room local persistence
- [ ] remote/local source separation
- [ ] synchronization strategy
- [ ] conflict/error policy
- [ ] DataStore for non-sensitive preferences
- [ ] WorkManager for background synchronization
- [ ] network connectivity awareness
- [ ] cached parking/session history

**Exit gate:** core operator flows remain usable offline and synchronize predictably when connectivity returns.

---

## Phase 5 — Parking domain evolution

**Goal:** evolve from CRUD prototype to a coherent parking domain.

- [ ] parking facilities / zones
- [ ] parking spaces and availability
- [ ] vehicle registry
- [ ] check-in/check-out session lifecycle
- [ ] rate plans and configurable pricing rules
- [ ] reliable duration calculation across date boundaries
- [ ] receipts and history
- [ ] reservation lifecycle
- [ ] QR ticket / validation flow
- [ ] operator roles and permissions model
- [ ] audit-friendly event history

**Exit gate:** domain model documented and unit-tested; no pricing logic remains embedded in UI code.

---

## Phase 6 — Location, notifications and platform capabilities

- [ ] map/location experience where business value exists
- [ ] nearby parking discovery
- [ ] permission-safe location handling
- [ ] local/push notifications
- [ ] check-out reminders
- [ ] reservation notifications
- [ ] deep links
- [ ] optional camera/QR scanning

**Exit gate:** platform permissions follow least-privilege and graceful-denial behavior.

---

## Phase 7 — Quality engineering

- [ ] unit tests for domain/use cases
- [ ] repository/data tests
- [ ] API contract tests where possible
- [ ] ViewModel tests
- [ ] Compose UI tests
- [ ] Android instrumentation smoke tests
- [ ] static analysis / lint
- [ ] formatting gate
- [ ] test fixtures and synthetic data
- [ ] CI release/debug builds
- [ ] coverage reporting used as signal, not vanity metric

**Exit gate:** GitHub CI verifies every pull request with deterministic automated checks.

---

## Phase 8 — Security and privacy hardening

- [ ] HTTPS-only production networking
- [ ] no secrets in source or BuildConfig defaults
- [ ] secure token/session storage if authentication is introduced
- [ ] least-privilege permissions
- [ ] dependency scanning
- [ ] secret scanning
- [x] security reporting policy
- [ ] threat model for operator/user flows
- [ ] privacy notes for location/vehicle identifiers

**Exit gate:** documented threat/security model + automated security checks + no known high-risk repository hygiene issue.

---

## Phase 9 — Backend/API contract and interoperability

The Android repository should remain usable independently, but a professional client requires a stable contract.

- [ ] OpenAPI contract or equivalent API documentation
- [ ] versioned endpoints
- [ ] typed error envelope
- [ ] authentication/authorization contract
- [ ] idempotent check-in/check-out operations where needed
- [ ] pagination/filtering contract
- [ ] integration/demo environment

A backend may be delivered in a separate repository to keep concerns clean.

---

## Phase 10 — Open-source release readiness

- [ ] `CHANGELOG.md`
- [ ] semantic/versioning strategy
- [ ] signed/tagged release process where practical
- [ ] contributor setup documentation
- [ ] architecture documentation/ADRs
- [ ] sample/demo data
- [ ] automated release build
- [ ] first public pre-release
- [ ] issue labels and contribution backlog

**Target milestone:** `v0.1.0-alpha` only after CI, security baseline and representative end-to-end flow are verified.

---

## Portfolio promotion gate

The repository becomes a **featured/pinned native Android project** only after all of the following are evidenced:

- architecture modernization complete enough to remove business/network logic from Activities
- secure environment configuration
- representative Kotlin unit tests
- Android CI is green
- modern UI flow is demonstrable
- README contains reproducible build/run steps
- license/contribution/security governance is complete

Until then the repository remains **active open-source revival**, which is a truthful and useful signal of engineering progression.
