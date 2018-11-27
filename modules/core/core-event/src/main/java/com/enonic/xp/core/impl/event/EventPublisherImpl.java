package com.enonic.xp.core.impl.event;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

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
    private final static int MAX_THREAD_POOL = 100;

    private final static Meter EVENT_METRIC = Metrics.meter( EventPublisher.class, "event" );

    private final Queue<Event> queue;

    private final EventMulticaster multicaster;

    private final ExecutorService executor;

    public EventPublisherImpl()
    {
        this.queue = Queues.newConcurrentLinkedQueue();
        this.multicaster = new EventMulticaster();
        this.executor = new ThreadPoolExecutor( 0, MAX_THREAD_POOL, 60L, TimeUnit.SECONDS, new SynchronousQueue<>() );
    }

    @Deactivate
    public void deactivate()
    {
        this.executor.shutdown();
    }

    @Override
    public void publish( final Event event )
    {
        if ( event != null )
        {
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
