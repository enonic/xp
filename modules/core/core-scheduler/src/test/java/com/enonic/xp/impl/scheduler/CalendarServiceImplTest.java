package com.enonic.xp.impl.scheduler;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.scheduler.CronCalendar;
import com.enonic.xp.scheduler.OneTimeCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CalendarServiceImplTest
{
    private CalendarServiceImpl calendarService;

    @BeforeEach
    public void initialize()
    {
        calendarService = new CalendarServiceImpl();
    }

    @Test
    public void cron()
    {
        final CronCalendar calendar = calendarService.cron( "* * * * *", TimeZone.getTimeZone( "GMT+5:30" ) );

        final Instant now = Instant.now();
        assertTrue( Duration.between( now, calendar.nextExecution( now ).get() ).get( ChronoUnit.SECONDS ) <= 60 );
        assertEquals( TimeZone.getTimeZone( "GMT+5:30" ), calendar.getTimeZone() );
        assertEquals( "* * * * *", calendar.getCronValue() );
    }

    @Test
    public void cronInvalid()
    {
        assertThrows( IllegalArgumentException.class, () -> calendarService.cron( "wrong value", TimeZone.getTimeZone( "GMT+5:30" ) ) );
    }

    @Test
    public void cronNull()
    {
        assertThrows( NullPointerException.class, () -> calendarService.cron( null, TimeZone.getTimeZone( "GMT+5:30" ) ) );
        assertThrows( NullPointerException.class, () -> calendarService.cron( "* * * * *", null ) );
    }

    @Test
    public void oneTime()
    {
        final OneTimeCalendar calendar = calendarService.oneTime( Instant.parse( "2014-09-25T10:00:00.00Z" ) );
        final Instant now = Instant.now();

        assertTrue( Duration.between( now, calendar.nextExecution( now ).get() ).isNegative() );
        assertEquals( Instant.parse( "2014-09-25T10:00:00.00Z" ), calendar.getValue() );
    }

    @Test
    public void oneTimeInvalid()
    {
        assertThrows( DateTimeParseException.class, () -> calendarService.oneTime( Instant.parse( "wrong value" ) ) );
    }

    @Test
    public void oneTimeNull()
    {
        assertThrows( NullPointerException.class, () -> calendarService.oneTime( null ) );
    }


}

