package com.enonic.xp.scheduler;

import java.time.Instant;
import java.util.TimeZone;


public interface CalendarService
{
    CronCalendar cron( String value, TimeZone timeZone );

    OneTimeCalendar oneTime( Instant value );

}
