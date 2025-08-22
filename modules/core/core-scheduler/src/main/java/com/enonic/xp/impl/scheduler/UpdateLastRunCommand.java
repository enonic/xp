package com.enonic.xp.impl.scheduler;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.impl.scheduler.serializer.SchedulerSerializer;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.task.TaskId;

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
        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            path( new NodePath( NodePath.ROOT, NodeName.from( name.getValue() ) ) ).
            editor( toBeEdited -> {
                toBeEdited.data.setInstant( ScheduledJobPropertyNames.LAST_RUN, lastRun );
            toBeEdited.data.setString( ScheduledJobPropertyNames.LAST_TASK_ID, lastTaskId != null ? lastTaskId.toString() : null );
            } ).
            refresh( RefreshMode.ALL ).
            build();

        final Node updatedNode = nodeService.update( updateNodeParams );

        return SchedulerSerializer.fromNode( updatedNode );
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
            Objects.requireNonNull( name, "name is required" );
            Objects.requireNonNull( lastRun, "lastRun is required" );
        }

        @Override
        public UpdateLastRunCommand build()
        {
            validate();
            return new UpdateLastRunCommand( this );
        }
    }
}
