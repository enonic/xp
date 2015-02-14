package com.enonic.xp.data;

import java.util.Objects;

public abstract class ValueType<T>
{
    private final java.lang.String name;

    private final Class<T> classType;

    private final JavaTypeConverter<T> converter;

    ValueType( final java.lang.String name, final JavaTypeConverter<T> converter )
    {
        this.name = name;
        this.classType = converter.getType();
        this.converter = converter;
    }

    public final java.lang.String getName()
    {
        return this.name;
    }

    public final Class<T> getJavaType()
    {
        return this.classType;
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

        return Objects.equals( name, that.name );
    }

    @Override
    public final int hashCode()
    {
        return Objects.hash( name );
    }

    @Override
    public final java.lang.String toString()
    {
        return name;
    }

    public final T convert( final Object object )
    {
        try
        {
            final T value = this.converter.convertFrom( object );
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

    T convertNullSafe( final Object object )
    {
        if ( object == null )
        {
            return null;
        }
        return convert( object );
    }

    private ValueTypeException convertError( final Object value, final java.lang.String reason )
    {
        final java.lang.String message = "Value of type [%s] cannot be converted to [%s]" + ( ( reason != null ) ? ": %s" : "" );
        throw new ValueTypeException( message, value.getClass().getName(), getName(), reason );
    }

    /**
     * Returns a new Value of this ValueType from object used in JSON.
     * See {@link Value#toJsonValue()}
     */
    abstract Value fromJsonValue( final Object object );

    static class PropertySet
        extends ValueType<com.enonic.xp.data.PropertySet>
    {
        PropertySet()
        {
            super( "PropertySet", JavaTypeConverters.DATA );
        }

        @Override
        Value fromJsonValue( final Object object )
        {
            return new Value.PropertySet( convertNullSafe( object ) );
        }
    }

    public static class String
        extends ValueType<java.lang.String>
    {
        String()
        {
            super( "String", JavaTypeConverters.STRING );
        }

        @Override
        Value fromJsonValue( final Object object )
        {
            return new Value.String( convertNullSafe( object ) );
        }
    }

    static class HtmlPart
        extends ValueType<java.lang.String>
    {
        HtmlPart()
        {
            super( "HtmlPart", JavaTypeConverters.STRING );
        }

        @Override
        Value fromJsonValue( final Object object )
        {
            return new Value.HtmlPart( convertNullSafe( object ) );
        }
    }

    static class Xml
        extends ValueType<java.lang.String>
    {
        Xml()
        {
            super( "Xml", JavaTypeConverters.STRING );
        }

        @Override
        Value fromJsonValue( final Object object )
        {
            return new Value.Xml( convertNullSafe( object ) );
        }
    }

    static class Long
        extends ValueType<java.lang.Long>
    {
        Long()
        {
            super( "Long", JavaTypeConverters.LONG );
        }

        @Override
        Value fromJsonValue( final Object object )
        {
            return new Value.Long( convertNullSafe( object ) );
        }
    }

    static class Double
        extends ValueType<java.lang.Double>
    {
        Double()
        {
            super( "Double", JavaTypeConverters.DOUBLE );
        }

        @Override
        Value fromJsonValue( final Object object )
        {
            return new Value.Double( convertNullSafe( object ) );
        }
    }

    static class Boolean
        extends ValueType<java.lang.Boolean>
    {
        Boolean()
        {
            super( "Boolean", JavaTypeConverters.BOOLEAN );
        }

        @Override
        Value fromJsonValue( final Object object )
        {
            return new Value.Boolean( convertNullSafe( object ) );
        }
    }

    static class LocalDate
        extends ValueType<java.time.LocalDate>
    {
        LocalDate()
        {
            super( "LocalDate", JavaTypeConverters.LOCAL_DATE );
        }

        @Override
        Value fromJsonValue( final Object object )
        {
            return new Value.LocalDate( convertNullSafe( object ) );
        }
    }

    static class LocalDateTime
        extends ValueType<java.time.LocalDateTime>
    {
        LocalDateTime()
        {
            super( "LocalDateTime", JavaTypeConverters.LOCAL_DATE_TIME );
        }

        @Override
        Value fromJsonValue( final Object object )
        {
            return new Value.LocalDateTime( convertNullSafe( object ) );
        }
    }

    static class LocalTime
        extends ValueType<java.time.LocalTime>
    {
        LocalTime()
        {
            super( "LocalTime", JavaTypeConverters.LOCAL_TIME );
        }

        @Override
        Value fromJsonValue( final Object object )
        {
            return new Value.LocalTime( convertNullSafe( object ) );
        }
    }

    static class DateTime
        extends ValueType<java.time.Instant>
    {
        DateTime()
        {
            super( "DateTime", JavaTypeConverters.DATE_TIME );
        }

        @Override
        Value fromJsonValue( final Object object )
        {
            return new Value.DateTime( convertNullSafe( object ) );
        }
    }

    static class GeoPoint
        extends ValueType<com.enonic.xp.util.GeoPoint>
    {
        GeoPoint()
        {
            super( "GeoPoint", JavaTypeConverters.GEO_POINT );
        }

        @Override
        Value fromJsonValue( final Object object )
        {
            return new Value.GeoPoint( convertNullSafe( object ) );
        }
    }

    static class BinaryReference
        extends ValueType<com.enonic.xp.util.BinaryReference>
    {
        BinaryReference()
        {
            super( "BinaryReference", JavaTypeConverters.BINARY_REFERENCE );
        }

        @Override
        Value fromJsonValue( final Object object )
        {
            return new Value.BinaryReference( convertNullSafe( object ) );
        }
    }

    static class Reference
        extends ValueType<com.enonic.xp.util.Reference>
    {
        Reference()
        {
            super( "Reference", JavaTypeConverters.REFERENCE );
        }

        @Override
        Value fromJsonValue( final Object object )
        {
            return new Value.Reference( convertNullSafe( object ) );
        }
    }

    static class Link
        extends ValueType<com.enonic.xp.util.Link>
    {
        Link()
        {
            super( "Link", JavaTypeConverters.LINK );
        }

        @Override
        Value fromJsonValue( final Object object )
        {
            return new Value.Link( convert( object ) );
        }
    }

}
