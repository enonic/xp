package com.enonic.xp.impl.scheduler;

import java.util.Set;

import com.enonic.xp.scheduler.CreateScheduledJobParams;

public interface SchedulerConfig
{
    Set<CreateScheduledJobParams> jobs();

    boolean auditlogEnabled();
}
