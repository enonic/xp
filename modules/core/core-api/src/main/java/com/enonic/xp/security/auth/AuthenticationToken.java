package com.enonic.xp.security.auth;

import java.util.Objects;

import org.jspecify.annotations.NonNull;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.IdProviderKey;

@PublicApi
public abstract sealed class AuthenticationToken
    permits PasswordAuthToken, VerifiedEmailAuthToken, VerifiedUsernameAuthToken
{
    private final IdProviderKey idProvider;

    protected AuthenticationToken( final @NonNull IdProviderKey idProvider )
    {
        this.idProvider = Objects.requireNonNull( idProvider );
    }

    public final IdProviderKey getIdProvider()
    {
        return this.idProvider;
    }
}
