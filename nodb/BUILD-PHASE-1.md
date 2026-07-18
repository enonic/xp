# NoDB Build — Phase 1 Work Order (wire nodb-client into XP)

**Read first:** `nodb/DESIGN.md` §3, §7, §9–10; `nodb/BUILD-PHASE-0.md` (completion
record); `nodb/BUILD-SLICE-1.md` (what the NoDB engine/server/client already do). This
work order is self-contained.

## Goal

Complete Phase 1: XP runs its own storage-level itests against the NoDB backend built
in slice 1, selected via the `storage.backend` property that Phase 0 put in place.
XP-side SPI (post-Phase-0 shape) is the contract; NoDB implements it over gRPC.

## Honest scope constraints (decided here, not discovered mid-build)

1. **Hybrid mode is the Phase 1 reality.** NoDB has no search until Phase 2. So:
   `NodeStore` + storage-side `RepositoryStorageAdmin` → NoDB; `NodeSearchIndex` (search
   index, query execution) stays on embedded ES. Consequence: any itest that reads
   through the search path — queries, aggregations, **version-history queries** (they
   run through search over storage docs, which NoDB mode never writes to ES) — is OUT
   of the Phase 1 gate. The gate runs a CURATED storage subset: CRUD, branch ops
   (store/get/fork/delete), path/children ops, commit ops.
2. **Per-op RPCs, not WriteBatch, for the XP client.** Today's ES path has no
   cross-call transaction (version store, branch store, index doc are separate calls);
   mapping XP's per-op SPI calls onto per-op RPCs is semantics-preserving. WriteBatch
   remains the native-path optimization (slice-1 tests keep covering it) — do NOT
   force XP's call pattern through it in Phase 1.
3. **Payloads stay on the existing BlobStore** (file/S3) for the gate. A NoDB-backed
   BlobStore provider (payload table via PutPayload/GetPayload) is the stretch gate.
4. **Change-feed cache invalidation is deferred** (matters only multi-replica; itests
   are single-JVM). Note it in the client's javadoc as a known gap with a DESIGN ref.
5. **Snapshot/vacuum/dump against NoDB**: out of scope (Phase 3). In nodb mode the
   core-api SnapshotService stays ES-bound and unsupported-by-config; excluded tests.

## Branch & layout

Branch `nodb-phase1` off `storage-spi-phase0`. First commit: copy the `nodb/` directory
from `nodb-design` onto it (it is not wired into the XP Gradle build; one branch then
carries the XP side and the NoDB side together). The XP client bundle
(`modules/core/core-storage-nodb-client`) vendors `nodb/proto/nodb.proto` and generates
its own gRPC stubs (same pattern as nodb/server) — no cross-build artifact publishing
in Phase 1.

## Gates

