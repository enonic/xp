package com.enonic.xp.repository;

import java.net.URL;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.core.internal.json.JsonHelper;
import com.enonic.xp.data.PropertyTree;

@PublicApi
public class IndexMapping
{
    private final JsonNode jsonNode;

    @Deprecated
    public IndexMapping( final JsonNode resourceNode )
    {
        this.jsonNode = resourceNode.deepCopy();
    }

    private IndexMapping( final JsonNode resourceNode, boolean ignore )
    {
        this.jsonNode = resourceNode;
    }

    @Deprecated
    public static IndexMapping from( final PropertyTree propertyTree )
    {
        return new IndexMapping( JsonHelper.from( propertyTree.toMap() ), false );
    }

    @Deprecated
    public static IndexMapping from( final URL url )
    {
        return new IndexMapping( JsonHelper.from( url ), false );
    }

    @Deprecated
    public static IndexMapping from( final String string )
    {
        return new IndexMapping( JsonHelper.from( string ), false );
    }

    public static IndexMapping from( final Map<String, Object> data )
    {
        return new IndexMapping( JsonHelper.from( data ), false );
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

    public IndexMapping merge( final IndexMapping with )
    {
        final JsonNode newJsonNode = this.jsonNode.deepCopy();
        JsonHelper.merge( newJsonNode, with.jsonNode );

        return new IndexMapping( newJsonNode, false );
    }
}
