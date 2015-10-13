package com.enonic.xp.core.impl.event;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

final class EventMulticaster
{
    private final static Logger LOG = LoggerFactory.getLogger( EventMulticaster.class );

    private final Set<EventListener> listeners;

    public EventMulticaster()
    {
        this.listeners = Sets.newConcurrentHashSet();
    }

    public void add( final EventListener listener )
    {
        this.listeners.add( listener );
    }

    public void remove( final EventListener listener )
    {
        this.listeners.remove( listener );
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
