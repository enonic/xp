package com.enonic.xp.lib.scheduler;

import java.util.Optional;

import com.google.common.base.Preconditions;

import com.enonic.xp.lib.scheduler.mapper.ScheduledJobMapper;
import com.enonic.xp.scheduler.SchedulerName;

public final class GetScheduledJobHandler
    extends BaseSchedulerHandler
{
    private SchedulerName name;

    @Override
    protected ScheduledJobMapper doExecute()
    {
        return Optional.ofNullable( this.schedulerService.get().get( name ) ).
            map( ScheduledJobMapper::from ).
            orElse( null );
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
