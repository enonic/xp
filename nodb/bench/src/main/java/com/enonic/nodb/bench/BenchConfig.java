package com.enonic.nodb.bench;

/**
 * Bench sizing knobs (DESIGN.md §10 risk #2 / BUILD-SLICE-1.md step 6).
 *
 * <p>Seed shape: {@code folderCount} "folder" nodes directly under root ({@code
 * /folder-000}, ...), each with {@code childrenPerFolder} children ({@code
 * /folder-000/child-000000}, ...). {@code nodeCount} is exactly {@code folderCount * (1 +
 * childrenPerFolder)} — {@link #full()} and {@link #reduced()} both pick numbers that divide
 * evenly so the advertised node count is exact, not approximate.
 *
 * <p>{@code writeBatchSize} nodes are written per {@code WriteBatch} RPC during seeding
 * (realistic batch size, not one node per call); one folder's worth of nodes (1 parent +
 * {@code childrenPerFolder} children) is sent per batch call in both configs below.
 *
 * <p>{@code measuredOps}/{@code warmupOps} govern the latency-measurement phase: each of
 * the five measured operations (getBranchEntry by id, by path, getChildren, getVersion,
 * single-node WriteBatch) runs {@code warmupOps + measuredOps} iterations; the first {@code
 * warmupOps} are discarded (JIT/connection-pool/OS-cache warmup) and only the remaining
 * {@code measuredOps} contribute to the reported percentiles.
 */
public record BenchConfig(int nodeCount, int folderCount, int childrenPerFolder, int writeBatchSize, int sharedBlobVariants,
                           int measuredOps, int warmupOps, int childrenPageSize)
{
    /** The full ~100k-node run (BUILD-SLICE-1.md step 6 goal) — this is what {@code BenchHarness.main} runs. */
    public static BenchConfig full()
    {
        int folderCount = 100;
        int childrenPerFolder = 999;
        return new BenchConfig( folderCount * ( 1 + childrenPerFolder ), folderCount, childrenPerFolder, 1000, 5, 2000, 200, 100 );
    }

    /**
     * A much smaller run exercised by the default {@code :bench:test} task so {@code
     * ../gradlew build} stays fast; the full 100k run is gated behind {@code
     * -Dnodb.bench.full=true} (see {@code bench/build.gradle.kts} and {@code
     * BenchHarnessTest}).
     */
    public static BenchConfig reduced()
    {
        int folderCount = 10;
        int childrenPerFolder = 499;
        return new BenchConfig( folderCount * ( 1 + childrenPerFolder ), folderCount, childrenPerFolder, 500, 5, 100, 20, 50 );
    }
}
