package com.enonic.xp.admin.impl.rest.resource.application.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetMarketApplicationsJson
{
    @JsonProperty("version")
    private String version;

    @SuppressWarnings("unused")
    public String getVersion()
    {
        return version;
    }
}
