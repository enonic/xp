package com.enonic.xp.scheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.TimeZone;

import org.jspecify.annotations.NullMarked;

/**
 * Builds the schedules a scheduled job can run on.
 */
@NullMarked
public interface CalendarService
{
    /**
     * A schedule that runs on every occurrence of a cron expression, read in the given time zone.
     * An occurrence arriving while the previous execution is still running is skipped.
     *
     * @throws IllegalArgumentException if the value is not a cron expression
     */
    CronCalendar cron( String value, TimeZone timeZone );

    /**
     * A schedule that runs once, at the given instant, keeping a record of that run afterwards.
     */
    OneTimeCalendar oneTime( Instant value );

    /**
     * A schedule that runs once, at the given instant. Pass {@code true} to have the job deleted
     * once it has run rather than keeping a record of the run - see
     * {@link OneTimeCalendar#isDeleteAfterRun()} for when that is appropriate.
     */
    OneTimeCalendar oneTime( Instant value, boolean deleteAfterRun );

    /**
     * A schedule that runs repeatedly, the given duration apart, measured between the starts of two
     * consecutive executions - see {@link FixedRateCalendar} for what happens when one overruns.
     *
     * @throws IllegalArgumentException if the duration is not positive
     */
    FixedRateCalendar fixedRate( Duration duration );

}
