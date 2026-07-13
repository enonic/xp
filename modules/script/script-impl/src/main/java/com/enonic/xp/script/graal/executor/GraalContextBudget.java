package com.enonic.xp.script.graal.executor;

import java.util.concurrent.Semaphore;

/**
 * Global (cross-application) budget for GraalJS contexts. Request-serving slots grow lazily
 * within {@code maxContexts} (every application is always allowed its first slot, so a full
 * budget can never lock a new application out); ephemeral task contexts are bounded by
 * {@code maxTaskContexts} — task threads are virtual and park cheaply while waiting.
 */
public final class GraalContextBudget
{
    private final Semaphore contextPermits;

    private final Semaphore taskContextPermits;

    public GraalContextBudget( final int maxContexts, final int maxTaskContexts )
    {
        this.contextPermits = new Semaphore( maxContexts );
        this.taskContextPermits = new Semaphore( maxTaskContexts, true );
    }

    public static GraalContextBudget unlimited()
    {
        return new GraalContextBudget( Integer.MAX_VALUE, Integer.MAX_VALUE );
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

    void acquireTaskContext()
    {
        try
        {
            taskContextPermits.acquire();
        }
        catch ( final InterruptedException e )
        {
            Thread.currentThread().interrupt();
            throw new RuntimeException( "Interrupted while waiting for a task script context", e );
        }
    }

    void releaseTaskContext()
    {
        taskContextPermits.release();
    }
}
