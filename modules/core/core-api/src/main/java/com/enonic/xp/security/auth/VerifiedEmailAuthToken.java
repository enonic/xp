package com.enonic.xp.security.auth;

import com.google.common.annotations.Beta;

@Beta
public final class VerifiedEmailAuthToken
    extends AuthenticationToken
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
