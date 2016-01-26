package com.enonic.xp.admin.impl.rest.resource.application.json;

public class MarketAppVersionInfoJson
{
    private String applicationUrl;

    public MarketAppVersionInfoJson()
    {
    }

    public MarketAppVersionInfoJson( final String applicationUrl )
    {
        this.applicationUrl = applicationUrl;
    }

    @SuppressWarnings("unused")
    public String getApplicationUrl()
    {
        return applicationUrl;
    }
}
