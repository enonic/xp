package com.enonic.xp.aggregation;


import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class AggregationsTest
{

    private static final ArrayList<Aggregation> LIST = new ArrayList();

    @BeforeAll
    public static void initApplicationKeys()
    {
        AggregationsTest.LIST.add( Aggregation.bucketAggregation( "aaa" ).build() );
        AggregationsTest.LIST.add( Aggregation.bucketAggregation( "bbb" ).build() );
        AggregationsTest.LIST.add( Aggregation.bucketAggregation( "ccc" ).build() );
    }

    @Test
    void fromEmpty()
    {
        Aggregations aggregations = Aggregations.empty();
        assertEquals( 0, aggregations.getSize() );
    }

    @Test
    void fromIterable()
    {
        Aggregations aggregations = Aggregations.from( AggregationsTest.LIST );

        assertEquals( 3, aggregations.getSize() );
        assertEquals( "aaa", aggregations.first().getName() );
        assertNotNull( aggregations.get( "aaa" ) );
        assertNotNull( aggregations.get( "bbb" ) );
        assertNotNull( aggregations.get( "ccc" ) );
    }

    @Test
    void fromArrayList()
    {
        Aggregations aggregations =
            Aggregations.from( AggregationsTest.LIST.get( 0 ), AggregationsTest.LIST.get( 1 ), AggregationsTest.LIST.get( 2 ) );

        assertEquals( 3, aggregations.getSize() );
        assertEquals( "aaa", aggregations.first().getName() );
        assertNotNull( aggregations.get( "aaa" ) );
        assertNotNull( aggregations.get( "bbb" ) );
        assertNotNull( aggregations.get( "ccc" ) );
    }
}
