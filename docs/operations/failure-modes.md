<!-- SPDX-FileCopyrightText: 2024 eTMA Handler Contributors -->
<!-- SPDX-License-Identifier: MIT -->

# eTMA Handler Failure Mode Analysis

**Date:** 2026-01-05
**Status:** Living document - update as new failures discovered
**Methodology:** Multi-perspective threat modeling based on observed incidents and expert analysis

---

## Executive Summary

This document catalogs known and anticipated failure modes for the eTMA marking system.
Each failure is analyzed from multiple expert perspectives to ensure comprehensive coverage.

**Observed Incidents (This Session):**
1. XML corruption - missing `>` characters caused student to "vanish"
2. Silent save failure - permission dialog interrupted save, no error shown
3. Duplicate records appearing in UI due to parse failures

---

## 1. FORENSICS EXPERT PERSPECTIVE

### 1.1 Evidence Destruction

| Failure Mode | Likelihood | Impact | Observed? |
|--------------|------------|--------|-----------|
| Auto-delete after send destroys only local copy | HIGH | CRITICAL | Design flaw |
| Corrupted file overwritten without backup | HIGH | CRITICAL | ✓ Observed |
| No audit trail of what was actually sent | MEDIUM | HIGH | Design flaw |
| Timestamps unreliable (system clock, timezone) | MEDIUM | MEDIUM | Suspected |

**Recommendations:**
- NEVER delete originals until confirmed receipt + N days
- Every write must create timestamped backup first
- Hash everything, store hashes separately from content
- Immutable audit log of all operations

### 1.2 Chain of Custody

| Failure Mode | Risk |
|--------------|------|
| Cannot prove file wasn't modified after marking | No digital signatures |
| Cannot prove when marking actually occurred | Timestamps mutable |
| Cannot prove who did the marking | No authentication |
| Student could claim different feedback was sent | No receipt mechanism |

**Recommendations:**
- Sign all outgoing bundles with tutor's key
- Timestamp authority integration
- Blockchain-style append-only audit log

---

## 2. AUDIT EXPERT PERSPECTIVE

### 2.1 Compliance Failures

| Regulation | Risk | Current State |
|------------|------|---------------|
| GDPR - Data subject access | Cannot reconstruct what was sent | No archive |
| GDPR - Right to erasure | Cannot prove deletion | No deletion log |
| Accessibility - WCAG | Feedback may be inaccessible | No checks |
| Academic integrity | Cannot prove marking was fair | No audit trail |
| OU Quality Assurance | Cannot demonstrate consistency | No metrics |

### 2.2 Financial Audit Risks

| Risk | Scenario |
|------|----------|
| Paid for unmarked work | System shows "sent" but wasn't |
| Duplicate payments | Same assignment marked twice (different tutors) |
| Time fraud | Timestamps manipulated to meet deadlines |
| Grade manipulation | No tamper evidence on scores |

---

## 3. GOVERNANCE EXPERT PERSPECTIVE

### 3.1 Accountability Gaps

```
WHO is responsible when:
├── Student claims feedback never received?
│   └── No proof of send, no receipt confirmation
├── Feedback contains errors/offensive content?
│   └── No pre-send review mechanism
├── Wrong mark recorded?
│   └── No validation between local and central
├── Data breach of student PII?
│   └── FHI files contain full address, unencrypted
└── System fails during deadline crunch?
    └── No SLA, no escalation path
```

### 3.2 Process Gaps

| Gap | Risk |
|-----|------|
| No formal handoff between marking and sending | Partial work can be sent |
| No approval workflow for edge cases | Extensions, late work, appeals |
| No version control on feedback | Cannot track changes |
| No peer review integration | Quality varies by tutor |

---

## 4. LAWYER PERSPECTIVE

### 4.1 Liability Exposure

| Scenario | Legal Risk |
|----------|------------|
| Student fails due to corrupted/lost feedback | Negligence claim |
| PII exposed in breach | GDPR fines (4% revenue) |
| Discriminatory feedback sent | Equality Act violation |
| Wrong student receives another's feedback | Data protection breach |
| Feedback plagiarized from another tutor | IP infringement |

### 4.2 Evidence Problems

| Problem | Legal Impact |
|---------|--------------|
| Cannot prove what was sent | Loses any dispute |
| Cannot prove when sent | Deadline disputes unresolvable |
| Cannot prove feedback was appropriate | Cannot defend complaints |
| Metadata reveals unintended info | Discovery nightmare |

### 4.3 Contract Issues

| Issue | Risk |
|-------|------|
| Implied duty of care to students | Breached by data loss |
| Employment contract with tutors | Tool failures = employer liability |
| Software license compliance | Using decompiled code? |
| Third-party dependencies | Supply chain liability |

