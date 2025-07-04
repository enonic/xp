package com.enonic.xp.task;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class SubmitLocalTaskParams
{
    private final RunnableTask runnableTask;

    private final String name;

    private final String description;

    private SubmitLocalTaskParams( final Builder builder )
    {
        this.runnableTask = Objects.requireNonNull( builder.runnableTask, "runnableTask is required" );
        this.name = builder.name;
        this.description = builder.description;
    }

    public RunnableTask getRunnableTask()
    {
        return runnableTask;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private RunnableTask runnableTask;

        private String name;

        private String description;

        private Builder()
        {
        }

        public Builder runnableTask( final RunnableTask runnableTask )
        {
            this.runnableTask = runnableTask;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        public SubmitLocalTaskParams build()
        {
            return new SubmitLocalTaskParams( this );
        }
    }
}
