# Security Policy

Security and privacy changes in Parking Android are handled under an evidence-first, least-privilege approach.

## Supported code

The actively maintained security baseline is the latest commit on `main`. Historical prototype commits and abandoned branches are not treated as supported releases.

## Reporting a vulnerability

Do not disclose exploitable details, credentials, personal data, customer data, private backend URLs or other sensitive evidence in a public issue.

Use GitHub's private vulnerability reporting / Security Advisory flow when it is available for this repository. If that channel is unavailable, open a minimal public issue that contains no exploit details and only requests a private communication channel.

A useful private report should include:

- affected component and version/commit
- attack preconditions
- reproducible steps or proof of concept using synthetic data
- expected impact
- suggested mitigation when known

## Scope

Relevant reports include issues affecting the Android client, dependency integrity, local data handling, network transport, authentication/authorization when introduced, exported Android components, secrets handling and privacy-sensitive vehicle/location data.

## Coordinated disclosure

Please allow remediation and validation before publishing technical exploit details. Security fixes should include regression evidence when practical.
