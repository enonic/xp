package com.enonic.xp.core.impl.event;

import java.util.concurrent.Executor;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.metrics.Counter;
import com.enonic.xp.metrics.Metrics;

@Component(immediate = true)
public final class EventPublisherImpl
    implements EventPublisher
{
    private static final Logger LOG = LoggerFactory.getLogger( EventPublisherImpl.class );

    private static final Counter EVENT_METRIC = Metrics.counter( EventPublisher.class.getName() +  ".event" );

    private final EventMulticaster multicaster = new EventMulticaster();

    private final Executor executor;

    @Activate
    public EventPublisherImpl( @Reference(service = EventPublisherExecutor.class) final Executor executor )
    {
        this.executor = executor;
    }

    @Override
    public void publish( final Event event )
    {
        if ( event != null )
        {
            LOG.debug( "Publishing event: {}", event );
            EVENT_METRIC.increment();

            dispatchEvent( event );
        }
    }

    private void dispatchEvent( final Event event )
    {
        executor.execute( () -> this.multicaster.publish( event ) );
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
