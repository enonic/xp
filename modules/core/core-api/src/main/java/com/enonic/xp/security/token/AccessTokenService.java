package com.enonic.xp.security.token;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.annotation.PublicApi;

/**
 * Issues and verifies XP's own access tokens. A token is a JWT in JWS compact form with the
 * RFC 9068 header and claim layout, signed with HS512, the only supported algorithm; the
 * profile's RS256 baseline is not implemented, since only XP verifies its tokens. Issued tokens
 * are not stored: a token is verified by its signature and lifetime alone. Tokens are therefore
 * short-lived, and longer access goes through a revocable grant that issues fresh ones. A
 * lifetime is a whole number of seconds, at least one.
 */
@PublicApi
@NullMarked
public interface AccessTokenService
{
    /**
     * Issues a token for the given subject and claims, valid from now for the requested lifetime,
     * or the default one.
     */
    AccessToken issue( IssueAccessTokenParams params );

    /**
     * Verifies a token. {@link AccessTokenVerification.Verified} means XP issued it and it holds;
     * {@link AccessTokenVerification.Invalid} means it names XP as issuer but does not hold;
     * {@link AccessTokenVerification.InvalidIssuer} means it is someone else's token.
     */
    AccessTokenVerification verify( VerifyAccessTokenParams params );

    /**
     * The issuer name written into every token ({@code token.issuer}). It also names XP as an
     * audience: a token issued for no particular resource names it, and a vhost declaring no
     * audience accepts tokens naming it.
     */
    String getIssuer();
}
