package com.enonic.xp.script.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class JsonMapGenerator
    extends MapGeneratorBase
{
    public JsonMapGenerator()
    {
        initRoot();
    }

    @Override
    protected Object newMap()
    {
        return JsonNodeFactory.instance.objectNode();
    }

    @Override
    protected Object newArray()
    {
        return JsonNodeFactory.instance.arrayNode();
    }

    @Override
    protected boolean isMap( final Object value )
    {
        return value instanceof ObjectNode;
    }

    @Override
    protected boolean isArray( final Object value )
    {
        return value instanceof ArrayNode;
    }

    @Override
    protected void putInMap( final Object map, final String key, final Object value )
    {
        ( (ObjectNode) map ).set( key, toValue( value ) );
    }

    @Override
    protected void addToArray( final Object array, final Object value )
    {
        ( (ArrayNode) array ).add( toValue( value ) );
    }

    private JsonNode toValue( final Object value )
    {
        if ( value instanceof JsonNode )
        {
            return (JsonNode) value;
        }

        if ( value instanceof Integer )
        {
            return IntNode.valueOf( (Integer) value );
        }

        if ( value instanceof Float )
        {
            return FloatNode.valueOf( (Float) value );
        }

        if ( value instanceof Double )
        {
            return DoubleNode.valueOf( (Double) value );
        }

        if ( value instanceof Number )
        {
            return LongNode.valueOf( ( (Number) value ).longValue() );
        }

        if ( value instanceof Boolean )
        {
            return BooleanNode.valueOf( (Boolean) value );
        }

        if ( value == null )
        {
            return NullNode.getInstance();
        }

        return TextNode.valueOf( value.toString() );
    }

    @Override
    protected MapGeneratorBase newGenerator()
    {
        return new JsonMapGenerator();
    }
}
