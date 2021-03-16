package com.enonic.xp.scheduler;

import java.time.Instant;
import java.util.TimeZone;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface CalendarService
{
    CronCalendar cron( String value, TimeZone timeZone );

    OneTimeCalendar oneTime( Instant value );

}
