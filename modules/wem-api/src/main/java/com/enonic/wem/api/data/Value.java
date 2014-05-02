package com.enonic.wem.api.data;

import java.util.Objects;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.data.type.InconvertibleValueException;
import com.enonic.wem.api.data.type.JavaTypeConverters;
import com.enonic.wem.api.data.type.ValueOfUnexpectedClassException;
import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.form.InvalidValueException;

/**
 * A generic holder for the value of a Property.
 */
public final class Value
{
    private final ValueType type;

    private final Object object;

    private Value( final ValueType type, final Object value )
        throws ValueOfUnexpectedClassException, InvalidValueException
    {
        Preconditions.checkNotNull( type, "type cannot be null" );
        Preconditions.checkNotNull( value, "value cannot be null" );
        Preconditions.checkArgument( !( value instanceof Value ), "The value of a Value cannot be: " + value.getClass() );
        this.type = type;
        this.object = value;
        type.checkValidity( this );
    }

    public boolean isString()
    {
        return this.type == ValueTypes.STRING;
    }

    public boolean isDateType()
    {
        return ( this.type == ValueTypes.DATE_TIME ) || ( this.type == ValueTypes.DATE_MIDNIGHT );
    }

    public boolean isNumericType()
    {
        return ( this.object instanceof Number );
    }

    public boolean isGeoPoint()
    {
        return this.type == ValueTypes.GEO_POINT;
    }

