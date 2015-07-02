package com.enonic.xp.admin.impl.rest.resource.repo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemDumpRequestJson
{
    private final String name;

    public SystemDumpRequestJson( @JsonProperty("name") final String name )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
