#!/usr/bin/env bash
# dev-stack.sh — one-command boot of the XP-on-NoDB hybrid stack (RUNNING.md automated).
#
#   nodb/dev-stack.sh start          build if needed, start everything, deploy admin apps
#   nodb/dev-stack.sh start --build  force rebuild of XP + NoDB first
#   nodb/dev-stack.sh stop           stop XP + NoDB (containers keep running)
#   nodb/dev-stack.sh status         show what's up and the psql ground truth
#   nodb/dev-stack.sh clean          stop everything, remove containers + XP home (keeps volumes)
#
# Data outlives restarts: Postgres/MinIO use named docker volumes (nodb-pg-data,
# nodb-minio-data). XP_HOME is disposable by design — config, token and apps are
# regenerated on every start.

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

# ---- knobs -------------------------------------------------------------------
TENANT=myxp
SU_PASS=password123
XP_HOME_DIR="${XP_HOME_DIR:-/tmp/xp-nodb-home}"
KEYS_DIR="$ROOT/.nodb-dev-keys"
PG_PORT=55432
MINIO_PORT=19000
MINIO_CONSOLE_PORT=19001
NODB_PORT=7700
XP_LOG=/tmp/xp-nodb.log
NODB_LOG=/tmp/nodb-server.log

if [ -d /opt/homebrew/opt/openjdk@25 ]; then
  JAVA_HOME_RUN=/opt/homebrew/opt/openjdk@25
elif [ -d /opt/homebrew/opt/openjdk ]; then
  JAVA_HOME_RUN=/opt/homebrew/opt/openjdk
else
  JAVA_HOME_RUN="${JAVA_HOME:?No JDK found: install openjdk@25 or set JAVA_HOME}"
fi

# Hardcoded admin apps (XP 8.1 line; snapshot URLs pinned 2026-08-04 — refresh via
# https://repo.enonic.com/service/rest/v1/search/assets?q=<name>&sort=version)
APP_URLS=(
  "https://repo.enonic.com/repository/snapshot/com/enonic/xp/app-standardidprovider/8.1.0-SNAPSHOT/app-standardidprovider-8.1.0-20260804.101214-158.jar"
  "https://repo.enonic.com/repository/snapshot/com/enonic/xp/app-main/8.1.0-SNAPSHOT/app-main-8.1.0-20260804.101231-228.jar"
  "https://repo.enonic.com/repository/snapshot/com/enonic/xp/app-applications/8.1.0-SNAPSHOT/app-applications-8.1.0-20260804.101218-217.jar"
  "https://repo.enonic.com/repository/snapshot/com/enonic/xp/app-users/8.1.0-SNAPSHOT/app-users-8.1.0-20260804.101219-206.jar"
  "https://repo.enonic.com/repository/public/com/enonic/app/contentstudio/6.0.3/contentstudio-6.0.3.jar"
)
APP_NAMES=(
  app-standardidprovider.jar
  app-main.jar
  app-applications.jar
  app-users.jar
  contentstudio.jar
)

XP_DIST="$ROOT/modules/runtime/build/install"
NODB_DIST="$ROOT/nodb/server/build/install/server"

log()  { printf '\033[1;32m==>\033[0m %s\n' "$*"; }
warn() { printf '\033[1;33m==>\033[0m %s\n' "$*"; }
die()  { printf '\033[1;31m==>\033[0m %s\n' "$*" >&2; exit 1; }

wait_for() { # wait_for <seconds> <description> <command...>
  local t=$1 desc=$2; shift 2
  for _ in $(seq 1 "$t"); do
    if "$@" >/dev/null 2>&1; then return 0; fi
    sleep 1
  done
  die "Timed out waiting for $desc (see logs)"
}

# ---- steps -------------------------------------------------------------------

build() {
  local force=${1:-no}
  if [ "$force" = yes ] || [ ! -x "$XP_DIST/bin/server.sh" ]; then
    log "Building XP distro (runtime:installDist)"
    ./gradlew :runtime:installDist
  else
    log "XP distro present — skipping build (use 'start --build' to force)"
  fi
  if [ "$force" = yes ] || [ ! -x "$NODB_DIST/bin/server" ]; then
    log "Building NoDB server"
    (cd nodb && ../gradlew :server:installDist -x test)
  else
    log "NoDB server present — skipping build"
  fi
}

