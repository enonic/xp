package com.enonic.xp.data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Link;
import com.enonic.xp.util.Reference;

@PublicApi
public final class ValueTypes
{
    public static final ValueType<PropertySet> PROPERTY_SET = new PropertySetValueType();

    public static final ValueType<String> STRING = new StringValueType();

    public static final ValueType<String> XML = new XmlValueType();

    public static final ValueType<LocalDate> LOCAL_DATE = new LocalDateValueType();

    public static final ValueType<LocalDateTime> LOCAL_DATE_TIME = new LocalDateTimeValueType();

    public static final ValueType<LocalTime> LOCAL_TIME = new LocalTimeValueType();

    public static final ValueType<Instant> DATE_TIME = new DateTimeValueType();

    public static final ValueType<Long> LONG = new LongValueType();

    public static final ValueType<Double> DOUBLE = new DoubleValueType();

    public static final ValueType<GeoPoint> GEO_POINT = new GeoPointValueType();

    public static final ValueType<Reference> REFERENCE = new ReferenceValueType();

    public static final ValueType<Link> LINK = new LinkValueType();

    public static final ValueType<Boolean> BOOLEAN = new BooleanValueType();

    public static final ValueType<BinaryReference> BINARY_REFERENCE = new BinaryReferenceValueType();

    private static final Map<String, ValueType> TYPES_BY_NAME = new HashMap<>();

    static
    {
        register( PROPERTY_SET );
        register( STRING );
        register( XML );
        register( LOCAL_DATE );
        register( LOCAL_TIME );
        register( LOCAL_DATE_TIME );
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
