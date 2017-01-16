package com.enonic.xp.server.internal.trace.event;

import com.enonic.xp.trace.TraceEvent;

public interface TraceEventDispatcher
{
    void queue( TraceEvent event );
}
