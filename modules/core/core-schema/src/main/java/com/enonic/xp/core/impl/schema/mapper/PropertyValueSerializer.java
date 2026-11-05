package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.util.GenericValue;

/**
 * The inverse of {@link PropertyValueDeserializer#fromYml(JsonNode)}: a {@link GenericValue} as a tree to write as YAML.
 */
public final class PropertyValueSerializer
{
    private PropertyValueSerializer()
    {
    }

    public static JsonNode toYml( final GenericValue value, final JsonNodeFactory factory )
    {
        return switch ( value.getType() )
        {
            case STRING -> factory.textNode( value.asString() );
            case NUMBER -> numberNode( value, factory );
            case BOOLEAN -> factory.booleanNode( value.asBoolean() );
            case LIST ->
            {
                final ArrayNode array = factory.arrayNode();
                value.values().forEach( item -> array.add( toYml( item, factory ) ) );
                yield array;
            }
            case OBJECT ->
            {
                final ObjectNode object = factory.objectNode();
                value.properties().forEach( e -> object.set( e.getKey(), toYml( e.getValue(), factory ) ) );
                yield object;
            }
        };
    }

    private static JsonNode numberNode( final GenericValue value, final JsonNodeFactory factory )
    {
        final Object raw = value.toRawJava();
        if ( raw instanceof Integer i )
        {
            return factory.numberNode( i );
        }
        if ( raw instanceof Long l )
        {
            return factory.numberNode( l );
        }
        return factory.numberNode( value.asDouble() );
    }
}