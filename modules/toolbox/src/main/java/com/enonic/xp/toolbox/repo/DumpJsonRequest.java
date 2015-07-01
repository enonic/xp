package com.enonic.xp.toolbox.repo;

import com.fasterxml.jackson.annotation.JsonProperty;

final class DumpJsonRequest
    implements JsonRequest
{
    @JsonProperty("targetDirectory")
    private String targetDirectory;

    public DumpJsonRequest targetDirectory( final String targetDirectory )
    {
        this.targetDirectory = targetDirectory;
        return this;
    }
}
