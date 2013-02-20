package com.enonic.wem.api.content.data;

import org.joda.time.DateMidnight;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.type.BaseDataType;
import com.enonic.wem.api.content.data.type.InconvertibleValueException;
import com.enonic.wem.api.content.data.type.JavaType;

/**
 * A generic holder for the value of a Data.
 */
public final class Value
{
    private final BaseDataType type;

    private final Object object;

    private Value( final Builder builder )
    {
        Preconditions.checkNotNull( builder.type, "type cannot be null" );
        Preconditions.checkNotNull( builder.value, "value cannot be null" );
        Preconditions.checkArgument( !( builder.value instanceof Builder ), "The value of a Value cannot be: " + builder.value.getClass() );
        Preconditions.checkArgument( !( builder.value instanceof Value ), "The value of a Value cannot be: " + builder.value.getClass() );

        type = builder.type;
        object = builder.value;

        Preconditions.checkArgument( type.hasCorrectType( object ), "Object expected to be of type [%s]: %s", type.getJavaType(),
                                     object.getClass().getSimpleName() );
    }

    public boolean isJavaType( Class javaType )
    {
        return javaType.isInstance( object );
    }

    public BaseDataType getType()
    {
        return type;
    }

    public Object getObject()
    {
        return object;
    }

    public String getString()
    {
        return (String) object;
    }

    public Long getLong()
    {
        return (Long) object;
    }

    public String asString()
        throws InconvertibleValueException
    {
        final String converted = JavaType.STRING.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaType.STRING );
        }
        return converted;
    }

    public Long asLong()
        throws InconvertibleValueException
    {
        final Long converted = JavaType.LONG.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaType.LONG );
        }
        return converted;
    }

    public Double asDouble()
        throws InconvertibleValueException
    {
        final Double converted = JavaType.DOUBLE.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaType.DOUBLE );
        }
        return converted;
    }

    public DateMidnight asDate()
        throws InconvertibleValueException
    {
        final DateMidnight converted = JavaType.DATE.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaType.DATE );
        }
        return converted;
    }

    @Override
    public String toString()
    {
        return String.valueOf( object );
    }

    public static Builder newValue()
    {
        return new Builder();
    }

    public static class Builder
    {
        private BaseDataType type;

        private Object value;

        public Builder type( BaseDataType value )
        {
            this.type = value;
            return this;
        }

        public Builder value( Object value )
        {
            Preconditions.checkArgument( !( value instanceof Builder ), "The value of a Value cannot be: " + value.getClass() );
            Preconditions.checkArgument( !( value instanceof Value ), "The value of a Value cannot be: " + value.getClass() );
            this.value = value;
            return this;
        }

        public Value build()
        {
            return new Value( this );
        }
    }
}
