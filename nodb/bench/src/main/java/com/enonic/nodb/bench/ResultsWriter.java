package com.enonic.nodb.bench;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/** Renders a {@link BenchResult} as {@code bench/RESULTS.md} (BUILD-SLICE-1.md step 6 / DESIGN.md §10 risk #2). */
final class ResultsWriter
{
    private ResultsWriter()
    {
    }

    static void write( Path path, BenchResult result )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "# NoDB bench results — latency baseline (DESIGN.md §10 risk #2)\n\n" );
        sb.append( "Generated " )
            .append( DateTimeFormatter.ISO_INSTANT.format( Instant.now() ) )
            .append( " by `BenchHarness` (see `nodb/bench/src/main/java/com/enonic/nodb/bench/`).\n\n" );

        sb.append( "## Machine / environment\n\n" );
        sb.append( "- Dev laptop, Docker Desktop (macOS), NOT a tuned production box or dedicated benchmark rig.\n" );
        sb.append( "- Postgres 17 in a Testcontainers container (`postgres:17`), same Docker daemon as the client/server JVM.\n" );
        sb.append(
            "- Server and client (and Postgres) all on loopback / the same host — this measures gRPC + serialization + a real\n" +
                "  Postgres round-trip, but NOT cross-host network latency.\n" );
        sb.append( "- JVM: server and bench client run in the same JVM process as separate objects (real TCP loopback socket\n" +
                       "  between them via `NodbClient`/`NodbServer`, not an in-process gRPC channel).\n\n" );

        sb.append( "## Seed\n\n" );
        BenchConfig config = result.config();
        sb.append( "- Node count: " ).append( result.seedNodeCount() ).append( " (" ).append( config.folderCount() )
            .append( " folders x (1 + " ).append( config.childrenPerFolder() ).append( " children))\n" );
        sb.append( "- WriteBatch size during seeding: " ).append( config.writeBatchSize() ).append( " nodes/call\n" );
        sb.append( "- Shared index-config/ACL blob variants: " ).append( config.sharedBlobVariants() )
            .append( " each (stored once via PutPayload, referenced by hash from every node's Version — node DATA is unique\n" +
                         "  per node, mirroring real XP: most nodes under a content type share identical index config/ACLs)\n" );
        sb.append( "- Seed wall-clock: " ).append( result.seedWallMillis() ).append( " ms\n" );
        sb.append( String.format( "- Seed throughput: %.0f nodes/sec%n%n", result.nodesPerSecond() ) );

        sb.append( "## Latency (client-observed, `System.nanoTime()` around each blocking gRPC call; warmup " )
            .append( config.warmupOps() ).append( " ops discarded, " ).append( config.measuredOps() )
            .append( " ops measured per row)\n\n" );
        sb.append( "| Operation | p50 (µs) | p95 (µs) | p99 (µs) | mean (µs) | n |\n" );
        sb.append( "|---|---:|---:|---:|---:|---:|\n" );
        for ( LatencyStats stats : result.opStats() )
        {
            sb.append( "| " ).append( stats.operation() ).append( " | " )
                .append( String.format( "%.0f", stats.p50Micros() ) ).append( " | " )
                .append( String.format( "%.0f", stats.p95Micros() ) ).append( " | " )
                .append( String.format( "%.0f", stats.p99Micros() ) ).append( " | " )
                .append( String.format( "%.0f", stats.meanMicros() ) ).append( " | " )
                .append( stats.count() ).append( " |\n" );
        }

        sb.append( "\n**Caveat:** loopback + containerized Postgres on a dev laptop, not tuned production infrastructure — this is a\n" )
            .append( "floor for RELATIVE comparison (e.g. against the XP-side embedded-ES baseline, or future NoDB slices), not an\n" )
            .append( "absolute SLO.\n\n" );

        sb.append( "## Running the full 100k-node bench yourself\n\n" );
        sb.append( "The numbers above come from an ACTUAL run (never hand-written), sized by the invoking entry point:\n\n" );
        sb.append( "- `../gradlew :bench:run` — runs `BenchHarness.main`, always the full ~100k-node config " )
            .append( "(`BenchConfig.full()`), and overwrites this file.\n" );
        sb.append( "- `../gradlew :bench:test -Dnodb.bench.full=true --tests \"*BenchHarnessTest\"` — same full run, " )
            .append( "driven through JUnit/Testcontainers instead of `main`.\n" );
        sb.append( "- Plain `../gradlew build` (or `:bench:test` without the system property) instead runs a MUCH " )
            .append( "smaller smoke-sized config (~5k nodes) so the build stays fast; it does NOT overwrite this file.\n" );
        try
        {
            Files.writeString( path, sb.toString(), StandardCharsets.UTF_8 );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }
}
