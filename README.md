# ISCO Workforce Coordinator

**Repository**: `cloud-itonami/isco`

This repository owns workforce lookup, classification, and materialization
workflows. The source-standard catalog boundary is
`cloud-itonami/org-ilo-isco`; per-occupation operational blueprints remain in
the `cloud-itonami-isco-*` family. Historical DIDs and protocol namespaces are
preserved as compatibility identities.

Standalone actor repository for the complete ILO ISCO-08 classification mirror:
619 occupations across the 10/43/130/436 hierarchy.

- `manifest.edn`, `identity.edn`, `dependencies.edn`: canonical repository metadata
- `data/isco-occupations.edn`: authoritative occupation hierarchy
- `lex/`: canonical EDN API contracts
- `wire/`: external JSON, JSON-LD, BPMN, and sample payloads
- `src/isco/coordinator.cljk`: kotoba-clj coordinator source

Run the deterministic, network-free suite with:

```sh
kbb -cp src:test run_tests.cljk
```

Generated WASM, shell build runners, and Go/TinyGo artifacts are intentionally
not repository assets. The external kotoba engine is pinned in `dependencies.edn`.
