package com.enonic.nodb.bench;

import java.util.List;

/** Outcome of one {@link BenchRunner#run} invocation: seed throughput + per-op latency stats. */
public record BenchResult(BenchConfig config, long seedNodeCount, long seedWallMillis, long searchDocCount, long indexDrainMillis,
                          List<LatencyStats> opStats, List<LatencyStats> highlightStats)
{
    public double nodesPerSecond()
    {
        return seedWallMillis == 0 ? 0 : seedNodeCount * 1000.0 / seedWallMillis;
    }
}
