package com.enonic.xp.impl.scheduler;

import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.core.internal.Local;
import com.enonic.xp.core.internal.concurrent.ThreadFactoryImpl;
import com.enonic.xp.impl.scheduler.distributed.SchedulableTask;

@Component(immediate = true)
@Local
public final class LocalSystemScheduler
    implements SystemScheduler
{
    private static final Logger LOG = LoggerFactory.getLogger( LocalSystemScheduler.class );

    private static final int CORE_POOL_SIZE = 1;

    private final ScheduledExecutorService simpleExecutor;

    private final ConcurrentHashMap<String, ScheduledFuture<?>> scheduledFutures = new ConcurrentHashMap<>();

    @Activate
    public LocalSystemScheduler()
    {
        simpleExecutor = new ScheduledThreadPoolExecutor( CORE_POOL_SIZE, new ThreadFactoryImpl( "local-system-scheduler-thread-%d" ) )
        {
            @Override
            protected void afterExecute( final Runnable runnable, Throwable throwable )
            {
                super.afterExecute( runnable, throwable );

                Throwable error = throwable;

                if ( error == null && runnable instanceof Future<?> && ( (Future<?>) runnable ).isDone() )
                {
                    try
                    {
                        ( (Future<?>) runnable ).get();
                    }
                    catch ( CancellationException ce )
                    {
                        LOG.debug( "Job was cancelled.", ce );
                    }
                    catch ( ExecutionException ee )
                    {
                        error = ee.getCause();
                    }
                    catch ( InterruptedException ie )
                    {
                        Thread.currentThread().interrupt();
                    }
                }

                if ( error != null )
                {
                    LOG.warn( "Error while running job. " + runnable, error );
                }
            }
        };
    }

    @Deactivate
    public void deactivate()
    {
        simpleExecutor.shutdownNow();
    }


    @Override
    public Optional<? extends ScheduledFuture<?>> get( final String name )
    {
        return Optional.ofNullable( scheduledFutures.get( name ) );
    }

    @Override
    public void dispose( final String name )
    {
        final ScheduledFuture<?> future = scheduledFutures.remove( name );
        if ( future != null )
        {
            future.cancel( false );
        }
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate( final SchedulableTask task, final long initialDelay, final long period,
                                                   final TimeUnit unit )
    {
        return scheduledFutures.compute( task.getName(), ( name, value ) -> {
            if ( value != null )
            {
                throw new IllegalStateException( String.format( "Task for [%s] job is scheduled already.", name ) );
            }
            return simpleExecutor.scheduleAtFixedRate( task, initialDelay, period, unit );
        } );
    }

}
