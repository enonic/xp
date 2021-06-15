package com.enonic.xp.convert;

final class FloatConverter
    extends NumberConverter<Float>
{
    FloatConverter()
    {
        super( Float.class );
    }

    @Override
    protected Float fromNumber( final Number value )
    {
        return value.floatValue();
    }

    @Override
    protected Float parse( final String value )
    {
        return Float.parseFloat( value );
    }
}
