package com.enonic.xp.core.impl.event;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

final class EventMulticaster
{
    private final static Logger LOG = LoggerFactory.getLogger( EventMulticaster.class );

    private final AtomicReference<List<EventListener>> listeners = new AtomicReference<>( List.of() );

    public void add( final EventListener listener )
    {
        listeners.updateAndGet( previous -> Stream.concat( previous.stream(), Stream.of( listener ) ).
            sorted( Comparator.comparingInt( EventListener::getOrder ) ).
            collect( Collectors.toUnmodifiableList() ) );
    }

    public void remove( final EventListener listener )
    {
        listeners.updateAndGet( previous -> previous.stream().filter( w -> w != listener ).
            sorted( Comparator.comparingInt( EventListener::getOrder ) ).
            collect( Collectors.toUnmodifiableList() ) );
    }

    public void publish( final Event event )
    {
        listeners.get().forEach( eventListener -> publish( eventListener, event ) );
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
