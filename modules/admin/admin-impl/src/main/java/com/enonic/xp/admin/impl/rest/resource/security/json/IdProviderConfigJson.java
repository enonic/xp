package com.enonic.xp.admin.impl.rest.resource.security.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.security.IdProviderConfig;

public final class IdProviderConfigJson
{
    private final IdProviderConfig idProviderConfig;

    @JsonCreator
    public IdProviderConfigJson( @JsonProperty("applicationKey") final String applicationKey,
                                 @JsonProperty("config") final List<PropertyArrayJson> config )
    {
        idProviderConfig = IdProviderConfig.create().
            applicationKey( ApplicationKey.from( applicationKey ) ).
            config( PropertyTreeJson.fromJson( config ) ).
            build();
    }

    public static IdProviderConfigJson toJson( final IdProviderConfig idProviderConfig )
    {
        return idProviderConfig == null
            ? null
            : new IdProviderConfigJson( idProviderConfig.getApplicationKey().toString(),
                                        PropertyTreeJson.toJson( idProviderConfig.getConfig() ) );
    }

    public String getApplicationKey()
    {
        return idProviderConfig.getApplicationKey().toString();
    }

    public List<PropertyArrayJson> getConfig()
    {
        return PropertyTreeJson.toJson( idProviderConfig.getConfig() );
    }

    @JsonIgnore
    public IdProviderConfig getIdProviderConfig()
    {
        return idProviderConfig;
    }
}