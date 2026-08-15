package com.enonic.xp.scheduler;

import java.time.Instant;


public interface OneTimeCalendar
    extends ScheduleCalendar
{
    Instant getValue();

    /**
     * Whether the job is deleted once it has run, instead of keeping a record of its last run.
     * Suits jobs armed for a single occasion under a name that is never reused, where no record
     * of the run is needed to keep the job from running again.
     */
    boolean isDeleteAfterRun();
}
