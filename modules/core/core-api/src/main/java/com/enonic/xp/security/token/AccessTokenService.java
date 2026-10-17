package com.enonic.xp.security.token;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.annotation.PublicApi;

/**
 * Issues and verifies the access tokens XP signs itself. A token is a signed, self-contained
 * statement that a user granted a client access, carrying the audience, scope and principal
 * restriction that were granted: a JWT (RFC 7519) in JWS compact form (RFC 7515) under an
 * XP-specific profile, which names its claims and the single algorithm it accepts; it is not the
 * RFC 9068 access-token profile. Nothing about an issued token is kept: a token verifies by its
 * signature and lifetime alone. Access tokens are therefore short-lived, and longer-lived access
 * goes through a revocable grant that issues fresh ones. A lifetime is a whole number of seconds,
 * at least one.
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
     * Verifies a token: {@link AccessTokenVerification.Verified} when it was issued here and holds,
     * {@link AccessTokenVerification.Invalid} when it names this issuer but does not hold, and
     * {@link AccessTokenVerification.InvalidIssuer} when it is some other party's token. The claims
     * a verified result carries are the token's own, read after its signature was checked against
     * a key of this XP and its lifetime found to be running: they may be relied on as what this XP
     * issued, and nothing else hands out claims with that guarantee.
     */
    AccessTokenVerification verify( VerifyAccessTokenParams params );
}
