package com.enonic.xp.admin.impl.rest.resource.security.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.security.UserStoreAuthConfig;

public final class UserStoreAuthConfigJson
{
    private final UserStoreAuthConfig userStoreAuthConfig;

    @JsonCreator
    public UserStoreAuthConfigJson( @JsonProperty("applicationKey") final String applicationKey,
                                    @JsonProperty("config") final List<PropertyArrayJson> config )
    {
        userStoreAuthConfig = UserStoreAuthConfig.create().
            applicationKey( ApplicationKey.from( applicationKey ) ).
            config( PropertyTreeJson.fromJson( config ) ).
            build();
    }

    public String getApplicationKey()
    {
        return userStoreAuthConfig.getApplicationKey().toString();
    }

    public List<PropertyArrayJson> getConfig()
    {
        return PropertyTreeJson.toJson( userStoreAuthConfig.getConfig() );
    }


    @JsonIgnore
    public UserStoreAuthConfig getUserStoreAuthConfig()
    {
        return userStoreAuthConfig;
    }

    public static UserStoreAuthConfigJson toJson( final UserStoreAuthConfig authConfig )
    {
        return authConfig == null
            ? null
            : new UserStoreAuthConfigJson( authConfig.getApplicationKey().toString(), PropertyTreeJson.toJson( authConfig.getConfig() ) );
    }
}
