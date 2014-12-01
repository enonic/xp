package com.enonic.wem.script.convert;

final class ShortConverter
    extends NumberConverter<Short>
{
    public ShortConverter()
    {
        super( Short.class );
    }

    @Override
    protected Short fromNumber( final Number value )
    {
        return value.shortValue();
    }

    @Override
    protected Short parse( final String value )
    {
        return Short.parseShort( value );
    }
}
