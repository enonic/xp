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
        String value = null;
        String cronValue = null;
        String timeZone = null;
        String duration = null;
        Boolean deleteAfterRun = null;

        if ( calendar instanceof CronCalendar cron )
        {
            cronValue = cron.getCronValue();
            timeZone = cron.getTimeZone().getID();
        }
        else if ( calendar instanceof OneTimeCalendar oneTime )
        {
            value = oneTime.getValue().toString();
            deleteAfterRun = oneTime.isDeleteAfterRun();
        }
        else if ( calendar instanceof FixedRateCalendar fixedRate )
        {
            duration = fixedRate.getDuration().toString();
        }
        // a calendar type this was not written for reports what it is and nothing more

        this.type = calendar.getType();
        this.value = value;
        this.cronValue = cronValue;
        this.timeZone = timeZone;
        this.duration = duration;
        this.deleteAfterRun = deleteAfterRun;
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
