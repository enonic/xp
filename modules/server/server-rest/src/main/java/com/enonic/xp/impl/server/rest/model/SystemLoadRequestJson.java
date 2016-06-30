package com.enonic.xp.impl.server.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemLoadRequestJson
{
    private final String name;

    public SystemLoadRequestJson( @JsonProperty("name") final String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
