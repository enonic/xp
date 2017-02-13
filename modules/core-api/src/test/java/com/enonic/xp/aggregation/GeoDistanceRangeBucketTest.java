package com.enonic.xp.aggregation;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;


public class GeoDistanceRangeBucketTest
{
    @Test
    public void builder()
    {
        GeoDistanceRangeBucket.Builder builder = GeoDistanceRangeBucket.create();

        builder.from( -777 );
        builder.to( 1000.9 );

        GeoDistanceRangeBucket geoDistanceRangeBucket = builder.build();

        assertNotNull( geoDistanceRangeBucket.getFrom() );
        assertNotNull( geoDistanceRangeBucket.getTo() );

        assertEquals( -777, geoDistanceRangeBucket.getFrom() );
        assertEquals( 1000.9, geoDistanceRangeBucket.getTo() );
    }
}
