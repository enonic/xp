package com.enonic.xp.impl.scheduler;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.impl.scheduler.serializer.SchedulerSerializer;
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeListEntry;
import com.enonic.xp.node.NodePath;

public class ListScheduledJobsCommand
    extends AbstractSchedulerCommand
{
    private final boolean withRunState;

    private ListScheduledJobsCommand( final Builder builder )
    {
        super( builder );
        this.withRunState = builder.withRunState;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public List<ScheduledJobEntry> execute()
    {
        return SchedulerContext.createContext().callWith( this::doExecute );
    }

    private List<ScheduledJobEntry> doExecute()
    {
        final NodeIds jobIds = nodeService.list( ListNodesParams.create().parentPath( NodePath.ROOT ).build() )
            .map( NodeListEntry::nodeId )
            .collect( NodeIds.collector() );

        return nodeService.getByIds( jobIds ).
            stream().
            map( node -> new ScheduledJobEntry( withRunState ? toScheduledJob( node ) : SchedulerSerializer.fromNode( node ),
                                                node.getNodeVersionId() ) ).
            collect( Collectors.toList() );
    }

    public static final class Builder
        extends AbstractSchedulerCommand.Builder<Builder>
    {
        private boolean withRunState;

        private Builder()
        {
        }

        public Builder withRunState( final boolean withRunState )
        {
            this.withRunState = withRunState;
            return this;
        }

        @Override
        public ListScheduledJobsCommand build()
        {
            validate();
            return new ListScheduledJobsCommand( this );
        }
    }
}
