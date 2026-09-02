# Parking Android — Open Source Revival

> **Status:** active open-source revival  
> **Current maturity:** legacy Android/Kotlin prototype under structured modernization  
> **Portfolio role:** native Android / Kotlin evidence in progress

## Overview

This repository contains a native Android parking-management prototype written in **Kotlin**. The current application already demonstrates a basic operational flow: vehicle registration, listing/querying vehicles, entry/exit data, remote HTTP requests through Volley and a simple parking-rate calculation.

The project is now being deliberately revived as an **open-source Android engineering project**. The objective is not to hide the original academic/prototype state, but to evolve it in public through documented architectural, security, testing and UX milestones.

## Current verified stack

- Kotlin / native Android
- Android SDK 34 (`minSdk 24`, `targetSdk 34`)
- AndroidX Core KTX and AppCompat
- Material Components
- ConstraintLayout
- Volley HTTP client
- JUnit 4 and Espresso dependencies
- Gradle Kotlin DSL

## Current implemented behavior

The existing prototype includes:

- vehicle/parking record creation
- vehicle list/query flow
- record detail retrieval
- record update flow
- entry and exit time handling
- basic hourly parking-rate calculation
- Android client → HTTP endpoint communication

## Known technical debt

The current code is **not yet production-ready**. The modernization backlog intentionally documents the existing debt:

- default package/application id `com.example.parcial`
- networking and UI/domain logic concentrated in Activities
- local cleartext endpoint (`http://10.0.2.2/...`) used by the original prototype
- legacy Volley-oriented networking without typed API contracts
- no dependency-injection strategy
- no persistence/offline architecture
- no environment/flavor strategy
- limited automated test evidence
- generic activity names
- IDE metadata currently versioned
- no CI quality gate yet

## Target architecture

The target open-source version will evolve toward:

```text
Android App (Kotlin)
│
├── presentation/
│   ├── Compose / Material 3
│   ├── ViewModels
│   └── StateFlow
│
├── domain/
│   ├── models
│   ├── use cases
│   └── repository contracts
│
├── data/
│   ├── remote/     → Retrofit / OkHttp
│   ├── local/      → Room
│   └── repository/
│
├── core/
│   ├── network
│   ├── security
│   ├── result/error model
│   └── configuration
│
└── tests/
    ├── unit
    ├── integration
    └── UI
```

Planned engineering direction:

`Kotlin` · `Jetpack Compose` · `Material 3` · `MVVM/Clean Architecture` · `Coroutines` · `Flow/StateFlow` · `Hilt` · `Retrofit` · `OkHttp` · `Room` · `DataStore` · `Navigation` · `WorkManager` · `JUnit` · `MockK` · `Compose UI Tests` · `GitHub Actions`

## Product vision

The long-term open-source project is intended to become a reusable parking-management client capable of supporting:

- parking-space availability
- vehicle check-in / check-out
- configurable rate rules
- duration and fee calculation
- reservations
- QR-based ticket/check-in flows
- receipts/history
- offline-first operation
- role-aware operator workflows
- maps/location integration where appropriate
- notifications
- a documented backend/API contract

The scope will be delivered incrementally. Features are **roadmap items, not current claims**.

## Modernization roadmap

See [`ROADMAP.md`](./ROADMAP.md) for the phased plan from legacy prototype to a maintainable open-source Android application.

## Open source

This project is being prepared as a community-friendly open-source project under the **Apache License 2.0**.

Contributions will be accepted once the baseline modernization phase is complete. See [`CONTRIBUTING.md`](./CONTRIBUTING.md).

## Evidence policy

This repository follows the same evidence-first rule used across my professional portfolio:

- implemented features are separated from planned features
- CI/test results are only claimed after they run successfully
- no production credentials or customer data are committed
- architecture changes are documented through pull requests
- security debt is documented instead of hidden

## Portfolio classification

**Category:** Native Android / Kotlin  
**Visibility:** Public / Open Source  
**Current priority:** Active revival  
**Target priority:** Portfolio evidence after Phase 2 quality gate

For broader engineering work, see my [GitHub profile](https://github.com/JavierQuinan).
