package com.enonic.xp.aggregation;


import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;


public class AggregationsTest
{

    private static ArrayList<Aggregation> list = new ArrayList();

    @BeforeClass
    public static void initModuleKeys()
    {
        AggregationsTest.list.add( Aggregation.bucketAggregation( "aaa" ).build() );
        AggregationsTest.list.add( Aggregation.bucketAggregation( "bbb" ).build() );
        AggregationsTest.list.add( Aggregation.bucketAggregation( "ccc" ).build() );
    }

    @Test
    public void fromEmpty()
    {
        Aggregations aggregations = Aggregations.empty();
        assertEquals( 0, aggregations.getSize() );
    }

    @Test
    public void fromIterable()
    {
        Aggregations aggregations = Aggregations.from( AggregationsTest.list );

        assertEquals( 3, aggregations.getSize() );
        assertEquals( "aaa", aggregations.first().getName() );
        assertNotNull( aggregations.get( "aaa" ) );
        assertNotNull( aggregations.get( "bbb" ) );
        assertNotNull( aggregations.get( "ccc" ) );
    }

    @Test
    public void fromArrayList()
    {
        Aggregations aggregations =
            Aggregations.from( AggregationsTest.list.get( 0 ), AggregationsTest.list.get( 1 ), AggregationsTest.list.get( 2 ) );

        assertEquals( 3, aggregations.getSize() );
        assertEquals( "aaa", aggregations.first().getName() );
        assertNotNull( aggregations.get( "aaa" ) );
        assertNotNull( aggregations.get( "bbb" ) );
        assertNotNull( aggregations.get( "ccc" ) );
    }
}
