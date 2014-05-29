package com.enonic.wem.api.data.type;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.serializer.RootDataSetJsonSerializer;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.util.GeoPoint;

final class JavaTypeConverters
{

    private final static DateTimeFormatter LOCAL_DATE_FORMATTER =
        new java.time.format.DateTimeFormatterBuilder().appendValue( ChronoField.YEAR, 4 ).appendLiteral( '-' ).appendValue(
            ChronoField.MONTH_OF_YEAR, 2 ).appendLiteral( '-' ).appendValue( ChronoField.DAY_OF_MONTH, 2 ).toFormatter();


    private final static java.time.format.DateTimeFormatter LOCAL_DATE_TIME_FORMATTER =
        new java.time.format.DateTimeFormatterBuilder().appendValue( ChronoField.YEAR, 4 ).appendLiteral( '-' ).appendValue(
            ChronoField.MONTH_OF_YEAR, 2 ).appendLiteral( '-' ).appendValue( ChronoField.DAY_OF_MONTH, 2 ).appendLiteral( "T" ).appendValue(
            ChronoField.CLOCK_HOUR_OF_DAY, 2 ).appendLiteral( ":" ).appendValue( ChronoField.MINUTE_OF_HOUR, 2 ).appendLiteral(
            ":" ).appendValue( ChronoField.SECOND_OF_MINUTE, 2 ).toFormatter();


    private final static RootDataSetJsonSerializer DATA_SERIALIZER = new RootDataSetJsonSerializer();

    public final static JavaTypeConverter<String> STRING = newString();

    public final static JavaTypeConverter<Long> LONG = newLong();

    public final static JavaTypeConverter<Double> DOUBLE = newDouble();

    public final static JavaTypeConverter<Boolean> BOOLEAN = newBoolean();

    public final static JavaTypeConverter<RootDataSet> DATA = newData();

    public final static JavaTypeConverter<ContentId> CONTENT_ID = newContentId();

    public final static JavaTypeConverter<EntityId> ENTITY_ID = newEntityId();

    public final static JavaTypeConverter<Instant> DATE_TIME = newInstant();

    public final static JavaTypeConverter<LocalDate> LOCAL_DATE = newLocalDate();

    public final static JavaTypeConverter<GeoPoint> GEO_POINT = newGeoPoint();

    private static String convertToString( final Object value )
    {
        if ( value instanceof String )
        {
            return (String) value;
        }
        else if ( value instanceof LocalDateTime )
        {
            return ( (LocalDateTime) value ).format( LOCAL_DATE_TIME_FORMATTER );
        }
        else if ( value instanceof LocalDate )
        {
            return ( (LocalDate) value ).format( LOCAL_DATE_FORMATTER );
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

    private static Long convertToLong( final Object value )
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

    private static Double convertToDouble( final Object value )
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

    private static Boolean convertToBoolean( final Object value )
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

    private static RootDataSet convertToData( final Object value )
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

    private static ContentId convertToContentId( final Object value )
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

    private static EntityId convertToEntityId( final Object value )
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

    private static Instant convertToInstant( final Object value )
    {
        if ( value instanceof LocalDate )
        {
            return ( (LocalDate) value ).atStartOfDay().toInstant( ZoneOffset.UTC );

        }
        if ( value instanceof Instant )
        {
            return ( (Instant) value );
        }
        else if ( value instanceof String )
        {
            return Instant.parse( (String) value );
        }
        else
        {
            return null;
        }
    }

    private static LocalDate convertToLocalDate( final Object value )
    {
        if ( value instanceof Instant )
        {
            return LocalDateTime.ofInstant( (Instant) value, ZoneOffset.UTC ).toLocalDate();
        }
        if ( value instanceof LocalDate )
        {
            return (LocalDate) value;
        }
        if ( value instanceof LocalDateTime )
        {
            return LocalDate.of( ( (LocalDateTime) value ).getYear(), ( (LocalDateTime) value ).getMonth(),
                                 ( (LocalDateTime) value ).getDayOfMonth() );
        }
        else if ( value instanceof String )
        {
            return LocalDate.parse( (String) value, LOCAL_DATE_FORMATTER );

        }
        else
        {
            return null;
        }
    }

    private static GeoPoint convertToGeoPoint( final Object value )
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

    private static JavaTypeConverter<String> newString()
    {
        return new JavaTypeConverter<>( String.class, JavaTypeConverters::convertToString );
    }

    private static JavaTypeConverter<Long> newLong()
    {
        return new JavaTypeConverter<>( Long.class, JavaTypeConverters::convertToLong );
    }

    private static JavaTypeConverter<Double> newDouble()
    {
        return new JavaTypeConverter<>( Double.class, JavaTypeConverters::convertToDouble );
    }

    private static JavaTypeConverter<Boolean> newBoolean()
    {
        return new JavaTypeConverter<>( Boolean.class, JavaTypeConverters::convertToBoolean );
    }

    private static JavaTypeConverter<RootDataSet> newData()
    {
        return new JavaTypeConverter<>( RootDataSet.class, JavaTypeConverters::convertToData );
    }

    private static JavaTypeConverter<ContentId> newContentId()
    {
        return new JavaTypeConverter<>( ContentId.class, JavaTypeConverters::convertToContentId );
    }

    private static JavaTypeConverter<EntityId> newEntityId()
    {
        return new JavaTypeConverter<>( EntityId.class, JavaTypeConverters::convertToEntityId );
    }

    private static JavaTypeConverter<Instant> newInstant()
    {
        return new JavaTypeConverter<>( Instant.class, JavaTypeConverters::convertToInstant );
    }

    private static JavaTypeConverter<LocalDate> newLocalDate()
    {
        return new JavaTypeConverter<>( LocalDate.class, JavaTypeConverters::convertToLocalDate );
    }

    private static JavaTypeConverter<GeoPoint> newGeoPoint()
    {
        return new JavaTypeConverter<>( GeoPoint.class, JavaTypeConverters::convertToGeoPoint );
    }
}
