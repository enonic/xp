package com.enonic.xp.impl.server.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApplicationInstallParams
{
    @JsonProperty("URL")
    private String url;

    private String sha512;

    public String getUrl()
    {
        return url;
    }

    public String getSha512()
    {
        return sha512;
    }
}
