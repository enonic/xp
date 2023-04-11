package com.enonic.xp.impl.server.rest.model;

import java.util.List;

import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.security.IdProviderConfig;

public class IdProviderConfigJson
{
    private final IdProviderConfig idProviderConfig;

    public IdProviderConfigJson( IdProviderConfig idProviderConfig )
    {
        this.idProviderConfig = idProviderConfig;
    }

    public String getKey()
    {
        return idProviderConfig.getApplicationKey().toString();
    }

    public List<PropertyArrayJson> getIdProviderConfig()
    {
        return PropertyTreeJson.toJson( idProviderConfig.getConfig() );
    }

}
