package com.enonic.xp.security.token;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.annotation.PublicApi;

/**
 * The outcome of verifying a bearer token against this XP's issuer and keys: the token is XP's and
 * holds, it is XP's but does not hold, or it is not XP's at all. A caller that accepts bearer tokens
 * from several issuers lets the last case through to whoever handles them, while the second is
 * answered as a bad token.
 */
@PublicApi
@NullMarked
public sealed interface AccessTokenVerification
    permits AccessTokenVerification.Verified, AccessTokenVerification.Invalid, AccessTokenVerification.InvalidIssuer
{
    /**
     * The token was issued here and holds.
     *
     * @param claims the token's claims, with the guarantee {@link AccessTokenService#verify} describes
     */
    record Verified(AccessTokenClaims claims)
        implements AccessTokenVerification
    {
    }

    /**
     * The token names this XP as its issuer but does not hold: it was signed with a key this XP
     * does not know, was tampered with, is malformed, or has expired. No reason is given, so a
     * rejected token leaks nothing to the client.
     */
    record Invalid()
        implements AccessTokenVerification
    {
    }

    /**
     * The token does not name this XP as its issuer, or does not parse as a token at all. It may
     * be a valid token of some other party.
     */
    record InvalidIssuer()
        implements AccessTokenVerification
    {
    }
}
