package com.enonic.wem.api.converter;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.enonic.wem.api.util.GeoPoint;

public final class DefaultConverters
    extends Converters
{
    public DefaultConverters()
    {
        // Register object -> * converters
        register( Object.class, String.class, this::objectToString );

        // Register string -> * converters
        register( String.class, Boolean.class, this::stringToBoolean );
        register( String.class, Double.class, this::stringToDouble );
        register( String.class, Long.class, this::stringToLong );
        register( String.class, LocalDate.class, this::stringToLocalDate );
        register( String.class, DateTime.class, this::stringToDateTime );
        register( String.class, GeoPoint.class, this::stringToGeoPoint );

        // Register number -> * converters
        register( Number.class, Long.class, this::numberToLong );
        register( Number.class, Double.class, this::numberToDouble );
    }

    public String objectToString( final Object from )
    {
        return from.toString();
    }

    public Boolean stringToBoolean( final String from )
    {
        return Boolean.parseBoolean( from );
    }

    public double stringToDouble( final String from )
    {
        return Double.parseDouble( from );
    }

    public long stringToLong( final String from )
    {
        return Long.parseLong( from );
    }

    public LocalDate stringToLocalDate( final String from )
    {
        return LocalDate.parse( from );
    }

    public DateTime stringToDateTime( final String from )
    {
        return DateTime.parse( from );
    }

    public GeoPoint stringToGeoPoint( final String from )
    {
        return GeoPoint.from( from );
    }

    public long numberToLong( final Number from )
    {
        return from.longValue();
    }

    public double numberToDouble( final Number from )
    {
        return from.doubleValue();
    }
}
