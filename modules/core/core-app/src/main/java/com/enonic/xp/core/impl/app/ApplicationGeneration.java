package com.enonic.xp.core.impl.app;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.event.ApplicationEvents;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

/**
 * The generation of every application on this cluster node: a counter that moves whenever the definition of the application
 * changes, its bundle, its descriptor or its schema. Values derived from the resources of an application are cached under its
 * generation and are recomputed once it has moved.
 * <p>
 * The generation lives in memory only. It is bumped synchronously by the services writing the change on this node, so a read
 * following a write sees the change, and by the {@link ApplicationEvents#UPDATED} event for changes written on the other nodes of
 * the cluster. A node starting up starts from zero, together with the empty caches the generation guards.
 */
@Component(immediate = true, service = {ApplicationGeneration.class, EventListener.class})
public final class ApplicationGeneration
    implements EventListener
{
    private final ConcurrentMap<ApplicationKey, AtomicLong> generations = new ConcurrentHashMap<>();

    /**
     * The current generation of the application, {@code 0} for an application nothing has changed since this node started.
     */
    public long of( final ApplicationKey applicationKey )
    {
        final AtomicLong generation = generations.get( applicationKey );
        return generation == null ? 0 : generation.get();
    }

    /**
     * Moves the generation of the application: everything cached under the previous one is stale from now on.
     */
    public void bump( final ApplicationKey applicationKey )
    {
        generations.computeIfAbsent( applicationKey, key -> new AtomicLong() ).incrementAndGet();
    }

    /**
     * A change written on another node of the cluster: told by the distributed {@link ApplicationEvents#UPDATED} event. Local changes
     * bump the generation where they are written, before the event is published.
     */
    @Override
    public void onEvent( final Event event )
    {
        if ( event.isLocalOrigin() || !ApplicationEvents.EVENT_TYPE.equals( event.getType() ) )
        {
            return;
        }
        if ( !event.getValueAs( String.class, ApplicationEvents.EVENT_TYPE_KEY ).filter( ApplicationEvents.UPDATED::equals ).isPresent() )
        {
            return;
        }
        event.getValueAs( String.class, ApplicationEvents.APPLICATION_KEY_KEY ).map( ApplicationKey::from ).ifPresent( this::bump );
    }
}
