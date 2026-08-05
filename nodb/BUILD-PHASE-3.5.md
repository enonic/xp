# NoDB Build — Phase 3.5 Work Order (version history + branch diff on SQL)

**Read first:** `nodb/DESIGN.md` §4 (schema), §9.1 (de-search decisions and why this
family is the exception); `nodb/BUILD-PHASE-4.md` prerequisite gate P2 and decision 4
(both are absorbed by this phase); `nodb/RUNNING.md` (the stack this extends).
Self-contained.

## Goal

Make the **storage-index query family** work in nodb mode by serving it from
PostgreSQL: version history, active versions, branch diff (resolve-sync-work), and
commit lookups. After this phase, Content Studio's publish dialog, publish-status
badges, version history panel and compare/restore work against the hybrid
(Phase 3) stack — hybrid mode becomes a daily-usable system instead of a storage
verification rig.

## Why this does not wait for Phase 4

The §9.1 deferral rationale ("structural queries work on search today; SQL is a
speedup, not a migration step") holds for the *search-index* family
(`findByParent`, children, ordering) but is **false** for this family: these
queries ran on the ES *storage index* (`has_child`/`child_type: branch` over
VERSION/BRANCH docs), which nodb mode never creates. For them, SQL is the only
path, not an optimization. Phase 4 decision 4 already acknowledged this by pulling
version-history into Gate C; this phase extracts that mini-slice (plus its P2
prerequisite) into its own deliverable because:

- it has **zero coupling to OpenSearch** — no outbox, no indexer, no translator;
  pure PG work on tables designed for exactly these predicates;
- it unblocks real hybrid-mode UI use now (observed 2026-08-04: Content Studio
  errors on every publish-resolution/version/compare dialog);
- it removes the `findVersions` blocker from binary GC (risk #14) ahead of
  Phase 5 (GC itself stays Phase 5 — note, don't scope);
- it shrinks Phase 4's Gate C and retires prerequisite P2 early, de-risking the
  long pole.

## Architecture decisions locked up front

1. **Scope is the storage-index family ONLY.** Version history (versions by node,
   paged/ordered), active versions per branch, branch diff / resolve-sync-work
   (the `has_child` draft-vs-master query), commit get/find, and
   versions-by-blob-key (the GC query — SPI only, no GC). The search-index
   structural family (`findByParent`, children, manual order, references) stays
   deferred to Phase 8 — nothing here touches it.
2. **Repo-scoped version identity first (absorbs Phase 4 gate P2).** All version
   get/delete/history/diff operations resolve the repository and predicate on
   `repo_key`. No unenforced tenant-global version-id assumption anywhere in the
   new surface.
3. **SPI methods on `NodeStore`, default-throws, command-level routing** — the
   Phase 1 `getChildren` hook pattern. nodb implements them on
   `node_version`/`branch_entry`/`node_commit`; the ES/hybrid default path is
   UNTOUCHED (byte-identical rule) — commands route to the SPI only when the
   backend supports it, otherwise the legacy storage-index flow runs exactly as
   today. Converging ES onto the SPI methods is optional later cleanup, never a
   requirement of this phase.
4. **Exact-parity acceptance.** These queries are deterministic (no scoring):
   results must match the ES storage-index path exactly — same hits, same order
   where the API orders, same counts — proven by both-backend diff tests.
5. **Diff semantics are branch_entry semantics.** The ES query's meaning —
   "node present in exactly one of (source, target), or present in both with
   different version ids, under an optional path scope" — is implemented as
   joins/anti-joins on `branch_entry` (with `node_version` only where version
   identity is compared). State the SQL semantics in code-adjacent docs; the ES
   query is the reference, not the specification.

## Branch

`nodb-phase35-version-sql` off `nodb-phase3-payloads`.

## Gates

| Gate | Deliverable | Verification (all must hold) | Est. |
|---|---|---|---|
| **P2** | **Repository-scoped version identity** (verbatim from Phase 4 prerequisites). Version get/delete and the new SQL surface resolve the repository and predicate on `repo_key`. | A test stores the same `version_id` in two repos in one tenant, then proves get/delete/history affect only the selected repo; server requests carry/resolve repo identity; grep/review finds no unscoped runtime version lookup/delete; existing single-repo behavior green. | ~120k |
| **0** | **Inventory, no production code.** (a) Call-site inventory of the storage-index query family: `NodeVersionQuery`, active-versions, `NodeVersionDiffQuery`/resolve-sync-work, commit queries, `IsBlobUsedByVersionCommand.findVersions` — who builds them, what options they carry (paging, ordering, path scope, branch pairs). (b) SQL shape + index check per query against the Phase 3 schema (confirm existing indexes serve them; list any additions as a migration per Phase 4 gate P3 discipline if P3 has landed, else as reviewed DDL). (c) Choose the itest classes that gate B must turn green (list them in this file). | Inventory recorded in this file; SQL shapes reviewed; itest list pinned. | ~60k |
| **A** | **Engine + SPI.** `NodeStore` methods (default-throws): `getVersions(node)`, `getActiveVersions(node)`, `diffBranches(source, target, pathScope?)`, commit get/find, `getVersionsByBlobKey` (risk-#14 enabler, no consumer wired). nodb engine implements them as repo-scoped SQL; wire-level RPCs added to the proto (one source of truth if Phase 4 gate P1 has landed; otherwise follow current proto conventions and note for P1). | nodb build green. Engine tests: each method against seeded multi-branch/multi-repo data; dual-tenant isolation asserted; P2 predicates proven (same version-id in two repos); diff correctness incl. path scoping, both directions, and the both-present-different-version case; pagination/ordering stable. | ~160k |
| **B** | **XP-side wiring.** nodb-mode routing of the owning commands (`FindNodeVersionsCommand`, `GetActiveNodeVersionsCommand`, resolve-sync-work / `FindNodesWithVersionDifferenceCommand`, commit reads) to the SPI; hybrid/ES default path byte-identical (no routing change when backend != nodb). | Full XP build green; default-mode spot itests byte-identical; the Gate 0 itest list green in nodb mode; **both-backend diff test**: identical results from ES path and SQL path over a seeded corpus (branch pairs × path scopes × edge cases). | ~160k |
| **C** | **Boot smoke + docs.** Live hybrid boot (RUNNING.md recipe): Content Studio flow — edit → publish dialog resolves dependencies → publish → version history panel → compare/revert — with zero `Search request failed` dialogs from this family. RUNNING.md "state of the world" updated (remove the limitation, note what remains: GC Phase 5, OpenSearch Phase 4). DESIGN.md §9 gets the 3.5 row; BUILD-PHASE-4.md amended: P2 marked done here, decision-4 scope reduced to "already landed in 3.5", Gate C estimate reduced. | Manual smoke recorded (screens/log evidence); docs updated; both suites green in both modes; push `nodb-phase35-version-sql`. | ~80k |

Total ≈ **580k** output tokens (the conversational ~400k estimate excluded P2's
full weight and the smoke/docs gate).

## Gate 0 results (2026-08-04)

**P2 status:** landed — `VersionStore.get/delete` repo-scoped end-to-end with proof
tests at engine/server/client layers. One holdout found by inventory: **commit get is
tenant-global on the wire** (`GetCommitRequest` carries no repo; `CommitStore.get` has
no `repo_key` predicate). `node_commit`'s PK is `commit_id` alone so no data collision
exists, but addressing must be repo-scoped — folded into Gate A (the commit RPC gains
`repo_id` alongside the new `FindCommits`).

**Scope corrections from inventory:**
- **Active versions already works in nodb mode** — `GetActiveNodeVersionsCommand` loops
  branches via SPI-routed `getBranchEntry` + `getVersion`; no storage-index query
  remains. The `getActiveVersions` SPI method is a one-round-trip convenience
  (engine has the join already: `BranchStore.JOINED_SELECT`); no Gate B routing needed.
- **`getVersionsByBlobKey` is not a separate method** — `IsBlobUsedByVersionCommand`
  builds a `NodeVersionQuery` with a blob-key `ValueFilter` and `size(0)`; it is served
  by the `findVersions` surface's filter support.

**The `findVersions` surface is bounded but wider than "history by node".** Enumerated
callers and their needs: `GetNodeVersionsCommand` (order `ts DESC, version_id ASC` +
keyset cursor `(ts, version_id)`), `RepoDumper` (ts floor, scroll-all `size=-1`),
`VersionTableVacuumCommand` (keyset `version_id ASC` + ts ceiling), `SegmentVacuum` /
`IsBlobUsedByVersion` (`size=0` count-only; blob-key term on `binary_keys` /
`node_data_hash`). Accurate `totalHits` required independent of page size. No ACL
filtering on any storage-source query (parity: SQL must not filter either).

**Diff semantics pinned (ES reference,** `DiffQueryFactory`**):** scope root IS
included; path comparison is CASE-INSENSITIVE (paths indexed lowercased; `NodePath`
equality ignores case); scope/excludes evaluate per-side (each hit's own branch-child
paths — what makes renames behave); both-present-with-different-versions yields 2 ES
hits deduped client-side into a NodeId set — SQL returns distinct node ids and the
corpus compares SETS, not raw hit counts (`HasUnpublishedChildren` needs only
existence). `HasUnpublishedChildrenCommand` is the observed root-`must_not`: scope =
parent path, excludes = [parent path], count-only.

**Required DDL (no migration-discipline gate landed yet — new ordered migration file,
upgrade note for existing dev tenants):**
```sql
CREATE INDEX branch_entry_path_lower ON branch_entry (repo_key, branch, lower(node_path) text_pattern_ops);
CREATE INDEX node_version_by_node_v2 ON node_version (repo_key, node_id, ts DESC, version_id ASC); -- replaces node_version_by_node
CREATE INDEX node_commit_by_repo ON node_commit (repo_key);
```
(The unique path index cannot serve prefix scans: DB collation is `en_US.utf8`, no
`text_pattern_ops`/`COLLATE "C"` anywhere; and parity needs `lower()`.)

**Routing seam (Gate B):** no production command-level routing precedent exists yet —
this phase writes the first. Backend detection = capability probe on the injected
`NodeStore` (a `default false` method beside the default-throws hooks), never a config
lookup. Owning commands: `FindNodeVersionsCommand` (one seam covers history, dump,
vacuum, blob checks), `FindNodesWithVersionDifferenceCommand`,
`HasUnpublishedChildrenCommand`, `FindNodeCommitsCommand` (sole caller: `RepoDumper`).

**Gate B itest list:** itest-core: `FindNodeVersionsCommandTest`,
`GetNodeVersionsCommandTest`, `FindNodesWithVersionDifferenceCommandTest`,
`HasUnpublishedChildrenCommandTest`, `ResolveSyncWorkCommandTest`,
`PushNodesCommandTest`, `GetActiveNodeVersionsCommandTest` (baseline),
`CompareNodeCommandTest` + `CompareNodesCommandTest` (setup-dependent — verify, don't
drop silently), `VersionTableVacuumTaskTest`. itest-core-content:
`ContentServiceImplTest_{versions,versionAttributes,getActiveVersions,publish,`
`publish_update_publishedTime,unpublish,resolvePublishDependencies,`
`resolveRequiredDependencies}` — ⚠️ itest-core-content has ZERO nodb harness plumbing
(no property forwarding, no `NodbTestCluster` wiring): Gate B extends the harness or
explicitly defers content-level proof to Gate C's live smoke.

**Wire toehold:** proto already reserves `rpc FindVersions (...) returns (stream
Version)` with an empty placeholder request message; server leaves it un-overridden.

## Gate B results (2026-08-05)

**Routing pattern established (first production instance):** builder-injected
`nodeStore(NodeStore)` on the owning commands; at `execute()`,
`nodeStore.supportsVersionQueries()` selects the SPI path, else the legacy search
path textually unchanged. `NodeServiceImpl` gains the `NodeStore` as an OSGi
`@Reference` constructor parameter (same acquisition as `VersionServiceImpl`).
ES translators, request factories and `DiffQueryFactory` untouched — default mode
byte-identical by construction (core-repo 396/396 green).

**Translator (`SpiVersionQueryFactory`):** accepts exactly the Gate 0 inventory
shapes; everything else throws `IllegalArgumentException` naming the construct —
no predicate is ever silently dropped. One documented widening: bare
`[timestamp DESC]` maps to `TS_DESC_ID_ASC` (ES leaves equal-ts order undefined).

**Fixes made during verification:** root path scope normalized to null scope
(engine `'/'`-prefix predicate only matched the root row); engine diff ordering
pinned to `GROUP BY node_id ORDER BY min(lower(node_path))` — deterministic,
parents-before-children, matching the ES path's asserted order (oracle corpus
rerun green); one itest-harness retrofit.

**Results:** curated nodb set green — FindNodeVersions 2/2, GetNodeVersions 8/8,
FindNodesWithVersionDifference 8/8, HasUnpublishedChildren 2/2, ResolveSyncWork
34/34, GetActiveNodeVersions 1/1, CompareNode 7/7, PushNodes 15/15. Default ES
mode: full classes incl. every excluded method, 112/112.

**Recorded exclusions (each verified against its stack trace):**
- `deleted_in_source`/`deleted_in_target` (diff), `status_deleted_stage_yields_new_
  in_target` (CompareNode), `CompareNodesCommandTest`, and 2 VersionTableVacuum
  methods: all fail in `DeleteNodeCommand` — the DELETE CASCADE lists children via
  a `NodeBranchQuery` against the ES storage index. **The one remaining
  storage-index consumer this phase exposed; Phase 4/8 dependency** (needs
  `NodeStore.getChildren` production wiring).
- `VersionTableVacuumTaskTest` (rest of class): per-method ES wipe vs class-scoped
  nodb tenant — pre-existing Phase 1 fixture-granularity asymmetry. The vacuum
  query shapes themselves are engine-tested.
- `PushNodesCommandTest#rename_to_name_already_there_but_renamed_in_same_push`:
  intra-push path swap vs `unique(repo_key, branch, node_path)` — DESIGN.md risk
  #8 (DEFERRABLE unique), predates this phase; schedule with subtree-move work.

**itest-core-content: deferred** — zero nodb harness plumbing exists (fixture
hard-codes the ES store across ~6 service constructions); wiring it is a full
replication of AbstractNodeTest's nodb branch, not mechanical forwarding.
Content-level proof moved to Gate C's live smoke per the work order's option.

## Gate C results (2026-08-05) — PHASE COMPLETE

Live smoke on the rebuilt stack (both sides on the new proto): full CS flow green —
create → update ×2 → version history (exact order) → resolvePublishContent →
publish → modify → hasUnpublishedChildren → compare → revert, all via the CS REST
endpoints with a session login. **Gate check: zero "Search request failed", zero
IndexException in the entire boot log.** Ground truth: versions 48→93, commits 0→4,
3 versions commit-linked; migration 002 confirmed applied to the live tenant.

**Second in-family latent bug found by the smoke and fixed:** publish's
commit-linkage step (`NodeStorageServiceImpl.commit`) re-stores each pushed version
with `commit_id` set; the engine's plain `INSERT` threw duplicate-key — publish
half-completed (pushed, no commit link, error swallowed by CS). Fix:
`VersionStore.store` is now `ON CONFLICT (repo_key, version_id) DO UPDATE` — the
ES-parity semantics (the ES path is an index-doc overwrite; DO NOTHING would
silently drop the commit_id mutation). Immutability lives above the store: payload
FKs still reject unknown hashes; re-storing callers mutate only
commit_id/attributes; data changes mint new version ids. `WriteBatchTest`'s
mid-batch-failure mechanism moved to the payload FK (property unchanged, 6/6).
New proof test: `versionReStoreIsAnUpsertLinkingTheCommitId`.

Revalidation post-fix: 72 itests, 69 green; the 3 failures are the two documented
out-of-family exclusions verbatim (delete-cascade ×2, rename-swap ×1). Two mid-smoke
publish ERRORs were correct behavior (raw updates left workflow IN_PROGRESS;
CheckContentValidity correctly refused — CS marks ready first).

Docs updated: RUNNING.md state-of-world, DESIGN.md §9 (3.5 row, DONE 2026-08-05),
BUILD-PHASE-4.md amendments (P2 done-in-3.5, decision 4 landed, Gate C reduced) —
the P4 file exists only on nodb-design; its edits sit untracked here and must be
carried to that branch.

## Key risks carried into the gates

- **Diff-semantics drift**: the ES `has_child` query encodes subtle cases
  (both-present-different-version; path scope applied to which branch's path;
  excluded roots). Gate 0's inventory + Gate B's both-backend corpus exist so
  these are enumerated and proven, not guessed. The corpus must include renames
  (path differs between branches) — the case most likely to diverge.
- **P2 ordering**: building the history surface before repo-scoping version
  identity would bake the tenant-global assumption into new code — P2 is first
  for a reason.
- **Estimate risk in Gate A** if proto conventions are unsettled (P1 not landed):
  keep RPC additions minimal and mechanical; do not start the P1 consolidation
  here.
- **Scope creep toward Phase 8**: children/manual-order/references will look
  temptingly adjacent once the SPI pattern is in hand. They are search-index
  served and work today — out of scope, by decision 1.

## Execution guidance

Same regime as Phases 0–3: agents build, orchestrator verifies (forced reruns,
independent re-run of the both-backend diff corpus, diff review, real boot with
Content Studio) and commits per gate; never commit red; machine hygiene before
long runs. This phase is independently pauseable after any gate and parallelizable
with Phase 4 Gate 0 spikes (no shared code).

## Definition of done

P2 green; the Gate 0 itest list green in nodb mode; both-backend diff corpus
green; default ES mode byte-identical; live Content Studio publish/version/compare
flow clean on the hybrid stack; risk #14's query dependency noted as unblocked
(GC still Phase 5); RUNNING.md + DESIGN.md + BUILD-PHASE-4.md updated; branch
pushed.
