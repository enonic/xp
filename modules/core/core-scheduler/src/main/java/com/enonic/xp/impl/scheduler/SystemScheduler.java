package com.enonic.xp.impl.scheduler;

import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.enonic.xp.impl.scheduler.distributed.SchedulableTask;

interface SystemScheduler
{
    void dispose( String name );

    Optional<? extends ScheduledFuture<?>> get( String name);

    ScheduledFuture<?> scheduleAtFixedRate( SchedulableTask command, long initialDelay, long period, TimeUnit unit );
}
