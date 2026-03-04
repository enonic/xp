package com.enonic.xp.security.auth;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.security.IdProviderKey;


@NullMarked
public final class UsernamePasswordAuthToken
    extends PasswordAuthToken
{
    private final String username;

    public UsernamePasswordAuthToken( final IdProviderKey idProvider, final String username, final String password )
    {
        super( idProvider, password );
        this.username = username;
    }

    public String getUsername()
    {
        return this.username;
    }
}
