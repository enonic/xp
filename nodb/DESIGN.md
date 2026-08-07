# NoDB — Storage Backend for Enonic XP

**Status:** Draft v0.1 · **Scope:** replace embedded Elasticsearch 2.4.6 with a pluggable
storage backend (Postgres + OpenSearch + S3), with zero changes to the Node API and JS libs.

---

## 1. Goals and non-goals

**Goals**
- `NodeService`, the JS libs (lib-node, lib-content) and all app-facing semantics stay
  byte-compatible. Compatibility gate: **XP's existing core-repo/itest suites run green
  against NoDB** with no test changes.
- Storage SPI in XP with two implementations: the current embedded ES (transitional) and NoDB.
- NoDB is a Java engine (a plain library, directly instantiable in tests) fronted by gRPC.
  **XP always consumes NoDB over gRPC** — one binding, one tested path. NoDB runs in two
  modes:
  - **server** — `enonic/nodb` container against shared Postgres/OpenSearch/S3 (cloud,
    self-hosted clusters);
  - **dev** — `nodb dev`: the same process additionally supervises local Postgres +
    OpenSearch child processes (downloaded binaries, no Docker required). Each sandbox is
    a tenant in the local NoDB, so N sandboxes share one PG + one OS (lighter than
    today's per-sandbox in-JVM ES).
- Multi-tenant by construction: tenant = Postgres schema, per-tenant credentials, metering
  hooks at the protocol boundary.

**Non-goals**
- No query-language extensions, no new Node API. Parity first.
- No sandboxing changes to the app runtime (separate track).
- No message broker / no Temporal-class dependency. The data plane is exactly:
  Postgres, OpenSearch, S3-compatible object store.

## 2. Placement of data (decided in architecture discussions)

| Data class | Today | NoDB |
|---|---|---|
| Branch entries, versions, commits | ES `storage-<repo>` index | **Postgres** (system of record) |
| Node data / index-config / ACL JSON | Blobstore (sha256-keyed) | **Postgres**, content-addressed `payload` table (keeps sha256 keys and dedup) |
| Search documents, aggregations | ES `search-<repo>` index | **OpenSearch**, *derived and rebuildable*, fed by transactional outbox |
| Binaries (media, attachments) | Blobstore | **S3** via existing `BlobStore` SPI (scoped down to binaries only) |

Rationale: version JSON is small, hot, latency-sensitive → belongs with the branch/version
rows it is read with (one round trip, one transaction, PITR-coherent backups, retention =
`DELETE`). Binaries are large and streamed → object store. Search is a cache, never truth.

**Status (2026-07-21)**: the node-data/index-config/ACL row above is REALIZED — see
`nodb/BUILD-PHASE-3.md`'s Gate D, which proves it at real boot with psql ground truth (rows
in the tenant `payload` table, FK-verified, dedup observed) and confirms the file BlobStore
is idle for these segments. Binaries (row 4) were realized earlier, in
`nodb/BUILD-PHASE-2.md`'s Gate D. Branch/version/commit (row 1) has been realized since
`nodb/BUILD-PHASE-1.md`. Search documents (row 3) are realized as of `nodb/BUILD-PHASE-4.md`
(2026-08-07): OpenSearch behind NoDB, documents stored durably in `search_document` and
replayable — see the phase-numbering note under §9 below.

## 3. The SPI

### 3.1 What the code says the seam is

`core-repo`'s service layer (`NodeStorageService`, `BranchService`, `VersionService`,
`CommitService`, `IndexDataService`, `NodeSearchService`) is already ES-free. Everything
ES funnels through four interfaces plus the blob layer:

1. `StorageDao` (generic doc store for branch/version/commit records)
2. `SearchDao` (search execution)
3. `IndexServiceInternal` (index admin: create/delete/close/open/mapping/refresh/health)
4. `SnapshotService` (ES fs-snapshot based)
5. `NodeVersionService`/`BinaryService` → `BlobStore` (payloads)

### 3.2 SPI shape: typed records, not generic documents

We do **not** promote `StorageDao` as-is. Its generic `StoreRequest` shape encodes
ES-isms (parent/child routing, doc-type strings) that a relational backend would have to
reverse-engineer. Instead the SPI is typed at the level the storage documents actually
have (see §4): `BranchEntryRecord`, `VersionRecord`, `CommitRecord`. The adaptation cost
in XP is confined to the `*StorageRequestFactory` layer, which is small and mechanical.

`SearchDao` is kept nearly as-is (`SearchRequest`/`SearchResult` are already ES-free and
carry the XP query AST); translation to the actual engine happens inside the backend.

`IndexServiceInternal`'s ES-shaped semantics (`waitForYellowStatus`, `isMaster`,
close/open-index) do **not** cross the SPI; they collapse into backend-internal concerns.

SPI surface (drafts in `nodb/spi/`):

- `StorageBackendProvider` → `StorageBackend` (OSGi service, selected by config)
- `NodeStore` — transactional branch/version/commit/payload operations
- `NodeSearchIndex` — index/delete search docs, `search(SearchRequest)`, per-repo lifecycle
- `RepositoryStorageAdmin` — create/delete repo storage, exists, stats
- `SnapshotStore` — snapshot/restore/list/delete (per repo or full tenant)
- Binaries stay on the existing `BlobStore` SPI (S3 provider).

### 3.3 Consistency contract (the hard compat requirement)

`RefreshMode.{STORAGE, SEARCH, ALL}` and per-request `forceRefresh` are relied on by
~15 command classes for read-after-write. Mapping:

- **STORAGE** → no-op in NoDB. Postgres transactional visibility is strictly stronger
  than an ES refresh. `forceRefresh` on branch ops likewise becomes a no-op.
