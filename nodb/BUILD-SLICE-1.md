# NoDB Build — Slice 1 Work Order

**Read first:** `nodb/DESIGN.md` (esp. §3.3, §4, §7.2, §10 risk register), `nodb/schema/schema.sql`
(v0.3), `nodb/proto/nodb.proto`, `nodb/spi/`. This work order is self-contained — do NOT
require prior conversation context.

## Goal

Standalone NoDB engine + gRPC server, write/read paths only, proving three things:
1. **Transactional WriteBatch** (risk #1): version(s) + branch entrie(s) + payloads +
   outbox row commit atomically; hash-only payload references answered with NEED_PAYLOAD.
2. **Tenant model end-to-end** (Phase 1 gate): token→TenantContext as the ONLY entry,
   trivial local issuer, schema-per-tenant + `SET LOCAL ROLE`.
3. **Latency baseline** (risk #2): benchmark harness measuring p50/p95 for get/save/children
   vs. a stub in-JVM baseline, so later slices have numbers to beat.

## Out of scope (do not build)

OpenSearch/indexer, ChangeFeed, search of any kind, snapshots/dumps, S3/binaries,
XP integration, `nodb dev` supervision, control plane, metering, console.
`awaitRefresh` = stub returning immediately (no indexer exists yet).

## Deliverables / file tree

```
nodb/
  engine/         # plain Java lib (NO OSGi, no Spring): stores, txn logic
  server/         # gRPC bindings, auth interceptor, dev issuer; fat-jar
  client-java/    # thin gRPC client lib (later becomes XP's nodb-client core)
  schema/         # Flyway-style migrations derived from schema.sql v0.3
  bench/          # JMH or simple harness: p50/p95 get/save/children, dual-tenant
  docker/         # compose: postgres:17 only (this slice)
```

## Build order (gate each step on its tests before the next)

1. Gradle multi-module skeleton; testcontainers-postgres wired; CI script.
2. Migrations: tenant-schema template from schema.sql v0.3 (payload, node_version+
   partitions, branch_entry+branch sub-partitions, node_commit, outbox,
   index_checkpoint, audit_log) + `nodb_system` (migration version). Tenant
   provisioning = create schema + role + template DDL. **Gate:** provisioning test
   creates 2 tenants; cross-schema access as tenant role fails with permission denied.
3. Engine stores: PayloadStore (putPayload = INSERT..ON CONFLICT DO NOTHING),
   VersionStore, BranchStore (incl. children by parent_path, getByPath), CommitStore.
   All methods take TenantContext; every SQL runs under SET LOCAL ROLE. **Gate:**
   store/get round-trips, dedup asserted (same bytes twice = 1 row), dual-tenant
   isolation tests.
4. WriteBatch: one transaction = N versions + N branch entries + optional commit +
   inline payloads + hash-only references (unknown hash → abort, return NEED_PAYLOAD
   list) + one outbox row batch; returns max seq. Audit row written in SAME txn for
   management-ish ops (repo create/delete). Repo lifecycle: create = repository row +
   partitions; delete = detach+drop. **Gate:** crash-consistency test (kill between
   ops impossible — assert single-txn via forced rollback), NEED_PAYLOAD retry flow,
   branch fork test (INSERT..SELECT into new sub-partition), repo drop leaves no rows.
5. gRPC server: proto codegen (flesh out message fields from spi/Records.java),
   auth interceptor (JWT verify against configured public key; claims: tenant, scope,
   subject, jti → TenantContext), dev issuer CLI (`nodb token --tenant t1`). QoS/scope
   enforcement: management RPCs reject runtime scope. **Gate:** integration tests over
   real gRPC: no token = UNAUTHENTICATED; tenant-A token can never read tenant B
   (assert at RPC level AND at SQL role level).
6. Bench harness: seed 100k nodes; measure get/getByPath/children/WriteBatch p50/p95
   via client-java against localhost server. Record results into bench/RESULTS.md.

## Execution guidance

- Model mix: mechanical codegen/tests → cheap model subagents; transaction semantics,
  auth interceptor, and final review of each gate → strongest model. Review diffs
  against DESIGN.md §3.3/§7.2 before marking a gate done.
- Token ceiling for this slice: ~1M output. If a gate loops >3 fix attempts, stop and
  record the blocker in this file instead of grinding.
- Update DESIGN.md §10 risk register entries #1 (resolved-by) when WriteBatch gate passes.

## Definition of done

All gates green in CI against testcontainers-postgres; dual-tenant isolation suite
passes; bench/RESULTS.md has baseline numbers; `docker compose up` + `nodb token` +
grpcurl WriteBatch/GetBranchEntry demo works end-to-end; zero OSGi/Spring deps in
engine/server.
