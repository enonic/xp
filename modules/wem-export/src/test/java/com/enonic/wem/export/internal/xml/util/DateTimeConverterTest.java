package com.enonic.wem.export.internal.xml.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

import static org.junit.Assert.*;

public class DateTimeConverterTest
{
    @Test
    public void instant()
        throws Exception
    {
        final Instant instant = Instant.now();

        final XMLGregorianCalendar calendar = DateTimeConverter.toXMLGregorianCalendar( instant );

        final Instant instant2 = DateTimeConverter.toInstant( calendar );

        assertEquals( instant, instant2 );
    }

    @Test
    public void instant_changed_timezone()
        throws Exception
    {
        final TimeZone defaultTimezone = TimeZone.getDefault();

        final Instant instant = Instant.now();

        final XMLGregorianCalendar calendar = DateTimeConverter.toXMLGregorianCalendar( instant );

        TimeZone.setDefault( TimeZone.getTimeZone( "BET" ) );

        final Instant instant2 = DateTimeConverter.toInstant( calendar );

        assertEquals( instant, instant2 );

        TimeZone.setDefault( defaultTimezone );
    }

    @Test
    public void localDateTime()
        throws Exception
    {
        final LocalDateTime localDateTime = LocalDateTime.of( 2014, 12, 3, 19, 39, 0 );

        final XMLGregorianCalendar calendar = DateTimeConverter.toXMLGregorianCalendar( localDateTime );

        final LocalDateTime parsedLocalDateTime = DateTimeConverter.toLocalDateTime( calendar );

        assertEquals( localDateTime, parsedLocalDateTime );
    }


    @Test
    public void localTime()
        throws Exception
    {
        final LocalTime localTime = LocalTime.of( 20, 24, 33 );

        final XMLGregorianCalendar calendar = DateTimeConverter.toXMLGregorianCalendar( localTime );

        final LocalTime parsedTime = DateTimeConverter.toLocalTime( calendar );

        assertEquals( localTime, parsedTime );
    }


    @Test
    public void localDate()
        throws Exception
    {
        final LocalDate localDate = LocalDate.now();

        final XMLGregorianCalendar calendar = DateTimeConverter.toXMLGregorianCalendar( localDate );

        final LocalDate parsedLocalDate = DateTimeConverter.toLocalDate( calendar );

        assertEquals( localDate, parsedLocalDate );
    }


    @Test
    public void date()
        throws Exception
    {
        final Date date = Date.from( Instant.now() );

        final XMLGregorianCalendar calendar = DateTimeConverter.toXMLGregorianCalendar( date );

        final Date parsedDate = DateTimeConverter.toDate( calendar );

        assertEquals( date, parsedDate );
    }

}