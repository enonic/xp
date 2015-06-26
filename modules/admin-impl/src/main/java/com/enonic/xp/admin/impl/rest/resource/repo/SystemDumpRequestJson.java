package com.enonic.xp.admin.impl.rest.resource.repo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemDumpRequestJson
{
    private final String targetDirectory;

    public SystemDumpRequestJson( @JsonProperty("targetDirectory") final String targetDirectory )
    {
        this.targetDirectory = targetDirectory;
    }

    public String getTargetDirectory()
    {
        return targetDirectory;
    }
}
