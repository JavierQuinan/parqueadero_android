# Development baseline

This document defines the reproducible local build baseline for the current Android/Kotlin revival state.

## Toolchain

The repository currently uses:

- Android Gradle Plugin `8.2.2`
- Gradle Wrapper `8.2`
- Kotlin `1.9.22`
- Android `compileSdk 34`
- Android `minSdk 24`
- Android `targetSdk 34`
- JDK `17` for Gradle/AGP execution

A recent Android Studio version compatible with AGP 8.2.x can be used, but the Gradle Wrapper remains the source of truth for command-line builds.

## Local requirements

Install JDK 17 and Android SDK Platform 34. Android Studio can generate `local.properties` with the local SDK path; this file must remain untracked.

No production credential, signing key or private backend configuration is required to compile the current debug application.

## API configuration

Runtime networking is configured through `PARKING_API_BASE_URL`.

The Android build reads it from either:

- Gradle property `-PPARKING_API_BASE_URL=...`
- environment variable `PARKING_API_BASE_URL`

Debug builds retain a local emulator fallback (`http://10.0.2.2/Parcial/`) so the historical prototype can still be exercised locally. Release builds have no default endpoint and must be explicitly configured before network-backed runtime use.

The main manifest disables cleartext traffic. A debug-only Network Security Configuration permits HTTP only to emulator host `10.0.2.2`.

See [`docs/LOCAL_BACKEND.md`](./docs/LOCAL_BACKEND.md) for the legacy action contract and local/demo strategy.

## Verification commands

Linux/macOS:

```bash
./gradlew --no-daemon lintDebug testDebugUnitTest assembleDebug
```

Windows PowerShell / CMD:

```text
gradlew.bat --no-daemon lintDebug testDebugUnitTest assembleDebug
```

Expected outputs include Android Lint results, unit-test results and the debug APK generated under the Gradle build directories.

To override the debug API base URL explicitly:

```bash
./gradlew -PPARKING_API_BASE_URL=http://10.0.2.2/Parcial/ assembleDebug
```

## Before opening a pull request

Run the same verification command used by CI. UI evidence must use synthetic/demo data and must not expose vehicle records, credentials, personal information or private infrastructure.
