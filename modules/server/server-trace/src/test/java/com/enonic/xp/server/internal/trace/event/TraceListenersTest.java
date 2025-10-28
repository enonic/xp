package com.enonic.xp.server.internal.trace.event;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.trace.TraceEvent;
import com.enonic.xp.trace.TraceListener;

class TraceListenersTest
{
    @Test
    void testAddRemove()
    {
        final TraceEvent event = TraceEvent.start( null );
        final TraceListeners listeners = new TraceListeners();

        listeners.onTrace( event );

        final TraceListener listener1 = Mockito.mock( TraceListener.class );
        listeners.add( listener1 );

        listeners.onTrace( event );
        Mockito.verify( listener1, Mockito.times( 1 ) ).onTrace( event );

        final TraceListener listener2 = Mockito.mock( TraceListener.class );
        listeners.add( listener2 );

        listeners.onTrace( event );
        Mockito.verify( listener1, Mockito.times( 2 ) ).onTrace( event );
        Mockito.verify( listener2, Mockito.times( 1 ) ).onTrace( event );

        listeners.remove( listener2 );

        listeners.onTrace( event );
        Mockito.verify( listener1, Mockito.times( 3 ) ).onTrace( event );
        Mockito.verify( listener2, Mockito.times( 1 ) ).onTrace( event );
    }
}
