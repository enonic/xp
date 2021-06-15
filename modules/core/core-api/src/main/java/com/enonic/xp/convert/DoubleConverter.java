package com.enonic.xp.convert;

final class DoubleConverter
    extends NumberConverter<Double>
{
    DoubleConverter()
    {
        super( Double.class );
    }

    @Override
    protected Double fromNumber( final Number value )
    {
        return value.doubleValue();
    }

    @Override
    protected Double parse( final String value )
    {
        return Double.parseDouble( value );
    }
}
