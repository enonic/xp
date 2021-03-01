package com.enonic.xp.impl.scheduler;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.impl.scheduler.distributed.CronCalendar;
import com.enonic.xp.impl.scheduler.distributed.OneTimeCalendar;

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
        final CronCalendar calendar = (CronCalendar) calendarService.cron( "* * * * *", TimeZone.getTimeZone( "GMT+5:30" ) );

        assertTrue( calendar.nextExecution().get().get( ChronoUnit.SECONDS ) <= 60 );
        assertEquals( TimeZone.getTimeZone( "GMT+5:30" ), calendar.getTimeZone() );
        assertEquals( "every minute", calendar.getDescription() );
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
        final OneTimeCalendar calendar = (OneTimeCalendar) calendarService.oneTime( Instant.parse( "2014-09-25T10:00:00.00Z" ) );

        assertTrue( calendar.nextExecution().get().isNegative() );
        assertEquals( Instant.parse( "2014-09-25T10:00:00.00Z" ), Instant.parse( calendar.getValue() ) );
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

