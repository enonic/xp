package com.enonic.xp.convert;

final class LongConverter
    extends NumberConverter<Long>
{
    LongConverter()
    {
        super( Long.class );
    }

    @Override
    protected Long fromNumber( final Number value )
    {
        return value.longValue();
    }

    @Override
    protected Long parse( final String value )
    {
        return Long.parseLong( value );
    }
}
