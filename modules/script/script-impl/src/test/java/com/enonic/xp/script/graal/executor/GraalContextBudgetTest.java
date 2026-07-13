package com.enonic.xp.script.graal.executor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraalContextBudgetTest
{
    @Test
    void contextPermitsAreExhaustibleAndReleasable()
    {
        final GraalContextBudget budget = new GraalContextBudget( 1, 1 );

        assertTrue( budget.tryAcquireContext() );
        assertFalse( budget.tryAcquireContext() );

        budget.releaseContexts( 0 );
        assertFalse( budget.tryAcquireContext() );

        budget.releaseContexts( 1 );
        assertTrue( budget.tryAcquireContext() );
    }

    @Test
    void unlimitedNeverExhausts()
    {
        final GraalContextBudget budget = GraalContextBudget.unlimited();
        for ( int i = 0; i < 1000; i++ )
        {
            assertTrue( budget.tryAcquireContext() );
        }
        budget.acquireTaskContext();
        budget.releaseTaskContext();
    }

    @Test
    void interruptedTaskContextWaitRestoresInterruptFlag()
        throws Exception
    {
        final GraalContextBudget budget = new GraalContextBudget( 1, 1 );
        budget.acquireTaskContext();

        try
        {
            Thread.currentThread().interrupt();
            assertThrows( RuntimeException.class, budget::acquireTaskContext );
            assertTrue( Thread.currentThread().isInterrupted(), "interrupt flag must be restored" );
        }
        finally
        {
            // clear the flag so it does not leak into other tests on this worker thread
            Thread.interrupted();
        }
    }
}
