package com.enonic.xp.impl.scheduler;

import java.time.Instant;
import java.util.Set;

import com.enonic.xp.impl.scheduler.serializer.SchedulerSerializer;
import com.enonic.xp.node.ApplyVersionAttributesParams;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.task.TaskId;

import static java.util.Objects.requireNonNull;

public class UpdateLastRunCommand
    extends AbstractSchedulerCommand
{
    private final ScheduledJobName name;

    private final Instant lastRun;

    private final TaskId lastTaskId;

    private UpdateLastRunCommand( final Builder builder )
    {
        super( builder );
        name = builder.name;
        lastRun = builder.lastRun;
        lastTaskId = builder.lastTaskId;
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
        final NodePath path = new NodePath( NodePath.ROOT, NodeName.from( name.getValue() ) );
        final Node node = nodeService.getByPath( path );
        if ( node == null )
        {
            throw new NodeNotFoundException( "Node not found: " + path );
        }

        final Attributes updatedAttributes = nodeService.applyVersionAttributes( ApplyVersionAttributesParams.create().
            nodeVersionId( node.getNodeVersionId() ).
            addAttributes( SchedulerSerializer.toLastRunAttributes( lastRun, lastTaskId ) ).
            removeAttributes( Set.of( ScheduledJobPropertyNames.LAST_RUN, ScheduledJobPropertyNames.LAST_TASK_ID ) ).
            build() );

        return SchedulerSerializer.fromNode( node, updatedAttributes );
    }

    public static final class Builder
        extends AbstractSchedulerCommand.Builder<Builder>
    {
        private Instant lastRun;

        private TaskId lastTaskId;

        private ScheduledJobName name;

        private Builder()
        {
        }

        public Builder lastRun( final Instant lastRun )
        {
            this.lastRun = lastRun;
            return this;
        }

        public Builder lastTaskId( final TaskId lastTaskId )
        {
            this.lastTaskId = lastTaskId;
            return this;
        }

        public Builder name( final ScheduledJobName name )
        {
            this.name = name;
            return this;
        }

        @Override
        protected void validate()
        {
            requireNonNull( name, "name is required" );
            requireNonNull( lastRun, "lastRun is required" );
        }

        @Override
        public UpdateLastRunCommand build()
        {
            validate();
            return new UpdateLastRunCommand( this );
        }
    }
}
