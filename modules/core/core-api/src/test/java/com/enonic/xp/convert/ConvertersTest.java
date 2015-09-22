package com.enonic.xp.convert;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConvertersTest
{
    @Test
    public void testConvert()
    {
        final int value1 = Converters.convert( "123", Integer.class );
        assertEquals( 123, value1 );

        final boolean value2 = Converters.convert( "true", Boolean.class );
        assertEquals( true, value2 );

        final Integer value3 = Converters.convert( null, Integer.class );
        assertNull( value3 );
    }

    @Test(expected = ConvertException.class)
    public void testConvertError()
    {
        Converters.convert( "abc", Integer.class );
    }

    @Test
    public void testConvertOrNull()
    {
        final Integer value1 = Converters.convertOrNull( "123", Integer.class );
        assertEquals( new Integer( 123 ), value1 );

        final Integer value2 = Converters.convertOrNull( "abc", Integer.class );
        assertNull( value2 );
    }

    @Test
    public void testConvertOrDefault()
    {
        final int value1 = Converters.convertOrDefault( "123", Integer.class, 1 );
        assertEquals( 123, value1 );

        final int value2 = Converters.convertOrDefault( "abc", Integer.class, 1 );
        assertEquals( 1, value2 );
    }
}
