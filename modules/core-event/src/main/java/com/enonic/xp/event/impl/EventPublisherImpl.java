package com.enonic.xp.event.impl;

import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import com.enonic.wem.api.event.Event;
import com.enonic.wem.api.event.EventListener;
import com.enonic.wem.api.event.EventPublisher;

@Component(immediate = true)
public final class EventPublisherImpl
    implements EventPublisher
{
    private final static Logger LOG = LoggerFactory.getLogger( EventPublisherImpl.class );

    private final Set<EventListener> listeners;

    public EventPublisherImpl()
    {
        this.listeners = Sets.newConcurrentHashSet();
    }

    @Override
    public void publish( final Event event )
    {
        for ( EventListener eventListener : this.listeners )
        {
            doPublish( eventListener, event );
        }
    }

    private void doPublish( final EventListener eventListener, final Event event )
    {
        try
        {
            eventListener.onEvent( event );
        }
        catch ( final Throwable t )
        {
            LOG.error( "Failed to deliver event [" + event.getClass().getName() + "] to [" + eventListener + "]", t );
        }
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addListener( final EventListener listener )
    {
        this.listeners.add( listener );
    }

    public void removeListener( final EventListener listener )
    {
        this.listeners.remove( listener );
    }
}
