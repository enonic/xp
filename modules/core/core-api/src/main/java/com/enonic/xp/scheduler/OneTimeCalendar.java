package com.enonic.xp.scheduler;

import java.time.Instant;

import org.jspecify.annotations.NullMarked;

/**
 * A schedule that runs once, at a fixed point in time.
 */
@NullMarked
public interface OneTimeCalendar
    extends ScheduleCalendar
{
    /**
     * The instant the job runs at.
     */
    Instant getValue();

    /**
     * Whether the job is deleted once it has run, instead of keeping a record of its last run.
     * Suits jobs armed for a single occasion under a name that is never reused, where no record
     * of the run is needed to keep the job from running again. Keeping the record is the default.
     */
    default boolean isDeleteAfterRun()
    {
        return false;
    }
}
