package com.enonic.xp.core.impl.event;

import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

final class EventMulticaster
{
    private final static Logger LOG = LoggerFactory.getLogger( EventMulticaster.class );

    protected final CopyOnWriteArrayList<EventListener> listeners;

    public EventMulticaster()
    {
        this.listeners = new CopyOnWriteArrayList<>();
    }

    public void add( final EventListener listener )
    {
        this.listeners.add( listener );
        sortListeners();
    }

    public void remove( final EventListener listener )
    {
        this.listeners.remove( listener );
    }

    private void sortListeners()
    {
        Collections.sort( this.listeners, ( o1, o2 ) -> o1.getOrder() - o2.getOrder() );
    }

    public void publish( final Event event )
    {
        for ( final EventListener eventListener : this.listeners )
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
