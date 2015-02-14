package com.enonic.xp.core.data;

import java.util.Objects;

import com.google.common.base.Preconditions;

/**
 * A generic holder for the value of a Property.
 */
public abstract class Value
{
    private final ValueType type;

    private final Object object;

    Value( final ValueType type, final Object value )
    {
        Preconditions.checkNotNull( type, "type cannot be null" );
        if ( value != null )
        {
            Preconditions.checkArgument( !( value instanceof Value ), "The value of a Value cannot be: " + value.getClass() );
            Preconditions.checkArgument( type.getJavaType().isInstance( value ),
                                         "value is of wrong class, expected [" + type.getJavaType().getName() + "], got: " +
                                             value.getClass().getName() );
        }

        this.type = type;
        this.object = value;
    }

    Value( final Value value )
    {
        this.type = value.type;
        this.object = value.getObject();
    }


    public boolean isSet()
    {
        return this.type == ValueTypes.PROPERTY_SET;
    }

    public boolean isString()
    {
        return this.type == ValueTypes.STRING;
    }

    public boolean isDateType()
    {
        return ( this.type == ValueTypes.DATE_TIME ) || ( this.type == ValueTypes.LOCAL_DATE );
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
     * Returns a JSON compatible value. Default is object. Override if needed.
     */
    Object toJsonValue()
    {
        return object;
    }

    /**
     * Attempts to return value as RootDataSet, using best effort converting if value is not of type RootDataSet.
     */
    public com.enonic.xp.core.data.PropertySet asData()
        throws ClassCastException
    {
        if ( object == null )
        {
            return null;
        }
        return ValueTypes.PROPERTY_SET.convert( object );
    }

    /**
     * Attempts to return value as String, using best effort converting if value is not of type String.
     */
    public java.lang.String asString()
    {
        if ( object == null )
        {
            return null;
        }
        return ValueTypes.STRING.convert( object );
    }

    /**
     * Attempts to return value as Long, using best effort converting if value is not of type Long.
     */
    public java.lang.Long asLong()
    {
        if ( object == null || "".equals( object ) )
        {
            return null;
        }
        return ValueTypes.LONG.convert( object );
    }

    /**
     * Attempts to return value as Boolean, using best effort converting if value is not of type Boolean.
     */
    public java.lang.Boolean asBoolean()
    {
        if ( object == null )
        {
            return null;
        }
        return ValueTypes.BOOLEAN.convert( object );
    }

    /**
     * Attempts to return value as Double, using best effort converting if value is not of type Double.
     */
    public java.lang.Double asDouble()
    {
        if ( object == null || "".equals( object ) )
        {
            return null;
        }
        return ValueTypes.DOUBLE.convert( object );
    }

    /**
     * Attempts to return value as LocalDate, using best effort converting if value is not of type LocalDate.
     */
    public java.time.LocalDate asLocalDate()
    {
        if ( object == null )
        {
            return null;
        }
        return ValueTypes.LOCAL_DATE.convert( object );
    }

    public java.time.LocalTime asLocalTime()
    {
        if ( object == null )
        {
            return null;
        }
        return ValueTypes.LOCAL_TIME.convert( object );
    }

    public java.time.LocalDateTime asLocalDateTime()
    {
        if ( object == null )
        {
            return null;
        }
        return ValueTypes.LOCAL_DATE_TIME.convert( object );
    }

    /**
     * Attempts to return value as java.time.Instant, using best effort converting if value is not of type java.time.Instant.
     */
    public java.time.Instant asInstant()
    {
        if ( object == null )
        {
            return null;
        }
        return ValueTypes.DATE_TIME.convert( object );
    }

    public com.enonic.xp.core.util.GeoPoint asGeoPoint()
    {
        if ( object == null )
        {
            return null;
        }
        return ValueTypes.GEO_POINT.convert( object );
    }

    /**
     * Attempts to return value as Reference, using best effort converting if value is not of type Reference.
     */
    public com.enonic.xp.core.util.Reference asReference()
    {
        if ( object == null )
        {
            return null;
        }
        return ValueTypes.REFERENCE.convert( object );
    }


    /**
     * Attempts to return value as Reference, using best effort converting if value is not of type Reference.
     */
    public com.enonic.xp.core.util.BinaryReference asBinaryReference()
    {
        if ( object == null )
        {
            return null;
        }
        return ValueTypes.BINARY_REFERENCE.convert( object );
    }


    /**
     * Attempts to return value as Reference, using best effort converting if value is not of type Reference.
     */
    public com.enonic.xp.core.util.Link asLink()
    {
        if ( object == null )
        {
            return null;
        }
        return ValueTypes.LINK.convert( object );
    }


    /**
     * Ensures a copy is done of this value. Objects could be reused if they are of immutable classes.
     *
     * @param tree the PropertyTree that the value will be attached to. Needed for values of type PropertySet.
     */
    abstract Value copy( final PropertyTree tree );

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
    public java.lang.String toString()
    {
        return asString();
    }

    public static Value newInstant( final java.time.Instant value )
    {
        return new DateTime( value );
    }

    public static Value newLocalTime( final java.time.LocalTime value )
    {
        return new LocalTime( value );
    }

    public static Value newLocalDateTime( final java.time.LocalDateTime value )
    {
        return new LocalDateTime( value );
    }

    public static Value newLocalDate( final java.time.LocalDate value )
    {
        return new LocalDate( value );
    }

    public static Value newLong( final java.lang.Long value )
    {
        return new Long( value );
    }

    public static Value newBoolean( final java.lang.Boolean value )
    {
        return new Boolean( value );
    }

    public static Value newDouble( final java.lang.Double value )
    {
        return new Double( value );
    }

    public static Value newString( final java.lang.String value )
    {
        return new String( value );
    }

    public static Value newXml( final java.lang.String value )
    {
        return new Xml( value );
    }

    public static Value newHtmlPart( final java.lang.String value )
    {
        return new HtmlPart( value );
    }

    public static Value newGeoPoint( final com.enonic.xp.core.util.GeoPoint value )
    {
        return new GeoPoint( value );
    }

    public static Value newReference( final com.enonic.xp.core.util.Reference value )
    {
        return new Reference( value );
    }

    public static Value newBinary( final com.enonic.xp.core.util.BinaryReference value )
    {
        return new BinaryReference( value );
    }

    public static Value newLink( final com.enonic.xp.core.util.Link value )
    {
        return new Link( value );
    }

    public static Value newData( final com.enonic.xp.core.data.PropertySet value )
    {
        return new PropertySet( value );
    }

    public boolean isNull()
    {
        return this.object == null;
    }

    public boolean isPropertySet()
    {
        return type.equals( ValueTypes.PROPERTY_SET );
    }


    static class PropertySet
        extends Value
    {
        PropertySet( final com.enonic.xp.core.data.PropertySet value )
        {
            super( ValueTypes.PROPERTY_SET, value );
        }

        PropertySet( final PropertySet source, final PropertyTree tree )
        {
            super( ValueTypes.PROPERTY_SET, !source.isNull() ? source.asData().copy( tree ) : null );
        }

        @Override
        Value copy( final PropertyTree tree )
        {
            return new PropertySet( this, tree );
        }

        @Override
        public java.lang.String toString()
        {
            throw new UnsupportedOperationException( "Value of type PropertySet does not support invocation of toString()" );
        }
    }

    static class String
        extends Value
    {
        String( final java.lang.String value )
        {
            super( ValueTypes.STRING, value );
        }

        String( final String source )
        {
            super( ValueTypes.STRING, source.getObject() );
        }

        @Override
        Value copy( final PropertyTree tree )
        {
            return new String( this );
        }
    }

    static class HtmlPart
        extends Value
    {
        HtmlPart( final java.lang.String value )
        {
            super( ValueTypes.HTML_PART, value );
        }

        HtmlPart( final HtmlPart source )
        {
            super( ValueTypes.HTML_PART, source.getObject() );
        }

        @Override
        Value copy( final PropertyTree tree )
        {
            return new HtmlPart( this );
        }
    }

    static class Xml
        extends Value
    {
        Xml( final java.lang.String value )
        {
            super( ValueTypes.XML, value );
        }

        Xml( final Xml source )
        {
            super( ValueTypes.XML, source.getObject() );
        }

        @Override
        Value copy( final PropertyTree tree )
        {
            return new Xml( this );
        }
    }

    static class Long
        extends Value
    {
        Long( final java.lang.Long value )
        {
            super( ValueTypes.LONG, value );
        }

        Long( final Long source )
        {
            super( ValueTypes.LONG, source.getObject() );
        }

        @Override
        Value copy( final PropertyTree tree )
        {
            return new Long( this );
        }
    }

    static class Double
        extends Value
    {
        Double( final java.lang.Double value )
        {
            super( ValueTypes.DOUBLE, value );
        }

        Double( final Double source )
        {
            super( ValueTypes.DOUBLE, source.getObject() );
        }

        @Override
        Value copy( final PropertyTree tree )
        {
            return new Double( this );
        }
    }

    static class Boolean
        extends Value
    {
        Boolean( final java.lang.Boolean value )
        {
            super( ValueTypes.BOOLEAN, value );
        }

        Boolean( final Boolean source )
        {
            super( ValueTypes.BOOLEAN, source.getObject() );
        }

        @Override
        Value copy( final PropertyTree tree )
        {
            return new Boolean( this );
        }
    }

    static class GeoPoint
        extends Value
    {
        GeoPoint( final com.enonic.xp.core.util.GeoPoint value )
        {
            super( ValueTypes.GEO_POINT, value );
        }

        GeoPoint( final GeoPoint source )
        {
            super( ValueTypes.GEO_POINT, source.getObject() );
        }

        @Override
        Value copy( final PropertyTree tree )
        {
            return new GeoPoint( this );
        }

        @Override
        Object toJsonValue()
        {
            return asString();
        }
    }

    static class Reference
        extends Value
    {
        Reference( final com.enonic.xp.core.util.Reference value )
        {
            super( ValueTypes.REFERENCE, value );
        }

        Reference( final Reference source )
        {
            super( ValueTypes.REFERENCE, source.getObject() );
        }

        @Override
        Value copy( final PropertyTree tree )
        {
            return new Reference( this );
        }

        @Override
        Object toJsonValue()
        {
            return asString();
        }
    }

    static class BinaryReference
        extends Value
    {
        BinaryReference( final com.enonic.xp.core.util.BinaryReference value )
        {
            super( ValueTypes.BINARY_REFERENCE, value );
        }

        BinaryReference( final BinaryReference source )
        {
            super( ValueTypes.BINARY_REFERENCE, source.getObject() );
        }

        @Override
        Value copy( final PropertyTree tree )
        {
            return new BinaryReference( this );
        }

        @Override
        Object toJsonValue()
        {
            return asString();
        }
    }

    static class Link
        extends Value
    {
        Link( final com.enonic.xp.core.util.Link value )
        {
            super( ValueTypes.LINK, value );
        }

        Link( final Link source )
        {
            super( ValueTypes.LINK, source.getObject() );
        }

        @Override
        Value copy( final PropertyTree tree )
        {
            return new Link( this );
        }

        @Override
        Object toJsonValue()
        {
            return asString();
        }
    }

    static class LocalDate
        extends Value
    {
        LocalDate( final java.time.LocalDate value )
        {
            super( ValueTypes.LOCAL_DATE, value );
        }

        LocalDate( final LocalDate source )
        {
            super( ValueTypes.LOCAL_DATE, source.getObject() );
        }

        @Override
        Value copy( final PropertyTree tree )
        {
            return new LocalDate( this );
        }

        @Override
        Object toJsonValue()
        {
            return asString();
        }
    }

    static class LocalDateTime
        extends Value
    {
        LocalDateTime( final java.time.LocalDateTime value )
        {
            super( ValueTypes.LOCAL_DATE_TIME, value );
        }

        LocalDateTime( final LocalDateTime source )
        {
            super( ValueTypes.LOCAL_DATE_TIME, source.getObject() );
        }

        @Override
        Value copy( final PropertyTree tree )
        {
            return new LocalDateTime( this );
        }

        @Override
        Object toJsonValue()
        {
            return asString();
        }
    }

    static class LocalTime
        extends Value
    {
        LocalTime( final java.time.LocalTime value )
        {
            super( ValueTypes.LOCAL_TIME, value );
        }

        LocalTime( final LocalTime source )
        {
            super( ValueTypes.LOCAL_TIME, source.getObject() );
        }

        @Override
        Value copy( final PropertyTree tree )
        {
            return new LocalTime( this );
        }

        @Override
        Object toJsonValue()
        {
            return asString();
        }
    }

    static class DateTime
        extends Value
    {
        DateTime( final java.time.Instant value )
        {
            super( ValueTypes.DATE_TIME, value );
        }

        DateTime( final DateTime source )
        {
            super( ValueTypes.DATE_TIME, source.getObject() );
        }

        @Override
        Value copy( final PropertyTree tree )
        {
            return new DateTime( this );
        }

        @Override
        Object toJsonValue()
        {
            return asString();
        }
    }
}
