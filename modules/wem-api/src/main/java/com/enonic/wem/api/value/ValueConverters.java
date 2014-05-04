package com.enonic.wem.api.value;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.util.GeoPoint;

final class ValueConverters
{
    public static String objectToString( final Object from )
    {
        return from.toString();
    }

    public static Boolean stringToBoolean( final String from )
    {
        return Boolean.parseBoolean( from );
    }

    public static double stringToDouble( final String from )
    {
        return Double.parseDouble( from );
    }

    public static long stringToLong( final String from )
    {
        return Long.parseLong( from );
    }

    public static EntityId stringToEntityId( final String from )
    {
        return EntityId.from( from );
    }

    public static ContentId stringToContentId( final String from )
    {
        return ContentId.from( from );
    }

    public static GeoPoint stringToGeoPoint( final String from )
    {
        return GeoPoint.from( from );
    }

    public static LocalDate stringToLocalDate( final String from )
    {
        return LocalDate.parse( from );
    }

    public static DateTime stringToDateTime( final String from )
    {
        return DateTime.parse( from );
    }

    public static long numberToLong( final Number from )
    {
        return from.longValue();
    }

    public static double numberToDouble( final Number from )
    {
        return from.doubleValue();
    }
}
