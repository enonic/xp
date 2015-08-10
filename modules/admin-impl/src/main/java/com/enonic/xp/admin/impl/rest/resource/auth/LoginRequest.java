package com.enonic.xp.admin.impl.rest.resource.auth;

public final class LoginRequest
{
    protected boolean rememberMe;

    protected String user;

    protected String password;

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