start_containers() {
  docker info >/dev/null 2>&1 || die "Docker is not running"

  if docker inspect nodb-pg >/dev/null 2>&1; then
    docker start nodb-pg >/dev/null
  else
    log "Creating Postgres container (port $PG_PORT, volume nodb-pg-data)"
    docker run -d --name nodb-pg -p "$PG_PORT:5432" \
      -v nodb-pg-data:/var/lib/postgresql/data \
      -e POSTGRES_DB=nodb -e POSTGRES_USER=nodb -e POSTGRES_PASSWORD=nodb \
      postgres:17 >/dev/null
  fi

  if docker inspect nodb-minio >/dev/null 2>&1; then
    docker start nodb-minio >/dev/null
  else
    log "Creating MinIO container (ports $MINIO_PORT/$MINIO_CONSOLE_PORT, volume nodb-minio-data)"
    docker run -d --name nodb-minio -p "$MINIO_PORT:9000" -p "$MINIO_CONSOLE_PORT:9001" \
      -v nodb-minio-data:/data \
      -e MINIO_ROOT_USER=nodb -e MINIO_ROOT_PASSWORD=nodb-secret \
      minio/minio:RELEASE.2024-11-07T00-52-20Z server /data --console-address ":9001" >/dev/null
  fi

  wait_for 30 "Postgres" docker exec nodb-pg pg_isready -U nodb
  wait_for 30 "MinIO"    docker exec nodb-minio mc --version
  docker exec nodb-minio sh -c \
    'mc alias set local http://localhost:9000 nodb nodb-secret >/dev/null && mc mb --ignore-existing local/nodb-binaries' >/dev/null
}

start_nodb() {
  if pgrep -f com.enonic.nodb.server >/dev/null; then
    log "NoDB server already running"
    return
  fi
  log "Starting NoDB server (port $NODB_PORT, log $NODB_LOG)"
  NODB_PG_URL="jdbc:postgresql://localhost:$PG_PORT/nodb" \
  NODB_PG_USER=nodb NODB_PG_PASSWORD=nodb \
  NODB_PORT=$NODB_PORT NODB_KEYS_DIR="$KEYS_DIR" \
  NODB_S3_ENDPOINT="http://localhost:$MINIO_PORT" \
  NODB_S3_BUCKET=nodb-binaries NODB_S3_REGION=us-east-1 \
  NODB_S3_ACCESS_KEY=nodb NODB_S3_SECRET_KEY=nodb-secret \
  NODB_S3_PATH_STYLE=true \
  nohup "$NODB_DIST/bin/server" > "$NODB_LOG" 2>&1 &
  wait_for 30 "NoDB gRPC port" bash -c "nc -z localhost $NODB_PORT"
}

provision_tenant() {
  local exists
  exists=$(docker exec nodb-pg psql -U nodb -d nodb -tAc \
    "SELECT 1 FROM information_schema.schemata WHERE schema_name='$TENANT'" || true)
  if [ "$exists" != "1" ]; then
    log "Provisioning tenant '$TENANT'"
    java -cp "$NODB_DIST/lib/*" com.enonic.nodb.server.tools.TenantBootstrapTool \
      --tenant "$TENANT" \
      --pg-url "jdbc:postgresql://localhost:$PG_PORT/nodb" \
      --pg-user nodb --pg-password nodb
  else
    log "Tenant '$TENANT' exists"
  fi
}

mint_token() {
  log "Minting runtime token (8h TTL)"
  NODB_TOKEN=$(java -cp "$NODB_DIST/lib/*" com.enonic.nodb.server.auth.NodbTokenTool \
    --tenant "$TENANT" --scope runtime --subject svc:xp \
    --keys-dir "$KEYS_DIR" --ttl-minutes 480 | tail -1)
  [ -n "$NODB_TOKEN" ] || die "Token minting failed"
}

configure_xp_home() {
  if [ ! -d "$XP_HOME_DIR/config" ]; then
    log "Seeding XP home at $XP_HOME_DIR"
    mkdir -p "$XP_HOME_DIR"
    cp -R "$XP_DIST/home/"* "$XP_HOME_DIR/"
  fi

  cat > "$XP_HOME_DIR/config/com.enonic.xp.storage.nodb.cfg" <<EOF
backend=nodb
nodbEndpoint=localhost:$NODB_PORT
nodbToken=${NODB_TOKEN}
EOF

  local su_hash
  su_hash=$(printf '%s' "$SU_PASS" | shasum -a 256 | awk '{print $1}')
  cat > "$XP_HOME_DIR/config/system.properties" <<EOF
xp.suPassword={sha256}${su_hash}
EOF
}

