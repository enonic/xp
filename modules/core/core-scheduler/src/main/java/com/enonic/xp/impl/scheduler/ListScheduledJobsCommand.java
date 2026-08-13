package com.enonic.xp.impl.scheduler;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.ListNodesResult;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.scheduler.ScheduledJob;

public class ListScheduledJobsCommand
    extends AbstractSchedulerCommand
{

    private ListScheduledJobsCommand( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public List<ScheduledJob> execute()
    {
        return SchedulerContext.createContext().callWith( this::doExecute );
    }

    private List<ScheduledJob> doExecute()
    {
        final ListNodesResult result = nodeService.list( ListNodesParams.create().parentPath( NodePath.ROOT ).build() );

        return nodeService.getByIds( result.getNodeIds() ).
            stream().
            map( this::toScheduledJob ).
            collect( Collectors.toList() );
    }

    public static final class Builder
        extends AbstractSchedulerCommand.Builder<Builder>
    {
        private Builder()
        {
        }

        @Override
        public ListScheduledJobsCommand build()
        {
            validate();
            return new ListScheduledJobsCommand( this );
        }
    }
}
