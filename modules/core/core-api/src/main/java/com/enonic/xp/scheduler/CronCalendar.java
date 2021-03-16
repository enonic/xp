package com.enonic.xp.scheduler;

import java.util.TimeZone;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface CronCalendar
    extends ScheduleCalendar
{
    String getCronValue();

    TimeZone getTimeZone();
}
