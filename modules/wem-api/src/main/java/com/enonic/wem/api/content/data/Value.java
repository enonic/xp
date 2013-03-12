package com.enonic.wem.api.content.data;

import java.util.Objects;

import org.joda.time.DateMidnight;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.blob.BlobKey;
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

        Preconditions.checkArgument( type.isValueOfExpectedJavaClass( object ), "Object expected to be of type [%s]: %s",
                                     type.getJavaType(), object.getClass().getSimpleName() );
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
        final DateMidnight converted = JavaType.DATE_MIDNIGHT.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaType.DATE_MIDNIGHT );
        }
        return converted;
    }

    public BlobKey asBlobKey()
        throws InconvertibleValueException
    {
        final BlobKey converted = JavaType.BLOB_KEY.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaType.BLOB_KEY );
        }
        return converted;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Value other = (Value) o;

        return Objects.equals( type, other.type ) && Objects.equals( object, other.object );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( type, object );
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

        public BaseDataType getType()
        {
            return type;
        }
    }
}
