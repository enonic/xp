package com.enonic.xp.admin.event.impl.json;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.event.Event;

public final class EventJsonSerializer
{
    public ObjectNode toJson( final Event event )
    {
        if ( event != null )
        {
            final ObjectNode json = JsonNodeFactory.instance.objectNode();
            json.put( "type", event.getType() );
            json.put( "timestamp", event.getTimestamp() );
            json.set( "data", toJsonNode( event.getData() ) );
            return json;
        }
        return null;
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
}