- **SEARCH** → *wait-for-checkpoint*: every write commits an `outbox` row with a
  monotonic sequence; the indexer applies outbox → OpenSearch and advances a checkpoint;
  `refresh(SEARCH)` blocks until `checkpoint ≥ caller's last committed seq`, then issues
  an OpenSearch `_refresh` on the affected indices. Same observable semantics as today,
  stronger on the storage side.
- Reads that today hit the storage index (`getById`, `getByPath`, children listing,
  branch entries) are served **directly from Postgres** and become strongly consistent.

The outbox is written in the same transaction as the branch/version rows, so the search
index can lag but can never miss a committed write, and is rebuildable from
`branch_entry` + `payload` at any time (also the recovery story for index corruption
and the upgrade story for OpenSearch major versions).

## 4. Postgres schema

See `nodb/schema/schema.sql`. One **schema per tenant** (never database-per-tenant: PG
pools are per-database, and per-cell pooling is a core property). Within a tenant,
`node_version` and `branch_entry` are **LIST-partitioned by repo — one partition per
repo**, created/dropped with the repo: each repo physically owns its version and branch
tables under one logical parent, so repo delete and in-place restore are partition DDL
(instant, matching today's index-per-repo semantics) while queries hit the parent with
pruning. `payload` stays tenant-shared and unpartitioned by design (cross-repo dedup,
cheap cloning). Highlights:

- `payload(hash PK, bytes)` — content-addressed, keeps today's `sha256:` BlobKey format
  so dedup (index-config/ACL segments especially) and dump formats carry over.
- `node_version` — mirrors today's VERSION document fields 1:1 (versionid, nodeid,
  nodepath, timestamp, blob keys→hashes, binary keys, commitid, attributes).
- `branch_entry` — mirrors the BRANCH document (PK `(repo, branch, node_id)`,
  unique `(repo, branch, node_path)`, generated `parent_path` column for children
  listings — replaces the ES path queries on the storage index).
- `node_commit` — mirrors the COMMIT document.
- `outbox` + `index_checkpoint` — §3.3.

ES parent/child (branch→version) and nodeId routing become ordinary FKs and indexes.

## 5. Search backend (OpenSearch)

Feature inventory that must reach parity (from the current translator layer):

- **Queries:** term, in, range (numeric/string/instant), like, exists, fulltext/match,
  simple-query-string, ngram (edge-ngram), stemmed, path-match, boolean, not, match-all.
- **Aggregations:** terms, stats, min, max, value-count, numeric-range, date-range,
  histogram, date-histogram, geo-distance; sub-aggregations.
- **Other:** term suggester, highlighting, field + geo-distance sort, ACL filter on every
  query, weighted fulltext fields, scroll (→ `search_after`/PIT in OpenSearch),
  edge-ngram + language stemmers + ICU collation analyzers.

The translator is a **port, not a redesign**: the XP query AST is unchanged; the target
DSL moves from ES 2.x Java client builders to the OpenSearch Java client. This is the
long pole of the whole project (~100 classes) but is mechanical and is validated by the
existing query itests. Index naming: there is only ONE index kind per repo (Postgres
replaced the storage index), so no `search` discriminator: queries target the alias
`<tenant>-<repo>`, which points at generational physical indices
`<tenant>-<repo>+g<N>` (e.g. `acme-com.enonic.cms.default+g1`) — rebuilds (V2
layout swap, restore acceleration, OpenSearch
major upgrades) build g(N+1) from Postgres, double-write during catch-up, flip the alias
atomically, drop g(N). Tenant ids are control-plane-minted and constrained
(`^[a-z][a-z0-9]{2,30}$` + reserved-name blacklist: `public`, `pg_*`,
`information_schema`, `nodb`, ...), so ONE bare identity works verbatim in all three
stores (PG schema `acme`, index prefix `acme-`, S3 prefix `acme/`). Delimiters are
watertight by alphabet, not convention: tenant ids contain no `-`, so the FIRST dash is
always the tenant/repo boundary (RepositoryId is `^[a-z0-9][a-z0-9_.-]*$` — dashes, even
consecutive, are legal inside repo ids); `+` is illegal in repo ids but legal mid-name
in OpenSearch, so `+g<N>` is unambiguous and physical names can never collide with
aliases (which share a namespace in OpenSearch). Names are constructed one-way from
TenantContext; nothing correctness- or security-relevant ever parses a name back —
the authoritative alias→generation mapping is NoDB metadata. Index-per-repo
initially; shared-index-with-routing as a later density optimization for small tenants.

**Virtual / derived fields (roadmap, additive)**: `_allText` is the archetype of a pattern
the platform already uses three times without naming it — a field whose value is COMPUTED
from other fields rather than authored ("put the value of these fields into this one").
The instances differ on two axes:

- *Computation site*: XP-side at index time (`_allText` today) vs NoDB server-side
  derivation from the payload (the Phase-4 deferral; Phase 8's `_references`; the
  embedding enrichment stage).
- *Storage target and retrievability*: (a) index-only, retrievable from `_source`
  (`_allText`); (b) index-only, deliberately excluded from `_source`, recoverable from a
  cache (vectors, below); (c) **materialized into the system of record**
  (`node_version.references` + GIN, Phase 8) — readable by ordinary node reads and
  surviving an index rebuild.

The rule that binds them: **search is a cache, never truth**, so a derived value kept only
in the index MUST be regenerable, and "retrievable" is not "authoritative". A virtual-field
declaration therefore needs four parts — name, source expression over other fields, index
config, and *target* (index-only vs materialized) — where index-only is right for
search-adjacent aids like `_allText` and materialization is right when applications treat
the value as data. Declaration rides the same per-property index-config seam as
`embedding:` below (portable, versioned, app-declared).

