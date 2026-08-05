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
   wire = DSL JSON + envelope (formatVersion, SOURCES, store-type, paging,
   filter-mode, sort/aggregations/filters/highlight/suggest — all of which have JSON
   forms). **Sources are a list of (repo, branch, principals) triples** — XP's
   multi-repo queries (multiRepoConnect / cross-project) carry PER-SOURCE principal
   sets (today's MultiRepoSearchSource/RepoBranchAclMap); the single-repo case is the
   one-element form. NoDB translates a multi-source query to ONE multi-index
   OpenSearch request with the ACL filter applied per index
   ((_index=X AND acl_X) OR ...), mirroring today's MultiRepoSearchSourceAdaptor;
   response hits carry repo/branch attribution (as FindNodesByMultiRepoQueryResult
   does). All sources resolve under the token's tenant prefix — multi-repo never
   means multi-tenant. NoDB translates ONE language; today's two parallel ES builder
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
   **Landed in Phase 3.5** (`BUILD-PHASE-3.5.md`, branch `nodb-phase35-version-sql`):
   the whole surface above ships there, including the binary-GC `findVersions` unblock
   (GC itself stays Phase 5). Gate C below shrinks accordingly — version-history SQL is
   no longer part of Phase 4's scope.
5. **Scoring-parity acceptance rule** (decided in Gate 0, enforced from Gate C):
   deterministic sorts (field/path/ts/manual) must match ES **exactly** — order and
   counts. Score-ordered fulltext results must match as **sets** (same hits, same
   totals); ordering deltas from ES-2.4-vs-OpenSearch relevance differences are
   documented per corpus query, not silently accepted and not chased to impossibility.

## Branch

`nodb-phase4-opensearch` off **`nodb-phase35-version-sql`** (Phase 3.5 landed after this
work order was written; it carries the Phase 3 payload work plus the version/diff/commit
SQL surface).

## Amendments after Phase 3.5 (2026-08-05)

- **P2 is DONE** (delivered in 3.5). Two prerequisites remain: P1, P3.
- **Decision 4 landed in 3.5** — version-history SQL is out of Gate C (~800k → ~650k).
  New total ≈ **4.97M**.
- **P1 is more urgent than when written**: Phase 3.5 hand-edited all three vendored proto
  copies twice; they stayed identical only because each change was explicitly checked.
- **P3 has new context**: 3.5 added migration `002` and proved the manifest-driven runner
  applies ordered files, so P3 reduces to checksums + the baseline/upgrade rule.
- **Delete-cascade is now the ONLY remaining ES-storage-index consumer.**
  `DeleteNodeCommand` lists children via a `NodeBranchQuery` against the index nodb never
  creates — it is why 5 itest classes stay excluded after 3.5. OpenSearch makes it work
  automatically; **Gate F must verify it explicitly**, not assume it.
- **Risk #8 is confirmed reachable, not theoretical** (`PushNodesCommandTest`'s intra-push
  rename swap hits `unique(repo_key, branch, node_path)`). Needs `DEFERRABLE` unique or
  equivalent. **Decide at Gate 0** — otherwise it resurfaces as a Gate F full-suite
  surprise.
- **`itest-core-content` has ZERO nodb harness plumbing** (fixture hard-codes the ES store
  across ~6 service constructions). 3.5 deferred it to a live smoke, but Gate F's "ALL
  itest classes in nodb mode" cannot dodge it. Wire it in **Gate B** (before the
  translation batches need content-level proof): **+~250k**, previously unestimated.
- **Gate 0 item (f)** (confirm the version-history SQL surface) is **dropped** — done by
  Phase 3.5's Gate 0 inventory.
- **Gate 0 item (d) addition**: pin the OpenSearch version to what the target managed
  service offers and match its bundled plugin inventory (managed OpenSearch ships ICU
  preinstalled; dev/self-hosted needs a thin derived image). ICU/CLDR version differences
  change collation keys — sort order must not become environment-dependent.

## Prerequisite gates

Complete these compact storage-contract gates before Gate 0 turns the query wire and
schema into larger moving targets. They deliberately do **not** pull Phase 5 operations
or Phase 6 production hardening into Phase 4.

| Gate | Deliverable | Verification (all must hold) | Est. |
|---|---|---|---|
| **P1** | **One protobuf source of truth.** Replace the independently maintained server/client copies of `nodb.proto` with one canonical source and generated artifacts consumed by both sides. Record the compatibility policy for this internal API (versioned rolling compatibility or explicit lockstep deployment). | Server and XP client generate/compile from the same schema; no manually synchronized proto copy remains; a build check fails on generated/schema drift; the selected compatibility policy is documented. | ~150k |
| **P2** | **Repository-scoped version identity.** **DONE — delivered in Phase 3.5** (`BUILD-PHASE-3.5.md` gate P2, branch `nodb-phase35-version-sql`); kept for the record. Resolve the mismatch between the SPI's repo-scoped version operations and PostgreSQL's `(repo_key, version_id)` key: version get/delete and the Phase-4 SQL history surface MUST resolve the repository and predicate on `repo_key`; do not rely on an unenforced tenant-global version-id assumption. | A test stores the same `version_id` in two repos in one tenant, then proves get/delete/history affect only the selected repo; server requests carry/resolve repo identity; grep/review finds no unscoped runtime version lookup/delete. Existing single-repo behavior remains green. | ~120k |
| **P3** | **Forward-only migration discipline.** Freeze applied migration contents before Phase 4 adds OpenSearch/checkpoint/history schema. Add immutable ordered migrations with recorded checksums (or equivalent tamper detection) and define the pre-GA baseline/upgrade rule for Phase-3 tenants. | Fresh tenant provisioning and upgrade from a Phase-3 schema both pass; changing an already-applied migration is rejected loudly; new Phase-4 schema lands in a new migration rather than an edit to `001_init.sql`; migration-order/checksum tests are green. | ~120k |

