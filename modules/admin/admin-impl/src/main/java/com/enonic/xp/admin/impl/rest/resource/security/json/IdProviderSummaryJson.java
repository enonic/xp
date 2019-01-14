package com.enonic.xp.admin.impl.rest.resource.security.json;

import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderConfig;

@SuppressWarnings("UnusedDeclaration")
public class IdProviderSummaryJson
{
    private final IdProvider idProvider;

    public IdProviderSummaryJson( final IdProvider idProvider )
    {

        this.idProvider = idProvider;
    }

    public String getDisplayName()
    {
        return idProvider.getDisplayName();
    }

    public String getKey()
    {
        return idProvider.getKey().toString();
    }

    public String getDescription()
    {
        return idProvider.getDescription();
    }

    public IdProviderConfigJson getIdProviderConfig()
    {
        final IdProviderConfig idProviderConfig = idProvider.getIdProviderConfig();
        return IdProviderConfigJson.toJson( idProviderConfig );
    }

}