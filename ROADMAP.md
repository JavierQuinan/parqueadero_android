# Parking Android — Product & Engineering Roadmap

This roadmap preserves the long-term product and engineering vision of Parking Android **without presenting planned capabilities as implemented evidence**.

## Status legend

- ✅ **Implemented and evidenced** — source exists in the repository and is covered by the current documentation/quality gate where applicable.
- 🔄 **Active engineering direction** — a logical next step for the current codebase, but not a completed capability.
- 🧭 **Strategic evolution** — a possible future capability or architecture direction; it is not a delivery commitment and must not be presented as current functionality.

The source of truth for current capabilities is [`README.md`](./README.md) plus [`ENGINEERING_EVIDENCE.md`](./ENGINEERING_EVIDENCE.md).

---

## 1. Current verified baseline

### Repository and Android foundation

- ✅ Native Android application in Kotlin.
- ✅ Android SDK 34 with `minSdk 24` and `targetSdk 34`.
- ✅ Professional namespace/application ID: `io.github.javierquinan.parking`.
- ✅ Gradle Wrapper and reproducible JDK 17 CI baseline.
- ✅ Apache-2.0 license, CONTRIBUTING, SECURITY, Code of Conduct, issue/PR templates and Dependabot configuration.

### Current parking workflow

- ✅ Vehicle / parking-record creation.
- ✅ Parking-record listing and lookup.
- ✅ Record detail retrieval.
- ✅ Checkout/update flow.
- ✅ Entry/exit time handling.
- ✅ Fee summary after successful checkout response.
- ✅ Legacy Volley/JSONObject transport documented explicitly.

### Security and configuration

- ✅ API base URL configured outside Activities through `BuildConfig`.
- ✅ Gradle/environment configuration boundary for `PARKING_API_BASE_URL`.
- ✅ Release builds have no default backend URL.
- ✅ Broad cleartext networking disabled in the main manifest.
- ✅ Debug HTTP exception restricted to emulator host `10.0.2.2`.

### Domain and quality evidence

- ✅ Pure Kotlin `ParkingFeeCalculator`.
- ✅ Pure Kotlin `ParkingRecordValidator`.
- ✅ Separate validation for check-in and checkout.
- ✅ Input normalization and validation for plate/year/date/time fields.
- ✅ API endpoint unit-test source.
- ✅ Parking fee and time-parsing unit-test source.
- ✅ Parking-record validation unit-test source.
- ✅ GitHub Actions executes `lintDebug`, `testDebugUnitTest` and `assembleDebug`.

---

## 2. Engineering evolution — stronger Android architecture

These items describe how the current prototype could evolve into stronger native-Android engineering evidence.

### Application boundaries

- 🔄 Move HTTP request construction and response parsing out of Activities.
- 🔄 Introduce repository interfaces between presentation/domain/data concerns.
- 🔄 Add a centralized typed result/error model for network and validation outcomes.
- 🔄 Increase unit coverage around parking-session rules and transport mapping.
- 🧭 ViewModel-driven screen state using Android lifecycle architecture components.
- 🧭 Coroutines and Flow/StateFlow for asynchronous and observable state.
- 🧭 Dependency injection where it produces a measurable maintainability benefit.

### Transport/API modernization

- 🔄 Define explicit request/response models for the existing backend contract.
- 🔄 Document error envelopes and compatibility assumptions.
- 🧭 Retrofit + OkHttp or an equivalent typed Android HTTP boundary.
- 🧭 Versioned API contract / OpenAPI specification if a maintained backend is introduced.
- 🧭 Idempotent check-in/check-out operations for retry-safe network behavior.

The current repository does **not** claim Retrofit, OkHttp, OpenAPI or a production backend until the code actually exists.

---

## 3. Parking-domain evolution

The strongest product direction is to evolve from a CRUD parking record client into a coherent parking-operations platform.

### Core operational domain

- 🔄 Formal parking-session lifecycle: opened → active → checked out / cancelled.
- 🔄 More explicit duration and pricing rules, including boundary cases.
- 🔄 Synthetic fixtures for pricing/session tests.
- 🧭 Parking facilities, zones and parking spaces.
- 🧭 Availability state and occupancy tracking.
- 🧭 Configurable rate plans and pricing policies.
- 🧭 Receipts and transaction/session history.
- 🧭 Reservation lifecycle.
- 🧭 Operator roles and auditable operational events.

