package com.enonic.xp.trace;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class TraceEventTest
{
    @Test
    public void startEvent()
    {
        final Trace trace = Mockito.mock( Trace.class );

        final TraceEvent event = TraceEvent.start( trace );
        assertNotNull( event );
        assertSame( TraceEvent.Type.START, event.getType() );
        assertSame( trace, event.getTrace() );
    }

    @Test
    public void endEvent()
    {
        final Trace trace = Mockito.mock( Trace.class );

        final TraceEvent event = TraceEvent.end( trace );
        assertNotNull( event );
        assertSame( TraceEvent.Type.END, event.getType() );
        assertSame( trace, event.getTrace() );
    }
}
