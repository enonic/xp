package com.enonic.xp.convert;

final class ByteConverter
    extends NumberConverter<Byte>
{
    ByteConverter()
    {
        super( Byte.class );
    }

    @Override
    protected Byte fromNumber( final Number value )
    {
        return value.byteValue();
    }

    @Override
    protected Byte parse( final String value )
    {
        return Byte.parseByte( value );
    }
}
