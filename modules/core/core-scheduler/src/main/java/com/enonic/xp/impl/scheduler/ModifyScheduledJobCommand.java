package com.enonic.xp.impl.scheduler;

import java.util.Objects;

import com.enonic.xp.impl.scheduler.serializer.SchedulerSerializer;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.scheduler.ModifyScheduledJobParams;
import com.enonic.xp.scheduler.ScheduledJob;

public class ModifyScheduledJobCommand
    extends AbstractSchedulerCommand
{
    private final ModifyScheduledJobParams params;

    private ModifyScheduledJobCommand( final Builder builder )
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
        final ScheduledJob original = GetScheduledJobCommand.create().
            nodeService( nodeService ).
            name( params.getName() ).
            build().
            execute();

        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            path( new NodePath( NodePath.ROOT, NodeName.from( params.getName().getValue() ) ) ).
            editor( toBeEdited -> toBeEdited.data = SchedulerSerializer.toUpdateNodeData( params, original ) ).
            refresh( RefreshMode.ALL ).
            build();

        final Node updatedNode = nodeService.update( updateNodeParams );

        return SchedulerSerializer.fromNode( updatedNode );
    }

    public static final class Builder
        extends AbstractSchedulerCommand.Builder<Builder>
    {
        private ModifyScheduledJobParams params;

        private Builder()
        {
        }

        public Builder params( final ModifyScheduledJobParams params )
        {
            this.params = params;
            return this;
        }

        @Override
        protected void validate()
        {
            Objects.requireNonNull( params, "params cannot be null" );
        }

        @Override
        public ModifyScheduledJobCommand build()
        {
            validate();
            return new ModifyScheduledJobCommand( this );
        }
    }
}
