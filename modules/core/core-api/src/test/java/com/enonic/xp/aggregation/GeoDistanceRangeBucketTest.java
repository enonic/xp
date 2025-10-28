package com.enonic.xp.aggregation;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class GeoDistanceRangeBucketTest
{
    @Test
    void builder()
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