## GATE 0 COMPLETE (2026-08-05) — decisions awaiting a ruling

All five items delivered (a: DSL completeness · b: envelope · c: translator surface ·
d: OpenSearch stack · e: corpus + ES baseline). Item (f) was dropped (done by Phase 3.5).
**No production Java was written; the gate's artifacts are this file's records plus the
corpus harness, the ICU image, and the index-template draft.**

**Two blockers found before they could cost a gate** (both reproduced, both with verified
fixes — see 0(d)): XP's index documents cannot be indexed by OpenSearch at all (bare STRING
field vs its dotted sub-fields), and all 91 dynamic templates silently never fire
(`match` vs `path_match`) — the latter fails with zero hits and no error.

**Decisions needed before Gate A/B write code:**

| # | Decision | Recommendation from the evidence |
|---|---|---|
| D1 | **`STRING` postfix** (blocker 1) — change the index-document shape | give STRING a real postfix (`_string`); verified end-to-end. Decide FIRST: it changes the Gate A index RPC and the Gate B serializer |
| D2 | **BRANCH store type** — OpenSearch query or a 4th SQL surface | **SQL surface** (`listChildEntries`/`listBranchEntries`, 3 call sites, ~150–250k). Retires the last ES-storage-index consumer instead of recreating it |
| D3 | **G-1 geoPoint** | **fail fast, no wire support** — it already errors in ES 2.4 |
| D4 | **G-2 `IN` typing / G-4 `range` case** | **fix both** — the DSL path already behaves correctly where NoQL does not; pin the new behavior with the existing corpus rows |
| D5 | **G-3 `analyzer` arg** | add the optional field (cheap; zero call sites today) |
| D6 | **G-6 store-mode field resolution** | derive `fieldResolution` from the envelope's store-type; moot for BRANCH if D2 = SQL |
| D7 | **Split the scoring-parity rule** (decision 5) | non-ICU sorts EXACT (order-by values are pre-encoded lexi-sortable ASCII); `_orderby_<loc>` ICU = documented per-locale deltas against a pinned ICU version |
| D8 | **`_orderby*` sorting mechanism** | `icu_collation_keyword` + `index:false, doc_values:true` (no fielddata) |
| D9 | **Response-side tagging** | NoDB tags each aggregation's kind + bucket-key type; **+150–200k to Gate E** |
| D10 | **Doc `_id` composite** (`<nodeId>_<branch>`) | required once per-branch mapping types are gone; Gate A deliverable text |

**Revised total: ≈5.3M** (4.97M + D2 ~200k + D9 ~175k), still inside the 5–8M window.
Prerequisites remaining: P1 (proto source of truth — now also fixes the stale "serialized
AST" comment), P3 (migration checksums).

## Gate 0(a) results — DSL completeness (2026-08-05)

**STOP CONDITION NOT TRIGGERED.** Every NoQL construct renders as DSL: all 10 comparison
operators, AND/OR/bare-NOT/parens, all 5 constraint functions (`fulltext`, `ngram`,
`stemmed`, `range`, `pathMatch`), all 3 order-by forms (field+COLLATE, `geoDistance`,
compound), 6 of 7 value types. **Zero new query types needed** — the public DSL stays a
strict subset of the wire schema. Grammar is hand-written jparsec
(`core-api/.../query/parser/QueryGrammar.java`), no JavaCC; there is **no JSON schema**
for the DSL anywhere — the contract is the builders + `lib/core/index.d.ts`.

**Wire-schema superset: 3 optional fields + 1 envelope flag.**

| ID | Gap | Addition | Note |
|---|---|---|---|
| G-1 | geoPoint-typed values in term/`!=`/range | `type: "geoPoint"` | unit-tested today; mechanical |
| G-3 | `fulltext`/`ngram` 4th arg = custom analyzer | optional `analyzer` | tested but ZERO call sites in repo |
| G-2 | `IN` pins field to STRING regardless of value type | `normalize: false` **or** a ruling | dated/geo `IN` matches NOTHING today; naive rendering silently starts matching — **latent bug, needs a ruling** |
| G-4 | `range()` string bounds raw; DSL normalizes | `normalize: false` **or** a ruling | invisible in current itests (all bounds lowercase) — pin whichever behavior with a corpus query |
| G-5 | `range()` both bounds empty | none — renderer rewrites to `exists` | edge |
| **G-6** | **store-mode field resolution has NO DSL representation** — every DSL builder hardcodes `SearchQueryFieldNameResolver`, so an unpostfixed (store) field name cannot be expressed | envelope `fieldResolution: search\|store`, **preferably derived from the envelope's store-type** rather than an independent flag | **The one that matters.** This is the delete-cascade path (`DeleteNodeCommand`'s `NodeBranchQuery`) the Phase-3.5 amendment calls the last ES-storage-index consumer. **Decide at Gate 0/B, not Gate F.** |

