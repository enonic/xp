package com.enonic.xp.impl.scheduler;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.impl.scheduler.serializer.SchedulerSerializer;
import com.enonic.xp.node.ListNodesByParentParams;
import com.enonic.xp.node.ListNodesByParentResult;
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
        final ListNodesByParentResult result = nodeService.list( ListNodesByParentParams.create().parentPath( NodePath.ROOT ).build() );

        return nodeService.getByIds( result.getNodeIds() ).
            stream().
            map( SchedulerSerializer::fromNode ).
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
