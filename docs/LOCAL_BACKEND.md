# Local / demo backend strategy

The Android repository does not ship or claim a production backend. The client communicates with a single PHP-style endpoint (`auto.php`) through the action-based JSON requests visible in the current source.

## Configuration boundary

The client reads the base URL from `BuildConfig.API_BASE_URL`.

Configuration sources, in priority order:

1. Gradle property `PARKING_API_BASE_URL`
2. environment variable `PARKING_API_BASE_URL`
3. debug-only fallback `http://10.0.2.2/Parcial/`

Release builds do not receive a default endpoint. A release can compile with an empty base URL; network-backed flows require an explicitly supplied endpoint.

Example local build with an explicit emulator endpoint:

```bash
./gradlew -PPARKING_API_BASE_URL=http://10.0.2.2/Parcial/ assembleDebug
```

## Cleartext policy

The main manifest disables cleartext traffic. Only the debug source set adds a Network Security Configuration exception restricted to Android Emulator host `10.0.2.2`.

This prevents the historical local HTTP endpoint from becoming the release networking policy.

## Current legacy action contract

The client sends JSON POST requests to `auto.php`. The actions evidenced in source are:

| Action | Current purpose |
| --- | --- |
| `consultarDato` | login/authentication lookup |
| `Insertar` | create vehicle/parking record |
| `consultar` | list parking records |
| `Datos` | retrieve one record by code |
| `Actualizar` | update checkout data and calculated fee |

This table documents the existing client contract only. It is not an OpenAPI specification and does not imply production stability.

## Current data boundary

The Android source still uses Volley + `JSONObject` directly in Activities for transport/response handling. Pure Kotlin validation and fee calculation are separated from that transport layer, but this repository does not claim a typed API client or stable production API.

Any public example endpoint, payload or screenshot must use local/synthetic information and must not expose credentials, private infrastructure or real vehicle/person data.
