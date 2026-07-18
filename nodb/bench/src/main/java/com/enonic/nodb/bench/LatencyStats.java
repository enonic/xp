package com.enonic.nodb.bench;

import java.util.Arrays;

/**
 * p50/p95/p99/mean over a set of latency samples, all in microseconds. Samples are
 * collected as nanoseconds (via {@code System.nanoTime()} around each client call, per
 * BUILD-SLICE-1.md step 6) and converted once here; percentiles use nearest-rank ({@code
 * ceil(p * n)}, 1-indexed then clamped) — simple and standard enough for a baseline
 * measurement, no interpolation needed.
 */
public record LatencyStats(String operation, int count, double p50Micros, double p95Micros, double p99Micros, double meanMicros)
{
    public static LatencyStats of( String operation, long[] samplesNanos )
    {
        long[] sorted = samplesNanos.clone();
        Arrays.sort( sorted );
        int n = sorted.length;
        double mean = Arrays.stream( sorted ).average().orElse( 0 ) / 1000.0;
        return new LatencyStats( operation, n, percentile( sorted, 0.50 ), percentile( sorted, 0.95 ), percentile( sorted, 0.99 ), mean );
    }

    private static double percentile( long[] sortedNanos, double p )
    {
        if ( sortedNanos.length == 0 )
        {
            return 0;
        }
        int rank = (int) Math.ceil( p * sortedNanos.length );
        int index = Math.min( Math.max( rank - 1, 0 ), sortedNanos.length - 1 );
        return sortedNanos[index] / 1000.0;
    }
}