---

## 5. ACCOUNTANT PERSPECTIVE

### 5.1 Asset Tracking

| Asset | Current Tracking | Risk |
|-------|------------------|------|
| Feedback content (IP) | None | Loss, theft, misattribution |
| Time spent marking | None | Cannot verify, billing disputes |
| Reusable comments (bank) | Local only | Lost on machine failure |
| Student submissions | Copied locally | Unauthorized retention |

### 5.2 Cost of Failures

| Failure | Direct Cost | Indirect Cost |
|---------|-------------|---------------|
| Lost feedback, remark needed | ~2 hours tutor time | Student complaint handling |
| Data breach | Fines + remediation | Reputation damage |
| System downtime at deadline | Overtime, extensions | Student anxiety, complaints |
| Grade appeal due to lost evidence | Review process | Legal if escalated |

---

## 6. HACKER PERSPECTIVE

### 6.1 Attack Surface

```
ATTACK VECTORS:
├── Local Machine
│   ├── FHI files unencrypted → Full PII exposure
│   ├── No integrity checks → Silent modification
│   ├── Predictable file paths → Easy targeting
│   └── Java app runs with user privileges → Malware vector
│
├── Network
│   ├── OU API credentials stored how? → Credential theft
│   ├── HTTPS but certificate pinning? → MITM possible
│   └── Update mechanism secure? → Supply chain attack
│
├── Data at Rest
│   ├── Student PII in plaintext → Laptop theft = breach
│   ├── Comments bank = years of IP → Valuable target
│   └── Backups unencrypted? → Secondary exposure
│
└── Human
    ├── Phishing for OU credentials → Account takeover
    ├── Social engineering tutor → "Re-send" to wrong address
    └── Insider threat (disgruntled tutor) → Mass data exfil
```

### 6.2 Exploit Scenarios

| Attack | Difficulty | Impact | Detection |
|--------|------------|--------|-----------|
| Modify FHI to change grades | EASY (text file) | HIGH | NONE currently |
| Steal all student PII from disk | EASY | CRITICAL | NONE |
| Inject malicious content in feedback | EASY | HIGH | NONE |
| Impersonate tutor to OU system | MEDIUM | CRITICAL | Maybe server-side |
| Ransomware targeting etmas folder | EASY | HIGH | Too late |
| Supply chain via compromised update | HARD | CRITICAL | NONE |

### 6.3 XML-Specific Attacks

| Attack | Payload | Impact |
|--------|---------|--------|
| XXE (XML External Entity) | `<!DOCTYPE foo [<!ENTITY xxe SYSTEM "file:///etc/passwd">]>` | File disclosure |
| Billion Laughs (DoS) | Recursive entity expansion | System crash |
| XPath Injection | Via student name field | Data leakage |
| XML Injection | `</tutor_comments><score>100</score>` in comment | Grade manipulation |

---

## 7. CYBERWAR COMMANDER PERSPECTIVE

### 7.1 Strategic Vulnerabilities

| Vulnerability | Nation-State Interest |
|---------------|----------------------|
| Mass student PII | Intelligence gathering |
| Academic integrity | Undermine trust in UK education |
| Research student data | IP theft, recruitment |
| Tutor credentials | Lateral movement to OU systems |

### 7.2 Disruption Scenarios

| Scenario | Method | Impact |
|----------|--------|--------|
| Mass grade manipulation | Compromise multiple tutors | Chaos, grade inflation/deflation |
| Selective targeting | Modify specific student records | Blackmail, influence |
| Data destruction | Ransomware at deadline | Maximum disruption |
| Disinformation | Insert false feedback | Student/tutor conflict |

### 7.3 Resilience Assessment

| Capability | Current State | Required State |
|------------|---------------|----------------|
| Backup & Recovery | Ad-hoc | Automated, tested, off-site |
| Incident Detection | None | Real-time monitoring |
| Incident Response | None | Documented playbook |
| Business Continuity | None | Alternate marking path |

---

## 8. ESCROW EXPERT PERSPECTIVE

### 8.1 Trust Model Failures

```
CURRENT TRUST MODEL:
Student ──submits──> OU System ──distributes──> Tutor
                                                  │
Tutor ──marks──> Local Machine ──bundles──> OU System
                      │
              NO ESCROW POINT
              NO VERIFICATION
              NO RECEIPT
```

| Gap | Risk |
|-----|------|
| No neutral third party | Disputes unresolvable |
| No tamper-evident storage | Evidence can be altered |
| No time-stamping authority | "When" is disputable |
| No delivery confirmation | "Did they get it?" unknown |

### 8.2 Recommended Escrow Points

