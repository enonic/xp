package com.enonic.xp.task;

import java.time.Instant;
import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.security.PrincipalKey;

@Beta
public final class TaskInfo
{
    private final TaskId id;

    private final String name;

    private final String description;

    private final TaskState state;

    private final TaskProgress progress;

    private final ApplicationKey application;

    private final PrincipalKey user;

    private final Instant startTime;

    private TaskInfo( final Builder builder )
    {
        Preconditions.checkNotNull( builder.id, "TaskId cannot be null" );
        id = builder.id;
        name = builder.name == null || builder.name.trim().isEmpty() ? "task-" + builder.id.toString() : builder.name;
        state = builder.state == null ? TaskState.WAITING : builder.state;
        description = builder.description == null ? "" : builder.description;
        progress = builder.progress == null ? TaskProgress.EMPTY : builder.progress;
        application = builder.application == null ? ApplicationKey.SYSTEM : builder.application;
        user = builder.user == null ? PrincipalKey.ofAnonymous() : builder.user;
        startTime = builder.startTime == null ? Instant.now() : builder.startTime;
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
            Objects.equals( user, taskInfo.user ) && Objects.equals( startTime, taskInfo.startTime );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, name, description, state, progress, application, user, startTime );
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

        public TaskInfo build()
        {
            return new TaskInfo( this );
        }
    }

}
