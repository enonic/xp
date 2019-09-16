package com.enonic.xp.query.aggregation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NumericRangeTest
{
    @Test
    public void testBuilder()
    {
        final NumericRange numericRange = NumericRange.create().key( "key" ).from( 0.0 ).to( 1.0 ).build();

        assertEquals( "key", numericRange.getKey() );
        assertEquals( new Double( 0.0 ), numericRange.getFrom() );
        assertEquals( new Double( 1.0 ), numericRange.getTo() );

    }
}
