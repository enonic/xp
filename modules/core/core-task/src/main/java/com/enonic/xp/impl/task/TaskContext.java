package com.enonic.xp.impl.task;

import java.time.Instant;

import com.enonic.xp.task.TaskInfo;

final class TaskContext
{
    private final TaskInfo taskInfo;

    private final Instant submitTime;

    private final Instant doneTime;

    private TaskContext( final Builder builder )
    {
        taskInfo = builder.taskInfo;
        submitTime = builder.submitTime;
        doneTime = builder.doneTime;
    }

    public TaskInfo getTaskInfo()
    {
        return taskInfo;
    }

    public Instant getSubmitTime()
    {
        return submitTime;
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

        private Instant submitTime;

        private Instant doneTime;

        private Builder()
        {
        }

        private Builder( final TaskContext source )
        {
            taskInfo = source.taskInfo;
            submitTime = source.submitTime;
            doneTime = source.doneTime;
        }

        public Builder taskInfo( final TaskInfo taskInfo )
        {
            this.taskInfo = taskInfo;
            return this;
        }

        public Builder submitTime( final Instant submitTime )
        {
            this.submitTime = submitTime;
            return this;
        }

        public Builder doneTime( final Instant doneTime )
        {
            this.doneTime = doneTime;
            return this;
        }

        public TaskContext build()
        {
            return new TaskContext( this );
        }
    }
}
