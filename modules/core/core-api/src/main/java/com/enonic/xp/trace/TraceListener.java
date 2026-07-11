package com.enonic.xp.trace;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface TraceListener
{
    void onTrace( TraceEvent event );
}
