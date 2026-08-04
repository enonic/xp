package com.enonic.xp.trace;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface TraceManager
{
    void dispatch( TraceEvent event );

    Trace newTrace( String name, @Nullable Trace parent );

    void enable( boolean enabled );
}
