package com.enonic.xp.aggregation;


import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.*;


public class AggregationsTest
{

    @Test
    public void fromEmpty()
    {
        Aggregations aggregations = Aggregations.empty();
        assertEquals( 0, aggregations.getSize() );
    }

    @Test
    public void fromIterable()
    {
        ArrayList<Aggregation> list = new ArrayList();
        list.add( Aggregation.bucketAggregation( "aaa" ).build() );
        list.add( Aggregation.bucketAggregation( "bbb" ).build() );
        list.add( Aggregation.bucketAggregation( "ccc" ).build() );

        Aggregations aggregations = Aggregations.from( list );

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
            Aggregations.from( Aggregation.bucketAggregation( "aaa" ).build(), Aggregation.bucketAggregation( "bbb" ).build(),
                               Aggregation.bucketAggregation( "ccc" ).build() );

        assertEquals( 3, aggregations.getSize() );
        assertEquals( "aaa", aggregations.first().getName() );
        assertNotNull( aggregations.get( "aaa" ) );
        assertNotNull( aggregations.get( "bbb" ) );
        assertNotNull( aggregations.get( "ccc" ) );
    }
}
