package com.enonic.xp.repository;

import java.net.URL;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.core.internal.json.JsonHelper;
import com.enonic.xp.data.PropertyTree;

@PublicApi
public class IndexSettings
{
    private final JsonNode jsonNode;

    @Deprecated
    public IndexSettings( final JsonNode resourceNode )
    {
        this.jsonNode = resourceNode.deepCopy();
    }

    private IndexSettings( final JsonNode resourceNode, final boolean ignore)
    {
        this.jsonNode = resourceNode;
    }

    @Deprecated
    public static IndexSettings from( final PropertyTree propertyTree )
    {
        return new IndexSettings( JsonHelper.from( propertyTree.toMap() ), false );
    }

    @Deprecated
    public static IndexSettings from( final URL url )
    {
        return new IndexSettings( JsonHelper.from( url ), false );
    }

    @Deprecated
    public static IndexSettings from( final String string )
    {
        return new IndexSettings( JsonHelper.from( string ), false );
    }

    public static IndexSettings from( final Map<String, Object> settings )
    {
        return new IndexSettings( JsonHelper.from( settings ), false );
    }

    public IndexSettings merge( final IndexSettings with )
    {
        final JsonNode newJsonNode = this.jsonNode.deepCopy();
        JsonHelper.merge( newJsonNode, with.jsonNode );

        return new IndexSettings( newJsonNode, false );
    }

    public Map<String, Object> getData()
    {
        return JsonHelper.toMap( jsonNode );
    }

    @Deprecated
    public JsonNode getNode()
    {
        return jsonNode;
    }

    public String getAsString()
    {
        return this.jsonNode.toString();
    }

}
