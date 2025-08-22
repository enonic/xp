package com.enonic.xp.impl.scheduler;

import java.util.Objects;

import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.scheduler.ScheduledJobName;

public class DeleteScheduledJobCommand
    extends AbstractSchedulerCommand
{
    private final ScheduledJobName name;

    private DeleteScheduledJobCommand( final Builder builder )
    {
        super( builder );
        name = builder.name;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public boolean execute()
    {
        return SchedulerContext.createContext().callWith( this::doExecute );
    }

    private boolean doExecute()
    {
        return nodeService.delete( DeleteNodeParams.create()
                                       .nodePath( new NodePath( NodePath.ROOT, NodeName.from( name.getValue() ) ) )
                                       .refresh( RefreshMode.ALL )
                                       .build() ).getNodeBranchEntries().isNotEmpty();
    }

    public static final class Builder
        extends AbstractSchedulerCommand.Builder<Builder>
    {
        private ScheduledJobName name;

        private Builder()
        {
        }

        public Builder name( final ScheduledJobName name )
        {
            this.name = name;
            return this;
        }

        @Override
        protected void validate()
        {
            Objects.requireNonNull( name, "name is required" );
        }

        @Override
        public DeleteScheduledJobCommand build()
        {
            validate();
            return new DeleteScheduledJobCommand( this );
        }
    }
}
