package com.enonic.xp.impl.scheduler;

import java.time.Instant;
import java.util.Set;

import com.enonic.xp.scheduler.ScheduledJobName;

/**
 * Shares planned job execution times between cluster members, so a job is not submitted
 * twice for the same occurrence and planned occurrences survive a scheduler failover.
 */
public interface SchedulingCoordinator
{
    /**
     * Planned time of the next execution of a job, or null when not known
     * (never run since the coordinator state was created, or state was lost).
     */
    Instant nextRun( ScheduledJobName name );

    /**
     * Records the planned time of the next execution of a job.
     */
    void nextRun( ScheduledJobName name, Instant value );

    /**
     * Discards the planned execution of a job, e.g. when the job is modified or deleted.
     */
    void forget( ScheduledJobName name );

    /**
     * Discards planned executions of all jobs except the given ones.
     */
    void retain( Set<ScheduledJobName> names );
}
