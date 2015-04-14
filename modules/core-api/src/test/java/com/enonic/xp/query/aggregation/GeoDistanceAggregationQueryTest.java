package com.enonic.xp.query.aggregation;

import org.junit.Test;

import com.enonic.xp.util.GeoPoint;

import static org.junit.Assert.*;

public class GeoDistanceAggregationQueryTest
{
    @Test
    public void testBuilder()
    {
        final GeoDistanceAggregationQuery query = GeoDistanceAggregationQuery.create( "geo" ).
            unit( "inch" ).
            origin( GeoPoint.from( "20,30" ) ).
            build();

        assertEquals( "geo", query.getName() );
        assertEquals( "inch", query.getUnit() );
        assertEquals( GeoPoint.from( "20,30" ), query.getOrigin() );
    }
}
