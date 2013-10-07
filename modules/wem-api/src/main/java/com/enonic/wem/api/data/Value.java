package com.enonic.wem.api.data;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.type.InconvertibleValueException;
import com.enonic.wem.api.data.type.JavaTypeConverter;
import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.data.type.ValueTypes;

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
    public java.lang.String getString()
        throws ClassCastException
    {
        return (java.lang.String) object;
    }

    /**
     * Attempts to return value as Long using casting.
     *
     * @throws ClassCastException if value is not of type Long.
     */
    public java.lang.Long getLong()
        throws ClassCastException
    {
        return (java.lang.Long) object;
    }

    /**
     * Attempts to return value as Double using casting.
     *
     * @throws ClassCastException if value is not of type Double.
     */
    public java.lang.Double getDouble()
        throws ClassCastException
    {
        return (java.lang.Double) object;
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
     * @throws com.enonic.wem.api.data.type.InconvertibleValueException if value is not convertible to String.
     */
    public java.lang.String asString()
        throws InconvertibleValueException
    {
        final java.lang.String converted = JavaTypeConverter.String.GET.convertFrom( object );
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
    public java.lang.Long asLong()
        throws InconvertibleValueException
    {
        final java.lang.Long converted = JavaTypeConverter.Long.GET.convertFrom( object );
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
    public java.lang.Double asDouble()
        throws InconvertibleValueException
    {
        final java.lang.Double converted = JavaTypeConverter.Double.GET.convertFrom( object );
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
    public org.joda.time.DateMidnight asDateMidnight()
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
     * Attempts to return value as org.joda.time.DateTime, using best effort converting if value is not of type org.joda.time.DateTime.
     *
     * @throws InconvertibleValueException if value is not convertible to org.joda.time.DateTime.
     */
    public org.joda.time.DateTime asDateTime()
        throws InconvertibleValueException
    {
        final org.joda.time.DateTime converted = JavaTypeConverter.DateTime.GET.convertFrom( object );
        if ( object != null && converted == null )
        {
            throw new InconvertibleValueException( object, JavaTypeConverter.DateTime.GET );
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
    public java.lang.String toString()
    {
        return java.lang.String.valueOf( object );
    }

    public Property newProperty( final java.lang.String name )
    {
        return getType().newProperty( name, this );
    }

    public static final class DateMidnight
        extends Value<org.joda.time.DateMidnight>
    {
        public DateMidnight( final org.joda.time.DateMidnight value )
        {
            super( ValueTypes.DATE_MIDNIGHT, value );
        }

        public DateMidnight( final org.joda.time.DateTime value )
        {
            super( ValueTypes.DATE_MIDNIGHT, value.toDateMidnight() );
        }

        public DateMidnight( final java.lang.String value )
        {
            super( ValueTypes.DATE_MIDNIGHT, JavaTypeConverter.DateMidnight.GET.convertFromString( value ) );
        }
    }

    public static final class DateTime
        extends Value<org.joda.time.DateTime>
    {
        public DateTime( final org.joda.time.DateTime value )
        {
            super( ValueTypes.DATE_TIME, value );
        }

        public DateTime( final org.joda.time.DateMidnight value )
        {
            super( ValueTypes.DATE_TIME, value.toDateTime() );
        }

        public DateTime( final java.lang.String value )
        {
            super( ValueTypes.DATE_TIME, JavaTypeConverter.DateTime.GET.convertFromString( value ) );
        }
    }

    public static final class Long
        extends Value<java.lang.Long>
    {
        public Long( final java.lang.Long value )
        {
            super( ValueTypes.LONG, value );
        }

        public Long( final Integer value )
        {
            super( ValueTypes.LONG, java.lang.Long.valueOf( value ) );
        }

        public Long( final Short value )
        {
            super( ValueTypes.LONG, java.lang.Long.valueOf( value ) );
        }

    }

    public static final class Double
        extends Value<java.lang.Double>
    {
        public Double( final java.lang.Double value )
        {
            super( ValueTypes.DOUBLE, value );
        }

        public Double( final Float value )
        {
            super( ValueTypes.DOUBLE, java.lang.Double.valueOf( value ) );
        }
    }

    public static final class String
        extends Value<java.lang.String>
    {
        public String( final java.lang.String value )
        {
            super( ValueTypes.STRING, value );
        }
    }

    public static final class Xml
        extends Value<java.lang.String>
    {
        public Xml( final java.lang.String value )
        {
            super( ValueTypes.XML, value );
        }
    }

    public static final class HtmlPart
        extends Value<java.lang.String>
    {
        public HtmlPart( final java.lang.String value )
        {
            super( ValueTypes.HTML_PART, value );
        }
    }

    public static final class GeographicCoordinate
        extends Value<java.lang.String>
    {
        public GeographicCoordinate( final java.lang.String value )
        {
            super( ValueTypes.GEOGRAPHIC_COORDINATE, value );
        }
    }

    public static final class ContentId
        extends Value<com.enonic.wem.api.content.ContentId>
    {
        public ContentId( final com.enonic.wem.api.content.ContentId value )
        {
            super( ValueTypes.CONTENT_ID, value );
        }

        public ContentId( final java.lang.String value )
        {
            super( ValueTypes.CONTENT_ID, com.enonic.wem.api.content.ContentId.from( value ) );
        }
    }

    public static final class AttachmentName
        extends Value<java.lang.String>
    {
        public AttachmentName( final java.lang.String value )
        {
            super( ValueTypes.ATTACHMENT_NAME, value );
        }
    }

    public static final class BinaryId
        extends Value<com.enonic.wem.api.content.binary.BinaryId>
    {
        public BinaryId( final com.enonic.wem.api.content.binary.BinaryId value )
        {
            super( ValueTypes.BINARY_ID, value );
        }

        public BinaryId( final java.lang.String value )
        {
            super( ValueTypes.BINARY_ID, com.enonic.wem.api.content.binary.BinaryId.from( value ) );
        }
    }
}