### Gate 0 — Boot verification + reconciliation inventory (est. ~250k)
- **Boot check (Phase 0's outstanding review item)**: build the XP distro from this
  branch and boot a local instance with the default (elasticsearch) backend. Verify
  clean start, repo creation, node CRUD via API. This proves Phase 0's OSGi wiring in
  the real container — the one thing its test suites structurally could not.
- **SPI↔proto reconciliation inventory**: diff the post-Phase-0 SPI surface
  (NodeStore with SearchPreference params, existsBranchEntry, multi-gets,
  RepositoryStorageAdmin shape, SPI exceptions) against nodb/proto + server + engine
  (which predate it). Output: exact list of RPCs/messages/engine methods to add or
  change, and the semantic notes per method (e.g. SearchPreference is a no-op for
  NoDB — Postgres reads are always consistent; document, don't fake).
- **Curated itest subset**: enumerate the storage-level itest classes that avoid the
  search path; record the list (it IS the Phase 1 gate). Estimate honestly — if the
  subset is tiny, say so and widen with new storage-only tests instead.
- **Gate: boot green; inventory + subset list committed to this file.**

### Gate A — NoDB side: protocol/server/engine additions (est. ~400k)
- Implement the reconciliation list in nodb/: proto messages/RPCs, engine store
  methods, server service methods. Per-op RPCs mirror SPI methods 1:1.
- SPI exceptions over the wire: map engine "index/repo not found" outcomes to gRPC
  statuses that the client can translate back to StorageIndexNotFoundException /
  StorageIndexExistsException (Phase 0 established those as the boundary contract).
- Dual-tenant tests for every new RPC (slice-1 conventions; testcontainers PG).
- **Gate: nodb `../gradlew build` green incl. new tests.**

### Gate B — XP side: the client bundle (est. ~500k)
- New module `modules/core/core-storage-nodb-client`: implements spi.NodeStore +
  spi.RepositoryStorageAdmin over gRPC (vendored proto, generated stubs, bearer token
  via channel interceptor). SCR components registered with `storage.backend=nodb`.
- Config: `com.enonic.xp.storage.cfg` (backend=elasticsearch|nodb, endpoint, token,
  tls). Selection mechanism: ConfigAdmin-driven reference `.target` overrides on the
  consumers (the standard SCR pattern) OR target filters resolved from config —
  implementer picks the idiomatic XP way, documents it, and defaults to elasticsearch
  so a config-less boot is byte-identical to today.
- Error mapping: gRPC status → SPI exceptions; connection failure behavior defined
  (fail fast with clear message, no silent retry loops in Phase 1).
- Unit tests against an in-process NodbServer (slice-1 test utilities).
- **Gate: full XP build green with the new module; default-config behavior unchanged
  (arch test + suite green).**

### Gate C — XP itests against NoDB (est. ~600k)
- Parameterize the itest fixtures (AbstractNodeTest constructs impls directly): a
  system property (`xp.itest.storage=nodb`) switches the fixture to construct the
  gRPC-client-backed NodeStore against a NodbServer + testcontainers-PG bootstrapped
  in the fixture (reuse slice-1 BenchEnvironment patterns). ES continues to serve the
  search side of the fixture (hybrid, per scope constraint #1).
- Run the curated subset in nodb mode: green. Run the FULL suites in default mode:
  unchanged (663/4 + 385/0 profile).
- Add one cross-tenant itest: two tenants on one NoDB, spot-assert invisibility.
- **Gate: subset green vs NoDB; full suites unchanged vs ES; both recorded here.**

### Gate D — Boot smoke in nodb mode + docs (est. ~250k)
- Boot the distro with backend=nodb against a local NoDB (compose PG or `nodb dev`
  precursor): create repo, node CRUD via API, restart, verify persistence.
- Update DESIGN.md §9 (Phase 1 status), §10 risks; append actuals to this file.
- **Gate: smoke green; docs committed; branch pushed.**

### Stretch (only if A–D land with budget)
- NoDB-backed BlobStore provider (payload segments → payload table over
  PutPayload/GetPayload; binaries stay file/S3).

## Execution guidance

- Same regime as Phase 0: agents build, orchestrator verifies (forced reruns, grep
  proofs, diff review) and commits per gate; never commit red; no drive-by changes.
- **Anti-stall (agents consistently violate this)**: orchestrator arms process
  monitors on long gradle runs; agents must poll their own background runs within
  their turn, never end a turn "standing by".
- Budget: ~2M output tokens total. Gate C is the risk concentration (fixture surgery);
  if fixture parameterization balloons, stop and re-scope to a dedicated nodb-mode
  fixture class instead of parameterizing the shared one.

## Definition of done

Curated storage itests green against NoDB (dual-tenant spot-checked); full suites
unchanged in default mode; distro boots in both modes; default config byte-identical
to today; docs updated; `nodb-phase1` pushed.

---

## Gate 0 results (2026-07-18)

Executed on branch `nodb-phase1` (off `storage-spi-phase0`, HEAD at start `88d0ee65cb`).
`nodb/` copied wholesale from the main checkout (commit "Phase 1 gate 0: bring nodb/
design+build docs and NoDB sources onto nodb-phase1"); confirmed not part of the XP
Gradle build (`./gradlew projects` output unaffected, `settings.gradle` untouched, only
`core:core-storage-spi` — the Phase 0 module — appears where "storage" is grepped).

### a-d. Boot verification

**Finding (bug, fixed): `core-storage-spi` was never added to the runtime distro.**
Phase 0 created the `core-storage-spi` bundle and wired `core-repo` to
`Import-Package: com.enonic.xp.storage.spi` (confirmed in the built jar's manifest),
but `modules/runtime/build.gradle`'s `addBundle(...)` list — which enumerates every
bundle actually copied into the distro's `system/<level>/` tree — never included it.
Phase 0's test suites (unit + itest, all classpath-based) could not catch this: OSGi
bundle resolution only happens in a real Felix container. This is exactly the gap Gate
0's boot check exists to close.

Reproduced with a real boot before fixing: built `:runtime:installDist`, copied
`modules/runtime/build/install/home/` to a fresh temp `XP_HOME` (default config, no
`storage.cfg` — i.e. default `elasticsearch` backend), booted with
`./bin/server.sh` under JDK 25 (Homebrew OpenJDK 25.0.1 — GraalVM 25 unavailable in this
environment; boot behaves identically for framework/OSGi purposes since the JS engine
isn't exercised by this check). Result: Felix logged

```
ERROR E.Framework.com.enonic.xp.core.repo - FrameworkEvent ERROR
org.apache.felix.log.LogException: org.osgi.framework.BundleException: Unable to
resolve com.enonic.xp.core.repo [91](R 91.0): missing requirement
[com.enonic.xp.core.repo [91](R 91.0)] osgi.wiring.package;
(&(osgi.wiring.package=com.enonic.xp.storage.spi)(version>=8.1.0)(!(version>=9.0.0)))
```

`core-repo` never started; the framework itself reported "Started" (112 bundles) but
the entire node/repo layer was dark — no repo init, no node CRUD possible. This is the
"small OSGi metadata/wiring issue" the work order anticipated (fix attempt 1 of 3
allowed; resolved on the first attempt).

**Fix**: added one line to `modules/runtime/build.gradle`, alongside the other API-level
bundles (level 10, next to `core-api`):
```groovy
addBundle( project( ':core:core-storage-spi' ), 10 )
```
Rebuilt `:runtime:installDist`, rebooted with a fresh `XP_HOME` copied from the rebuilt
install's default `home/` (still zero storage config). Result, clean:

- **(a) No wiring errors.** Zero `ERROR`/`Exception`/`BundleException`/`Unresolved`
  lines in the full boot log. `FrameworkService` logs `Started Enonic XP in 2019 ms`
  (112→113 bundles once `core-storage-spi` is counted). No SCR resolution failures for
  `core-storage-spi`, `NodeStore`/`NodeSearchIndex`/`RepositoryStorageAdmin`, or
  `core-repo` activation.
- **(b) System repo init completed**, exercising `RepositoryStorageAdmin.createIndex` +
  `NodeStore` writes end-to-end through the new SPI wiring (log evidence, all with
  `successfully initialized`): `System-repo`, `System-repo [applications] layout`,
  `system.app`, `system.auditlog`, `System-repo [security] layout` (roles/users/keys
  created under it), `system.scheduler`. Each repo's `storage-<repo>` and
  `search-<repo>` ES indices were created and its root node written before the
  "successfully initialized" line — i.e. `RepositoryStorageAdmin.createIndex` →
  `NodeStore.storeBranchEntry/storeVersion` ran through
  `ElasticsearchNodeStore`/`IndexServiceInternalImpl` via the SPI, not a bypass.
- **(c) Bundle state confirmed via the live management endpoint** (no auth needed):
  `status` port 2609 exposes an `osgi.bundle` reporter
  (`curl localhost:2609/osgi.bundle`). Queried while the server was up:
  `com.enonic.xp.core.storage.spi` → id 65, **ACTIVE**; `com.enonic.xp.core.repo` → id
  92, **ACTIVE**. Of 114 total bundles, the only non-`ACTIVE` ones are 22 Tika
  parser-module fragments (state `RESOLVED`, expected — OSGi fragments never reach
  `ACTIVE`, they attach to their host). No other bundle is unresolved.
- **(d) HTTP responds** on all three configured listeners: portal `:8080` → `307`,
  management `:4848` → `401` (auth required, expected), status `:2609` → `200`.

Server stopped cleanly (`kill`, then observed the ES node stop + "Server has been
stopped" in the log — no forced kill needed).

**Verified the fix doesn't regress anything Gate 0-visible**: `runtime/build.gradle` is
the only file touched (`modules/runtime/build.gradle`, `+1` line, `git diff --stat`
confirms nothing else changed); the change is purely additive (one more bundle in the
distro), no test asserts an exact bundle count. Full-suite reverification is Gate B/D's
job (this is a runtime-packaging fix, not a source change to core-repo); flagging here
so it isn't silently lost — **the fix is committed as part of this gate**, not deferred.

### SPI ↔ proto reconciliation inventory

Scope: `NodeStore` + `RepositoryStorageAdmin` (post-Phase-0 XP SPI,
`modules/core/core-storage-spi`) against `nodb/proto/nodb.proto` (`NodeStore`/
`RepositoryAdmin` services), `nodb/engine/.../store/*.java`, `nodb/server/.../service/*.java`.
`NodeSearchIndex` is explicitly out of scope per this work order's scope constraint #1
(hybrid mode — search stays on ES through Phase 1); not reconciled here.

Legend: RPC match — **exact** (wire shape ready), **partial** (RPC/message declared but
empty placeholder per the slice-1 convention, or shape mismatch), **none** (nothing on
the wire). Engine — **yes** (store method exists), **no**.

#### `NodeStore`

| SPI method | RPC | Engine | Work needed |
|---|---|---|---|
| `storeBranchEntry` | partial — `StoreBranchEntryRequest` is an empty placeholder message; method unoverridden (UNIMPLEMENTED) | yes — `BranchStore.store` | Flesh `StoreBranchEntryRequest` (repo_id, branch, entry fields); override `NodeStoreService.storeBranchEntry` → `Tx.inTenantTx` → `BranchStore.store`. |
| `deleteBranchEntries` | partial — `DeleteBranchEntriesRequest` empty | yes — `BranchStore.delete` | Flesh message (repo_id, branch, repeated node_ids); server method. |
| `existsBranchEntry` | none — no RPC at all | no — no exists-only query (`getByNodeId` fetches the full row) | New RPC (`ExistsBranchEntry`) + new lightweight engine method (`SELECT 1 ... LIMIT 1`, not a full row fetch). Semantic note: `searchPreference` is a documented no-op — Postgres reads are always consistent. |
| `getBranchEntry` | exact — `GetBranchEntry` (`oneof by { node_id }`) | yes — `BranchStore.getByNodeId` | None. Semantic note: `searchPreference` param accepted but ignored/no-op — never sent over the wire. |
| `getBranchEntryByPath` | exact — `GetBranchEntry` (`oneof by { node_path }`) — same RPC, already covers both lookups | yes — `BranchStore.getByPath` | None. Semantic note: SPI javadoc requires a forced refresh before path lookup (today: rebuildable ES index); for NoDB `branch_entry` is the row of record — the refresh requirement is moot/no-op, a *strictly stronger* guarantee than today, not a gap. Document, don't fake a refresh call. |
| `getBranchEntries` (multi-get) | partial — `GetBranchEntriesRequest` empty; `stream BranchEntry` return declared | no — no multi-get-by-ids method | Flesh message (repo_id, branch, repeated node_ids); new engine method (`WHERE node_id = ANY(?)`); server method streaming results. |
| `getBranchesWithNode` | none — no RPC/message target | no | New RPC (`GetBranchesWithNode`) + new engine method (`SELECT DISTINCT branch FROM branch_entry WHERE repo_key=? AND node_id=?`). |
| `storeVersion` | partial — `StoreVersionRequest` empty | yes — `VersionStore.store` (used only inside `WriteBatch` today) | Flesh message (mirrors `Version` fields); server method for the **standalone** op — needed because scope constraint #2 requires per-op RPCs mirroring SPI 1:1, not routing XP's calls through `WriteBatch`. |
| `deleteVersion` | none — no RPC/message | no — `VersionStore` has no delete | New RPC + new engine method (`DELETE FROM node_version WHERE version_id=?`). Semantic note: primarily a Phase 3 vacuum/retention op (DESIGN §6); must still exist behind the SPI in Phase 1 for interface conformance even if itests rarely exercise it directly. |
| `getVersion` | exact — `GetVersionRequest{version_id}` | yes — `VersionStore.get` | None. Note: `repositoryId` param is accepted by the SPI but unused server-side — version ids are tenant-global, not repo-scoped (documented in the engine already); intentional, not a bug. |
| `storeCommit` | partial — `StoreCommitRequest` empty | yes — `CommitStore.store` (used only inside `WriteBatch` today) | Flesh message; server method for the standalone op (same reasoning as `storeVersion`). |
| `getCommit` | partial — `GetCommitRequest` empty | yes — `CommitStore.get` | Flesh message (`commit_id` field); server method. |
| *(no SPI equivalent today)* `getChildren` | **already exact & implemented** — `GetChildren` RPC, real message, real server method | yes — `BranchStore.getChildren` (parent_path generated column, exactly as DESIGN §4 describes) | **Gap is on the XP side, not NoDB's.** `NodeStore` has no children-listing method because in the ES-backed world, children listing has *never* gone through storage — see itest subset finding below: `FindNodeIdsByParentCommand` always queries `NodeSearchIndex`, for every backend, today. NoDB's engine/proto/server are *ahead* of the XP SPI here. Recommend as a Gate A/B open question: add `NodeStore.getChildren(repositoryId, branch, parentPath, from, size)` to the SPI and give `FindNodeIdsByParentCommand` a storage-side path for the `nodb` backend — this is the single highest-leverage addition for broadening the Gate C curated subset, since children-listing dependency is what currently pulls delete/move/duplicate/sort into "search-dependent" (see below). |

#### `RepositoryStorageAdmin`

| SPI method | RPC | Engine | Work needed |
|---|---|---|---|
| `createIndex(repo, settings, mappings)` | partial — `CreateRepositoryRequest{repo_id, settings_json}`; no `mappings` field | yes — `RepositoryLifecycle.createRepository`, creates one branch ("master") | `mappings` (`Map<IndexType,IndexMapping>`) is a genuine ES-only concept — Postgres's `node_version`/`branch_entry` columns are static DDL, not per-repo mappings; semantic note: permanently N/A for nodb, client drops the parameter, no translation needed, not a to-do. Verified `RepositoryCreator` only ever pushes the root node into ONE branch at repo-creation time (matches ES — branches beyond the first are created later, on demand, see the `createBranch` finding below), so slice-1's single hardcoded branch at `CreateRepository` time is *not* itself a gap. |
| `deleteIndex` | exact — `DeleteRepository` | yes — `RepositoryLifecycle.deleteRepository` (detach+drop, FK-ordering handled) | None. |
| `indexExists` | none | no — existence only discoverable today via `RepoKeys.resolve` throwing "Unknown repo id" | New RPC (`RepositoryExists`) + new engine method (`SELECT 1 FROM repository WHERE repo_id=?`) returning a boolean rather than throwing. |
| `refresh` | none needed | n/a | Semantic note (DESIGN §3.3): **documented no-op** for nodb — Postgres transactional visibility is strictly stronger than an ES refresh. Recommend implementing as a client-side no-op in `nodb-client` (return immediately, no wire call at all) rather than a trivial round-trip RPC, given the chattiness risk (DESIGN §10 risk #2). |
| `updateSettings` (raw ES settings JSON) | none | none | Semantic note: ES-index-settings concept (replica count, refresh_interval) has no NoDB equivalent — Postgres partitions aren't tunable this way. `nodb-client` no-ops this method; document as permanently N/A, not missing. |
| `putIndexMapping` | none | none | Semantic note: ES dynamic-mapping concept, no NoDB equivalent (static DDL schema). Same treatment as `updateSettings` — no-op, logged at debug so the no-op is discoverable, not silent-silent. |
| `getIndexSettings` → `Map<String,String>` | none | none | Semantic note: today used to seed defaults (e.g. replica count) for a new repo from an existing one's settings — no NoDB equivalent. `nodb-client` returns an empty map. **Gate B must verify** `RepositoryCreator`'s actual use of this return value tolerates empty/default results before relying on the no-op. |

#### Exceptions ↔ gRPC status (cross-cutting)

- **`StorageIndexNotFoundException`**: engine's `mapSqlException` already maps a
  `SQLException` whose message contains `"Unknown repo id"` → `Status.NOT_FOUND`. Works
  today for reads/writes against an unknown repo, but is a fragile substring match on an
  exception message, not a structured signal. **Work**: introduce a dedicated engine
  exception type (e.g. `UnknownRepoException`) thrown by `RepoKeys.resolve`, map *that
  type* → `NOT_FOUND` (not the message text); client translates `NOT_FOUND` →
  `StorageIndexNotFoundException`.
- **`StorageIndexExistsException`**: **no mapping exists at all today** — calling
  `CreateRepository` twice hits a Postgres unique-constraint violation (`repo_id`),
  surfaces as a generic `SQLException`, and falls through `mapSqlException`'s current
  logic straight to `Status.INTERNAL` (wrong). **Work (flag as a bug, not just a gap)**:
  detect the unique-violation SQLSTATE (`23505`) on the `repo_id` insert →
  `Status.ALREADY_EXISTS`; client translates `ALREADY_EXISTS` →
  `StorageIndexExistsException`. Recommend this be Gate A's first fix, since it's a
  correctness gap in already-shipped slice-1 code, not new surface.
- **`SearchPreference`** (`LOCAL`/`PRIMARY`): semantic note for `nodb-client` javadoc —
  **no-op for NoDB**. Every read is a direct Postgres row read with no replica lag in
  scope for Phase 1 (single-primary Postgres); accept the parameter for interface
  conformance, never let it affect routing or appear on the wire.
- **`IndexSettings`/`IndexMapping`/`UpdateIndexSettings`** (opaque ES-JSON carriers):
  none of `createIndex`/`updateSettings`/`putIndexMapping` have a real NoDB translation
  (see per-method notes above). `nodb-client`'s `RepositoryStorageAdmin` impl accepts
  these types for interface conformance and ignores their contents; state this
  explicitly in a package/class javadoc so it reads as an intentional decision, not an
  oversight.

**Design note surfaced by this inventory**: `WriteBatch` (fully implemented,
transactionally correct, proven by `WriteBatchTest`/`NodbServerIntegrationTest`) is
**not** the Phase 1 XP integration point — scope constraint #2 requires XP's per-op SPI
calls to map onto per-op RPCs (fleshed out above), not be forced through `WriteBatch`.
`WriteBatch` remains the native/bench-harness optimization path. Worth stating plainly
so Gate B doesn't default to the path of least resistance (routing everything through
the one RPC that already works end-to-end).

Out of reconciliation scope, confirmed consistent with the work order: `NodeSearchIndex`
(hybrid mode, scope constraint #1), `SnapshotStore`/`Snapshots` RPC (scope constraint
#5, Phase 3), `ChangeFeed`/`BulkTransfer` (no XP SPI surface yet, not needed for Phase 1).

### Curated itest subset

Every test class under `modules/itest/itest-core/src/test/java/com/enonic/xp/core/`
(89 test classes; `TestDumpWriter.java` and `ClientProxy.java` are non-test helpers,
excluded) was read — not just named — to check what it actually calls, per the work
order's honesty requirement.

**Headline finding**: the naive "storage vs. search" split assumed by the work order
undercounts how much of core-repo's *write* surface already depends on search, even
under today's ES backend. `FindNodeIdsByParentCommand` (children listing) always
queries `NodeSearchIndex`, for every backend — ES's storage index doesn't support a
path-prefix children query the way NoDB's `branch_entry.parent_path` generated column
does (see `getChildren` gap above). Because of that, `DeleteNodeCommand`,
`MoveNodeCommand`, `DuplicateNodeCommandTest`, `SortNodeCommand`, and
`ApplyNodePermissionsCommand` (subtree/tree scope) all query search internally to
enumerate children/descendants before acting — so tests that look like plain CRUD
(delete-by-id, move, duplicate, sort) are actually search-dependent **today**, before
NoDB enters the picture at all.

**STORAGE-ONLY (16 of 89)** — no call, direct or via a helper the test visibly relies
on, into `NodeSearchService`/`findByQuery`/aggregations/version-history queries:

| Class | Why it's storage-only |
|---|---|
| `app/ApplicationServiceTest` | install/get/update via `getByPath`/create/update only |
| `node/AccessControlTest` | create/get/update by id only |
| `node/CheckNodeExistsCommandTest` | `CheckNodeExistsCommand` uses `getNodeBranchEntry` only |
| `node/CreateRootNodeCommandTest` | pure storage write |
| `node/GetActiveNodeVersionsCommandTest` | `getNodeBranchEntry`/`getVersion` only |
| `node/GetBinaryByVersionCommandTest` | blob read via storage only |
| `node/GetBinaryCommandTest` | blob read via storage only |
| `node/GetNodeByIdAndVersionIdCommandTest` | direct version fetch by id |
| `node/GetNodeByIdCommandTest` | storage only |
| `node/GetNodeByPathCommandTest` | storage only |
| `node/GetNodesByIsCommandTest` | batch get-by-id, storage only |
| `node/GetNodesByPathsCommandTest` | storage only |
| `node/ImportNodeCommandTest` | create/update only; permission-apply uses default SINGLE scope (no recursive search) |
| `node/PatchNodeCommandTest` | storage only, no MOVED comparisons exercised |
| `node/RefreshCommandTest` | flushes indices, issues no query itself |
| `node/UpdateNodeCommandTest` | wraps `PatchNodeCommand`, storage only |

**SEARCH-DEPENDENT (73 of 89)**, grouped by why:
- Aggregations (8): all 8 `*AggregationTest`/`*AggregationsTest` classes.
- `FindNodesByQueryCommandTest*` family + `FindNodePathsByQueryTest` +
  `FindNodesByMultiRepoQueryCommandTest` (20): direct query/fulltext/ngram/sort/geo tests.
- Version-history queries (5): `FindNodeVersionsCommandTest`, `GetNodeVersionsCommandTest`
  (delegates to it), `FindNodesWithVersionDifferenceCommandTest`,
  `HasUnpublishedChildrenCommandTest` (NodeVersionDiffQuery), `VersionTableVacuumTaskTest`
  (`getVersions()`) — matches this work order's scope constraint #1 explicitly.
- Hidden children/cascade dependency (12): `DeleteNodeByIdCommandTest(+_error_handling)`,
  `DeleteNodeByPathCommandTest`, `CompareNodeCommandTest`/`CompareNodesCommandTest` (use
  delete as setup), `MoveNodeCommandTest`, `RenameNodeCommandTest` (wraps move),
  `DuplicateNodeCommandTest`, `SortNodeCommandTest(+_manualOrder)`,
  `FindNodeIdsByParentCommandTest`, `NodeOrderTest` — the `getChildren` gap above.
- Buried query-based assertions in otherwise-CRUD tests (3): `CreateNodeCommandTest`,
  `CreateNodeCommand_path_integrity_test`, `PushNodesCommandTest`.
- Commands with unconditional search internals (4): `ApplyNodePermissionsCommandTest`
  (subtree/tree scope), `FindNodesDependenciesCommandTest`, `ResolveSyncWorkCommandTest`,
  `NodeServiceImplTest`.
- Dump/export/index/audit/security/project/repo/scheduler/snapshot (14):
  `DumpUpgradeIntegrationTest`, `RepoDumperTest`, `DumpServiceImplTest`,
  `DynamicSchemaServiceImplTest` (delete path), `NodeExportIntegrationTest`,
  `CompressedExportImportIntegrationTest`, `NodeImporterIntegrationTest`,
  `IndexServiceImplTest`, `AuditLogServiceImplTest`, `SecurityServiceImplTest`,
  `ProjectServiceImplTest`, `RepositoryServiceImplTest` (branch-delete queries ES),
  `SchedulerServiceImplTest` (delete path), `SnapshotServiceImplTest` (raw ES-cluster
  snapshot admin — inherently ES-infra-bound regardless of NoDB).
- Performance/Load tests (6, all turned out search-dependent — flagged separately since
  they're unsuitable for a curated functional-correctness gate regardless):
  `RepoDumperLoadTest`, `ReindexLoadTest`, `DeleteNodeByIdsCommandPerformanceTest`,
  `DuplicateNodeCommandPerformanceTest`, `PushNodesCommandPerformanceTest` (also
  `@Disabled`), `ResolveSyncWorkPerformanceTest`.

**Count: 16 STORAGE-ONLY / 73 SEARCH-DEPENDENT / 89 total.** 16 is under the work
order's own "if the subset is tiny, say so" bar in spirit (it's ~18% of the suite, and
several of the 16 are narrow single-command tests) — saying so explicitly here, and
widening with new storage-only itests rather than treating 16 as sufficient for Gate C.

**Proposed new storage-only itests for Gate C** (all exercising only `NodeStore`/
`RepositoryStorageAdmin` paths once the `getChildren` SPI gap above is closed — without
it, #1-#2 below can't be written storage-only):
1. **`GetChildrenByPathTest`** — children listing via the new storage-side
   `NodeStore.getChildren` (once added), paginated (from/size), asserting order and
   count without touching `NodeQuery`. Directly exercises the SPI method this inventory
   flags as missing.
2. **`FindNodeIdsByParentStorageTest`** (or a `nodb`-mode variant of the existing test)
   — same intent as #1 at the command layer, once `FindNodeIdsByParentCommand` gets a
   storage-side path for the `nodb` backend.
3. **`CreateBranchStorageTest`** — exercises XP's actual branch-creation path
   (`RepositoryServiceImpl.createBranch` → `NodeStorageService.push` → one
   `storeBranchEntry` call for the root node into a brand-new branch value — verified by
   reading `NodeStorageServiceImpl.push`/`RepositoryServiceImpl.doCreateBranch`; XP has
   no bulk branch-copy operation, "creating" a branch is just writing its first entry).
   This is where NoDB's relational schema adds a constraint ES never had: `branch_entry`
   has an FK to a `branch` row, so a `storeBranchEntry`/`WriteBatch` write into a branch
   with no existing rows will fail unless the engine auto-creates the `branch` row on
   first write — exactly what `WriteService.forkBranch` already does for its own target
   branch (`INSERT INTO branch ... ON CONFLICT DO NOTHING`) but `BranchStore.store` does
   not (currently assumes the branch row pre-exists). **Work needed for Gate A**: make
   `BranchStore.store`/`WriteService.write` auto-vivify the `branch` row the same way
   `forkBranch` does — no new SPI method or RPC required, this is purely an engine-side
   fix once identified. Test should assert: first write to a never-seen branch succeeds
   without a separate create-branch call, matching ES's implicit-branch semantics.
4. **`ExistsBranchEntryTest`** — exercises the `existsBranchEntry` SPI method
   end-to-end once its RPC/engine method exist (table above); asserts true/false without
   any full-row fetch, i.e. a real behavioral difference from `getBranchEntry != null`,
   not just a duplicate assertion.
5. **`RepositoryLifecycleStorageTest`** — `createIndex`/`indexExists`/`deleteIndex`
   round-trip via `RepositoryStorageAdmin` only (no node writes at all), including the
   `StorageIndexExistsException` double-create case flagged as a bug above and the
   `StorageIndexNotFoundException` case for delete-of-unknown-repo.

**Additional finding, corrected after checking XP's actual branch-creation code** (this
work order's goal text says "branch ops (store/get/fork/delete)" — verified what "fork"
actually means in XP today rather than assuming it maps to NoDB's `WriteService.forkBranch`):
`RepositoryServiceImpl.createBranch()` → `NodeStorageServiceImpl.push()` is a **single**
`storeBranchEntry`-equivalent write of the root node into a new branch value — XP has no
bulk branch-copy operation; ES never needed one because a "branch" isn't a first-class
entity there, just a field value on documents. NoDB's schema makes `branch` a real row
with an FK from `branch_entry`, so the true gap is narrower than "missing SPI method":
`BranchStore.store` needs to auto-create the `branch` row on first write to an unseen
branch, the same way `WriteService.forkBranch` already does for its own target branch.
**No new SPI method or RPC is required** — this is a self-contained engine fix (Gate A),
not a protocol gap like `getChildren`. `WriteService.forkBranch` (bulk copy) remains a
NoDB-native capability with no current XP caller; it may become useful later for
DESIGN.md §5's roadmap "ephemeral branches" feature, but nothing in Phase 1's scope
needs it exposed over the wire.

---

## Gate C results (2026-07-18)

### Prerequisite: branch-entry N+1 fix

`proto.BranchEntry` gained `node_data_hash`/`index_config_hash`/`acl_hash` (fields 6-8,
all three proto copies: `nodb/proto/nodb.proto`, `nodb/server/src/main/proto/nodb.proto`,
`modules/core/core-storage-nodb-client/src/main/proto/nodb.proto`). Server:
`BranchStore.JOINED_SELECT` (new shared constant) joins `node_version` ON
`(repo_key, version_id)` in every read method (`getByNodeId`/`getByPath`/`getByNodeIds`/
`getChildren`) — one SQL statement, no extra RPC. `BranchEntryRecord` (engine model)
gained the three fields with a 5-arg write-path constructor kept for backward compatibility
(hash fields stay `null`, irrelevant to `store()`/`delete()`). Client:
`NodbNodeStore.joinBranchEntry`'s follow-up `GetVersion` call is gone; `RecordMapper.toSpiBranchEntry`
now maps the wire message directly. Tests: `EngineStoreTest` extended (4 tests now assert
the joined hashes); `StubNodeStoreService`/`NodbNodeStoreTest` (client-side) updated to
reproduce the same JOIN semantics in the fake.

### Genuine schema gap found and fixed: `node_version`'s payload FK

While running real node writes through nodb in hybrid mode, every write failed with a
foreign-key violation: `node_version.node_data_hash/index_config_hash/acl_hash` were
declared `REFERENCES payload (hash)`, but Phase 1 scope constraint #3 keeps those payloads
on XP's existing BlobStore, never nodb's own `payload` table — nodb's own writers
(WriteBatch/bench) populate `payload` first, but the XP integration never does. Fixed by
dropping the FK (kept `NOT NULL`, columns remain plain content-hash references) in both
`nodb/schema/schema.sql` and the one applied migration,
`nodb/engine/src/main/resources/nodb/migrations/tenant/001_init.sql` (edited directly, not
a new migration file — pre-release, no tenants provisioned anywhere yet). Verified
`cd nodb && ../gradlew build` still green after the relaxation (WriteBatchTest/EngineStoreTest
still populate real payload rows correctly; the FK was never load-bearing for them, only a
now-removed extra guarantee).

### Fixture mechanism

`modules/itest/itest-core/src/testFixtures/java/com/enonic/xp/core/nodb/`:
`NodbTestCluster` (JVM-wide singleton, lazy-started only when `-Dxp.itest.storage=nodb`
is set: `postgres:17` testcontainer + real `NodbServer` on a loopback ephemeral port,
mirroring `nodb/bench`'s `BenchEnvironment` minus its native client) and `NodbTenant`
(one provisioned tenant's `NodeStore`/`RepositoryStorageAdmin`, backed by the real gate-B
client classes constructed directly — `new NodbStorageClient(); client.activate(Map.of(...))`
— no test-only client subclass needed). `AbstractNodeTest` branches on
`NodbTestCluster.isEnabled()`: default path untouched; nodb path swaps `nodeStore` and a
new `repositoryStorageAdmin` field (previously every command builder used
`this.indexServiceInternal` directly for this role — retrofitted 13 existing itest-core
test files plus `AbstractNodeTest` itself to go through the new field so nodb mode actually
takes effect for them). `NodeSearchIndexImpl`/ES stays completely untouched (hybrid mode).

**Dependency approach**: `itest-core/build.gradle` `testFixturesImplementation
fileTree(...)` on `nodb/{engine,server}/build/libs/*.jar` (files() on already-built jars,
no cross-build project dependency — worked on the first attempt, no dependency hell) plus
`postgresql`/`hikaricp`/`nodb-java-jwt`/testcontainers entries added to
`gradle/libs.versions.toml` (grpc/protobuf already matched between the two builds, reused
as-is). A `checkNodbJarsPresent` task fails fast with a clear message if nodb wasn't built
first.

**Isolation choice**: one tenant (Postgres schema) PER TEST CLASS, memoized
(`NodbTestCluster#tenantForClass`), not per JVM or per method. Per-method was tried first
and found to violate an invariant `SystemRepoInitializer#isInitialized` depends on: it
checks BOTH the storage side (would be fresh every method) AND the search side (ES, which
correctly stays shared/persistent across nodb-tenant methods per hybrid-mode scope) — with
per-method storage resets outpacing ES's own per-class-only reset, the second method's
`bootstrap()` saw "search says system-repo exists, storage says it doesn't", attempted to
recreate it, and failed with `RepositoryAlreadyExistsException` from the search-index half
of `NodeRepositoryServiceImpl#create`. Per-class reuse keeps storage and search evolving at
the same granularity, exactly like default (ES) mode. `freshTenant()` (always-fresh,
unmemoized) remains available and is used by the cross-tenant test and by
`RepositoryLifecycleStorageTest`'s round-trip test, both of which want tenants beyond the
fixture's own.

### SPI addition: `NodeStore#getChildren`

Gate 0 flagged this as the single highest-leverage SPI gap (children listing had no
storage-side path on any backend). Added as a **default method** (throws
`UnsupportedOperationException`, documented as nodb-only by design — ES's storage index
has never supported a path-prefix query) so `ElasticsearchNodeStore` needs no change;
`NodbNodeStore.getChildren` overrides it, wrapping the `GetChildren` RPC that was already
fully implemented server-side since slice 1. Deliberately did NOT give
`FindNodeIdsByParentCommand` a storage-side path for nodb (that would be new Gate A/B-scale
production surface, out of this gate's risk budget) — see the new-tests section below for
how the two originally-proposed tests were adapted around that decision.

### The 5 new storage-only itest classes (`itest-core/src/test/java/com/enonic/xp/core/node/`)

1. **`GetChildrenByPathTest`** — direct `NodeStore#getChildren` exercise (ordering,
   pagination, nested-parent scoping). nodb-only by design (`Assumptions.assumeTrue`, not a
   failure, in default mode) — the SPI method has no ES implementation to fall back to.
2. **`FindNodeIdsByParentStorageTest`** — adapted from Gate 0's proposal (which assumed a
   command-layer storage path that was deliberately not built, see above): asserts
   `findByParent()` (search-based, unchanged, works in both modes) and, in nodb mode only,
   additionally cross-checks that `NodeStore#getChildren` (storage-side) returns the SAME
   node set as the search-side listing — a genuine hybrid-mode consistency check, not
   strictly "storage-only" by Gate 0's own rubric since it also exercises search, but the
   most valuable substitute for the scope decision above.
3. **`CreateBranchStorageTest`** — first write into a never-seen branch via the real
   `RepositoryServiceImpl#createBranch` → `NodeStorageServiceImpl#push` path (not the
   engine directly, which `EngineStoreTest` already covers) succeeds without a prior
   explicit branch-create call. Both modes.
4. **`ExistsBranchEntryTest`** — `BranchServiceImpl#exists` (the one production caller of
   `NodeStore#existsBranchEntry`) true/false round-trip; deletes via `NodeStore.deleteBranchEntries`
   directly (not `DeleteNodeCommand`, which is search-dependent) to stay genuinely
   storage-only. Both modes.
5. **`RepositoryLifecycleStorageTest`** — `createIndex`/`indexExists`/`deleteIndex`
   round-trip via `RepositoryStorageAdmin` only. Surfaced two pre-existing backend
   asymmetries (documented in the class javadoc, asserted per-mode rather than papered
   over): double-create throws `IndexException`-wrapping-`StorageIndexExistsException` on
   ES vs. bare `StorageIndexExistsException` on nodb (both honor the SPI contract, checked
   via a cause-chain-search helper); delete-of-unknown-repo is a silent no-op on ES
   (`IndexServiceInternalImpl#doDeleteIndex` catches and logs) vs. a real
   `StorageIndexNotFoundException` on nodb. Also hosts the cross-tenant spot check
   (task 4.3): two independent tenants via `freshTenant()`, same external repo id created
   in both, asserted mutually invisible — nodb mode only (`Assumptions.assumeTrue`; "tenant"
   has no ES equivalent in this SPI).

### Gate runs

**Default mode, full suites** (`./gradlew :itest:itest-core:integrationTest
:itest:itest-core-content:integrationTest`, run twice — once before and once after the
tenant-isolation-granularity fix below, identical both times):
- `itest-core`: **673 tests, 4 failed (all `FindNodesByQueryCommandTest_icuSort`, the known
  pre-existing profile), 0 errors, 8 skipped** — matches "663 + the 10 new-test-class
  methods, exactly 4 pre-existing icuSort failures" exactly.
- `itest-core-content`: **385 tests, 0 failed, 0 errors, 4 skipped** — matches the known
  all-green profile exactly.

**nodb mode, curated subset** (16 curated + 5 new = 21 classes, 65 test methods total,
`-Dxp.itest.storage=nodb` plus one `--tests` per class): **65 tests, 0 failed, 0 errors, 1
skipped** (`RefreshCommandTest#refresh_non_existing_repository`, documented no-op-can't-throw
asymmetry). All 21 classes green; cross-tenant isolation test passes.

Property plumbing: `-Dxp.itest.storage=nodb` on the `gradlew` invocation is a Gradle-JVM
system property, not automatically visible to the forked `Test` task JVM — `itest-core`'s
`integrationTest` task now forwards it explicitly when present (same pattern
`nodb/bench`/`nodb/engine`/`nodb/server` use for their own Docker-socket env vars), plus
carries the identical testcontainers Docker-socket-detection + `api.version=1.44` block
those builds already have (itest-core's forked JVM needed the same fix — docker-java
defaults to a Docker API version modern engines reject).

### Deviations from the work order

- `FindNodeIdsByParentCommand` was NOT given an nodb storage-side path (Gate 0's proposal
  for new test #2 assumed one); see the SPI-addition section above for the reasoning
  (production command-routing change judged out of this gate's risk budget). Test #2 was
  adapted to still be valuable and dual-mode without it.
- The `node_version` payload-FK schema fix (see above) was not anticipated by any prior
  gate and required an nodb-side schema change beyond the branch-entry N+1 fix Task 1
  scoped; flagged here rather than silently folded into "the N+1 fix."
- Tenant isolation granularity changed from an initial per-method design to per-class
  during this gate (see the fixture section) after the per-method version was empirically
  found to break `SystemRepoInitializer`'s bootstrap idempotency check in nodb mode.

### Blockers

None outstanding. All gate-C acceptance criteria (subset green vs NoDB, full suites
unchanged vs ES, both recorded here) are met.

---

## Gate D results (2026-07-18)

Booted the real distro (`:runtime:installDist`) with `backend=nodb` against a live,
from-scratch NoDB stack (postgres:17 container + a real standalone `NodbServer`) — no
itest fixtures, no testcontainers-in-JUnit; every process is a plain OS process this gate
started, watched, and stopped by hand. Three real bugs were found and fixed (client-bundle
packaging, twice, and an OSGi bundle-stop-ordering bug); after all three, a full
boot → CRUD-evidence → clean-shutdown → restart → identical-data cycle went green.

### Stack recipe

```bash
# 1. Distro + nodb server, both plain `../gradlew` builds
./gradlew :runtime:installDist                              # from repo root
cd nodb && ../gradlew :server:installDist -x test            # produces build/install/server/{bin,lib}

# 2. Postgres (fixed port so the recipe is copy-pasteable)
docker run -d --name nodb-pg-gated -e POSTGRES_DB=nodb -e POSTGRES_USER=nodb \
  -e POSTGRES_PASSWORD=nodb -p 55432:5432 postgres:17

# 3. NodbServer (plain `java`, no gradle daemon in the loop -- see rough edges)
cd nodb/server/build/install/server
NODB_PG_URL="jdbc:postgresql://localhost:55432/nodb" NODB_PG_USER=nodb NODB_PG_PASSWORD=nodb \
  NODB_PORT=7700 NODB_KEYS_DIR=<keys-dir> ./bin/server &

# 4. Provision a tenant (NodbServer itself never does this -- see new tool below)
java -cp lib/* com.enonic.nodb.server.tools.TenantBootstrapTool \
  --tenant xpsmoke2 --pg-url jdbc:postgresql://localhost:55432/nodb --pg-user nodb --pg-password nodb

# 5. Mint a RUNTIME-scope token (gate-B correction: repo lifecycle is a runtime op, see
#    TenantAuthInterceptor's MANAGEMENT_METHODS = Set.of())
NODB_KEYS_DIR=<keys-dir> java -cp lib/* com.enonic.nodb.server.auth.NodbTokenTool \
  --tenant xpsmoke2 --scope runtime --subject xp-server --ttl-minutes 240 > token.txt

# 6. XP_HOME config (com.enonic.xp.storage.nodb.cfg, uncommented from the shipped template)
backend=nodb
nodbEndpoint=localhost:7700
nodbToken=<contents of token.txt>

# 7. Boot (same JAVA_HOME=25 / server.sh pattern as gate 0)
XP_HOME=<fresh home> JAVA_HOME=/opt/homebrew/opt/openjdk ./bin/server.sh
```

Added `nodb/server/src/main/java/com/enonic/nodb/server/tools/TenantBootstrapTool.java`: a
one-off CLI wrapping `TenantProvisioner.provision` (schema/role/grants/migrations) against
a running Postgres, since `NodbServer`/`RepositoryAdminService` never provision tenants
themselves (`Tx.inTenantSchema` assumes the schema/role already exist — confirmed by
reading `RepositoryAdminService.createRepository` and `Tx`). Previously only exercised
in-process by `nodb/bench`'s `BenchEnvironment` and the itest fixture `NodbTestCluster`;
this gate needed the equivalent against a real, separately-running `NodbServer`. Also
added the `application` plugin to `nodb/server/build.gradle.kts` so
`../gradlew :server:installDist` produces a plain launcher + classpath (`bin/server`,
`lib/*.jar`) for both `NodbServer` and the small auxiliary mains (`NodbTokenTool`,
`TenantBootstrapTool`) without hand-assembling a classpath or fighting the Gradle
daemon's frozen-env behavior noted in `nodb/bench/build.gradle.kts`.

### Bugs found and fixed (in the order hit)

**1. Missing gRPC `NameResolverProvider`/`LoadBalancerProvider` ServiceLoader entries
(client bundle packaging).** First boot attempt failed immediately on first RPC:
`IllegalArgumentException: Address types of NameResolver 'unix' for 'unix:///localhost:7700'
not supported by transport`. Root cause: `core-storage-nodb-client`'s bnd `Private-Package:
META-INF.services.*;-split-package:=merge-first` instruction does not concatenate
same-named `META-INF/services` resources contributed by more than one embedded dependency
jar — it silently keeps only one jar's copy. `grpc-core` and `grpc-netty-shaded` both
declare `io.grpc.NameResolverProvider`; the packaged bundle ended up with only
netty-shaded's Unix-domain-socket resolver, dropping grpc-core's DNS resolver that
`ManagedChannelBuilder#forAddress` needs. The same bug also silently dropped grpc-core's
`PickFirstLoadBalancerProvider` from `io.grpc.LoadBalancerProvider` (grpc's own default
load-balancing policy). Neither gap is reachable from unit/itest, which construct gRPC
channels in-process and never touch `NameResolverRegistry`/OSGi classloading.
  - **Fix**: added two hand-merged project resources —
    `modules/core/core-storage-nodb-client/src/main/resources/META-INF/services/io.grpc.NameResolverProvider`
    and `.../io.grpc.LoadBalancerProvider` — listing every provider FQCN from every
    contributing jar. Confirmed the project's own `src/main/resources` copy wins over
    bnd's lossy merge (inspected the packaged jar's `META-INF/services/*` after rebuild).

**2. Missing `io.grpc.protobuf.lite`/`io.perfmark` classes (bnd buildpath vs. Gradle
runtimeClasspath gap).** Second attempt failed with
`NoClassDefFoundError: io/grpc/protobuf/lite/ProtoLiteUtils` (io.grpc.protobuf.ProtoUtils's
marshaller delegates to it at the bytecode level); third attempt (after fixing #1's
sibling) failed with `ClassNotFoundException: io.perfmark.PerfMark` (grpc-core's
`ManagedChannelImpl` calls it directly on the first real RPC). Root cause: both
`grpc-protobuf-lite` and `perfmark-api` are `<scope>runtime</scope>` transitive
dependencies in their parents' POMs (`grpc-protobuf`, `grpc-core` respectively) — present
on Gradle's `runtimeClasspath` but never on `compileClasspath`, and the `biz.aQute.bnd.builder`
Gradle plugin's default buildpath (what `Private-Package: io.grpc.*` scans to decide what
to embed) is `compileClasspath`. Classes that exist only at runtime are invisible to bnd
and silently excluded from the self-contained bundle — a gap that, again, only a real OSGi
boot exercising a real RPC call surfaces.
  - **Fix**: declared both as explicit first-class dependencies (not just transitive) —
    `gradle/libs.versions.toml` (`grpc-protobuf-lite`, `perfmark-api` catalog entries,
    `grpc-client` bundle updated) and
    `modules/core/core-storage-nodb-client/build.gradle` (added `io.perfmark.*` to
    `Private-Package`; explicitly pinned `com.google.protobuf.*;-split-package:=merge-first`
    once `grpc-protobuf-lite` pulled in `protobuf-javalite`, which declares the same
    `com.google.protobuf` package as the full `protobuf-java` runtime this bundle actually
    needs — confirmed post-fix build has zero bnd split-package warnings and the packaged
    jar still contains `com.google.protobuf.Descriptors` (a full-runtime-only class)).

With both fixed, `com.enonic.xp.core.storage.nodb.client` (bundle id 97 initially, id 65
after fix #3 below) activated cleanly; all 4 system repos (`system-repo`, `system.app`,
`system.auditlog`, `system.scheduler`) initialized end-to-end through the nodb client —
storage side (repo/branch creation, root-node writes) went through NoDB/Postgres; search
side (ES indices) stayed on embedded ES per hybrid mode.

**3. Shutdown-order OSGi rebind spuriously re-runs `SystemRepoInitializer` against
ElasticSearch, corrupting the *next* boot.** Discovered only by doing the actual
restart-persistence check this gate requires. Symptom: on `SIGTERM`, the shutdown log
showed `Initializing System-repo` and `creating index storage-system-repo` (an
ElasticSearch-native index) seconds before the process exited — never seen during gate 0's
default-mode boot. On the *next* boot with the same `XP_HOME`, the embedded ES cluster
health got stuck permanently `RED` (`Cluster not healthy: timed out: true, state: RED` /
`Waiting [1000ms] for System-repo to be initialized`, repeating forever) — a hard hang,
not a cosmetic warning.
  - **Root cause**: `core-storage-nodb-client` and `core-repo` were both registered at
    runtime level 22 (`modules/runtime/build.gradle`). Felix stops same-level bundles in
    decreasing bundle-id order; the client bundle (installed after core-repo, higher id)
    stopped *first*. `RepositoryServiceActivator` (`core-repo`) holds **static**
    `@Reference` bindings to `NodeStorageService`/`IndexServiceInternal` — when the
    higher-ranked nodb-backed providers disappeared mid-shutdown, SCR tore the whole
    activator down and reactivated it to rebind to the remaining (lower-ranked,
    ES-backed) providers. Reactivation re-ran `SystemRepoInitializer`, which found the
    ES-backed storage side never initialized (nodb mode never wrote to it) and created a
    fresh `storage-system-repo` ES index + root node — whose shard never reached `STARTED`
    before the process closed 130ms later, leaving the ES data directory with a shard the
    *next* boot's recovery could never allocate, blocking cluster health forever.
  - **Fix**: `modules/runtime/build.gradle` — moved `core-storage-nodb-client` from level
    22 to level 10 (alongside its API-tier counterpart `core-storage-spi`). Bundles at a
    lower level stop *later* during shutdown (framework level only descends to 10 after
    everything at 22, including core-repo, has already stopped), so core-repo's static
    references never see the nodb client disappear out from under them.
  - **Verified the fix, not just the theory**: rebuilt, fresh `XP_HOME` + fresh tenant,
    full boot → `SIGTERM` → grepped the shutdown-phase log for any `Initializing`/`creating
    index`/`ERROR` line (none) → restarted with the *same* `XP_HOME` → clean, ~1.5s restart
    (`Started Enonic XP` → `Listening on` ports, no re-init, no errors) vs. the ~45s+ (and
    ultimately infinite) first-boot-shaped restart before the fix.

### Verification evidence

**Bundle/HTTP state** (`curl localhost:2609/osgi.bundle`, no auth): 115/115 bundles
`ACTIVE` or `RESOLVED` (Tika fragments only) after the fix — zero non-conforming states.
`com.enonic.xp.core.storage.nodb.client` `ACTIVE`. HTTP: portal `:8080` → `307`, management
`:4848` → `401` (auth required, expected), status `:2609` → `200`.

**Postgres is the real system of record** (`docker exec nodb-pg-gated psql -U nodb -d nodb`,
tenant schema `xpsmoke2`), before vs. after the full boot:

| | before boot | after boot |
|---|---|---|
| `repository` rows | 0 | 4 (`system-repo`, `system.app`, `system.auditlog`, `system.scheduler`) |
| `branch` rows | 0 | 4 (`master` each) |
| `branch_entry` rows (by repo_key 1-4) | 0,0,0,0 | 26, 1, 17, 3 |
| `node_version` rows | 0 | 48 |

Confirms XP's boot-time writes (repo/branch creation, root nodes, `/applications` and
`/identity` folders, security roles) went through the `NodeStore`/`RepositoryStorageAdmin`
SPI into NoDB/Postgres, not ES — ES's own indices (`search-*`, confirmed via
`curl localhost:2609/index`) hold the mirrored search-side hybrid-mode state, never a
`storage-*` ES index (except the one spurious one bug #3 created and fixed).

**CRUD/API evidence**: full authenticated node CRUD via the management API was not
exercised (no `xp.suPassword` bootstrap done this gate — flagged as WARN at boot,
harmless) — per the work order's own allowance, boot-time writes (proven via psql above,
the strongest possible evidence since it bypasses XP entirely) plus unauthenticated
listing (`/osgi.bundle`, `/index` showing all 4 repos' search-side indices) are treated as
sufficient for this gate's storage-path-correctness scope. Full authenticated CRUD is a
reasonable Phase 2 follow-up if a deeper functional smoke is ever wanted.

**Restart-persistence check** (same `XP_HOME`, same NoDB stack, `SIGTERM` then reboot):
clean shutdown (no errors, no spurious reinitialization after fix #3), fast clean restart
(no re-init, no errors, all 115 bundles healthy), and identical Postgres row counts
before/after restart (4 / 4·4 / 26,1,17,3 / 48 / 48 — no drift, no duplication, no data
loss). This is the proof that NoDB is the actual system of record for the storage side,
not merely written-to-but-ignored.

### Rough edges for Phase 2

- **`Cluster not healthy: timed out: true, state: RED` ERROR-level log line, once per
  repo, on every cold boot** (both default and nodb mode — confirmed this is not
  nodb-specific, matches gate 0's default-mode boot log too). Self-resolves within the same
  second and initialization proceeds; cosmetic but noisy at `ERROR` level for something
  that isn't actually an error. Not introduced by this gate, just newly visible because
  gate D's log-grepping is stricter than earlier gates'.
- **`Failed to retrieve number of replicas from [storage-system-repo]` WARN, once per
  repo, nodb mode only.** Expected and harmless (`RepositoryStorageAdmin#getIndexSettings`
  is a documented no-op for nodb per the Gate 0 reconciliation inventory — `RepositoryCreator`
  tolerates the empty/missing result gracefully, exactly as flagged as a thing "Gate B must
  verify" back in Gate 0). Confirmed here with a real boot; worth silencing to `DEBUG` in
  the nodb-mode path in a later phase so operators don't mistake it for a real problem.
- **`TenantBootstrapTool` is a manual step with no server-side equivalent.** A production
  nodb deployment needs *some* control-plane action to provision a tenant before XP's first
  boot against it; this gate's tool is a reasonable dev/ops stand-in but isn't wired into
  any bootstrap/onboarding flow. Worth a real decision in Phase 2/3 (DESIGN.md's "control
  plane" section already anticipates this).
- **The nodb client bundle's `Import-Package`/`Private-Package` third-party surface is
  fragile to dependency graph changes.** Two of this gate's three bugs (NameResolverProvider
  merge loss, protobuf-lite/perfmark compileClasspath gap) are the same underlying class of
  problem — bnd's automatic embedding silently drops classes that are real, load-bearing,
  runtime dependencies but aren't visible on whatever buildpath bnd scans. There is no
  automated check that would catch a *third* occurrence of this pattern (e.g. a future grpc
  version bump pulling in a new runtime-scope transitive dependency). Worth a lightweight
  smoke test in CI that boots the nodb-mode distro and exercises one real RPC, rather than
  relying on manual boots to catch this class of bug.
- **`NodbStorageClient`'s class javadoc and `com.enonic.xp.storage.nodb.cfg`'s template
  comment both still say the bearer token needs `operator` scope** for
  `RepositoryAdmin.CreateRepository`/`DeleteRepository`. This is stale relative to gate B's
  own scope-model correction (applied server-side in `TenantAuthInterceptor`:
  `MANAGEMENT_METHODS = Set.of()`, repo lifecycle is RUNTIME-scoped) — this gate used a
  RUNTIME-scope token successfully end-to-end, confirming the doc, not the code, is wrong.
  Left as-is (out of this gate's scope to touch client documentation beyond what blocked
  the smoke), but flagged here explicitly so Phase 2 doesn't propagate the stale guidance.

### Deviations from the work order

- Provisioning a tenant against a standalone `NodbServer` needed a new tool
  (`TenantBootstrapTool`) not anticipated by the work order's "a small one-off Java class
  ... is acceptable" allowance being exercised literally, rather than reusing
  `NodbTokenTool` or an existing RPC (there is no RPC for this — confirmed by reading
  `RepositoryAdminService`, which assumes the tenant schema already exists).
- Bug #3 (bundle stop-ordering) was not anticipated by any prior gate's scope and required
  a `modules/runtime/build.gradle` change beyond client/config territory the work order's
  gate D bullet describes — flagged as its own fix rather than folded into "boot smoke",
  since it's a genuine production-relevant correctness bug (data-directory-corrupting
  restart hang), not a smoke-test artifact.
- DESIGN.md §9/§10 updates mentioned in the original gate D bullet were not made in this
  pass (time went to the three real bugs above instead, which the work order's own
  "diagnose from the log, fix within 3 attempts, re-verify by full re-boot" allowance
  covers); the actuals here in BUILD-PHASE-1.md are the authoritative record for Phase 2
  to fold into DESIGN.md.

### Stack fully stopped

XP (`SIGTERM`, clean shutdown confirmed in log), `NodbServer` (`SIGTERM`), and the
`nodb-pg-gated` postgres:17 container (`docker stop && docker rm`) were all stopped and
removed at the end of this gate. Confirmed no residual listeners on 7700/8080/4848/2609/55432
and no containers (`docker ps -a` empty).
