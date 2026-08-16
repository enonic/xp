package com.enonic.xp.impl.scheduler;

import java.util.List;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionId;
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

    /**
     * Lists jobs without fetching run metadata from node versions.
     * Used by the scheduling tick, which tracks run state via {@link SchedulingCoordinator}
     * and a per-job memo keyed by the returned node version id.
     */
    List<ScheduledJobEntry> listEntries()
    {
        return ListScheduledJobEntriesCommand.create().
            nodeService( nodeService ).
            build().
            execute();
    }

    /**
     * Version of a job's node, or null if the job is gone. Used by the scheduling tick to tell
     * whether a job was modified or replaced while one of its runs was in flight.
     */
    NodeVersionId versionId( final ScheduledJobName name )
    {
        return SchedulerContext.createContext().callWith( () -> {
            final Node node = nodeService.getByPath( new NodePath( NodePath.ROOT, NodeName.from( name.getValue() ) ) );
            return node != null ? node.getNodeVersionId() : null;
        } );
    }
}
