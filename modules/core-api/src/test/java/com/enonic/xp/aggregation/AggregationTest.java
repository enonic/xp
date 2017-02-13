package com.enonic.xp.aggregation;


import org.junit.Test;

import static org.junit.Assert.*;


public class AggregationTest
{
    @Test
    public void addAggregations()
    {
        BucketAggregation.Builder builder = Aggregation.bucketAggregation( "aaa" );

        Aggregations aggregations =
            Aggregations.from( Aggregation.bucketAggregation( "aaa" ).build(), Aggregation.bucketAggregation( "bbb" ).build(),
                               Aggregation.bucketAggregation( "ccc" ).build() );

        builder.addAggregations( aggregations );
        Aggregation aggregation = builder.build();

        assertEquals( 3, aggregation.getSubAggregations().getSize() );
        assertNotNull( aggregation.getSubAggregations().get( "aaa" ) );
        assertNotNull( aggregation.getSubAggregations().get( "bbb" ) );
        assertNotNull( aggregation.getSubAggregations().get( "ccc" ) );
    }

    @Test
    public void addSybAggregation()
    {
        BucketAggregation.Builder builder = Aggregation.bucketAggregation( "aaa" );

        builder.addSubAggregation( Aggregation.bucketAggregation( "aaa" ).build() );
        builder.addSubAggregation( Aggregation.bucketAggregation( "bbb" ).build() );
        builder.addSubAggregation( Aggregation.bucketAggregation( "ccc" ).build() );
        Aggregation aggregation = builder.build();

        assertEquals( 3, aggregation.getSubAggregations().getSize() );
        assertNotNull( aggregation.getSubAggregations().get( "aaa" ) );
        assertNotNull( aggregation.getSubAggregations().get( "bbb" ) );
        assertNotNull( aggregation.getSubAggregations().get( "ccc" ) );
    }
}
