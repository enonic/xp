package com.enonic.xp.data;

import java.util.Objects;

import com.google.common.annotations.Beta;

@Beta
public abstract class ValueType<T>
{
    private final String name;

    private final Class<T> classType;

    private final JavaTypeConverter<T> converter;

    protected ValueType( final String name, final JavaTypeConverter<T> converter )
    {
        this.name = name;
        this.classType = converter.getType();
        this.converter = converter;
    }

    public final String getName()
    {
        return this.name;
    }

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
        if ( !( o instanceof ValueType ) )
        {
            return false;
        }

        final ValueType that = (ValueType) o;

        return Objects.equals( name, that.name );
    }

    @Override
    public final int hashCode()
    {
        return Objects.hash( name );
    }

    @Override
    public final String toString()
    {
        return name;
    }

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

    T convertNullSafe( final Object object )
    {
        if ( object == null )
        {
            return null;
        }
        return convert( object );
    }

    private ValueTypeException convertError( final Object value, final String reason )
    {
        final java.lang.String message = "Value of type [%s] cannot be converted to [%s]" + ( ( reason != null ) ? ": %s" : "" );
        throw new ValueTypeException( message, value.getClass().getName(), getName(), reason );
    }

    /**
     * Returns a new Value of this ValueType from object used in JSON.
     * See {@link Value#toJsonValue()}
     */
    public abstract Value fromJsonValue( final Object object );
}
