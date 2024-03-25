package com.enonic.xp.impl.scheduler;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.impl.scheduler.serializer.SchedulerSerializer;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.ScheduledJob;

public class CreateScheduledJobCommand
    extends AbstractSchedulerCommand
{
    private final CreateScheduledJobParams params;

    private CreateScheduledJobCommand( final Builder builder )
    {
        super( builder );
        params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ScheduledJob execute()
    {
        return SchedulerContext.createContext().callWith( this::doExecute );
    }

    private ScheduledJob doExecute()
    {
        final PropertyTree data = SchedulerSerializer.toCreateNodeData( params );

        final CreateNodeParams createNodeParams = CreateNodeParams.create().setNodeId( NodeId.from( params.getName().getValue() ) ).
            name( params.getName().getValue() ).
            data( data ).
            parent( NodePath.ROOT ).
            refresh( RefreshMode.ALL ).
            build();

        final Node node = nodeService.create( createNodeParams );

        return SchedulerSerializer.fromNode( node );

    }

    public static final class Builder
        extends AbstractSchedulerCommand.Builder<Builder>
    {
        private CreateScheduledJobParams params;

        private Builder()
        {
        }

        public Builder params( final CreateScheduledJobParams params )
        {
            this.params = params;
            return this;
        }

        @Override
        protected void validate()
        {
            Preconditions.checkNotNull( params, "params cannot be null." );
        }

        @Override
        public CreateScheduledJobCommand build()
        {
            validate();
            return new CreateScheduledJobCommand( this );
        }
    }
}
