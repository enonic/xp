package com.enonic.wem.api.content.data;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.type.InconvertibleValueException;
import com.enonic.wem.api.content.data.type.JavaTypeConverter;
import com.enonic.wem.api.content.data.type.ValueType;
import com.enonic.wem.api.content.data.type.ValueTypes;

/**
 * A generic holder for the value of a Property.
 */
public abstract class Value<T>
{
    private final ValueType type;

    private final Object object;

    private Value( final ValueType type, final T value )
    {
        Preconditions.checkNotNull( type, "type cannot be null" );
        Preconditions.checkNotNull( value, "value cannot be null" );
        Preconditions.checkArgument( !( value instanceof Value ), "The value of a Value cannot be: " + value.getClass() );

        this.type = type;
        final boolean valueIsOfExpectedJavaClass = type.isValueOfExpectedJavaClass( value );
        if ( !valueIsOfExpectedJavaClass )
        {
            throw new IllegalArgumentException(
                "Value expected to be of Java type [" + type.getJavaTypeConverter().getType() + "]: " + value.getClass() );
        }

        object = value;
        type.checkValidity( this );
    }

    public boolean isJavaType( Class javaType )
    {
        return javaType.isInstance( object );
    }

    public ValueType getType()
    {
        return type;
    }

    /**
     * Returns value as Object.
     */
    public Object getObject()
    {
        return object;
    }

    /**
     * Attempts to return value as String using casting.
     *
     * @throws ClassCastException if value is not of type String.
     */
    public String getString()
        throws ClassCastException
    {
        return (String) object;
    }

    /**
     * Attempts to return value as Long using casting.
     *
     * @throws ClassCastException if value is not of type Long.
     */
    public Long getLong()
        throws ClassCastException
    {
        return (Long) object;
    }

    /**
     * Attempts to return value as Double using casting.
     *
     * @throws ClassCastException if value is not of type Double.
     */
    public Double getDouble()
        throws ClassCastException
    {
        return (Double) object;
    }

    /**
     * Attempts to return value as org.joda.time.DateMidnight using casting.
     *
     * @throws ClassCastException if value is not of type org.joda.time.DateMidnight.
     */
    public org.joda.time.DateMidnight getDate()
        throws ClassCastException
    {
        return (org.joda.time.DateMidnight) object;
    }

    /**
     * Attempts to return value as com.enonic.wem.api.content.binary.BinaryId using casting.
     *
     * @throws ClassCastException if value is not of type com.enonic.wem.api.content.binary.BinaryId.
     */
    public com.enonic.wem.api.content.binary.BinaryId getBinaryId()
        throws ClassCastException
    {
        return (com.enonic.wem.api.content.binary.BinaryId) object;
    }

    /**
     * Attempts to return value as com.enonic.wem.api.content.ContentId using casting.
     *
     * @throws ClassCastException if value is not of type com.enonic.wem.api.content.ContentId.
     */
    public com.enonic.wem.api.content.ContentId getContentId()
        throws ClassCastException
    {
        return (com.enonic.wem.api.content.ContentId) object;
    }

    /**
     * Attempts to return value as String, using best effort converting if value is not of type String.
     *
     * @throws InconvertibleValueException if value is not convertible to String.
     */
    public String asString()
        throws InconvertibleValueException
    {
        final String converted = JavaTypeConverter.String.GET.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaTypeConverter.String.GET );
        }
        return converted;
    }

    /**
     * Attempts to return value as com.enonic.wem.api.content.ContentId, using best effort converting if value is not of type com.enonic.wem.api.content.ContentId.
     *
     * @throws InconvertibleValueException if value is not convertible to com.enonic.wem.api.content.ContentId.
     */
    public com.enonic.wem.api.content.ContentId asContentId()
        throws InconvertibleValueException
    {
        final com.enonic.wem.api.content.ContentId converted = JavaTypeConverter.ContentId.GET.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaTypeConverter.ContentId.GET );
        }
        return converted;
    }

    /**
     * Attempts to return value as Long, using best effort converting if value is not of type Long.
     *
     * @throws InconvertibleValueException if value is not convertible to Long.
     */
    public Long asLong()
        throws InconvertibleValueException
    {
        final Long converted = JavaTypeConverter.Long.GET.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaTypeConverter.Long.GET );
        }
        return converted;
    }

    /**
     * Attempts to return value as Double, using best effort converting if value is not of type Double.
     *
     * @throws InconvertibleValueException if value is not convertible to Double.
     */
    public Double asDouble()
        throws InconvertibleValueException
    {
        final Double converted = JavaTypeConverter.Double.GET.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaTypeConverter.Double.GET );
        }
        return converted;
    }

    /**
     * Attempts to return value as org.joda.time.DateMidnight, using best effort converting if value is not of type org.joda.time.DateMidnight.
     *
     * @throws InconvertibleValueException if value is not convertible to org.joda.time.DateMidnight.
     */
    public org.joda.time.DateMidnight asDate()
        throws InconvertibleValueException
    {
        final org.joda.time.DateMidnight converted = JavaTypeConverter.DateMidnight.GET.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaTypeConverter.DateMidnight.GET );
        }
        return converted;
    }

    /**
     * Attempts to return value as com.enonic.wem.api.content.binary.BinaryId, using best effort converting if value is not of type com.enonic.wem.api.content.binary.BinaryId.
     *
     * @throws InconvertibleValueException if value is not convertible to com.enonic.wem.api.content.binary.BinaryId.
     */
    public com.enonic.wem.api.content.binary.BinaryId asBinaryId()
        throws InconvertibleValueException
    {
        final com.enonic.wem.api.content.binary.BinaryId converted = JavaTypeConverter.BinaryId.GET.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaTypeConverter.BinaryId.GET );
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
            super( ValueTypes.DATE_MIDNIGHT, JavaTypeConverter.DateMidnight.GET.convertFrom( value ) );
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
