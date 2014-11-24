package com.enonic.wem.api.data2;

import java.util.Objects;

public abstract class ValueType<T>
{
    private final java.lang.String name;

    private final Class<T> classType;

    private final JavaTypeConverter<T> converter;

    public ValueType( final java.lang.String name, final JavaTypeConverter<T> converter )
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

    private ValueTypeException convertError( final Object value, final java.lang.String reason )
    {
        final java.lang.String message = "Value of type [%s] cannot be converted to [%s]" + ( ( reason != null ) ? ": %s" : "" );
        throw new ValueTypeException( message, value.getClass().getName(), getName(), reason );
    }

    /**
     * Returns a new Value of this ValueType from object used in JSON.
     * See {@link com.enonic.wem.api.data2.Value#toJsonValue()}
     */
    abstract Value fromJsonValue( final Object object );

    static class PropertySet
        extends ValueType<com.enonic.wem.api.data2.PropertySet>
    {
        PropertySet()
        {
            super( "PropertySet", JavaTypeConverters.DATA );
        }

        @Override
        Value fromJsonValue( final Object object )
        {
            return new Value.PropertySet( convert( object ) );
        }
    }

    static class String
        extends ValueType<java.lang.String>
    {
        String()
        {
            super( "String", JavaTypeConverters.STRING );
        }

        @Override
        Value fromJsonValue( final Object object )
        {
            return new Value.String( convert( object ) );
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
            return new Value.HtmlPart( convert( object ) );
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
            return new Value.Xml( convert( object ) );
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
            return new Value.Long( convert( object ) );
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
            return new Value.Double( convert( object ) );
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
            return new Value.Boolean( convert( object ) );
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
            return new Value.LocalDate( convert( object ) );
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
            return new Value.LocalDateTime( convert( object ) );
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
            return new Value.LocalTime( convert( object ) );
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
            return new Value.DateTime( convert( object ) );
        }
    }

    static class GeoPoint
        extends ValueType<com.enonic.wem.api.util.GeoPoint>
    {
        GeoPoint()
        {
            super( "GeoPoint", JavaTypeConverters.GEO_POINT );
        }

        @Override
        Value fromJsonValue( final Object object )
        {
            return new Value.GeoPoint( convert( object ) );
        }
    }

    static class Reference
        extends ValueType<com.enonic.wem.api.util.Reference>
    {
        Reference()
        {
            super( "Reference", JavaTypeConverters.REFERENCE );
        }

        @Override
        Value fromJsonValue( final Object object )
        {
            return new Value.Reference( convert( object ) );
        }
    }

}
