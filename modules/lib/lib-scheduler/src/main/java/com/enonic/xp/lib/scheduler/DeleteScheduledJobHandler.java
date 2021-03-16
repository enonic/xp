package com.enonic.xp.lib.scheduler;

import com.google.common.base.Preconditions;

import com.enonic.xp.scheduler.SchedulerName;

public final class DeleteScheduledJobHandler
    extends BaseSchedulerHandler
{
    private SchedulerName name;

    @Override
    protected Boolean doExecute()
    {
        return this.schedulerService.get().delete( name );
    }

    @Override
    protected void validate()
    {
        Preconditions.checkArgument( name != null && !name.getValue().isBlank(), "name must be set." );
    }

    public void setName( final String value )
    {
        name = SchedulerName.from( value );
    }

}
