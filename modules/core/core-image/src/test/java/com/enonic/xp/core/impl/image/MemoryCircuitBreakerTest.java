package com.enonic.xp.core.impl.image;

import org.junit.jupiter.api.Test;

import com.enonic.xp.exception.ThrottlingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemoryCircuitBreakerTest
{
    @Test
    void tryAcquire_throttle()
    {
        final MemoryCircuitBreaker memoryCircuitBreaker = new MemoryCircuitBreaker( 10 );

        assertThrows( ThrottlingException.class, () -> memoryCircuitBreaker.tryAcquire( 11 ) );
    }

    @Test
    void softTryAcquire_throttle()
    {
        final MemoryCircuitBreaker memoryCircuitBreaker = new MemoryCircuitBreaker( 10 );

        final int acquired = memoryCircuitBreaker.softTryAcquire( 20 );
        assertEquals( 10, acquired );
        assertThrows( ThrottlingException.class, () -> memoryCircuitBreaker.softTryAcquire( 1 ) );
    }

    @Test
    void release()
    {
        final MemoryCircuitBreaker memoryCircuitBreaker = new MemoryCircuitBreaker( 10 );

        memoryCircuitBreaker.tryAcquire( 10 );
        assertThrows( ThrottlingException.class, () -> memoryCircuitBreaker.tryAcquire( 1 ) );
        memoryCircuitBreaker.release( 1 );
        memoryCircuitBreaker.tryAcquire( 1 );
    }
}
