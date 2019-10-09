package com.enonic.xp.query.aggregation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DistanceRangeTest
{
    @Test
    public void testBuilder()
    {
        final DistanceRange distanceRange = DistanceRange.create().key( "key" ).from( 0.0 ).to( 1.0 ).build();

        assertEquals( "key", distanceRange.getKey() );
        assertEquals( new Double( 0.0 ), distanceRange.getFrom() );
        assertEquals( new Double( 1.0 ), distanceRange.getTo() );

    }
}
