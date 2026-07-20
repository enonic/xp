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
