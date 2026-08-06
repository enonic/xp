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

### P3 DONE (2026-08-06) — mechanism and the pre-GA baseline rule

**Checksum:** `sha256:<hex>` (same shape as the content-addressed `payload.hash` keys) over
each migration file's UTF-8 content, **normalized** by converting `\r\n`/`\r` to `\n` and
stripping trailing whitespace per line and at end of file. Those are exactly the edits
tooling makes with no human intent (`core.autocrlf` on a fresh checkout, an editor's
strip-on-save, a missing/added final newline) — a checksum that fired on them would cry wolf
instead of catching drift. Everything else, down to one character of SQL or comment, flips
the hash. Consequence to respect: a migration may not rely on significant trailing
whitespace inside a multi-line string literal.

**Storage:** a **companion table** `nodb_system.tenant_migration (tenant_id, version, name,
checksum, applied_at)`, PK `(tenant_id, version)`, FK to `nodb_system.tenant` `ON DELETE
CASCADE` (so `dropTenant`'s existing single DELETE still cleans up). Not a column on
`nodb_system.tenant`: checksums are one row *per migration per tenant*, which a single
`template_version` column cannot hold. Created by `MigrationRunner.ensureSystemSchema`
alongside the `tenant` table, so it appears with `CREATE TABLE IF NOT EXISTS` on existing
installations. **`nodb/schema/schema.sql` is unchanged (still v0.4)** — it is the summed
*tenant-schema* content of `001+002`, and `nodb_system` has never been part of it (it is
Java-side DDL); no tenant-schema DDL was added and `001`/`002` were not touched.

**Invariants enforced on every provisioning/upgrade run**, each failing with
`MigrationIntegrityException` (never a downstream SQL error): (1) the manifest is
`NNN_name.sql`, ordered and gapless from `001`; (2) an already-applied slot still carries the
same file name (rename/reorder rejected); (3) an already-applied slot still has the same
checksum — *"migration 002_x.sql has changed since it was applied to tenant Y; migrations are
immutable — add a new migration instead"*; (4) a tenant is never ahead of the manifest
(forward-only, no downgrade).

**Adopt-on-first-run rule (pre-GA).** A tenant with a `template_version` but **no**
`tenant_migration` row for an applied slot is in an UNKNOWN state, not a mismatch: it was
provisioned before this gate. On the first run after this gate it **adopts** the current file
checksums as its baseline (one `INFO` log line per adopted slot) and proceeds; only slots that
do carry a recorded checksum are compared. Safe strictly because everything is pre-GA — the
protocol and schema are a draft, every installation is a development installation whose
tenants are provisioned from scratch by itest/bench/dev-stack runs, so an unchecksummed slot
can only have come from the matching file in the same working tree. **At GA this must become
an error:** no adoption, an unknown state is a failure like a tamper, resolved only by an
explicit audited operator action (a recorded baseline import) — with durable customer data,
"assume it matches" is precisely the assumption that hides a divergent schema.

**Green:** `nodb/engine` `MigrationIntegrityTest` 11 tests (fresh checksum recording,
Phase-3→002 upgrade, tamper rejected, whitespace-only reformat accepted, rename rejected,
ahead-of-manifest rejected, out-of-order/gap/bad-name manifests rejected, adopt-on-first-run,
dual-tenant isolation) + `TenantProvisioningTest` 5, whole `nodb` build green.

### P1 DONE (2026-08-06) — mechanism and compatibility policy

**One source of truth: `nodb/proto/nodb.proto`, read in place by both builds.** The protobuf
Gradle plugin's proto source set accepts an arbitrary `srcDir`, so neither build needs the
file inside its own module: `nodb/server/build.gradle.kts` adds `$rootDir/proto` and
`modules/core/core-storage-nodb-client/build.gradle` adds `$rootDir/nodb/proto` — the same
file, no copy, no generated staging dir, nothing tracked twice. The two vendored copies
(`nodb/server/src/main/proto/nodb.proto`,
`modules/core/core-storage-nodb-client/src/main/proto/nodb.proto`) are deleted. **Drift
guard:** each build registers `checkNoVendoredProto` (wired into both `generateProto` and
`check`, so it fires before protoc and inside the normal verification lifecycle) which
fails if the canonical file is missing or if *any* `.proto` reappears under that module's
`src/main/proto/`. Fixed on the way past: the `Search` RPC comment, which still described
the abandoned "serialized XP query AST" wire (decision 1) — item 6 of "Items the work
order does not account for".

**Compatibility policy: EXPLICIT LOCKSTEP while the protocol is a v0.1 draft** (recorded
in full at the top of the canonical proto). All producers and consumers live in this
repository, and XP and NoDB are built and deployed together (`dev-stack.sh` rebuilds both),
so there is no independent upgrade path to preserve: breaking changes — field renumbering
(Phase 3.5 did exactly that), retyping, RPC reshaping, message deletion — are permitted,
and the only obligation is that both sides are regenerated and redeployed from the same
revision. A mixed-version XP/NoDB pair is unsupported and undefined. **This ends at Phase 6**
(multi-XP, rolling upgrades, horizontally split runtimes), where revision N must interoperate
with N±1 live. Moving to versioned rolling compatibility then requires: no renumbering or
retyping ever, additive-only changes, `reserved` ranges for every removed field number and
name, new semantics behind new fields rather than redefined ones, and a per-connection
negotiated protocol version (the search envelope's `format_version` is the precedent).

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
| D1 | **`text` postfix** (blocker 1) — change the index-document shape | give the text/STRING value type a real postfix — **`_text`, not `_string`** (agreed 2026-08-05). The postfix set is XP's documented public vocabulary (developer.enonic.com/docs/code/stable/storage/properties#value_types lists exactly nine index types — text, number, datetime, geoPoint, ngram, analyzed, stemmed, path, orderby) and eight of the nine already have matching postfixes (`_number`, `_datetime`, `_geopoint`, `_ngram`, `_analyzed`, `_path`, `_orderby`, `_stemmed_<lang>`/`_orderby_<loc>`); **`text` is the only one whose postfix is empty**, which is exactly this blocker. `_text` completes the set, matches the docs, and makes the index self-describing in XP's own terms. (`StaticIndexValueType.STRING` keeps its internal enum name — smallest change; renaming it to `TEXT` is optional cleanup.) ⚠ Record in the mapping resource that **OpenSearch's `text` means ANALYZED**, so XP's `_text` maps to an engine `keyword` — the collision is a vocabulary difference, not an error (XP's `_analyzed` is itself an engine `text` field). Because every value type is also indexed as text, `_text` exists for every indexed property and is therefore the universal retrievable form — and the natural home for `_allText`'s missing base variant (DESIGN §5 virtual fields). Verified end-to-end (the Gate 0(d) validation ran on this shape). Decide FIRST: it changes the Gate A index RPC and the Gate B serializer. **Apply on the nodb side ONLY** — changing the ES layout would break the byte-identical rule and force a reindex of existing installs; the nodb doc builder emits `_string` and NoDB's translator resolves STRING to it (same mechanism as D6). Safe by XP's own rules: `.`, `_` and `[]` are ILLEGAL in property keys (dot IS the path separator, underscore is the system-reserved prefix — developer.enonic.com/docs/code/stable/storage/properties), so no user property can collide with a `_`-prefixed sub-field, and every dot in a physical name is a genuine PropertyPath separator — the OpenSearch object tree therefore mirrors XP's PropertyTree exactly (which is also what would make a future `nested` mapping clean). No dotted-property-name parity risk exists |
| D1b | **`_analyzed` → `_fulltext` in the nodb path — DO IT NOW** (agreed 2026-08-06, reversing an earlier "defer to Gate F" recommendation) | Rides D1's change at zero incremental cost: same resolver table, same mapping file. **Coherence evidence is four-to-one**: the index-config directive is `fulltext`, the query function is `fulltext()`, the dynamic template is literally named **`template_fulltext`** (`search-mapping.json`, matching `*._analyzed`) — only the physical field says `_analyzed`. The docs' "analyzed" label is likewise the outlier against the directive developers write, so aligning it is a clarification, NOT a vocabulary break (the opposite of `text`, where the doc label is coherent because it separates index role from value type). **Why the earlier objections don't hold:** (i) the corpus compares RESULTS, not layouts — a physical rename is invisible to it, except highlight result keys, which are bounded and corpus-covered; (ii) the "app queries `data.title._analyzed` by literal name" hack **already breaks under D1** (a string-typed lookup now appends `._text`, giving `data.title._analyzed._text`), so `_fulltext` adds no new compatibility cost. Scope: nodb path only; ES stays on `_analyzed`. **Must change in the same commit:** the highlighter's postfix list and `HighlightedPropertiesFactory`'s result mapping (see also its suspicious `name_analyzed` underscore-no-dot naming), the `*._analyzed` → `*._fulltext` template (being rewritten to `path_match` anyway), and the doc label |
| D2 | **BRANCH store type** — OpenSearch query or a 4th SQL surface | **SQL surface.** Three arguments, verified 2026-08-06, strongest first. **(1) Reindex cannot read the index it rebuilds.** `ReindexExecutor:79` lists branch entries in order to REBUILD the search index; today that is non-circular only because storage and search are separate ES indices. With no storage index, answering branch listing from OpenSearch makes reindex read what it is reconstructing — and makes **Gate G's rebuild drill impossible by construction**. **(2) The OpenSearch route is not "one more translator", it is denormalizing storage into search.** Search docs carry `TIMESTAMP, VERSION, NODE_TYPE, ALL_TEXT, MANUAL_ORDER_VALUE, PATH, ID, SOURCE, NAME, REFERENCE, PARENT_PATH, PERMISSIONS_*` — **no blob keys** — while callers consume `BranchIndexPath.entryFields()` = `versionId` + the three payload hashes. Serving it from OpenSearch means write-amplifying every node doc with three content hashes, i.e. exactly the storage-in-search shape DESIGN §5 says was eliminated ("only ONE index kind per repo"). **(3) Freshness on a destructive op.** `DeleteNodeCommand:85` forces `refresh(STORAGE)` before listing children; under OpenSearch that becomes `awaitRefresh` on the outbox checkpoint, so delete either blocks on indexer lag or proceeds against an incomplete subtree (a data-loss shape). In SQL the refresh call disappears — a correctness improvement, not just a saving. **Call sites confirmed: exactly 3** (`DeleteNodeCommand`, `RepositoryServiceImpl.deleteBranch:297`, `ReindexExecutor:79`) plus `NodeSearchService` plumbing; nothing in `lib-*`, nothing app-facing. **Shape (checked, no consumer reshaping needed):** consumers already loop one entry at a time (`ReindexExecutor:94`), so the SPI returns a **keyset-paged iterable** on `(repo_key, branch, node_path)` — the index migration 002 already added — behind the existing `NodeBranchEntries` type, whose population merely becomes lazy; the delete paths keep materializing (one bounded by subtree, one an admin whole-branch op). Must preserve an up-front total for `listener.branch(..., getSize())` (cheap indexed count or the first page's total). **~150–250k + ~50k** for paged plumbing and the count. ⚠ Pre-existing and NOT caused by this change: `ReindexExecutor` accumulates every node id into `ReindexResult`, so reindex is O(N) in memory regardless of how the query is fed — true on ES today, out of scope here, but it means "reindex is memory-safe at scale" is not a claim D2 can make alone |
| D3 | **G-1 geoPoint** | **fail fast, no wire support** — it already errors in ES 2.4 |
| D4 | **G-2 `IN` typing / G-4 `range` case** | **fix both** — the DSL path already behaves correctly where NoQL does not; pin the new behavior with the existing corpus rows |
| D5 | **G-3 `analyzer` arg** | add the optional field (cheap; zero call sites today) |
| D6 | **G-6 store-mode field resolution** | derive `fieldResolution` from the envelope's store-type; moot for BRANCH if D2 = SQL |
| D7 | **Split the scoring-parity rule** (decision 5) | non-ICU sorts EXACT (order-by values are pre-encoded lexi-sortable ASCII); `_orderby_<loc>` ICU = documented deltas — but see D8: with XP-side keys the deltas become "XP's pinned ICU 78.3 vs embedded ES's ancient ICU", i.e. an intended end state rather than an unfixable engine difference, and they vanish at Gate F |
| D8 | **`_orderby*` sorting mechanism — RESOLVED: compute ICU collation keys in XP** (agreed 2026-08-05), superseding the earlier `icu_collation_keyword` recommendation | **Treat language sorts exactly like numeric/date sorts: XP computes the key and ships it.** Evidence: XP already depends on `com.ibm.icu:icu4j:78.3` (`libs.versions.toml:80`, wired into core-repo) while the OpenSearch 3.7 image bundles **icu4j 77.1** — delegating to `icu_collation_keyword` would silently hand the sort contract to the engine's OLDER ICU. And the current filters set **no options whatsoever** (every one is literally `{"type":"icu_collation","language":"<code>"}` — zero strength/alternate/caseFirst/numeric/decomposition), so `Collator.getInstance(locale).getCollationKey(text)` + hex encoding is faithful by construction; no option matrix to replicate. **Consequences: `analysis-icu` is then needed for NOTHING** (Gate 0(d) proved it was required only by those 44 filters; the other 2 filters and all 40 language analyzers are core Lucene, ja/ko/zh via core `cjk`) → **stock `opensearchproject/opensearch:3.7.0`, no derived image, no managed-service plugin-parity risk.** Net-negative code: adds one `CollationKeyResolver` beside `OrderByValueResolver` (hex precedent exists in `LexiSortable`) + one call site in `IndexItemFactory.createOrderBy`; deletes 44 collation filters + 44 `icu_sort_*` analyzers (settings ~600 → ~50 lines), collapses 44 `*._orderby_<loc>` templates into one `keyword` template, removes the fielddata question, and retires `IcuSortConfigConsistencyTest`'s 44-filter assertions. Query time gets simpler too (ordinary keyword sort, not an analyzed-field sort) and the `unmappedType("long")`-on-a-string-field bug is fixed on the way past. **~200k as a Gate A sub-item.** Scope: **nodb path only**, like D1 — changing `_orderby_<loc>` for embedded ES would alter that layout and force a reindex. Plan for: collator thread-safety (clone/pool), write-path CPU per (value × configured language) — measure in Gate G, prefix-safe truncation of keys (~2–3× source length hex-encoded), and ICU 78.3 becoming an explicit pinned contract upgraded only by generational rebuild. Bonus: the 4 long-failing `icuSort` itests should pass on a modern pinned ICU |
| D9 | **Response-side tagging** | NoDB tags each aggregation's kind + bucket-key type; **+150–200k to Gate E** |
| D10 | **Doc `_id` composite** (`<nodeId>_<branch>`) | required once per-branch mapping types are gone; Gate A deliverable text |

**Revised total: ≈5.3M** (4.97M + D2 ~200k + D9 ~175k), still inside the 5–8M window.
Prerequisites remaining: P1 (proto source of truth — now also fixes the stale "serialized
AST" comment), P3 (migration checksums).

## GATE A DONE (2026-08-06) — OpenSearch foundations + indexer

190 nodb tests / 21 suites / zero failures under a clean forced rerun; XP client bundle
green; **zero XP module files changed** (`git diff -- modules/ gradle/` empty), so the
byte-identical rule holds by construction rather than by testing. Migration **003** added
under P3's checksum discipline (001/002 untouched). Mapping port: settings 600 lines → 2
filters + 9 analyzers + 1 normalizer + 1 tokenizer; 91 dynamic templates → 48; stock
`opensearchproject/opensearch:3.7.0` (no plugin, no derived image).

### 🚨 BLOCKER 3 — `path_match: "*"` matches OBJECT paths (third of the same family)

Blocker 2's fix (`match` → `path_match`) exposed the next layer: ES 2.4's `match: "*"`
matched *leaf names*, so the catch-all was harmless; `path_match: "*"` also matches object
paths, so the catch-all mapped `data` itself as a keyword and **every document failed** —
`failed to parse field [data] of type [keyword] … Can't get text on a START_OBJECT`.
Fix: **`match_mapping_type: "string"`** on the catch-all (`object` is one of the values it
can take, so naming a leaf type is what excludes objects). Note it takes a SINGLE value in
OpenSearch 3.7, not the array ES 8 accepts. Fires on the first document of every repo, like
blocker 1. Asserted by `theCatchAllExcludesObjectsViaMatchMappingType`, and found ONLY
because the test indexes a real document — the bar this gate set.

### D8 amended: the collation resolver lives in NoDB, not XP core

D8 said XP computes the keys. Implemented server-side instead (`CollationKeyResolver` in
`nodb/engine`, **icu4j 78.3 pinned in nodb's own version catalog** — the same version XP
pins). Rationale accepted: D8's actual risk was the *engine image's* ICU (77.1) silently
owning the sort contract, and an explicitly pinned 78.3 under NoDB's control removes that
identically — while NoDB is the component that owns index generations, i.e. exactly who
must pin a version whose bump requires `+g(N+1)`. The XP-side alternatives were both worse:
`IndexItem.getPath()` and `IndexItemFactory.createOrderBy` are *unconditional* call sites,
so wiring them there either breaks byte-identical or needs a backend flag inside core-repo
(which has none — selection is OSGi service ranking, invisible above the SPI); and the
client bundle embeds dependencies as private packages, i.e. a 14 MB icu4j embed. The input
is faithful by construction: XP already ships the same lowercased/1024-truncated
`OrderByValueResolver` output that ES 2.4's `icu_sort_<loc>` chain collated.
**Consequences:** `IcuSortConfigConsistencyTest` stays green and untouched (its 44-filter
assertions still describe the ES path), and port-list item 13 (test fixtures) does not
apply — the port is a new resource with its own consistency test. `_text`/`_fulltext` are
likewise server-side, in two classes (`IndexFields` + `IndexDocumentProjection`), so Gate
B's client stays a pure serializer and decision 3's later swap needs no rework.

### Composite `_id` (D10): `<nodeId>@<branch>`

`@` is legal in an OpenSearch `_id` and cannot occur in either component (`Branch` is
`^[a-zA-Z0-9\-:_]+$`), so it is injective without being parseable — deliberately, since
`_branch`/`_repo` are the fields that answer attribution. Without it, draft would silently
overwrite master in a single-type index: data loss, not an error.

### Migration 003 — two tables, and why

- **`search_document`** stores the **canonical XP-shipped** document (not the projected
  one), PK `(repo_key, branch, node_id)`, FK to branch `ON DELETE CASCADE`. Required
  because decision 3 + an async indexer means `refresh(SEARCH)` cannot survive a restart
  and Gate G has nothing to replay unless the document is durable — and because the
  projection is **versioned**, a bump must be replayable from rows that predate it.
- **`search_index`** is DESIGN §5's authoritative alias→generation map (`state`,
  `template_version`, `projection_version`). `projection_version` is what makes the Gate
  0(b) ACL finding *detectable* (a doc missing the injected admin key would otherwise
  vanish silently from admin queries).
- `outbox`/`index_checkpoint` from 001 unchanged — what was missing was content to apply.

### Notable implementation decisions (not pinned by the work order)

Jackson + raw REST rather than `opensearch-java` (the wire IS JSON DSL, so the translator's
job is JSON→JSON; a typed client forces parse→builder→re-serialize and adds a second query
model) · writes target the physical generation, reads target the alias (alias writes break
the moment a rebuild puts two indices behind it) · bulk first, checkpoint second
(at-least-once; the other order loses writes) · a repo with no index is skipped, never
implicitly indexed (an implicit index would come up with OpenSearch's dynamic mapping —
blocker 2's symptom by another door) · a missing shipped document is skipped, not treated
as a delete (WriteBatch commits its outbox row before XP ships the document — two SPI
calls, always have been) · per-op delete drops `search_document` rows in the same
transaction, or a rebuild resurrects deleted nodes · version/commit-only writes emit no
outbox row and report `outbox_seq = 0` · dates on the wire are epoch millis · repo create
is PG-then-index, delete is index-then-PG (a down search backend must not block a repo
delete) · `refresh(SEARCH)` drains synchronously if the poller is dead · `_bulk`'s
200-with-item-errors is inspected and thrown · container tests run `refresh_interval: -1`
so a missing `awaitRefresh` fails deterministically.

### Deferred from Gate A, with reasons

`_analyzed`→`_fulltext` in the **ES** highlighter (byte-identical path; Gate D owns
highlighting) · `_allText._text` base variant (additive, post-parity) ·
`unmappedType("keyword")` (query-side; Gate C) · `Search`/`Reindex` RPCs answer
UNIMPLEMENTED (Gates B–E) · the alias-flip rebuild **drill** (structure, metadata and
atomic `updateAliases` are in place; the drill is Gate G) · RUNNING.md (Gate G) ·
Prometheus/`nodb_outbox_lag_*` (Phase 6) · multi-instance indexer leader election (Phase 6;
the checkpoint's `GREATEST` semantics already make a second instance safe, just wasteful) ·
server-side document derivation from payloads (the phase's recorded main deferral — which
is exactly why `search_document` stores rather than forwards).

## GATE B DONE (2026-08-06) — XP wire + NoQL→DSL renderer

core-repo **461 tests / 96 suites / 0 failures** (forced rerun); nodb **198 tests**; XP
client **51**; whole tree assembles. **The end-to-end path works**: NoQL → renderer → wire
→ NoDB → OpenSearch → attributed results, 6/6 green against a real OpenSearch container
(`NodbSearchWireEndToEndTest` — includes multi-repo fan-out with per-hit attribution,
fail-closed empty principals, and an untranslated construct failing loudly).

**Byte-identical rule held**: zero files touched under `elasticsearch/query/translator/`,
`factory/query`, `factory/dsl`, `factory/function` or the resolvers. The only ES-adjacent
edit is 2 lines in `NodeSearchServiceImpl` — an import and `.searchDsl( () -> … )` — whose
lambda never executes in ES mode.

**The oracle is the gate's teeth, and it is cheap**: `NoqlDslRoundTripOracleTest`, **65
tests**, builds each NoQL string BOTH ways (expression tree vs renderer→`DslExpr`→
`ConstraintExpressionBuilder`) and requires identity across **44 construct rows**, of which
28 are additionally byte-diffed against **26 distinct pre-existing golden fixtures**
(verified present on disk). Two documented normalizations: `_name` removal, and collapsing
ES's `{"term":{"f":{"value":x}}}` long form — the engine emits those only to attach a query
name, and the two builder families disagree about query *names*, not about queries.

**Rulings implemented as decided:** G-1 geoPoint fails fast; G-2/G-4 **fixed** (per-value-type
resolution; normalized string bounds) with no `normalize:false` escape hatch; G-3 emits the
optional `analyzer`; G-5 rewrites both-bounds-empty `range()` to `exists`. Nesting is
preserved pairwise left-associative (asserted — a 3-term AND renders as two nested pairs).
Item 4's `unmappedType` bug fixed on the way past: field sorts now emit
`unmapped_type: keyword`, pseudo-fields none.

**Key design decisions not pinned by the work order:**
1. **The DSL is a lazy `Supplier<SearchDsl>` on `SearchRequest`, not eager.** Eager rendering
   would make G-1's fail-fast fire in ES mode — where a geoPoint term must keep erroring at
   ES — i.e. it would break byte-identity. An absent supplier means "no wire form", which is
   how branch/version/commit queries fail fast **without needing a `store_type` field**
   (D6 resolved by construction).
2. `SearchDsl` carries Java `Map`/`List`, not JSON text, so the client is a literal
   serializer and the oracle can feed the same structure into `PropertyTree.fromMap`.
3. G-3's `analyzer` was NOT added to XP's public `factory/dsl` builders — it is a
   wire-superset field, and expanding XP's public DSL is a product decision. That makes
   `ngram_set_analyzer` a documented asserted delta rather than a byte-identical row.
4. Aggregations/suggest/highlight are **not rendered at all** — the envelope slots exist and
   are rejected when populated (Gates D/E own the server side; half-filling them would be
   ~400 unverifiable lines).
5. `NodeSearchIndex#get` throws in nodb mode — verified its only route (`IndexDataService#get`)
   has **zero callers** in the repo.
6. Index create/delete are no-ops in nodb mode: `NodeRepositoryServiceImpl` calls the storage
   admin first, so `CreateRepository` already made the OpenSearch index; a second create
   would race.
7. `refresh(repositoryId)` awaits the highest outbox seq **this JVM** wrote for that repo (the
   SPI method carries no seq).
8. OpenSearch in `NodbTestCluster` is opt-in (`-Dxp.itest.opensearch=true`) — it adds a large
   container to every nodb itest run while only query itests need it. **Gate F is where it
   stops being optional.**

**NoDB translator subset now live** (Gate C extends it): term, in (N `should` clauses —
fan-out preserved, not a `terms` query), like, range, exists, boolean, matchAll, all with
boost; filters values/ids/exists/range/boolean; field sorts. ACL applied per source as
`should[ must[ term _repo, term _branch, terms _permissions.read._text ] ]` with
`minimum_should_match:1` — **no admin shortcut**, per DESIGN §7.2. Fails `INVALID_ARGUMENT`
on: fulltext/ngram/stemmed/pathMatch, geoDistance sorts, COLLATE sorts, aggregations/
suggest/highlight, unknown `format_version`, and GET_ALL beyond the 10 000 window (real
`search_after`+PIT is Gate C).

### `itest-core-content`: plumbing DONE, blocked by D2 — and it proves D2's necessity

All plumbing landed (property forwarding, Docker-socket block, nodb-client dependency, and
the nodb branch replicated into `AbstractContentServiceTest` per-class **and**
`AbstractIssueServiceTest` per-**method**, because that fixture wipes ES indices in
`@BeforeEach` and a per-class tenant reproduces the documented granularity hazard).
`ContentServiceImplTest_getById` in nodb mode: **all 6 test bodies PASS** — content create,
publish and get run green through the nodb backend — and all 6 then fail in the shared
`@AfterEach` `projectService.delete(...)` with `IndexNotFoundException[storage-system-repo]`
from `DeleteNodeCommand:88`'s `NodeBranchQuery`. **That is exactly D2's delete-cascade gap**,
reached from a second direction. No workaround was added: papering over teardown would hide
the one thing D2 exists to fix. **Gate C must land D2 before content-level itests can be
green**, which sharpens Gate C's ordering.

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
7. `standard` token filter (6 chains) → delete. **Finding: `document_index_default`
   (index-time) and `fulltext_search_default` (search-time) are BYTE-IDENTICAL** — both
   `{type: custom, tokenizer: standard, filter: [standard, lowercase, asciifolding,
   word_delimiter]}`. Two names, one analyzer: there is no index/search asymmetry to
   preserve, both change identically here, and the pair can collapse (or stay as aliases).
   Consequence worth documenting for developers: because `asciifolding` is in the chain,
   **`fulltext()` IS accent-insensitive while the raw `_text` field is NOT** — proven by
   corpus row `UNTYPED-05-ascii-folding` → 0 hits. That asymmetry is behavior, not a bug.
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
4. **`unmappedType("long")` on string fields — latent bug, becomes live at Gate C.**
   `SortQueryBuilderFactory` hardcodes `UNMAPPED_TYPE = "long"` and applies it to EVERY field
   sort (line 70), including the `_score`/`_id`/`_doc` pseudo-fields — but every `_orderby*`
   field is a lexi-encoded STRING (and a `keyword` after D8). Invisible today: when the field
   IS mapped the setting is ignored, and when it is absent everywhere the docs are all
   "missing" and `missing` defaults to `_last` regardless of type. **It bites on multi-index
   sorts** — repo A has `data.title._orderby` mapped as a string, repo B never had that
   property so it falls back to `long`, and modern OpenSearch rejects mixed types in one
   sort across indices. That is exactly Gate C's multi-source fan-out, over repos with
   divergent dynamic mappings, which is normal in XP. Fix: `unmappedType("keyword")` for
   `_orderby*`, and do not set it at all for the built-in pseudo-fields. Behavior-neutral
   where it works today. **Corpus row to add** (would pass in ES mode and fail on
   OpenSearch, so it is worth having before Gate C): a multi-repo sort on a field present in
   only one of the repos.
5. **Nondeterministic request JSON:** aggregations, suggestions and `ESSource` index/type sets
   are `HashSet`s — once serialized, element order varies across JVM runs, breaking wire-level
   golden files and any request caching. **Gate B's serializer must use ordered collections.**
6. **Proto drift:** `nodb/proto/nodb.proto:73-75` still documents `Search` as carrying "the
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
9. **`_allText` becomes an explicit XP mechanism at this gate** (deleting `_all`/
   `include_in_all` kills the last ES magic it leaned on) — see DESIGN.md §5 "Virtual /
   derived fields". Two Gate A choices to record rather than inherit: (i) its
   retrievability — the value is in `_source` today and the SPI can already read it via
   `returnFields`, which is simply never exposed to apps (an API gap, closable
   independently of this phase); (ii) **emit it as a multi-field** — `_analyzed` and
   `_ngram` are pure analyzer derivatives of ONE value, so unlike `_orderby` (independently
   computed, cannot be a multi-field) they can share it. `_source` carries the whole
   concatenated text twice today; this halves the largest field in the index and yields one
   canonical retrievable form.

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
