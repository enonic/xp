package com.enonic.wem.api.data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.util.BinaryReference;
import com.enonic.wem.api.util.GeoPoint;
import com.enonic.wem.api.util.Link;
import com.enonic.wem.api.util.Reference;

final class JavaTypeConverters
{

    private final static DateTimeFormatter LOCAL_DATE_FORMATTER =
        new java.time.format.DateTimeFormatterBuilder().appendValue( ChronoField.YEAR, 4 ).appendLiteral( '-' ).appendValue(
            ChronoField.MONTH_OF_YEAR, 2 ).appendLiteral( '-' ).appendValue( ChronoField.DAY_OF_MONTH, 2 ).toFormatter();

    private final static DateTimeFormatter LOCAL_DATE_TIME_FORMATTER =
        new java.time.format.DateTimeFormatterBuilder().appendValue( ChronoField.YEAR, 4 ).appendLiteral( '-' ).appendValue(
            ChronoField.MONTH_OF_YEAR, 2 ).appendLiteral( '-' ).appendValue( ChronoField.DAY_OF_MONTH, 2 ).appendLiteral( "T" ).appendValue(
            ChronoField.HOUR_OF_DAY, 2 ).appendLiteral( ":" ).appendValue( ChronoField.MINUTE_OF_HOUR, 2 ).appendLiteral(
            ":" ).appendValue( ChronoField.SECOND_OF_MINUTE, 2 ).toFormatter();

    private final static DateTimeFormatter LOCAL_TIME_FORMATTER =
        new java.time.format.DateTimeFormatterBuilder().appendValue( ChronoField.HOUR_OF_DAY, 1, 2, SignStyle.NORMAL ).appendLiteral(
            ":" ).appendValue( ChronoField.MINUTE_OF_HOUR, 1, 2, SignStyle.NORMAL ).toFormatter();

    private final static PropertySetJsonSerializer DATA_SERIALIZER = new PropertySetJsonSerializer();

    public final static JavaTypeConverter<String> STRING = newString();

    public final static JavaTypeConverter<Long> LONG = newLong();

    public final static JavaTypeConverter<Double> DOUBLE = newDouble();

    public final static JavaTypeConverter<Boolean> BOOLEAN = newBoolean();

    public final static JavaTypeConverter<PropertySet> DATA = newData();

    public final static JavaTypeConverter<ContentId> CONTENT_ID = newContentId();

    public final static JavaTypeConverter<Instant> DATE_TIME = newInstant();

    public final static JavaTypeConverter<LocalDate> LOCAL_DATE = newLocalDate();

    public final static JavaTypeConverter<LocalDateTime> LOCAL_DATE_TIME = newLocalDateTime();

    public final static JavaTypeConverter<LocalTime> LOCAL_TIME = newLocalTime();

    public final static JavaTypeConverter<GeoPoint> GEO_POINT = newGeoPoint();

    public final static JavaTypeConverter<Reference> REFERENCE = newReference();

    public final static JavaTypeConverter<BinaryReference> BINARY_REFERENCE = newBinaryReference();

