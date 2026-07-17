# NoDB bench results — latency baseline (DESIGN.md §10 risk #2)

Generated 2026-07-17T19:34:16.223780Z by `BenchHarness` (see `nodb/bench/src/main/java/com/enonic/nodb/bench/`).

## Machine / environment

- Dev laptop, Docker Desktop (macOS), NOT a tuned production box or dedicated benchmark rig.
- Postgres 17 in a Testcontainers container (`postgres:17`), same Docker daemon as the client/server JVM.
- Server and client (and Postgres) all on loopback / the same host — this measures gRPC + serialization + a real
  Postgres round-trip, but NOT cross-host network latency.
- JVM: server and bench client run in the same JVM process as separate objects (real TCP loopback socket
  between them via `NodbClient`/`NodbServer`, not an in-process gRPC channel).

## Seed

- Node count: 100000 (100 folders x (1 + 999 children))
- WriteBatch size during seeding: 1000 nodes/call
- Shared index-config/ACL blob variants: 5 each (stored once via PutPayload, referenced by hash from every node's Version — node DATA is unique
  per node, mirroring real XP: most nodes under a content type share identical index config/ACLs)
- Seed wall-clock: 58505 ms
- Seed throughput: 1709 nodes/sec

## Latency (client-observed, `System.nanoTime()` around each blocking gRPC call; warmup 200 ops discarded, 2000 ops measured per row)

| Operation | p50 (µs) | p95 (µs) | p99 (µs) | mean (µs) | n |
|---|---:|---:|---:|---:|---:|
| getBranchEntry(by node_id) | 1085 | 1279 | 1588 | 1091 | 2000 |
| getBranchEntry(by node_path) | 982 | 1079 | 1223 | 983 | 2000 |
| getChildren(page=100) | 3527 | 4252 | 4541 | 3578 | 2000 |
| getVersion | 3853 | 4062 | 4240 | 3659 | 2000 |
| writeBatch(1 node) | 1440 | 1501 | 1592 | 1429 | 2000 |

**Caveat:** loopback + containerized Postgres on a dev laptop, not tuned production infrastructure — this is a
floor for RELATIVE comparison (e.g. against the XP-side embedded-ES baseline, or future NoDB slices), not an
absolute SLO.

## Running the full 100k-node bench yourself

The numbers above come from an ACTUAL run (never hand-written), sized by the invoking entry point:

- `../gradlew :bench:run` — runs `BenchHarness.main`, always the full ~100k-node config (`BenchConfig.full()`), and overwrites this file.
- `../gradlew :bench:test -Dnodb.bench.full=true --tests "*BenchHarnessTest"` — same full run, driven through JUnit/Testcontainers instead of `main`.
- Plain `../gradlew build` (or `:bench:test` without the system property) instead runs a MUCH smaller smoke-sized config (~5k nodes) so the build stays fast; it does NOT overwrite this file.
