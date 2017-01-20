package com.enonic.xp.server.internal.trace.event;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.trace.TraceEvent;
import com.enonic.xp.trace.TraceListener;

public class TraceEventDispatcherImplTest
{
    private TraceEventDispatcherImpl dispatcher;

    @Before
    public void setUp()
    {
        this.dispatcher = new TraceEventDispatcherImpl();
        this.dispatcher.activate();
    }

    @After
    public void tearDown()
    {
        this.dispatcher.deactivate();
    }

    @Test
    public void testQueue()
        throws Exception
    {
        final TraceListener listener = Mockito.mock( TraceListener.class );
        this.dispatcher.addListener( listener );

        final TraceEvent event = TraceEvent.start( null );
        this.dispatcher.queue( event );

        Thread.sleep( 100L );
        Mockito.verify( listener, Mockito.times( 1 ) ).onTrace( event );

        this.dispatcher.removeListener( listener );
        this.dispatcher.queue( event );

        Thread.sleep( 100L );
        Mockito.verify( listener, Mockito.times( 1 ) ).onTrace( event );
    }
}
