package com.enonic.xp.core.impl.export.xml.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

import com.enonic.xp.core.impl.export.xml.util.XmlDateTimeConverter;

import static org.junit.Assert.*;

public class XmlDateTimeConverterTest
{
    private final TimeZone defaultTimezone = TimeZone.getDefault();

    @Test
    public void instant()
        throws Exception
    {
        final Instant instant = Instant.now();

        final XMLGregorianCalendar calendar = XmlDateTimeConverter.toXMLGregorianCalendar( instant );

        final Instant instant2 = XmlDateTimeConverter.toInstant( calendar );

        assertEquals( instant, instant2 );
    }

    @Test
    public void instant_changed_timezone()
        throws Exception
    {
        final Instant instant = Instant.now();

        final XMLGregorianCalendar calendar = XmlDateTimeConverter.toXMLGregorianCalendar( instant );

        final Instant instant2 = XmlDateTimeConverter.toInstant( calendar );

        assertEquals( instant, instant2 );

        TimeZone.setDefault( defaultTimezone );
    }

    @Test
    public void localDateTime()
        throws Exception
    {
        final LocalDateTime localDateTime = LocalDateTime.of( 2014, 12, 3, 19, 39, 0 );

        final XMLGregorianCalendar calendar = XmlDateTimeConverter.toXMLGregorianCalendar( localDateTime );

        final LocalDateTime parsedLocalDateTime = XmlDateTimeConverter.toLocalDateTime( calendar );

        assertEquals( localDateTime, parsedLocalDateTime );
    }

    @Test
    public void localDateTime_changed_timezone()
        throws Exception
    {
        final LocalDateTime localDateTime = LocalDateTime.of( 2014, 12, 3, 19, 39, 0 );

        TimeZone.setDefault( TimeZone.getTimeZone( "GMT-16:00" ) );

        final XMLGregorianCalendar calendar = XmlDateTimeConverter.toXMLGregorianCalendar( localDateTime );

        final LocalDateTime parsedLocalDateTime = XmlDateTimeConverter.toLocalDateTime( calendar );

        assertEquals( localDateTime, parsedLocalDateTime );

        TimeZone.setDefault( defaultTimezone );
    }


    @Test
    public void localTime()
        throws Exception
    {
        final LocalTime localTime = LocalTime.of( 20, 24, 33 );

        final XMLGregorianCalendar calendar = XmlDateTimeConverter.toXMLGregorianCalendar( localTime );

        final LocalTime parsedTime = XmlDateTimeConverter.toLocalTime( calendar );

        assertEquals( localTime, parsedTime );
    }


    @Test
    public void localDate()
        throws Exception
    {
        final LocalDate localDate = LocalDate.now();

        final XMLGregorianCalendar calendar = XmlDateTimeConverter.toXMLGregorianCalendar( localDate );

        final LocalDate parsedLocalDate = XmlDateTimeConverter.toLocalDate( calendar );

        assertEquals( localDate, parsedLocalDate );
    }


    @Test
    public void localDateAnotherTimeZone()
        throws Exception
    {
        TimeZone.setDefault( TimeZone.getTimeZone( "GMT-16:00" ) );

        final LocalDate localDate = LocalDate.now();

        final XMLGregorianCalendar calendar = XmlDateTimeConverter.toXMLGregorianCalendar( localDate );

        final LocalDate parsedLocalDate = XmlDateTimeConverter.toLocalDate( calendar );

        assertEquals( localDate, parsedLocalDate );

        TimeZone.setDefault( defaultTimezone );
    }

    @Test
    public void date()
        throws Exception
    {
        TimeZone.setDefault( TimeZone.getTimeZone( "GMT-16:00" ) );

        final Date date = Date.from( Instant.now() );

        final XMLGregorianCalendar calendar = XmlDateTimeConverter.toXMLGregorianCalendar( date );

        final Date parsedDate = XmlDateTimeConverter.toDate( calendar );

        assertEquals( date, parsedDate );
    }

}