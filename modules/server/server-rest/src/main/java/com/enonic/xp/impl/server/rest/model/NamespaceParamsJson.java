package com.enonic.xp.impl.server.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NamespaceParamsJson
{
    private final String key;

    private final String description;

    @JsonCreator
    public NamespaceParamsJson( @JsonProperty("key") final String key, @JsonProperty("description") final String description )
    {
        this.key = key;
        this.description = description;
    }

    public String getKey()
    {
        return key;
    }

    public String getDescription()
    {
        return description;
    }
}