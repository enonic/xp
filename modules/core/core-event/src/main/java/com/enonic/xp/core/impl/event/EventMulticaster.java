package com.enonic.xp.core.impl.event;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

final class EventMulticaster
{
    private static final Logger LOG = LoggerFactory.getLogger( EventMulticaster.class );

    private final AtomicReference<List<EventListener>> listenersRef = new AtomicReference<>( List.of() );

    public void add( final EventListener listener )
    {
        updateAndSortListeners( stream -> Stream.concat( stream, Stream.of( listener ) ) );
    }

    public void remove( final EventListener listener )
    {
        updateAndSortListeners( stream -> stream.filter( w -> w != listener ) );
    }

    private void updateAndSortListeners( final UnaryOperator<Stream<EventListener>> updateFunction )
    {
        listenersRef.updateAndGet( oldListeners -> updateFunction.apply( oldListeners.stream() ).
            sorted( Comparator.comparingInt( EventListener::getOrder ) ).
            collect( Collectors.toUnmodifiableList() ) );
    }

    public void publish( final Event event )
    {
        for ( final EventListener eventListener : this.listenersRef.get() )
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
