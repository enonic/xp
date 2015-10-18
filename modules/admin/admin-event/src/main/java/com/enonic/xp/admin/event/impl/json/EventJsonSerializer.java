package com.enonic.xp.admin.event.impl.json;

import java.util.Map;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.app.ApplicationUpdatedEvent;
import com.enonic.xp.content.ContentChangeEvent;
import com.enonic.xp.content.ContentCreatedEvent;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishedEvent;
import com.enonic.xp.content.ContentUpdatedEvent;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.Event2;

public final class EventJsonSerializer
{
    public ObjectNode toJson( final Event event )
    {
        if ( event instanceof ApplicationUpdatedEvent )
        {
            return toJson( (ApplicationUpdatedEvent) event );
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

        if ( event instanceof Event2 )
        {
            return toJson( (Event2) event );
        }

        return null;
    }

    private ObjectNode toJson( final ApplicationUpdatedEvent event )
    {
        final ObjectNode json = newObjectNode();
        json.put( "eventType", event.getEventType().toString() );
        json.put( "applicationKey", event.getApplicationKey().toString() );
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

    private ObjectNode toJson( final Event2 event )
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "type", event.getType() );
        json.put( "timestamp", event.getTimestamp() );

        final ObjectNode dataJson = json.putObject( "data" );
        for ( final Map.Entry<String, ?> entry : event.getData().entrySet() )
        {
            final String key = entry.getKey();
            final Object value = entry.getValue();

            if ( value instanceof Byte )
            {
                dataJson.put( key, (Byte) value );
            }
            else if ( value instanceof Short )
            {
                dataJson.put( key, (Short) value );
            }
            else if ( value instanceof Float )
            {
                dataJson.put( key, (Float) value );
            }
            else if ( value instanceof Double )
            {
                dataJson.put( key, (Double) value );
            }
            else if ( value instanceof Integer )
            {
                dataJson.put( key, (Integer) value );
            }
            else if ( value instanceof Long )
            {
                dataJson.put( key, (Long) value );
            }
            else if ( value instanceof Boolean )
            {
                dataJson.put( key, (Boolean) value );
            }
            else
            {
                dataJson.put( key, value.toString() );
            }
        }

        return json;
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
