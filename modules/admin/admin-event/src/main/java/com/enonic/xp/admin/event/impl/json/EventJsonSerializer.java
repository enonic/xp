package com.enonic.xp.admin.event.impl.json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.app.ApplicationEvent;
import com.enonic.xp.content.ContentChangeEvent;
import com.enonic.xp.content.ContentCreatedEvent;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishedEvent;
import com.enonic.xp.content.ContentUpdatedEvent;
import com.enonic.xp.event.Event;

public final class EventJsonSerializer
{
    public ObjectNode toJson( final Event event )
    {
        if ( event instanceof ApplicationEvent )
        {
            return toJson( (ApplicationEvent) event );
        }

        if ( event instanceof ContentUpdatedEvent )
        {
            return toJson( (ContentUpdatedEvent) event );
        }

        if ( event instanceof ContentPublishedEvent )
        {
            return toJson( (ContentPublishedEvent) event );
        }

        if ( event instanceof ContentCreatedEvent )
        {
            return toJson( (ContentCreatedEvent) event );
        }

        if ( event instanceof ContentChangeEvent )
        {
            return toJson( (ContentChangeEvent) event );
        }

        return null;
    }

    private ObjectNode toJson( final ApplicationEvent event )
    {
        final ObjectNode json = newObjectNode();
        json.put( "eventType", event.getState() );
        json.put( "applicationKey", event.getKey().toString() );
        return eventWrapper( event, json );
    }

    private ObjectNode toJson( final ContentUpdatedEvent event )
    {
        final ObjectNode json = newObjectNode();
        json.put( "contentId", event.getContentId().toString() );
        return eventWrapper( event, json );
    }

    private ObjectNode toJson( final ContentPublishedEvent event )
    {
        final ObjectNode json = newObjectNode();
        json.put( "contentId", event.getContentId().toString() );
        return eventWrapper( event, json );
    }

    private ObjectNode toJson( final ContentCreatedEvent event )
    {
        final ObjectNode json = newObjectNode();
        json.put( "contentId", event.getContentId().toString() );
        return eventWrapper( event, json );
    }

    private ObjectNode toJson( final ContentChangeEvent event )
    {
        final ObjectNode json = newObjectNode();
        final ArrayNode changes = json.putArray( "changes" );

        for ( final ContentChangeEvent.ContentChange change : event.getChanges() )
        {
            final ObjectNode changeJson = changes.addObject();
            changeJson.put( "t", change.getType().id() );

            final ArrayNode pathsJson = changeJson.putArray( "p" );
            for ( final ContentPath path : change.getContentPaths() )
            {
                pathsJson.add( path.asAbsolute().toString() );
            }
        }

        return eventWrapper( event, json );
    }

    private ObjectNode newObjectNode()
    {
        return JsonNodeFactory.instance.objectNode();
    }

    private ObjectNode eventWrapper( final Event event, final ObjectNode data )
    {
        final ObjectNode json = newObjectNode();
        json.put( "type", event.getClass().getSimpleName() );
        json.set( "event", data );
        return json;
    }
}
