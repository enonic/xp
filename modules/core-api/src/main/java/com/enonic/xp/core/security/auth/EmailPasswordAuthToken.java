package com.enonic.xp.core.security.auth;

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
