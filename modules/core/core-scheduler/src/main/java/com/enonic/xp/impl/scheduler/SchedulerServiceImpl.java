package com.enonic.xp.impl.scheduler;

import java.util.List;

import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.ModifyScheduledJobParams;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.SchedulerName;
import com.enonic.xp.scheduler.SchedulerService;

public class SchedulerServiceImpl
    implements SchedulerService
{
    private final IndexService indexService;

    private final RepositoryService repositoryService;

    private final NodeService nodeService;

    private final SchedulerExecutorService schedulerExecutorService;

    public SchedulerServiceImpl( final IndexService indexService, final RepositoryService repositoryService, final NodeService nodeService,
                                 final SchedulerExecutorService schedulerExecutorService )
    {
        this.indexService = indexService;
        this.repositoryService = repositoryService;
        this.nodeService = nodeService;
        this.schedulerExecutorService = schedulerExecutorService;
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
        return CreateScheduledJobCommand.create().
            nodeService( nodeService ).
            params( params ).
            build().
            execute();
    }

    @Override
    public ScheduledJob modify( final ModifyScheduledJobParams params )
    {
        UnscheduleJobCommand.create().
            schedulerExecutorService( schedulerExecutorService ).
            name( params.getName() ).
            build().
            execute();

        return ModifyScheduledJobCommand.create().
            nodeService( nodeService ).
            params( params ).
            build().
            execute();
    }

    @Override
    public boolean delete( final SchedulerName name )
    {
        UnscheduleJobCommand.create().
            schedulerExecutorService( schedulerExecutorService ).
            name( name ).
            build().
            execute();

        return DeleteScheduledJobCommand.create().
            nodeService( nodeService ).
            name( name ).
            build().
            execute();
    }

    @Override
    public ScheduledJob get( final SchedulerName name )
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
