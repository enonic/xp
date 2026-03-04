package com.enonic.xp.scheduler;

import java.util.TimeZone;


public interface CronCalendar
    extends ScheduleCalendar
{
    String getCronValue();

    TimeZone getTimeZone();
}
