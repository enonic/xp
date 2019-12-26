package com.enonic.xp.util;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.json.ObjectMapperHelper;

public class JsonHelper
{
    private static final ObjectMapper MAPPER = ObjectMapperHelper.create().
        enable( SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED );

    public static JsonNode merge( JsonNode mainNode, JsonNode updateNode )
    {
        Iterator<String> fieldNames = updateNode.fieldNames();
        while ( fieldNames.hasNext() )
        {
            String fieldName = fieldNames.next();
            JsonNode jsonNode = mainNode.get( fieldName );

            final boolean fieldExistsAndIsEmbeddedObject = jsonNode != null && jsonNode.isObject();
            if ( fieldExistsAndIsEmbeddedObject )
            {
                merge( jsonNode, updateNode.get( fieldName ) );
            }
            else
            {
                if ( mainNode instanceof ObjectNode )
                {
                    JsonNode value = updateNode.get( fieldName );
                    ( (ObjectNode) mainNode ).replace( fieldName, value );
                }
            }
        }
        return mainNode;
    }

    public static JsonNode from( final URL url )
    {
        if ( url == null )
        {
            throw new IllegalArgumentException( "Cannot read JsonNode: URL not given" );
        }

        try
        {
            return MAPPER.readTree( url );
        }
        catch ( IOException e )
        {
            throw new IllegalArgumentException( "Cannot load settings from URL [" + url + "]", e );
        }
    }

    public static JsonNode from( final PropertyTree propertyTree )
    {
        final Map<String, Object> settingsMap = propertyTree.toMap();

        return from( settingsMap );
    }

    public static JsonNode from( final Map<String, Object> settings )
    {
        return MAPPER.valueToTree( settings );
    }

    public static JsonNode from( final String json )
    {
        try
        {
            return MAPPER.readTree( json );
        }
        catch ( IOException e )
        {
            throw new IllegalArgumentException( "Cannot serialize settings from string [" + json + "]", e );
        }
    }
}
