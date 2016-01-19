package com.enonic.xp.admin.impl.rest.resource.application.json;

import java.util.Map;

public class MarkedApplicationJson
{
    private String displayName;

    private String description;

    private String iconUrl;

    private String applicationUrl;

    private String latestVersion;

    private Map<String, MarkedAppVersionInfoJson> versions;

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

    public void setApplicationUrl( final String applicationUrl )
    {
        this.applicationUrl = applicationUrl;
    }

    public void setVersions( final Map<String, MarkedAppVersionInfoJson> versions )
    {
        this.versions = versions;
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
    public String getApplicationUrl()
    {
        return applicationUrl;
    }

    @SuppressWarnings("unused")
    public Map<String, MarkedAppVersionInfoJson> getVersions()
    {
        return versions;
    }
}
