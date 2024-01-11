package com.enonic.xp.repository;

import java.net.URL;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.util.JsonHelper;

@PublicApi
public final class IndexSettings
{
    private final JsonNode jsonNode;

    @Deprecated
    public IndexSettings( final JsonNode resourceNode )
    {
        this.jsonNode = resourceNode;
    }

    private IndexSettings( final JsonNode resourceNode, final boolean ignore)
    {
        this.jsonNode = resourceNode;
    }

    public static IndexSettings from( final PropertyTree propertyTree )
    {
        return new IndexSettings( JsonHelper.from( propertyTree ), false );
    }

    public static IndexSettings from( final URL url )
    {
        return new IndexSettings( JsonHelper.from( url ), false );
    }

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
        final JsonNode newJsonNode = JsonHelper.from( Map.of() );
        JsonHelper.merge( newJsonNode, this.jsonNode );
        JsonHelper.merge( newJsonNode, with.jsonNode );

        return new IndexSettings( newJsonNode, false );
    }

    @Deprecated
    public JsonNode getNode()
    {
        return jsonNode.deepCopy();
    }

    public Boolean getBoolean( final String fieldName )
    {
        final JsonNode node = jsonNode.get( fieldName );

        return node != null ? node.asBoolean() : null;
    }

    public Integer getInteger( final String fieldName )
    {
        final JsonNode node = jsonNode.get( fieldName );
        return node != null ? node.asInt() : null;
    }

    public String getText( final String fieldName )
    {
        final JsonNode node = jsonNode.get( fieldName );
        return node != null ? node.asText() : null;
    }

    public String getAsString()
    {
        return this.jsonNode.toString();
    }

}
