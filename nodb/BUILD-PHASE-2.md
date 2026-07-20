# NoDB Build — Phase 2 Work Order (Binaries: NoDB-fronted object storage)

**Read first:** `nodb/DESIGN.md` §2 (data placement), §7.2 (security/tenant scoping),
§9 phase table + §9.2 decision; `nodb/BUILD-PHASE-1.md` (client bundle, tenant/token
model, test patterns); `nodb/BUILD-SLICE-1.md` (engine/server conventions). Self-contained.

## Goal

Move binary handling so that **XP → NoDB → S3**: XP holds no object-store credentials,
NoDB fronts the object store. Binaries stay **content-addressed on S3, never in Postgres**
(rejected: WAL/TOAST). Per-tenant isolation by S3 prefix + STS-scoped presigned URLs.
Default (BlobStore) mode stays byte-identical; nodb mode routes binaries through NoDB.

Independent of the payload phase (Phase 3) — both sit only on Phase 1. This phase adds a
binary path; it does not touch node payloads (still BlobStore-fronted until Phase 3).

## Scope constraints (decided up front)

1. **Binaries are opaque to NoDB** — no format spec, no parsing (unlike payloads). Pure
   content-addressed blobs keyed by the existing `sha256:` BlobKey.
2. **Content-addressing + per-tenant dedup preserved**: key = sha256 of bytes; layout
   `<bucket>/<tenant>/binary/<sha256>`; identical bytes under a tenant store once.
3. **Binaries-before-commit invariant** (risk register #4; four backup mechanisms depend
   on it): a binary must be durably on S3 *before* the version row referencing it (via
   `node_version.binary_keys`) commits. The upload path must guarantee this ordering.
4. **Both-backend toggle**: nodb mode → binaries via NoDB; default → existing file/S3
   BlobStore, unchanged. Same itests pass on both.
5. **No new node-payload work** and no query/command changes beyond the binary read/write
   plumbing.

## Branch & layout

Branch `nodb-phase2-binaries` off `nodb-phase1` (carries both the XP tree and `./nodb/`).
Local/dev + itest object store: **MinIO** (testcontainers-minio); prod: any S3-compatible.

## Gates

### Gate 0 — Design spikes + inventory (est. ~300k)

