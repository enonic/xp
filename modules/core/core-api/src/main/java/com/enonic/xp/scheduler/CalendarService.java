package com.enonic.xp.scheduler;

import java.util.TimeZone;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface CalendarService
{
    ScheduleCalendar cron( String value, TimeZone timeZone );

//    ScheduleCalendar oneTime( final String value );

}
