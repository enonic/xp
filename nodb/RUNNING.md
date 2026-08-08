# Running XP on NoDB — manual setup guide

How to compile NoDB, start it against Dockerized Postgres + MinIO + OpenSearch, provision a
tenant, mint a token, and boot XP with `backend=nodb`. Every command below is taken from the
recipes that actually ran in the Phase 1–4 gate smokes (see BUILD-PHASE-*.md for the
evidence they produced). `nodb/dev-stack.sh` automates all of it; this file is the manual
equivalent and the operator reference.

Since Phase 4 Gate G the dev-stack starts OpenSearch **by default** (`WITH_OPENSEARCH=1`
is now the baked-in default): in nodb mode search runs on OpenSearch, so a stack without
it has no search at all — every NodeSearch RPC answers `UNIMPLEMENTED` and Content Studio
is unusable. `WITH_OPENSEARCH=0` remains as an explicit opt-out for storage-only
debugging. (Until Gate F the flag defaulted off because search still ran on XP's embedded
ES; Gate F made OpenSearch non-optional for the itest suites and Gate G for the runtime.)

**This stack stays off the standard XP ports.** XP listens on web **18080** / management
**14848** / statistics **12609** (knobs `XP_WEB_PORT`/`XP_MGMT_PORT`/`XP_STATS_PORT` in
dev-stack.sh), and dev-stack's stop/start only ever touch ITS OWN XP and NoDB processes
(matched by install/home path, not by class name) — a developer's own sandboxes on
8080/4848/2609 are never contacted, bound or killed.

**State of the world on this branch** (`nodb-phase4-opensearch`): in nodb mode, XP stores
branch/version/commit records AND node payloads (node-data, index-config, ACL) in NoDB's
Postgres, binaries via NoDB in S3/MinIO, and — since Phase 4 — **search runs on OpenSearch,
owned by NoDB**. Queries flow `app → XP (parse/normalize NoQL → JSON DSL) → gRPC → NoDB
(translate) → OpenSearch`; writes flow through the transactional outbox → indexer →
checkpoint, and `refresh(SEARCH)` keeps the read-your-writes contract (DESIGN §3.3). The
version-history/compare/commit family is SQL since Phase 3.5. **A `backend=nodb` boot starts
ZERO embedded Elasticsearch** — no ES node, no ES threads, no ES data directory (Phase 4
Gate G; Gate F proved the same for the itest suites). The full itest suites and the
129-query golden corpus are green in nodb mode.

