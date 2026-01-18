package com.enonic.xp.security.auth;

import com.enonic.xp.security.IdProviderKey;

public abstract sealed class PasswordAuthToken
    extends AuthenticationToken
    permits EmailPasswordAuthToken, UsernamePasswordAuthToken
{
    private String password;

    protected PasswordAuthToken( final IdProviderKey idProvider )
    {
        super( idProvider );
    }

    public final String getPassword()
    {
        return this.password;
    }

    public final void setPassword( final String password )
    {
        this.password = password;
    }
}