download_apps() {
  mkdir -p "$XP_HOME_DIR/deploy"
  local i
  for i in "${!APP_URLS[@]}"; do
    local target="$XP_HOME_DIR/deploy/${APP_NAMES[$i]}"
    if [ ! -s "$target" ]; then
      log "Downloading ${APP_NAMES[$i]}"
      curl -sfL -o "$target" "${APP_URLS[$i]}" || warn "Failed: ${APP_URLS[$i]} (URL may need refreshing)"
    fi
  done
}

start_xp() {
  if pgrep -f com.enonic.xp.launcher >/dev/null; then
    warn "XP already running — restarting it to pick up the fresh token"
    pkill -f com.enonic.xp.launcher || true
    sleep 3
  fi
  if nc -z localhost 8080 2>/dev/null; then
    die "Port 8080 is taken by something else — stop it or set jetty ports in $XP_HOME_DIR/config"
  fi
  log "Starting XP (log $XP_LOG)"
  JAVA_HOME="$JAVA_HOME_RUN" XP_HOME="$XP_HOME_DIR" \
    nohup "$XP_DIST/bin/server.sh" > "$XP_LOG" 2>&1 &
  wait_for 120 "XP on :8080" bash -c "curl -s -o /dev/null localhost:8080"
}

verify() {
  echo
  log "Ground truth:"
  docker exec nodb-pg psql -U nodb -d nodb -c \
    "SELECT (SELECT count(*) FROM $TENANT.payload)      AS payloads,
            (SELECT count(*) FROM $TENANT.node_version) AS versions,
            (SELECT count(*) FROM $TENANT.branch_entry) AS branch_entries;"
  local blobs
  blobs=$(find "$XP_HOME_DIR/repo/blob" -type f 2>/dev/null | wc -l | tr -d ' ')
  echo "  local blobstore files: $blobs (0 = all data in NoDB, as designed)"
  echo
  log "Ready:  http://localhost:8080/admin   (su / $SU_PASS)"
  echo "        MinIO console: http://localhost:$MINIO_CONSOLE_PORT (nodb / nodb-secret)"
  echo "        Logs: $XP_LOG  $NODB_LOG"
  echo "        Note: publish/version-history/compare work since Phase 3.5; search/aggregations stay on embedded ES until Phase 4."
}

# ---- commands ----------------------------------------------------------------

cmd_start() {
  local force=no
  [ "${1:-}" = "--build" ] && force=yes
  build "$force"
  start_containers
  start_nodb
  provision_tenant
  mint_token
  configure_xp_home
  download_apps
  start_xp
  verify
}

cmd_stop() {
  log "Stopping XP + NoDB (containers left running)"
  pkill -f com.enonic.xp.launcher 2>/dev/null || true
  pkill -f com.enonic.nodb.server 2>/dev/null || true
}

cmd_status() {
  pgrep -f com.enonic.xp.launcher   >/dev/null && echo "XP:       running" || echo "XP:       stopped"
  pgrep -f com.enonic.nodb.server   >/dev/null && echo "NoDB:     running" || echo "NoDB:     stopped"
  docker inspect -f 'Postgres: {{.State.Status}}' nodb-pg 2>/dev/null    || echo "Postgres: absent"
  docker inspect -f 'MinIO:    {{.State.Status}}' nodb-minio 2>/dev/null || echo "MinIO:    absent"
  if docker exec nodb-pg pg_isready -U nodb >/dev/null 2>&1; then
    docker exec nodb-pg psql -U nodb -d nodb -c \
      "SELECT (SELECT count(*) FROM $TENANT.payload)      AS payloads,
              (SELECT count(*) FROM $TENANT.node_version) AS versions;" 2>/dev/null || true
  fi
}

cmd_clean() {
  cmd_stop
  log "Removing containers (named volumes preserved: nodb-pg-data, nodb-minio-data)"
  docker rm -f nodb-pg nodb-minio 2>/dev/null || true
  log "Removing XP home $XP_HOME_DIR"
  rm -rf "$XP_HOME_DIR"
  warn "To also wipe data: docker volume rm nodb-pg-data nodb-minio-data"
}

case "${1:-start}" in
  start)  shift || true; cmd_start "$@";;
  stop)   cmd_stop;;
  status) cmd_status;;
  clean)  cmd_clean;;
  *) die "Usage: $0 {start [--build]|stop|status|clean}";;
esac
