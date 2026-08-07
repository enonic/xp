# NoDB Build — Phase 5 Work Order (snapshots, restore, vacuum/GC, dump/load)

**Read first:** `nodb/DESIGN.md` §6 (the three backup layers, manifest snapshots, vacuum,
dump), §3.3 (refresh contract — restore interacts with it), §10 risks #5 (outbox trim),
#9 (referential-backup validity horizon), #14 (binary GC, unblocked by Phase 3.5);
`nodb/BUILD-PHASE-4.md` GATE G record (the rebuild endpoint and `search_document` replay
this phase builds on; the unexercised 3.5→4 upgrade path); `nodb/FINDINGS.md` #1 (the
two-transaction hazard — a PREREQUISITE here, see decisions); `nodb/RUNNING.md`.
Self-contained.

## Goal

Operations parity: a NoDB tenant can be **snapshotted, restored (whole-repo and
side-by-side), vacuumed and garbage-collected, dumped and loaded** — with retention
policies stated rather than implied — and a dump taken on an ES-backed instance loads
into a NoDB-backed one (the migration path Phase 7 will industrialise). After this phase,
"your data is safe and movable" stops being a design claim and becomes a drilled,
measured property.

## Architecture decisions locked up front

1. **The single-snapshot invariant (FINDINGS #1, non-negotiable).** Every GC/vacuum
   decision — "is this payload referenced?", "is this binary orphaned?", "which versions
   are older than the threshold?" — is computed and acted on within ONE repeatable-read
   transaction, or split into a mark phase and a sweep phase with an explicit, persisted
   grace window between them. No decision from read A may be enforced by write B in a
   different snapshot. The checkpoint lost-write (Phase 4 Gate C) is what the other shape
   costs. Gate 0 audits every existing `Tx.inTenantTx` pair on the vacuum/GC paths before
   any new code is written, and the engine gains a checked single-snapshot read helper.
2. **GC never outruns backups (risk #9).** A payload/binary is collectible only when
   unreferenced by (a) live `node_version` rows AND (b) every manifest snapshot within the
   retention horizon. Snapshot metadata therefore participates in the GC reachability
   set, and the horizon is an explicit per-tenant setting with a stated default — never
   an emergent property of when vacuum last ran. Referential backups carry their expiry:
   restoring one past the horizon fails loudly up front, not halfway through.
3. **Snapshots are manifest-shaped and exclude derived state** (DESIGN §6): the repo's
   narrow rows (branch entries, versions, commits) + the referenced content-hash set +
   the outbox position, taken under repeatable-read `COPY` without blocking writers.
   OpenSearch is never snapshotted — the manifest records the outbox seq to catch up to,
   and restore rebuilds via the Gate G replay machinery. Content-addressing makes
   snapshots incremental by construction; this phase keeps them **in-cell** (the portable
   `.ntb` export is the stretch gate, not the core).
4. **Restore is row-scoped and never disturbs neighbours** (DESIGN §6): in-place (drop
   the repo's partitions, reload, reindex — that repo briefly unavailable) and
   **side-by-side + atomic swap** (rows load under a new surrogate `repo_key`; verify;
   swap two `repo_id` mapping rows in one transaction while the search alias flips
   generation). The **zero-downtime rebuild driver** (BUILDING generation + atomic
   `updateAliases`, the Gate G residue) lands HERE, as restore's index half — one
   mechanism serving restore, reindex and future engine upgrades. During restore the repo
   carries an INDEXING status: get/getByPath/children answer from Postgres immediately;
   `refresh(SEARCH)` and queries wait or report status rather than silently serving an
   incomplete index.
5. **Vacuum is SQL; binary GC is mark-and-sweep with the same horizon.** Version
   retention = `DELETE` old `node_version` rows (keyset-batched, the 3.5 surfaces);
   payload GC = indexed refcount query (FK from versions + snapshot manifests), then
   sweep after the grace window; binary GC on S3 = mark from `node_version.binary_keys`
   + manifests, sweep respecting the grace window — now possible because version
   enumeration is SQL (risk #14 closes this phase). All GC is **operator/management
   triggered** in this phase; scheduling policy is Phase 6.
6. **Dump/load is the compatibility surface, not a new format.** `RepoDumper` reads
   through `NodeService` and blob keys, so dumps must work unchanged in nodb mode — and
   the round trip **ES-dump → nodb-load** is the phase's headline verification (it is
   Phase 7's migration path). Known input: `DumpServiceImplTest#limit_number_of_versions`
   is disabled on both backends (unordered size-limited query — Gate F disposition);
   decide the ordering rule here and re-enable it.
7. **Ops surface on the management plane.** Snapshot/restore/list/delete, vacuum and GC
   triggers, and restore status are management-scope RPCs (operator credential), joining
   the Gate G rebuild endpoint — which MOVES from the unauthenticated ops port to this
   surface (its reachable=trusted placement was explicitly interim). `nodb/smoke.sh`
   gains a snapshot→delete-repo→restore drill next to the rebuild drill.

## Branch

`nodb-phase5-ops` off `nodb-phase4-opensearch`.

## Prerequisite gates

| Gate | Deliverable | Verification | Est. |
|---|---|---|---|
| **P1** | **Single-snapshot audit (FINDINGS #1).** Enumerate every consecutive-`Tx.inTenantTx` pair on vacuum/GC/dump paths; classify each as safe or hazardous; add the checked one-snapshot read helper to the engine and convert hazardous sites. | Audit table recorded in this file; helper exists with tests; zero hazardous pairs remain on the paths this phase touches. | ~120k |
| **P2** | **The 3.5→4 upgrade path, proven** (Gate G residue). A tenant provisioned at Phase-3.5 schema (migrations 001–002, real content) upgrades in place: bootstrap applies 003, search index created, rebuild replays, smoke queries answer. | Scripted test from a 3.5-shaped volume; documented as the upgrade recipe in RUNNING.md. | ~100k |

## Gates

| Gate | Deliverable | Verification (all must hold) | Est. |
|---|---|---|---|
| **0** | Spikes + inventory, no production code. (a) Vacuum/GC call-site inventory (`VersionTableVacuumCommand`, both blob vacuums, `IsBlobUsedByVersionCommand`, `/system/vacuum` incl. its pre-existing `tasks`-param bug) and their SPI routing plan. (b) Manifest snapshot design: exact `COPY` sets per repo/tenant, metadata schema (new migration), incremental hash-set representation, outbox-position capture. (c) Restore state machine: in-place vs side-by-side steps, INDEXING status surfacing through `refresh`/queries, failure/rollback points. (d) GC reachability model: the precise reference sources (versions, manifests-in-horizon), grace-window bookkeeping, and the risk-#5 outbox-trim rule (cursor registry or checkpoint-floor). (e) Dump-format check against nodb values (Gate 0 of Phase 3 proved payloads byte-compatible; verify dump's version/binary enumeration on the SQL surfaces) + the `limit_number_of_versions` ordering decision. | Everything recorded in this file; migration draft for snapshot/GC metadata; the corpus/suites untouched. | ~350k |
| **A** | **Manifest snapshots (engine + wire):** per-repo and per-tenant snapshot under repeatable-read `COPY`; snapshot metadata + hash manifest persisted (new migration under P3 discipline); incremental against prior snapshots; list/delete with horizon accounting (decision 2). | Engine tests: snapshot under concurrent writers is consistent (no torn repo); incremental snapshot stores only delta hashes; dual-tenant isolation; horizon math tested at the boundaries. | ~500k |
| **B** | **Restore + the zero-downtime index driver:** in-place repo restore (partition DDL, `COPY` reload, rebuild); side-by-side restore under a surrogate `repo_key` + atomic swap (two mapping rows, one transaction, alias generation flip); the BUILDING-generation driver with atomic `updateAliases` (replaces Gate G's delete-then-recreate for all callers incl. the rebuild endpoint); INDEXING status honoured by `refresh(SEARCH)` and queries. | Restore drill in tests: snapshot → mutate → restore → byte-identical fingerprint (rows AND index); side-by-side swap leaves neighbours untouched (dual-repo assertions); a query during rebuild either waits or reports INDEXING — never silently partial; generation counter never reuses a number. | ~650k |
| **C** | **Vacuum + GC:** version retention (age/count policies, keyset-batched deletes); payload GC (mark via refcount incl. manifests, persisted grace window, sweep); binary GC on S3 (mark-and-sweep, same horizon; closes risk #14); `/system/vacuum` routed and its `tasks`-param bug fixed; all under P1's invariant. | GC never collects: a payload referenced only by a snapshot in horizon; a binary referenced only by an old version within retention. GC does collect provably-orphaned rows/objects (seeded). Mark/sweep interrupted mid-way is resumable and never double-frees. Growth counters from Phase 3's baseline shrink after vacuum on a seeded tenant. | ~600k |
| **D** | **Dump/load + the migration round trip:** dump verified in nodb mode (system + content repos); **ES-mode dump → nodb-mode load** round trip with suites/corpus spot-checks green on the loaded instance; load performance measured (streaming, not materialised); `limit_number_of_versions` re-enabled per Gate 0's ruling. | The round-trip instance passes the smoke flow + a corpus family subset; dump byte-compat asserted against blob-key/format expectations; load of a multi-repo dump lands repos, branches, versions, binaries, and rebuilds search. | ~500k |
| **E** | **Ops surface + drills + docs:** management-plane RPCs (snapshot/restore/list/delete/vacuum/GC/restore-status; rebuild endpoint moved here); `smoke.sh` snapshot→delete→restore drill; retention defaults documented; RUNNING.md operator recipes (snapshot, restore both modes, vacuum, GC, upgrade from P2); DESIGN.md §9 row. | Drill green in smoke.sh (orchestrator re-runs it); endpoint auth posture stated (operator scope pending Phase 6 — interim posture explicit, not accidental); docs current. | ~400k |

Total ≈ **3.2M** output tokens (P1+P2 ~220k, gates ~3.0M).

## Key risks carried into the gates

- **GC is the first code that can destroy data.** Every prior phase only added. The
  P1 invariant, the horizon rule and the resumable mark/sweep are the three legs; a gate
  is not green if any of them is asserted rather than tested.
- **Restore vs the refresh contract**: `awaitRefresh` during a rebuild must not deadlock
  against the INDEXING status (Gate B tests it directly under concurrent writers).
- **`COPY` under partition DDL**: side-by-side restore creates partitions for a surrogate
  `repo_key` while the parent is live — catalog-lock behaviour needs a spike in Gate 0(c).
- **Dump fidelity on binaries**: dumps reference binaries by blob key; the nodb path must
  stream them from S3 through the existing `BinaryService` without materialising.
- **Machine hygiene**: this phase's tests move real bulk data; the box's known
  memory-pressure flakiness applies double (stop idle daemons before long runs).

## Execution guidance

Same regime as Phases 0–4: agents build, orchestrator verifies (forced reruns,
freshness-asserted tallies per Gate F's process note, drills re-run independently, real
boot) and commits per gate; never commit red. One gate per session; A/B and C/D are
independently pauseable pairs. The corpus baseline is FROZEN (Gate F re-recorded it) —
Phase 5 adds no corpus rows except where Gate 0(e)'s ordering ruling requires pinning
dump enumeration.

## Definition of done

P1 audit clean and the helper in use; P2 upgrade recipe proven and documented; snapshot →
restore (both modes) drills green incl. the byte-identical fingerprint; the BUILDING
generation driver serves restore AND the rebuild endpoint; GC provably respects the
horizon and provably collects orphans; risk #14 closed; ES→nodb dump/load round trip
green; management-plane ops surface live with its interim auth posture stated; RUNNING.md
+ DESIGN.md updated; branch pushed.
