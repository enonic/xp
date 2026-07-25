package com.enonic.xp.script.graal.executor;

import java.util.concurrent.Semaphore;

/**
 * Global (cross-application) budget for GraalJS contexts. Request-serving slots grow lazily
 * within {@code maxContexts} (every application is always allowed its first slot, so a full
 * budget can never lock a new application out). Contexts retained by live connections
 * (websocket/SSE) are additionally capped by {@code maxRetainedContexts}: connections compete
 * with each other for capacity, never with request serving — the marginal connection is
 * rejected at open instead of the next request failing. Isolated per-invocation contexts
 * ({@code executeMethod}, named tasks) are bounded by {@code maxIsolatedContexts} — their
 * threads are virtual and park cheaply while waiting.
 */
public final class GraalContextBudget
{
    private final Semaphore contextPermits;

    private final Semaphore retainedPermits;

    private final Semaphore isolatedPermits;

    public GraalContextBudget( final int maxContexts, final int maxRetainedContexts, final int maxIsolatedContexts )
    {
        this.contextPermits = new Semaphore( maxContexts );
        this.retainedPermits = new Semaphore( maxRetainedContexts );
        this.isolatedPermits = new Semaphore( maxIsolatedContexts, true );
    }

    public static GraalContextBudget unlimited()
    {
        return new GraalContextBudget( Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE );
    }

    boolean tryAcquireContext()
    {
        return contextPermits.tryAcquire();
    }

    void releaseContexts( final int permits )
    {
        if ( permits > 0 )
        {
            contextPermits.release( permits );
        }
    }

    /**
     * One permit per live connection (not per slot): a slot shared by two connections holds two.
     * Fails immediately when the budget is exhausted — a connection is worthless if its events
     * cannot be served, so it is rejected at open.
     */
    void acquireRetainedContext()
    {
        if ( !retainedPermits.tryAcquire() )
        {
            throw new IllegalStateException(
                "Cannot retain a script context: the connection budget is exhausted (xp.script-engine.graal.max-retained-contexts)" );
        }
    }

    void releaseRetainedContext()
    {
        retainedPermits.release();
    }

    void acquireIsolatedContext()
    {
        try
        {
            isolatedPermits.acquire();
        }
        catch ( final InterruptedException e )
        {
            Thread.currentThread().interrupt();
            throw new RuntimeException( "Interrupted while waiting for an isolated script context", e );
        }
    }

    void releaseIsolatedContext()
    {
        isolatedPermits.release();
    }
}
