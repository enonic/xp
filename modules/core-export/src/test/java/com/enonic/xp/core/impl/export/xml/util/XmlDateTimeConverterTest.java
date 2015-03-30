package com.enonic.xp.core.impl.export.xml.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.Test;

import static org.junit.Assert.*;

public class XmlDateTimeConverterTest
{
    @Test
    public void parseInstant()
        throws Exception
    {
        final Instant value1 = XmlDateTimeConverter.parseInstant( "2012-11-12T22:11:00.000Z" );
        assertEquals( "2012-11-12T22:11:00.000Z", XmlDateTimeConverter.format( value1 ) );

        final Instant value2 = XmlDateTimeConverter.parseInstant( "2012-11-12T22:11:00.000+01:00" );
        assertEquals( "2012-11-12T21:11:00.000Z", XmlDateTimeConverter.format( value2 ) );
    }

    @Test
    public void parseLocalDateTime()
        throws Exception
    {
        final LocalDateTime value1 = XmlDateTimeConverter.parseLocalDateTime( "2012-11-12T22:11:00.000Z" );
        assertEquals( "2012-11-12T22:11:00.000Z", XmlDateTimeConverter.format( value1 ) );

        final LocalDateTime value2 = XmlDateTimeConverter.parseLocalDateTime( "2012-11-12T22:11:00.000+01:00" );
        assertEquals( "2012-11-12T21:11:00.000Z", XmlDateTimeConverter.format( value2 ) );
    }

    @Test
    public void parseLocalDate()
        throws Exception
    {
        final LocalDate value1 = XmlDateTimeConverter.parseLocalDate( "2012-11-12Z" );
        assertEquals( "2012-11-12Z", XmlDateTimeConverter.format( value1 ) );

        final LocalDate value2 = XmlDateTimeConverter.parseLocalDate( "2012-11-12+01:00" );
        assertEquals( "2012-11-12Z", XmlDateTimeConverter.format( value2 ) );
    }

    @Test
    public void parseLocalTime()
        throws Exception
    {
        final LocalTime value1 = XmlDateTimeConverter.parseLocalTime( "22:11:00.000Z" );
        assertEquals( "22:11:00.000Z", XmlDateTimeConverter.format( value1 ) );

        final LocalTime value2 = XmlDateTimeConverter.parseLocalTime( "22:11:00.000+01:00" );
        assertEquals( "22:11:00.000Z", XmlDateTimeConverter.format( value2 ) );
    }
}
