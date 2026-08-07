package com.enonic.nodb.bench;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Gate 6: exercises the full bench harness (seed + measure) end-to-end. By default this
 * runs {@link BenchConfig#reduced()} (~5k nodes) so {@code ../gradlew build} stays fast; set
 * {@code -Dnodb.bench.full=true} to run the full ~100k-node {@link BenchConfig#full()}
 * config instead and (re)write the real {@code bench/RESULTS.md} — see that file's own
 * "running the full bench" section, or {@link BenchHarness} (the plain-`main` equivalent of
 * this same full run, via {@code ../gradlew :bench:run}).
 *
 * <p>{@link BenchEnvironment} manages its own Postgres testcontainer + in-process {@code
 * NodbServer} lifecycle directly (no {@code @Testcontainers} JUnit extension needed here).
 */
class BenchHarnessTest
{
    @Test
    void seedsAndMeasuresLatency()
        throws Exception
    {
        boolean full = Boolean.getBoolean( "nodb.bench.full" );
        BenchConfig config = full ? BenchConfig.full() : BenchConfig.reduced();

        try (BenchEnvironment env = BenchEnvironment.start())
        {
            BenchResult result = BenchRunner.run( env, config );

            assertEquals( config.nodeCount(), result.seedNodeCount(), "every seeded node should be counted as written" );
            assertTrue( result.seedWallMillis() >= 0 );
            assertEquals( config.nodeCount(), result.searchDocCount(), "every seeded node should also ship a search document" );
            assertTrue( result.indexDrainMillis() >= 0 );
            assertEquals( 12, result.opStats().size(),
                          "getBranchEntry(id), getBranchEntry(path), getChildren, getVersion, writeBatch, term, fulltext, " +
                              "aggregation, highlight-via-NoDB, indexDocuments, index+awaitRefresh, awaitRefresh-noop" );
            assertEquals( 2, result.highlightStats().size(), "FINDINGS #7: plain vs unified" );
            for ( LatencyStats stats : result.opStats() )
            {
                assertEquals( config.measuredOps(), stats.count(), stats.operation() + ": all measured (post-warmup) ops should land" );
                assertTrue( stats.p50Micros() >= 0, stats.operation() + ": p50 should be a real non-negative measurement" );
                assertTrue( stats.p95Micros() >= stats.p50Micros(), stats.operation() + ": p95 should be >= p50" );
                assertTrue( stats.p99Micros() >= stats.p95Micros(), stats.operation() + ": p99 should be >= p95" );
            }

            if ( full )
            {
                ResultsWriter.write( Path.of( "RESULTS.md" ), result );
            }
        }
    }
}
