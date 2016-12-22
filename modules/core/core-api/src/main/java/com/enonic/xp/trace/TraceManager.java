package com.enonic.xp.trace;

public interface TraceManager2
{
    void start( Trace2 trace );

    void end( Trace2 trace );

    Trace2 newTrace( String type, Trace2 parent );
}
