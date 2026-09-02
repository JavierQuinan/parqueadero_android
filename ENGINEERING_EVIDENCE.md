# Parking Android — Current Engineering Evidence

This document records only engineering evidence that exists in the repository today. It is not a feature roadmap.

## Native Android baseline

- Kotlin / Android SDK 34
- `minSdk 24`, `targetSdk 34`
- Gradle Kotlin DSL
- AndroidX / AppCompat / Material Components
- Volley-based legacy HTTP transport
- namespace/application ID `io.github.javierquinan.parking`

## Repository and open-source controls

- Apache License 2.0
- `CONTRIBUTING.md`
- `SECURITY.md`
- `CODE_OF_CONDUCT.md`
- issue and pull-request templates
- Dependabot configuration
- Gradle Wrapper committed
- IDE/local build artifacts ignored

## Network hardening implemented

- Activities no longer embed the backend base URL.
- `ApiConfig` resolves `BuildConfig.API_BASE_URL`.
- `PARKING_API_BASE_URL` may be supplied as a Gradle property or environment variable.
- release builds have no default endpoint.
- broad cleartext traffic is disabled by the main manifest.
- debug HTTP is restricted to emulator host `10.0.2.2` through a debug-only Network Security Configuration.
- `ApiConfigTest` verifies that the configured base URL resolves the current `auto.php` resource.

## Kotlin domain extraction implemented

`ParkingFeeCalculator` is pure Kotlin and no longer depends on `Activity`, `Toast`, Volley or Android time APIs.

Current tariff rule is explicitly documented and tested: **complete elapsed hours × configured hourly rate**. This preserves the historical prototype behavior instead of silently inventing a new pricing policy.

The calculator:

- parses `HH:mm`;
- parses `hh:mm AM/PM`;
- validates minutes/hours;
- rejects a negative hourly rate;
- rejects exit time earlier than entry time for the same parking date;
- returns elapsed minutes, billable complete hours and total fee.

`ParkingRecordValidator` is also pure Kotlin and separates:

- check-in/create validation, where exit time may be empty;
- checkout validation, where exit time is required.

It normalizes the vehicle plate and validates required model/year/color/date/time fields before the Activity builds the JSON request.

## UI integration implemented

`ParkingManagementActivity` delegates validation and fee calculation to the domain layer.

For checkout:

1. a selected record code is required;
2. current form data is validated;
3. the fee is calculated by `ParkingFeeCalculator`;
4. the `Actualizar` request is sent;
5. `FeeSummaryActivity` opens only after a successful HTTP response callback.

This removes fee/time business logic from the Activity while preserving the current Volley transport contract.

## Automated quality gate

GitHub Actions executes:

```text
lintDebug
+ testDebugUnitTest
+ assembleDebug
```

The workflow uses JDK 17, the committed Gradle Wrapper and read-only repository permissions.

Versioned local unit-test evidence includes:

- `ApiConfigTest`;
- `ParkingFeeCalculatorTest`;
- `ParkingRecordValidatorTest`.

## Current technical boundary

The repository does not claim:

- a production backend;
- Retrofit/OkHttp typed transport;
- MVVM/Clean Architecture across all screens;
- Compose UI;
- Room/offline-first persistence;
- authentication/authorization infrastructure;
- production-ready pricing policy;
- production deployment/security audit.

Networking and JSON response handling still live primarily in Activities and remain part of the legacy prototype boundary.

## Evidence rule

Only implemented source, observed CI results and reproducible development instructions are presented as evidence. Ideas that are not implemented are intentionally omitted from recruiter-facing documentation.
