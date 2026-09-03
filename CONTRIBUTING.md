# Contributing

Thanks for your interest in improving Parking Android.

Contributions should preserve the repository's evidence-first and security-first approach: public claims must describe implemented source, observed checks or documented current behavior.

## Before contributing

1. Read [`README.md`](./README.md), [`ENGINEERING_EVIDENCE.md`](./ENGINEERING_EVIDENCE.md) and [`DEVELOPMENT.md`](./DEVELOPMENT.md).
2. Keep pull requests focused on one concrete problem or engineering improvement.
3. Avoid bundling unrelated refactors and product changes.
4. Never commit production credentials, API keys, signing material, personal data, customer data or private backend URLs.
5. Run the local verification command documented in `DEVELOPMENT.md` when the change affects buildable Android code.

## Engineering principles

- Kotlin-first native Android engineering
- business rules extracted from Android framework classes where practical
- explicit environment/network configuration
- HTTPS-first release posture
- deterministic unit tests for pure Kotlin logic
- clear error and validation boundaries
- synthetic/demo data in public evidence
- accurate claims instead of production-readiness language without proof

## Pull requests

A pull request should include:

- the problem being corrected;
- the implementation approach;
- tests for changed behavior where applicable;
- screenshots for UI changes only when they use synthetic/demo data;
- compatibility notes when changing the legacy JSON/backend contract;
- the actual verification performed.

Current commit-scope examples:

```text
refactor(domain): extract parking fee calculation
fix(validation): reject malformed checkout data
test(domain): cover parking input rules
docs: document current backend contract
chore(ci): harden Android verification workflow
```

## Security issues

Do not open a public issue containing a real credential, personal data, private infrastructure detail or exploitable vulnerability. Follow [`SECURITY.md`](./SECURITY.md) for coordinated reporting.

## Conduct

Participation in project spaces is governed by [`CODE_OF_CONDUCT.md`](./CODE_OF_CONDUCT.md).

## Licensing

By contributing, you agree that your contribution will be licensed under the repository's Apache License 2.0.
