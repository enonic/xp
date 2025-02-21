package com.enonic.xp.repository;

import java.net.URL;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.util.JsonHelper;

@PublicApi
public class IndexMapping
{
    private final JsonNode jsonNode;

    public static IndexMapping from( final PropertyTree propertyTree )
    {
        return new IndexMapping( JsonHelper.from( propertyTree ) );
    }

    public static IndexMapping from( final URL url )
    {
        return new IndexMapping( JsonHelper.from( url ) );
    }

    public static IndexMapping from( final String string )
    {
        return new IndexMapping( JsonHelper.from( string ) );
    }

    public static IndexMapping from( final Map<String, Object> map )
    {
        return new IndexMapping( JsonHelper.from( map ) );
    }

    @Deprecated
    public IndexMapping( final JsonNode resourceNode )
    {
        this.jsonNode = resourceNode;
    }

    public PropertyTree getData()
    {
        return PropertyTree.fromMap( JsonHelper.toMap( jsonNode ) );
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
