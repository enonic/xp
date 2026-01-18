package com.enonic.xp.security.auth;

import java.util.Objects;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.IdProviderKey;

@PublicApi
@NullMarked
public abstract sealed class AuthenticationToken
    permits PasswordAuthToken, VerifiedEmailAuthToken, VerifiedUsernameAuthToken
{
    private final IdProviderKey idProvider;

    protected AuthenticationToken( final IdProviderKey idProvider )
    {
        this.idProvider = Objects.requireNonNull( idProvider );
    }

    public final IdProviderKey getIdProvider()
    {
        return this.idProvider;
    }
}
