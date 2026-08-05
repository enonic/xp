# Running XP on NoDB — manual setup guide

How to compile NoDB, start it against Dockerized Postgres + MinIO, provision a tenant,
mint a token, and boot XP with `backend=nodb`. Every command below is taken from the
recipes that actually ran in the Phase 1–3 Gate D smokes (see BUILD-PHASE-*.md for the
evidence they produced).

**State of the world on this branch** (`nodb-phase35-version-sql`): in nodb mode, XP stores
branch/version/commit records AND node payloads (node-data, index-config, ACL) in NoDB's
Postgres, and binaries via NoDB in S3/MinIO. Since Phase 3.5, the storage-index query
family is served from Postgres too: version history, publish resolution
(resolve-sync-work), compare and commit queries all run as SQL in nodb mode — Content
Studio's publish dialog, publish-status badges, version history panel and
compare/restore work on this stack. Remaining known gaps in hybrid mode:
search/aggregations still run on XP's embedded Elasticsearch (the OpenSearch phase is
next), so free-text query behavior is limited to what the embedded ES indexes this
boot; delete-with-children fails (`DeleteNodeCommand` lists children via a
storage-index `NodeBranchQuery` — Phase 4/8); rename-swap pushes hit the branch-path
constraint (DESIGN.md risk #8); and vacuum/GC is deferred (Phase 5): deleted content
leaves payload rows/S3 objects behind by design, for now.

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
# (alternative: aws --endpoint-url http://localhost:19000 s3 mb s3://nodb-binaries
#  with AWS_ACCESS_KEY_ID=nodb AWS_SECRET_ACCESS_KEY=nodb-secret)
```

## 3. Start NoDB

```bash
export NODB_PG_URL="jdbc:postgresql://localhost:55432/nodb"
export NODB_PG_USER=nodb
export NODB_PG_PASSWORD=nodb
export NODB_PORT=7700
export NODB_KEYS_DIR="$PWD/.nodb-dev-keys"     # dev issuer keypair lives here
export NODB_S3_ENDPOINT="http://localhost:19000"
export NODB_S3_BUCKET=nodb-binaries
export NODB_S3_REGION=us-east-1
export NODB_S3_ACCESS_KEY=nodb
export NODB_S3_SECRET_KEY=nodb-secret
export NODB_S3_PATH_STYLE=true                  # required for MinIO

nohup nodb/server/build/install/server/bin/server > /tmp/nodb-server.log 2>&1 &
```

On first start the dev keypair is generated under `NODB_KEYS_DIR` (the server holds the
public key; the token tool below signs with the private key — **they must point at the
same directory**). This is the dev issuer; a real deployment replaces it with the
control plane (DESIGN.md §7.2).

## 4. Provision a tenant and mint a runtime token

Tenant ids must match `^[a-z][a-z0-9]{2,30}$` (lowercase alphanumeric, no dashes).

```bash
CP="nodb/server/build/install/server/lib/*"

# Create the tenant schema (applies migrations, creates the tenant role):
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

# Set the su password — MUST be {sha256}<hex>, plaintext is rejected:
SU_HASH=$(printf '%s' 'password123' | shasum -a 256 | awk '{print $1}')
cat > "$XP_HOME/config/system.properties" <<EOF
xp.suPassword={sha256}${SU_HASH}
EOF

# Boot (JDK 25 required at runtime):
JAVA_HOME=/opt/homebrew/opt/openjdk XP_HOME="$XP_HOME" \
  nohup modules/runtime/build/install/bin/server.sh > /tmp/xp-nodb.log 2>&1 &
```

If ports 8080/4848/2609 are occupied (e.g. another sandbox), override them in
`$XP_HOME/config/com.enonic.xp.web.jetty.cfg` (`http.web.port`, `http.management.port`,
`http.statistics.port`) before booting.

## 6. Verify

```bash
# Started + all bundles healthy (look for the nodb client ACTIVE):
grep "Started Enonic XP" /tmp/xp-nodb.log
curl -s localhost:2609/osgi.bundle | grep -o '"com.enonic.xp.core.storage.nodb.client"[^}]*'

# Ground truth — node data is in Postgres, not on disk:
PGPASSWORD=nodb psql -h localhost -p 55432 -U nodb -d nodb -c \
  'SELECT count(*) AS payloads FROM myxp.payload;
   SELECT count(*) AS versions FROM myxp.node_version;'
find "$XP_HOME/repo/blob" -type f | wc -l        # → 0 in nodb mode

# Binaries land in MinIO under the tenant prefix once attachments exist:
docker exec nodb-minio mc ls -r local/nodb-binaries/myxp/ 2>/dev/null
```

A healthy boot initializes the system repos through NoDB — the psql counts are non-zero
immediately (~50 versions / ~55 payload rows from bootstrap), and the FK guarantees
every version's three hashes resolve to payload rows.

## 7. Shutdown / cleanup

```bash
pkill -f 'com.enonic.xp.launcher'      # XP
pkill -f 'com.enonic.nodb.server'      # NoDB
docker rm -f nodb-pg nodb-minio
# Postgres/MinIO data is inside the containers in this setup — add -v volume mounts
# to the docker run commands if you want the data to survive container removal.
```

## Troubleshooting (each of these was hit for real during the gate smokes)

| Symptom | Cause / fix |
|---|---|
| `UnsupportedClassVersionError ... class file version 69.0` | XP launched with an older JVM — set `JAVA_HOME` to JDK 25. |
| Boot hangs, then first RPC fails `UNAVAILABLE` | NoDB not reachable at `nodbEndpoint`. There is deliberately no retry loop and no silent fallback to ES — fix the endpoint/server and reboot. |
| `UNAUTHENTICATED` on every call | Token expired (check `--ttl-minutes`), or the server's `NODB_KEYS_DIR` differs from the token tool's `--keys-dir` (key mismatch). |
| Repo create fails `PERMISSION_DENIED` | Token minted with the wrong scope — XP needs `--scope runtime`. |
| Binary upload fails at first attachment | Bucket missing (NoDB never auto-creates it) or `NODB_S3_PATH_STYLE` not set for MinIO. |
| su login rejected | `xp.suPassword` must be `{sha256}<hex>`, not plaintext. |
| `com.enonic.xp.storage.nodb.cfg` present but XP still on ES | `backend` not set to exactly `nodb` — the client refuses activation for any other value (fails loud in the log, by design). |
| Cosmetic `Cluster not healthy ... RED` lines early in boot | Pre-existing embedded-ES startup noise (search still runs on ES in hybrid mode); RED→YELLOW within seconds is normal. |

## Relationship to the target architecture

This is the **developer/manual** topology. In the design's end state (DESIGN.md §7),
`nodb dev` supervises Postgres/OpenSearch itself (no docker commands), tenants/tokens
come from the control plane rather than CLI tools, and every sandbox is a tenant in one
shared local NoDB. What you wire by hand above is exactly what those layers automate.
