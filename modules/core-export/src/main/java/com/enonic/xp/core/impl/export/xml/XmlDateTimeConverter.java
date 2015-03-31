package com.enonic.xp.core.impl.export.xml;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.enonic.xp.util.Exceptions;

final class XmlDateTimeConverter
{
    private final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern( "yyyy-MM-ddX" );

    private final static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern( "HH:mm:ss.SSSX" );

    private final static DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern( "yyyy-MM-dd'T'HH:mm:ss.SSSX" );

    public static Instant parseInstant( final String value )
    {
        return toInstant( fromXmlValue( value ) );
    }

    public static String format( final Instant value )
    {
        return value.atZone( ZoneId.of( "UTC" ) ).format( DATE_TIME_FORMAT );
    }

    public static LocalDateTime parseLocalDateTime( final String value )
    {
        return toLocalDateTime( fromXmlValue( value ) );
    }

    public static String format( final LocalDateTime value )
    {
        return value.atZone( ZoneId.of( "UTC" ) ).format( DATE_TIME_FORMAT );
    }

    public static LocalDate parseLocalDate( final String value )
    {
        return toLocalDate( fromXmlValue( value ) );
    }

    public static String format( final LocalDate value )
    {
        return ZonedDateTime.of( value, LocalTime.now(), ZoneId.of( "UTC" ) ).format( DATE_FORMAT.withZone( ZoneId.of( "UTC" ) ) );
    }

    public static LocalTime parseLocalTime( final String value )
    {
        return toLocalTime( fromXmlValue( value ) );
    }

    public static String format( final LocalTime value )
    {
        return ZonedDateTime.of( LocalDate.now(), value, ZoneId.of( "UTC" ) ).format( TIME_FORMAT.withZone( ZoneId.of( "UTC" ) ) );
    }

    private static Instant toInstant( final XMLGregorianCalendar calendar )
    {
        return calendar.toGregorianCalendar().toInstant();
    }

    private static LocalDateTime toLocalDateTime( final XMLGregorianCalendar calendar )
    {
        if ( calendar == null )
        {
            return null;
        }

        final Instant instant = calendar.toGregorianCalendar().toInstant();

        return LocalDateTime.ofInstant( instant, ZoneId.of( "UTC" ) );
    }

    private static LocalTime toLocalTime( final XMLGregorianCalendar calendar )
    {
        if ( calendar == null )
        {
            return null;
        }

        return LocalTime.of( calendar.getHour(), calendar.getMinute(), calendar.getSecond(), calendar.getMillisecond() );
    }

    private static LocalDate toLocalDate( final XMLGregorianCalendar calendar )
    {
        if ( calendar == null )
        {
            return null;
        }

        return OffsetDateTime.of( calendar.getYear(), calendar.getMonth(), calendar.getDay(), 0, 0, 0, 0, ZoneOffset.UTC ).toLocalDate();
    }

    private static XMLGregorianCalendar fromXmlValue( final String value )
    {
        return newDataTypeFactory().newXMLGregorianCalendar( value );
    }

    private static DatatypeFactory newDataTypeFactory()
    {
        try
        {
            return DatatypeFactory.newInstance();
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }
}
