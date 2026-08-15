package com.enonic.xp.impl.scheduler;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.scheduler.CronCalendar;
import com.enonic.xp.scheduler.FixedDelayCalendar;
import com.enonic.xp.scheduler.OneTimeCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalendarServiceImplTest
{
    private CalendarServiceImpl calendarService;

    @BeforeEach
    void initialize()
    {
        calendarService = new CalendarServiceImpl();
    }

    @Test
    void cron()
    {
        final CronCalendar calendar = calendarService.cron( "* * * * *", TimeZone.getTimeZone( "GMT+5:30" ) );

        final Instant now = Instant.now();
        assertTrue( Duration.between( now, calendar.nextExecution( now ).get() ).get( ChronoUnit.SECONDS ) <= 60 );
        assertEquals( TimeZone.getTimeZone( "GMT+5:30" ), calendar.getTimeZone() );
        assertEquals( "* * * * *", calendar.getCronValue() );
    }

    @Test
    void cronInvalid()
    {
        assertThrows( IllegalArgumentException.class, () -> calendarService.cron( "wrong value", TimeZone.getTimeZone( "GMT+5:30" ) ) );
    }

    @Test
    void cronNull()
    {
        assertThrows( NullPointerException.class, () -> calendarService.cron( null, TimeZone.getTimeZone( "GMT+5:30" ) ) );
        assertThrows( NullPointerException.class, () -> calendarService.cron( "* * * * *", null ) );
    }

    @Test
    void oneTime()
    {
        final OneTimeCalendar calendar = calendarService.oneTime( Instant.parse( "2014-09-25T10:00:00.00Z" ) );
        final Instant now = Instant.now();

        assertTrue( Duration.between( now, calendar.nextExecution( now ).get() ).isNegative() );
        assertEquals( Instant.parse( "2014-09-25T10:00:00.00Z" ), calendar.getValue() );
        assertFalse( calendar.isDeleteAfterRun() );
    }

    @Test
    void oneTimeDeleteAfterRun()
    {
        assertTrue( calendarService.oneTime( Instant.parse( "2014-09-25T10:00:00.00Z" ), true ).isDeleteAfterRun() );
        assertFalse( calendarService.oneTime( Instant.parse( "2014-09-25T10:00:00.00Z" ), false ).isDeleteAfterRun() );
    }

    @Test
    void oneTimeInvalid()
    {
        assertThrows( DateTimeParseException.class, () -> calendarService.oneTime( Instant.parse( "wrong value" ) ) );
    }

    @Test
    void oneTimeNull()
    {
        assertThrows( NullPointerException.class, () -> calendarService.oneTime( null ) );
    }



    @Test
    void fixedDelay()
    {
        final FixedDelayCalendar calendar = calendarService.fixedDelay( Duration.ofMinutes( 5 ) );

        final Instant now = Instant.parse( "2026-01-01T10:30:00Z" );
        assertEquals( now.plus( Duration.ofMinutes( 5 ) ), calendar.nextExecution( now ).get() );
        assertEquals( Duration.ofMinutes( 5 ), calendar.getDuration() );
        assertEquals( ScheduleCalendarType.FIXED_DELAY, calendar.getType() );
    }

    @Test
    void fixedDelayInvalid()
    {
        assertThrows( NullPointerException.class, () -> calendarService.fixedDelay( null ) );
        assertThrows( IllegalArgumentException.class, () -> calendarService.fixedDelay( Duration.ZERO ) );
        assertThrows( IllegalArgumentException.class, () -> calendarService.fixedDelay( Duration.ofSeconds( -1 ) ) );
    }
}
