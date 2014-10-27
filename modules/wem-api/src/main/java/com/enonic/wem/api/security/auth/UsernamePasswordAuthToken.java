package com.enonic.wem.api.security.auth;

public final class UsernamePasswordAuthToken
    extends PasswordAuthToken
{
    private String username;

    public String getUsername()
    {
        return this.username;
    }

    public void setUsername( final String username )
    {
        this.username = username;
    }
}
