package com.enonic.xp.aggregation;


import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;


public class AggregationTest
{
    private static ArrayList<Aggregation> list = new ArrayList();

    @BeforeClass
    public static void initModuleKeys()
    {
        AggregationTest.list.add( Aggregation.bucketAggregation( "aaa" ).build() );
        AggregationTest.list.add( Aggregation.bucketAggregation( "bbb" ).build() );
        AggregationTest.list.add( Aggregation.bucketAggregation( "ccc" ).build() );
    }

    @Test
    public void addAggregations()
    {
        final BucketAggregation.Builder builder = Aggregation.bucketAggregation( "xxx" );

        final Aggregations aggregations =
            Aggregations.from( AggregationTest.list.get( 0 ), AggregationTest.list.get( 1 ), AggregationTest.list.get( 2 ) );

        builder.addAggregations( aggregations );
        final Aggregation aggregation = builder.build();

        assertEquals( 3, aggregation.getSubAggregations().getSize() );
        assertNotNull( aggregation.getSubAggregations().get( "aaa" ) );
        assertNotNull( aggregation.getSubAggregations().get( "bbb" ) );
        assertNotNull( aggregation.getSubAggregations().get( "ccc" ) );
    }

    @Test
    public void addSubAggregation()
    {
        final BucketAggregation.Builder builder = Aggregation.bucketAggregation( "xxx" );

        builder.addSubAggregation( AggregationTest.list.get( 0 ) );
        builder.addSubAggregation( AggregationTest.list.get( 1 ) );
        builder.addSubAggregation( AggregationTest.list.get( 2 ) );

        final Aggregation aggregation = builder.build();

        assertEquals( 3, aggregation.getSubAggregations().getSize() );
        assertNotNull( aggregation.getSubAggregations().get( "aaa" ) );
        assertNotNull( aggregation.getSubAggregations().get( "bbb" ) );
        assertNotNull( aggregation.getSubAggregations().get( "ccc" ) );
    }
}
