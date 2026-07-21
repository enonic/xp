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
| **0** | Spikes + decisions, no production code. (a) **Format spike**: read XP's payload serialization (`NodeVersionServiceImpl` store/serialize path) and answer concretely — is it generically parseable (typed values, REFERENCE identifiable) as an XP-independent format, or must Phase 3 define a boundary format XP serializes to? (b) **A-vs-B decision**: decorator-extension (divert payload segments to `PutPayload`/`GetPayload`) vs transactional `WriteBatch` (segments as `PayloadRef`s). (c) source-verify payload write path + read path (per-segment §2.1) + the FK-reenable ordering. | Decisions + call-site inventory + format answer recorded in this file; A-vs-B chosen with reasoning; FK-reenable ordering identified; no build needed. | ~250k |
| **A** | NoDB side. Confirm/extend `payload` storage: batched multi-hash `GetPayload` (§2.1 bulk reads); if approach B, confirm `WriteBatch` carries the 3 segments as `PayloadRef`s (it already accepts them — mostly wiring). Implement the format handling per Gate 0. **Re-add the `node_version` FK** (`node_data_hash`/`index_config_hash`/`acl_hash` → `payload(hash)`). | `cd nodb && ../gradlew build` green. Tests (per-class counts reported): payload round-trip + dedup (one row for identical bytes); **FK enforced** (version referencing an absent payload is rejected); batched multi-hash get returns all; dual-tenant isolation of the payload pool; prior slice-1/Phase-1/2 nodb tests still green. | ~350k |
| **B** | XP side. Route the 3 payload segments to NoDB per the Gate-0 choice: **B** = `NodeVersionServiceImpl`/write path hands segments to the storage SPI so they ride the transactional version write; **A** = extend the Phase-2 `BlobStore` decorator to divert payload segments too. Reads use per-segment `GetPayload` + the §2.1 caches. SCR `storage.backend=nodb`. | Full XP build green (`-x` the two itest integrationTests). **Default (no config) byte-identical** — `git status` confined to expected files, no `core-api`/core-repo assertion edits; a default-mode spot itest green. Unit tests vs in-process NodbServer green. If B: verify the version write is still one transactional unit (payload+version+branch+outbox). | ~450k |
| **C** | XP itests, native payloads. Fixture in nodb mode stores payloads IN NoDB (file BlobStore no longer receives node/index-config/ACL writes). Run the storage itest set in nodb mode — should be **broader** than Phase 1's subset now that payloads are native. | nodb-mode storage itests green (report the class list + counts; expect more classes green than Phase 1's 21 — payload round-trip, get/getByPath, read-your-writes through XP's real path). **Default suites unchanged** (itest-core known profile: only the 4 pre-existing icuSort failures; itest-core-content clean). Dual-tenant payload isolation itest. | ~500k |
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
- **Payload GC** — deleting a version frees its payload rows when refcount hits zero
  (indexed FK lookup, not a blobstore sweep). Unlike binary GC (Phase 2, blocked on
  version-history), payload GC is a plain SQL refcount check and can work in nodb mode —
  but confirm it doesn't route through `findVersions` the way binary GC does.

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
