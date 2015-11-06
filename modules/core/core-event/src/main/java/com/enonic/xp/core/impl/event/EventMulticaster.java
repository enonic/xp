package com.enonic.xp.core.impl.event;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

final class EventMulticaster
{
    private final static Logger LOG = LoggerFactory.getLogger( EventMulticaster.class );

    protected final List<EventListener> listeners;

    public EventMulticaster()
    {
        this.listeners = Lists.newArrayList();
    }

    public synchronized void add( final EventListener listener )
    {
        this.listeners.add( listener );
        sortListeners();
    }

    public synchronized void remove( final EventListener listener )
    {
        this.listeners.remove( listener );
    }

    private void sortListeners()
    {
        Collections.sort( this.listeners, ( o1, o2 ) -> o1.getOrder() - o2.getOrder() );
    }

    public synchronized void publish( final Event event )
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
