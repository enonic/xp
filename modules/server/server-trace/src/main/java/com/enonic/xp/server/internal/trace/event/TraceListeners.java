package com.enonic.xp.server.internal.trace.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.enonic.xp.trace.TraceEvent;
import com.enonic.xp.trace.TraceListener;

final class TraceListeners
    implements TraceListener
{
    private final List<TraceListener> list = new CopyOnWriteArrayList<>();

    void add( final TraceListener listener )
    {
        this.list.add( listener );
    }

    void remove( final TraceListener listener )
    {
        this.list.remove( listener );
    }

    @Override
    public void onTrace( final TraceEvent event )
    {
        for ( final TraceListener listener : this.list )
        {
            listener.onTrace( event );
        }
    }
}
