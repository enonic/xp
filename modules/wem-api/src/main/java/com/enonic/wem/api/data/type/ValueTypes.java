package com.enonic.wem.api.data.type;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.util.GeoPoint;

public final class ValueTypes
{
    public static final ValueType<RootDataSet> DATA = newData();

    public static final ValueType<String> STRING = newString();

    public static final ValueType<String> HTML_PART = newHtmlPart();

    public static final ValueType<String> XML = newXml();

    public static final ValueType<LocalDate> LOCAL_DATE = newLocalDate();

    public static final ValueType<Instant> DATE_TIME = newDateTime();

    public static final ValueType<ContentId> CONTENT_ID = newContentId();

    public static final ValueType<Long> LONG = newLong();

    public static final ValueType<Double> DOUBLE = newDouble();

    public static final ValueType<GeoPoint> GEO_POINT = newGeoPoint();

    public static final ValueType<EntityId> ENTITY_ID = newEntityId();

    public static final ValueType<Boolean> BOOLEAN = newBoolean();

    private static final Map<Integer, ValueType> typesByKey = new HashMap<>();

    private static final Map<String, ValueType> typesByName = new HashMap<>();

    static
    {
        register( DATA );
        register( STRING );
        register( HTML_PART );
        register( XML );
        register( LOCAL_DATE );
        register( DATE_TIME );
        register( CONTENT_ID );
        register( LONG );
        register( DOUBLE );
        register( GEO_POINT );
        register( ENTITY_ID );
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

    private static ValueType<Boolean> newBoolean()
    {
        return new ValueTypeImpl<>( 12, "Boolean", JavaTypeConverters.BOOLEAN );
    }

    private static ValueType<String> newString()
    {
        return new ValueTypeImpl<>( 1, "String", JavaTypeConverters.STRING );
    }

    private static ValueType<RootDataSet> newData()
    {
        return new ValueTypeImpl<>( 0, "Data", JavaTypeConverters.DATA );
    }

    private static ValueType<String> newHtmlPart()
    {
        return new ValueTypeImpl<>( 3, "HtmlPart", JavaTypeConverters.STRING );
    }

    private static ValueType<Double> newDouble()
    {
        return new ValueTypeImpl<>( 9, "Double", JavaTypeConverters.DOUBLE );
    }

    private static ValueType<Long> newLong()
    {
        return new ValueTypeImpl<>( 8, "Long", JavaTypeConverters.LONG );
    }

    private static ValueType<String> newXml()
    {
        return new ValueTypeImpl<>( 4, "Xml", JavaTypeConverters.STRING );
    }

    private static ValueType<LocalDate> newLocalDate()
    {
        return new ValueTypeImpl<>( 5, "LocalDate", JavaTypeConverters.LOCAL_DATE );
    }

    private static ValueType<Instant> newDateTime()
    {
        return new ValueTypeImpl<>( 6, "DateTime", JavaTypeConverters.DATE_TIME );
    }

    private static ValueType<ContentId> newContentId()
    {
        return new ValueTypeImpl<>( 7, "ContentId", JavaTypeConverters.CONTENT_ID );
    }

    private static ValueType<EntityId> newEntityId()
    {
        return new ValueTypeImpl<>( 11, "EntityId", JavaTypeConverters.ENTITY_ID );
    }

    private static ValueType<GeoPoint> newGeoPoint()
    {
        return new ValueTypeImpl<>( 10, "GeoPoint", JavaTypeConverters.GEO_POINT );
    }
}
