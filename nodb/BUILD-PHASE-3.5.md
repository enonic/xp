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
