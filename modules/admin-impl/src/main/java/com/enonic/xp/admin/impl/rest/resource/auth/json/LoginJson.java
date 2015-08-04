package com.enonic.xp.admin.impl.rest.resource.auth.json;

public final class LoginJson
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
