package com.enonic.wem.api.data.type;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.base.BaseDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.serializer.RootDataSetJsonSerializer;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.util.GeoPoint;

final class JavaTypeConverters
{
    private final static DateTimeFormatter DATE_MIDNIGHT_FORMATTER = new DateTimeFormatterBuilder().
        appendYear( 4, 4 ).appendLiteral( "-" ).appendMonthOfYear( 2 ).appendLiteral( "-" ).appendDayOfMonth( 2 ).toFormatter();

    private final static DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder().
        appendYear( 4, 4 ).appendLiteral( "-" ).appendMonthOfYear( 2 ).appendLiteral( "-" ).appendDayOfMonth( 2 ).
        appendLiteral( "T" ).appendHourOfDay( 2 ).appendLiteral( ":" ).appendMinuteOfHour( 2 ).appendLiteral( ":" ).appendSecondOfMinute(
        2 ).toFormatter();

    private final static RootDataSetJsonSerializer DATA_SERIALIZER = new RootDataSetJsonSerializer();

    public final static JavaTypeConverter<String> STRING = newString();

    public final static JavaTypeConverter<Long> LONG = newLong();

    public final static JavaTypeConverter<Double> DOUBLE = newDouble();

    public final static JavaTypeConverter<Boolean> BOOLEAN = newBoolean();

    public final static JavaTypeConverter<RootDataSet> DATA = newData();

    public final static JavaTypeConverter<ContentId> CONTENT_ID = newContentId();

    public final static JavaTypeConverter<EntityId> ENTITY_ID = newEntityId();

    public final static JavaTypeConverter<DateTime> DATE_TIME = newDateTime();

    public final static JavaTypeConverter<DateMidnight> DATE_MIDNIGHT = newDateMidnight();

    public final static JavaTypeConverter<GeoPoint> GEO_POINT = newGeoPoint();

    private static JavaTypeConverter<String> newString()
    {
        return new JavaTypeConverter<String>( String.class )
        {
            @Override
            public String convertFrom( final Object value )
            {
                if ( value instanceof String )
                {
                    return (String) value;
                }
                else if ( value instanceof DateTime )
                {
                    return DATE_TIME_FORMATTER.print( (DateTime) value );
                }
                else if ( value instanceof DateMidnight )
                {
                    return DATE_MIDNIGHT_FORMATTER.print( (DateMidnight) value );
                }
                else if ( value instanceof RootDataSet )
                {
                    return DATA_SERIALIZER.serializeToString( (RootDataSet) value );
                }
                else
                {
                    return value.toString();
                }
            }

            @Override
            public String convertFromString( final String value )
            {
                return value;
            }
        };
    }

    private static JavaTypeConverter<Long> newLong()
    {
        return new JavaTypeConverter<Long>( Long.class )
        {
            @Override
            public Long convertFrom( final Object value )
            {
                if ( value instanceof Number )
                {
                    return ( (Number) value ).longValue();
                }
                else if ( value instanceof String )
                {
                    return new Long( (String) value );
                }
                else
                {
                    return null;
                }
            }

            @Override
            public Long convertFromString( final String value )
            {
                return new Long( value );
            }
        };
    }

    private static JavaTypeConverter<Double> newDouble()
    {
        return new JavaTypeConverter<Double>( Double.class )
        {
            @Override
            public Double convertFrom( final Object value )
            {
                if ( value instanceof Number )
                {
                    return ( (Number) value ).doubleValue();
                }
                else if ( value instanceof String )
                {
                    return new Double( (String) value );
                }
                else
                {
                    return null;
                }
            }

            @Override
            public Double convertFromString( final String value )
            {
                return new Double( value );
            }
        };
    }

    private static JavaTypeConverter<Boolean> newBoolean()
    {
        return new JavaTypeConverter<Boolean>( Boolean.class )
        {
            @Override
            public Boolean convertFrom( final Object value )
            {
                if ( value instanceof Boolean )
                {
                    return (Boolean) value;
                }
                else if ( value instanceof String )
                {
                    return Boolean.parseBoolean( (String) value );
                }

                return null;
            }

            @Override
            public Boolean convertFromString( final String value )
            {
                return Boolean.parseBoolean( value );
            }
        };
    }