**Team ruling needed before Gate B locks the format:** (1) G-6 envelope flag vs XP-side
rewrite of store-mode queries; (2) G-2/G-4 — preserve the two latent-bug behaviors
bit-for-bit or fix them and document the deltas in the corpus (either is defensible;
silently changing them is not); (3) G-3 add `analyzer` now (cheap) vs declare the 4-arg
form unsupported and fail fast.

**Canonicalization rules that are part of the format** (not style): query always present
(empty → `matchAll`); sort always an array; `direction` always explicit; **logical nesting
preserved pairwise left-associative, NEVER flattened** (flat `must:[a,b,c]` is
set-equivalent but not score-equivalent — and decision 5 forbids order/count drift on
deterministic sorts); numerics as JSON numbers with Double semantics, stringified when
NoQL semantics put them on a string field (`IN`, `LIKE`); field names emitted post-
`IndexPath` (already lowercased/trimmed) WITHOUT subfield postfixes — postfix resolution
is the server's job; no `queryName` carried (nothing in XP reads `matched_queries`).

**Three typing rules coexist and the renderer must know which applies:** term/compare
types from the `Value` (dated→`._datetime`, numeric→`._number`, geo→`._geopoint`);
`IN`/`LIKE` always the base string field while still type-converting the value (source of
G-2); `range()` infers from bounds INCLUDING "a plain string that ISO-offset-parses counts
as an instant" — the renderer must reproduce that sniffing, not re-sniff server-side.

**Free Gate B oracle (adopt this).** Golden ES-JSON fixtures already exist for BOTH
families (`core-repo/src/test/resources/.../factory/query/*.json` 17 files,
`factory/function/*.json` 11 files, `factory/dsl/*/`). So the renderer's test is an
equivalence round-trip inside XP core: `QueryParser.parse(noql)` → render DSL →
`DslExpr.from` → `ConstraintExpressionBuilder.build` → compare ES JSON against the
existing NoQL golden file. Proves every mapping row at byte level, needs no OpenSearch,
and catches the nesting/typing/normalization risks before Gate C corpus diffs make them
expensive. The rows that cannot pass are exactly G-1…G-4 — a self-checking gap list.

