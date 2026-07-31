package com.enonic.xp.impl.server.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SchemaResourceParamsJson
{
    private final String resource;

    @JsonCreator
    public SchemaResourceParamsJson( @JsonProperty("resource") final String resource )
    {
        this.resource = resource;
    }

    public String getResource()
    {
        return resource;
    }
}