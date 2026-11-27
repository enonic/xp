package com.enonic.xp.security.token;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.annotation.PublicApi;

/**
 * The outcome of verifying a bearer token: XP's and valid, XP's but invalid, or not XP's at all.
 * A caller accepting tokens from several issuers passes the last case on and rejects the second.
 */
@PublicApi
@NullMarked
public sealed interface AccessTokenVerification
    permits AccessTokenVerification.Verified, AccessTokenVerification.Invalid, AccessTokenVerification.InvalidIssuer
{
    /**
     * The token was issued here and holds.
     *
     * @param claims the token's claims
     */
    record Verified(AccessTokenClaims claims)
        implements AccessTokenVerification
    {
    }

    /**
     * The token names XP as issuer but does not hold: unknown key, tampered, malformed or expired.
     * No reason is given, so nothing leaks to the client.
     */
    record Invalid()
        implements AccessTokenVerification
    {
    }

    /**
     * The token does not name XP as issuer, or is not a token at all. It may be another issuer's
     * valid token.
     */
    record InvalidIssuer()
        implements AccessTokenVerification
    {
    }
}
