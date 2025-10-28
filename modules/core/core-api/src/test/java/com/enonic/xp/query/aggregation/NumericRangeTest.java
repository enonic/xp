package com.enonic.xp.query.aggregation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NumericRangeTest
{
    @Test
    void testBuilder()
    {
        final NumericRange numericRange = NumericRange.create().key( "key" ).from( 0.0 ).to( 1.0 ).build();

        assertEquals( "key", numericRange.getKey() );
        assertEquals( 0.0, numericRange.getFrom() );
        assertEquals( 1.0, numericRange.getTo() );

    }
}
