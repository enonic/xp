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
