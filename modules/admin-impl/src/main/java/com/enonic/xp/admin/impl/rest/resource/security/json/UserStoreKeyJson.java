package com.enonic.xp.admin.impl.rest.resource.security.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.security.UserStoreKey;

@SuppressWarnings("UnusedDeclaration")
public class UserStoreKeyJson
{
    private final UserStoreKey userStoreKey;

    @JsonCreator
    public UserStoreKeyJson( @JsonProperty("id") final String id )
    {

        this.userStoreKey = UserStoreKey.from( id );
    }

    public UserStoreKeyJson( final UserStoreKey userStoreKey )
    {
        this.userStoreKey = userStoreKey;

    }

    public UserStoreKey getUserStoreKey()
    {
        return userStoreKey;
    }
}
