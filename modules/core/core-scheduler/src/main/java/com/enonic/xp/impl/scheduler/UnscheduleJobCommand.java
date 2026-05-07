package com.enonic.xp.impl.scheduler;

import com.enonic.xp.scheduler.ScheduledJobName;

import static java.util.Objects.requireNonNull;

final class UnscheduleJobCommand
{
    private final SchedulerExecutorService schedulerExecutorService;

    private final ScheduledJobName name;

    private UnscheduleJobCommand( final Builder builder )
    {
        this.schedulerExecutorService = builder.schedulerExecutorService;
        this.name = builder.name;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public void execute()
    {
        schedulerExecutorService.dispose( name.getValue() );
    }

    public static class Builder
    {
        private SchedulerExecutorService schedulerExecutorService;

        private ScheduledJobName name;

        public Builder schedulerExecutorService( final SchedulerExecutorService schedulerExecutorService )
        {
            this.schedulerExecutorService = schedulerExecutorService;
            return this;
        }

        public Builder name( final ScheduledJobName name )
        {
            this.name = name;
            return this;
        }

        private void validate()
        {
            requireNonNull( schedulerExecutorService );
            requireNonNull( name, "name is required" );
        }

        public UnscheduleJobCommand build()
        {
            validate();
            return new UnscheduleJobCommand( this );
        }
    }
}
