package com.enonic.xp.impl.scheduler;

import java.util.Objects;

import com.enonic.xp.impl.scheduler.serializer.SchedulerSerializer;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;

public class GetScheduledJobCommand
    extends AbstractSchedulerCommand
{
    private final ScheduledJobName name;

    private GetScheduledJobCommand( final Builder builder )
    {
        super( builder );
        name = builder.name;
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
        final Node node = nodeService.getByPath( new NodePath( NodePath.ROOT, NodeName.from( name.getValue() ) ) );
        if ( node != null )
        {
            return SchedulerSerializer.fromNode( node );
        }

        return null;
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
        public GetScheduledJobCommand build()
        {
            validate();
            return new GetScheduledJobCommand( this );
        }
    }
}