*The retrievability pain and its actual cause*: `_allText`'s concatenated value IS present
in `_source`, and the SPI already reads arbitrary values out of `_source` via
`returnFields` (`SearchHitsFactory`) — but `returnFields` is never exposed to applications
(zero occurrences in `modules/lib/` and core-api's node package). So "you can't get the
stored value back" is an **API-surface gap, not an engine limitation**, closable by
surfacing `SearchHit.returnValues` through the query API, independently of the OpenSearch
port.

*Index-size note*: `_allText` is emitted only as ANALYZED + NGRAM items (no raw variant),
so `_source` carries the entire concatenated text TWICE. Because both are pure analyzer
derivatives of one value, multi-fields are appropriate here — unlike `_orderby`, whose
value is computed independently and therefore cannot be a multi-field. That halves the
largest field in the index and yields one canonical retrievable form.

*Also enabled by the OpenSearch move (roadmap)*: dot-expanded object mapping means the
physical index hierarchy mirrors XP's PropertyTree exactly (property keys forbid `.`, `_`
and `[]`, so the mapping is unambiguous). Plain objects flatten for querying — so
`data.x = a AND data.y = b` still matches across different array elements, exactly as
today — but marking a property set `nested` becomes a natural per-property index-config
directive plus a query construct, giving same-object matching ("the contact named Bob whose
email is @acme") that XP has never been able to express. Additive, post-parity, and it
lands most cleanly after server-side document derivation, when NoDB knows the property
structure itself.

**Vector search / embeddings (roadmap, additive)**: extends three existing seams —
(1) index config: per-property `embedding: {model, dimensions}` directive alongside
fulltext/ngram, emitted as `knn_vector` mappings on `<property>._vector` sub-fields —
the same physical-field convention as `._analyzed`/`._ngram` (model identity is
generation metadata, not field name; vectors excluded from `_source`, recoverable from
the embedding cache; chunked long-text opts into nested vectors); (2) query AST: `nearest()`/semantic
function in the existing function-expression family, hybrid lexical+vector via the
boolean structure; query-time text→vector happens in NoDB (it holds model credentials
and meters the calls per tenant); (3) the outbox indexer gains an enrichment stage —
embeddings are computed off the write path with lag reported through the existing
refresh/checkpoint contract. Two structural wins: an embedding CACHE keyed by
(content-hash, model-id) — branches/versions sharing content embed once, eliminating
the dominant cost — and model migration = generational reindex (`+g(N+1)`, alias
flip), the same machinery as an engine upgrade. **Activation is two-key**: apps DECLARE
intent in index config (portable, versioned); the tenant ENTITLEMENT (control plane)
decides if/how it is honored — apps declare a semantic *profile*, plan×profile resolves
to a concrete model (portable across plans and self-hosted; explicit pinning as
enterprise escape hatch). Not entitled → enrichment skips and `nearest()` fails loudly
(optional declared fallback; never silent degradation); late activation = outbox-driven
backfill; plan change = generational rebuild with the new model (cold cache, metered,
one-time) — an index event, never a data migration. Entitlements carry the quotas:
embedding-token budget, chunking allowed, vector RAM budget. Caveat: HNSW RAM changes
OS cell sizing (vector density becomes a quota dimension); filtered k-NN must be
parity-tested against shared-index and V2 branch-membership layouts.

**Ephemeral branches** (fork draft → edit → merge → drop): on the PG side these are cheap
by construction — fork copies narrow rows only (payloads/versions shared via content
hashes), and branch create/drop is sub-partition DDL (see schema.sql). In the search
index they are O(branch) in the V1 parity model (doc per node×branch): fork triggers a
background parallel bulk-index; the branch serves get/getByPath/children instantly from
PG and carries `INDEXING` status until queries catch up. **V2 optimization** (motivated
by branch churn): one doc per distinct VERSION with branch membership on the doc —
deduplicates the index across branches (draft/master/temp mostly share versions) and
makes fork a metadata operation (ultimately O(1) with entered/left-seq membership
encoding). V2 is an index-layout swap only: system of record, SPI, and apps untouched;
deployed as a rebuild — exactly what "search is derived" is for.

## 6. Snapshots, vacuum, dump

- **Backup/restore — three layers:**
  1. *Continuous cell PITR* (physical): PG base backup + WAL archiving; disaster
     recovery for a whole cell, restore to any second. Not customer-facing granularity.
  2. *Manifest snapshots* (logical, per repo/branch/tenant): the repo's narrow rows
     (branch entries, versions, commits) + the set of referenced content hashes + the
     outbox position, taken under repeatable-read `COPY` without blocking writers.
     Content-addressing makes these INCREMENTAL by construction — cost proportional to
     delta, not repo size — and collapses the historical snapshot-vs-dump split: kept
     in-cell it is a snapshot; exported with its referenced content it is a portable
     dump. Also enables cheap repo cloning across repos/tenants/environments.
  3. *Immutable content + delayed GC*: `payload` and S3 binaries are append-only;
     their protection is retention discipline (vacuum grace window; optionally S3
     object lock), not copying.
- **Complete tenant backup** (`.ntb`, streamed via BulkTransfer): the manifest snapshot
  at tenant scope. One repeatable-read transaction captures ALL repos' rows — a
  cross-repo consistent point-in-time, impossible in today's ES+blobstore split —
  plus the tenant-level payload content packs (dumped once, shared pool) and the
  binary manifest. Two modes: *referential* (frequent/cheap; binary bytes stay in the
  bucket under GC grace + optional object lock) and *self-contained* (adds binary
  packs; portable to any NoDB installation — doubles as cell/region migration and
  customer-offboarding export). Deliberately excluded: OpenSearch (rebuilt; manifest
  records the outbox seq to catch up to), outbox/checkpoint (transient), credentials
  (re-minted on restore), control-plane records (re-registered). Completeness test:
  archive + software + fresh infra = running tenant. Restores are proven by periodic
  automated drills into an ephemeral tenant with referential-integrity verification.
- **Single-repo restore** (row-scoped, never disturbs neighbor repos; no index closing
  or cluster restarts): in-place (drop partitions, reload, reindex — only that repo
  briefly unavailable), side-by-side + **atomic swap** (rows load under a new surrogate
  `repo_key`; verify; swap = update two `repo_id` mapping rows in one transaction while
  the search alias flips generation — repos are named via surrogate keys precisely so
  swap/rename never rewrites rows), or *surgical PITR* — control plane spins an
  ephemeral PG from the cell WAL archive at time T, extracts one repo's rows as a
  manifest, imports into the live cell. Per-repo point-in-time restore without a
  pre-existing snapshot. Branch-level restore works identically.
- **Replaces today's `es snapshot` + restic ritual**: snapshots are restic-shaped
  natively (hash-addressed content packs + manifest) and land in object storage as one
  self-consistent artifact — offsite = bucket replication (or restic over the snapshots
  prefix, now copying rather than creating consistency).
- **Restore at scale**: row load is streaming `COPY` (minutes for millions of versions);
  search reindex is the long pole and is decoupled — after row load the repo is
  immediately correct for get/getByPath/children (Postgres), while a parallel
  partitioned bulk reindex rebuilds OpenSearch; the repo carries an `INDEXING` status
  and refresh(SEARCH)/queries wait or report it rather than serving silently incomplete
  results. Engine-level index snapshots may later shortcut reindex for the largest
  repos (optimization only — never the correctness path). Mega-repos: dedicated cells,
  optionally hash sub-partitioning inside the repo's partition — invisible above the SPI.
- **Vacuum**: version retention becomes SQL (`DELETE` old `node_version` rows; `payload`
  rows garbage-collected when refcount from versions reaches zero — an indexed FK query,
  not a blobstore sweep). Binary GC on S3 keeps today's mark-and-sweep against
  `node_version.binary_hashes`.
- **Dump/export**: `RepoDumper` already reads through `NodeService` + blob keys, so dumps
  work unchanged — and remain the **migration path**: dump on an ES-backed instance,
  load on a NoDB-backed one. Blob-key compatibility (§4) is what makes this seamless.

## 7. NoDB server (multi-tenant)

- Engine = plain Java library (no OSGi requirement inside NoDB). Server = engine + gRPC
  (`nodb/proto/nodb.proto`), packaged as the `enonic/nodb` container.
- XP-side: `nodb-client` is the ONLY NoDB binding — a thin gRPC client. XP never
  holds database credentials in any topology; it holds a NoDB endpoint + tenant token.
- Dev mode (`nodb dev`): the server process supervises local Postgres + OpenSearch child
  processes (managed downloaded binaries; data under the NoDB home dir; no Docker).
  `enonic sandbox start` = ensure local NoDB is running → provision a tenant for the
  sandbox → boot XP with endpoint + token. Sandbox delete = drop tenant schema; sandbox
  export = per-tenant dump.
- Tenant identity: mTLS or bearer token per XP runtime → resolved to a tenant schema +
  OpenSearch namespace + S3 prefix. NoDB owns the connection pools per cell (fixes the
  many-JVMs-vs-Postgres connection fan-out) and is the natural metering/quota point.
- NoDB server is stateless; scale horizontally per cell. Cells (tenant→Postgres cluster
  mapping) are control-plane metadata, out of scope here.
- **Connectivity**: data plane is INTERNAL and per-cell — the endpoint is issued with
  the tenant token (tenant→cell applied once at provisioning; no global router in the
  data path). gRPC needs L7-aware balancing: client-side round_robin over headless DNS
  (default) or Envoy; a plain L4 LB pins long-lived HTTP/2 connections and starves new
  replicas. Management plane (CLI, console, remote dev) reaches the same fleets through
  one authenticated external gRPC ingress (TLS + token/mTLS, optional VPN/allowlist);
  self-hosted installs usually expose nothing externally.
- **Scaling model — three verbs**: (1) replicas (kubectl/HPA, identical config) scale
  RPC, indexing/enrichment, bulk transfer, feed fanout — background work self-distributes
  via per-tenant DB leases (dead replicas' leases expire and are reclaimed); guard the
  connection budget with a pooler per cell + replica-aware pool caps, or adding replicas
  REDUCES PG throughput. (2) The cell's stores scale by their own means (PG replicas /
  bigger primary; OS data nodes). (3) Cell saturation = control-plane verb: add a cell,
  migrate tenants, or promote hot tenants to dedicated cells. §7.1 metrics identify
  which verb applies (gRPC latency vs pool saturation vs outbox lag).
- Small self-hosted production = XP + one NoDB process (container or bare) pointing at
  the customer's PG/OS/S3. Two processes instead of one is the accepted cost of never
  distributing DB credentials to runtimes and of a single tested storage path.

### 7.1 Observability

- **Prometheus metrics** on the ops port (7701, `/metrics`), via Micrometer (same stack
  XP already uses). Inventory: gRPC request rate / error rate / latency percentiles per
  method; per-tenant request and byte counters (tenant label only — repo-level label
  cardinality is deliberately avoided); HikariCP pool stats per cell; PG/OS/S3 client
  latencies; indexer throughput; JVM/process metrics.
- **Headline SLO: outbox lag** — `nodb_outbox_lag_seconds` (age of oldest unapplied entry)
  and `nodb_outbox_lag_entries` per indexer. This is the direct measure of the
  refresh(SEARCH) contract; alerting on it is alerting on read-your-writes health.
- **Health**: `/health/live` + `/health/ready` (ready = PG reachable, indexer running,
  OS reachable) for K8s probes; dev mode surfaces supervised-child status here too.
- **Tracing**: OpenTelemetry context propagated from XP through gRPC into JDBC/OS/S3
  spans — a slow page in XP resolves to the exact storage call that caused it.
- **Metering ≠ metrics**: billing/quota counters (requests, stored bytes, egress) are
  durable per-tenant rows in NoDB's own schema, aggregated by the control plane;
  Prometheus is for operations, not invoicing.

### 7.2 Security and tenant scoping

**Channel.** All XP↔NoDB traffic is TLS. Runtime authn is mTLS (per-runtime cert) or
short-lived JWTs issued by the control plane (audience `nodb`; claims: `tenant_id`,
scopes; validated offline against the control-plane key — no per-request callout).
A credential is bound to exactly ONE tenant. Tenant id never appears in request
payloads; a gRPC interceptor resolves the authenticated identity to a `TenantContext`,
and all name derivation (schema, index prefix, S3 prefix) happens in one code path from
that context. Revocation = control plane rotates/revokes the credential.

**Identity layers.** NoDB has NO user store. Three layers: XP end users (XP id
providers, unchanged — never hold NoDB credentials); platform humans (devs/admins/staff
— accounts, org membership, and roles live in the CONTROL PLANE, which mints short-
lived tokens: same tenant claim, per-user `subject` for audit); services (runtime
tokens). Two users on one tenant = two tokens with the same tenant claim — NoDB counts
tenants, not people. NoDB's config file holds only infra URLs, cell identity, and the
issuer public key(s) (self-hosted: your own OIDC issuer or CLI-bootstrap key; `nodb
dev` auto-trusts the local CLI) — never users, tenants, or permissions. Break-glass is
an ISSUANCE POLICY, not a mechanism: the control plane normally refuses staff tokens
for customer tenants; the break-glass flow (ticket + approval) mints a short-TTL
staff-marked token, and the issuance event itself lands in the customer's audit log.
Personal (human) and service (runtime/CI) tokens are distinct classes: personal tokens
can be environment-asymmetric (operator on dev/clone tenants, read-only or nothing on
prod); runtime tokens never pass through human hands. QoS rides the scope claim:
runtime-class traffic is prioritized, tooling-class traffic rate-limited per subject —
direct dev access can never move production latency. Attribution is end-to-end:
issuance logged (who/when/scope), every operation stamped with subject + token id.

**Two-layer authorization.** NoDB enforces the TENANT boundary only; intra-tenant
authorization decomposes into four steps in three places: XP RESOLVES user→principal
keys (id providers) and ASSERTS them on the request; NoDB mechanically APPLIES the ACL
filter (`readPermission ∈ asserted principals`) into every data-plane query — never
optional, never client-suppressible; OpenSearch EVALUATES it at query execution (it
must run in-engine: post-filtering would corrupt pagination, totals, aggregations).
Search docs carry read-permission principal keys indexed from the ACL payload;
`applyPermissions` reindexes affected docs, as today. Point reads (get/getByPath) skip
OS: XP evaluates NodePermissionsResolver in-JVM against the (cache-hot, deduplicated)
ACL payload. NoDB never RESOLVES identity or decides access — it has no users; a
runtime asserting false principals can only over-expose its own tenant to its own
users, the identical trust as today's in-JVM filter construction. WRITE/MODIFY/DELETE
permission checks run entirely in XP before the WriteBatch is sent — NoDB does not
ACL-check writes (duplicated policy would drift, and asserted principals make it
theater against a rogue runtime; the rogue-runtime containment is the tenant boundary).
There is NO filter bypass and no filter-mode flag: the indexer injects
`role:system.admin` into every doc's read-keys projection, so "admin sees all" is an
indexed FACT — elevated contexts, management tools, and the console all assert explicit
principals through the one uniform query path (least privilege possible; support can
impersonate a user's exact principal set for why-can't-X-see-this debugging, subject-
stamped in audit). ACL payloads are opaque to the STORE; the INDEXER has a versioned
projection over them (read-keys extraction — where the admin-key injection lives). Rule for
evolving the permission model: a permission stays in the ACL iff it filters RESULT
SETS (must be indexable set-membership — READ can never leave); permissions gating
ACTIONS (e.g. PUBLISH, planned to move to XP-side policy) can become policy — evaluated
in XP with workflow context, invisible to NoDB (ACL payloads are opaque bytes), with
the bonus that rights changes stop causing ACL-payload churn and reindex storms.

**Postgres.** Tenant = schema (`<tenant>`; ids are control-plane-constrained, see §5).
Defense in depth: pooled connections run
as a low-privilege service role; on checkout NoDB issues `SET LOCAL ROLE <tenant>`,
and each tenant role has USAGE on its own schema only. A wrong-schema query — bug,
injection, or otherwise — fails with `permission denied` in the database itself rather
than leaking rows. No cross-schema FKs; per-schema dump = tenant export/migration unit.

**OpenSearch.** Indices are `<tenant>-<repo>` (alias over generational physical
indices, see §5), names derived only from `TenantContext`. OpenSearch is network-reachable from NoDB alone (runtimes have no
route); NoDB is its sole client. The future shared-index density mode adds a `tenant`
field + per-tenant filtered alias, with the filter injected server-side in NoDB.

**Object storage.** Key layout: `<bucket>/<tenant>/<segment>/<sha256-key>`. Only NoDB
holds S3 credentials. Large binaries bypass NoDB's bandwidth via presigned URLs that are
object-scoped and time-limited; when presigning, NoDB uses STS session credentials with
an inline policy restricted to the tenant prefix, so even a key-derivation bug cannot
mint a URL outside the tenant. Content-address dedup is deliberately PER-TENANT (hash
under the tenant prefix): global dedup would create a cross-tenant existence oracle and
entangle deletion/GDPR erasure across tenants.

**Audit.** Platform audit is a system TABLE (`audit_log`) in each tenant's schema —
never a system tenant (cross-tenant concentration, lifecycle entanglement) and never a
repo (would inherit node-API writability/versioning). Written only by NoDB internals in
the SAME transaction as the recorded operation (atomic op+trail); append-only,
optionally hash-chained; read via management-plane RPC into the console; rides tenant
backup/migration/offboarding/deletion automatically. Control-plane events (issuance,
break-glass, plan changes) are mirrored in so the customer reads ONE log. XP's
application audit (core-audit) stays tenant content, unchanged — complementary stream
with runtime-level trust. Fleet-wide security monitoring is a log stream to the
observability stack, not a store.

**Posture.** Postgres/OpenSearch/S3 sit on a network segment reachable only by NoDB;
tenant runtimes reach only NoDB's gRPC port. Admin operations are audit-logged per
tenant; metering counters are keyed by the authenticated tenant, never by claimed ids.

### 7.3 Management plane, CLI, and the change feed

NoDB's resource hierarchy is **Tenant → Repo → Branch**; nodes are data-plane payload,
not a management noun. Two API planes, two credential scopes (same JWT machinery):

- **Data plane** (runtime scope, held by XP): node read/write/search within the tenant.
- **Management plane** (operator scope, held by humans/CI via `enonic auth`): repo and
  branch lifecycle, snapshot/restore, dump/load and export/import as server-side
  streaming ops, stats/quotas, reindex, vacuum. A runtime credential cannot drop a repo.

**Enonic CLI** becomes a management-plane client: `enonic repo|branch|snapshot|dump ...`
work identically against local `nodb dev` (implicit credentials), self-hosted NoDB, or a
cloud cell — and require no running XP instance (ops on scaled-to-zero tenants).

**Change feed**: runtimes subscribe to their tenant's committed-change stream (produced
by the outbox) for in-process cache invalidation. This replaces the Hazelcast event bus
for storage events between XP replicas, and it is what makes external writers (CLI
load/import, console) safe while runtimes are live — they are ordinary writers whose
changes invalidate caches like anyone else's.

