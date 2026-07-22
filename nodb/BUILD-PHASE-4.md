# NoDB Build — Phase 4 Work Order (OpenSearch: DSL-on-the-wire, indexer, refresh)

**Read first:** `nodb/DESIGN.md` §3.3 (consistency contract), §5 (search backend, index
naming/aliases), §9 phase table, §10 risks (esp. 10a outbox emission, #3 wire format);
`nodb/BUILD-PHASE-3.md` Gate 0 results (v1 payload format — type-tagged JSON);
`nodb/RUNNING.md` (the stack this extends). Self-contained.

## Goal

Search moves to **OpenSearch, owned by NoDB**. After this phase, nodb mode needs no
embedded Elasticsearch: queries flow `app → XP (parse/normalize) → JSON DSL over gRPC →
NoDB (translate) → OpenSearch`, writes flow through the outbox → indexer → checkpoint,
and `refresh(SEARCH)` keeps today's read-your-writes contract. The full XP itest suite —
including the 68 search-dependent classes excluded since Phase 1 — runs green in nodb
mode. This is the long pole and the last big wall before a fully NoDB-native XP.

## Architecture decisions locked up front

1. **The JSON query DSL is the wire format** (supersedes "serialize the AST"). XP
   normalizes at its boundary: DSL queries pass through; NoQL strings are parsed (as
   today) then RENDERED to DSL (`QueryExpr → DSL` serializer — new, mechanical). The
   wire = DSL JSON + envelope (formatVersion, repo/branch, store-type, paging,
   principals + filter-mode, sort/aggregations/filters/highlight/suggest — all of which
   have JSON forms). NoDB translates ONE language; today's two parallel ES builder
   families (`factory/query/` expression-tree + `factory/dsl/`) collapse into one
   DSL→OpenSearch translator. The wire schema MAY be a superset of the public DSL if
   NoQL constructs lack DSL forms (internal contract; versioned).
   **Boundary (pinned):** the NoQL→DSL renderer lives in XP CORE (core-repo, beside
   the NoQL parser), invoked when SearchRequest is prepared — ABOVE the storage SPI.
   The nodb-client accepts only canonical DSL + envelope and is a pure serializer
   (zero query-language knowledge; missing DSL → fail fast, never translate). The
   NoDB server validates against the versioned wire schema and rejects unknown
   constructs loudly. The ES backend does NOT switch to the rendered DSL in this
   phase — its legacy path stays untouched (default byte-identical rule); converging
   ES onto DSL is optional later cleanup, never a Phase 4 requirement.
2. **Server-side translation** (resolved open question #1): the translator lives in
   NoDB. XP-side stays thin (parse + normalize + envelope).
3. **Index documents: XP-shipped v1.** XP keeps building index documents (today's
   `IndexDataService` output — property values expanded per index-config into typed
   sub-fields) and ships them to NoDB with the write; the outbox carries doc identity
   and the indexer applies them to OpenSearch. Server-side derivation from payloads
   (the design's end state, enabled by Phase 3's format spec) is an EXPLICIT LATER SWAP
   — do not fight translation parity and doc-building parity simultaneously. Record as
   the phase's main deferral.
4. **Version-history queries go to SQL, not OpenSearch.** They historically ran on the
   ES *storage-index* docs, which nodb mode never creates. Implement the small
   NodeVersionQuery surface (versions by node, active versions, version diff between
   branches, commits) directly on `node_version`/`branch_entry` in NoDB — a mini-slice
   of Phase 8 pulled forward. Bonus: this unblocks binary GC's `findVersions`
   dependency (risk #14) — note it, don't scope GC here.
5. **Scoring-parity acceptance rule** (decided in Gate 0, enforced from Gate C):
   deterministic sorts (field/path/ts/manual) must match ES **exactly** — order and
   counts. Score-ordered fulltext results must match as **sets** (same hits, same
   totals); ordering deltas from ES-2.4-vs-OpenSearch relevance differences are
   documented per corpus query, not silently accepted and not chased to impossibility.

## Branch

`nodb-phase4-opensearch` off `nodb-phase3-payloads`.

## Gates

| Gate | Deliverable | Verification (all must hold) | Est. |
|---|---|---|---|
| **0** | Spikes + inventory, no production code. (a) **DSL completeness**: enumerate every `QueryExpr` construct the NoQL parser emits (read the parser) and map each to a DSL form; list gaps → wire-schema superset additions. (b) **Envelope inventory**: everything on `SearchRequest` that isn't the query (principals/filter-mode, store-type incl. how branch/version/commit queries arrive, multi-repo sources, highlight/suggest params). (c) **Translator-surface inventory** from the ES code by family (queries, aggs, sorts, suggest, highlight, field-name resolution rules, analyzer/mapping settings incl. ICU). (d) **OpenSearch stack**: version choice, analysis-icu plugin, container, memory; index template for `<tenant>-<repo>+g<N>` + alias. (e) **Golden-query corpus harness design**: record ES-mode results (hits/order/totals/buckets) for a corpus once, diff harness for nodb mode; define the corpus (~50 queries spanning every family + the acceptance-rule tagging per query). (f) Confirm the version-history SQL surface (read NodeVersionQuery call sites). | Everything recorded in this file; DSL gap list + wire schema v1 drafted; corpus harness runs in ES mode and records a baseline. | ~400k |
| **A** | **Foundations in NoDB**: OpenSearch container wired into the stack (env, health); index lifecycle (create per repo with mappings/analyzers from the template, alias `<tenant>-<repo>` → `+g1`, delete, exists); **indexer**: outbox consumer + checkpoint table + `awaitRefresh(seq)` (the §3.3 contract); **fix per-op outbox emission** (risk 10a — every write path emits); index-document RPC (XP-shipped docs per decision 3) applied by the indexer. | nodb build green. Tests: index lifecycle round-trip; write→outbox→indexed→awaitRefresh→visible; checkpoint monotonic; per-op writes emit outbox rows (10a closed, test); dual-tenant index isolation (`<tenant>-` prefix); rebuild-from-docs (drop index, replay) works. | ~700k |
| **B** | **XP-side wire + NoQL→DSL**: the `QueryExpr → DSL` renderer (per Gate 0's mapping); `SearchRequest → wire envelope` serialization; nodb-mode `NodeSearchIndex` impl routes search/index/delete/refresh to NoDB RPCs. Hybrid ES stays default-wired; nodb mode now has a real search path end to end (even before full translation parity — Gate B proves plumbing with the core query families from Gate C's first batch or simple term queries). | Full XP build green; default byte-identical (spot itests); renderer unit-tested against the Gate 0 construct list (every NoQL construct → expected DSL JSON); one end-to-end nodb-mode query itest green (parse → wire → translate → OpenSearch → results). | ~600k |
| **C** | **Translation batch 1 — structured queries** (in NoDB): term/in/range/like/exists/boolean/not/matchAll/ids + logical field-name resolution (logical path → typed sub-field per value type) + ACL filter injection + admin-key + field sorts + paging + `search_after`/PIT for deep scroll. **Version-history SQL** (decision 4) lands here too. | Corpus diff green for batch-1 families (exact-match rule); the structured-query + version-history itest classes green in nodb mode (report which). | ~800k |
| **D** | **Translation batch 2 — text + geo**: fulltext/ngram/stemmed/simple-query-string with weighted fields, language analyzers + ICU collation, pathMatch, geo-distance sort; **suggesters + highlighting**. | Corpus diff green under the acceptance rule (set-match for scored queries, exact for deterministic); text-family itest classes green in nodb mode incl. the 4 icuSort tests (now on a modern engine — if they PASS in nodb mode where they failed on ES 2.4, document it; they are env-sensitive). | ~800k |
| **E** | **Translation batch 3 — aggregations**: terms/stats/min/max/value-count/numeric-range/date-range/histogram/date-histogram/geo-distance + sub-aggregations. | Bucket-level corpus diff green; aggregation itest classes green in nodb mode. | ~600k |
| **F** | **The switch + full-suite gate**: nodb mode drops embedded ES entirely (fixture + runtime — no ES node starts in nodb mode); ALL itest classes run in nodb mode. | **Full itest-core + itest-core-content green in nodb mode** (the phase gate; icuSort disposition per Gate D). Default mode: full suites still byte-identical on embedded ES. Corpus fully green under the acceptance rules. | ~700k |
| **G** | **Boot smoke + docs + push**: full stack (PG + MinIO + OpenSearch) boot with `backend=nodb`; live editing flow (create → query-your-write via refresh → publish → aggregate); restart persistence incl. index intact + rebuild drill (drop index, reindex from outbox/docs, verify identical); RUNNING.md updated (OpenSearch container + env); DESIGN.md §9/§10 updates; push `nodb-phase4-opensearch`. | Smoke green; rebuild drill proves the index is disposable; growth counts recorded; docs current. | ~400k |

Total ≈ **5.0M** output tokens (within the long-standing 5–8M window; G's rebuild drill
and E's bucket diffs are where surprises would push it up).

## Key risks carried into the gates

- **Scoring parity** (decision 5) — the acceptance rule exists so Gate D fails loudly on
  set differences and documents order-only deltas, instead of drowning in relevance noise.
- **Analyzer fidelity** — ES 2.4 analyzer configs (edge-ngram, language stemmers, ICU
  collation keys) must be reproduced in OpenSearch mappings; index-time differences are
  invisible until query time. Gate A ports the settings; Gate D proves them.
- **Refresh semantics under concurrency** — `awaitRefresh` must hold under interleaved
  writers (the ~15 command-class contract). Gate A tests it directly, Gate F exercises
  it via the full suite.
- **The 68-class cliff** — Gate F is intentionally a big-bang verification, but Gates
  C–E each run their family's itest classes early, so F should be confirmation, not
  discovery.
- **Deferral recorded**: server-side doc derivation from payloads (decision 3) — the
  rebuildable-from-Postgres-alone property arrives with that swap, not this phase
  (Gate G's rebuild drill replays shipped docs, which is index-disposable but not yet
  XP-independent).

## Execution guidance

Same regime as Phases 0–3: agents build, orchestrator verifies (forced reruns, corpus
diffs re-run independently, diff review, real boot) and commits per gate; never commit
red; monitors against agent stalls; machine hygiene before long runs (`--stop` idle
daemons — memory pressure is a known flake source on this box). One gate per session is
a sane cadence; C/D/E are independently pauseable. If DSL completeness (Gate 0a) finds
major NoQL constructs with no DSL form, STOP and review the wire-schema additions with
the team before Gate B locks the format.

## Definition of done

nodb mode runs zero embedded ES; full itest suites green in nodb mode; corpus green
under the documented acceptance rules; refresh contract proven; index rebuild drill
green; default mode byte-identical; RUNNING.md + DESIGN.md updated; branch pushed.
