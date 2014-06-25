package com.enonic.wem.admin.rest.resource.module.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ModuleInstallParams
{
    private final String url;

    @JsonCreator
    public ModuleInstallParams( @JsonProperty("url") String url )
    {
        this.url = url;
    }

    public String getUrl()
    {
        return this.url;
    }
}
