package com.enonic.wem.core.event;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import com.enonic.wem.api.event.Event;
import com.enonic.wem.api.event.EventListener;
import com.enonic.wem.api.event.EventPublished;
import com.enonic.wem.api.event.EventService;

public class EventServiceImpl
    implements EventService
{
    private final static Logger LOG = LoggerFactory.getLogger( EventServiceImpl.class );

    private final ExecutorService executor;

    private Set<EventListener> subscriptions;

    public EventServiceImpl()
    {
        this.subscriptions = Sets.newConcurrentHashSet();
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public void publish( final Event event )
    {
        for ( EventListener eventListener : this.subscriptions )
        {
            doPublish( eventListener, event );
        }
    }

    @Override
    public void publishAsync( final Event event )
    {
        for ( EventListener eventListener : this.subscriptions )
        {
            this.executor.submit( () -> doPublish( eventListener, event ) );
        }
    }

    @Override
    public void publishAsync( final Event event, final EventPublished onPublished )
    {
        for ( EventListener eventListener : this.subscriptions )
        {
            this.executor.submit( () -> {
                doPublish( eventListener, event );
                onPublished.published( event );
            } );
        }
    }

    private void doPublish( final EventListener eventListener, final Event event )
    {
        try
        {
            eventListener.onEvent( event );
        }
        catch ( Throwable t )
        {
            LOG.error( "Failed to deliver event [" + event.getClass().getName() + "] to [" + eventListener + "]", t );
        }
    }

    @Override
    public void subscribe( final EventListener eventListener )
    {
        Preconditions.checkNotNull( eventListener );
        this.subscriptions.add( eventListener );
    }

    @Override
    public void unsubscribe( final EventListener eventListener )
    {
        Preconditions.checkNotNull( eventListener );
        this.subscriptions.remove( eventListener );
    }
}
