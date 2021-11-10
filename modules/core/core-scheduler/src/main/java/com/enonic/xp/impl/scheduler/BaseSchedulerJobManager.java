package com.enonic.xp.impl.scheduler;

import java.util.List;

import com.enonic.xp.node.NodeService;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.ModifyScheduledJobParams;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;

abstract class BaseSchedulerJobManager
    implements SchedulerJobManager
{
    private final NodeService nodeService;

    BaseSchedulerJobManager( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    @Override
    public ScheduledJob create( final CreateScheduledJobParams params )
    {
        return doCreate( params );
    }

    @Override
    public boolean delete( final ScheduledJobName name )
    {
        return doDelete( name );
    }


    @Override
    public ScheduledJob modify( ModifyScheduledJobParams params )
    {
        return doModify( params );
    }

    @Override
    public ScheduledJob get( ScheduledJobName name )
    {
        return doGet( name );
    }

    @Override
    public List<ScheduledJob> list()
    {
        return doList();
    }

    protected ScheduledJob doCreate( final CreateScheduledJobParams params )
    {
        return CreateScheduledJobCommand.create().nodeService( nodeService ).params( params ).build().execute();
    }

    protected boolean doDelete( final ScheduledJobName name )
    {
        return DeleteScheduledJobCommand.create().nodeService( nodeService ).name( name ).build().execute();
    }

    protected ScheduledJob doModify( ModifyScheduledJobParams params )
    {
        return ModifyScheduledJobCommand.create().nodeService( nodeService ).params( params ).build().execute();
    }

    protected ScheduledJob doGet( ScheduledJobName name )
    {
        return GetScheduledJobCommand.create().nodeService( nodeService ).name( name ).build().execute();
    }

    protected List<ScheduledJob> doList()
    {
        return ListScheduledJobsCommand.create().nodeService( nodeService ).build().execute();
    }

}
