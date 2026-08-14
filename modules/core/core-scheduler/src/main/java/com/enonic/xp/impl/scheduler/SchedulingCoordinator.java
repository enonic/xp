package com.enonic.xp.impl.scheduler;

import java.util.Set;

import com.enonic.xp.scheduler.ScheduledJobName;

/**
 * Shares planned job executions between cluster members, so a job is not submitted
 * twice for the same occurrence, planned occurrences survive a scheduler failover,
 * and the task of the previous run is known when the next run is due.
 */
public interface SchedulingCoordinator
{
    /**
     * Planned execution of a job, or null when not known
     * (never run since the coordinator state was created, or state was lost).
     * For a one-time job a non-null value means its only execution was already submitted.
     */
    PlannedRun plannedRun( ScheduledJobName name );

    /**
     * Records the planned execution of a job.
     */
    void plannedRun( ScheduledJobName name, PlannedRun value );

    /**
     * Discards the planned execution of a job, e.g. when the job is modified or deleted.
     */
    void forget( ScheduledJobName name );

    /**
     * Discards planned executions of all jobs except the given ones.
     */
    void retain( Set<ScheduledJobName> names );
}
