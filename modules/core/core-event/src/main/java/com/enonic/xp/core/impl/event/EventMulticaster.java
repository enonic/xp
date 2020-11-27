package com.enonic.xp.core.impl.event;

import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.core.internal.concurrent.AtomicSortedList;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

final class EventMulticaster
{
    private static final Logger LOG = LoggerFactory.getLogger( EventMulticaster.class );

    private final AtomicSortedList<EventListener> listeners = new AtomicSortedList<>( Comparator.comparingInt( EventListener::getOrder ) );

    public void add( final EventListener listener )
    {
        listeners.add( listener );
    }

    public void remove( final EventListener listener )
    {
        listeners.remove( listener );
    }

    public void publish( final Event event )
    {
        for ( final EventListener eventListener : this.listeners.snapshot() )
        {
            publish( eventListener, event );
        }
    }

    private void publish( final EventListener listener, final Event event )
    {
        try
        {
            listener.onEvent( event );
        }
        catch ( final Exception t )
        {
            LOG.warn( "Uncaught exception during event processing", t );
        }
    }
}
