package com.enonic.xp.core.internal.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A reference that waits for value to be initialized before it can be read.
 *
 * @param <T> The result type returned by this reference {@link #get(long, TimeUnit)} and {@link #getNow(Object)} methods.
 */
public class DynamicReference<T>
{
    private volatile CompletableFuture<T> completableFuture;

    private final Object lock = new Object();

    /**
     * Creates the reference to uninitialized (value not set) state.
     */
    public DynamicReference()
    {
        this.completableFuture = new CompletableFuture<>();
    }

    /**
     * Waits if necessary for at most the given time for value
     * to be set, and then returns its result, if available.
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return the result value
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws TimeoutException     if the wait timed out
     */
    public T get( long timeout, TimeUnit unit )
        throws InterruptedException, TimeoutException
    {
        try
        {
            return completableFuture.get( timeout, unit );
        }
        catch ( ExecutionException e )
        {
            throw new RuntimeException( e );
        }
    }

    /**
     * Returns the result value if set, else returns the given valueIfAbsent.
     *
     * @param valueIfAbsent valueIfAbsent the value to return if not set
     * @return the result value, if set, else the given valueIfAbsent
     */
    public T getNow( final T valueIfAbsent )
    {
        return completableFuture.getNow( valueIfAbsent );
    }

    /**
     * Sets the value returned by {@link #get(long, TimeUnit)} and {@link #getNow(Object)} to the given value.
     * If value is already set, overrides it with the new value.
     *
     * @param value the result value
     */
    public void set( final T value )
    {
        synchronized ( lock )
        {
            if ( !completableFuture.complete( value ) )
            {
                completableFuture = CompletableFuture.completedFuture( value );
            }
        }
    }

    /**
     * Resets the reference to its uninitialized (value not set) state.
     */
    public void reset()
    {
        synchronized ( lock )
        {
            if ( completableFuture.isDone() )
            {
                completableFuture = new CompletableFuture<>();
            }
        }
    }
}
