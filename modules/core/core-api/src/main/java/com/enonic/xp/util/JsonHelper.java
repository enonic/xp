package com.enonic.xp.util;

import java.net.URL;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.data.PropertyTree;

@Deprecated
public final class JsonHelper
{
    private JsonHelper()
    {
    }

    public static JsonNode merge( JsonNode mainNode, JsonNode updateNode )
    {
        return com.enonic.xp.core.internal.json.JsonHelper.merge( mainNode, updateNode );
    }

    public static JsonNode from( final URL url )
    {
        return com.enonic.xp.core.internal.json.JsonHelper.from( url );
    }

    public static JsonNode from( final PropertyTree propertyTree )
    {
        return from( propertyTree.toMap() );
    }

    public static JsonNode from( final Map<String, Object> settings )
    {
        return com.enonic.xp.core.internal.json.JsonHelper.from( settings );
    }

    public static JsonNode from( final String json )
    {
        return com.enonic.xp.core.internal.json.JsonHelper.from( json );
    }
}
