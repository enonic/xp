package com.enonic.xp.toolbox.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class JsonHelper
{
    private final static ObjectMapper MAPPER = new ObjectMapper();

    static
    {
        MAPPER.enable( MapperFeature.SORT_PROPERTIES_ALPHABETICALLY );
        MAPPER.setSerializationInclusion( JsonInclude.Include.ALWAYS );
        MAPPER.enable( SerializationFeature.INDENT_OUTPUT );
    }

    public static String serialize( final Object value )
        throws Exception
    {
        return MAPPER.writeValueAsString( value );
    }

    public static String prettifyJson( final String json )
        throws Exception
    {
        final JsonNode node = MAPPER.readTree( json );
        return MAPPER.writeValueAsString( node );
    }

    public static ObjectNode newObjectNode()
    {
        return JsonNodeFactory.instance.objectNode();
    }
}
