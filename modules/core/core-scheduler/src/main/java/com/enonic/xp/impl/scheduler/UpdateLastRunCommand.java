package com.enonic.xp.impl.scheduler;

import java.time.Instant;
import java.util.Set;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.impl.scheduler.serializer.SchedulerSerializer;
import com.enonic.xp.node.ApplyVersionAttributesParams;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.scheduler.ScheduleCalendarType;
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

    private final NodeVersionId expectedVersionId;

    private UpdateLastRunCommand( final Builder builder )
    {
        super( builder );
        name = builder.name;
        lastRun = builder.lastRun;
        lastTaskId = builder.lastTaskId;
        expectedVersionId = builder.expectedVersionId;
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

        if ( expectedVersionId != null && !expectedVersionId.equals( node.getNodeVersionId() ) )
        {
            // the job changed while this run was in flight - whatever schedule replaced it owns
            // its own record, and must not inherit a run it never made
            return null;
        }

        if ( isOneTime( node ) )
        {
            // a one-time job's lastRun is a tombstone that must survive node version changes
            // (export/import, direct node edits), so it is stored in node data; the job runs
            // once, so the single node version this creates causes no version churn (#12271)
            final Node updatedNode = nodeService.update( UpdateNodeParams.create().
                id( node.id() ).
                editor( toBeEdited -> {
                    toBeEdited.data.setInstant( ScheduledJobPropertyNames.LAST_RUN, lastRun );
                    toBeEdited.data.setString( ScheduledJobPropertyNames.LAST_TASK_ID, lastTaskId != null ? lastTaskId.toString() : null );
                } ).
                refresh( RefreshMode.ALL ).
                build() );

            return SchedulerSerializer.fromNode( updatedNode );
        }

        final Attributes updatedAttributes = nodeService.applyVersionAttributes( ApplyVersionAttributesParams.create().
            nodeVersionId( node.getNodeVersionId() ).
            addAttributes( SchedulerSerializer.toLastRunAttributes( lastRun, lastTaskId ) ).
            removeAttributes( Set.of( ScheduledJobPropertyNames.LAST_RUN, ScheduledJobPropertyNames.LAST_TASK_ID ) ).
            build() );

        return SchedulerSerializer.fromNode( node, updatedAttributes );
    }

    private static boolean isOneTime( final Node node )
    {
        final PropertySet calendar = node.data().getRoot().getSet( ScheduledJobPropertyNames.CALENDAR );
        return calendar != null &&
            ScheduleCalendarType.ONE_TIME.name().equals( calendar.getString( ScheduledJobPropertyNames.CALENDAR_TYPE ) );
    }

    public static final class Builder
        extends AbstractSchedulerCommand.Builder<Builder>
    {
        private Instant lastRun;

        private TaskId lastTaskId;

        private ScheduledJobName name;

        private NodeVersionId expectedVersionId;

        private Builder()
        {
        }

        /**
         * Version the job was read at. The run is not recorded if the job has changed since.
         */
        public Builder expectedVersionId( final NodeVersionId expectedVersionId )
        {
            this.expectedVersionId = expectedVersionId;
            return this;
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
