package com.enonic.xp.impl.scheduler;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.impl.scheduler.serializer.SchedulerSerializer;
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.ListNodesResult;
import com.enonic.xp.node.NodePath;

/**
 * Lists scheduled jobs without fetching run metadata from node versions,
 * saving one version read per job compared to {@link ListScheduledJobsCommand}.
 */
class ListScheduledJobEntriesCommand
    extends AbstractSchedulerCommand
{
    private ListScheduledJobEntriesCommand( final Builder builder )
    {
        super( builder );
    }

    static Builder create()
    {
        return new Builder();
    }

    public List<ScheduledJobEntry> execute()
    {
        return SchedulerContext.createContext().callWith( this::doExecute );
    }

    private List<ScheduledJobEntry> doExecute()
    {
        final ListNodesResult result = nodeService.list( ListNodesParams.create().parentPath( NodePath.ROOT ).build() );

        return nodeService.getByIds( result.getNodeIds() ).
            stream().
            map( node -> new ScheduledJobEntry( SchedulerSerializer.fromNode( node ), node.getNodeVersionId() ) ).
            collect( Collectors.toList() );
    }

    static final class Builder
        extends AbstractSchedulerCommand.Builder<Builder>
    {
        private Builder()
        {
        }

        @Override
        public ListScheduledJobEntriesCommand build()
        {
            validate();
            return new ListScheduledJobEntriesCommand( this );
        }
    }
}
