package com.enonic.xp.security.auth;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.security.IdProviderKey;

@NullMarked
public abstract sealed class PasswordAuthToken
    extends AuthenticationToken
    permits EmailPasswordAuthToken, UsernamePasswordAuthToken
{
    private final String password;

    protected PasswordAuthToken( final IdProviderKey idProvider, final String password )
    {
        super( idProvider );
        this.password = password;
    }

    public final String getPassword()
    {
        return this.password;
    }
}
