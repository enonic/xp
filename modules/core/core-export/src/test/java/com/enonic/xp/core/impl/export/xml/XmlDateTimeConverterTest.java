package com.enonic.xp.core.impl.export.xml;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlDateTimeConverterTest
{
    @Test
    void parseInstant()
    {
        final Instant value1 = XmlDateTimeConverter.parseInstant( "2012-11-12T22:11:00.000Z" );
        assertEquals( "2012-11-12T22:11:00Z", XmlDateTimeConverter.format( value1 ) );

        final Instant value2 = XmlDateTimeConverter.parseInstant( "2012-11-12T22:11:00.010+01:00" );
        assertEquals( "2012-11-12T21:11:00.010Z", XmlDateTimeConverter.format( value2 ) );

        final Instant value3 = XmlDateTimeConverter.parseInstant( "2012-11-12T22:11:00.000010+01:00" );
        assertEquals( "2012-11-12T21:11:00.000010Z", XmlDateTimeConverter.format( value3 ) );

        final Instant value4 = XmlDateTimeConverter.parseInstant( "2012-11-12T22:11:00+01:00" );
        assertEquals( "2012-11-12T21:11:00Z", XmlDateTimeConverter.format( value4 ) );
    }

    @Test
    void parseLocalDateTime()
    {
        final LocalDateTime value1 = XmlDateTimeConverter.parseLocalDateTime( "2012-11-12T22:11:00.000Z" );
        assertEquals( "2012-11-12T22:11:00.000Z", XmlDateTimeConverter.format( value1 ) );

        final LocalDateTime value2 = XmlDateTimeConverter.parseLocalDateTime( "2012-11-12T22:11:00.000+01:00" );
        assertEquals( "2012-11-12T21:11:00.000Z", XmlDateTimeConverter.format( value2 ) );
    }

    @Test
    void parseLocalDate()
    {
        final LocalDate value1 = XmlDateTimeConverter.parseLocalDate( "2012-11-12Z" );
        assertEquals( "2012-11-12Z", XmlDateTimeConverter.format( value1 ) );

        final LocalDate value2 = XmlDateTimeConverter.parseLocalDate( "2012-11-12+01:00" );
        assertEquals( "2012-11-12Z", XmlDateTimeConverter.format( value2 ) );
    }

    @Test
    void parseLocalTime()
    {
        final LocalTime value1 = XmlDateTimeConverter.parseLocalTime( "22:11:00.000Z" );
        assertEquals( "22:11:00.000Z", XmlDateTimeConverter.format( value1 ) );

        final LocalTime value2 = XmlDateTimeConverter.parseLocalTime( "22:11:00.000+01:00" );
        assertEquals( "22:11:00.000Z", XmlDateTimeConverter.format( value2 ) );
    }

    @Test
    void formatInstant()
    {
        assertEquals( "2012-11-12T22:11:00Z", XmlDateTimeConverter.format( Instant.parse( "2012-11-12T22:11:00Z" ) ) );
        assertEquals( "2012-11-12T22:11:00.100Z", XmlDateTimeConverter.format( Instant.parse( "2012-11-12T22:11:00.100Z" ) ) );
        assertEquals( "2012-11-12T22:11:00.000010Z", XmlDateTimeConverter.format( Instant.parse( "2012-11-12T22:11:00.000010Z" ) ) );
        assertEquals( "2012-11-12T22:11:00.123456789Z", XmlDateTimeConverter.format( Instant.parse( "2012-11-12T22:11:00.123456789Z" ) ) );
        assertEquals( "1970-01-01T00:00:00Z", XmlDateTimeConverter.format( Instant.EPOCH ) );
    }

    @Test
    void formatLocalDateTime()
    {
        assertEquals( "2012-11-12T22:11:00.000Z", XmlDateTimeConverter.format( LocalDateTime.of( 2012, 11, 12, 22, 11, 0 ) ) );
        assertEquals( "2012-11-12T22:11:30.000Z", XmlDateTimeConverter.format( LocalDateTime.of( 2012, 11, 12, 22, 11, 30 ) ) );
        assertEquals( "2012-11-12T00:00:00.000Z", XmlDateTimeConverter.format( LocalDateTime.of( 2012, 11, 12, 0, 0, 0 ) ) );
        assertEquals( "2012-11-12T22:11:00.123Z", XmlDateTimeConverter.format( LocalDateTime.of( 2012, 11, 12, 22, 11, 0, 123000000 ) ) );
    }

    @Test
    void formatLocalDate()
    {
        assertEquals( "2012-11-12Z", XmlDateTimeConverter.format( LocalDate.of( 2012, 11, 12 ) ) );
        assertEquals( "2000-01-01Z", XmlDateTimeConverter.format( LocalDate.of( 2000, 1, 1 ) ) );
        assertEquals( "1999-12-31Z", XmlDateTimeConverter.format( LocalDate.of( 1999, 12, 31 ) ) );
    }

    @Test
    void formatLocalTime()
    {
        assertEquals( "22:11:00.000Z", XmlDateTimeConverter.format( LocalTime.of( 22, 11, 0 ) ) );
        assertEquals( "00:00:00.000Z", XmlDateTimeConverter.format( LocalTime.of( 0, 0, 0 ) ) );
        assertEquals( "23:59:59.000Z", XmlDateTimeConverter.format( LocalTime.of( 23, 59, 59 ) ) );
        assertEquals( "22:11:00.123Z", XmlDateTimeConverter.format( LocalTime.of( 22, 11, 0, 123000000 ) ) );
    }
}
