package com.enonic.xp.aggregation;


import org.junit.Test;

import static org.junit.Assert.*;


public class SingleValueMetricAggregationTest
{
    private static final double DELTA = 1e-14;

    @Test
    public void builder()
    {
        SingleValueMetricAggregation.Builder builder = SingleValueMetricAggregation.create( "aaa" );

        builder.value( -1000.9 );

        SingleValueMetricAggregation singleValueMetricAggregation = builder.build();

        assertEquals( "aaa", singleValueMetricAggregation.getName() );
        assertEquals( -1000.9, singleValueMetricAggregation.getValue(), DELTA );
    }
}
