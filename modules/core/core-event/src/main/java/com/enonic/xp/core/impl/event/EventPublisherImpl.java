package com.enonic.xp.core.impl.event;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Meter;
import com.google.common.collect.Queues;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.util.Metrics;

@Component(immediate = true)
public final class EventPublisherImpl
    implements EventPublisher
{
    private final static Logger LOG = LoggerFactory.getLogger( EventPublisherImpl.class );

    private final static Meter EVENT_METRIC = Metrics.meter( EventPublisher.class, "event" );

    private final Queue<Event> queue;

    private final EventMulticaster multicaster;

    private final Executor executor;

    public EventPublisherImpl()
    {
        this.queue = Queues.newConcurrentLinkedQueue();
        this.multicaster = new EventMulticaster();
        this.executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void publish( final Event event )
    {
        if ( event != null )
        {
            if ( LOG.isDebugEnabled() )
            {
                LOG.debug( "Publishing event: " + event.toString() );
            }
            EVENT_METRIC.mark();
            this.queue.add( event );
            dispatchEvents();
        }
    }

    private void dispatchEvents()
    {
        while ( true )
        {
            final Event event = this.queue.poll();
            if ( event == null )
            {
                return;
            }

            dispatchEvent( event );
        }
    }

    private void dispatchEvent( final Event event )
    {
        this.executor.execute( () -> this.multicaster.publish( event ) );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addListener( final EventListener listener )
    {
        this.multicaster.add( listener );
    }

    public void removeListener( final EventListener listener )
    {
        this.multicaster.remove( listener );
    }
}
