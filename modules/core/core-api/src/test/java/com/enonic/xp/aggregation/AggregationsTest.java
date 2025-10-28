package com.enonic.xp.aggregation;


import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class AggregationsTest
{

    private static final ArrayList<Aggregation> list = new ArrayList();

    @BeforeAll
    public static void initApplicationKeys()
    {
        AggregationsTest.list.add( Aggregation.bucketAggregation( "aaa" ).build() );
        AggregationsTest.list.add( Aggregation.bucketAggregation( "bbb" ).build() );
        AggregationsTest.list.add( Aggregation.bucketAggregation( "ccc" ).build() );
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
        Aggregations aggregations = Aggregations.from( AggregationsTest.list );

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
            Aggregations.from( AggregationsTest.list.get( 0 ), AggregationsTest.list.get( 1 ), AggregationsTest.list.get( 2 ) );

        assertEquals( 3, aggregations.getSize() );
        assertEquals( "aaa", aggregations.first().getName() );
        assertNotNull( aggregations.get( "aaa" ) );
        assertNotNull( aggregations.get( "bbb" ) );
        assertNotNull( aggregations.get( "ccc" ) );
    }
}
