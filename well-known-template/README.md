# .well-known Template

A comprehensive template for web identity, consent, and discovery standards.

## Quick Start

```bash
cp -r .well-known /your/project/
cp robots.txt llms.txt ads.txt /your/project/
# Edit files to match your project
```

## File Inventory

### Security & Trust

| File | Standard | Status | Purpose |
|------|----------|--------|---------|
| `.well-known/security.txt` | RFC 9116 | Active | Security contact & disclosure policy |
| `.well-known/pgp-key.txt` | - | Active | PGP public key for encrypted reports |
| `robots.txt` | RFC 9309 | Active | Crawler directives |

### AI & Consent (Emerging)

| File | Standard | Status | Purpose |
|------|----------|--------|---------|
| `.well-known/aibdp.json` | AIBDP | Draft | AI Boundary Declaration Protocol |
| `.well-known/ai.txt` | - | Informal | Simple AI policy (human-readable) |
| `.well-known/consent-required.txt` | - | Draft | Data handling consent requirements |
| `llms.txt` | llmstxt.org | Informal | LLM context file |

### Identity & Discovery

| File | Standard | Status | Purpose |
|------|----------|--------|---------|
| `.well-known/webfinger` | RFC 7033 | Active | User/resource discovery |
| `.well-known/host-meta` | RFC 6415 | Active | Host metadata (XRD) |
| `.well-known/did.json` | W3C DID | Active | Decentralized Identifier |
| `.well-known/keybase.txt` | - | Dead | Keybase proof (RIP) |
| `.well-known/nostr.json` | NIP-05 | Active | Nostr identity verification |
| `.well-known/atproto-did` | AT Protocol | Active | Bluesky DID |
| `.well-known/matrix/server` | Matrix | Active | Matrix server discovery |
| `.well-known/matrix/client` | Matrix | Active | Matrix client discovery |
| `.well-known/nodeinfo` | NodeInfo | Active | Federated network metadata |
| `.well-known/avatar` | - | Informal | Avatar URL |

### Authentication & Authorization

| File | Standard | Status | Purpose |
|------|----------|--------|---------|
| `.well-known/openid-configuration` | OIDC | Active | OpenID Connect discovery |
| `.well-known/oauth-authorization-server` | RFC 8414 | Active | OAuth server metadata |
| `.well-known/jwks.json` | RFC 7517 | Active | JSON Web Key Set |
| `.well-known/change-password` | - | Active | Password change URL hint |

### Advertising & Monetization

| File | Standard | Status | Purpose |
|------|----------|--------|---------|
| `ads.txt` | IAB | Active | Authorized digital sellers |
| `sellers.json` | IAB | Active | Seller information |
| `app-ads.txt` | IAB | Active | Mobile app ad sellers |

### Mobile & Apps

| File | Standard | Status | Purpose |
|------|----------|--------|---------|
| `.well-known/assetlinks.json` | Google | Active | Android App Links |
| `.well-known/apple-app-site-association` | Apple | Active | iOS Universal Links |

### Privacy & Legal

| File | Standard | Status | Purpose |
|------|----------|--------|---------|
| `.well-known/dnt-policy.txt` | EFF | Abandoned | Do Not Track policy |
| `.well-known/gpc.json` | GPC | Active | Global Privacy Control |
| `.well-known/privacy-policy` | - | Informal | Privacy policy redirect |

### Provenance & Supply Chain

| File | Standard | Status | Purpose |
|------|----------|--------|---------|
| `.well-known/provenance.json` | - | Draft | Build/source provenance |
| `sbom.json` | CycloneDX/SPDX | Active | Software Bill of Materials |

### Miscellaneous

| File | Standard | Status | Purpose |
|------|----------|--------|---------|
| `.well-known/humans.txt` | humanstxt.org | Active | Project credits |
| `.well-known/traffic-advice` | Chrome | Active | Speculation rules |
| `.well-known/core` | RFC 6690 | Active | CoRE link format (IoT) |

## The Graveyard

Standards that died so others could... also die:

- **Keybase** - Zoom acquisition (2020)
- **Persona/BrowserID** - Mozilla gave up (2016)
- **OpenID 1.0/2.0** - Replaced by OIDC
- **DNT** - Browser vendors abandoned it
- **P3P** - IE-only, XML nightmare
- **XRI/XRD** - Overcomplicated into irrelevance

## Philosophy

This template exists because:

1. **Silence is consent** - AI companies interpret missing declarations as permission
2. **Standards fragment** - Everyone wants to own the spec
3. **Dead protocols leave ghosts** - Keybase proofs still get checked
4. **Defense in depth** - Multiple declarations, multiple chances to be heard

## License

CC0 / Public Domain - Use however you want.
