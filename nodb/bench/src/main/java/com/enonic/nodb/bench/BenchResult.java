package com.enonic.nodb.bench;

import java.util.List;

/** Outcome of one {@link BenchRunner#run} invocation: seed throughput + per-op latency stats. */
public record BenchResult(BenchConfig config, long seedNodeCount, long seedWallMillis, List<LatencyStats> opStats)
{
    public double nodesPerSecond()
    {
        return seedWallMillis == 0 ? 0 : seedNodeCount * 1000.0 / seedWallMillis;
    }
}
