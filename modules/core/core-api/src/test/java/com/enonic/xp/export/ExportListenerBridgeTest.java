package com.enonic.xp.export;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The deprecated total forwards from {@code resolved} and not the other way round: producers call {@code resolved}, so a listener that
 * implements only the old name would stop hearing anything the moment that direction were inverted.
 */
class ExportListenerBridgeTest
{
    @Test
    void export_listener_of_the_old_name_alone_still_hears_the_total()
    {
        final AtomicInteger heard = new AtomicInteger();

        final NodeExportListener listener = new NodeExportListener()
        {
            @Override
            public void nodeExported( final int count )
            {
            }

            @Override
            @SuppressWarnings("deprecation")
            public void nodeResolved( final int count )
            {
                heard.set( count );
            }
        };

        listener.resolved( 7 );

        assertEquals( 7, heard.get() );
    }

    @Test
    void import_listener_of_the_old_name_alone_still_hears_the_total()
    {
        final AtomicInteger heard = new AtomicInteger();

        final NodeImportListener listener = new NodeImportListener()
        {
            @Override
            public void nodeImported( final int count )
            {
            }

            @Override
            public void nodeSkipped( final int count )
            {
            }

            @Override
            @SuppressWarnings("deprecation")
            public void nodeResolved( final int count )
            {
                heard.set( count );
            }
        };

        listener.resolved( 7 );

        assertEquals( 7, heard.get() );
    }
}