Remaining known gaps: vacuum/GC is deferred (Phase 5) — deleted content leaves payload
rows/S3 objects behind by design, for now; snapshot/restore is ES-only (NoDB's counterpart
is Phase 5); rename-swap pushes still hit the branch-path constraint (DESIGN.md risk #8);
index documents are XP-shipped (server-side derivation from payloads is the recorded
Phase 4 deferral — the index is disposable, but rebuilding it still replays XP-shipped
documents rather than deriving from payloads).

## Prerequisites

- Docker running (Desktop or compatible).
- JDK 25 for *running* XP (the Gradle build uses its own toolchain, but the launcher
  needs a JVM matching the compiled class version). On macOS/Homebrew:
  `brew install openjdk` → `/opt/homebrew/opt/openjdk`.
- `psql` and the `aws` CLI optional but useful for verification.

All paths below assume the XP repo root as the working directory, on this branch.

## 1. Compile

```bash
# XP distro (includes the core-storage-spi + core-storage-nodb-client bundles):
./gradlew :runtime:installDist
#  → modules/runtime/build/install/{bin/server.sh,home,...}

# NoDB server (engine + gRPC server + tools):
cd nodb && ../gradlew :server:installDist -x test && cd ..
#  → nodb/server/build/install/server/{bin/server,lib/*.jar}
```

## 2. Start the infrastructure containers

```bash
# Postgres 17 (NoDB's system of record)
docker run -d --name nodb-pg -p 55432:5432 \
  -e POSTGRES_DB=nodb -e POSTGRES_USER=nodb -e POSTGRES_PASSWORD=nodb \
  postgres:17

# MinIO (S3-compatible store for binaries)
docker run -d --name nodb-minio -p 19000:9000 -p 19001:9001 \
  -e MINIO_ROOT_USER=nodb -e MINIO_ROOT_PASSWORD=nodb-secret \
  minio/minio:RELEASE.2024-11-07T00-52-20Z server /data --console-address ":9001"

# Create the bucket — NoDB does NOT auto-create it:
docker exec nodb-minio sh -c \
  'mc alias set local http://localhost:9000 nodb nodb-secret && mc mb local/nodb-binaries'

# OpenSearch 3.7.0 (NoDB's search backend — STOCK image, no plugins: NoDB computes ICU
# collation keys itself, decision D8). 512m heap is the measured dev sweet spot; the 2g
# memory limit is a cap, not a reservation.
docker run -d --name nodb-os -p 19200:9200 \
  --memory 2g \
  -e discovery.type=single-node \
  -e DISABLE_SECURITY_PLUGIN=true \
  -e DISABLE_INSTALL_DEMO_CONFIG=true \
  -e OPENSEARCH_JAVA_OPTS="-Xms512m -Xmx512m" \
  opensearchproject/opensearch:3.7.0
curl -sf 'localhost:19200/_cluster/health?wait_for_status=yellow&timeout=30s'
```

The version is pinned to 3.7.0 deliberately: it is the minor AWS OpenSearch Service runs
(it adopts every other minor, so the next legitimate bump is 3.9). Do not use the floating
`:3` tag — it resolves past what the managed target supports.

## 3. Start NoDB

```bash
export NODB_PG_URL="jdbc:postgresql://localhost:55432/nodb"
export NODB_PG_USER=nodb
export NODB_PG_PASSWORD=nodb
export NODB_PORT=7700
export NODB_OPS_PORT=7701
export NODB_KEYS_DIR="$PWD/.nodb-dev-keys"     # dev issuer keypair lives here
export NODB_OPENSEARCH_URL="http://localhost:19200"
export NODB_S3_ENDPOINT="http://localhost:19000"
export NODB_S3_BUCKET=nodb-binaries
export NODB_S3_REGION=us-east-1
export NODB_S3_ACCESS_KEY=nodb
export NODB_S3_SECRET_KEY=nodb-secret
export NODB_S3_PATH_STYLE=true                  # required for MinIO

nohup nodb/server/build/install/server/bin/server > /tmp/nodb-server.log 2>&1 &
curl -sf localhost:7701/health/ready            # {"status":"UP"} once PG + OpenSearch answer
```

`NODB_OPENSEARCH_URL` is what turns search on. Set, NoDB registers the `NodeSearch` RPCs,
creates a per-repo index (alias `<tenant>-<repo>` → physical `<tenant>-<repo>+gN`) on repo
create, runs the outbox indexer, and includes OpenSearch + indexer health in
`/health/ready`. **Unset, NoDB has no search backend at all** — every NodeSearch RPC
answers `UNIMPLEMENTED` and an XP pointed at it has no working queries (Content Studio is
unusable). That is a legitimate state only for storage-only debugging.

On first start the dev keypair is generated under `NODB_KEYS_DIR` (the server holds the
public key; the token tool below signs with the private key — **they must point at the
same directory**). This is the dev issuer; a real deployment replaces it with the
control plane (DESIGN.md §7.2).

## 4. Provision a tenant and mint a runtime token

Tenant ids must match `^[a-z][a-z0-9]{2,30}$` (lowercase alphanumeric, no dashes).

```bash
CP="nodb/server/build/install/server/lib/*"

# Create the tenant schema (applies the checksummed migrations, creates the tenant role):
java -cp "$CP" com.enonic.nodb.server.tools.TenantBootstrapTool \
  --tenant myxp \
  --pg-url "jdbc:postgresql://localhost:55432/nodb" \
  --pg-user nodb --pg-password nodb

# Mint a RUNTIME-scope token for XP (repo lifecycle is runtime-scoped by design):
java -cp "$CP" com.enonic.nodb.server.auth.NodbTokenTool \
  --tenant myxp --scope runtime --subject svc:xp \
  --keys-dir "$PWD/.nodb-dev-keys" --ttl-minutes 480
# → prints the JWT; export it:
export NODB_TOKEN="<the printed token>"
```

## 5. Configure and boot XP

```bash
# Fresh XP home seeded from the distro:
export XP_HOME=/tmp/xp-nodb-home
mkdir -p "$XP_HOME" && cp -R modules/runtime/build/install/home/* "$XP_HOME/"

# Point XP's storage at NoDB (this file's presence + backend=nodb is what activates
# the nodb client — without it, boot is byte-identical to stock XP on embedded ES):
cat > "$XP_HOME/config/com.enonic.xp.storage.nodb.cfg" <<EOF
backend=nodb
nodbEndpoint=localhost:7700
nodbToken=${NODB_TOKEN}
EOF

# Keep OFF the standard XP ports (8080/4848/2609) so this stack NEVER collides with a
# developer's own sandboxes on the same machine — dev-stack.sh does the same:
cat > "$XP_HOME/config/com.enonic.xp.web.jetty.cfg" <<EOF
http.web.port=18080
http.management.port=14848
http.statistics.port=12609
EOF

# Set the su password — MUST be {sha256}<hex>, plaintext is rejected:
SU_HASH=$(printf '%s' 'password123' | shasum -a 256 | awk '{print $1}')
cat > "$XP_HOME/config/system.properties" <<EOF
xp.suPassword={sha256}${SU_HASH}
EOF

# Boot (JDK 25 required at runtime):
JAVA_HOME=/opt/homebrew/opt/openjdk XP_HOME="$XP_HOME" \
  nohup modules/runtime/build/install/bin/server.sh > /tmp/xp-nodb.log 2>&1 &
```

The same `backend=nodb` config is what keeps the embedded Elasticsearch dark: the ES
activator reads the `com.enonic.xp.storage.nodb` PID alongside its own and skips the node
start entirely, a config-gated nodb `IndexServiceInternal` in core-repo satisfies the
components that need cluster-shaped answers, and the `/health` liveness probe accepts the
active NoDB storage client in place of the ES client services (Phase 4 Gate G). Without
the config file, none of that activates and the boot is stock XP.

## 6. Verify

```bash
# Started + all bundles healthy (look for the nodb client ACTIVE):
grep "Started Enonic XP" /tmp/xp-nodb.log
curl -s localhost:12609/osgi.bundle | grep -o '"com.enonic.xp.core.storage.nodb.client"[^}]*'
curl -s -o /dev/null -w '%{http_code}\n' localhost:12609/health   # 200 in nodb mode too

# Ground truth — node data is in Postgres, not on disk:
PGPASSWORD=nodb psql -h localhost -p 55432 -U nodb -d nodb -c \
  'SELECT count(*) AS payloads FROM myxp.payload;
   SELECT count(*) AS versions FROM myxp.node_version;'
find "$XP_HOME/repo/blob" -type f | wc -l        # → 0 in nodb mode

# Search is on OpenSearch — per-repo aliases and documents:
curl -s 'localhost:19200/_cat/aliases/myxp-*?h=alias,index'
curl -s 'localhost:19200/myxp-com.enonic.cms.default/_count'

# ZERO embedded Elasticsearch, mechanically:
jstack $(pgrep -f com.enonic.xp.launcher) | grep -c 'elasticsearch\['   # → 0
ls "$XP_HOME/repo/index" 2>/dev/null             # → absent (no ES data dir)

# Binaries land in MinIO under the tenant prefix once attachments exist:
docker exec nodb-minio mc ls -r local/nodb-binaries/myxp/ 2>/dev/null
```

A healthy boot initializes the system repos through NoDB — the psql counts are non-zero
immediately (~50 versions / ~55 payload rows from bootstrap), each system repo has a
`myxp-<repo>+g1` index behind its alias, and the FK guarantees every version's three
hashes resolve to payload rows.

`nodb/smoke.sh` drives the whole editing flow against a running stack and fails loudly on
the first broken step — login → create → query-your-write → update → publish → fulltext →
aggregation → version history → compare → rebuild drill. It is the repeatable form of the
Gate G smoke and suitable as a CI job on a booted stack:

```bash
nodb/smoke.sh          # XP_URL/OS_URL/OPS_URL/SU_PASS overridable via env
```

## 7. Rebuild the search index (operator recipe — "the index is disposable")

The OpenSearch index is a cache; PostgreSQL (`search_document`) holds every document
durably, in the same transaction as the write that produced it. If an index is lost,
corrupted, or a mapping/projection change needs a re-index, rebuild it from replay via the
ops port:

```bash
# Simulate (or suffer) the disaster — the index vanishes out-of-band:
curl -s -X DELETE 'localhost:19200/myxp-com.enonic.cms.default+g1'

# Rebuild: drop whatever generations remain, create a fresh generation behind the alias,
# replay every stored search_document row into it, refresh:
curl -s -X POST 'localhost:7701/admin/rebuild-search-index?tenant=myxp&repo=com.enonic.cms.default'
# → {"tenant":"myxp","repo":"com.enonic.cms.default","replayed":123,"tookMillis":456}

# Verify: same doc count as before, queries answer again:
curl -s 'localhost:19200/myxp-com.enonic.cms.default/_count'
```

Notes:
- The rebuild is per repo. Repos are listed by `curl -s 'localhost:19200/_cat/aliases/<tenant>-*?h=alias'`.
- The endpoint responds 409 for an unknown repo, 400 for missing parameters, 405 for
  non-POST. It is only registered when a search backend is configured.
- The ops port's trust posture is reachable-equals-trusted (same as the health endpoints):
  never expose it beyond the host/pod boundary. Real operator AuthN/AuthZ is Phase 6
  control-plane work.
- The rebuild takes the repo's index offline for the duration (delete → create → replay).
  A zero-downtime generational flip (build `+g(N+1)` behind the alias, then atomic
  `updateAliases`) is what the generation machinery is for; wiring that driver is Phase 5+
  work.
- This replays the documents XP shipped. It does NOT re-derive them from payloads —
  that is the recorded Phase 4 deferral (decision 3) — so after a projection-version bump
  the replay applies the CURRENT projection to the stored documents.

## 8. Upgrading a pre-Phase-4 tenant (3.5-era → 4)

Applies to a tenant provisioned and used BEFORE Phase 4 (the path Gate G explicitly did not
exercise — its smoke ran on fresh volumes only; proven by `Phase35To4UpgradeTest`,
BUILD-PHASE-5.md gate P2). Such a tenant has `template_version = 2`, no rows in
`nodb_system.tenant_migration` (the checksum table arrived with Phase 4 gate P3), none of
003's tables (`search_document`, `search_index`), no per-repo OpenSearch indices — and,
crucially, **no `search_document` rows for its existing content**, because XP never shipped
index documents before Phase 4. That last fact decides the shape of the whole recipe: the
§7 rebuild endpoint replays `search_document` and therefore **cannot** populate the index
for pre-Phase-4 content — it would answer `{"replayed":0}` over an empty index. The only
mechanism that (re)creates the documents is XP itself re-shipping them, which is exactly
what `IndexService.reindex(initialize=true)` does. So the sequence is: **provision → start
NoDB with OpenSearch → boot XP → XP-driven reindex per repo.**

```bash
# 0. Stop XP (keep Postgres up). If the old stack has no OpenSearch container, create one
#    now — §2's docker run for nodb-os, §3's NODB_OPENSEARCH_URL will point at it.

# 1. Re-run the bootstrap tool — the same command as §4, idempotent:
java -cp "$CP" com.enonic.nodb.server.tools.TenantBootstrapTool \
  --tenant myxp \
  --pg-url "jdbc:postgresql://localhost:55432/nodb" \
  --pg-user nodb --pg-password nodb
```

The run applies migration 003 and records its checksum, all in ONE transaction — a failed
run leaves the tenant exactly at 3.5; just re-run it. Because a 3.5-era tenant predates the
checksum table, expect one **adopt line per already-applied migration** (once, on this
first run only — the pre-GA adopt-on-first-run rule):

```
Adopting migration 001_init.sql (version 1) as the recorded baseline for tenant myxp: applied before checksums existed (pre-GA adopt-on-first-run rule)
Adopting migration 002_version_query_indexes.sql (version 2) as the recorded baseline for tenant myxp: applied before checksums existed (pre-GA adopt-on-first-run rule)
```

```bash
# Verify: template_version 3, and three checksummed rows:
PGPASSWORD=nodb psql -h localhost -p 55432 -U nodb -d nodb -c \
  "SELECT template_version FROM nodb_system.tenant WHERE tenant_id = 'myxp';
   SELECT version, name, checksum FROM nodb_system.tenant_migration
     WHERE tenant_id = 'myxp' ORDER BY version;"

# 2. Start (or restart) NoDB WITH NODB_OPENSEARCH_URL set — §3 verbatim. A 3.5-era stack
#    ran without it; without it there is no search backend at all.

# 3. Boot XP — §5 verbatim.
```

⚠ **Between XP boot and step 4, queries against pre-existing repos fail** (their index does
not exist yet) — Content Studio is not usable on them. Writes are safe throughout: they
commit to Postgres, their documents land in `search_document`, and the indexer skips them
until the repo has an index; step 4's purge-and-reship sweeps them in.

```bash
# 4. Reindex EVERY pre-existing repo through XP's management API, initialize=true.
#    List the repos and their branches:
PGPASSWORD=nodb psql -h localhost -p 55432 -U nodb -d nodb -c \
  "SELECT r.repo_id, string_agg(b.branch, ',') AS branches
     FROM myxp.repository r JOIN myxp.branch b USING (repo_key) GROUP BY r.repo_id;"

#    Then per repo (su credentials; the endpoint requires the admin role):
curl -s -u su:password123 -X POST 'http://localhost:14848/repo/index/reindex' \
  -H 'Content-Type: application/json' \
  -d '{"repository":"com.enonic.cms.default","branches":"draft,master","initialize":true}'
# → {"repositoryId":"com.enonic.cms.default", ..., "numberReindexed": <N>, ...}
```

`initialize=true` is load-bearing, twice over: it drives NoDB's idempotent
delete-then-create index pair — which is what CREATES the index, alias and `search_index`
row for a repo that predates migration 003 (NoDB log:
`Created search index myxp-<repo>+g1 behind alias myxp-<repo> (template vN, projection vM)`)
— and the reindex walk then re-ships every node's document through the normal
IndexDocuments path (`search_document` upsert + outbox row, one transaction per document),
which the indexer applies to OpenSearch. Without `initialize` the index is never created
and the shipped documents sit inert in `search_document`.

```bash
# 5. Verify — same ground truth as §6:
curl -s 'localhost:19200/_cat/aliases/myxp-*?h=alias,index'      # one alias per repo, +g1
curl -s 'localhost:19200/myxp-com.enonic.cms.default/_count'     # > 0
nodb/smoke.sh   # the full editing flow incl. fulltext, history, compare, rebuild drill
```

Version history, branch listing and compare need no migration step — 003 is purely
additive (two new, initially empty tables) and never touches the 3.5 storage rows; the P2
test asserts the pre-upgrade surfaces answer identically after the upgrade.

**If the upgrade half-completes (rollback posture):** every step is idempotent and
re-runnable in order, and no step destroys 3.5 data — there is nothing to roll back TO.
Specifically:

- **Bootstrap fails mid-run** → the single transaction rolled back; the tenant is still
  bit-for-bit at 3.5. Re-run it.
- **Reindex of a repo interrupted** → re-run that repo's reindex with `initialize=true`
  (it purges and starts over). Repos upgrade independently; the others are unaffected.
- **OpenSearch dies after documents were shipped** → they are durable in
  `search_document`; the §7 rebuild endpoint can now finish that repo without XP.
- **Binary rollback**: a pre-Phase-4 NoDB server still serves the upgraded tenant's
  storage surfaces (it never reads the two new tables), but re-provisioning from the old
  binary fails loudly with the forward-only error — the schema is never downgraded, by
  design.

Residual gap, owned forward: there is no single "upgrade tenant" action — one bootstrap
run plus one reindex call per repo are separate operator steps, and the reindex requires a
booted XP. A one-shot tenant-wide trigger belongs on the Phase 5 Gate E management-plane
ops surface.

## 9. Shutdown / cleanup

```bash
pkill -f 'com.enonic.xp.launcher'      # XP
pkill -f 'com.enonic.nodb.server'      # NoDB
docker rm -f nodb-pg nodb-minio nodb-os
# Postgres/MinIO/OpenSearch data is inside the containers in this setup — add -v volume
# mounts to the docker run commands if you want the data to survive container removal
# (dev-stack.sh uses named volumes nodb-pg-data, nodb-minio-data, nodb-os-data).
```

## Troubleshooting (each of these was hit for real during the gate smokes)

| Symptom | Cause / fix |
|---|---|
| `UnsupportedClassVersionError ... class file version 69.0` | XP launched with an older JVM — set `JAVA_HOME` to JDK 25. |
| Boot hangs, then first RPC fails `UNAVAILABLE` | NoDB not reachable at `nodbEndpoint`. There is deliberately no retry loop and no silent fallback to ES — fix the endpoint/server and reboot. |
| `UNAUTHENTICATED` on every call | Token expired (check `--ttl-minutes`), or the server's `NODB_KEYS_DIR` differs from the token tool's `--keys-dir` (key mismatch). |
| Repo create fails `PERMISSION_DENIED` | Token minted with the wrong scope — XP needs `--scope runtime`. |
| Every query fails `UNIMPLEMENTED`, repo create succeeds | NoDB was started without `NODB_OPENSEARCH_URL` — it has no search backend and says so per RPC. Export the URL and restart NoDB, then rebuild each repo's index (section 7) since repos created meanwhile have none. |
| Queries fail `UNAVAILABLE`, `/health/ready` says `{"failed":["opensearch"]}` | OpenSearch container down or unreachable from NoDB. Writes still commit (PG is the truth); the outbox holds the backlog and the indexer catches up when OpenSearch returns. |
| Writes hang then fail `DEADLINE_EXCEEDED` in `awaitRefresh` | The indexer cannot drain: OpenSearch down/slow, or the outbox has a large backlog. The write itself IS committed and durable — only searchability lags. Check `/health/ready`, OpenSearch logs, and retry; the refresh barrier is retryable by design. |
| OpenSearch answers HTTP 429 `circuit_breaking_exception` | Heap pressure — index metadata and field data live on the OpenSearch heap. Seen at Gate F when leaked per-test tenants piled up indices on a 512 MB heap. Remove leftover indices (`_cat/indices`), or raise `OPENSEARCH_JAVA_OPTS`. On a shared dev box, `docker restart nodb-os` after a big cleanup. |
| Search returns hits for content deleted long ago | The index drifted from the truth (e.g. restored from an old volume). Rebuild it — section 7. |
| Binary upload fails at first attachment | Bucket missing (NoDB never auto-creates it) or `NODB_S3_PATH_STYLE` not set for MinIO. |
| su login rejected | `xp.suPassword` must be `{sha256}<hex>`, not plaintext. |
| `com.enonic.xp.storage.nodb.cfg` present but XP still on ES | `backend` not set to exactly `nodb` — the client refuses activation for any other value (fails loud in the log, by design). Note the ES node is skipped only for `backend=nodb`, so a half-configured file boots a stock ES XP. |
| `/health` on the status port answers 503 in nodb mode | Should not happen since Gate G — the liveness check accepts the active `NodbStorageClient` in place of the ES client services. If it fires, the nodb client failed activation; check the XP log for its loud refusal. |
| `Cluster not healthy ... RED` lines early in boot | Gone in nodb mode since Gate G (they were embedded-ES startup noise; no ES node starts anymore). Still normal briefly in DEFAULT mode. |

## Relationship to the target architecture

This is the **developer/manual** topology. In the design's end state (DESIGN.md §7),
`nodb dev` supervises Postgres/OpenSearch itself (no docker commands), tenants/tokens
come from the control plane rather than CLI tools, and every sandbox is a tenant in one
shared local NoDB. What you wire by hand above is exactly what those layers automate.
