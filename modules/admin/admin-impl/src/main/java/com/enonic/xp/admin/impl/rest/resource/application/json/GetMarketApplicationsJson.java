package com.enonic.xp.admin.impl.rest.resource.application.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetMarketApplicationsJson
{
    @JsonProperty("version")
    private String version;

    @JsonProperty(value = "start", defaultValue = "0")
    private String start;

    @JsonProperty(value = "count", defaultValue = "10")
    private String count;

    public String getVersion()
    {
        return version;
    }

    public String getStart()
    {
        return start;
    }

    public String getCount()
    {
        return count;
    }
}
