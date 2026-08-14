package com.enonic.xp.impl.scheduler;

import java.util.List;

import com.enonic.xp.node.NodeService;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.ModifyScheduledJobParams;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.scheduler.SchedulerService;

public class SchedulerServiceImpl
    implements SchedulerService
{
    private final NodeService nodeService;

    private final SchedulingCoordinator schedulingCoordinator;

    private final ScheduleAuditLogSupport auditLogSupport;

    public SchedulerServiceImpl( final NodeService nodeService,
                                 final SchedulingCoordinator schedulingCoordinator, final ScheduleAuditLogSupport auditLogSupport )
    {
        this.nodeService = nodeService;
        this.schedulingCoordinator = schedulingCoordinator;
        this.auditLogSupport = auditLogSupport;
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
        final ScheduledJob job = ModifyScheduledJobCommand.create().
            nodeService( nodeService ).
            params( params ).
            build().
            execute();

        schedulingCoordinator.forget( params.getName() );

        auditLogSupport.modify( params, job );

        return job;
    }

    @Override
    public boolean delete( final ScheduledJobName name )
    {
        final boolean result = DeleteScheduledJobCommand.create().
            nodeService( nodeService ).
            name( name ).
            build().
            execute();

        schedulingCoordinator.forget( name );

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
