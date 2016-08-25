package com.enonic.xp.repository;

import com.fasterxml.jackson.databind.JsonNode;

public class IndexResource
{
    private final JsonNode resourceNode;

    public IndexResource( final JsonNode resourceNode )
    {
        this.resourceNode = resourceNode;
    }

    public JsonNode getResourceNode()
    {
        return resourceNode;
    }

    public String getAsString()
    {
        return this.resourceNode.toString();
    }
}


