package com.enonic.wem.api.content;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;

import org.junit.Test;

import com.enonic.wem.api.data.Property;

import static junit.framework.Assert.assertEquals;

public class JavaTypeConverterTest
{
    @Test
    public void convert_from_string_to_localtime()
    {
        Property property = Property.newLocalTime( "myLocalTime", "10:24:50" );
        LocalTime actual = property.getLocalTime();
        LocalTime expected = LocalTime.of( 10, 24, 50 );
        assertEquals( expected, actual );
    }

    @Test
    public void convert_from_instant_to_localtime()
    {
        Instant instant = Instant.parse( "2014-12-03T10:15:30.00Z" );
        Property property = Property.newLocalTime( "myLocalTime", instant );
        LocalTime actual = property.getLocalTime();
        LocalTime expected = LocalTime.of( 10, 15, 30 );
        assertEquals( expected, actual );
    }

    @Test
    public void convert_from_date_to_localtime()
    {
        LocalDate date = LocalDate.of( 2014, Month.DECEMBER, 31 );
        Property property = Property.newLocalTime( "myLocalTime", date );
        LocalTime actual = property.getLocalTime();
        LocalTime expected = LocalTime.of( 0, 0, 0 );
        assertEquals( expected, actual );
    }

    @Test
    public void convert_from_datetime_to_localtime()
    {
        LocalDateTime datetime = LocalDateTime.of( 2014, Month.DECEMBER, 31, 23, 59, 59 );
        Property property = Property.newLocalTime( "myLocalTime", datetime );
        LocalTime actual = property.getLocalTime();
        LocalTime expected = LocalTime.of( 23, 59, 59 );
        assertEquals( expected, actual );
    }

    @Test
    public void convert_from_localtime_to_localtime()
    {
        LocalTime time = LocalTime.of( 10, 20, 55 );
        Property property = Property.newLocalTime( "myLocalTime", time );
        LocalTime actual = property.getLocalTime();
        assertEquals( time, actual );
    }

    @Test
    public void convert_from_instant_to_localdatetime()
    {
        Instant instant = Instant.parse( "2014-12-31T10:15:30.00Z" );
        Property property = Property.newLocalDateTime( "myLocalDateTime", instant );
        LocalDateTime actual = property.getLocalDateTime();
        LocalDateTime expected = LocalDateTime.of( 2014, 12, 31, 10, 15, 30 );
        assertEquals( expected, actual );
    }

    @Test
    public void convert_from_string_to_localdatetime()
    {
        Property property = Property.newLocalDateTime( "myLocalDateTime", "2014-12-31T10:15:30" );
        LocalDateTime actual = property.getLocalDateTime();
        LocalDateTime expected = LocalDateTime.of( 2014, 12, 31, 10, 15, 30 );
        assertEquals( expected, actual );
    }

    @Test
    public void convert_from_date_to_localdatetime()
    {
        LocalDate date = LocalDate.of( 2014, Month.DECEMBER, 31 );
        Property property = Property.newLocalDateTime( "myLocalDateTime", date );
        LocalDateTime actual = property.getLocalDateTime();
        LocalDateTime expected = LocalDateTime.of( 2014, 12, 31, 0, 0, 0 );
        assertEquals( expected, actual );
    }

    @Test
    public void convert_from_time_to_localdatetime()
    {
        LocalTime localtime = LocalTime.of( 12, 45, 50 );
        Property property = Property.newLocalDateTime( "myLocalDateTime", localtime );
        LocalDateTime actual = property.getLocalDateTime();
        LocalDate date = LocalDate.now();
        LocalDateTime expected = LocalDateTime.of( date.getYear(), date.getMonth(), date.getDayOfMonth(), 12, 45, 50 );
        assertEquals( expected, actual );
    }


    @Test
    public void convert_from_localdatetime_to_localdatetime()
    {
        LocalDateTime expected = LocalDateTime.now();
        Property property = Property.newLocalDateTime( "myLocalDateTime", expected );
        LocalDateTime actual = property.getLocalDateTime();
        assertEquals( expected, actual );
    }

    @Test
    public void convert_from_string_to_date()
    {
        Property property = Property.newLocalDate( "myLocalDate", "2014-12-31" );
        LocalDate actual = property.getLocalDate();
        assertEquals( LocalDate.of( 2014, 12, 31 ), actual );
    }

    @Test
    public void convert_from_date_to_date()
    {
        LocalDate date = LocalDate.of( 2014, 12, 31 );
        Property property = Property.newLocalDate( "myLocalDate", date );
        LocalDate actual = property.getLocalDate();
        assertEquals( date, actual );
    }

    @Test
    public void convert_from_instant_to_date()
    {
        Instant instant = Instant.parse( "2014-12-03T10:15:30.00Z" );
        Property property = Property.newLocalDate( "myLocalDate", instant );
        LocalDate expected = LocalDate.of( 2014, 12, 03 );
        LocalDate actual = property.getLocalDate();
        assertEquals( expected, actual );
    }

    @Test
    public void convert_from_datetime_to_date()
    {
        LocalDateTime datetime = LocalDateTime.of( 2014, 12, 31, 23, 59, 59 );
        Property property = Property.newLocalDate( "myLocalDate", datetime );
        LocalDate expected = LocalDate.of( datetime.getYear(), datetime.getMonth(), datetime.getDayOfMonth() );
        LocalDate actual = property.getLocalDate();
        assertEquals( expected, actual );
    }

    @Test
    public void convert_from_string_to_instant()
    {
        String instant_as_string = "2014-12-03T10:15:30.00Z";
        Property property = Property.newInstant( "myInstant", instant_as_string );
        Instant expected = Instant.parse( instant_as_string );
        Instant actual = property.getInstant();
        assertEquals( expected, actual );
    }

    @Test
    public void convert_from_date_to_instant()
    {
        LocalDate date = LocalDate.of( 2014, 12, 31 );
        Property property = Property.newInstant( "myInstant", date );
        Instant expected = date.atStartOfDay().toInstant( ZoneOffset.UTC );
        Instant actual = property.getInstant();
        assertEquals( expected, actual );
    }

}

