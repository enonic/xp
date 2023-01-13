package com.enonic.xp.core.impl.content;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.event.Event;

public final class ContentEvents
{
    private ContentEvents()
    {
    }

    public static Event online( Collection<Content> contents )
    {
        return Event.create( "content.online" ).distributed( true ).value( "contents", nodesToList( contents ) ).build();
    }

    public static Event offline( Collection<Content> contents )
    {
        return Event.create( "content.offline" ).distributed( true ).value( "contents", nodesToList( contents ) ).build();
    }

    private static ImmutableList<ImmutableMap<String, String>> nodesToList( final Collection<Content> contents )
    {
        return contents.stream().map( ContentEvents::nodeToMap ).collect( ImmutableList.toImmutableList() );
    }

    private static ImmutableMap<String, String> nodeToMap( final Content content )
    {
        final ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String>builder()
            .put( "id", content.getId().toString() )
            .put( "path", content.getPath().toString() )
            .put( "repo", ContextAccessor.current().getRepositoryId().toString() );

        final ContentPublishInfo publishInfo = content.getPublishInfo();
        if ( publishInfo != null )
        {
            if ( publishInfo.getFrom() != null )
            {
                builder.put( "from", publishInfo.getFrom().toString() );
            }
            if ( publishInfo.getTo() != null )
            {
                builder.put( "to", publishInfo.getTo().toString() );
            }
        }

        return builder.build();
    }
}