**Realtime (roadmap)**: two tiers. *Observation* — the change feed IS the realtime
source for committed changes (live lists, preview refresh, "someone saved" banners):
NoDB streams tenant-wide, XP terminates websockets and applies node-ACL filtering
(two-layer authz). A filtered Watch (repo/branch/path scope) makes this cheap.
*Collaboration* (Google-Docs/Sanity-style co-editing) is XP-side by necessity — it
needs user identity, ACLs, and schema, none of which NoDB knows: CRDT sessions at
property-path granularity with document-affinity routing across replicas; ephemeral
presence never touches storage; sessions CHECKPOINT into NoDB as ordinary debounced
writes (version history stays meaningful; non-participants learn via the change feed;
inactive documents behave exactly as today). NoDB additions in support: conditional
writes (CAS on expected version — cheap `WHERE version_id = :expected`, useful to all
API clients) and the filtered watch. NoDB moves data; XP mediates people.

**Console**: most of Data Toolbox's functionality (repo/branch browsing, node/version
inspection, query console, dump/snapshot management) is storage-layer work and moves to
a first-party NoDB console on the management plane. It is an OPERATOR tool: operator
tokens assert explicit principals (typically `role:system.admin`, or narrower for
least-privilege support access) through the same always-applied ACL filter as every
other caller — no bypass path exists (§7.2). Features that execute
app code or fire runtime events (media reprocessing, content-layer hooks) stay XP-side —
the usual trust boundary: NoDB tools touch data, runtime tools touch behavior.

