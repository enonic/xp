package com.enonic.xp.convert;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BooleanConverterTest
{
    @Test
    void testSameType()
    {
        assertEquals( true, Converters.convert( true, Boolean.class ) );
    }

    @Test
    void testFromString()
    {
        assertEquals( true, Converters.convert( "true", Boolean.class ) );
        assertEquals( true, Converters.convert( "on", Boolean.class ) );
        assertEquals( true, Converters.convert( "yes", Boolean.class ) );
        assertEquals( true, Converters.convert( "1", Boolean.class ) );
        assertEquals( false, Converters.convert( "jepp", Boolean.class ) );
        assertEquals( false, Converters.convert( "false", Boolean.class ) );
        assertEquals( true, Converters.convertOrDefault( null, Boolean.class, true ) );
    }

    @Test
    void testFromNumber()
    {
        assertEquals( true, Converters.convert( 11, Boolean.class ) );
        assertEquals( true, Converters.convert( 2L, Boolean.class ) );
        assertEquals( false, Converters.convert( (byte) 0, Boolean.class ) );
    }
}