Decisions to lock before building (each has a compat dimension — spike, don't assume):

- **Upload path** (risk #4): choose *stream-through-NoDB* (XP streams bytes → NoDB → S3,
  NoDB confirms durable before returning — invariant satisfied synchronously, simplest)
  vs *presigned-PUT + confirm* (three-step: presign → XP uploads to S3 → confirm →
  reference). **Recommendation: stream-through-NoDB as the baseline** (invariant-safe,
  fewer moving parts); presigned-PUT is a later large-upload optimization.
- **Download path** — two sub-cases:
  - *XP-internal reads* (image processing, export/dump, media info): XP needs the bytes →
    **stream/get-bytes through NoDB**.
  - *Serving media to browsers* (portal media handler): **presigned-GET redirect (or CDN)**
    bypasses NoDB bandwidth — the scaling win — BUT a 302-to-S3 **changes response
    semantics** vs today's same-origin stream. Decide: presigned-redirect as the serving
    default (faster, needs CORS/CDN thought) vs stream-through-NoDB (byte-parity, NoDB
    carries media bandwidth). **Recommendation: stream-through-NoDB for parity in this
    phase; presigned-GET serving as a documented opt-in** (revisit for cloud scale).
- **Source-verify the binary call sites** (à la Phase 0 Gate 0): `BinaryService`, the
  binary segment of `BlobStore`, portal media handler(s), image/thumbnail processing,
  export/dump binary read (`DumpWriter.writeBinaryBlob`), and attachment write on
  content create/update. Enumerate each: gets-bytes vs serves vs writes.
- **Binary key + layout**: confirm sha256 `BlobKey`, `<bucket>/<tenant>/binary/<sha256>`,
  dedup via S3 `HEAD` before `PUT`.
- **GC**: confirm the mark-and-sweep against `node_version.binary_keys` (design §6) — in
  nodb mode NoDB performs the S3 delete; enumerate the current binary-GC command.
- **Gate: decisions + call-site inventory + minio test harness recorded in this file.**

### Gate A — NoDB side: binary store engine + S3 + RPCs (est. ~450k)

- Engine `BinaryStore` over the AWS SDK v2 S3 client: `put(tenant, bytes)→sha256` (HEAD-
  then-PUT dedup, idempotent), `get(tenant, hash)→stream`, `exists`, `delete`,
  `presignGet(tenant, hash, ttl)` and (if chosen) `presignPut`. Tenant → S3 prefix from
  `TenantContext`; **presigned URLs use STS session credentials with an inline policy
  restricted to the tenant prefix** (§7.2) so a URL can never escape the tenant.
- Proto/RPCs (per Gate 0): `PutBinary` (client-streaming upload) returning the hash;
  `GetBinary` (server-streaming download); `BinaryExists`; `DeleteBinary`; `PresignGet`
  (+ `PresignPut` if chosen). Chunked streaming with backpressure; size limits.
- Config: `NODB_S3_ENDPOINT/BUCKET/credentials` (NoDB holds them; XP never does).
- Tests (testcontainers-minio, dual-tenant): upload/download round-trip; dedup (same
  bytes → one object); cross-tenant isolation (tenant-A token cannot get/delete tenant-B
  binary; a presigned URL minted for A is scoped to A's prefix); presign fetch works.
- **Gate: `cd nodb && ../gradlew build` green incl. new binary tests.**

### Gate B — XP side: NoDB-backed binary provider (est. ~450k)

- In `core-storage-nodb-client`: a binary path implementing XP's binary access — either a
  `BlobStore` provider scoped to the binary segment, or a dedicated binary component the
  `BinaryService`/portal use — routing put/get/exists/delete to the Gate-A RPCs, SCR-
  registered `storage.backend=nodb`. Streaming both directions (no whole-file buffering).
- Wire the two read cases from Gate 0: get-bytes (processing/export) → `GetBinary`;
  media serving → per the chosen serving mode (stream, or presigned-redirect if opted in).
- **Enforce binaries-before-commit**: binary `PutBinary` completes (durable) before the
  `WriteBatch`/version write that lists it in `binary_keys`. Wire the ordering at the
  write path; add an invariant test.
- Config + error mapping; **default (no config / backend≠nodb) byte-identical** to today.
- Tests: against in-process NoDB + testcontainers-minio (reuse Phase-1 patterns).
- **Gate: full XP build green with the binary path; default-mode behaviour unchanged
  (git status shows no unrelated edits; arch/suite green).**

### Gate C — XP itests + invariant + isolation (est. ~450k)

- Parameterize fixtures (reuse Phase-1 `xp.itest.storage=nodb` + a MinIO container) so
  binary-touching itests run in nodb mode: attachment create/read, image/media handling,
  binary export/import round-trip.
- Run the binary-relevant itest classes in nodb mode: green. Full suites in default mode:
  unchanged profile.
- Invariant test: version referencing a binary never commits before the binary is on S3
  (simulate a mid-write failure → no dangling `binary_keys`; orphan binary is acceptable,
  GC'd).
- Cross-tenant isolation itest: two tenants, one NoDB+MinIO; tenant-A binary invisible to
  tenant-B; presigned URL tenant-scoped.
- **Gate: binary itests green vs NoDB (both modes recorded); invariant + isolation proven.**

### Gate D — Boot smoke + GC + docs (est. ~300k)

- Boot XP `backend=nodb` against local NoDB + MinIO: create content with an image
  attachment via the API, retrieve/serve it, restart, verify the binary persists on S3
  (mc/aws ls under the tenant prefix) and is still served.
- **Binary GC**: delete the content, run the binary-GC path, verify the orphaned S3
  object is removed by NoDB (mark-and-sweep against `binary_keys`).
- Update DESIGN.md §9 (Phase 2 status), §6 (binary GC now NoDB-performed), risk #4
  (resolved); append actuals to this file. Push `nodb-phase2-binaries`.
- **Gate: boot+serve+restart+GC smoke green; docs committed; branch pushed.**

## Execution guidance

- Same regime: agents build, orchestrator verifies (forced reruns, grep proofs, diff
  review, and a real boot/serve smoke) and commits per gate; never commit red.
- **Anti-stall**: orchestrator arms process monitors on long runs; agents poll their own
  background runs within their turn, never end a turn "standing by".
- Budget: ~2M output tokens. Streaming (chunked up/download + backpressure) is the novel
  surface — if it balloons, fall back to stream-through-NoDB only and defer presigned
  URLs to a later optimization.

## Definition of done

Binaries in nodb mode flow XP → NoDB → S3, content-addressed + per-tenant-isolated, with
the binaries-before-commit invariant enforced and cross-tenant access denied (incl.
presigned URLs); default BlobStore mode byte-identical; distro boots, serves, and GCs
binaries in nodb mode; docs updated; `nodb-phase2-binaries` pushed.

## Gate 0 results

**Housekeeping note:** `nodb/BUILD-PHASE-2.md` did not exist on this branch prior to this
gate (`nodb-phase2-binaries` forked from `nodb-phase1` before the Phase-2 work order landed
on `nodb-design`, commit `416600a413`). This file was brought over from the main checkout
(read-only source, branch `nodb-design`) verbatim, mirroring how `nodb-phase1`'s own Gate 0
brought `BUILD-PHASE-1.md`/`DESIGN.md` onto that branch (commit `abc9130aee`). `DESIGN.md`
in this worktree is correspondingly the pre-`nodb-design`-update copy (predates §2.1, the
current §9 phase table, §9.1/§9.2) and was read read-only from the main checkout for this
gate, per the work order. Bringing `DESIGN.md` current on this branch is Gate D's job, not
Gate 0's — left untouched here.

### 1. Upload-path decision — CONFIRMED: stream-through-NoDB

Recommendation adopted as-is: **stream-through-NoDB** (XP streams bytes → NoDB `PutBinary`
(client-streaming) → NoDB writes to S3 → NoDB confirms durable → returns the sha256 hash to
XP) is the Gate A/B baseline. Reasoning, grounded in the current code (not assumed):

- The binaries-before-commit invariant is already enforced **synchronously and in-process**
  today: `BinaryServiceImpl.store()` (`modules/core/core-repo/.../binary/BinaryServiceImpl.java:36`)
  calls `blobStore.addRecord(...)` and returns only after the blob is durably written; its
  caller blocks on that return before building the `Node`/version. Stream-through-NoDB is
  the direct structural analog: replace the in-process `blobStore.addRecord` call with a
  blocking (from XP's perspective) `PutBinary` RPC that only returns once NoDB confirms the
  S3 `PutObject` succeeded. No new ordering primitive is needed — the *existing* call
  ordering already provides the invariant; stream-through-NoDB preserves it by construction.
- Presigned-PUT+confirm would require XP to (a) call NoDB to presign, (b) PUT to S3 directly,
  (c) call NoDB again to confirm/register the hash — 3 round trips instead of 1, and a new
  failure mode (step (b) succeeds against S3 but step (c) never arrives — an object durably
  on S3 that NoDB never learns about, harmless for GC but wasted engineering to handle
  without gaining anything, since XP is trusted to write today anyway). It only pays off when
  NoDB's own bandwidth becomes the bottleneck for very large uploads — a real concern
  eventually, explicitly deferred per the work order as a later optimization.
- Stream-through-NoDB also keeps the write path symmetric with the already-implemented
  `WriteBatch` RPC (`nodb/DESIGN.md` risk #1, resolved): one call, one confirmation, no
  distributed 3-step protocol to reason about for Gate B/C's invariant test.

No blocker found; no reason to deviate from the work order's recommendation.

### 2. Download-path decision

**(a) XP-internal reads — CONFIRMED: stream/get-bytes through NoDB (`GetBinary`).**
Every internal consumer needs actual bytes to operate on, not a redirect:
- `com.enonic.xp.core.impl.image.ImageServiceImpl` (`modules/core/core-image/src/main/java/com/enonic/xp/core/impl/image/ImageServiceImpl.java:115-116,125`)
  calls `contentService.getBinary(...)` to (i) verify the attachment's sha512 checksum and
  (ii) obtain the source `ByteSource` it decodes/scales/crops/re-encodes with an image
  library (`createImage`, line 182 on) — the bytes are read into a real image-processing
  pipeline, not just proxied.
- `DuplicateNodeCommand.execute()` (`modules/core/core-repo/.../node/DuplicateNodeCommand.java:229`)
  calls `binaryService.get(repositoryId, attachedBinary)` to re-attach the same bytes to a
  new node (which internally re-stores them via `BinaryService.store`, so this is a
  get-then-write, not a serve).
- Dump export reads bytes out of the *live* BlobStore to write them into the dump archive
  (see item 3 below) — also an internal get.
There is no case among XP-internal readers where a redirect would substitute for bytes; all
of Gate 0's item-2a candidates confirm `GetBinary` streaming as the only fit.

**(b) Serving media to browsers — CONFIRMED: stream-through-NoDB as the phase-2 default,
presigned-GET as a documented opt-in — for a stronger reason than "byte-parity for its own
sake": the current portal media handlers actively transform bytes server-side, so a raw
redirect is not merely a behavior change, it is often not equivalent at all.**

Investigated the actual handler code
(`modules/portal/portal-impl/src/main/java/com/enonic/xp/portal/impl/handler/`):

- `AbstractAttachmentHandlerWorker.execute()` (`AbstractAttachmentHandlerWorker.java:62-137`)
  is the shared base for both `AttachmentHandlerWorker` (plain file attachments) and
  `ImageHandlerWorker` (media/image serving). It: fetches the content, resolves the
  attachment, calls `getBinary()` (line 194-203, itself calling
  `contentService.getBinary(id, binaryReference)`), decides a `contentType`, optionally calls
  the overridable `transform(...)` hook, sets a `Content-Security-Policy` header
  (different value for SVG vs. non-SVG, lines 90-104), sets `gzip` `Content-Encoding` for
  `.svgz`, computes a permission-and-branch-aware `Cache-Control` fingerprint check
  (lines 106-124, `isPublic` depends on ACL + whether branch is master), optionally sets
  `Content-Disposition: attachment` (line 127-130), and finally streams the body through
  `RangeRequestHelper.handleRangeRequest(...)` (line 168), which implements HTTP Range /
  206-partial-content handling itself, same-origin.
- **`ImageHandlerWorker.transform()`** (`ImageHandlerWorker.java:104-147`) is not optional —
  it is the whole point of the image-serving path: it always calls
  `imageService.readImage(readImageParams)`, which decodes, orients, crops to focal point,
  scales, adjusts quality/background, and re-encodes the image per the URL's scale/filter/
  quality/format parameters. The resulting bytes are a **derived artifact that does not
  exist as an S3 object** — there is nothing on S3 a presigned URL could point at (XP's own
  `ImageServiceImpl` even disk-caches these derivatives locally, see `getCachedImagePath`,
  `ImageServiceImpl.java:151-172` — a resize cache, not S3). A 302-to-S3 is **not feasible**
  for `ImageMediaHandler`/`ImageServiceMappingHandler`-served requests at all, for any
  request that includes scale/crop/quality/format parameters — which is effectively every
  image-serving request in normal use (the media API path always includes a scale segment).
- For the **plain-attachment** path (`AttachmentHandlerWorker`/`AttachmentHandler`, no
  `transform()` override — `transform()` is a no-op passthrough at the base class,
  `AbstractAttachmentHandlerWorker.java:139-144`), a byte-identical redirect is at least
  *structurally* possible (S3 natively supports Range GETs on presigned URLs), but several
  response behaviors the current handler owns would need to be replicated at presign time
  or given up: the CSP header (SVG-specific), gzip `Content-Encoding` for `.svgz`, the
  permission/branch-aware `Cache-Control` fingerprint logic (computed in XP from ACL +
  branch, not something S3 can decide), and `Content-Disposition: attachment` (settable via
  S3 `response-content-disposition` override, but only if computed and embedded into the
  presign call, i.e. it still requires an XP→NoDB round trip per request to mint the URL).
- **Conclusion**: stream-through-NoDB for serving is the only option that is behavior-
  preserving across the board today (Range handling, CSP, cache-control, disposition, and —
  critically — the always-on image transform). Presigned-GET is viable only for a narrower
  subset (non-image, non-SVG-CSP-sensitive attachments) and is correctly scoped by the work
  order as a documented opt-in / future cloud-scale optimization, not the phase-2 default.

### 3. Source-verified binary call-site inventory

| Site (file:line) | What it does | Classification |
|---|---|---|
| `modules/core/core-repo/src/main/java/com/enonic/xp/repo/impl/binary/BinaryService.java:11` | `store(repositoryId, binaryAttachment)` interface method | contract |
| `modules/core/core-repo/src/main/java/com/enonic/xp/repo/impl/binary/BinaryService.java:13` | `get(repositoryId, attachedBinary)` interface method | contract |
| `.../binary/BinaryServiceImpl.java:36` | `store()` → `blobStore.addRecord(segment, byteSource)` | **writes** |
| `.../binary/BinaryServiceImpl.java:44` | `get()` → `blobStore.getRecord(segment, key)` | **gets-bytes** |
| `.../node/NodeConstants.java:28` | `BINARY_SEGMENT_LEVEL = SegmentLevel.from("binary")` — the binary "segment" of `BlobStore` is `[repositoryId, "binary"]` (`RepositorySegmentUtils.toSegment`, `BLOB_TYPE_LEVEL`=1) | key/layout |
| `modules/core/core-api/src/main/java/com/enonic/xp/blob/BlobStore.java` (whole interface) | `addRecord`/`getRecord`/`removeRecord`/`list`/`listSegments`/`deleteSegment`, all `Segment`-scoped | contract |
| `modules/blobstore/blobstore-file/.../FileBlobStore.java:66` | `addRecord(segment, in)` → `BlobKey.sha256(in)` then writes under `sha256/<hex>` | **writes**, confirms sha256 keying |
| `modules/core/core-api/src/main/java/com/enonic/xp/blob/BlobKey.java:42-46` | `BlobKey.sha256(ByteSource)` → `"sha256:" + hex(sha256(bytes))` | key confirmation |
| `modules/core/core-repo/.../node/CreateNodeCommand.java:74,99-119,117,~135` | `execute()` calls `storeAndAttachBinaries()` (loops pending `BINARY_REFERENCE` properties, calls `binaryService.store` at line 117) **before** `nodeStorageService.store(...)` | **writes**; ordering point for the invariant (see §5) |
| `modules/core/core-repo/.../node/UpdatedAttachedBinariesResolver.java:100` | `resolve()` calls `binaryService.store(repositoryId, binaryAttachment)` for newly-referenced binaries on update/patch | **writes** |
| `modules/core/core-repo/.../node/PatchNodeCommand.java:124-130,147` | Calls `UpdatedAttachedBinariesResolver...resolve()` at line 130, **then** `nodeStorageService.store(...)` at line 147 | ordering point for the invariant on updates |
| `modules/core/core-repo/.../node/AbstractGetBinaryCommand.java:41` | `binaryService.get(ContextAccessor.current().getRepositoryId(), attachedBinary)` — backs `NodeService.getBinary`/`ContentService.getBinary` | **gets-bytes** |
| `modules/core/core-repo/.../node/DuplicateNodeCommand.java:229` | `binaryService.get(repositoryId, attachedBinary)` then re-attaches to the duplicate (re-triggers a write via the duplicate's own create path) | **gets-bytes** feeding a write |
| `modules/core/core-repo/.../repository/RepositoryEntryServiceImpl.java:95` | `binaryService.store(SystemConstants.SYSTEM_REPO_ID, binaryAttachment)` — repository icon/binary in the system repo | **writes** |
| `modules/core/core-repo/.../repository/RepositoryEntryServiceImpl.java:172` | `binaryService.get(SystemConstants.SYSTEM_REPO_ID, attachedBinary)` | **gets-bytes** |
| `modules/portal/portal-impl/.../handler/AbstractAttachmentHandlerWorker.java:194-203` | `contentService.getBinary(id, binaryReference)`, throws 404 if null | **gets-bytes**, feeds serve |
| `modules/portal/portal-impl/.../handler/AbstractAttachmentHandlerWorker.java:62-137,168` | `execute()` builds the `PortalResponse`, streams via `RangeRequestHelper` | **serves-to-client** |
| `modules/portal/portal-impl/.../handler/image/ImageHandlerWorker.java:104-147` | `transform()` → `imageService.readImage(...)`, always applied for image serving | **gets-bytes + transforms**, feeds serve |
| `modules/portal/portal-impl/.../handler/attachment/AttachmentHandlerWorker.java` | Plain-attachment `AbstractAttachmentHandlerWorker` subclass, no transform | **serves-to-client** (passthrough) |
| `modules/portal/portal-impl/.../handler/ImageMediaHandler.java`, `AttachmentMediaHandler.java`, `AttachmentHandler.java` | `UniversalApiHandler` SCR entry points wiring path→worker | **serves-to-client** (routing) |
| `modules/core/core-image/.../ImageServiceImpl.java:115-116,125,143` | `readImage`/`writeImage` → `contentService.getBinary(...)` then `createImage(blob, ...)` (decode/scale/crop/re-encode) | **gets-bytes**, XP-internal processing |
| `modules/core/core-image/.../ImageServiceImpl.java:151-172` | `getCachedImagePath` — local disk cache of derived (scaled) images, keyed off sha512 + transform params | derived-artifact cache (not S3-addressable) |
| `modules/core/core-repo/.../dump/writer/ZipDumpWriterModel9.java:209-211` | `writeBinaryBlob(repositoryId, blobKey)` → `addBlob(segment, blobKey)` (registers for later flush) | **writes** (into dump archive), triggers a downstream get |
| `modules/core/core-repo/.../dump/blobstore/ZipDumpBlobStore.java` (`flush()`) | For each registered `BlobReference`: `sourceBlobStore.getRecord(reference.segment(), reference.key())` then writes `record.getBytes()` into the zip entry | **gets-bytes** (from the live/source BlobStore) + writes (into the archive) |
| `modules/core/core-repo/.../dump/reader/AbstractEntryProcessor.java:46-53` | `addBinary()` (dump *load*): `dumpReader.getBinary(repositoryId, blobKey)` then `blobStore.addRecord(segment, dumpBinary)` | **gets-bytes** (from archive) + **writes** (into live BlobStore) — the import-side mirror of `writeBinaryBlob` |
| `modules/core/core-repo/.../dump/reader/ZipDumpReaderModel9.java:220` | `getBlobByteSource(repositoryId, NodeConstants.BINARY_SEGMENT_LEVEL, blobKey)` | **gets-bytes** (from archive) |
| `modules/core/core-repo/.../vacuum/blob/BinaryBlobVacuumTask.java` (whole file) | `VacuumTask` SCR component (`order=200`, `deletesBlobs()=true`) — the current binary-GC command | GC (delete) |
| `modules/core/core-repo/.../vacuum/blob/BinaryBlobVacuumCommand.java:24,30` | Binds the generic vacuum to the binary segment (`NodeConstants.BINARY_SEGMENT_LEVEL`) and the version index field (`VersionIndexPath.BINARY_BLOB_KEYS`) | GC config |
| `modules/core/core-repo/.../vacuum/blob/AbstractBlobVacuumCommand.java:44-51,63-99` | Mark-and-sweep: lists all blob records in binary segments older than an age threshold not referenced by any live version (`IsBlobUsedByVersionCommand` query against `binaryblobkeys`), then `blobStore.removeRecord(...)` | GC (delete) |
| `modules/core/core-repo/.../vacuum/versiontable/VersionTableVacuumCommand.java:168-169` | **Second** binary-deleting path: when pruning old version-table rows, also deletes now-unreferenced binary blobs inline (`removeNodeBlobRecord(..., NodeConstants.BINARY_SEGMENT_LEVEL, blobKey)`) filtered by `!isBlobKeyUsed(blobKey, VersionIndexPath.BINARY_BLOB_KEYS)` | GC (delete) — **surprise**, see below |

No image/thumbnail "media info" extractor reads persisted binary bytes via `BinaryService`
separately from the above: `ImageContentProcessor` (`modules/core/core-content/.../processor/ImageContentProcessor.java`)
operates on the **in-memory** `BinaryAttachment` supplied to create/update, before it is
ever persisted through `BinaryService.store` — not a separate BlobStore read, so it needs
no NoDB wiring beyond whatever create/update already routes.

### 4. Binary key + layout + dedup — CONFIRMED, with one nuance

- Key is sha256, confirmed at `BlobKey.sha256(ByteSource)` (`BlobKey.java:42-46`,
  `"sha256:" + hex(sha256(bytes))`), used by `FileBlobStore.addRecord` (`FileBlobStore.java:66`)
  for every binary write via `BinaryServiceImpl.store`.
- Proposed S3 layout `<bucket>/<tenant>/binary/<sha256>` and dedup via S3 `HEAD`-before-`PUT`
  are both consistent with the existing model (content-addressed, immutable, add-only) and
  were exercised concretely in the harness smoke test (§7 below): `HEAD` on a
  not-yet-written key returns `NoSuchKey`; after `PUT`, `HEAD` succeeds — the exact
  dedup-check shape Gate A's `BinaryStore.put` needs (skip the `PUT` when `HEAD` succeeds).
- **Nuance (worth flagging, not a blocker)**: today's dedup scope is **per repository**, not
  per tenant — the `BlobStore` segment is `[repositoryId, "binary"]`
  (`RepositorySegmentUtils.toSegment`, `BLOB_TYPE_LEVEL`), so identical bytes uploaded to two
  repositories in the same tenant are stored twice today. The proposed NoDB layout
  (`<tenant>/binary/<sha256>`, no repo component) is **wider** than today's dedup scope —
  cross-repo dedup within a tenant, which is a deliberate and desirable improvement fully
  aligned with `DESIGN.md` §7.2's stated per-tenant (not global) dedup rationale, but it is
  a behavior change from current per-repo dedup, not merely a straight port. No compat risk
  (dedup is a storage optimization, invisible to the `BlobKey`-based reference contract) —
  noted for completeness since Gate 0 asks to "confirm," not assume.
- Reference from versions: ES today indexes `VersionIndexPath.BINARY_BLOB_KEYS = "binaryblobkeys"`
  (`.../version/VersionIndexPath.java:15`), written by `VersionStorageDocFactory.java:27` and
  read back into `BlobKeys` by `NodeVersionFactory.java:36`. On the NoDB side,
  `nodb/schema/schema.sql:62` already has `node_version.binary_keys text[] NOT NULL DEFAULT '{}'`
  with a GIN index at line 75 for the mark-and-sweep query, and the wire proto
  (`nodb/proto`, generated `Version.java`/`VersionOrBuilder.java`) already carries
  `repeated string binary_keys = 8` — i.e. the NoDB-side plumbing for `binary_keys` was
  already laid down in Phase 1's `WriteBatch`/`Version` message and needs no schema/proto
  change for Phase 2; only the S3-backing engine and XP-side wiring are new.

### 5. Binaries-before-commit invariant — enforcement point identified

The invariant is enforced **today** by call ordering, not by any explicit transaction or
barrier — and the same ordering is exactly where Gate B must preserve it:

- **Create**: `CreateNodeCommand.execute()` calls `storeAndAttachBinaries()`
  (`CreateNodeCommand.java:74`, which internally calls `binaryService.store(...)` at line
  117 for every `BINARY_REFERENCE` property) **before** building the `Node` and calling
  `this.nodeStorageService.store(StoreNodeParams.newVersion(...), ...)` (a few lines later,
  same method). Because `BinaryService.store()` is synchronous and returns only once the
  underlying `blobStore.addRecord` call completes, the version-referencing write physically
  cannot happen until the binary write has returned.
- **Update/patch**: `PatchNodeCommand` calls `UpdatedAttachedBinariesResolver.create()....resolve()`
  at `PatchNodeCommand.java:130` (which calls `binaryService.store(...)` internally at
  `UpdatedAttachedBinariesResolver.java:100` for newly-referenced binaries), and only
  afterward calls `this.nodeStorageService.store(...)` at `PatchNodeCommand.java:147`.
- **Gate B's job**: when the NoDB-backed binary provider replaces `BinaryServiceImpl`
  (or the `BlobStore` it wraps), its `store()`/`put()` call must remain **synchronous from
  XP's perspective** — it must block until NoDB's `PutBinary` RPC confirms the S3 write is
  durable — so that the pre-existing ordering in `CreateNodeCommand`/`PatchNodeCommand`
  continues to guarantee the invariant with zero change to those command classes. The
  invariant is therefore enforced by construction at the `BinaryService`/`BlobStore`-provider
  boundary, not by anything new needed in the command layer — the risk is a provider
  implementation that returns early (e.g. after enqueueing an async upload) rather than after
  NoDB's confirmed-durable response; Gate B's invariant test should specifically assert the
  provider blocks for durability, and Gate C's invariant test (mid-write failure → no
  dangling `binary_keys`) exercises the same property end-to-end.

### 6. Binary GC

Current binary-GC command: **`BinaryBlobVacuumTask`**
(`modules/core/core-repo/.../vacuum/blob/BinaryBlobVacuumTask.java`) — an OSGi
`VacuumTask` component (`order=200`, `name="BinaryBlobVacuumTask"`, `deletesBlobs()=true`),
delegating to `BinaryBlobVacuumCommand` (binds `NodeConstants.BINARY_SEGMENT_LEVEL` +
`VersionIndexPath.BINARY_BLOB_KEYS`) which runs the generic
`AbstractBlobVacuumCommand` mark-and-sweep: list all blob records in binary segments older
than an age threshold (`isOldBlobRecord`, avoids reaping records from writes still in
flight), check each against live versions via `IsBlobUsedByVersionCommand` (a query on the
`binaryblobkeys` index field), delete the ones with no referencing version
(`blobStore.removeRecord`).

**Surprise**: there is a **second** binary-deleting code path —
`VersionTableVacuumCommand` (`.../vacuum/versiontable/VersionTableVacuumCommand.java:168-169`)
also deletes now-orphaned binary blobs inline as part of pruning old version-table rows
(`removeNodeBlobRecord(repository.getId(), NodeConstants.BINARY_SEGMENT_LEVEL, blobKey)`,
filtered by the same "not referenced elsewhere" check). Both paths ultimately call the same
`BlobStore.removeRecord`, so in nodb mode both need to route through NoDB's `DeleteBinary`
RPC — Gate A/B should make sure whatever binary-delete seam is introduced is shared by both
callers rather than only wired for `BinaryBlobVacuumTask`.

In nodb mode, `blobStore.removeRecord` for the binary segment is replaced by a NoDB
`DeleteBinary(tenant, hash)` RPC — NoDB performs the actual S3 `DeleteObject`; the
mark-and-sweep query logic (list + check-referenced-by-version) is unchanged since it
already runs against `node_version`/its index, which is exactly what
`node_version.binary_keys` (Postgres, GIN-indexed, `nodb/schema/schema.sql:62,75`) is
designed to serve.

### 7. Test harness — CONFIRMED, dependencies enumerated, smoke-tested live

- `nodb/gradle/libs.versions.toml` currently declares `testcontainers = "1.20.4"` with only
  `testcontainers-postgresql` and `testcontainers-junit-jupiter` in the `testcontainers`
  bundle (lines 7,22-23,35). **No AWS SDK v2 and no testcontainers-minio module exist
  anywhere in this repository** (checked both `nodb/` and the full XP tree — there is no
  precedent S3 `BlobStore` implementation in this codebase at all; Gate A's `BinaryStore`
  is greenfield, not a port of an existing internal S3 client).
- To add for Gate A (`nodb/gradle/libs.versions.toml` + `nodb/engine/build.gradle.kts`):
  - `[versions]`: `testcontainers-minio` can reuse the existing `testcontainers` version ref
    (`org.testcontainers:minio:1.20.4` — same release train as `testcontainers-postgresql`,
    confirmed available on Maven Central and used successfully in the smoke test below); a
    new `awssdk = "2.29.15"` (or later) version ref.
  - `[libraries]`: `testcontainers-minio = { module = "org.testcontainers:minio", version.ref = "testcontainers" }`;
    `awssdk-bom = { module = "software.amazon.awssdk:bom", version.ref = "awssdk" }` (platform
    import, so individual AWS module versions don't need separate version refs);
    `awssdk-s3 = { module = "software.amazon.awssdk:s3" }`; optionally
    `awssdk-sts = { module = "software.amazon.awssdk:sts" }` for the STS-scoped presigned-URL
    work called for by `DESIGN.md` §7.2 (Gate A's `presignGet`/`presignPut`).
  - `[bundles]`: extend `testcontainers = [...]` with `testcontainers-minio`, or add a
    separate `testcontainers-minio` bundle entry — either is fine, `engine/build.gradle.kts`
    already special-cases the Docker-socket detection block that both bundles would share.
  - `nodb/engine/build.gradle.kts`: add `implementation(platform(libs.awssdk.bom))`,
    `implementation(libs.awssdk.s3)` (main — the engine's `BinaryStore` needs it at runtime,
    not just in tests) and `testImplementation(libs.testcontainers.minio)`.
- **Harness proven live** (not just read about): built a standalone scratch Gradle project
  (outside this repo, in the session scratchpad — no repo files touched) reusing this
  worktree's cached Gradle 9.6.1 wrapper distribution, with `testcontainers:minio:1.20.4` +
  `software.amazon.awssdk:s3` (via `awssdk-bom:2.29.15`), and ran a real test against a
  live `minio/minio` Docker container (Docker 29.4.2 available in this environment):
  create bucket → `HEAD` a not-yet-written key (asserted `NoSuchKeyException`) → `PUT` the
  object → `HEAD` succeeds → `GET` round-trips the exact bytes. **Result: `BUILD SUCCESSFUL`,
  test passed** (`putHeadGetRoundTrip() PASSED`, ~24s wall clock including Gradle Daemon
  cold-start and MinIO image pull). This validates: Docker/testcontainers works in this dev
  environment for MinIO specifically (not just the already-proven Postgres case from Slice 1/
  Phase 1), the exact HEAD-before-PUT dedup shape Gate A needs is a straightforward AWS SDK
  v2 call sequence, and there are no jar-resolution or version-conflict surprises pulling
  both `testcontainers:minio` and the `awssdk:s3` BOM together. No engine build was touched
  or run, per the gate's "do not build the engine yet" instruction — this was an isolated
  throwaway project.

### Summary of blockers

None found. All Gate 0 decisions have direct code-level support for the work order's
stated recommendations; the two "surprises" (per-repo vs per-tenant dedup scope widening,
and the second binary-GC path in `VersionTableVacuumCommand`) are both informational and
should be picked up as explicit acknowledgements/wiring points in Gates A/B, not blockers.
