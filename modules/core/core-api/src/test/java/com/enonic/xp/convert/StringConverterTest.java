package com.enonic.xp.convert;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringConverterTest
{
    @Test
    public void testSameType()
    {
        assertEquals( "test", Converters.convert( "test", String.class ) );
    }

    @Test
    public void testToString()
    {
        assertEquals( "true", Converters.convert( true, String.class ) );
        assertEquals( "11", Converters.convert( 11, String.class ) );
        assertEquals( "default", Converters.convertOrDefault( null, String.class, "default" ) );
    }
}
