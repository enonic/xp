package com.enonic.xp.convert;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void testFromString()
    {
        assertEquals( this.num, Converters.convert( this.num.toString(), this.type ) );
    }

    @Test
    void testParseError()
    {
        assertThrows(ConvertException.class, () -> Converters.convert( "abc", this.type ) );
    }

    @Test
    void testFromNumber()
    {
        assertEquals( this.num, Converters.convert( (byte) 11, this.type ) );
        assertEquals( this.num, Converters.convert( (short) 11, this.type ) );
        assertEquals( this.num, Converters.convert( 11, this.type ) );
        assertEquals( this.num, Converters.convert( 11L, this.type ) );
        assertEquals( this.num, Converters.convert( 11.0f, this.type ) );
        assertEquals( this.num, Converters.convert( 11.0d, this.type ) );
        assertEquals( this.num, Converters.convertOrDefault( null, this.type, this.num ) );
    }
}
