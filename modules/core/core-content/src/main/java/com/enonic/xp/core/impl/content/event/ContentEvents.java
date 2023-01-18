package com.enonic.xp.core.impl.content.event;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.event.Event;

final class ContentEvents
{
    private ContentEvents()
    {
    }

    public static Event online( Collection<ContentEvent> events )
    {
        return Event.create( "content.online" ).distributed( true ).value( "contents", nodesToList( events ) ).build();
    }

    public static Event offline( Collection<ContentEvent> contents )
    {
        return Event.create( "content.offline" ).distributed( true ).value( "contents", nodesToList( contents ) ).build();
    }

    private static ImmutableList<ImmutableMap<String, String>> nodesToList( final Collection<ContentEvent> contents )
    {
        return contents.stream().map( ContentEvents::nodeToMap ).collect( ImmutableList.toImmutableList() );
    }

    private static ImmutableMap<String, String> nodeToMap( final ContentEvent event )
    {
        final ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String>builder()
            .put( "id", event.getNodeId().toString() )
            .put( "path", event.getNodePath().toString() )
            .put( "nodeVersionId", event.getNodeVersionId().toString() )
            .put( "time", event.getTime().toString() )
            .put( "repo", event.getRepositoryId().toString() );

        return builder.build();
    }
}
