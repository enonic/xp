package com.enonic.xp.admin.impl.rest.resource.application.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MarkedAppVersionInfoJson
{
    private String downloadUrl;

    public MarkedAppVersionInfoJson( final @JsonProperty("downloadUrl") String downloadUrl )
    {
        this.downloadUrl = downloadUrl;
    }

    @SuppressWarnings("unused")
    public String getDownloadUrl()
    {
        return downloadUrl;
    }
}
