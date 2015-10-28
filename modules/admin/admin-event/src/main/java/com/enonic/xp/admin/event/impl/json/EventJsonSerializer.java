package com.enonic.xp.admin.event.impl.json;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.app.ApplicationEvent;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.Event2;

public final class EventJsonSerializer
{
    public ObjectNode toJson( final Event event )
    {
        if ( event instanceof ApplicationEvent )
        {
            return toJson( (ApplicationEvent) event );
        }

        if ( event instanceof Event2 )
        {
            return toJson( (Event2) event );
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

    private ObjectNode toJson( final Event2 event )
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "type", event.getType() );
        json.put( "timestamp", event.getTimestamp() );
        json.set( "data", toJsonNode( event.getData() ) );
        return json;
    }

    private JsonNode toJsonNode( Object value )
    {
        final JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
        if ( value instanceof Map )
        {
            final ObjectNode objectNode = jsonNodeFactory.objectNode();
            for ( final Map.Entry<String, ?> entry : ( (Map<String, ?>) value ).entrySet() )
            {
                objectNode.set( entry.getKey(), toJsonNode( entry.getValue() ) );
            }
            return objectNode;
        }
        else if ( value instanceof Collection )
        {
            final ArrayNode arrayNode = jsonNodeFactory.arrayNode();
            final List<JsonNode> subJsonNodes = ( (Collection<?>) value ).stream().map( this::toJsonNode ).collect( Collectors.toList() );
            return arrayNode.addAll( subJsonNodes );
        }
        else if ( value instanceof Byte )
        {
            return jsonNodeFactory.numberNode( (Byte) value );
        }
        else if ( value instanceof Short )
        {
            return jsonNodeFactory.numberNode( (Short) value );
        }
        else if ( value instanceof Float )
        {
            return jsonNodeFactory.numberNode( (Float) value );
        }
        else if ( value instanceof Double )
        {
            return jsonNodeFactory.numberNode( (Double) value );
        }
        else if ( value instanceof Integer )
        {
            return jsonNodeFactory.numberNode( (Integer) value );
        }
        else if ( value instanceof Long )
        {
            return jsonNodeFactory.numberNode( (Long) value );
        }
        else if ( value instanceof Boolean )
        {
            return jsonNodeFactory.booleanNode( (Boolean) value );
        }
        else if ( value != null )
        {
            return jsonNodeFactory.textNode( value.toString() );
        }
        else
        {
            return null;
        }
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
