package com.enonic.xp.admin.impl.rest.resource.application.json;

public class MarkedAppVersionInfoJson
{
    private String downloadUrl;

    public MarkedAppVersionInfoJson( final String downloadUrl )
    {
        this.downloadUrl = downloadUrl;
    }

    @SuppressWarnings("unused")
    public String getDownloadUrl()
    {
        return downloadUrl;
    }
}
