# FHI Format

The authoritative specification is [`docs/specs/fhi-schema.adoc`](https://github.com/hyperpolymath/tma-mark2/blob/main/docs/specs/fhi-schema.adoc). Companion: [`docs/specs/integrity.adoc`](https://github.com/hyperpolymath/tma-mark2/blob/main/docs/specs/integrity.adoc) for the integrity guarantees (hash chains, signatures).

## In one sentence

A `.fhi` file is the wire format the Open University's submission system uses to ship a student's electronic Tutor-Marked Assignment to a tutor for marking. The eTMA Handler consumes `.fhi` on the inbound side and emits a graded `.docx` on the outbound side.

## Status of the parser

| Surface | Status |
|---------|--------|
| Parser (`.fhi` → struct) | 0% — stub |
| Generator (struct → `.fhi`) | 0% — stub |
| Test fixtures (`test/fixtures/fhi/`) | empty — Phase 1.2 is blocked here |

The parser is the next milestone on the [v1 critical path](Roadmap-and-Status); progress is gated on obtaining ≥ 5 real anonymised `.fhi` samples.

## See also

* [`EXPLAINME.adoc`](https://github.com/hyperpolymath/tma-mark2/blob/main/EXPLAINME.adoc) for the README claims this format backs up.
* [`docs/architecture/overview.adoc`](https://github.com/hyperpolymath/tma-mark2/blob/main/docs/architecture/overview.adoc) for where the parser sits in the pipeline.
