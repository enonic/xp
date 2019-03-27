package com.enonic.xp.impl.server.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemDumpUpgradeRequestJson
{
    private final String name;

    public SystemDumpUpgradeRequestJson( @JsonProperty("name") final String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
