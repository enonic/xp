package com.enonic.wem.api.data2;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.util.GeoPoint;

public final class ValueTypes
{
    public static final ValueType<PropertySet> PROPERTY_SET = newData();

    public static final ValueType<String> STRING = newString();

    public static final ValueType<String> HTML_PART = newHtmlPart();

    public static final ValueType<String> XML = newXml();

    public static final ValueType<LocalDate> LOCAL_DATE = newLocalDate();

    public static final ValueType<LocalDateTime> LOCAL_DATE_TIME = newLocalDateTime();

    public static final ValueType<LocalTime> LOCAL_TIME = newLocalTime();

    public static final ValueType<Instant> DATE_TIME = newDateTime();

    public static final ValueType<ContentId> CONTENT_ID = newContentId();

    public static final ValueType<Long> LONG = newLong();

    public static final ValueType<Double> DOUBLE = newDouble();

    public static final ValueType<GeoPoint> GEO_POINT = newGeoPoint();

    public static final ValueType<Boolean> BOOLEAN = newBoolean();

    private static final Map<Integer, ValueType> typesByKey = new HashMap<>();

    private static final Map<String, ValueType> typesByName = new HashMap<>();

    static
    {
        register( PROPERTY_SET );
        register( STRING );
        register( HTML_PART );
        register( XML );
        register( LOCAL_DATE );
        register( LOCAL_TIME );
        register( LOCAL_DATE_TIME );
        register( DATE_TIME );
        register( CONTENT_ID );
        register( LONG );
        register( DOUBLE );
        register( GEO_POINT );
        register( BOOLEAN );
    }

    private static void register( ValueType valueType )
    {
        Object previous = typesByKey.put( valueType.getKey(), valueType );
        Preconditions.checkState( previous == null, "ValueType already registered: " + valueType.getKey() );

        previous = typesByName.put( valueType.getName(), valueType );
        Preconditions.checkState( previous == null, "ValueType already registered: " + valueType.getName() );
    }

    public static ValueType getByKey( final int key )
    {
        return typesByKey.get( key );
    }

    public static ValueType getByName( final String name )
    {
        return typesByName.get( name );
    }

    private static ValueType<String> newString()
    {
        return new ValueType.String( 1 );
    }

    private static ValueType<PropertySet> newData()
    {
        return new ValueType.PropertySet( 0 );
    }

    private static ValueType<String> newHtmlPart()
    {
        return new ValueType.HtmlPart( 3 );
    }

    private static ValueType<String> newXml()
    {
        return new ValueType.Xml( 4 );
    }

    private static ValueType<LocalDate> newLocalDate()
    {
        return new ValueType.LocalDate( 5 );
    }

    private static ValueType<Instant> newDateTime()
    {
        return new ValueType.DateTime( 6 );
    }

    private static ValueType<ContentId> newContentId()
    {

        return new ValueType.ContentId( 7 );
    }

    private static ValueType<Long> newLong()
    {
        return new ValueType.Long( 8 );
    }

    private static ValueType<Double> newDouble()
    {
        return new ValueType.Double( 9 );
    }


    private static ValueType<GeoPoint> newGeoPoint()
    {
        return new ValueType.GeoPoint( 10 );
    }

    private static ValueType<Boolean> newBoolean()
    {
        return new ValueType.Boolean( 12 );
    }

    private static ValueType<LocalDateTime> newLocalDateTime()
    {
        return new ValueType.LocalDateTime( 13 );
    }

    private static ValueType<LocalTime> newLocalTime()
    {
        return new ValueType.LocalTime( 14 );
    }
}
