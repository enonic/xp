package com.enonic.xp.lib.scheduler;

import com.google.common.base.Preconditions;

import com.enonic.xp.scheduler.ScheduledJobName;

public final class DeleteScheduledJobHandler
    extends BaseSchedulerHandler
{
    private ScheduledJobName name;

    @Override
    protected Boolean doExecute()
    {
        return this.schedulerService.get().delete( name );
    }

    @Override
    protected void validate()
    {
        Preconditions.checkArgument( name != null && !name.getValue().isBlank(), "name must be set" );
    }

    public void setName( final String value )
    {
        name = ScheduledJobName.from( value );
    }

}
