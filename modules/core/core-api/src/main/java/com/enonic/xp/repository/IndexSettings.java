package com.enonic.xp.repository;

import java.net.URL;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.annotations.Beta;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.util.JsonHelper;

@Beta
public class IndexSettings
{
    private final JsonNode jsonNode;

    public static IndexSettings from( final PropertyTree propertyTree )
    {
        return new IndexSettings( JsonHelper.from( propertyTree ) );
    }

    public static IndexSettings from( final URL url )
    {
        return new IndexSettings( JsonHelper.from( url ) );
    }

    public static IndexSettings from( final String string )
    {
        return new IndexSettings( JsonHelper.from( string ) );
    }

    public static IndexSettings from( final Map<String, Object> settings )
    {
        return new IndexSettings( JsonHelper.from( settings ) );
    }

    public IndexSettings( final JsonNode resourceNode )
    {
        this.jsonNode = resourceNode;
    }

    public JsonNode getNode()
    {
        return jsonNode;
    }

    public String getAsString()
    {
        return this.jsonNode.toString();
    }

}
