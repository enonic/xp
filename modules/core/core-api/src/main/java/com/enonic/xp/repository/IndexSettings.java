package com.enonic.xp.repository;

import com.fasterxml.jackson.databind.JsonNode;

public class IndexSettings
{
    private final JsonNode jsonNode;

    private final boolean includeDefaultSettings;

    public IndexSettings( final JsonNode jsonNode )
    {
        this.jsonNode = jsonNode;
        includeDefaultSettings = true;
    }

    public IndexSettings( final JsonNode jsonNode, final boolean includeDefaultSettings )
    {
        this.jsonNode = jsonNode;
        this.includeDefaultSettings = includeDefaultSettings;
    }

    public boolean includeDefaultSettings()
    {
        return includeDefaultSettings;
    }

    public JsonNode get()
    {
        return this.jsonNode;
    }

    public String getAsString()
    {
        return this.jsonNode.toString();
    }
}
