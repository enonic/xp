package com.enonic.xp.lib.export;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class FunctionBasedNodeExportListenerTest
{
    @Test
    void call_functions()
    {
        AtomicLong nodesResolved = new AtomicLong();
        AtomicLong nodesImported = new AtomicLong();

        final FunctionBasedNodeExportListener functionBasedNodeImportListener = new FunctionBasedNodeExportListener( i -> {
            nodesImported.addAndGet( i );
            return null;
        }, i -> {
            nodesResolved.addAndGet( i );
            return null;
        } );

        functionBasedNodeImportListener.nodeResolved( 10 );
        functionBasedNodeImportListener.nodeExported( 2 );
        assertEquals( 10, nodesResolved.get() );
        assertEquals( 2, nodesImported.get() );
    }

    @Test
    void null_safe()
    {
        final FunctionBasedNodeExportListener functionBasedNodeImportListener = new FunctionBasedNodeExportListener( null, null );
        functionBasedNodeImportListener.nodeResolved( 10 );
        functionBasedNodeImportListener.nodeExported( 2 );
    }
}
