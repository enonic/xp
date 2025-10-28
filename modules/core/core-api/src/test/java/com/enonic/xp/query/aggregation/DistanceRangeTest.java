package com.enonic.xp.query.aggregation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DistanceRangeTest
{
    @Test
    void testBuilder()
    {
        final DistanceRange distanceRange = DistanceRange.create().key( "key" ).from( 0.0 ).to( 1.0 ).build();

        assertEquals( "key", distanceRange.getKey() );
        assertEquals( 0.0, distanceRange.getFrom() );
        assertEquals( 1.0, distanceRange.getTo() );

    }
}
