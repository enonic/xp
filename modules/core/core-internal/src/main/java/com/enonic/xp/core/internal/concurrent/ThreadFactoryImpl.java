package com.enonic.xp.core.internal.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Thread factory for {@link Executors} which customizes thread naming and {@link Thread.UncaughtExceptionHandler}
 */
final class ThreadFactoryImpl
    implements ThreadFactory
{
    private final AtomicLong count = new AtomicLong( 1 );

    private final String namePattern;

    private final Consumer<Throwable> uncaughtExceptionHandler;

    /**
     * Makes ThreadFactory which customizes thread naming only
     *
     * @param namePattern Example {@code "my-service-%d"}
     */
    public ThreadFactoryImpl( final String namePattern )
    {
        this.namePattern = namePattern;
        this.uncaughtExceptionHandler = null;
    }

    /**
     * Makes ThreadFactory which customizes thread naming and {@link Thread.UncaughtExceptionHandler}
     * For Executors which are returning Futures setting {@link Thread.UncaughtExceptionHandler} is misleading and confusing.
     * <p>
     * uncaughtExceptionHandler should be used primarily for logging of uncaught Exceptions
     * <p>
     * Use {@link ThreadFactoryImpl#ThreadFactoryImpl(java.lang.String)} instead.
     *
     * @param namePattern              Example {@code "my-service-%d"}
     * @param uncaughtExceptionHandler Throwable consumer
     */
    public ThreadFactoryImpl( final String namePattern, final Consumer<Throwable> uncaughtExceptionHandler )
    {
        this.namePattern = namePattern;
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }

    /**
     * Constructs a new {@link Thread} with name and {@link Thread.UncaughtExceptionHandler} set
     *
     * @param r a runnable to be executed by new thread instance
     * @return constructed thread
     */
    @Override
    public Thread newThread( final Runnable r )
    {
        final Thread thread = Executors.defaultThreadFactory().newThread( r );

        thread.setName( String.format( namePattern, count.getAndIncrement() ) );

        if ( uncaughtExceptionHandler != null )
        {
            thread.setUncaughtExceptionHandler( ( t, e ) -> uncaughtExceptionHandler.accept( e ) );
        }
        return thread;
    }
}
