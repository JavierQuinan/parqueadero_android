# Parking Android Prototype

> **Repository status:** learning / historical prototype. This repository is kept as technical evidence of native Android work and is **not presented as a production-ready or featured portfolio project**.

## Purpose

Native Android prototype used to explore a parking-management application flow and Android client development.

## Verified technical stack

- Kotlin / Android
- Android SDK 34 (`minSdk 24`, `targetSdk 34`)
- AndroidX Core KTX and AppCompat
- Material Components
- ConstraintLayout
- Volley for HTTP networking
- JUnit and Espresso dependencies for testing

## Current technical debt

The repository intentionally remains classified as a prototype because the current codebase still contains implementation details that should be redesigned before any professional showcase or production use:

- default/example application namespace (`com.example.parcial`)
- generic activity names
- `android:usesCleartextTraffic="true"`
- no documented architecture or environment strategy
- limited automated-test evidence

These limitations are documented instead of being hidden or overstated.

## If this project is revived

A professional rework should include a domain-specific package name, HTTPS-only networking, environment configuration, ViewModel-based state management, repository/data layers, structured API client handling, improved test coverage and CI validation.

## Portfolio classification

**Category:** Android / Kotlin learning evidence  
**Visibility:** Public  
**Portfolio priority:** Low  
**Recommended use:** Historical evidence, not a pinned repository

For current professional work and selected engineering projects, see the main [GitHub profile](https://github.com/JavierQuinan).