    public boolean isJavaType( final Class javaType )
    {
        return javaType.isInstance( this.object );
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
     * Attempts to return value as Boolean using casting.
     *
     * @throws ClassCastException if value is not of type Double.
     */
    public Boolean getBoolean()
        throws ClassCastException
    {
        return (Boolean) object;
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
        return (DateMidnight) object;
    }

    /**
     * Attempts to return value as com.enonic.wem.api.content.ContentId using casting.
     *
     * @throws ClassCastException if value is not of type com.enonic.wem.api.content.ContentId.
     */
    public com.enonic.wem.api.content.ContentId getContentId()
        throws ClassCastException
    {
        return (ContentId) object;
    }

    /**
     * Attempts to return value as com.enonic.wem.api.data.RootDataSet using casting.
     *
     * @throws ClassCastException if value is not of type com.enonic.wem.api.data.RootDataSet.
     */
    public com.enonic.wem.api.data.RootDataSet getData()
        throws ClassCastException
    {
        return (RootDataSet) object;
    }

    /**
     * Attempts to return value as com.enonic.wem.api.entity.EntityId using casting.
     *
     * @throws ClassCastException if value is not of type com.enonic.wem.api.entity.EntityId.
     */
    public com.enonic.wem.api.entity.EntityId getEntityId()
        throws ClassCastException
    {
        return (EntityId) object;
    }

    /**
     * Attempts to return value as String, using best effort converting if value is not of type String.
     *
     * @throws com.enonic.wem.api.data.type.InconvertibleValueException if value is not convertible to String.
     */
    public String asString()
        throws InconvertibleValueException
    {
        final String converted = JavaTypeConverters.STRING.convertFrom( object );
        if ( converted == null )
        {
            throw new InconvertibleValueException( object, JavaTypeConverters.STRING );
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
        final ContentId converted = JavaTypeConverters.CONTENT_ID.convertFrom( object );
        if ( converted == null )
        {
            throw new InconvertibleValueException( object, JavaTypeConverters.CONTENT_ID );
        }
        return converted;
    }

    /**
     * Attempts to return value as com.enonic.wem.api.entity.EntityId, using best effort converting if value is not of type com.enonic.wem.api.entity.EntityId.
     *
     * @throws InconvertibleValueException if value is not convertible to com.enonic.wem.api.entity.EntityId.
     */
    public EntityId asEntityId()
        throws InconvertibleValueException
    {
        final EntityId converted = JavaTypeConverters.ENTITY_ID.convertFrom( object );
        if ( converted == null )
        {
            throw new InconvertibleValueException( object, JavaTypeConverters.ENTITY_ID );
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
        final Long converted = JavaTypeConverters.LONG.convertFrom( object );
        if ( converted == null )
        {
            throw new InconvertibleValueException( object, JavaTypeConverters.LONG );
        }
        return converted;
    }

    /**
     * Attempts to return value as Boolean, using best effort converting if value is not of type Boolean.
     *
     * @throws InconvertibleValueException if value is not convertible to Boolean.
     */
    public Boolean asBoolean()
        throws InconvertibleValueException
    {
        final Boolean converted = JavaTypeConverters.BOOLEAN.convertFrom( object );
        if ( converted == null )
        {
            throw new InconvertibleValueException( object, JavaTypeConverters.BOOLEAN );
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
        final Double converted;

        converted = JavaTypeConverters.DOUBLE.convertFrom( object );

        if ( converted == null )
        {
            throw new InconvertibleValueException( object, JavaTypeConverters.DOUBLE );
        }

        return converted;
    }

    /**
     * Attempts to return value as org.joda.time.DateMidnight, using best effort converting if value is not of type org.joda.time.DateMidnight.
     *
     * @throws InconvertibleValueException if value is not convertible to org.joda.time.DateMidnight.
     */
    public DateMidnight asDateMidnight()
        throws InconvertibleValueException
    {
        final DateMidnight converted = JavaTypeConverters.DATE_MIDNIGHT.convertFrom( object );
        if ( converted == null )
        {
            throw new InconvertibleValueException( object, JavaTypeConverters.DATE_MIDNIGHT );
        }
        return converted;
    }

    /**
     * Attempts to return value as org.joda.time.DateTime, using best effort converting if value is not of type org.joda.time.DateTime.
     *
     * @throws InconvertibleValueException if value is not convertible to org.joda.time.DateTime.
     */
    public DateTime asDateTime()
        throws InconvertibleValueException
    {
        final DateTime converted = JavaTypeConverters.DATE_TIME.convertFrom( object );
        if ( converted == null )
        {
            throw new InconvertibleValueException( object, JavaTypeConverters.DATE_TIME );
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
        if ( !( o instanceof Value ) )
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

    public static Value newDateTime( final DateTime value )
    {
        return new Value( ValueTypes.DATE_TIME, value );
    }

    public static Value newDateTime( final DateMidnight value )
    {
        return newDateTime( value.toDateTime() );
    }

    public static Value newDateTime( final String value )
    {
        return newDateTime( JavaTypeConverters.DATE_TIME.convertFromString( value ) );
    }

    public static Value newDateMidnight( final DateMidnight value )
    {
        return new Value( ValueTypes.DATE_MIDNIGHT, value );
    }

    public static Value newDateMidnight( final DateTime value )
    {
        return newDateMidnight( value.toDateMidnight() );
    }

    public static Value newDateMidnight( final String value )
    {
        return newDateMidnight( JavaTypeConverters.DATE_MIDNIGHT.convertFromString( value ) );
    }

    public static Value newLong( final String value )
    {
        return newLong( Long.parseLong( value ) );
    }

    public static Value newLong( final Number value )
    {
        return new Value( ValueTypes.LONG, value.longValue() );
    }

    public static Value newBoolean( final String value )
    {
        return newBoolean( Boolean.parseBoolean( value ) );
    }

    public static Value newBoolean( final Boolean value )
    {
        return new Value( ValueTypes.BOOLEAN, value );
    }

    public static Value newDouble( final String value )
    {
        return newDouble( Double.parseDouble( value ) );
    }

    public static Value newDouble( final Number value )
    {
        return new Value( ValueTypes.DOUBLE, value.doubleValue() );
    }

    public static Value newString( final String value )
    {
        return new Value( ValueTypes.STRING, value );
    }

    public static Value newXml( final String value )
    {
        return new Value( ValueTypes.XML, value );
    }

    public static Value newHtmlPart( final String value )
    {
        return new Value( ValueTypes.HTML_PART, value );
    }

    public static Value newGeoPoint( final String value )
    {
        return new Value( ValueTypes.GEO_POINT, value );
    }

    public static Value newContentId( final ContentId value )
    {
        return new Value( ValueTypes.CONTENT_ID, value );
    }

    public static Value newContentId( final String value )
    {
        return newContentId( ContentId.from( value ) );
    }

    public static Value newEntityId( final EntityId value )
    {
        return new Value( ValueTypes.ENTITY_ID, value );
    }

    public static Value newEntityId( final String value )
    {
        return newEntityId( EntityId.from( value ) );
    }

    public static Value newData( final String value )
    {
        return newData( JavaTypeConverters.DATA.convertFromString( value ) );
    }

    public static Value newData( final RootDataSet value )
    {
        return new Value( ValueTypes.DATA, value );
    }
}
