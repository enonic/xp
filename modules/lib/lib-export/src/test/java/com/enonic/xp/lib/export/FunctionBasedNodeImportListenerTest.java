package com.enonic.xp.lib.export;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class FunctionBasedNodeImportListenerTest
{
    @Test
    void call_functions()
    {
        AtomicLong nodesResolved = new AtomicLong();
        AtomicLong nodesImported = new AtomicLong();
        AtomicLong nodesSkipped = new AtomicLong();

        final FunctionBasedNodeImportListener functionBasedNodeImportListener = new FunctionBasedNodeImportListener( i -> {
            nodesImported.addAndGet( i );
            return null;
        }, i -> {
            nodesResolved.addAndGet( i );
            return null;
        }, i -> {
            nodesSkipped.addAndGet( i );
            return null;
        } );

        functionBasedNodeImportListener.nodeResolved( 10 );
        functionBasedNodeImportListener.nodeImported( 2 );
        functionBasedNodeImportListener.nodeSkipped( 1 );
        assertEquals( 10, nodesResolved.get() );
        assertEquals( 2, nodesImported.get() );
        assertEquals( 1, nodesSkipped.get() );
    }

    @Test
    void null_safe()
    {
        final FunctionBasedNodeImportListener functionBasedNodeImportListener = new FunctionBasedNodeImportListener( null, null, null );
        functionBasedNodeImportListener.nodeResolved( 10 );
        functionBasedNodeImportListener.nodeImported( 2 );
        functionBasedNodeImportListener.nodeSkipped( 1 );
    }
}
