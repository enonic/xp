package com.enonic.xp.admin.impl.rest.resource.repo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemLoadRequestJson
{
    private final String sourceDirectory;

    public SystemLoadRequestJson( @JsonProperty("sourceDirectory") final String sourceDirectory )
    {
        this.sourceDirectory = sourceDirectory;
    }

    public String getSourceDirectory()
    {
        return sourceDirectory;
    }
}
