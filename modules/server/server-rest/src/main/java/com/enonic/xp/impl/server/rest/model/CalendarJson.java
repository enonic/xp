package com.enonic.xp.impl.server.rest.model;

import com.enonic.xp.scheduler.CronCalendar;
import com.enonic.xp.scheduler.FixedRateCalendar;
import com.enonic.xp.scheduler.OneTimeCalendar;
import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;

/**
 * Schedule of a job as reported by the API. The values are rendered here rather than left to
 * reflection over the calendar itself, so that what a client reads is the same notation it would
 * use to describe the schedule - an ISO-8601 duration for a fixed rate, say, rather than the
 * seconds a {@link java.time.Duration} serializes to by default.
 * <p>
 * Only the fields that apply to the type are set; the rest are left out of the response.
 */
public class CalendarJson
{
    private final ScheduleCalendarType type;

    private final String value;

    private final String cronValue;

    private final String timeZone;

    private final String duration;

    private final Boolean deleteAfterRun;

    public CalendarJson( final ScheduleCalendar calendar )
    {
        this.type = calendar.getType();

        if ( calendar instanceof CronCalendar cron )
        {
            this.cronValue = cron.getCronValue();
            this.timeZone = cron.getTimeZone().getID();
            this.value = null;
            this.duration = null;
            this.deleteAfterRun = null;
        }
        else if ( calendar instanceof OneTimeCalendar oneTime )
        {
            this.value = oneTime.getValue().toString();
            this.deleteAfterRun = oneTime.isDeleteAfterRun();
            this.cronValue = null;
            this.timeZone = null;
            this.duration = null;
        }
        else if ( calendar instanceof FixedRateCalendar fixedRate )
        {
            this.duration = fixedRate.getDuration().toString();
            this.value = null;
            this.cronValue = null;
            this.timeZone = null;
            this.deleteAfterRun = null;
        }
        else
        {
            // a calendar type this was not written for still reports what it is
            this.value = null;
            this.cronValue = null;
            this.timeZone = null;
            this.duration = null;
            this.deleteAfterRun = null;
        }
    }

    public ScheduleCalendarType getType()
    {
        return type;
    }

    public String getValue()
    {
        return value;
    }

    public String getCronValue()
    {
        return cronValue;
    }

    public String getTimeZone()
    {
        return timeZone;
    }

    public String getDuration()
    {
        return duration;
    }

    public Boolean getDeleteAfterRun()
    {
        return deleteAfterRun;
    }
}
