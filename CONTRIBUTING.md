# Contributing

Thanks for your interest in improving Parking Android.

This repository is currently in an **open-source revival** phase. Contributions are welcome, but changes should preserve the evidence-first and security-first direction of the project.

## Before contributing

1. Read [`README.md`](./README.md) and [`ROADMAP.md`](./ROADMAP.md).
2. Check whether the proposed change belongs to the current modernization phase.
3. Avoid bundling unrelated refactors and product features in the same pull request.
4. Never commit production credentials, API keys, personal data, customer data or private backend URLs.

## Development principles

- Kotlin-first native Android engineering
- clear separation between presentation, domain and data layers
- no business logic in Activities/Composables when it belongs in domain/use-case code
- HTTPS-first networking
- dependency injection instead of hidden global dependencies
- deterministic tests where practical
- explicit error states
- English-first technical naming and documentation, with Spanish documentation encouraged for user-facing material

## Pull requests

A good pull request should include:

- a focused problem statement
- the implementation approach
- tests for changed behavior when applicable
- screenshots for UI changes using only synthetic/demo data
- migration/compatibility notes for architectural changes
- no unsupported claims such as “production-ready” unless the repository evidence supports them

## Commit scope examples

```text
feat(parking): add check-in use case
refactor(network): introduce typed API client
fix(rates): handle overnight parking sessions
test(domain): cover hourly-rate calculation
docs: document local development environment
chore(ci): add Android build quality gate
```

## Security issues

Do not open a public issue containing a real credential or exploitable private-system detail. A dedicated security reporting policy will be added during Phase 0 of the roadmap.

## Licensing

By contributing, you agree that your contribution will be licensed under the repository's Apache License 2.0.
