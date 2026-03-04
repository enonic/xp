package com.enonic.xp.scheduler;

import java.util.List;


public interface SchedulerService
{
    ScheduledJob create( CreateScheduledJobParams params );

    ScheduledJob modify( ModifyScheduledJobParams params );

    boolean delete( ScheduledJobName name );

    ScheduledJob get( ScheduledJobName name );

    List<ScheduledJob> list();
}
