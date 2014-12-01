package com.enonic.wem.api.convert;

final class ByteConverter
    extends NumberConverter<Byte>
{
    public ByteConverter()
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