### 7.4 Personas (cloud offering)

One principle: everyone works through the same two APIs (data + management plane);
personas differ in TOKEN SCOPE, never protocol — no side doors, including for Enonic.
The exposure line is data mechanics (product: repos/branches/versions/queries — fully
exposed) vs operational mechanics (factory: cells/shards/pools — never exposed outside
Enonic). **Developers**: the whole storage model via NoDB console (browse/inspect/
diff/query), CLI (dump/clone/remote-sandbox), change feed for their own tooling —
scoped to dev/clone tenants (+opt read-on-prod); limits surface as plan quotas, never
infra symptoms; our own tools use the same APIs (dogfooding = API quality). **Customer
admins** (console, tenant scope): self-service restore (repo→point-in-time, side-by-
side, swap) as flagship; metering that matches the invoice; token mint/rotate/revoke;
audit log incl. Enonic's own accesses; entitlements + declared-vs-granted upsell
surface. No tuning knobs — the plan IS the tuning interface. **Enonic ops** (control
plane + infra): cells, placement/migration, scaling verbs, fleet/schema upgrades,
cell PITR. Tenant-content-blind by default; content access is BREAK-GLASS — explicit,
time-boxed, audited, and visible in the customer's own audit log.

## 8. Runtime environment: no OSGi inside NoDB

