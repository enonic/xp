package com.enonic.xp.impl.task;

import java.time.Instant;

import com.enonic.xp.task.TaskInfo;

final class TaskInfoHolder
{
    private final TaskInfo taskInfo;

    private final Instant doneTime;

    private TaskInfoHolder( final Builder builder )
    {
        taskInfo = builder.taskInfo;
        doneTime = builder.doneTime;
    }

    public TaskInfo getTaskInfo()
    {
        return taskInfo;
    }

    public Instant getDoneTime()
    {
        return doneTime;
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
        private TaskInfo taskInfo;

        private Instant doneTime;

        private Builder()
        {
        }

        private Builder( final TaskInfoHolder source )
        {
            taskInfo = source.taskInfo;
            doneTime = source.doneTime;
        }

        public Builder taskInfo( final TaskInfo taskInfo )
        {
            this.taskInfo = taskInfo;
            return this;
        }

        public Builder doneTime( final Instant doneTime )
        {
            this.doneTime = doneTime;
            return this;
        }

        public TaskInfoHolder build()
        {
            return new TaskInfoHolder( this );
        }
    }
}
