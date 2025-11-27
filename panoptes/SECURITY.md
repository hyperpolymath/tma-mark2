# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 3.x.x   | :white_check_mark: |
| 2.x.x   | :white_check_mark: |
| 1.x.x   | :x:                |

## Reporting a Vulnerability

We take security seriously. If you discover a security vulnerability, please follow these steps:

### Reporting Process

1. **Do NOT** create a public GitHub issue for security vulnerabilities
2. Email security concerns to: `security@panoptes.example.com`
3. Include:
   - Description of the vulnerability
   - Steps to reproduce
   - Potential impact
   - Suggested fix (if any)

### Response Timeline

- **Initial Response**: Within 48 hours
- **Status Update**: Within 7 days
- **Resolution Target**: Within 30 days (depending on severity)

### What to Expect

1. Acknowledgment of your report
2. Assessment of the vulnerability
3. Development of a fix
4. Coordinated disclosure (if applicable)
5. Credit in the security advisory (if desired)

## Security Features

### Local Processing

Panoptes processes all files locally. No data is sent to external servers unless you explicitly configure remote Ollama endpoints.

### File Permissions

- Panoptes respects file system permissions
- Operations are performed with the user's privileges
- No privilege escalation is performed

### Database Security

- SQLite database is stored in user-accessible location only
- No plaintext passwords are stored
- Configuration supports encryption for sensitive values

### Network Security

- Ollama communication is HTTP by default (localhost)
- Support for HTTPS when using remote endpoints
- No telemetry or analytics

### Code Security

- `unsafe` code is forbidden (`#![forbid(unsafe_code)]`)
- Dependencies are regularly audited
- Static analysis with Clippy at maximum strictness

## Security Best Practices

When using Panoptes:

1. **Keep Updated**: Always use the latest version
2. **Secure Ollama**: If exposing Ollama externally, use authentication
3. **File Permissions**: Restrict access to config files containing sensitive data
4. **Database Location**: Store database in secure, backed-up location
5. **Watch Directories**: Only watch directories you trust

## Vulnerability Disclosure Policy

We follow a coordinated vulnerability disclosure process:

1. Report received and acknowledged
2. Vulnerability verified and assessed
3. Fix developed and tested
4. Security advisory prepared
5. Fix released with advisory
6. Public disclosure after patch availability

## Security Audits

We welcome security audits and penetration testing. Please contact us before conducting any tests to ensure coordination.

## Hall of Fame

We thank the following security researchers for responsibly disclosing vulnerabilities:

- (Your name could be here)

---

Last updated: 2024-12-01
