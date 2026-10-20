package com.enonic.xp.security.token;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.annotation.PublicApi;

import static java.util.Objects.requireNonNull;

/**
 * An access token as issued: the string to hand to the client and the claims it carries.
 *
 * @param value  the token in its serialized form, to be sent as a bearer token
 * @param claims the claims the token carries
 */
@PublicApi
@NullMarked
public record AccessToken(String value, AccessTokenClaims claims)
{
    public AccessToken
    {
        requireNonNull( value );
        requireNonNull( claims );
    }

    @Override
    public String toString()
    {
        return "AccessToken{" + claims + '}';
    }
}
