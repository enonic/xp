package com.enonic.xp.aggregation;


import org.junit.Test;

import static org.junit.Assert.*;


public class StatsAggregationTest
{
    private static final double DELTA = 1e-14;

    @Test
    public void builder()
    {
        StatsAggregation.Builder builder = StatsAggregation.create( "aaa" );

        builder.min( -1000.9 );
        builder.max( 1.23e4 );
        builder.count( 34.0 );
        builder.avg( 1.0009e4 );
        builder.sum( 1.4e4 );

        StatsAggregation statsAggregation = builder.build();

        assertEquals( "aaa", statsAggregation.getName() );
        assertEquals( -1000.9, statsAggregation.getMin(), DELTA );
        assertEquals( 1.23e4, statsAggregation.getMax(), DELTA );
        assertEquals( 34.0, statsAggregation.getCount(), DELTA );
        assertEquals( 1.0009e4, statsAggregation.getAvg(), DELTA );
        assertEquals( 1.4e4, statsAggregation.getSum(), DELTA );
    }
}
