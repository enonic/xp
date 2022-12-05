package com.enonic.xp.impl.scheduler;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
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
        final NodeIds result = nodeService.deleteByPath( NodePath.create( NodePath.ROOT, name.getValue() ).build() );

        return !result.isEmpty();
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

        protected void validate()
        {
            Preconditions.checkNotNull( name, "name cannot be null." );
        }

        public DeleteScheduledJobCommand build()
        {
            validate();
            return new DeleteScheduledJobCommand( this );
        }
    }
}
