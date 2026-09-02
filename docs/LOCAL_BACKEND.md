# Local / demo backend strategy

The Android repository does not ship or claim a production backend. The historical client communicates with a single PHP-style endpoint (`auto.php`) through action-based JSON requests.

## Configuration boundary

The client reads the base URL from `BuildConfig.API_BASE_URL`.

Configuration sources, in priority order:

1. Gradle property `PARKING_API_BASE_URL`
2. environment variable `PARKING_API_BASE_URL`
3. debug-only fallback `http://10.0.2.2/Parcial/`

Release builds do not receive a default endpoint. A release can compile with an empty base URL, but network-backed runtime flows remain intentionally unavailable until an explicit HTTPS endpoint is supplied.

Example local verification with an explicit endpoint:

```bash
./gradlew -PPARKING_API_BASE_URL=http://10.0.2.2/Parcial/ assembleDebug
```

## Cleartext policy

The main manifest disables cleartext traffic. Only the debug source set adds a Network Security Configuration exception, restricted to Android Emulator host `10.0.2.2`.

This prevents the historical local HTTP endpoint from silently becoming a production networking policy.

## Legacy action contract

The current client sends JSON POST requests to `auto.php`. The actions evidenced in source are:

| Action | Current purpose |
| --- | --- |
| `consultarDato` | login/authentication lookup |
| `Insertar` | create vehicle/parking record |
| `consultar` | list parking records |
| `Datos` | retrieve one record by code |
| `Actualizar` | update checkout data and calculated fee |

This table documents the existing prototype contract only. It is not an OpenAPI specification and does not imply production stability.

## Next contract step

Phase 2/9 will replace ad-hoc JSON/Volley coupling with typed transport models and a versioned backend contract. Until then, changes to these action names should be treated as compatibility changes and documented in pull requests.
