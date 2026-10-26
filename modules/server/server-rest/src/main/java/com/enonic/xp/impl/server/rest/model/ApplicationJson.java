package com.enonic.xp.impl.server.rest.model;

import java.time.Instant;

public class ApplicationJson
{
    private final ApplicationInfoJson info;

    public ApplicationJson( final ApplicationInfoJson info )
    {
        this.info = info;
    }

    public String getKey()
    {
        return info.getKey();
    }

    public String getVersion()
    {
        return info.getVersion();
    }

    public String getDisplayName()
    {
        return info.getTitle();
    }

    public String getMaxSystemVersion()
    {
        return info.getMaxSystemVersion();
    }

    public String getMinSystemVersion()
    {
        return info.getMinSystemVersion();
    }

    public String getUrl()
    {
        return info.getUrl();
    }

    public String getVendorName()
    {
        return info.getVendorName();
    }

    public String getVendorUrl()
    {
        return info.getVendorUrl();
    }

    public Instant getModifiedTime()
    {
        return info.getModifiedTime();
    }

    public String getState()
    {
        return info.getState();
    }

    public boolean getLocal()
    {
        return info.getLocal();
    }
}
