package com.enonic.xp.convert;

import org.junit.Test;

import static org.junit.Assert.*;

public class BooleanConverterTest
{
    @Test
    public void testSameType()
    {
        assertEquals( true, Converters.convert( true, Boolean.class ) );
    }

    @Test
    public void testFromString()
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
    public void testFromNumber()
    {
        assertEquals( true, Converters.convert( 11, Boolean.class ) );
        assertEquals( true, Converters.convert( 2L, Boolean.class ) );
        assertEquals( false, Converters.convert( (byte) 0, Boolean.class ) );
    }
}
