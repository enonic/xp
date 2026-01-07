package com.enonic.xp.core.internal.concurrent;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jspecify.annotations.NullMarked;

/**
 * A "one size fits all" {@link Executor} for OSGi services
 */
@NullMarked
public final class SimpleExecutor
    implements Executor
{
    private final ExecutorService executorService;

    private SimpleExecutor( final ExecutorService executorService )
    {
        this.executorService = executorService;
    }

    /**
     * Constructs {@linkplain SimpleExecutor} with customized thread naming pattern and uncaught exception handler.
     *
     * @param executorServiceSupplier  function to be executed to construct {@link ExecutorService}
     *                                 Usually one of {@link java.util.concurrent.Executors} methods
     * @param namePattern              Example {@code "my-service-%d"}
     * @param uncaughtExceptionHandler should be used primarily for logging of uncaught Exceptions
     */
    public SimpleExecutor( final Function<ThreadFactory, ExecutorService> executorServiceSupplier, final String namePattern,
                           final Consumer<Throwable> uncaughtExceptionHandler )
    {
        this( executorServiceSupplier.apply( new ThreadFactoryImpl( namePattern, uncaughtExceptionHandler ) ) );
    }

    /**
     * Constructs {@linkplain SimpleExecutor} with virtual threads with customized name and uncaught exception handler.
     *
     * @param namePrefix               prefix for virtual thread names.
     * @param uncaughtExceptionHandler should be used primarily for logging of uncaught Exceptions
     * @return constructed {@link SimpleExecutor} configured with a new-virtual-thread-per-task executor
     */
    public static SimpleExecutor ofVirtual( final String namePrefix, final Consumer<Throwable> uncaughtExceptionHandler )
    {
        Objects.requireNonNull( uncaughtExceptionHandler, "uncaughtExceptionHandler is required" );
        return new SimpleExecutor( Executors.newThreadPerTaskExecutor( Thread.ofVirtual()
                                                                           .name( namePrefix, 0 )
                                                                           .uncaughtExceptionHandler( ( _, e ) -> uncaughtExceptionHandler.accept( e ) )
                                                                           .factory() ) );
    }

    /**
     * Creates a {@link SimpleExecutor} using a single-threaded executor with a customized thread name and
     * uncaught exception handler.
     *
     * @param name the name pattern for threads created by the executor
     * @param uncaughtExceptionHandler should be used primarily for logging of uncaught Exceptions
     * @return a {@link SimpleExecutor} configured with a single-threaded executor
     */
    public static SimpleExecutor ofSingle( final String name, final Consumer<Throwable> uncaughtExceptionHandler )
    {
        Objects.requireNonNull( uncaughtExceptionHandler, "uncaughtExceptionHandler is required" );
        return new SimpleExecutor( Executors.newSingleThreadExecutor( new ThreadFactoryImpl( name, uncaughtExceptionHandler ) ) );
    }

    /**
     * Shut downs {@link ExecutorService} and awaits its termination for specified duration.
     * Allows to warn about not tasks awaiting execution via neverCommenced.
     *
     * @param awaitTerminationDuration time to await termination.
     * @param neverCommenced           consumer of tasks that never commenced execution. Called only if such tasks exist.
     * @return Returns {@code true} if all tasks have completed following shut down.
     */
    public boolean shutdownAndAwaitTermination( final Duration awaitTerminationDuration, final Consumer<List<Runnable>> neverCommenced )
    {
        executorService.shutdown();
        try
        {
            if ( !executorService.awaitTermination( awaitTerminationDuration.toMillis(), TimeUnit.MILLISECONDS ) )
            {
                shutdownNowAndReportNeverCommenced( neverCommenced );
            }
        }
        catch ( InterruptedException e )
        {
            Thread.currentThread().interrupt();
            shutdownNowAndReportNeverCommenced( neverCommenced );
        }
        return executorService.isTerminated();
    }

    private void shutdownNowAndReportNeverCommenced( final Consumer<List<Runnable>> neverCommencedConsumer )
    {
        final List<Runnable> neverCommenced = executorService.shutdownNow();
        if ( !neverCommenced.isEmpty() )
        {
            neverCommencedConsumer.accept( neverCommenced );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute( final Runnable command )
    {
        executorService.execute( command );
    }
}
