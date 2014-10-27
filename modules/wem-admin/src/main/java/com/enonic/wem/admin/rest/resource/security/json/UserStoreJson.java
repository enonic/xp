package com.enonic.wem.admin.rest.resource.security.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.security.UserStore;

@SuppressWarnings("UnusedDeclaration")
public class UserStoreJson
{
    private final UserStore userStore;

    @JsonCreator
    public UserStoreJson( @JsonProperty("displayName") final String displayName, @JsonProperty("key") final UserStoreKeyJson keyJson )
    {

        this.userStore = UserStore.newUserStore().displayName( displayName ).key( keyJson.getUserStoreKey() ).build();
    }

    public UserStoreJson( final UserStore userStore )
    {
        this.userStore = userStore;

    }

    public String getDisplayName()
    {
        return userStore.getDisplayName();
    }

    public String getKey()
    {
        return userStore.getKey().toString();
    }
}
