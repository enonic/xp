package com.enonic.xp.admin.impl.rest.resource.security.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.security.AuthConfig;

public final class AuthConfigJson
{
    private final AuthConfig authConfig;

    @JsonCreator
    public AuthConfigJson( @JsonProperty("applicationKey") final String applicationKey,
                           @JsonProperty("config") final List<PropertyArrayJson> config )
    {
        authConfig = AuthConfig.create().
            applicationKey( ApplicationKey.from( applicationKey ) ).
            config( PropertyTreeJson.fromJson( config ) ).
            build();
    }

    public String getApplicationKey()
    {
        return authConfig.getApplicationKey().toString();
    }

    public List<PropertyArrayJson> getConfig()
    {
        return PropertyTreeJson.toJson( authConfig.getConfig() );
    }


    @JsonIgnore
    public AuthConfig getAuthConfig()
    {
        return authConfig;
    }

    public static AuthConfigJson toJson( final AuthConfig authConfig )
    {
        return authConfig == null
            ? null
            : new AuthConfigJson( authConfig.getApplicationKey().toString(), PropertyTreeJson.toJson( authConfig.getConfig() ) );
    }
}