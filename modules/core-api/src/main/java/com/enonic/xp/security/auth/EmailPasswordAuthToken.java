package com.enonic.xp.security.auth;

import com.google.common.annotations.Beta;

@Beta
public final class EmailPasswordAuthToken
    extends PasswordAuthToken
{
    private String email;

    public String getEmail()
    {
        return this.email;
    }

    public void setEmail( final String email )
    {
        this.email = email;
    }
}
