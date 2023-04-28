package com.enonic.xp.impl.scheduler;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;
import com.hazelcast.scheduledexecutor.IScheduledFuture;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;

import com.enonic.xp.impl.scheduler.distributed.SchedulableTask;

@Component(immediate = true)
public final class ClusteredSystemScheduler
    implements SystemScheduler
{
    private final IScheduledExecutorService hazelcastExecutor;

    @Activate
    public ClusteredSystemScheduler( @Reference final HazelcastInstance hazelcastInstance )
    {
        hazelcastExecutor = hazelcastInstance.getScheduledExecutorService( "scheduler" );
    }

    @Override
    public Optional<? extends ScheduledFuture<?>> get( final String name )
    {
        return hazelcastExecutor.getAllScheduledFutures().values().
            stream().
            flatMap( Collection::stream ).
            filter( future -> {
                final ScheduledTaskHandler handler = future.getHandler();
                return handler != null && handler.getTaskName().equals( name );
            }).
            findAny();
    }

    @Override
    public void dispose( final String name )
    {
        hazelcastExecutor.getAllScheduledFutures().values().stream().
            flatMap( Collection::stream ).
            filter( future -> {
                final ScheduledTaskHandler handler = future.getHandler();
                return handler != null && name.equals( handler.getTaskName() );
            } ).
            findAny().
            ifPresent( IScheduledFuture::dispose );
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate( final SchedulableTask task, final long initialDelay, final long period,
                                                   final TimeUnit unit )
    {
        return hazelcastExecutor.scheduleAtFixedRate( task, initialDelay, period, unit );
    }
}
