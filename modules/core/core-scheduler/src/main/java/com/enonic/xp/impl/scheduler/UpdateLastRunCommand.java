package com.enonic.xp.impl.scheduler;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.xp.impl.scheduler.serializer.SchedulerSerializer;
import com.enonic.xp.node.Node;
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
            path( NodePath.create( NodePath.ROOT, name.getValue() ).build() ).
            editor( ( toBeEdited -> {
                toBeEdited.data.setInstant( ScheduledJobPropertyNames.LAST_RUN, lastRun );
                toBeEdited.data.setString( ScheduledJobPropertyNames.LAST_TASK_ID, lastTaskId.toString() );
            } ) ).
            build();

        final Node updatedNode = nodeService.update( updateNodeParams );
        nodeService.refresh( RefreshMode.ALL );

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

        protected void validate()
        {
            Preconditions.checkNotNull( name, "name cannot be null." );
            Preconditions.checkNotNull( lastRun, "lastRun cannot be null." );
            Preconditions.checkNotNull( lastTaskId, "lastTaskId cannot be null." );
        }

        public UpdateLastRunCommand build()
        {
            validate();
            return new UpdateLastRunCommand( this );
        }
    }
}
