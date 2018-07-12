package com.enonic.xp.impl.server.rest.model;

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
