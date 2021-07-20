package com.enonic.xp.scheduler;

import java.util.List;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface SchedulerService
{
    ScheduledJob create( CreateScheduledJobParams params );

    ScheduledJob modify( ModifyScheduledJobParams params );

    boolean delete( SchedulerName name );

    ScheduledJob get( SchedulerName name );

    List<ScheduledJob> list();
}
