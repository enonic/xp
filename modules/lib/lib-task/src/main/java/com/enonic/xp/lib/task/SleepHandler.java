package com.enonic.xp.lib.task;

import java.util.concurrent.TimeUnit;

public final class SleepHandler
{
    private Double timeMillis;

    public void setTimeMillis( final Double timeMillis )
    {
        this.timeMillis = timeMillis;
    }

    public void sleep()
        throws InterruptedException
    {
        if ( timeMillis == null )
        {
            return;
        }
        TimeUnit.MILLISECONDS.sleep( timeMillis.longValue() );
    }
}
