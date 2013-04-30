package com.enonic.wem.api.content.data;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.type.BaseValueType;
import com.enonic.wem.api.content.data.type.InconvertibleValueException;
import com.enonic.wem.api.content.data.type.JavaType;
import com.enonic.wem.api.content.data.type.ValueTypes;

/**
 * A generic holder for the value of a Property.
 */
public class Value<T>
{
    private final BaseValueType type;

    private final Object object;

    private Value( final BaseValueType type, final T value )
    {
        Preconditions.checkNotNull( type, "type cannot be null" );
        Preconditions.checkNotNull( value, "value cannot be null" );
        Preconditions.checkArgument( !( value instanceof Value ), "The value of a Value cannot be: " + value.getClass() );

        this.type = type;
        final boolean valueIsOfExpectedJavaClass = type.isValueOfExpectedJavaClass( value );
        if ( !valueIsOfExpectedJavaClass )
        {
            throw new IllegalArgumentException(
                "Value expected to be of Java type [" + type.getJavaType().getType() + "]: " + value.getClass() );
        }

        object = value;
        type.checkValidity( this );
    }

    public boolean isJavaType( Class javaType )
    {
        return javaType.isInstance( object );
    }

    public BaseValueType getType()
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

    public com.enonic.wem.api.content.ContentId asContentId()
        throws InconvertibleValueException
    {
        final com.enonic.wem.api.content.ContentId converted = JavaType.CONTENT_ID.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaType.CONTENT_ID );
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

    public org.joda.time.DateMidnight asDate()
        throws InconvertibleValueException
    {
        final org.joda.time.DateMidnight converted = JavaType.DATE_MIDNIGHT.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaType.DATE_MIDNIGHT );
        }
        return converted;
    }

    public com.enonic.wem.api.content.binary.BinaryId asBinaryId()
        throws InconvertibleValueException
    {
        final com.enonic.wem.api.content.binary.BinaryId converted = JavaType.BINARY_ID.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaType.BINARY_ID );
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

    public Property newProperty( final String name )
    {
        return getType().newProperty( name, this );
    }

    public static final class Date
        extends Value<org.joda.time.DateMidnight>
    {
        public Date( final org.joda.time.DateMidnight value )
        {
            super( ValueTypes.DATE_MIDNIGHT, value );
        }

        public Date( final String value )
        {
            super( ValueTypes.DATE_MIDNIGHT, JavaType.DATE_MIDNIGHT.convertFrom( value ) );
        }
    }

    public static final class WholeNumber
        extends Value<Long>
    {
        public WholeNumber( final Long value )
        {
            super( ValueTypes.WHOLE_NUMBER, value );
        }

        public WholeNumber( final Integer value )
        {
            super( ValueTypes.WHOLE_NUMBER, Long.valueOf( value ) );
        }

        public WholeNumber( final Short value )
        {
            super( ValueTypes.WHOLE_NUMBER, Long.valueOf( value ) );
        }

    }

    public static final class DecimalNumber
        extends Value<Double>
    {
        public DecimalNumber( final Double value )
        {
            super( ValueTypes.DECIMAL_NUMBER, value );
        }
    }

    public static final class Text
        extends Value<String>
    {
        public Text( final String value )
        {
            super( ValueTypes.TEXT, value );
        }
    }

    public static final class Xml
        extends Value<String>
    {
        public Xml( final String value )
        {
            super( ValueTypes.XML, value );
        }
    }

    public static final class HtmlPart
        extends Value<String>
    {
        public HtmlPart( final String value )
        {
            super( ValueTypes.HTML_PART, value );
        }
    }

    public static final class GeographicCoordinate
        extends Value<String>
    {
        public GeographicCoordinate( String value )
        {
            super( ValueTypes.GEOGRAPHIC_COORDINATE, value );
        }
    }

    public static final class ContentId
        extends Value<com.enonic.wem.api.content.ContentId>
    {
        public ContentId( com.enonic.wem.api.content.ContentId value )
        {
            super( ValueTypes.CONTENT_ID, value );
        }
    }

    public static final class BinaryId
        extends Value<com.enonic.wem.api.content.binary.BinaryId>
    {
        public BinaryId( com.enonic.wem.api.content.binary.BinaryId value )
        {
            super( ValueTypes.BINARY_ID, value );
        }
    }
}
