package com.enonic.xp.impl.scheduler;

import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.enonic.xp.impl.scheduler.distributed.SchedulableTask;

interface SystemScheduler
{
    void dispose( String name );

    void disposeAllDone();

    Set<String> getAllFutures();

    ScheduledFuture<?> schedule( SchedulableTask command, long delay, TimeUnit unit );

    ScheduledFuture<?> scheduleAtFixedRate( SchedulableTask command, long initialDelay, long period, TimeUnit unit );


}
