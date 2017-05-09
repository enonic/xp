package com.enonic.xp.task;

import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

@Beta
public final class TaskInfo
{
    private final TaskId id;

    private final String description;

    private final TaskState state;

    private final TaskProgress progress;

    private TaskInfo( final Builder builder )
    {
        Preconditions.checkNotNull( builder.id, "TaskId cannot be null" );
        id = builder.id;
        state = builder.state == null ? TaskState.WAITING : builder.state;
        description = builder.description == null ? "" : builder.description;
        progress = builder.progress == null ? TaskProgress.EMPTY : builder.progress;
    }

    public TaskId getId()
    {
        return id;
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

    @Override
    public boolean equals( final Object o )
    {
        if ( !( o instanceof TaskInfo ) )
        {
            return false;
        }

        final TaskInfo taskInfo = (TaskInfo) o;
        return Objects.equals( id, taskInfo.id ) && Objects.equals( description, taskInfo.description ) && state == taskInfo.state &&
            Objects.equals( progress, taskInfo.progress );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, description, state, progress );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "id", id ).
            add( "description", description ).
            add( "state", state ).
            add( "progress", progress ).
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

        private TaskState state;

        private String description;

        private TaskProgress progress;

        private Builder()
        {
        }

        private Builder( final TaskInfo source )
        {
            id = source.id;
            state = source.state;
            description = source.description;
            progress = source.progress;
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

        public TaskInfo build()
        {
            return new TaskInfo( this );
        }
    }

}
