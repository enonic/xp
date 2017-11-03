package com.enonic.xp.trace;

public interface TraceManager
{
    void dispatch( TraceEvent event );

    Trace newTrace( String name, Trace parent );

    void enable( boolean enabled );
}
