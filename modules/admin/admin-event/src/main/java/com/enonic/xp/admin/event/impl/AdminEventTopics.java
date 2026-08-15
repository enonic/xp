package com.enonic.xp.admin.event.impl;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;

/**
 * The admin event topic registry: what applications registered, and how far each topic's sequence
 * has advanced.
 * <p>
 * Held apart from the socket-facing half of the hub, and referencing nothing that can be restarted
 * beneath it, because registrations arrive from {@code main.js} and an application is bootstrapped
 * once: state lost here is not registered again until every owning application restarts
 * (enonic/xp#12273). Sequence numbering and {@link #epoch} share this lifetime, so subscribers
 * count gaps across a restart of the socket-facing half.
 */
@Component(service = AdminEventTopics.class)
public final class AdminEventTopics
{
    private final ConcurrentMap<String, TopicState> topics = new ConcurrentHashMap<>();

    // identifies this registry incarnation, and with it the sequence numbering
    private final String epoch = UUID.randomUUID().toString();

    String epoch()
    {
        return this.epoch;
    }

    TopicState find( final String topic )
    {
        return this.topics.get( topic );
    }

    TopicState findOrCreate( final String topic )
    {
        return this.topics.computeIfAbsent( topic, key -> new TopicState() );
    }

    /**
     * Clears the registration of every topic owned by the application, keeping the sequence
     * numbering and the current subscribers.
     */
    void clearOwnedBy( final ApplicationKey owner, final char qualifier )
    {
        final String prefix = owner + String.valueOf( qualifier );
        this.topics.forEach( ( topic, state ) -> {
            if ( topic.startsWith( prefix ) )
            {
                synchronized ( state.lock )
                {
                    state.allow = null;
                }
            }
        } );
    }

    /**
     * Drops every subscription, for a hub that is going away with its sockets.
     */
    void forgetAllSubscribers()
    {
        this.topics.values().forEach( state -> state.subscribers.clear() );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addApplication( final Application application )
    {
        // tracked only for removeApplication
    }

    public void removeApplication( final Application application )
    {
        clearOwnedBy( application.getKey(), AdminEventHubImpl.QUALIFIER );
    }
}
