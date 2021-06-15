package com.enonic.xp.convert;

abstract class NumberConverter<T extends Number>
    implements Converter<T>
{
    private final Class<T> type;

    NumberConverter( final Class<T> type )
    {
        this.type = type;
    }

    @Override
    public final Class<T> getType()
    {
        return this.type;
    }

    @Override
    public final T convert( final Object value )
    {
        if ( value instanceof Number )
        {
            return fromNumber( (Number) value );
        }

        return parse( value.toString() );
    }

    protected abstract T fromNumber( Number value );

    protected abstract T parse( String value );
}
