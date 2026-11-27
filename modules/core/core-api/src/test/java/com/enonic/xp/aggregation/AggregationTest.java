package com.enonic.xp.aggregation;


import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class AggregationTest
{
    private static final ArrayList<Aggregation> LIST = new ArrayList();

    @BeforeAll
    public static void initApplicationKeys()
    {
        AggregationTest.LIST.add( Aggregation.bucketAggregation( "aaa" ).build() );
        AggregationTest.LIST.add( Aggregation.bucketAggregation( "bbb" ).build() );
        AggregationTest.LIST.add( Aggregation.bucketAggregation( "ccc" ).build() );
    }

    @Test
    void addAggregations()
    {
        final BucketAggregation.Builder builder = Aggregation.bucketAggregation( "xxx" );

        final Aggregations aggregations =
            Aggregations.from( AggregationTest.LIST.get( 0 ), AggregationTest.LIST.get( 1 ), AggregationTest.LIST.get( 2 ) );

        builder.addAggregations( aggregations );
        final Aggregation aggregation = builder.build();

        assertEquals( 3, aggregation.getSubAggregations().getSize() );
        assertNotNull( aggregation.getSubAggregations().get( "aaa" ) );
        assertNotNull( aggregation.getSubAggregations().get( "bbb" ) );
        assertNotNull( aggregation.getSubAggregations().get( "ccc" ) );
    }

    @Test
    void addSubAggregation()
    {
        final BucketAggregation.Builder builder = Aggregation.bucketAggregation( "xxx" );

        builder.addSubAggregation( AggregationTest.LIST.get( 0 ) );
        builder.addSubAggregation( AggregationTest.LIST.get( 1 ) );
        builder.addSubAggregation( AggregationTest.LIST.get( 2 ) );

        final Aggregation aggregation = builder.build();

        assertEquals( 3, aggregation.getSubAggregations().getSize() );
        assertNotNull( aggregation.getSubAggregations().get( "aaa" ) );
        assertNotNull( aggregation.getSubAggregations().get( "bbb" ) );
        assertNotNull( aggregation.getSubAggregations().get( "ccc" ) );
    }
}
