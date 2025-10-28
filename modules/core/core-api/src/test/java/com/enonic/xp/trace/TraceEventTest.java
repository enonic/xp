package com.enonic.xp.trace;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class TraceEventTest
{
    @Test
    void startEvent()
    {
        final Trace trace = Mockito.mock( Trace.class );

        final TraceEvent event = TraceEvent.start( trace );
        assertNotNull( event );
        assertSame( TraceEvent.Type.START, event.getType() );
        assertSame( trace, event.getTrace() );
    }

    @Test
    void endEvent()
    {
        final Trace trace = Mockito.mock( Trace.class );

        final TraceEvent event = TraceEvent.end( trace );
        assertNotNull( event );
        assertSame( TraceEvent.Type.END, event.getType() );
        assertSame( trace, event.getTrace() );
    }
}
