package com.enonic.xp.security.auth;

public abstract class PasswordAuthToken
    extends AuthenticationToken
{
    private String password;

    public final String getPassword()
    {
        return this.password;
    }

    public final void setPassword( final String password )
    {
        this.password = password;
    }
}
