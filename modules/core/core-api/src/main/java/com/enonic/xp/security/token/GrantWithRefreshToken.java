package com.enonic.xp.security.token;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.annotation.PublicApi;

import static java.util.Objects.requireNonNull;

/**
 * A grant together with the refresh token that currently redeems it.
 *
 * @param grant        the grant
 * @param refreshToken the refresh token: a credential, to hand to the client and never log or store
 */
@PublicApi
@NullMarked
public record GrantWithRefreshToken(Grant grant, String refreshToken)
{
    public GrantWithRefreshToken
    {
        requireNonNull( grant );
        requireNonNull( refreshToken );
    }

    @Override
    public String toString()
    {
        return "GrantWithRefreshToken{" + grant + '}';
    }
}
