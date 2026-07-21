# NoDB Build — Phase 3 Work Order (Node payloads into NoDB)

**Read first:** `nodb/DESIGN.md` §2 + §2.1 (data placement, per-segment read model), §3
(SPI), §9 phase table + §9.1 decisions, §10 risk register (esp. #10b FK, #10e format
spec); `nodb/BUILD-PHASE-2.md` (the decorator pattern + fixture); `nodb/BUILD-SLICE-1.md`
(engine/server conventions, `payload` table, `PutPayload`/`GetPayload`, `WriteBatch`).
Self-contained — do not require prior conversation context.

## Goal

Move the three **node payload segments** — node-data, index-config, ACL — from XP's
BlobStore into NoDB's `payload` table (content-addressed, the placement §2 always
specified; Phase 1 ran hybrid on BlobStore). After this, in nodb mode NoDB is the
**complete system of record** for structured data: branch/version/commit + payloads in
Postgres, binaries fronted to S3 (Phase 2); the file BlobStore falls idle.

Binaries are OUT of scope (done in Phase 2). Search stays on ES (Phase 4). No
structural-query de-search (Phase 8). No command/query behaviour change.

## Scope constraints

1. **Payloads are the node-data/index-config/ACL segments only** — binaries stay on the
   Phase-2 NoDB→S3 path, untouched.
