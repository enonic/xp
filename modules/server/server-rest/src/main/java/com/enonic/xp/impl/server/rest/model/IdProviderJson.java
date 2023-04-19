package com.enonic.xp.impl.server.rest.model;

import com.enonic.xp.security.IdProvider;

public class IdProviderJson
{
    private final IdProvider idProvider;

    public IdProviderJson( IdProvider idProvider )
    {
        this.idProvider = idProvider;
    }

    public String getKey()
    {
        return idProvider.getKey().toString();
    }

    public String getDisplayName()
    {
        return idProvider.getDisplayName();
    }

    public String getDescription()
    {
        return idProvider.getDescription();
    }

}
