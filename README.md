# Parking Android — Kotlin Open Source

> **Portfolio evidence:** native Android / Kotlin · parking CRUD flow · environment-aware networking · pure Kotlin domain rules · observed Android CI quality gate.
>
> This repository is an open-source engineering artifact. It demonstrates the implementation that exists today and does not advertise unbuilt product features.

## Current implemented scope

The Android client currently supports the legacy parking workflow evidenced by its source:

- vehicle / parking-record creation;
- parking-record listing and lookup;
- record detail retrieval;
- checkout/update flow;
- entry and exit time handling;
- deterministic whole-hour parking-fee calculation;
- HTTP JSON communication through Volley;
- fee summary shown after a successful checkout response.

## Verified stack

- Kotlin / native Android
- Android SDK 34 (`minSdk 24`, `targetSdk 34`)
- AndroidX / AppCompat
- Material Components
- ConstraintLayout
- Volley
- JUnit 4
- Espresso dependency / instrumentation baseline
- Gradle Kotlin DSL
- Gradle Wrapper 8.2
- Android Gradle Plugin 8.2.2
- JDK 17 CI baseline
- namespace/application ID `io.github.javierquinan.parking`

## Current architecture

```text
Android UI
├── LoginActivity
├── ParkingManagementActivity
└── FeeSummaryActivity

Core
└── network/
    └── ApiConfig

Domain — pure Kotlin
├── ParkingFeeCalculator
└── ParkingRecordValidator

Tests
├── ApiConfigTest
├── ParkingFeeCalculatorTest
└── ParkingRecordValidatorTest
```

Networking still uses the legacy Volley/JSONObject contract, but validation and parking-fee logic are no longer embedded in the Activity.

## Domain rules extracted from Android UI

### `ParkingFeeCalculator`

The fee calculator is pure Kotlin and does not depend on Android framework APIs.

The current pricing policy intentionally preserves the prototype's established behavior:

```text
complete elapsed hours × hourly rate
```

It supports:

- `HH:mm` input;
- `hh:mm AM/PM` input;
- hour/minute validation;
- non-negative rate validation;
- same-date checkout validation;
- elapsed-minute and billable-hour output.

This extraction also removes the previous `java.time.LocalTime` / API-26 requirement from `ParkingManagementActivity`, which is important because the application declares `minSdk 24`.

### `ParkingRecordValidator`

The validator is pure Kotlin and separates two existing workflow states:

- **check-in/create:** exit time may be empty;
- **checkout/update:** exit time is mandatory.

It normalizes the plate and validates model, year, color, date and time input before JSON transport data is constructed.

## Network configuration and cleartext boundary

The application does not embed the backend base URL inside Activities.

`ApiConfig` reads `BuildConfig.API_BASE_URL`, sourced from:

1. Gradle property `PARKING_API_BASE_URL`;
2. environment variable `PARKING_API_BASE_URL`;
3. debug-only emulator fallback.

Security boundary:

- release builds have no default API endpoint;
- broad cleartext traffic is disabled by the main manifest;
- debug cleartext is restricted to emulator host `10.0.2.2` through a debug-only Network Security Configuration.

See [`docs/LOCAL_BACKEND.md`](./docs/LOCAL_BACKEND.md) for the legacy action contract actually present in the source.

## Observed quality evidence

GitHub Actions executed the repository verification command successfully on this hardening change:

```bash
./gradlew --no-daemon lintDebug testDebugUnitTest assembleDebug
```

Observed CI result:

- `testDebugUnitTest` — PASS;
- `assembleDebug` — PASS;
- `lintDebug` — PASS;
- overall Gradle build — `BUILD SUCCESSFUL`.

The repository currently versions **14 unit-test methods** across:

- `ApiConfigTest`;
- `ParkingFeeCalculatorTest`;
- `ParkingRecordValidatorTest`.

The CI log confirms the unit-test task passed; it is not presented as a fabricated `14/14` count because Gradle did not print that aggregate count in the job log.

See [`ENGINEERING_EVIDENCE.md`](./ENGINEERING_EVIDENCE.md) for the current evidence inventory and [`DEVELOPMENT.md`](./DEVELOPMENT.md) for reproducible local commands.

## Open-source governance

The project includes:

- [Apache License 2.0](./LICENSE)
- [`CONTRIBUTING.md`](./CONTRIBUTING.md)
- [`SECURITY.md`](./SECURITY.md)
- [`CODE_OF_CONDUCT.md`](./CODE_OF_CONDUCT.md)
- issue and pull-request templates
- Dependabot configuration for Gradle and GitHub Actions

## Current technical boundary

This repository does **not** claim:

- a production backend;
- a production deployment/security audit;
- typed Retrofit/OkHttp transport;
- MVVM/Clean Architecture across every screen;
- Jetpack Compose UI;
- Room/offline-first persistence;
- authentication/authorization infrastructure;
- a production-approved parking pricing policy.

The remaining Activity-level Volley/JSON transport is documented as part of the current legacy boundary rather than hidden or described as future work.

## Portfolio classification

**Category:** Native Android / Kotlin  
**Visibility:** Public / Open Source  
**Classification:** `PORTFOLIO EVIDENCE`

## Resumen en español

Aplicación Android nativa en **Kotlin** con flujo de registro/consulta/salida de vehículos, configuración de API por entorno, política de red diferenciada entre debug y release, cálculo de tarifa extraído a Kotlin puro y validación separada para ingreso/salida. GitHub Actions ejecutó correctamente `testDebugUnitTest`, `assembleDebug` y `lintDebug`; actualmente hay 14 métodos de prueba unitarios versionados. La documentación distingue claramente el código implementado de capacidades que no existen en el repositorio.

For broader engineering work, see my [GitHub profile](https://github.com/JavierQuinan).
