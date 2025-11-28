<!-- SPDX-FileCopyrightText: 2024 eTMA Handler Contributors -->
<!-- SPDX-License-Identifier: MIT -->

# Security Policy

## Supported Versions

| Version | Supported          | End of Life    |
| ------- | ------------------ | -------------- |
| 2.x.x   | :white_check_mark: | Active         |
| 1.x.x   | :x:                | 2024-01-01     |
| < 1.0   | :x:                | Not supported  |

## Reporting a Vulnerability

We take security seriously. If you discover a security vulnerability, please follow these steps:

### Reporting Process

1. **Do NOT** create a public GitHub issue for security vulnerabilities
2. Email security concerns to the maintainers (see MAINTAINERS.md)
3. Include:
   - Description of the vulnerability
   - Steps to reproduce
   - Potential impact assessment
   - Affected versions
   - Suggested fix (if any)

### Response SLA

| Severity | Acknowledgement | Status Update | Resolution Target |
| -------- | --------------- | ------------- | ----------------- |
| Critical | 24 hours        | 48 hours      | 7 days            |
| High     | 24 hours        | 72 hours      | 14 days           |
| Medium   | 48 hours        | 7 days        | 30 days           |
| Low      | 72 hours        | 14 days       | 90 days           |

**Severity Definitions**:
- **Critical**: Remote code execution, data breach, authentication bypass
- **High**: Privilege escalation, significant data exposure
- **Medium**: Information disclosure, CSRF
- **Low**: Minor information leakage, best practice violations

## Security Features

### Data Protection

- **Local Storage**: All data stored locally using CubDB (no cloud)
- **Encryption**: XChaCha20-Poly1305 for sensitive data (optional)
- **Password Hashing**: Argon2id for any authentication
- **No Telemetry**: No data sent to external servers

### Assignment Data

- Student submissions processed locally only
- No submission data leaves the tutor's machine
- Temporary files cleaned up after processing

### Network Security

- Default: Localhost-only web interface
- No external network calls required
- Optional: Post-quantum key exchange (Kyber)

### Code Security

- Elixir/BEAM fault tolerance
- No native code dependencies (pure Elixir where possible)
- WASM sandbox for plugins
- Dependencies audited regularly

## Security Best Practices

1. **Keep Updated**: Use the latest version
2. **File Permissions**: Protect data directory
3. **Network**: Keep on localhost unless needed
4. **Backups**: Enable automatic backups
5. **Passwords**: Use strong passwords if auth enabled

## Vulnerability Disclosure

1. Report received and acknowledged
2. Vulnerability verified and assessed
3. Fix developed and tested
4. Patch released with advisory
5. Public disclosure after patch

---

Last updated: 2024-12-01
