package com.enonic.xp.admin.impl.rest.resource.auth;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class LoginJson
{
    private final boolean rememberMe;

    private final String user;

    private final String password;

    @JsonCreator
    public LoginJson( @JsonProperty("user") final String user, @JsonProperty("password") final String password,
                      @JsonProperty("rememberMe") final String rememberMeParam )
    {
        this.user = user;
        this.password = password;
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

    public boolean isRememberMe()
    {
        return rememberMe;
    }
}
