package com.enonic.xp.lib.scheduler;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.lib.scheduler.mapper.ScheduledJobMapper;
import com.enonic.xp.scheduler.ScheduledJob;

public final class ListScheduledJobsHandler
    extends BaseSchedulerHandler
{
    @Override
    protected List<ScheduledJobMapper> doExecute()
    {
        final List<ScheduledJob> scheduledJobs = this.schedulerService.get().list();

        return scheduledJobs.stream().
            map( ScheduledJobMapper::from ).
            collect( Collectors.toList() );
    }

    @Override
    protected void validate()
    {
    }
}
