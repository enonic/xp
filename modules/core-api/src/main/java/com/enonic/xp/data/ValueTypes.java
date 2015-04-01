package com.enonic.xp.data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Link;
import com.enonic.xp.util.Reference;

public final class ValueTypes
{
    public static final ValueType<PropertySet> PROPERTY_SET = new ValueType.PropertySet();

    public static final ValueType<String> STRING = new ValueType.String();

    public static final ValueType<String> HTML_PART = new ValueType.HtmlPart();

    public static final ValueType<String> XML = new ValueType.Xml();

    public static final ValueType<LocalDate> LOCAL_DATE = new ValueType.LocalDate();

    public static final ValueType<LocalDateTime> LOCAL_DATE_TIME = new ValueType.LocalDateTime();

    public static final ValueType<LocalTime> LOCAL_TIME = new ValueType.LocalTime();

    public static final ValueType<ZonedDateTime> DATE_TIME = new ValueType.DateTime();

    public static final ValueType<Instant> INSTANT = new ValueType.Instant();

    public static final ValueType<Long> LONG = new ValueType.Long();

    public static final ValueType<Double> DOUBLE = new ValueType.Double();

    public static final ValueType<GeoPoint> GEO_POINT = new ValueType.GeoPoint();

    public static final ValueType<Reference> REFERENCE = new ValueType.Reference();

    public static final ValueType<Link> LINK = new ValueType.Link();

    public static final ValueType<Boolean> BOOLEAN = new ValueType.Boolean();

    public static final ValueType<BinaryReference> BINARY_REFERENCE = new ValueType.BinaryReference();

    private static final Map<String, ValueType> TYPES_BY_NAME = new HashMap<>();

    static
    {
        register( PROPERTY_SET );
        register( STRING );
        register( HTML_PART );
        register( XML );
        register( LOCAL_DATE );
        register( LOCAL_TIME );
        register( LOCAL_DATE_TIME );
        register( INSTANT );
        register( DATE_TIME );
        register( LONG );
        register( DOUBLE );
        register( GEO_POINT );
        register( BOOLEAN );
        register( REFERENCE );
        register( LINK );
        register( BINARY_REFERENCE );
    }

    private static void register( ValueType valueType )
    {
        Object previous = TYPES_BY_NAME.put( valueType.getName(), valueType );
        Preconditions.checkState( previous == null, "ValueType already registered: " + valueType.getName() );
    }

    public static ValueType getByName( final String name )
    {
        return TYPES_BY_NAME.get( name );
    }

}
