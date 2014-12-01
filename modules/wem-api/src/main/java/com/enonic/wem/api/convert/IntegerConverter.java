package com.enonic.wem.api.convert;

final class IntegerConverter
    extends NumberConverter<Integer>
{
    public IntegerConverter()
    {
        super( Integer.class );
    }

    @Override
    protected Integer fromNumber( final Number value )
    {
        return value.intValue();
    }

    @Override
    protected Integer parse( final String value )
    {
        return Integer.parseInt( value );
    }
}
