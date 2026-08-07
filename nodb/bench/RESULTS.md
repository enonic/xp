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

<!-- phase4-baseline -->
## Phase 4 baseline — the complete PG + OpenSearch path (Gate G)

Recorded 2026-08-07T11:56:25.149110Z by `BenchHarness`. **A BASELINE, NOT AN SLO.**

### Environment / caveat

- Everything on ONE dev laptop over loopback: bench client + `NodbServer` in one JVM (real TCP socket),
  `postgres:17` and stock `opensearchproject/opensearch:3.7.0` (512m heap) in Testcontainers on the same
  Docker daemon. This measures gRPC + serialization + real PG/OpenSearch round-trips, NOT cross-host
  network latency — the honest cross-host numbers can only come from a real deployment later; do not
  quote these as such.
- OpenSearch runs the PRODUCTION default `refresh_interval` (1s), unlike the engine tests which pin `-1`.
- The outbox indexer drains concurrently with seeding (poll 100ms, batch 500), so "index drain" below is
  the residual `awaitRefresh` barrier after the last shipped document, not the full indexing cost.

### Corpus

- Nodes seeded in PG: 100000 (100 folders x (1 + 999 children)), WriteBatch 1000 nodes/call
- Search documents shipped (`IndexDocuments`, same batch size): 100000 — each with a 6-word title, 150-word body (64-word vocabulary), one of 20 categories, a numeric price,
  a timestamp and an ACL read key; text fields shipped as both bare and `._analyzed` variants,
  mirroring the XP index-document shape
- Seed wall-clock: 107085 ms (934 nodes/sec, PG write + search-document ship combined)
- Index drain after seeding (final `awaitRefresh` barrier): 31932 ms
- Note on the fulltext rows: with 150-word bodies drawn from a 64-word vocabulary, an OR fulltext over
  title+body matches (and scores) nearly the ENTIRE corpus — those rows are the match-everything worst
  case, not a selective-query number; the term row (1-in-20 categories) is the selective counterpart.

### Latency (client-observed, `System.nanoTime()` around each blocking call; warmup 200 ops discarded, 2000 ops measured per row)

| Operation | p50 (µs) | p95 (µs) | p99 (µs) | mean (µs) | n |
|---|---:|---:|---:|---:|---:|
| getBranchEntry(by node_id) | 1147 | 1693 | 2476 | 1232 | 2000 |
| getBranchEntry(by node_path) | 1032 | 1191 | 1528 | 1047 | 2000 |
| getChildren(page=100) | 7436 | 11680 | 12837 | 7557 | 2000 |
| getVersion | 992 | 1100 | 1313 | 1000 | 2000 |
| writeBatch(1 node) | 1765 | 2159 | 2746 | 1820 | 2000 |
| search: term(data.category) | 1971 | 3363 | 5545 | 2181 | 2000 |
| search: fulltext(data.title,data.body) | 27783 | 35699 | 38921 | 28386 | 2000 |
| search: aggregation terms(data.category), size 0 | 803 | 1389 | 2257 | 891 | 2000 |
| search: fulltext + highlight(data.body) via NoDB (plain) | 9300 | 11383 | 14632 | 9530 | 2000 |
| indexDocuments(1 doc) | 1251 | 2101 | 2831 | 1370 | 2000 |
| refresh(SEARCH): indexDocuments(1 doc) + awaitRefresh | 14564 | 22603 | 28392 | 15599 | 2000 |
| refresh(SEARCH): awaitRefresh, nothing pending | 2573 | 2866 | 3392 | 2585 | 2000 |

### FINDINGS #7 — highlight `type: plain` (current, forced) vs engine default `unified`

Same query stream (fixed seed), same alias, same three-field expansion with `require_field_match: false`,
issued directly at OpenSearch so the ONLY variable is the highlighter type:

| Operation | p50 (µs) | p95 (µs) | p99 (µs) | mean (µs) | n |
|---|---:|---:|---:|---:|---:|
| highlight type=plain (direct OpenSearch, match on data.body._fulltext) | 1520 | 1959 | 3823 | 1623 | 2000 |
| highlight type=unified (direct OpenSearch, match on data.body._fulltext) | 1617 | 2203 | 4851 | 1715 | 2000 |

Measured ratio plain/unified: 0.94x at p50, 0.95x at mean.

### Re-running this section

`../gradlew :bench:run` (or `:bench:test -Dnodb.bench.full=true --tests "*BenchHarnessTest"`) reruns the
full bench and REPLACES everything from the `phase4-baseline` marker down; the Phase-1 record above the
marker is preserved verbatim. Plain `../gradlew build` runs the reduced (~5k node) config and does not
touch this file.
