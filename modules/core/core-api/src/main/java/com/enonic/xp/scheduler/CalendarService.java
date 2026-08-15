package com.enonic.xp.scheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.TimeZone;


public interface CalendarService
{
    CronCalendar cron( String value, TimeZone timeZone );

    OneTimeCalendar oneTime( Instant value );

    OneTimeCalendar oneTime( Instant value, boolean deleteAfterRun );

    FixedDelayCalendar fixedDelay( Duration duration );

}
