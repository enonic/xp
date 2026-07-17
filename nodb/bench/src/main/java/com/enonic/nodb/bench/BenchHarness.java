package com.enonic.nodb.bench;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runnable entry point for the full ~100k-node latency bench (BUILD-SLICE-1.md step 6,
 * DESIGN.md §10 risk #2): spins up a postgres:17 testcontainer + an in-process {@code
 * NodbServer} bound to a real loopback TCP port ({@link BenchEnvironment}), seeds and
 * measures ({@link BenchRunner}), and writes {@code bench/RESULTS.md} ({@link
 * ResultsWriter}).
 *
 * <p>Run via {@code ../gradlew :bench:run} (application plugin, see
 * {@code bench/build.gradle.kts}). The exact same run also happens, JUnit/Testcontainers-
 * driven instead of via this {@code main}, as {@code BenchHarnessTest} when invoked with
 * {@code -Dnodb.bench.full=true} — see that class and {@code bench/RESULTS.md}'s own
 * "running the full bench" section for both paths. Plain {@code ../gradlew build} does
 * NOT run this class; it only runs {@code BenchHarnessTest}'s much smaller default-sized
 * smoke config, so the build stays fast.
 */
public final class BenchHarness
{
    private static final Logger LOG = LoggerFactory.getLogger( BenchHarness.class );

    private BenchHarness()
    {
    }

    public static void main( String[] args )
        throws Exception
    {
        LOG.info( "Starting NoDB bench: full config ({} nodes) — this stands up its own Postgres container and NodbServer, "
                       + "expect this to take a few minutes.", BenchConfig.full().nodeCount() );
        try (BenchEnvironment env = BenchEnvironment.start())
        {
            BenchResult result = BenchRunner.run( env.client(), env.repoId(), BenchConfig.full() );
            LOG.info( "Seeded {} nodes in {} ms ({} nodes/sec)", result.seedNodeCount(), result.seedWallMillis(),
                       String.format( "%.0f", result.nodesPerSecond() ) );
            for ( LatencyStats stats : result.opStats() )
            {
                LOG.info( "{}: p50={}us p95={}us p99={}us mean={}us n={}", stats.operation(), stats.p50Micros(), stats.p95Micros(),
                           stats.p99Micros(), stats.meanMicros(), stats.count() );
            }
            Path resultsPath = Path.of( "RESULTS.md" );
            ResultsWriter.write( resultsPath, result );
            LOG.info( "Wrote {}", resultsPath.toAbsolutePath() );
        }
    }
}