```
IMPROVED MODEL:
1. On receipt of student work:
   └── Hash and timestamp → Escrow service

2. On completion of marking:
   └── Hash feedback + original → Escrow service

3. On send:
   └── Hash of bundle → Escrow + delivery receipt

4. Disputes:
   └── Escrow provides tamper-evident record
```

---

## 9. TRADEMARK ATTORNEY PERSPECTIVE

### 9.1 IP Risks

| Asset | Risk | Protection |
|-------|------|------------|
| Comment bank content | Copied between tutors without attribution | None |
| Feedback templates | OU claims ownership? Tutor claims? | Undefined |
| Student work | Retained beyond necessary | Policy violation |
| Module materials quoted | Copyright in feedback | Fair use unclear |

### 9.2 Attribution Failures

| Scenario | Risk |
|----------|------|
| Tutor A's comments used by Tutor B | No tracking |
| "Best practice" comments shared | Origin lost |
| AI-assisted feedback | Disclosure required? |
| Student quotes tutor feedback | Attribution chain |

---

## 10. VAULT/SECRETS MANAGER PERSPECTIVE

### 10.1 Credential Exposure

| Secret | Current Storage | Risk |
|--------|-----------------|------|
| OU API credentials | Unknown (config file?) | Plaintext exposure |
| Tutor identity | FHI files | PII in every file |
| Student data | FHI files | Unencrypted at rest |
| Comment bank | Local file | No access control |

### 10.2 Secret Rotation

| Secret | Rotation | Impact of Compromise |
|--------|----------|---------------------|
| OU credentials | Never? | Full account takeover |
| Local encryption key | N/A (none used) | N/A |
| API tokens | Unknown | Unauthorized actions |

### 10.3 Recommended Vault Integration

```
SECRETS THAT SHOULD BE IN VAULT:
├── OU API credentials (rotated monthly)
├── Encryption keys for local storage
├── Signing keys for bundles
├── Session tokens (short-lived)
└── Backup encryption keys (escrowed)

VAULT POLICIES:
├── Tutors: read own credentials only
├── System: rotate credentials automatically
├── Audit: log all secret access
└── Emergency: break-glass procedure documented
```

---

## 11. CRYPTOGRAPHER PERSPECTIVE

### 11.1 Cryptographic Failures

| Failure | Current State | Required State |
|---------|---------------|----------------|
| No encryption at rest | FHI files plaintext | AES-256-GCM |
| No integrity protection | Files can be modified | HMAC or signatures |
| No authenticity | Anyone can create FHI | Digital signatures |
| No confidentiality in transit | Relies on HTTPS only | E2E encryption |
| No forward secrecy | Compromise = all history | Rotating keys |

### 11.2 Recommended Cryptographic Controls

```
DATA AT REST:
├── Student PII: Encrypted with tutor's key
├── Feedback content: Encrypted with derived key
├── Comment bank: Encrypted with master key
└── Backups: Encrypted with separate key (escrowed)

DATA IN TRANSIT:
├── TLS 1.3 minimum
├── Certificate pinning
└── Mutual TLS for API calls

SIGNATURES:
├── Tutor signs completed marking
├── System countersigns on receipt
└── Timestamp authority provides time proof
```

### 11.3 Key Management

| Key | Generation | Storage | Rotation | Recovery |
|-----|------------|---------|----------|----------|
| Tutor marking key | On first use | Hardware token ideal, software acceptable | Annual | Escrow with OU |
| Encryption key | Derived from password | Never stored | On password change | Password reset |
| Signing key | Per-session | Memory only | Per session | N/A |

---

## 12. DIGITAL CURATOR PERSPECTIVE

### 12.1 Preservation Risks

| Risk | Timeframe | Impact |
|------|-----------|--------|
| Format obsolescence | 5-10 years | Cannot read old feedback |
| Software obsolescence | Now (Java Swing) | Cannot run tool |
| Storage media failure | 3-5 years | Data loss |
| Organizational change | Any time | Access lost |
| Legal retention requirements | 6+ years | Compliance failure |

### 12.2 Archival Requirements

| Content | Retention Period | Format | Access |
|---------|------------------|--------|--------|
| Marked submissions | Exam board + appeals period | Original + PDF/A | Restricted |
| Feedback given | Same as above | PDF/A + plaintext | Restricted |
| Comment bank | Indefinite (tutor IP) | Open format | Tutor only |
| Audit logs | 7 years minimum | Immutable | Compliance team |

### 12.3 Migration Path

```
CURRENT: FHI (custom XML) + DOCX/PDF
    │
    ▼
ARCHIVE:
├── PDF/A-3 (feedback embedded in submission)
├── Detached XML (machine-readable metadata)
├── SHA-256 manifest
└── Digital signature
```

