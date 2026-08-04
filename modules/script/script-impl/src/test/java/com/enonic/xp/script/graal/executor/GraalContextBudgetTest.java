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
        final GraalContextBudget budget = new GraalContextBudget( 1, 1, 1 );

        assertTrue( budget.tryAcquireContext() );
        assertFalse( budget.tryAcquireContext() );

        budget.releaseContexts( 0 );
        assertFalse( budget.tryAcquireContext() );

        budget.releaseContexts( 1 );
        assertTrue( budget.tryAcquireContext() );
    }

    @Test
    void retainedPermitsRejectTheMarginalConnection()
    {
        final GraalContextBudget budget = new GraalContextBudget( 1, 1, 1 );

        budget.acquireRetainedContext();
        // one connection is the budget: the next open fails, immediately
        assertThrows( IllegalStateException.class, budget::acquireRetainedContext );

        budget.releaseRetainedContext();
        budget.acquireRetainedContext();
    }

    @Test
    void unlimitedNeverExhausts()
    {
        final GraalContextBudget budget = GraalContextBudget.unlimited();
        for ( int i = 0; i < 1000; i++ )
        {
            assertTrue( budget.tryAcquireContext() );
        }
        budget.acquireRetainedContext();
        budget.releaseRetainedContext();
        budget.acquireIsolatedContext();
        budget.releaseIsolatedContext();
    }

    @Test
    void interruptedIsolatedContextWaitRestoresInterruptFlag()
        throws Exception
    {
        final GraalContextBudget budget = new GraalContextBudget( 1, 1, 1 );
        budget.acquireIsolatedContext();

        try
        {
            Thread.currentThread().interrupt();
            assertThrows( RuntimeException.class, budget::acquireIsolatedContext );
            assertTrue( Thread.currentThread().isInterrupted(), "interrupt flag must be restored" );
        }
        finally
        {
            // clear the flag so it does not leak into other tests on this worker thread
            Thread.interrupted();
        }
    }
}
