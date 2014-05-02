package com.enonic.wem.api.data.type;

import com.enonic.wem.api.data.Value;

/**
 * ValueTypes should only be created when:
 * * the type can give something more when indexed
 * * needs validation.
 */
public abstract class ValueType<T>
{
    private final int key;

    private final String name;

    private final Class classType;

    private final JavaTypeConverter<T> javaTypeConverter;

    public ValueType( final int key, final String name, final JavaTypeConverter<T> javaTypeConverter )
    {
        this.key = key;
        this.name = name;
        this.classType = javaTypeConverter.getClass();
        this.javaTypeConverter = javaTypeConverter;
    }

    public final int getKey()
    {
        return key;
    }

    public final String getName()
    {
        return name;
    }

    public final Class getClassType()
    {
        return classType;
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

    /**
     * Attempts to convert given object to this type.
     */
    public final T convert( final Object object )
    {
        try
        {
            final T value = this.javaTypeConverter.convertFrom( object );
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

    /**
     * Attempts to convert given java.lang.String to this type.
     */
    public final T convert( final String object )
    {
        return this.javaTypeConverter.convertFromString( object );
    }

    public abstract Value newValue( Object value );
}
