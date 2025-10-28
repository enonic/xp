package com.enonic.xp.server.internal.trace.event;

import java.util.concurrent.Phaser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.trace.TraceEvent;
import com.enonic.xp.trace.TraceListener;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class TraceEventDispatcherImplTest
{
    private TraceEventDispatcherExecutorImpl executor;

    @BeforeEach
    void setUp()
    {
        executor = new TraceEventDispatcherExecutorImpl();
    }

    @AfterEach
    void tearDown()
    {
        executor.deactivate();
    }

    @Test
    void testQueue()
    {
        final Phaser phaser = new Phaser( 2 );

        TraceEventDispatcherImpl dispatcher = new TraceEventDispatcherImpl( executor );

        final TraceListener listener = Mockito.mock( TraceListener.class );
        dispatcher.addListener( listener );

        // must be the last listener
        dispatcher.addListener( event -> phaser.arriveAndAwaitAdvance() );

        final TraceEvent event = TraceEvent.start( null );
        dispatcher.queue( event );

        phaser.arriveAndAwaitAdvance();

        dispatcher.removeListener( listener );
        dispatcher.queue( event );

        phaser.arriveAndAwaitAdvance();

        verify( listener, times( 1 ) ).onTrace( event );
    }
}