2. **Re-add the `node_version` payload FK** dropped in Phase 1 Gate C (#10b) — now valid
   because payloads live in NoDB's `payload` table. Requires store-payloads-before-
   version-row ordering (WriteBatch gives this in one tx; a decorator gives it via
   commit-before-reference).
3. **XP-independent payload format** (#10e): NoDB must parse payload bytes as its OWN
   documented, versioned format (to later derive `_references`/search docs, Phase 8) —
   NOT XP's internal serializer classes. Defining/validating this is Gate 0's pivotal
   spike.
4. **Both-backend toggle preserved**: nodb mode → payloads in NoDB; default → file
   BlobStore, byte-identical. Same itests pass on both.
5. **No `core-api` changes; no test-assertion changes** (mechanical fixture/wiring
   adaptation allowed, listed per gate).

## Branch

`nodb-phase3-payloads` off `nodb-phase2-binaries` (carries all of Phases 0–2 + `./nodb/`).

## Gates (verification table)

| Gate | Deliverable | Verification (gate criteria — all must hold) | Est. |
|---|---|---|---|
| **0** | Spikes + decisions, no production code. (a) **Format spike**: read XP's payload serialization (`NodeVersionServiceImpl` store/serialize path) and answer concretely — is it generically parseable (typed values, REFERENCE identifiable) as an XP-independent format, or must Phase 3 define a boundary format XP serializes to? (b) **A-vs-B decision**: decorator-extension (divert payload segments to `PutPayload`/`GetPayload` — ~4 RPCs/save, near-zero XP surgery, keeps NodeVersionService→BlobStore symmetric across BOTH backends) vs transactional `WriteBatch` (segments as `PayloadRef`s — 1 RPC/save, in-tx FK ordering, but pulls payloads into the storage-SPI write for nodb mode only, creating a per-backend write-path asymmetry Phase 0 deliberately avoided). Decide on DATA: micro-bench a 4-RPC vs 1-RPC save with the existing slice-1 bench harness; weigh measured latency delta against the symmetry cost. (c) source-verify payload write path + read path (per-segment §2.1) + the FK-reenable ordering. (d) **Payload-GC: DECIDED (2026-07-21) — deferred to Phase 5** (the roadmap's designated vacuum/retention phase), alongside binary GC (#14). Verified fact behind it: XP's payload vacuum shares `AbstractBlobVacuumCommand:118` → `findVersions()` — the same nodb-mode blocker as binary GC — and §6's intended GC is NoDB-SERVER-side anyway (SQL refcount / binary_keys sweep), so the XP vacuum path was never the destination. Deferral is safe because content-addressing makes GC fully retroactive (a Phase-5 GC reclaims everything accumulated since day one); the cost is interim storage growth only. Guardrails: growth must be OBSERVABLE (Gate D adds payload-row/S3-object counts to the smoke evidence; a per-tenant metric lands with §7.1), and the hard deadline is before pilot tenants with real churn (Phase 7). | Decisions + call-site inventory + format answer recorded in this file; A-vs-B chosen with bench data; FK-reenable ordering identified; GC scoping decided; no production build needed. | ~250k |
| **A** | NoDB side. Confirm/extend `payload` storage: batched multi-hash `GetPayload` (§2.1 bulk reads); if approach B, confirm `WriteBatch` carries the 3 segments as `PayloadRef`s (it already accepts them — mostly wiring). Implement the format handling per Gate 0. **Re-add the `node_version` FK** (`node_data_hash`/`index_config_hash`/`acl_hash` → `payload(hash)`). | `cd nodb && ../gradlew build` green. Tests (per-class counts reported): payload round-trip + dedup (one row for identical bytes); **FK enforced** (version referencing an absent payload is rejected); batched multi-hash get returns all; dual-tenant isolation of the payload pool; prior slice-1/Phase-1/2 nodb tests still green. | ~350k |
| **B** | XP side. Route the 3 payload segments to NoDB per the Gate-0 choice: **B** = `NodeVersionServiceImpl`/write path hands segments to the storage SPI so they ride the transactional version write; **A** = extend the Phase-2 `BlobStore` decorator to divert payload segments too. Reads use per-segment `GetPayload` + the §2.1 caches. SCR `storage.backend=nodb`. | Full XP build green (`-x` the two itest integrationTests). **Default (no config) byte-identical** — `git status` confined to expected files, no `core-api`/core-repo assertion edits; a default-mode spot itest green. Unit tests vs in-process NodbServer green. If B: verify the version write is still one transactional unit (payload+version+branch+outbox). | ~450k |
| **C** | XP itests, native payloads. Fixture in nodb mode stores payloads IN NoDB (file BlobStore no longer receives node/index-config/ACL writes). Run the storage itest set in nodb mode. **Expectation corrected (review 2026-07-21): the subset does NOT grow** — Phase 1's 68 exclusions are SEARCH-driven (queries/version-history/children-via-index), and moving payload bytes changes storage location, not query capability. The gate is: the SAME Phase-1/2 subset stays green with payloads native, PLUS new payload-specific itests (round-trip through XP's real path, dedup visible, FK enforcement surfaced, per-segment lazy reads, dual-tenant payload isolation). | nodb-mode subset green (same classes as Phase 1/2, counts reported) + new payload itests green. **Default suites unchanged** (itest-core known profile: only the 4 pre-existing icuSort failures; itest-core-content clean). | ~500k |
| **D** | Boot + restart smoke + docs + push. Boot `backend=nodb` (+ local NoDB + PG; no MinIO needed unless also exercising binaries). Create/read nodes via API. | Clean boot, `core-storage-nodb-client` bundle ACTIVE. **psql ground truth**: node-data/index-config/ACL rows present in the tenant schema's `payload` table (dedup visible — few index-config/ACL rows vs many node-data). Restart with same XP_HOME → data retained, no re-init. Confirm the **file BlobStore holds no node payloads** in nodb mode. DESIGN.md §9 status + #10b/#10e updated; branch pushed. | ~300k |

Total ≈ **1.85M** output tokens; the variables are Gate 0's format answer and (if B) Gate B's write-path surgery.

## Key risks carried into the gates

- **Format spec (#10e)** — if XP's serialization needs XP's own classes to parse, Gate 0
  must define a boundary format; that enlarges Gates A/B. Resolve in Gate 0 before
  committing to B.
- **FK re-enable ordering (#10b)** — the FK makes "version references a payload that
  isn't stored" impossible; the write path MUST store payloads first. WriteBatch (B) does
  this in-tx; a decorator (A) relies on commit-before-reference (orphan payloads on
  failure are acceptable, GC'd — but a *committed* version with a missing payload is
  never acceptable). The FK is what enforces it; test it explicitly (Gate A).
- **Read-your-writes** — payload reads in nodb mode come from Postgres (strongly
  consistent) + the §2.1 immutable caches; no refresh dependency. Confirm no regression
  vs the file-BlobStore read path.
- **Payload GC** — VERIFIED (review 2026-07-21): XP's existing payload vacuum DOES route
  through `findVersions` (`AbstractBlobVacuumCommand:118` is shared by both blob
  vacuums), so it is blocked in nodb mode exactly like binary GC (risk #14). The
  design's SQL-refcount GC (indexed FK lookup, grace window) is a NoDB-SERVER-side
  mechanism that must be built or explicitly deferred — Gate 0 decision (d). Deleting a
  version only frees payload rows once that server-side GC exists.

## Execution guidance

- Same regime: agents build, orchestrator verifies (forced reruns, grep proofs, diff
  review, real-boot for DS wiring) and commits per gate; never commit red.
- **Anti-stall**: orchestrator arms process monitors on long runs; agents poll their own
  background runs within their turn, never end a turn "standing by".
- If Gate 0's format spike comes back "needs XP serializer to parse", STOP and re-scope
  (define the boundary format as an explicit Gate A deliverable, or fall back to storing
  opaque bytes now and deferring server-side parsing to when Phase 8 needs it).

## Definition of done

In nodb mode, node payloads store/read/GC from NoDB's `payload` table with the FK
re-enabled and a documented XP-independent format; NoDB is the complete system of record
(file BlobStore idle for payloads); default mode byte-identical; distro boots + persists
node data in Postgres across restart; docs updated; `nodb-phase3-payloads` pushed.

## Gate 0 results (2026-07-21)

### (a) Format spike — verdict: **(i) XP-independent as-is**

Read end-to-end from the real store path: `NodeVersionServiceImpl.store`
(`modules/core/core-repo/src/main/java/com/enonic/xp/repo/impl/node/dao/NodeVersionServiceImpl.java:64-97`)
calls `serializeAndAddBlobRecord` three times, once per segment, each with its own
serializer function reference:

| Segment | Serializer (file:line) | Envelope class |
|---|---|---|
| node-data | `NodeVersionJsonSerializer.toNodeVersionBytes` (`.../node/json/NodeVersionJsonSerializer.java:19-22`) | `NodeVersionDataJson` (`.../node/json/NodeVersionDataJson.java`), `data` field via `PropertyTreeJson.toJson` (`modules/core/core-api/src/main/java/com/enonic/xp/data/PropertyTreeJson.java:20-28,71-84`) |
| index-config | `NodeVersionJsonSerializer.toIndexConfigDocumentBytes` (`.../NodeVersionJsonSerializer.java:24-37`) | `IndexConfigDocumentJson` (`.../node/json/IndexConfigDocumentJson.java`) → `PatternConfigJson`/`IndexConfigJson`/`AllTextIndexConfigJson` |
| ACL | `NodeVersionJsonSerializer.toAccessControlBytes` (`.../NodeVersionJsonSerializer.java:39-42`) | `AccessControlJson` (`.../node/json/AccessControlJson.java`) → `AccessControlEntryJson` |

All three go through the same `ObjectMapper` (`ObjectMapperHelper.create()`,
`modules/core/core-internal/.../json/ObjectMapperHelper.java:19-32`: plain Jackson
`JsonMapper` + `JavaTimeModule`, ISO-8601 dates, `NON_NULL` inclusion — no XP-specific
Jackson module, no custom `TypeIdResolver`/polymorphic-type machinery). **Every property
array carries its type as a plain string tag**, written by
`PropertyTreeJson.propertyArrayToJson` (`PropertyTreeJson.java:71-84`): `{"name","type","values"}`,
`type = propertyArray.getValueType().getName()`. The 14 possible tag values are a
**closed, statically-registered set** — `ValueTypes.java:50-66`'s static block is the only
registration path in the whole codebase (`grep -rn "extends ValueType"` finds only the 14
built-ins in `com.enonic.xp.data`; there is no public `register` method, no app-level
extension point).

**Value shape for `v`**: `Value.toJsonValue()` (`Value.java:97-99`) defaults to the raw
Java object, but `ReferenceValue`/`DateTimeValue`/`LocalDateValue`/etc. each **override**
it (`ReferenceValue.java:24-28`, `DateTimeValue.java:24-28`) to return `asString()` — i.e.
**a `Reference` value serializes as a bare string identical in shape to a `String`
value** (`NodeId` — `modules/core/core-api/src/main/java/com/enonic/xp/node/NodeId.java` —
accepts any string, not just UUIDs: `NodeId.ROOT = NodeId.from("000-000-000-000")`). This
is the crux of the format question: **the `"type":"Reference"` tag is the ONLY signal
that distinguishes a reference from a same-shaped string** — nothing about the value `"v"`
itself is distinguishable. A generic parser that does not know this tag exists would
silently misclassify references as strings; a generic parser that DOES read the tag
(a fixed 14-entry lookup table, not an XP class) identifies every `REFERENCE` property
with 100% precision, directly from the bytes, with zero XP code involved.

**Real example** (node-data segment), envelope fields per `NodeVersionDataJson.java:19-37`,
`data` array entries copied verbatim from the existing XP fixture
`modules/core/core-api/src/test/resources/com/enonic/xp/data/PropertyTreeJsonTest-all-types.json`
(validated by `PropertyTreeJsonTest.java`, not fabricated for this report):

```json
{
  "id": "36b0a48e-1e1a-4b7a-9c2a-000000000001",
  "nodeType": "base:folder",
  "childOrder": "_path ASC",
  "data": [
    { "name": "singleInstant", "type": "DateTime", "values": [ { "v": "2007-12-03T10:15:30Z" } ] },
    { "name": "reference", "type": "Reference", "values": [ { "v": "my-node-id" } ] },
    { "name": "singleSet", "type": "PropertySet", "values": [
        { "set": [
            { "name": "long", "type": "Long", "values": [ { "v": 1 } ] },
            { "name": "setWithinSet", "type": "PropertySet", "values": [
                { "set": [ { "name": "long", "type": "Long", "values": [ { "v": 1 } ] } ] }
            ] }
        ] }
    ] }
  ],
  "attachedBinaries": []
}
```
(`manualOrderValue` omitted: `NON_NULL` inclusion drops it when null, same as any other
null field anywhere in these three segments — a documented quirk, not an XP-class
dependency: absence of a JSON key IS the null signal.)

Index-config segment (`IndexConfigDocumentJson`/`PatternConfigJson`/`IndexConfigJson`/`AllTextIndexConfigJson`, all plain named-field Jackson beans, no type-tag machinery needed since there's no polymorphic value union here):
```json
{
  "patternConfigs": [
    { "path": "myProperty", "indexConfig": {
        "decideByType": false, "enabled": true, "nGram": false,
        "fulltext": true, "includeInAllText": true, "path": false, "languages": ["en"]
    } }
  ],
  "defaultConfig": {
    "decideByType": true, "enabled": true, "nGram": false,
    "fulltext": false, "includeInAllText": true, "path": false
  },
  "allTextConfig": { "enabled": true }
}
```

ACL segment (`AccessControlJson`/`AccessControlEntryJson`):
```json
{
  "permissions": [
    { "principal": "user:system:admin", "allow": ["READ","CREATE","MODIFY","DELETE","PUBLISH","READ_PERMISSIONS","WRITE_PERMISSIONS"], "deny": [] },
    { "principal": "role:everyone", "allow": ["READ"], "deny": [] }
  ]
}
```

**v1 payload format spec (for NoDB to document as its OWN copy, not an XP dependency):**

| Segment | Top-level shape | Notes for a generic parser |
|---|---|---|
| node-data | `{id, nodeType, childOrder, manualOrderValue?, attachedBinaries[], data[PropertyArrayJson]}` | `data[]` is the reference-bearing structure; walk it recursively |
| PropertyArrayJson | `{name, type, values[PropertyValueJson]}` | `type` ∈ the 14-entry closed table below |
| PropertyValueJson | `{v}` (scalar types) or `{set[PropertyArrayJson]}` (type=`PropertySet`) or `{}` (null) | absence of both `v`/`set` keys = null value/set |
| index-config | `{analyzer?, patternConfigs[], defaultConfig, allTextConfig?}` | plain named-field beans, booleans/strings only |
| ACL | `{permissions[{principal, allow[], deny[]}]}` | `principal` = `type:idprovider:id` string; `allow`/`deny` = closed `Permission` enum names |

Type-tag table (`ValueTypes.java:20-46`, `getName()` per subclass — `ReferenceValueType.java:10` `"Reference"`, `DateTimeValueType.java:10` `"DateTime"`, etc.):

| tag string | `v` shape |
|---|---|
| `String` | string |
| `Xml` | string (raw XML) |
| `LocalDate` | string `yyyy-MM-dd` |
| `LocalDateTime` | string `yyyy-MM-ddTHH:mm:ss` |
| `LocalTime` | string `HH:mm[:ss]` |
| `DateTime` | string, ISO-8601 instant (`Z` suffix) |
| `Long` | JSON number (integer) |
| `Double` | JSON number (float) |
| `GeoPoint` | string `"lat,lon"` |
| `Reference` | string = opaque `NodeId` (same shape as `String` — type tag is the ONLY signal) |
| `Link` | string (content path/URI-ish) |
| `Boolean` | JSON boolean |
| `BinaryReference` | string (attachment reference name) |
| `PropertySet` | nested `{set:[...]}` (recursive) or absent (null) |

**Answer to the gating question**: yes — a generic parser with no XP classes can
reliably identify `REFERENCE` (and every other typed value) from the bytes alone, as
long as it carries this 14-entry tag table as ITS OWN documented constant (not by
loading/reflecting on XP's `ValueType` classes). This is a closed, stable set (no
extension point exists anywhere in the codebase) — the risk register's Phase-8 concern
resolves to "NoDB defines and versions its own copy of this table," not "NoDB depends on
XP code to parse." **No STOP-and-rescope needed.**

### (b) A-vs-B micro-bench — decision: **B (WriteBatch)**

Ran via a throwaway JUnit test, `nodb/bench/src/test/java/com/enonic/nodb/bench/Gate0PayloadPathBenchTest.java`
(package `com.enonic.nodb.bench`, reuses `BenchEnvironment`'s real loopback `NodbServer` +
testcontainers `postgres:17`; `BenchEnvironment` gained two small package-private
accessors, `port()`/`runtimeToken()`, so the scratch test could build a raw
`NodeStoreGrpc` stub for `StoreVersion`/`StoreBranchEntry`, which `NodbClient`'s thin
surface doesn't expose; `bench/build.gradle.kts` gained one `testImplementation` on the
grpc bundle for the same reason — both are additive, bench-module-only, not
production/engine/server code). 2000 measured saves per path after 200 warmup saves,
node-data ~2KB unique random bytes/save, index-config/ACL bytes identical across every
save on both paths (server-side dedup on path A; zero bytes-over-wire on path B after a
one-time priming write). Command: `../gradlew :bench:test --tests
"com.enonic.nodb.bench.Gate0PayloadPathBenchTest"` — green (`BUILD SUCCESSFUL`).

| Path | Shape | p50 (µs) | p95 (µs) | p99 (µs) | mean (µs) |
|---|---|---|---|---|---|
| A (decorator) | 3×PutPayload + StoreVersion + StoreBranchEntry, 5 sequential RPCs/save | 5219.4 | 6520.1 | 7884.4 | 5407.8 |
| B (WriteBatch) | 1 RPC/save | 1958.5 | 3274.6 | 5783.2 | 2244.0 |
| **Delta (A−B)** | | **3260.9 (62.5% of A)** | **3245.5 (49.8% of A)** | | |

B is **~2.7× faster at p50, ~2.0× faster at p95** on the loopback floor (localhost TCP,
zero real network latency — this already isolates the RPC-count effect from any RTT).
**This is a loopback floor, not a production number**: in a real deployment each RPC
carries one network round-trip; path A's 4 EXTRA round-trips (vs B's single call) get
multiplied by that RTT, so B's advantage only widens off-loopback — a 1ms RTT alone would
add ~4ms to path A's per-save cost that path B never pays.

**Recommendation: B (WriteBatch)**, weighing the measured latency against the symmetry
cost the work order flags:
- The latency delta is large and one-sided in every percentile measured (not just p50)
  — this isn't a marginal call.
- The re-added `node_version` FK (#10b) needs payload-before-version ordering; B gets
  this for free in ONE transaction (WriteService.write validates all hash-only refs
  before writing any row — `nodb/engine/src/main/java/com/enonic/nodb/engine/store/WriteService.java:38-84`),
  where A relies on "commit-before-reference" holding by construction across 5 separate
  calls (see (c) below — true today, but a property of caller discipline, not something
  the FK can enforce independent of call ordering).
- The symmetry cost (B pulls payloads into the storage-SPI write for nodb mode only,
  vs A's "NodeVersionService→BlobStore stays symmetric across both backends") is real
  but confined: the default (file-BlobStore) path is untouched either way — only the
  nodb-mode implementation of the write path differs in SHAPE, not in the abstractions
  core-repo's callers see (`NodeStorageServiceImpl.store`'s three consecutive calls,
  `versionService.store`/`branchService.store`, stay call-compatible; only what happens
  BEHIND `NodeVersionService`/the nodb-client SPI implementation changes).

### (c) Call-site inventory + FK ordering

**Write path** (traced via `NodeStorageServiceImpl.store`,
`modules/core/core-repo/src/main/java/com/enonic/xp/repo/impl/storage/NodeStorageServiceImpl.java:76-103`):

```
NodeStorageServiceImpl.store(StoreNodeParams, InternalContext)   [.java:76-103]
  line 83:  nodeVersionService.store(nodeStoreVersion, context)
              → NodeVersionServiceImpl.store                     [dao/NodeVersionServiceImpl.java:64-81]
                  line 68-70  ACL segment          → blobStore.addRecord(...)
                  line 71-72  index-config segment → blobStore.addRecord(...)
                  line 73-74  node-data segment     → blobStore.addRecord(...)
                (returns NodeVersionKey: 3 BlobKeys — segments are ALWAYS written
                 before this method returns)
  line 85-94: build NodeVersion from the returned NodeVersionKey + binary keys + attrs
  line 98:  versionService.store(nodeVersion, context)     → VersionServiceImpl.store
              (VersionServiceImpl.java:26-30 → nodeStore.storeVersion(repositoryId,
               VersionRecord) — the XP-side storage SPI, com.enonic.xp.storage.spi.NodeStore,
               already established Phase 0/1; nodb mode's implementation of this SPI call
               maps 1:1 onto nodb.proto's StoreVersion RPC)
  line 99:  branchService.store(nodeBranchEntry, context)  → BranchServiceImpl.store
              (BranchServiceImpl.java:58-73 → nodeStore.storeBranchEntry(repositoryId,
               branch, BranchEntryRecord) at line 70, inside an in-memory cache.compute()
               lambda, same call chain, immediately after the version write — maps 1:1 onto
               nodb.proto's StoreBranchEntry RPC)
  line 100: indexDataService.store(...)                    → search-index write
```
(Corrected from an earlier draft of this section that mis-cited these two calls as
`StorageDao`/ES-only — they already go through the SPI interface DESIGN.md §3.2 describes,
verified directly against the current source above. This also means path A's `StoreVersion`
+ `StoreBranchEntry` RPCs are not hypothetical: they are exactly what the nodb-client's
existing `com.enonic.xp.storage.spi.NodeStore` implementation already calls today for
every node save, per-op, in both approaches — the only question A-vs-B changes is
whether payload writes join that same transactional unit or stay as separate `PutPayload`
calls in front of it.)

All four steps run **sequentially on the same thread, no async dispatch, no
transactional wrapper** — payload segments are always fully written (blocking
`blobStore.addRecord` calls) before the version row is built or written. This is
exactly the "commit-before-reference" ordering approach A's decorator needs, and it
**already holds today**, unconditionally, for both backends — approach A requires ZERO
reordering of this call sequence, only redirecting what `blobStore.addRecord` does for
these three segments (mirroring the Phase-2 `NodbBinaryBlobStore` pattern). Approach B
would need to restructure this exact method (plus `NodeVersionServiceImpl`) to defer the
version/branch-entry writes until the payload bytes are known and bundle all of it into
one `WriteBatch` call — a materially wider diff (see invasiveness comparison below).

**Surprises**: (1) no compensating rollback exists today if `versionService.store`
throws after the blob writes succeed — orphaned blobs are accepted/GC'd, consistent with
the design's content-addressed GC story. (2) `push()` (branch promotion) is a SEPARATE
call chain that does NOT call `nodeVersionService.store` at all — it reuses an existing
`NodeVersionKey` read-only and only writes branch + index-data, confirming version/payload
data is immutable and only branch pointers move on push (no new FK concern there).
(3) `AttachedBinaryJson` (`.../node/json/AttachedBinaryJson.java`) embeds the binary's
`blobKey` string INSIDE the node-data JSON itself, in addition to `node_version.binary_keys`
— worth flagging for Phase 8/GC bookkeeping (two places carry the same key).

**Read path / per-segment caches** — confirmed exactly per DESIGN.md §2.1 against
`NodeVersionServiceImpl.java`:
- Three independent Guava caches keyed by `BlobKey`: `nodeDataCache`, `indexConfigCache`,
  `accessControlCache` (`NodeVersionServiceImpl.java:43-47`), sized 98%/1%/1% of
  `cacheCapacity` (`.java:55-56`) — content-addressed + immutable, so cached entries never
  need invalidation (only the branch head pointer does, matching §2.1's claim exactly).
- `get()` (`.java:99-126`) fetches/deserializes all 3 segments independently via
  `fetchAndDeserializeCached` (`.java:144-156`), one per cache.
- `getPermissions()` (`.java:128-135`) is a genuinely separate, cheaper, ACL-ONLY read —
  not a filter over `get()`'s result — matching §2.1's "index-config essentially only at
  index/write time, ACL for permission checks" segment-scoped read model exactly.

**Decorator-extension vs WriteBatch invasiveness** (approach A vs B, file-count
comparison):
- **A** — extend the Phase-2 pattern: `NodbBinaryBlobStore`
  (`modules/core/core-storage-nodb-client/.../NodbBinaryBlobStore.java`, 486 lines) already
  shows the exact shape — a `BlobStore` decorator registered at higher OSGi ranking so
  every `@Reference BlobStore` in core-repo rebinds automatically, zero core-repo edits.
  Extending it (or adding a sibling `NodbPayloadBlobStore`) to intercept
  `NODE_SEGMENT_LEVEL`/`INDEX_CONFIG_SEGMENT_LEVEL`/`ACCESS_CONTROL_SEGMENT_LEVEL` and
  route to `PutPayload`/`GetPayload` (unary RPCs — simpler than binaries' streaming) is
  **~1 file** in `core-storage-nodb-client`, plus the FK/dedup work in `nodb/` itself.
  Core-repo: **0 files touched.**
- **B** — bundling payload+version+branch-entry into one `WriteBatch` requires
  restructuring the exact call chain above: `NodeVersionServiceImpl.store` can no longer
  just write-and-return-keys, it must hand raw bytes+hashes upward; `NodeStorageServiceImpl.store`
  must collect all three (payload bytes, version, branch entry) and issue ONE call into
  the nodb-client's `NodeStore` SPI implementation instead of three separate service
  calls. Core-repo: **2-3 files touched** (`NodeVersionServiceImpl`, `NodeVersionService`
  interface reshaping its return contract for nodb mode, `NodeStorageServiceImpl`) plus
  the nodb-client-side `NodeStore` SPI implementation in `core-storage-nodb-client`. Note
  `NodeVersionKey` itself (`modules/core/core-api/src/main/java/com/enonic/xp/node/NodeVersionKey.java`)
  need NOT change — it already models "3 opaque hash-shaped keys," compatible with either
  approach — so this stays within the "no core-api changes" scope constraint.
- Net: **A is less invasive to core-repo** (matches the work order's framing exactly),
  but B's latency and FK-ordering wins (see (b)) are judged to outweigh that in this
  gate's recommendation.

### (d) Payload GC

Already decided in the work order header (2026-07-21): deferred to Phase 5, alongside
binary GC (#14). Not revisited here.

### Summary

| Decision | Outcome |
|---|---|
| Format (#10e) | XP-independent as-is; v1 spec + type-tag table documented above; no rescope |
| A-vs-B | **B (WriteBatch)** — ~2.7×/2.0× (p50/p95) latency win on loopback, widens off-loopback; in-tx FK ordering; core-repo diff is wider than A but confined to 2-3 files + the SPI impl |
| FK ordering | Payload-before-version already holds today (sequential, synchronous call chain); B enforces it in-tx via `WriteService.write`'s pre-validation, A relies on the same call ordering continuing to hold |
| GC | Deferred to Phase 5 (unchanged from work order header) |

No blockers. Gate A can proceed with the WriteBatch (B) integration.
