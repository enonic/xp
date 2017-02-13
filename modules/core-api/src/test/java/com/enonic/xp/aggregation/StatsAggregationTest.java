package com.enonic.xp.aggregation;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;


public class StatsAggregationTest
{
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
        assertEquals( -1000.9, statsAggregation.getMin() );
        assertEquals( 1.23e4, statsAggregation.getMax() );
        assertEquals( 34.0, statsAggregation.getCount() );
        assertEquals( 1.0009e4, statsAggregation.getAvg() );
        assertEquals( 1.4e4, statsAggregation.getSum() );
    }
}
