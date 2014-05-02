package com.enonic.wem.api.data;

import java.util.Objects;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.entity.EntityId;

/**
 * A generic holder for the value of a Property.
 */
public final class Value
{
    private final ValueType type;

    private final Object object;

    private Value( final ValueType type, final Object value )
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
    public DateMidnight getDate()
        throws ClassCastException
    {
        return (DateMidnight) object;
    }

    /**
     * Attempts to return value as com.enonic.wem.api.content.ContentId using casting.
     *
     * @throws ClassCastException if value is not of type com.enonic.wem.api.content.ContentId.
     */
    public ContentId getContentId()
        throws ClassCastException
    {
        return (ContentId) object;
    }

    /**
     * Attempts to return value as com.enonic.wem.api.data.RootDataSet using casting.
     *
     * @throws ClassCastException if value is not of type com.enonic.wem.api.data.RootDataSet.
     */
    public RootDataSet getData()
        throws ClassCastException
    {
        return (RootDataSet) object;
    }

    /**
     * Attempts to return value as com.enonic.wem.api.entity.EntityId using casting.
     *
     * @throws ClassCastException if value is not of type com.enonic.wem.api.entity.EntityId.
     */
    public EntityId getEntityId()
        throws ClassCastException
    {
        return (EntityId) object;
    }

    /**
     * Attempts to return value as String, using best effort converting if value is not of type String.
     */
    public String asString()
    {
        return ValueTypes.STRING.convert( object );
    }

    /**
     * Attempts to return value as com.enonic.wem.api.content.ContentId, using best effort converting if value is not of type com.enonic.wem.api.content.ContentId.
     */
    public ContentId asContentId()
    {
        return ValueTypes.CONTENT_ID.convert( object );
    }

    /**
     * Attempts to return value as com.enonic.wem.api.entity.EntityId, using best effort converting if value is not of type com.enonic.wem.api.entity.EntityId.
     */
    public EntityId asEntityId()
    {
        return ValueTypes.ENTITY_ID.convert( object );
    }

    /**
     * Attempts to return value as Long, using best effort converting if value is not of type Long.
     */
    public Long asLong()
    {
        return ValueTypes.LONG.convert( object );
    }

    /**
     * Attempts to return value as Boolean, using best effort converting if value is not of type Boolean.
     */
    public Boolean asBoolean()
    {
        return ValueTypes.BOOLEAN.convert( object );
    }

    /**
     * Attempts to return value as Double, using best effort converting if value is not of type Double.
     */
    public Double asDouble()
    {
        return ValueTypes.DOUBLE.convert( object );
    }

    /**
     * Attempts to return value as org.joda.time.DateMidnight, using best effort converting if value is not of type org.joda.time.DateMidnight.
     */
    public DateMidnight asDateMidnight()
    {
        return ValueTypes.DATE_MIDNIGHT.convert( object );
    }

    /**
     * Attempts to return value as org.joda.time.DateTime, using best effort converting if value is not of type org.joda.time.DateTime.
     */
    public DateTime asDateTime()
    {
        return ValueTypes.DATE_TIME.convert( object );
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
        return newDateTime( ValueTypes.DATE_TIME.convert( value ) );
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
        return newDateMidnight( ValueTypes.DATE_MIDNIGHT.convert( value ) );
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
        return newData( ValueTypes.DATA.convert( value ) );
    }

    public static Value newData( final RootDataSet value )
    {
        return new Value( ValueTypes.DATA, value );
    }
}
