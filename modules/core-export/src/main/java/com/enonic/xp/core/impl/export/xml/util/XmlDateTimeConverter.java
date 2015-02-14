package com.enonic.xp.core.impl.export.xml.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.enonic.xp.core.export.ExportNodeException;

public class XmlDateTimeConverter
{
    private static final int UTC_OFFSET = 0;

    public static Instant toInstant( final XMLGregorianCalendar calendar )
    {
        return calendar.toGregorianCalendar().toInstant();
    }

    public static XMLGregorianCalendar toXMLGregorianCalendar( final Instant instant )
    {
        return doGetXmlGregCalFromInstant( instant );
    }

    public static LocalDateTime toLocalDateTime( final XMLGregorianCalendar calendar )
    {
        if ( calendar == null )
        {
            return null;
        }

        final Instant instant = calendar.toGregorianCalendar().toInstant();

        return LocalDateTime.ofInstant( instant, ZoneId.of( "UTC" ) );
    }

    public static XMLGregorianCalendar toXMLGregorianCalendar( final LocalDateTime localDateTime )
    {
        final Instant instant = localDateTime.toInstant( ZoneOffset.UTC );

        return doGetXmlGregCalFromInstant( instant );
    }

    public static LocalTime toLocalTime( final XMLGregorianCalendar calendar )
    {
        if ( calendar == null )
        {
            return null;
        }

        return LocalTime.of( calendar.getHour(), calendar.getMinute(), calendar.getSecond(), calendar.getMillisecond() );
    }

    public static XMLGregorianCalendar toXMLGregorianCalendar( final LocalTime localTime )
    {
        final int ms = safeLongToInt( TimeUnit.NANOSECONDS.toMillis( localTime.getNano() ) );

        try
        {
            return DatatypeFactory.newInstance().
                newXMLGregorianCalendarTime( localTime.getHour(), localTime.getMinute(), localTime.getSecond(), ms, UTC_OFFSET );
        }
        catch ( DatatypeConfigurationException e )
        {
            throw new ExportNodeException( e );
        }
    }

    public static LocalDate toLocalDate( final XMLGregorianCalendar calendar )
    {
        if ( calendar == null )
        {
            return null;
        }

        return OffsetDateTime.of( calendar.getYear(), calendar.getMonth(), calendar.getDay(), 0, 0, 0, 0, ZoneOffset.UTC ).toLocalDate();
    }

    public static XMLGregorianCalendar toXMLGregorianCalendar( final LocalDate localDate )
    {
        Instant instant = localDate.atStartOfDay().atZone( ZoneId.of( "UTC" ) ).toInstant();

        return doGetXmlGregCalFromInstant( instant );
    }

    public static java.util.Date toDate( XMLGregorianCalendar calendar )
    {
        if ( calendar == null )
        {
            return null;
        }

        return calendar.toGregorianCalendar().getTime();
    }

    public static XMLGregorianCalendar toXMLGregorianCalendar( final Date date )
    {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime( date );
        calendar.setTimeZone( TimeZone.getTimeZone( "UTC" ) );

        try
        {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar( calendar );
        }
        catch ( DatatypeConfigurationException e )
        {
            throw new ExportNodeException( e );
        }
    }

    private static XMLGregorianCalendar doGetXmlGregCalFromInstant( final Instant instant )
    {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime( Date.from( instant ) );
        c.setTimeZone( TimeZone.getTimeZone( "UTC" ) );

        try
        {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar( c );
        }
        catch ( DatatypeConfigurationException e )
        {
            throw new ExportNodeException( e );
        }
    }

    private static int safeLongToInt( long l )
    {
        if ( l < Integer.MIN_VALUE || l > Integer.MAX_VALUE )
        {
            throw new IllegalArgumentException( l + " cannot be cast to int without changing its value." );
        }
        return (int) l;
    }

}