NoDB is a **plain Java service** — no OSGi, no Spring; explicit wiring, gRPC-java,
HikariCP, JDBC + OpenSearch + S3 clients. OSGi exists in XP to manage third-party code
arriving at runtime; NoDB runs no tenant code and has a closed, build-time dependency
graph, so OSGi would add only ceremony (cf. the `repack-elasticsearch` tax). Fat-jar
packaging, AppCDS-friendly startup; native-image/jlink remain possible.

OSGi touches the project only at the XP boundary: `nodb-client` and the
transitional `storage-elasticsearch` backend are ordinary XP bundles (thin; SPI in,
protobuf out). Since XP consumes NoDB exclusively over gRPC, the engine and its
dependencies (JDBC drivers, OpenSearch/S3 clients) never need OSGi-fication at all —
no repeat of the `repack-elasticsearch` tax in any form.

Consequence: the storage layer stops anchoring XP to Felix. If the XP runtime later moves
off OSGi, NoDB and the data plane are outside the blast radius.

## 9. Delivery phases and gates

| Phase | Deliverable | Gate |
|---|---|---|
| **0** | SPI module in XP; current embedded-ES code refactored to implement it | Full XP test suite green, no behavior change; ships in an 8.x | **DONE 2026-07-18** — branch `storage-spi-phase0`, gates 0/A–D green (full build 729 tasks + both itest suites; only pre-existing icuSort failures). `core-storage-spi` created; StorageDao/SearchDao zero consumers outside the ES package; `storage.backend=elasticsearch` selection property in place; arch test enforces both boundary directions. Gate E (module extraction) deliberately deferred. |
| **1** | NoDB engine + gRPC server + `nodb-client`; `NodeStore` on Postgres, binaries on S3; tenant model END-TO-END (token→TenantContext as the only entry, trivial dev issuer; schema-per-tenant + SET LOCAL ROLE) | Storage-level itests green against NoDB, run DUAL-TENANT with cross-tenant isolation assertions |
| **2** | OpenSearch index + translator port; outbox/indexer; refresh checkpoint | Full core-repo + itest suites green; golden-query corpus diffed against ES backend | **DONE 2026-08-07** — branch `nodb-phase4-opensearch` (tracked as `BUILD-PHASE-4.md`, gates 0/A–G). DSL-on-the-wire (NoQL→DSL renderer in core, server-side translator), XP-shipped index documents via outbox/indexer/`awaitRefresh` (§3.3 live), per-repo alias→`+gN` generations, ICU keys computed in NoDB (icu4j 78.3 pinned, stock OpenSearch 3.7.0 image). Gate F: full itest-core + itest-core-content green in BOTH modes, 129-query corpus 0 FAILURE, zero embedded ES in itests. Gate G: production `backend=nodb` boot starts zero Elasticsearch (config-gated ES activator + nodb `IndexServiceInternal` + nodb-aware liveness probe), live CS smoke + restart persistence, rebuild drill green (`POST /admin/rebuild-search-index` replays `search_document`), PG+OpenSearch baseline recorded in `bench/RESULTS.md`. Main deferral: server-side document derivation from payloads (docs are XP-shipped, stored, replayable). |
| **3** | Snapshots, vacuum, dump/load verified; retention policies | Ops parity + dump-based migration round-trip test |
| **3.5** | Storage-index query family → SQL (`BUILD-PHASE-3.5.md`): version history, branch diff / resolve-sync-work, commit get/find served from `node_version`/`branch_entry`/`node_commit` in nodb mode; repo-scoped version identity (absorbs Phase 4 prerequisite P2); three new indexes via tenant migration 002 | Curated itest list green in both modes (both-backend diff corpus identical); live Content Studio publish/version/compare smoke clean on the hybrid stack | **DONE 2026-08-05** — branch `nodb-phase35-version-sql` |
| **4** | Control-plane integration (real issuer, membership, break-glass policy), metering/QoS by scope, external ingress, Docker/compose, Helm | Quota/QoS tests; issuance-to-audit attribution verified end-to-end |
| **5** | Migration tooling, dual-run validation, embedded-ES deprecation | Pilot tenant migrated |

