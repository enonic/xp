package com.enonic.wem.admin.rest.resource.auth;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.security.UserStoreKey;

public final class LoginJson
{
    private final boolean rememberMe;

    private final String user;

    private final String password;

    private final UserStoreKey userStoreKey;

    @JsonCreator
    public LoginJson( @JsonProperty("user") final String user, @JsonProperty("password") final String password,
                      @JsonProperty("userStore") final String userStoreKey, @JsonProperty("rememberMe") final String rememberMeParam )
    {
        this.user = user;
        this.password = password;
        this.userStoreKey = new UserStoreKey( userStoreKey );
        this.rememberMe = Boolean.valueOf( rememberMeParam );
    }

    public String getUser()
    {
        return user;
    }

    public String getPassword()
    {
        return password;
    }

    public UserStoreKey getUserStoreKey()
    {
        return userStoreKey;
    }

    public boolean isRememberMe()
    {
        return rememberMe;
    }
}
