package com.enonic.xp.security.auth;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.security.IdProviderKey;

import static java.util.Objects.requireNonNull;

@NullMarked
public abstract sealed class PasswordAuthToken
    extends AuthenticationToken
    permits EmailPasswordAuthToken, UsernamePasswordAuthToken
{
    private final String password;

    protected PasswordAuthToken( final IdProviderKey idProvider, final String password )
    {
        super( idProvider );
        this.password = requireNonNull( password, "password cannot be null" );
    }

    public final String getPassword()
    {
        return this.password;
    }
}
