package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.repository.RepositoryId;

public class OnlineEventsProducer
{
    private static final Map<RepositoryId, Map<ContentId, Holder>> SCHEDULED_ONLINE = new ConcurrentHashMap<>();

    private static final Map<RepositoryId, Map<ContentId, Holder>> SCHEDULED_OFFLINE = new ConcurrentHashMap<>();

    private static class Holder
    {
        Instant time;

        ContentId contentId;

        ContentPath contentPath;
    }

    private final EventPublisher eventPublisher;

    public OnlineEventsProducer( final EventPublisher eventPublisher )
    {
        this.eventPublisher = eventPublisher;
    }

    public void unpublishded( final ContentIds contentIds )
    {
        SCHEDULED_ONLINE.getOrDefault( ContextAccessor.current().getRepositoryId(), new ConcurrentHashMap<>() )
            .keySet()
            .removeAll( contentIds.getSet() );
        SCHEDULED_OFFLINE.getOrDefault( ContextAccessor.current().getRepositoryId(), new ConcurrentHashMap<>() )
            .keySet()
            .removeAll( contentIds.getSet() );

        this.eventPublisher.publish( Event.create( "content.offline" ).distributed( true ).value( "nodes", contentIds ).build() );
    }

    public void published( final ContentIds contentIds )
    {
        this.eventPublisher.publish( Event.create( "content.online" ).distributed( true ).value( "nodes", contentIds ).build() );
    }
}
