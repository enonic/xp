package com.enonic.xp.core.internal.concurrent;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A "one size fits all" {@link RecurringJobScheduler} for OSGi services.
 */
public final class SimpleRecurringJobScheduler
    implements RecurringJobScheduler
{
    private final ScheduledExecutorService scheduledExecutorService;

    /**
     * Constructs ManagedRecurringJobScheduler with customized thread naming pattern.
     *
     * @param scheduledExecutorServiceSupplier function to be executed to construct {@link ScheduledExecutorService}.
     *                                         Usually one of {@link java.util.concurrent.Executors} methods.
     * @param namePattern                      Example {@code "my-service-%d"}
     */
    public SimpleRecurringJobScheduler( final Function<ThreadFactory, ScheduledExecutorService> scheduledExecutorServiceSupplier,
                                        final String namePattern )
    {
        scheduledExecutorService = scheduledExecutorServiceSupplier.apply( new ThreadFactoryImpl( namePattern ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecurringJob scheduleWithFixedDelay( final Runnable command, final Duration initialDelay, final Duration delay,
                                                Consumer<Exception> exceptionHandler, Consumer<Throwable> errorHandler )
    {
        final ScheduledFuture<?> scheduledFuture =
            scheduledExecutorService.scheduleWithFixedDelay( new WrappedRunnable( command, exceptionHandler, errorHandler ),
                                                             initialDelay.toMillis(), delay.toMillis(), TimeUnit.MILLISECONDS );

        return new WrappedScheduledFuture( scheduledFuture );
    }

    /**
     * Shuts down {@link ScheduledExecutorService}
     *
     * @return list of tasks that never commenced execution
     */
    public List<Runnable> shutdownNow()
    {
        return scheduledExecutorService.shutdownNow();
    }

    private static class WrappedRunnable
        implements Runnable
    {
        final Runnable command;

        final Consumer<Exception> exceptionHandler;

        final Consumer<Throwable> errorHandler;

        WrappedRunnable( final Runnable command, final Consumer<Exception> exceptionHandler, final Consumer<Throwable> errorHandler )
        {
            this.command = command;
            this.exceptionHandler = exceptionHandler;
            this.errorHandler = errorHandler;
        }

        @Override
        public void run()
        {
            try
            {
                command.run();
            }
            catch ( Exception e )
            {
                // give a chance to log exception
                exceptionHandler.accept( e );
                // continue to run
            }
            catch ( Throwable t )
            {
                // give a chance to log error
                errorHandler.accept( t );
                // exception thrown from Runnable aborts scheduled job.
                // it is set in ScheduledFuture "outcome", but we don't expose it.
                throw t;
            }
        }
    }

    private static class WrappedScheduledFuture
        implements RecurringJob
    {
        final ScheduledFuture<?> scheduledFuture;

        WrappedScheduledFuture( final ScheduledFuture<?> scheduledFuture )
        {
            this.scheduledFuture = scheduledFuture;
        }

        @Override
        public void cancel()
        {
            scheduledFuture.cancel( true );
        }
    }
}
