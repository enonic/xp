package com.enonic.wem.api.data.type;

final class ValueTypeImpl<T>
    implements ValueType<T>
{
    private final int key;

    private final String name;

    private final Class<T> classType;

    private final JavaTypeConverter<T> converter;

    public ValueTypeImpl( final int key, final String name, final JavaTypeConverter<T> converter )
    {
        this.key = key;
        this.name = name;
        this.classType = converter.getType();
        this.converter = converter;
    }

    @Override
    public final int getKey()
    {
        return this.key;
    }

    @Override
    public final String getName()
    {
        return this.name;
    }

    @Override
    public final Class<T> getJavaType()
    {
        return this.classType;
    }

    @Override
    public final boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ValueTypeImpl ) )
        {
            return false;
        }

        final ValueTypeImpl that = (ValueTypeImpl) o;
        return key == that.key;
    }

    @Override
    public final int hashCode()
    {
        return key;
    }

    @Override
    public final String toString()
    {
        return name;
    }

    @Override
    public final T convert( final Object object )
    {
        try
        {
            final T value = this.converter.convertFrom( object );
            if ( value != null )
            {
                return value;
            }
        }
        catch ( final Exception e )
        {
            throw convertError( object, e.getMessage() );
        }

        throw convertError( object, null );
    }

    private ValueTypeException convertError( final Object value, final String reason )
    {
        final String message = "Value of type [%s] cannot be converted to [%s]" + ( ( reason != null ) ? ": %s" : "" );
        throw new ValueTypeException( message, value.getClass().getName(), getName(), reason );
    }
}