Phase 2 is the long pole. Phases 0–1 are low-risk and independently valuable
(Phase 0 alone removes the last three ES imports from core-repo's public layer).

**Execution-track note (2026-07-21)**: the table above is this document's original,
coarse-grained phase plan. The actual build tracks its work in finer-grained,
separately-numbered phases in `nodb/BUILD-PHASE-0.md` through `BUILD-PHASE-3.md` (0 = spike/
binary-vs-payload sequencing decision, 1 = NodeStore-on-Postgres storage-SPI read/write path,
2 = binaries onto S3, 3 = node payloads — node-data/index-config/ACL — onto Postgres); these
numbers do **not** map 1:1 onto the table's Phase 1–5 (e.g. this table's "Phase 3: Snapshots,
vacuum, dump/load" is downstream of, and distinct from, `BUILD-PHASE-3.md`'s payload work).
As of this note: `BUILD-PHASE-0/1/2/3.md` are all gate-complete (Gate D green in each) —
this table's row 1 (NodeStore on Postgres + binaries on S3 + tenant model) is functionally
done; row 2 (OpenSearch) is done as of 2026-08-07 (`BUILD-PHASE-4.md`, see the row above;
`BUILD-PHASE-3.5.md` delivered the version-history SQL slice between them). Reconcile phase numbering with
the `nodb-design` tracking branch's risk register (#12/#13 there) before renumbering this
table — not attempted here to avoid clobbering that branch's own bookkeeping.

## 10. Risk register (self-review 2026-07-17)

Cross-cutting findings surfaced while building (fixed defects whose class deserves an
audit, plus open questions needing a decision or measurement) are collected in
[`FINDINGS.md`](FINDINGS.md).

1. **Protocol atomicity (BUG)**: SPI promises atomic version+branch+outbox writes; proto
   has only per-record RPCs. Add a `WriteBatch` RPC (one transaction, one Ack/seq).
   **Resolved (engine layer) 2026-07-17**: `com.enonic.nodb.engine.store.WriteService.write`
   commits versions+branch entries+commit+outbox as one JDBC transaction (caller-supplied
   connection from `Tx.inTenantTx`); hash-only payload references are validated before any
   row is written, so an unresolvable hash returns `WriteBatchResponse.needPayload` with
   nothing persisted, with no separate rollback step needed. Proven by
   `engine/src/test/java/com/enonic/nodb/engine/store/WriteBatchTest.java` (atomicity via
   forced duplicate-key failure, NEED_PAYLOAD + retry, branch fork, repo drop, outbox
   monotonicity). **Resolved (gRPC layer) 2026-07-17**: the wire-level `WriteBatch` RPC
   (slice-1 step 5) is implemented in `server/.../service/NodeStoreService`, delegating to
   the same `WriteService.write` inside `Tx.inTenantTx` — no separate atomicity logic at
   the gRPC boundary. Proven end-to-end by
   `server/src/test/java/com/enonic/nodb/server/NodbServerIntegrationTest.java`.
2. **Latency chattiness**: in-JVM sub-ms → 0.5–2ms/hop; XP storage calls are chatty.
   Client cache + feed invalidation is Phase 1 ARCHITECTURE; add perf gates (p95 get/
   save, page-render vs embedded-ES baseline) to Phases 1–2. Biggest adoption risk.
   **Baseline established (slice 1) 2026-07-17**: `bench/RESULTS.md` — over real loopback
   gRPC + containerized PG on a dev laptop, point reads p50 ~1ms (getBranchEntry by
   path 982µs / by id 1085µs), single-node writeBatch p50 1440µs, getChildren/getVersion
   ~3.5–3.9ms; seed ~1700 nodes/s. A floor for relative comparison, not an SLO; the
   embedded-ES comparison and per-op perf gates remain to be added in Phase 1–2.
3. **Query AST wire format** (open q. #1) — Phase 2 stands on it; includes per-query
   principal/ACL propagation (size, caching, trust statement). Spike before the port.
4. **Binary UPLOAD path undefined**: staged upload (presign→upload→confirm→reference)
   or writes-through-NoDB; otherwise the binaries-before-commit invariant (four backup
   mechanisms depend on it) is unenforceable.
5. **Outbox trim vs many consumers**: cursor registry + TTL + bounded retention +
   defined resync ("cursor too old → flush and re-subscribe").
6. **Catalog pressure**: tenants × repos × branch sub-partitions = 10k+ relations/cell
   (relcache, autovacuum, pg_dump). Branch sub-partitioning becomes conditional
   (churn/size threshold); relations-per-cell is a capacity metric.
7. Per-tenant ordered outbox caps mega-tenant indexing — ordering is only per-node;
   shard consumers by node-id hash.
8. Subtree move: O(descendants) path rewrite in one tx; needs DEFERRABLE unique(path)
   and stated semantics.
9. Referential backups expire with GC retention — enforce an explicit validity horizon.
10. Loose ends: node_commit/audit not partition-scoped (repo drop not purely DDL);
    custom repo index definitions / putIndexMapping path dropped without replacement;
    INDEXING-vs-awaitRefresh semantics; schema-migration orchestration across N tenant
    schemas during rolling upgrades.
    **Two sub-items resolved by the `BUILD-PHASE-3.md` execution track (2026-07-21)**,
    referred to elsewhere as "#10b" (the payload FK) and "#10e" (the payload format spec) —
    those labels do not appear as lettered sub-bullets in this copy of the risk register
    (this document has not been kept in sync with the `nodb-design` tracking branch's own
    numbering; reconcile there before relying on the letters). What was resolved:
    - **Payload FK**: `node_version.{node_data_hash,index_config_hash,acl_hash}` now carry
      `REFERENCES payload(hash)` (re-added in Gate A after Phase 1 Gate C had dropped it),
      enforced live (`\d xpgate3.node_version` on a running tenant) and confirmed with a
      `NOT EXISTS` sweep returning 0 for all three columns, before and after new content, in
      `BUILD-PHASE-3.md`'s Gate D.
    - **Payload format**: node-data/index-config/ACL payload bytes are plain JSON,
      parseable without any XP serializer class — a closed, 14-entry type-tag table (e.g.
      `"type":"Reference"`) is the only signal distinguishing a `Reference` from a same-
      shaped `String`; documented as NoDB's own v1 format spec in Gate 0 and confirmed
      against real bytes read via `psql` (no XP code involved) in Gate D.
11. **Binary/payload GC blocked in nodb mode** (found at Phase-2 Gate D, reconfirmed
    structurally in Phase-3 Gate 0): XP's existing blob-vacuum commands
    (`BinaryBlobVacuumTask`, `VersionTableVacuumCommand`) both route through
    `NodeService.findVersions`, served by an Elasticsearch storage-side index
    (`storage-<repo>`) that nodb mode never creates (version-history queries are
    out of scope through Phase 1's design). Payload GC is explicitly **deferred to
    Phase 5** (Gate 0 decision, alongside binary GC) — safe because content-addressing
    makes GC fully retroactive, at the cost of interim storage growth; `BUILD-PHASE-3.md`
    Gate D records a payload-row/byte + S3-object baseline (57 rows / 29,469 bytes / 0
    S3 objects as of 2026-07-21) as the pre-GC starting point.

## 11. Open questions

1. Exact `SearchRequest` payload across the gRPC boundary: serialize the XP query AST
   (keeps translation server-side, one implementation) — preferred — or translate
   client-side and pass engine DSL (couples clients to OpenSearch).
2. ~~Embedded-mode / sandbox story~~ **Resolved:** there is no embedded XP binding — XP
   always speaks gRPC to a NoDB process. `nodb dev` supervises real local Postgres +
   OpenSearch child processes (managed binaries, no Docker); every sandbox is a tenant in
   the one local NoDB, so N sandboxes share one PG + one OS — lighter than today's
   per-sandbox in-JVM ES. No SQLite/H2/Lucene-lite variants: a second dialect or second
   search backend trades permanent parity risk for little gain. Topologies:
   (a) dev = local `nodb dev`; (b) self-hosted = XP + one NoDB process + customer's
   PG/OS/S3; (c) shared/multi-tenant cloud = NoDB server fleet per cell.
3. `IndexDataService.IndexDocument` currently lives under the `elasticsearch` package —
   relocate as part of Phase 0.
4. Shared-index density mode for OpenSearch: ship in Phase 4 or defer.