---

## 13. OBSERVED FAILURES (This Session)

### 13.1 XML Corruption Incident

**What happened:**
1. Tutor saved feedback for Rachael Mortimer
2. Save operation produced malformed XML
3. On reload, parser failed
4. UI showed "Holly appears twice, Rachael vanished"

**Root cause:**
```java
// Original code creates empty file FIRST
new FileWriter(pathname).close();  // File now empty!
// Then tries to write - if interrupted, empty file remains
```

**Failure chain:**
```
Save clicked
    │
    ▼
Permission dialog appeared (audio subsystem)
    │
    ▼
Save operation interrupted
    │
    ▼
Partial XML written: <tag<tag instead of <tag><tag>
    │
    ▼
No error shown to user
    │
    ▼
User assumes save succeeded
    │
    ▼
Reload: parser fails on malformed XML
    │
    ▼
Student record "vanishes"
```

### 13.2 Silent Save Failure

**What happened:**
1. Tutor made edits
2. Clicked save
3. Permission dialog appeared
4. Edits were NOT saved
5. No error message shown
6. Tutor discovered later that work was lost

**Root cause:**
```java
catch (Exception anException) {
    System.out.println("Error2: " + anException);  // Only to console!
    // User never sees this
}
// ...
this.savedFlag = true;  // Set regardless of actual outcome!
```

---

## 14. FAILURE PROBABILITY MATRIX

| Failure Mode | Likelihood | Impact | Detection | Priority |
|--------------|------------|--------|-----------|----------|
| Silent save failure | HIGH | CRITICAL | NONE | P0 |
| XML corruption | HIGH | CRITICAL | NONE | P0 |
| PII exposure (laptop theft) | MEDIUM | CRITICAL | NONE | P0 |
| Wrong student receives feedback | LOW | CRITICAL | NONE | P1 |
| Grade manipulation | LOW | CRITICAL | NONE | P1 |
| Backup failure | MEDIUM | HIGH | NONE | P1 |
| Credential theft | LOW | HIGH | NONE | P2 |
| Format obsolescence | CERTAIN (long-term) | MEDIUM | N/A | P2 |

---

## 15. MITIGATION ROADMAP

### Phase 0: Immediate (This Week)
- [x] Fix XML corruption (atomic writes)
- [x] Add save verification
- [x] Create backup system
- [ ] Encrypt FHI files at rest

### Phase 1: Short-term (This Month)
- [ ] Implement IntegrityStore
- [ ] Add audit logging
- [ ] Pre-send validation (identity checks)
- [ ] File lock detection

### Phase 2: Medium-term (This Quarter)
- [ ] Digital signatures on bundles
- [ ] Delivery receipts
- [ ] Encrypted storage
- [ ] Proper key management

### Phase 3: Long-term (This Year)
- [ ] Escrow integration
- [ ] Timestamp authority
- [ ] Full audit trail
- [ ] Archive format migration

---

## 16. TESTING CHECKLIST

### Before Every Release

- [ ] Save interruption test (kill process mid-save)
- [ ] Disk full test
- [ ] Permission denied test
- [ ] Network failure during send test
- [ ] Corrupted file recovery test
- [ ] Wrong encoding test (UTF-8 vs ISO-8859-1)
- [ ] Special characters in student name test
- [ ] Maximum file size test
- [ ] Concurrent access test
- [ ] Clock skew test

### Chaos Engineering Scenarios

- [ ] Delete random file mid-operation
- [ ] Corrupt random bytes in FHI
- [ ] Revoke file permissions during save
- [ ] Kill process at each state transition
- [ ] Simulate full disk at each write point
- [ ] Network timeout at each API call

---

## Appendix A: Incident Response Playbook

### A.1 Data Loss Detected

```
1. STOP - Do not overwrite anything
2. Check ~/.etma-backup for recent backups
3. Check IntegrityStore for content-addressed copy
4. If found: restore and verify
5. If not found: check forensic logs for last known good state
6. Document incident
7. Notify affected parties if PII involved
```

### A.2 Suspected Breach

```
1. Isolate affected machine (network disconnect)
2. Preserve evidence (forensic image)
3. Notify OU IT Security
4. Assess scope (which students affected)
5. GDPR notification if required (72 hours)
6. Document timeline
```

### A.3 Grade Dispute

```
1. Pull audit log for disputed submission
2. Retrieve signed bundle from escrow
3. Compare hash to student's claimed receipt
4. Reconstruct timeline from forensic logs
5. Provide evidence to academic authority
```

---

**Document Version:** 1.0
**Last Updated:** 2026-01-05
**Next Review:** 2026-02-05
