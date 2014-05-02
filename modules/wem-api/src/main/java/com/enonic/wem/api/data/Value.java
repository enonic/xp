package com.enonic.wem.api.data;

import java.util.Objects;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.util.GeoPoint;

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
     * Attempts to return value as RootDataSet, using best effort converting if value is not of type RootDataSet.
     */
    public RootDataSet asData()
        throws ClassCastException
    {
        return ValueTypes.DATA.convert( object );
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

    public GeoPoint asGeoPoint()
    {
        return ValueTypes.GEO_POINT.convert( object );
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

    public static Value newValue( final ValueType type, final Object value )
    {
        return new Value( type, type.convert( value ) );
    }

    public static Value newDateTime( final Object value )
    {
        return newValue( ValueTypes.DATE_TIME, value );
    }

    public static Value newDateMidnight( final Object value )
    {
        return newValue( ValueTypes.DATE_MIDNIGHT, value );
    }

    public static Value newLong( final Object value )
    {
        return newValue( ValueTypes.LONG, value );
    }

    public static Value newBoolean( final Object value )
    {
        return newValue( ValueTypes.BOOLEAN, value );
    }

    public static Value newDouble( final Object value )
    {
        return newValue( ValueTypes.DOUBLE, value );
    }

    public static Value newString( final Object value )
    {
        return newValue( ValueTypes.STRING, value );
    }

    public static Value newXml( final Object value )
    {
        return newValue( ValueTypes.XML, value );
    }

    public static Value newHtmlPart( final Object value )
    {
        return newValue( ValueTypes.HTML_PART, value );
    }

    public static Value newGeoPoint( final Object value )
    {
        return newValue( ValueTypes.GEO_POINT, value );
    }

    public static Value newContentId( final Object value )
    {
        return newValue( ValueTypes.CONTENT_ID, value );
    }

    public static Value newEntityId( final Object value )
    {
        return newValue( ValueTypes.ENTITY_ID, value );
    }

    public static Value newData( final Object value )
    {
        return newValue( ValueTypes.DATA, value );
    }
}
