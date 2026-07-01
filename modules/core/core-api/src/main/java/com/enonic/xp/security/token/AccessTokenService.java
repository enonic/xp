package com.enonic.xp.security.token;

import java.util.Optional;

import org.jspecify.annotations.NullMarked;

/**
 * Issues and verifies self-issued access tokens (RFC 9068 "at+jwt"), signed with a managed
 * token-signing key.
 * <p>
 * Because the issuer and verifier are the same trust domain, the tokens are symmetric (HS512);
 * the {@code kid} header keeps the format open to a managed keyring and key rotation.
 */
@NullMarked
public interface AccessTokenService
{
    /**
     * Issues a signed access token for the given parameters.
     */
    String issue( AccessTokenParams params );

    /**
     * Verifies a token: signature (algorithm pinned, key resolved by the token's {@code kid}),
     * issuer presence and expiry. Audience is exposed on the result for the caller to enforce.
     *
     * @return the decoded token, or {@link Optional#empty()} if verification fails.
     */
    Optional<AccessToken> verify( String token );
}
