package com.enonic.xp.admin.impl.rest.resource.application.json;

import java.util.Map;

public class MarketApplicationJson
{
    private String displayName;

    private String description;

    private String iconUrl;

    private String latestVersion;

    private String name;

    private String url;

    private Map<String, MarketAppVersionInfoJson> versions;

    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }

    public void setDescription( final String description )
    {
        this.description = description;
    }

    public void setIconUrl( final String iconUrl )
    {
        this.iconUrl = iconUrl;
    }

    public void setLatestVersion( final String latestVersion )
    {
        this.latestVersion = latestVersion;
    }

    public void setVersions( final Map<String, MarketAppVersionInfoJson> versions )
    {
        this.versions = versions;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setUrl( final String url )
    {
        this.url = url;
    }

    @SuppressWarnings("unused")
    public String getUrl()
    {
        return url;
    }

    @SuppressWarnings("unused")
    public String getDisplayName()
    {
        return displayName;
    }

    @SuppressWarnings("unused")
    public String getDescription()
    {
        return description;
    }

    @SuppressWarnings("unused")
    public String getIconUrl()
    {
        return iconUrl;
    }

    @SuppressWarnings("unused")
    public String getLatestVersion()
    {
        return latestVersion;
    }

    @SuppressWarnings("unused")
    public Map<String, MarketAppVersionInfoJson> getVersions()
    {
        return versions;
    }

    @SuppressWarnings("unused")
    public String getName()
    {
        return name;
    }
}
