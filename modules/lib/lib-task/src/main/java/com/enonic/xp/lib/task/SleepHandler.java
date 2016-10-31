package com.enonic.xp.lib.task;

import java.util.concurrent.TimeUnit;

public final class SleepHandler
{
    private Long timeMillis;

    public void setTimeMillis( final Long timeMillis )
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
        TimeUnit.MILLISECONDS.sleep( timeMillis );
    }
}
