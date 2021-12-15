package com.enonic.xp.impl.scheduler;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.enonic.xp.impl.scheduler.distributed.SchedulableTask;

public interface SchedulerExecutorService
{
    void dispose( String name );

    void disposeAllDone();

    Set<String> getAllFutures();

    Optional<? extends ScheduledFuture<?>> get( String name);

    ScheduledFuture<?> schedule( SchedulableTask command, long delay, TimeUnit unit );

    ScheduledFuture<?> scheduleAtFixedRate( SchedulableTask command, long initialDelay, long period, TimeUnit unit );
}
