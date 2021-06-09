package com.enonic.xp.core.impl.image;

import java.util.concurrent.Semaphore;

import com.enonic.xp.exception.ThrottlingException;

public class MemoryCircuitBreaker
{
    private final Semaphore semaphore;

    private final int maxPermits;

    public MemoryCircuitBreaker( int maxPermits )
    {
        this.maxPermits = maxPermits;
        this.semaphore = new Semaphore( maxPermits );
    }

    public int softTryAcquire( final int permits )
    {
        final int min = Math.min( maxPermits, permits );
        tryAcquire( min );
        return min;
    }

    public void tryAcquire( final int permits )
    {
        final boolean acquired = semaphore.tryAcquire( permits );
        if ( !acquired )
        {
            throw new ThrottlingException( "Insufficient resources" );
        }
    }

    public void release( final int permits )
    {
        semaphore.release( permits );
    }
}
