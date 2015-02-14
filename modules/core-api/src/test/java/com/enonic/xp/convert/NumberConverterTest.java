package com.enonic.xp.convert;

import org.junit.Test;

import com.enonic.xp.convert.ConvertException;
import com.enonic.xp.convert.Converters;

import static org.junit.Assert.*;

public abstract class NumberConverterTest<T extends Number>
{
    private final Class<T> type;

    private final T num;

    public NumberConverterTest( final Class<T> type, final T num )
    {
        this.type = type;
        this.num = num;
    }

    @Test
    public void testFromString()
    {
        assertEquals( this.num, Converters.convert( this.num.toString(), this.type ) );
    }

    @Test(expected = ConvertException.class)
    public void testParseError()
    {
        Converters.convert( "abc", this.type );
    }

    @Test
    public void testFromNumber()
    {
        assertEquals( this.num, Converters.convert( (byte) 11, this.type ) );
        assertEquals( this.num, Converters.convert( (short) 11, this.type ) );
        assertEquals( this.num, Converters.convert( 11, this.type ) );
        assertEquals( this.num, Converters.convert( 11L, this.type ) );
        assertEquals( this.num, Converters.convert( 11.0f, this.type ) );
        assertEquals( this.num, Converters.convert( 11.0d, this.type ) );
    }
}