    private static JavaTypeConverter<RootDataSet> newData()
    {
        return new JavaTypeConverter<RootDataSet>( RootDataSet.class )
        {
            @Override
            public RootDataSet convertFrom( final Object value )
            {
                if ( value instanceof RootDataSet )
                {
                    return (RootDataSet) value;
                }
                else if ( value instanceof String )
                {
                    return DATA_SERIALIZER.parse( (String) value );
                }
                else
                {
                    return null;
                }
            }

            @Override
            public RootDataSet convertFromString( final String value )
            {
                return DATA_SERIALIZER.parse( value );
            }
        };
    }

    private static JavaTypeConverter<ContentId> newContentId()
    {
        return new JavaTypeConverter<ContentId>( ContentId.class )
        {
            @Override
            public ContentId convertFrom( final Object value )
            {
                if ( value instanceof ContentId )
                {
                    return (ContentId) value;
                }
                else if ( value instanceof String )
                {
                    return ContentId.from( (String) value );
                }
                else
                {
                    return null;
                }
            }

            @Override
            public ContentId convertFromString( final String value )
            {
                return ContentId.from( value );
            }
        };
    }

    private static JavaTypeConverter<EntityId> newEntityId()
    {
        return new JavaTypeConverter<EntityId>( EntityId.class )
        {
            @Override
            public EntityId convertFrom( final Object value )
            {
                if ( value instanceof EntityId )
                {
                    return (EntityId) value;
                }
                else if ( value instanceof String )
                {
                    return EntityId.from( (String) value );
                }
                else
                {
                    return null;
                }
            }

            @Override
            public EntityId convertFromString( final String value )
            {
                return EntityId.from( value );
            }
        };
    }

    private static JavaTypeConverter<DateTime> newDateTime()
    {
        return new JavaTypeConverter<DateTime>( DateTime.class )
        {
            @Override
            public DateTime convertFrom( final Object value )
            {
                if ( value instanceof BaseDateTime )
                {
                    return ( (BaseDateTime) value ).toDateTime();
                }
                else if ( value instanceof String )
                {
                    return DATE_TIME_FORMATTER.parseDateTime( (String) value ).toDateTime();
                }
                else if ( value instanceof Number )
                {
                    return new DateTime( ( (Number) value ).longValue() );
                }
                else
                {
                    return null;
                }
            }

            @Override
            public DateTime convertFromString( final String value )
            {
                return DATE_TIME_FORMATTER.parseDateTime( value );
            }
        };
    }

    private static JavaTypeConverter<DateMidnight> newDateMidnight()
    {
        return new JavaTypeConverter<DateMidnight>( DateMidnight.class )
        {
            @Override
            public DateMidnight convertFrom( final Object value )
            {
                if ( value instanceof DateMidnight )
                {
                    return (DateMidnight) value;
                }
                else if ( value instanceof String )
                {
                    return DATE_MIDNIGHT_FORMATTER.parseDateTime( (String) value ).toDateMidnight();
                }
                else if ( value instanceof Number )
                {
                    return new DateMidnight( ( (Number) value ).longValue() );
                }
                else
                {
                    return null;
                }
            }

            @Override
            public DateMidnight convertFromString( final String value )
            {
                return DATE_MIDNIGHT_FORMATTER.parseDateTime( value ).toDateMidnight();
            }
        };
    }

    private static JavaTypeConverter<GeoPoint> newGeoPoint()
    {
        return new JavaTypeConverter<GeoPoint>( GeoPoint.class )
        {
            @Override
            public GeoPoint convertFrom( final Object value )
            {
                if ( value instanceof GeoPoint )
                {
                    return (GeoPoint) value;
                }
                else if ( value instanceof String )
                {
                    return GeoPoint.from( (String) value );
                }
                else
                {
                    return null;
                }
            }

            @Override
            public GeoPoint convertFromString( final String value )
            {
                return GeoPoint.from( value );
            }
        };
    }
}
