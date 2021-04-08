package com.enonic.xp.impl.scheduler;

import com.google.common.base.Preconditions;

import com.enonic.xp.scheduler.ScheduledJobName;

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
            Preconditions.checkNotNull( schedulerExecutorService, "schedulerExecutorService must be set." );
            Preconditions.checkNotNull( name, "name must be set." );
        }

        public UnscheduleJobCommand build()
        {
            validate();
            return new UnscheduleJobCommand( this );
        }
    }
}
