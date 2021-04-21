package com.enonic.xp.impl.scheduler;

import java.util.List;

import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.ModifyScheduledJobParams;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.scheduler.SchedulerService;

public class SchedulerServiceImpl
    implements SchedulerService
{
    private final IndexService indexService;

    private final RepositoryService repositoryService;

    private final NodeService nodeService;

    private final SchedulerExecutorService schedulerExecutorService;

    private final ScheduleAuditLogSupport auditLogSupport;

    public SchedulerServiceImpl( final IndexService indexService, final RepositoryService repositoryService, final NodeService nodeService,
                                 final SchedulerExecutorService schedulerExecutorService, final ScheduleAuditLogSupport auditLogSupport )
    {
        this.indexService = indexService;
        this.repositoryService = repositoryService;
        this.nodeService = nodeService;
        this.schedulerExecutorService = schedulerExecutorService;
        this.auditLogSupport = auditLogSupport;
    }

    public void initialize()
    {
        SchedulerRepoInitializer.create().
            setIndexService( indexService ).
            setRepositoryService( repositoryService ).
            build().
            initialize();
    }

    @Override
    public ScheduledJob create( final CreateScheduledJobParams params )
    {
        final ScheduledJob job = CreateScheduledJobCommand.create().
            nodeService( nodeService ).
            params( params ).
            build().
            execute();

        auditLogSupport.create( params, job );

        return job;
    }

    @Override
    public ScheduledJob modify( final ModifyScheduledJobParams params )
    {
        UnscheduleJobCommand.create().
            schedulerExecutorService( schedulerExecutorService ).
            name( params.getName() ).
            build().
            execute();

        final ScheduledJob job = ModifyScheduledJobCommand.create().
            nodeService( nodeService ).
            params( params ).
            build().
            execute();

        auditLogSupport.modify( params, job );

        return job;
    }

    @Override
    public boolean delete( final ScheduledJobName name )
    {
        UnscheduleJobCommand.create().
            schedulerExecutorService( schedulerExecutorService ).
            name( name ).
            build().
            execute();

        final boolean result = DeleteScheduledJobCommand.create().
            nodeService( nodeService ).
            name( name ).
            build().
            execute();

        auditLogSupport.delete( name, result );

        return result;
    }

    @Override
    public ScheduledJob get( final ScheduledJobName name )
    {
        return GetScheduledJobCommand.create().
            nodeService( nodeService ).
            name( name ).
            build().
            execute();
    }

    @Override
    public List<ScheduledJob> list()
    {
        return ListScheduledJobsCommand.create().
            nodeService( nodeService ).
            build().
            execute();
    }
}
