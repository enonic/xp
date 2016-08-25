package com.enonic.xp.repository;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonIndexResource
    implements IndexResource
{
    private final JsonNode resourceNode;

    public JsonIndexResource( final JsonNode resourceNode )
    {
        this.resourceNode = resourceNode;
    }

    public JsonNode getResourceNode()
    {
        return resourceNode;
    }

    @Override
    public String getAsString()
    {
        return this.resourceNode.toString();
    }
}