    public final static JavaTypeConverter<Link> LINK = newLink();

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
        else if ( value instanceof PropertySet )
        {
            return DATA_SERIALIZER.serializeToString( (PropertySet) value );
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
            if ( isNumber( value.toString() ) )
            {
                return new Long( (String) value );
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    public static boolean isNumber( String strNum )
    {
        boolean ret = true;
        try
        {
            Double.parseDouble( strNum );

        }
        catch ( NumberFormatException e )
        {
            ret = false;
        }
        return ret;
    }

    private static Double convertToDouble( final Object value )
    {
        if ( value instanceof Number )
        {
            return ( (Number) value ).doubleValue();
        }
        else if ( value instanceof String )
        {
            if ( isNumber( value.toString() ) )
            {
                return new Double( (String) value );
            }
            return null;
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

    private static PropertySet convertToData( final Object value )
    {
        if ( value instanceof PropertySet )
        {
            return (PropertySet) value;
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

    private static Instant convertToInstant( final Object value )
    {
        if ( value instanceof LocalDate )
        {
            return ( (LocalDate) value ).atStartOfDay().toInstant( ZoneOffset.UTC );
        }
        if ( value instanceof LocalTime )
        {
            return ( (LocalTime) value ).atDate( LocalDate.now() ).toInstant( ZoneOffset.UTC );
        }
        if ( value instanceof LocalDateTime )
        {
            return ( (LocalDateTime) value ).toInstant( ZoneOffset.UTC );
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

    private static LocalTime convertToLocalTime( final Object value )
    {
        if ( value instanceof Instant )
        {
            return LocalDateTime.ofInstant( (Instant) value, ZoneOffset.UTC ).toLocalTime().truncatedTo( ChronoUnit.MINUTES );
        }
        if ( value instanceof LocalTime )
        {
            return (LocalTime) value;
        }
        if ( value instanceof LocalDate )
        {
            return ( (LocalDate) value ).atStartOfDay().toLocalTime();
        }
        if ( value instanceof LocalDateTime )
        {
            return LocalTime.of( ( (LocalDateTime) value ).getHour(), ( (LocalDateTime) value ).getMinute(),
                                 ( (LocalDateTime) value ).getSecond() );
        }
        else if ( value instanceof String )
        {
            return LocalTime.parse( (String) value, LOCAL_TIME_FORMATTER );
        }
        else
        {
            return null;
        }
    }

    private static LocalDateTime convertToLocalDateTime( final Object value )
    {
        if ( value instanceof Instant )
        {
            return LocalDateTime.ofInstant( (Instant) value, ZoneOffset.UTC );
        }
        if ( value instanceof LocalDate )
        {
            return ( (LocalDate) value ).atStartOfDay();
        }
        if ( value instanceof LocalDateTime )
        {
            return (LocalDateTime) value;
        }
        if ( value instanceof LocalTime )
        {
            return ( (LocalTime) value ).atDate( LocalDate.now() );

        }
        else if ( value instanceof String )
        {
            return LocalDateTime.parse( (String) value, LOCAL_DATE_TIME_FORMATTER );

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

    private static Reference convertToReference( final Object value )
    {
        if ( value instanceof Reference )
        {
            return (Reference) value;
        }
        else if ( value instanceof String )
        {
            return Reference.from( (String) value );
        }
        else
        {
            return null;
        }
    }

    private static BinaryReference convertToBinaryReference( final Object value )
    {
        if ( value instanceof BinaryReference )
        {
            return (BinaryReference) value;
        }
        else if ( value instanceof String )
        {
            return BinaryReference.from( (String) value );
        }
        else
        {
            return null;
        }
    }

    private static Link convertToLink( final Object value )
    {
        if ( value instanceof Link )
        {
            return (Link) value;
        }
        else if ( value instanceof String )
        {
            return Link.from( (String) value );
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

    private static JavaTypeConverter<PropertySet> newData()
    {
        return new JavaTypeConverter<>( PropertySet.class, JavaTypeConverters::convertToData );
    }

    private static JavaTypeConverter<ContentId> newContentId()
    {
        return new JavaTypeConverter<>( ContentId.class, JavaTypeConverters::convertToContentId );
    }

    private static JavaTypeConverter<Instant> newInstant()
    {
        return new JavaTypeConverter<>( Instant.class, JavaTypeConverters::convertToInstant );
    }

    private static JavaTypeConverter<LocalDate> newLocalDate()
    {
        return new JavaTypeConverter<>( LocalDate.class, JavaTypeConverters::convertToLocalDate );
    }

    private static JavaTypeConverter<LocalDateTime> newLocalDateTime()
    {
        return new JavaTypeConverter<>( LocalDateTime.class, JavaTypeConverters::convertToLocalDateTime );
    }

    private static JavaTypeConverter<LocalTime> newLocalTime()
    {
        return new JavaTypeConverter<>( LocalTime.class, JavaTypeConverters::convertToLocalTime );
    }

    private static JavaTypeConverter<GeoPoint> newGeoPoint()
    {
        return new JavaTypeConverter<>( GeoPoint.class, JavaTypeConverters::convertToGeoPoint );
    }

    private static JavaTypeConverter<Reference> newReference()
    {
        return new JavaTypeConverter<>( Reference.class, JavaTypeConverters::convertToReference );
    }

    private static JavaTypeConverter<BinaryReference> newBinaryReference()
    {
        return new JavaTypeConverter<>( BinaryReference.class, JavaTypeConverters::convertToBinaryReference );
    }

    private static JavaTypeConverter<Link> newLink()
    {
        return new JavaTypeConverter<>( Link.class, JavaTypeConverters::convertToLink );
    }
}
