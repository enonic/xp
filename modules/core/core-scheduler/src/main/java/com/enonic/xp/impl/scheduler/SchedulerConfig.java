package com.enonic.xp.impl.scheduler;

import java.util.Set;

import com.enonic.xp.scheduler.CreateScheduledJobParams;

public interface SchedulerConfig
{
    Set<CreateScheduledJobParams> jobs();

    boolean auditlogEnabled();

    /**
     * Whether this node accepts running the scheduler. One member of a cluster ticks the schedule,
     * and it is chosen among the members that accept it - a node that does not is left alone by the
     * schedule, but still serves the API and holds the jobs like any other.
     */
    boolean acceptScheduling();
}
