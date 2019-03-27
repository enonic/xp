package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviders;

public final class IdProvidersJson
{
    private final List<IdProviderSummaryJson> idProvidersJson;

    public IdProvidersJson( final IdProviders idProviders )
    {
        this.idProvidersJson = new ArrayList<>();
        if ( idProviders != null )
        {
            for ( IdProvider idProvider : idProviders )
            {
                idProvidersJson.add( new IdProviderSummaryJson( idProvider ) );
            }
        }
    }

    public List<IdProviderSummaryJson> getIdProviders()
    {
        return idProvidersJson;
    }
}