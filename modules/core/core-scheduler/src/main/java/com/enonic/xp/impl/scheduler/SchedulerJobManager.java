package com.enonic.xp.impl.scheduler;

import java.util.List;

import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.ModifyScheduledJobParams;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;

public interface SchedulerJobManager
{
    ScheduledJob create( CreateScheduledJobParams params );

    ScheduledJob modify( ModifyScheduledJobParams params );

    boolean delete( ScheduledJobName name );

    ScheduledJob get( ScheduledJobName name );

    List<ScheduledJob> list();
}
