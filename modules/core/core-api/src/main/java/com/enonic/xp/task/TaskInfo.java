package com.enonic.xp.task;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import com.google.common.base.MoreObjects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.cluster.ClusterNodeId;
import com.enonic.xp.security.PrincipalKey;

@PublicApi
public final class TaskInfo
    implements Serializable
{
    private static final long serialVersionUID = 0;

    private final TaskId id;

    private final String name;

    private final String description;

    private final TaskState state;

    private final TaskProgress progress;

    private final ApplicationKey application;

    private final PrincipalKey user;

    private final Instant startTime;

    private final ClusterNodeId node;

    private TaskInfo( final Builder builder )
    {
        id = Objects.requireNonNull( builder.id, "Task id is required" );
        application = Objects.requireNonNull( builder.application, "Task application is required" );
        name = Objects.requireNonNull( builder.name, "Task name is required" );
        state = Objects.requireNonNullElse( builder.state, TaskState.WAITING );
        description = Objects.requireNonNullElse( builder.description, "" );
        progress = Objects.requireNonNullElse( builder.progress, TaskProgress.EMPTY );
        user = Objects.requireNonNullElse( builder.user, PrincipalKey.ofAnonymous() );
        startTime = Objects.requireNonNull( builder.startTime, "Task startTime is required" );
        node = builder.node;
    }

    public TaskId getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public TaskState getState()
    {
        return state;
    }

    public boolean isRunning()
    {
        return state == TaskState.RUNNING;
    }

    public boolean isDone()
    {
        return state == TaskState.FINISHED || state == TaskState.FAILED;
    }

    public TaskProgress getProgress()
    {
        return progress;
    }

    public ApplicationKey getApplication()
    {
        return application;
    }

    public PrincipalKey getUser()
    {
        return user;
    }

    public Instant getStartTime()
    {
        return startTime;
    }

    public ClusterNodeId getNode()
    {
        return node;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( !( o instanceof TaskInfo ) )
        {
            return false;
        }
        final TaskInfo taskInfo = (TaskInfo) o;
        return Objects.equals( id, taskInfo.id ) && Objects.equals( name, taskInfo.name ) &&
            Objects.equals( description, taskInfo.description ) && state == taskInfo.state &&
            Objects.equals( progress, taskInfo.progress ) && Objects.equals( application, taskInfo.application ) &&
            Objects.equals( user, taskInfo.user ) && Objects.equals( startTime, taskInfo.startTime ) &&
            Objects.equals( node, taskInfo.node );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, name, description, state, progress, application, user, startTime, node );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "id", id ).
            add( "name", name ).
            add( "description", description ).
            add( "state", state ).
            add( "progress", progress ).
            add( "application", application ).
            add( "user", user ).
            add( "startTime", startTime ).
            add( "node", node ).
            toString();
    }

    public Builder copy()
    {
        return new Builder( this );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private TaskId id;

        private String name;

        private TaskState state;

        private String description;

        private TaskProgress progress;

        private ApplicationKey application;

        private PrincipalKey user;

        private Instant startTime;

        private ClusterNodeId node;

        private Builder()
        {
        }

        private Builder( final TaskInfo source )
        {
            id = source.id;
            name = source.name;
            state = source.state;
            description = source.description;
            progress = source.progress;
            application = source.application;
            user = source.user;
            startTime = source.startTime;
            node = source.node;
        }

        public Builder id( final TaskId id )
        {
            this.id = id;
            return this;
        }

        public Builder state( final TaskState state )
        {
            this.state = state;
            return this;
        }

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder progress( final TaskProgress progress )
        {
            this.progress = progress;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder application( final ApplicationKey application )
        {
            this.application = application;
            return this;
        }

        public Builder user( final PrincipalKey user )
        {
            this.user = user;
            return this;
        }

        public Builder startTime( final Instant startTime )
        {
            this.startTime = startTime;
            return this;
        }

        public Builder node( final ClusterNodeId location )
        {
            this.node = location;
            return this;
        }

        public TaskInfo build()
        {
            return new TaskInfo( this );
        }
    }
}
