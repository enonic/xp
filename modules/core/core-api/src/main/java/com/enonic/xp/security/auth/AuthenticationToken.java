package com.enonic.xp.security.auth;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.security.IdProviderKey;

import static java.util.Objects.requireNonNull;


@NullMarked
public abstract sealed class AuthenticationToken
    permits PasswordAuthToken, VerifiedEmailAuthToken, VerifiedUsernameAuthToken
{
    private final IdProviderKey idProvider;

    protected AuthenticationToken( final IdProviderKey idProvider )
    {
        this.idProvider = requireNonNull( idProvider );
    }

    public final IdProviderKey getIdProvider()
    {
        return this.idProvider;
    }
}