### Pricing quality

The current implemented rule preserves the historical prototype behavior: complete elapsed hours × hourly rate.

Future pricing work could include:

- 🧭 grace periods;
- 🧭 partial-hour rounding policies;
- 🧭 day/night or zone-specific tariffs;
- 🧭 daily caps;
- 🧭 lost-ticket/manual-adjustment rules;
- 🧭 overnight/multi-day sessions;
- 🧭 tax/receipt integration where required by the target jurisdiction.

None of those policies should be represented as implemented until backed by source and tests.

---

## 4. Modern Android UX

The present XML/AppCompat interface remains valid historical/native Android evidence. A future modernization can be evaluated independently from the domain/data work.

- 🧭 Material 3 design system.
- 🧭 Jetpack Compose for selected or all screens.
- 🧭 Navigation architecture.
- 🧭 explicit loading, empty, offline and error states.
- 🧭 accessibility semantics/content descriptions.
- 🧭 English + Spanish localization baseline.
- 🧭 responsive layouts for different Android form factors.
- 🧭 sanitized screenshots/demo media for portfolio review.

Compose is a potential evolution, not a current repository claim.

---

## 5. Offline resilience

For parking operations, offline resilience can add real business value where connectivity is unreliable.

- 🧭 Room or equivalent local persistence.
- 🧭 local/remote data-source separation.
- 🧭 cached active parking sessions.
- 🧭 deterministic synchronization policy.
- 🧭 conflict and retry handling.
- 🧭 connectivity-aware UI state.
- 🧭 WorkManager for background synchronization where appropriate.
- 🧭 DataStore for non-sensitive local preferences.

An offline-first claim will only be added to the README after offline flows and synchronization behavior are implemented and tested.

---

## 6. Platform capabilities

Potential extensions that could make Parking Android a stronger real-world product:

- 🧭 QR ticket generation/scanning for check-in/check-out.
- 🧭 local notifications for session/reservation events.
- 🧭 camera integration only where the workflow requires it.
- 🧭 map/location support for multi-facility discovery.
- 🧭 deep links into active sessions or reservations.
- 🧭 permission-safe behavior with graceful denial states.

Platform permissions should remain least-privilege and be introduced only with a defined business use case.

---

## 7. Quality engineering target

The repository already has a CI baseline. A higher quality bar could include:

- 🔄 expand pure Kotlin domain tests;
- 🔄 test HTTP mapping/error handling once networking leaves Activities;
- 🧭 ViewModel tests if ViewModels are introduced;
- 🧭 deterministic UI/instrumentation smoke flows;
- 🧭 screenshot/regression testing when UI stability warrants it;
- 🧭 coverage reporting as an engineering signal, not a vanity percentage;
- 🧭 release-build validation;
- 🧭 dependency/security checks aligned with the actual stack.

---

## 8. Security and privacy target

Current network configuration already separates debug cleartext behavior from release. Further maturity could include:

- 🔄 verify that all real release endpoints are HTTPS-only;
- 🔄 maintain secret-free source and BuildConfig defaults;
- 🧭 explicit threat model for operator, vehicle and parking-session flows;
- 🧭 secure token/session storage if authenticated APIs are introduced;
- 🧭 privacy notes for vehicle identifiers and location data if those features are added;
- 🧭 automated secret/dependency scanning appropriate to the repository.

---

## 9. Open-source and release maturity

- 🔄 keep README, evidence inventory and roadmap synchronized with the source.
- 🧭 architecture decision records for significant changes.
- 🧭 CHANGELOG and versioning strategy once public releases are meaningful.
- 🧭 synthetic/demo dataset and repeatable demo scenario.
- 🧭 signed/tagged releases where practical.
- 🧭 first public pre-release only after a representative flow is stable and reproducible.

---

## Portfolio rule

The roadmap is intentionally ambitious because it records what the project **could become**. Recruiter-facing evidence remains limited to what is verifiably implemented.

A future item moves from 🧭/🔄 to ✅ only when:

1. its source is versioned;
2. the documentation reflects the actual implementation;
3. relevant tests or reproducible verification exist where appropriate; and
4. CI remains green.
