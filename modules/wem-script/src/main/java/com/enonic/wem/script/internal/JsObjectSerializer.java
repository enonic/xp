package com.enonic.wem.script.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jdk.nashorn.api.scripting.JSObject;

import com.enonic.wem.api.util.Exceptions;

final class JsObjectSerializer
{
    private final ObjectMapper mapper;

    public JsObjectSerializer()
    {
        this.mapper = new ObjectMapper();
        this.mapper.disable( SerializationFeature.INDENT_OUTPUT );
    }

    public String toString( final JSObject value )
    {
        try
        {
            return this.mapper.writeValueAsString( toJson( value ) );
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    private JsonNode toJson( final JSObject value )
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
