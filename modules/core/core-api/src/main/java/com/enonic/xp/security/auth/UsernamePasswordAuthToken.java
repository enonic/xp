package com.enonic.xp.security.auth;

import com.google.common.annotations.Beta;

@Beta
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