**Also settled:** NoQL cannot nest constraint functions at all (`parseFunction(false)`,
args are `ValueExpr` only) — "nested functions" is a non-issue. Bare `NOT` over any
expression is `bool.mustNot` on both sides — not a gap. Special fields (`_path`, `_name`,
`_id`, `_ts`) are ordinary compares with no special casing. `ORDER BY score()` parses but
throws at build time — dead construct. `ids` exists only as a FILTER, never a query type
(Gate C's "ids" item is a filter).

## Gate 0(b)+(c) results — envelope + translator surface (2026-08-05)

### CORRECTION to this file's own Phase-3.5 amendment

**The `BRANCH` store type does NOT "work automatically" once OpenSearch is live.** nodb mode
creates no `storage-<repo>` index and the authoritative rows are `branch_entry` in Postgres;
answering it from OpenSearch would also make delete-cascade depend on index freshness
(`DeleteNodeCommand:86` already forces `refresh(STORAGE)` first). It is a *storage* question
asked with a query DSL, in exactly **three call sites**: `DeleteNodeCommand:88-98` (children
by path prefix + branch, ORDER BY path DESC, GET_ALL), `RepositoryServiceImpl:297-306`
(deleteBranch), `ReindexExecutor:79-85` (reindex).
**Decision needed at Gate 0: make it a fourth SQL surface** (`listChildEntries(repo, branch,
pathPrefix)` + `listBranchEntries(repo, branch)`) — a direct sibling of 3.5's version
surface, ~150–250k — rather than re-creating it as an OpenSearch query with exact-match
parity. This retires the last ES-storage-index consumer for real.

### Envelope (b)

`SearchRequest` is only 4 fields (`SPI/SearchRequest.java:9-16`); everything else rides on
`Query`/`AbstractQuery`. **`searchPreference` is structurally always null on the search
path** (only the ES `NodeStore`'s own branch lookups set it) → not on the wire.
Store types: VERSION/COMMIT/diff are SQL since 3.5; BRANCH per the correction above. So
**the Phase-4 search wire carries search-source queries only.**

Envelope v1: `format_version`, `sources[{repo_id, branch (CASE-PRESERVED), principals[]}]`,
`query` (canonical DSL), `query_filters[]` (inside `bool.filter` with the ACL filter),
`post_filters[]` (separate `setPostFilter` slot — must not be merged), `sort[]`,
`aggregations`, `suggest`, `highlight`, `from`, `size` (-1 = ALL), `batch_size`, `explain`,
`search_optimizer`, `return_fields[]`. Deliberately absent: `search_preference`, any
filter-mode/ACL-bypass (no such concept exists — admin is an asserted principal),
`store_type`, dead `filter.cache`. **Reserve `principals_digest` now** so the later
register-once optimization is not a format break (typical principal set ~100 bytes, worst
case ~100+ keys/2-4KB; highly stable per request — defer the digest, it would add
per-connection server state that collides with Phase 6).

Branch names are matched with an `IdFilter` on `_type` **to preserve case**
(`MultiRepoSearchSourceAdaptor:87-91`) — `ValueFilter` would lowercase them.

**ACL — two behaviours to preserve/port precisely:** (1) today `AclFilterBuilderFactory:15-18`
**returns null and applies NO filter at all** when the principal set contains
`role:system.admin` — and 22 non-test sites construct admin contexts, so this fires
constantly. DESIGN §7.2 replaces it with the indexer injecting `role:system.admin` into every
doc's read-keys, i.e. the filter is never absent: a genuine behaviour change. Any doc missing
the injected key (indexed before a projection bump, empty ACL) silently vanishes from admin
queries → **the read-keys projection must be versioned in Gate A and Gate F needs an "admin
sees everything ES-admin saw" corpus query.** (2) Empty principals must stay fail-closed
(→ `user:system:anonymous`), never match-all.

**Hit attribution must be explicit on the wire.** `FindNodesByMultiRepoQueryResultFactory:41-46`
derives repo by string-slicing `_index` and branch from `_type`; under generational names
(`<tenant>-<repo>+g<N>`) that breaks and violates DESIGN §5's "nothing parses a name back".
Response hits carry `repo_id`/`branch` as fields.

### Translator surface (c) — Gate A/C/D/E task list

140 java files under `elasticsearch/`: `factory/query` 12, `factory/dsl` 15,
`factory/function` 26, `aggregation` 11 + `aggregation/query` 14, `suggistion` 5,
`highlight` 1, `query/source` 8, `result` 5. The DSL family is the better translator base
(already JSON-shaped) but is **not a superset**: it lacks NEQ, NOT_LIKE, NOT_IN, standalone
`not`, the `range()` function form, and the whole filter vocabulary — matches item (a)'s gap
list. Conversely only the DSL family has a `filter` clause.

**Field-name resolution is a SIX-rule system — S code, L risk, and it has TWO divergent
implementations.** Rules: (1) `IndexPath` lowercases+trims every path; (2) postfix per value
type (`""`, `_datetime`, `_number`, `_ngram`, `_analyzed`, `_orderby`, `_geopoint`, `_path`,
`_orderby_<loc>`, `_stemmed_<lang>`); (3) value→sub-field order: dated→NUMBER→geo→raw;
(4) value coercion must match the sub-field (numeric ALWAYS `Double`; strings
`trim().toLowerCase()`); (5) order-by: `_score`/`_id`/`_doc` pass through unmodified, else
`._orderby[_loc]` with `no→nb` normalization and DUCET fallback; (6) the store resolver
ignores value type entirely (no Phase-4 role after the BRANCH correction).
⚠ **Rule 3 diverges between families**: the expression tree types from the `Value`, while the
DSL only recognizes `Number`→`_number` and requires an explicit `type:"dateTime"` to reach
`._datetime`. **The renderer MUST emit the explicit `type` for every dated AST value or
results change.** `in`/`like`/`ExistsFilter` force STRING regardless of value type.
Unit-test this matrix exhaustively BEFORE Gate C, independent of OpenSearch.

**Good parity news:** order-by values are pre-encoded lexi-sortable ASCII
(`OrderByValueResolver`/`LexiSortable` — numbers to 17-char hex, dates to a fixed UTC
pattern, strings lowercased and truncated at 1024), so all non-ICU field sorts are string
sorts and port exactly.

**Mapping/analyzer port (Gate A), 13 breaking constructs**, from 8 JSON resources
(`search-settings.json` 600 lines: 44 ICU collation filters + 44 `icu_sort_*` analyzers,
`search-mapping.json`: 91 dynamic templates, zero explicit properties):
1. `icu_collation` filter + `icu_sort_*` analyzers sorted on → `icu_collation_keyword` FIELD
   TYPE (OpenSearch cannot sort `text` without fielddata); `analysis-icu` not bundled →
   repo creation fails without the plugin.
2. `_default_` root mapping type — removed in ES7; kills the "new branch inherits templates"
   trick.
3. **One mapping type per branch (`_type` = branch name)** → branch becomes a `keyword`
   field. **Knock-on the work order missed: the search doc `_id` is today the bare nodeId,
   unique only per type** — with one type per index it must become composite
   (`<nodeId>_<branch>`), which changes the Gate A index RPC, `delete(repo, branch, ids)`,
   and `IndexDocumentRecord`.
4. `"type":"string"` + `"index":"analyzed"|"not_analyzed"` (88+) → `text`/`keyword` + boolean.
5. `ignore_above` on analyzed fields (47×) → `keyword` + a **`lowercase` normalizer, which
   does not exist anywhere today** and must be authored.
6. `edgeNGram`+`side:front` → `edge_ngram`/`min_gram`/`max_gram`.
7. `standard` token filter (6 chains) → delete.
8. `_all` + `include_in_all` (91×) → delete (XP has its own `_alltext`; `all_field_analyzer`
   becomes dead).
9. `_parent:{type:version}` in branch-mapping → moot if the storage index dies; no
   `has_child`/`has_parent` replacement exists (this is why diff HAD to move to SQL).
10. `geo_point`/`date`/`double` with `index:"not_analyzed"` → drop `index`; add explicit date
    `format` (OpenSearch parsing is stricter).
11. bare `{"enabled":false}` → add `"type":"object"`.
12. **missing `index.mapping.total_fields.limit`** — 2.4 had none, OpenSearch defaults to
    **1000**, and a fully dynamic mapping with a `*` catch-all WILL hit it on any real
    content repo (would look like a random Gate F failure). Also set `depth.limit`,
    `nested_fields.limit`, `max_result_window` (OpenSearch caps `from+size` at 10 000).
13. Test fixtures encode 2.4 shapes (`search-test-settings.json` `store.type:memory`,
    `default_index`; `IcuSortConfigConsistencyTest` asserts `_default_` + exactly 44 ICU
    filters) — update with the mappings or Gate A goes green against fixtures that no longer
    describe reality.
Verified ABSENT (no work): `_timestamp`, `_ttl`, `_size`, `_uid`, `precision_step`,
`fielddata`, `doc_values`, `"index":"no"`, multi_field, `omit_norms`, `copy_to`, geo
`lat_lon`/`geohash*`.

**Other removals to re-express:** `IndicesQueryBuilder` (gone in ES6+) is exactly the
mechanism multi-repo ACL fan-out uses → `bool.should(bool.must(term _index, <inner>))`.
`in` is N `should` term clauses, NOT a `terms` query — preserve the fan-out or scoring
drifts. Aggregation ctors all change (`BucketOrder`, `DoubleBounds`/`LongBounds`), and
**`AbstractAggregationBuilder` vs `AggregationBuilder` — the discriminator today's sub-agg
guard uses — no longer exists**, so sub-agg support becomes an explicit per-type rule.
`DateHistogramInterval`/`.interval()` removed → a documented string→`calendar_interval` vs
`fixed_interval` rule is needed (XP's interval is an untyped String).
Suggester ctor inverts (field in ctor, name at `addSuggestion`) and **`"jarowinkler"` was
renamed `jaro_winkler`** — an XP-API-visible wire value needing a compat mapping.
Highlighting: XP expands each property to THREE fields using `name_analyzed`/`name_ngram`
(**underscore, no dot — verify against the `*._analyzed` templates or highlighting silently
returns nothing**), forces `type:plain` unconditionally, and OpenSearch flips
`require_field_match` default false→true while XP only sends it when non-null.

**Scroll → `search_after`+PIT:** triggered only by `size==-1`, page = `batchSize`,
keep-alive 60s, adds `sort:_doc` only when the caller gave no sorts; aggs/suggestions come
from the FIRST response while **`totalHits`/`maxScore` come from the final EMPTY page — so
`maxScore` is very likely `NaN` for every GET_ALL query today**; `explain` and SearchType are
never set on the scroll path. The port needs a total order (append `_shard_doc`),
`track_total_hits:true`, and must reproduce the NaN/explain quirks or record them as
deliberate deltas.

### Items the work order does not account for — fold into gates

1. **The RESPONSE side is absent from Gates C–E.** `AggregationsFactory:43-86` discriminates
   results by `instanceof` on ES *internal* classes and reads joda bucket keys; date-vs-numeric
   histogram is distinguished ONLY by `instanceof InternalHistogram` ordering — a JSON wire has
   no such type identity. NoDB's response must **explicitly tag each aggregation's kind and
   bucket-key type**, and `AggregationsFactory`/`SuggestionsFactory` must be re-implemented
   against tags. **Add ~150–200k to Gate E** or it surfaces mid-gate as "aggregations come
   back but throw".
2. **SPLIT the scoring-parity rule (decision 5) at Gate 0, not Gate D.** Language-aware sorts
   resolve to `._orderby_<loc>` fields holding ICU collation keys, and ICU/CLDR versions change
   those keys — two orderings can differ while both are correct. Proposed: `_orderby`/numeric/
   date/path/manual = **exact**; `_orderby_<loc>` (ICU) = **documented per-locale deltas with a
   pinned ICU version**, treated like today's 4 icuSort tests.
3. **Doc `_id` identity change** (item 3 above) belongs in Gate A's deliverable text.
4. **Nondeterministic request JSON:** aggregations, suggestions and `ESSource` index/type sets
   are `HashSet`s — once serialized, element order varies across JVM runs, breaking wire-level
   golden files and any request caching. **Gate B's serializer must use ordered collections.**
5. **Proto drift:** `nodb/proto/nodb.proto:73-75` still documents `Search` as carrying "the
   serialized XP query AST", which decision 1 supersedes — fix in P1 so the single source of
   truth does not describe the abandoned design.

## Gate 0(d) results — OpenSearch stack (2026-08-05)

**Version pinned: `opensearchproject/opensearch:3.7.0`.** Newest OSS is 3.8.0, but AWS
OpenSearch Service runs **3.7** (announced all-regions 2026-07-30) and adopts only every
other minor (3.1/3.3/3.5/3.7). Next legitimate bump is 3.9. **The compose file's floating
`:3` was already broken** — it resolves today to 3.8.0, which the managed target does not
support: precisely the drift the amendment warned about. Fixed.

**Plugin parity confirmed:** AWS prepackages ICU Analysis on every domain (min version 1.0);
vanilla self-hosted lacks it. **`analysis-icu` is necessary and sufficient** — justified from
the resources, not assumed: XP's only ES plugin today is `analysis-icu:2.4.6`; the 44
`icu_collation` filters need it; the other 2 filters are core; the 40 language analyzers are
core Lucene, and **ja/ko/zh map onto core `cjk`, so kuromoji/nori/smartcn are NOT required**.
No phonetic/stempel/ukrainian reference exists. Image `enonic/nodb-opensearch:3.7.0` =
1.29 GB (ICU layer +14.8 MB), bundles icu4j 77.1.

**ICU collation proven end-to-end** (real alias, XP-shaped docs): `icu_collation_keyword`
language=no → `a z æ ø å`; plain keyword → `a z å æ ø`. Also green through the alias:
fulltext, edge-ngram prefix, `path_hierarchy`, numeric/date range, terms agg.

**Resources (measured):** idle RSS **926 MiB** at 512m heap (non-heap 232 MB because the
image bundles 27 plugins), 943 MiB after smoke; 256m heap boots at 647 MiB. Dev config:
512m heap, `--memory 2g`. Container total with PG+MinIO ≈ 1.4 GB. Note XP runs with **no
`-Xmx` at all** (server.sh sets only `AlwaysPreTouch`) so it takes ¼ of host RAM *and* still
carries embedded ES until Gate F — which is why OpenSearch is opt-in, and why the footprint
improves at Gate F.

**Delivered:** `nodb/docker/opensearch/Dockerfile` (2 lines + rationale),
`nodb/docker/opensearch/index-template.json` (template draft + ES-2.4-ism inventory),
`dev-stack.sh` `WITH_OPENSEARCH=1` opt-in (default off, verified to issue ZERO OpenSearch
commands when unset — tested by sourcing the real script with docker/curl stubbed),
compose pinned to the derived image.

### 🚨 BLOCKER 1 — the bare STRING field cannot coexist with its sub-fields

`StaticIndexValueType.STRING` has postfix `""`, and `IndexItem.getPath()` appends a suffix
only when non-empty, so `IndexItemFactory.create()` **always** emits a bare field alongside
its dotted sub-fields. OpenSearch expands dots into objects, so the same name must be a leaf
AND an object:
```
{"data.x":"Hello","data.x._analyzed":"Hello"}  →  can't merge a non object mapping
{"_path":"/a/b","_path._path":"/a/b"}          →  same
{"_allText":"t","_allText._analyzed":"t"}      →  same
```
**This fires on the FIRST DOCUMENT of every repo.** Verified fix: give `STRING` a real
postfix (`_string`) — the whole end-to-end validation above ran on that shape. Alternatives
(change the `.` separator; multi-fields) written up in the template file; multi-fields
rejected because a derived multi-field cannot carry an independently computed `_orderby`,
contradicting decision 3. **This changes the index-document RPC shape → decide BEFORE Gate A
codes the indexer or Gate B codes the serializer.**
Also: `_id` and `_source` are rejected outright as document fields (reserved metadata);
ES 2.4 tolerated both.

### 🚨 BLOCKER 2 — all 91 dynamic templates silently never fire

Every template uses `"match": "*._analyzed"`, which after dot-expansion matches the *leaf*
name `_analyzed` and therefore never matches. **Index create succeeds, indexing succeeds,
and queries return 0 hits with no error** — the worst possible failure mode. Verified fix:
`path_match` instead of `match`. `index.mapper.allow_dots_in_name` (which XP passes via
`-D`) no longer exists.

### Hard rejections verified (Gate A port list, each reproduced)

`"type":"string"`; `"index":"analyzed"/"not_analyzed"`; `_all`; `include_in_all`; the
`_default_` wrapper; `edgeNGram` → `edge_ngram` (**`"side":"front"` is silently accepted but
inert** — a behavior change, not a syntax error); **the `standard` token filter is REMOVED and
appears in 6 XP analyzers** (behavior change, not syntax); `ignore_above` illegal on `text`.

### Decisions Gate A owes

1. **Sorting the 45 `_orderby*` fields** — text can't be sorted. Option A `"fielddata": true`
   (works; heap-resident collation keys × 44 locales on a shared cell). **Option B
   (recommended, drafted, verified): `icu_collation_keyword` + `index:false, doc_values:true`**
   — no fielddata, `ignore_above` legal again.
2. **The bare/untyped string field has NO dynamic template at all** and fell through to ES
   2.4's dynamic default (index-time `standard`, search-time `default_search` — a mismatched
   pair). The draft maps it to `keyword`: a behavior change on the most-used field variant in
   the system. **Settle with the Gate 0(e) corpus in ES mode, not by reasoning.**
3. `refresh_interval`: draft keeps `1s`; **tests should run `-1` so a missing `awaitRefresh`
   fails deterministically**. New since 2.4: `index.search.idle.after` (30s) stops background
   refresh on idle indices and makes the next search block.
4. Limits: `total_fields.limit` 1000 → 10000, `depth.limit` 20 → 50 — **object mappers now
   count** (3 properties → 10 leaves but 15 mappers). Measure on the real corpus.
5. **ICU/CLDR is part of the sort contract**, not just a test concern: `icu_collation_keyword`
   bakes keys into doc values at index time, so an ICU bump must go through a `+g(N+1)`
   rebuild, never in place.
6. `all_field_analyzer` becomes dead once `_all` is gone — confirm nothing queries `_all`,
   then delete.
7. Bundled plugins create system indices (`.plugins-ml-config`, `top_queries-*`) in NoDB's
   cluster — index enumeration and Gate G's rebuild drill must ignore them.
8. `NODB_OPENSEARCH_URL` is deliberately not yet passed by `dev-stack.sh`; Gate A owns that
   wiring.

## Gate 0(e) results — golden-query corpus + ES baseline (2026-08-05)

**Delivered** (11 classes, ~2300 lines, `modules/itest/itest-core/src/test/java/com/enonic/
xp/core/nodb/corpus/`): `GoldenCorpus` (data-driven corpus definition via `CorpusDsl`),
`CorpusFixture` (deterministic seed data), `CorpusRecorder`, `CorpusArtifact`,
`CorpusComparator`, `NodbGoldenCorpusTest`, plus `GoldenQuery`/`Acceptance`/`QueryOutcome`/
`SourceKind`. Gradle: `./gradlew :itest:itest-core:recordNodbCorpus` (writes
`src/test/resources/nodb/corpus/es-baseline.json`) and `:diffNodbCorpus` (compares;
`-Dxp.nodb.corpus.baseline=<path>` overrides).

**Baseline recorded in ES mode: 127 queries** — 100 EXACT, 21 SET, 6 ICU_DOCUMENTED;
117 non-empty; 202 KB, stable key order. **Verified by the orchestrator:** self-diff clean,
and a **negative control** (one EXACT entry's hit order reversed in a copy of the baseline)
correctly FAILS the diff — so the clean self-diff is not vacuous.
⚠ **Runtime 45 min** for a full diff run — too slow for per-commit CI. Gates C/D/E should run
family subsets (add a family filter property) and reserve the full run for Gate F/G.

### The gap cases are now MEASURED, and two rulings change

| Row | ES-mode result | Consequence |
|---|---|---|
| `GAP-G1-noql-term-geopoint` | **ERRORS**: `IndexException: Search request failed` (a `term` query against a `geo_point` field is invalid in ES — inherent, not a fixture artifact) | **G-1 needs NO wire support.** The construct is already broken in ES 2.4. Declare `type:"geoPoint"` unsupported and **fail fast** — that MATCHES today's behavior instead of inventing new capability. |
| `GAP-G1-dsl-term-geopoint`, `-dsl-in-geopoint` | `IllegalArgumentException: There is no [geoPoint] dsl expression` | confirms the DSL gap is real and explicit |
| `GAP-G2-in-dated` | **0 hits** | NoQL `IN` with dated values matches nothing — the latent bug, confirmed |
| `GAP-G2-dsl-in-dated` | **2 hits** | …while the DSL path **works**. The two families ALREADY disagree, so "preserving" NoQL's behavior preserves a bug the DSL path does not have → **argues for FIXING G-2**, not pinning it |
| `GAP-G2-in-number` | 0 hits | same shape as dated |
| `GAP-G2-in-geo` | 2 hits | DSL path works |
| `GAP-G4-range-raw-case` | **0 hits** | raw-case string bounds match nothing today (field is lowercased) → **argues for normalizing (fixing) G-4** |
| `GAP-G3-fulltext-4arg-analyzer` | 5 hits, works | keep `analyzer` on the wire (cheap) |
| `GAP-G5-range-both-empty` | 8 hits | behaves as `exists`, as predicted |

### The untyped-string field: Gate A decision 2 is settled BY MEASUREMENT

ES 2.4's effective behavior on the bare/untyped string field (10 `UNTYPED-*` rows):
exact multi-word value → **1 hit**; single token of a multi-word value → **0 hits** (NOT
tokenized); case-differing → **1 hit** (lowercased); punctuation/hyphenation preserved →
**1 hit**; ASCII folding → **0 hits** (NO folding); over-`ignore_above` value → **0 hits**
(not indexed); `LIKE` prefix → works; `_orderby` sibling → works.
⇒ **It behaves exactly as `keyword` + a `lowercase` normalizer, with NO asciifolding and
`ignore_above` preserved** — which confirms Gate 0(d)'s drafted mapping empirically rather
than by reasoning. Gate A implements that and the corpus proves it.

**Other recorded facts:** `maxScore` is `NaN` whenever results are sorted by field (not only
on `size=-1`) — the port must reproduce it or record a deliberate delta.
`ACL-03-empty-principals` → 0 hits confirms fail-closed. `PAGE-04-size0-count-only` empty by
design.

## Gates

| Gate | Deliverable | Verification (all must hold) | Est. |
|---|---|---|---|
| **0** | Spikes + inventory, no production code. (a) **DSL completeness**: enumerate every `QueryExpr` construct the NoQL parser emits (read the parser) and map each to a DSL form; list gaps → wire-schema superset additions. (b) **Envelope inventory**: everything on `SearchRequest` that isn't the query (principals/filter-mode, store-type incl. how branch/version/commit queries arrive, multi-repo sources, highlight/suggest params). (c) **Translator-surface inventory** from the ES code by family (queries, aggs, sorts, suggest, highlight, field-name resolution rules, analyzer/mapping settings incl. ICU). (d) **OpenSearch stack**: version choice, analysis-icu plugin, container, memory; index template for `<tenant>-<repo>+g<N>` + alias. (e) **Golden-query corpus harness design**: record ES-mode results (hits/order/totals/buckets) for a corpus once, diff harness for nodb mode; define the corpus (~50 queries spanning every family + the acceptance-rule tagging per query). (f) Confirm the version-history SQL surface (read NodeVersionQuery call sites). | Everything recorded in this file; DSL gap list + wire schema v1 drafted; corpus harness runs in ES mode and records a baseline. | ~400k |
| **A** | **Foundations in NoDB**: OpenSearch container wired into the stack (env, health); index lifecycle (create per repo with mappings/analyzers from the template, alias `<tenant>-<repo>` → `+g1`, delete, exists); **indexer**: outbox consumer + checkpoint table + `awaitRefresh(seq)` (the §3.3 contract); **fix per-op outbox emission** (risk 10a — every write path emits); index-document RPC (XP-shipped docs per decision 3) applied by the indexer. | nodb build green. Tests: index lifecycle round-trip; write→outbox→indexed→awaitRefresh→visible; checkpoint monotonic; per-op writes emit outbox rows (10a closed, test); dual-tenant index isolation (`<tenant>-` prefix); rebuild-from-docs (drop index, replay) works. | ~700k |
| **B** | **XP-side wire + NoQL→DSL**: the `QueryExpr → DSL` renderer (per Gate 0's mapping); `SearchRequest → wire envelope` serialization; nodb-mode `NodeSearchIndex` impl routes search/index/delete/refresh to NoDB RPCs. Hybrid ES stays default-wired; nodb mode now has a real search path end to end (even before full translation parity — Gate B proves plumbing with the core query families from Gate C's first batch or simple term queries). | Full XP build green; default byte-identical (spot itests); renderer unit-tested against the Gate 0 construct list (every NoQL construct → expected DSL JSON); one end-to-end nodb-mode query itest green (parse → wire → translate → OpenSearch → results). | ~600k |
| **C** | **Translation batch 1 — structured queries** (in NoDB): term/in/range/like/exists/boolean/not/matchAll/ids + logical field-name resolution (logical path → typed sub-field per value type) + ACL filter injection + admin-key + field sorts + paging + `search_after`/PIT for deep scroll + **multi-source queries** (multi-index fan-out with per-index ACL, hit repo/branch attribution — FindNodesByMultiRepoQueryCommandTest is this item's itest). ~~**Version-history SQL** (decision 4) lands here too.~~ Version-history SQL landed in Phase 3.5 (decision 4) — out of this gate. | Corpus diff green for batch-1 families (exact-match rule); the structured-query itest classes green in nodb mode (report which; the version-history classes are already green since Phase 3.5). | ~800k → ~650k (version-history SQL delivered in Phase 3.5) |
| **D** | **Translation batch 2 — text + geo**: fulltext/ngram/stemmed/simple-query-string with weighted fields, language analyzers + ICU collation, pathMatch, geo-distance sort; **suggesters + highlighting**. | Corpus diff green under the acceptance rule (set-match for scored queries, exact for deterministic); text-family itest classes green in nodb mode incl. the 4 icuSort tests (now on a modern engine — if they PASS in nodb mode where they failed on ES 2.4, document it; they are env-sensitive). | ~800k |
| **E** | **Translation batch 3 — aggregations**: terms/stats/min/max/value-count/numeric-range/date-range/histogram/date-histogram/geo-distance + sub-aggregations. | Bucket-level corpus diff green; aggregation itest classes green in nodb mode. | ~600k |
| **F** | **The switch + full-suite gate**: nodb mode drops embedded ES entirely (fixture + runtime — no ES node starts in nodb mode); ALL itest classes run in nodb mode. | **Full itest-core + itest-core-content green in nodb mode** (the phase gate; icuSort disposition per Gate D). Default mode: full suites still byte-identical on embedded ES. Corpus fully green under the acceptance rules. | ~700k |
| **G** | **Boot smoke + performance baseline + docs + push**: full stack (PG + MinIO + OpenSearch) boot with `backend=nodb`; live editing flow (create → query-your-write via refresh → publish → aggregate); restart persistence incl. index intact + rebuild drill (drop index, reindex from outbox/docs, verify identical); make the boot/restart smoke a repeatable CI job; record a cross-host concurrent baseline for the complete PostgreSQL + OpenSearch path; RUNNING.md updated (OpenSearch container + env); DESIGN.md §9/§10 updates; push `nodb-phase4-opensearch`. | CI smoke green; rebuild drill proves the index is disposable; cross-host p50/p95 throughput and latency recorded as a baseline (not yet an SLO); growth counts recorded; docs current. | ~450k |

Total ≈ **5.4M** output tokens (within the long-standing 5–8M window; G's rebuild drill
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

## Explicit later-phase gates

The prerequisite gates above close contract debt that Phase 4 would otherwise amplify.
The following remain intentionally deferred:

- Binary and payload garbage collection stays in **Phase 5**.
- Multi-XP cache coherence, RPC deadlines/cancellation, upload concurrency and temporary-
  disk quotas, real-AWS STS isolation, and shared-cell load/fairness stay in **Phase 6**.
  Multi-XP coherence and the AWS STS isolation test are mandatory before a horizontally
  split or production pilot, even though they do not block the Phase-4 search port.
- The independent payload parser/validator lands with the first server-derived consumer
  (index-document derivation or Phase-8 `_references`), protected by a golden payload
  corpus.
- Cross-host end-to-end performance baselines land in Phase-4 Gate G, once the complete
  PostgreSQL + OpenSearch request path exists.

## Execution guidance

Same regime as Phases 0–3: agents build, orchestrator verifies (forced reruns, corpus
diffs re-run independently, diff review, real boot) and commits per gate; never commit
red; monitors against agent stalls; machine hygiene before long runs (`--stop` idle
daemons — memory pressure is a known flake source on this box). One gate per session is
a sane cadence; C/D/E are independently pauseable. If DSL completeness (Gate 0a) finds
major NoQL constructs with no DSL form, STOP and review the wire-schema additions with
the team before Gate B locks the format.

## Definition of done

P1–P3 are green; nodb mode runs zero embedded ES; full itest suites green in nodb mode;
corpus green under the documented acceptance rules; refresh contract proven; index
rebuild drill green; default mode byte-identical; RUNNING.md + DESIGN.md updated; branch
pushed.
