package com.enonic.wem.script.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jdk.nashorn.api.scripting.JSObject;

final class JsObjectSerializer
{
    public JsonNode toJson( final JSObject value )
    {
        if ( value.isArray() )
        {
            return toJsonArray( value );
        }

        if ( value.isFunction() )
        {
            return toJsonFunction( value );
        }

        return toJsonObject( value );
    }

    private JsonNode toJson( final Object value )
    {
        if ( value instanceof JSObject )
        {
            return toJson( (JSObject) value );
        }

        return JsonNodeFactory.instance.textNode( value.toString() );
    }

    private ArrayNode toJsonArray( final JSObject value )
    {
        final ArrayNode json = JsonNodeFactory.instance.arrayNode();
        for ( final Object item : value.values() )
        {
            json.add( toJson( item ) );
        }

        return json;
    }

    private JsonNode toJsonFunction( final JSObject value )
    {
        return JsonNodeFactory.instance.textNode( value.toString() );
    }

    private ObjectNode toJsonObject( final JSObject value )
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        for ( final String key : value.keySet() )
        {
            json.set( key, toJson( value.getMember( key ) ) );
        }

        return json;
    }
}
