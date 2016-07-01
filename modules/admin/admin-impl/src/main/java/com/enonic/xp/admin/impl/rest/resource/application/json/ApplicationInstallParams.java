package com.enonic.xp.admin.impl.rest.resource.application.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApplicationInstallParams
{
    @JsonProperty("URL")
    private String URL;


    public String getURL()
    {
        return URL;
    }
}
