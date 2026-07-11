package com.enonic.xp.launcher.impl.weaver;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.enonic.xp.trace.TestTrace;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.TraceEvent;
import com.enonic.xp.trace.TraceManager;

/**
 * TraceManager that records created traces and dispatched events for assertions.
 */
final class RecordingTraceManager
    implements TraceManager
{
    final List<TestTrace> traces = new CopyOnWriteArrayList<>();

    final List<TraceEvent> events = new CopyOnWriteArrayList<>();

    @Override
    public Trace newTrace( final String name, final Trace parent )
    {
        final TestTrace trace = TestTrace.of( name, parent != null ? parent.getId() : null );
        this.traces.add( trace );
        return trace;
    }

    @Override
    public void dispatch( final TraceEvent event )
    {
        this.events.add( event );
    }

    @Override
    public void enable( final boolean enabled )
    {
    }

    TestTrace singleTrace()
    {
        if ( this.traces.size() != 1 )
        {
            throw new AssertionError( "Expected exactly one trace, got " + this.traces );
        }
        return this.traces.get( 0 );
    }
}
