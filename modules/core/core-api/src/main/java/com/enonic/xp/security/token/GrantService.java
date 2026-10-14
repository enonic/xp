package com.enonic.xp.security.token;

import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.PrincipalKey;

/**
 * Keeps a user's grants and redeems them with refresh tokens. The grant is the one durable record
 * of an approval; access tokens are issued from it and never kept. Grants are stored under the
 * user and go when the user goes.
 * <p>
 * Refresh tokens rotate (OAuth 2.1): a redeem after the rotation window returns a new token and
 * retires the old one. A retired token presented again within a short grace period gets the same
 * successor back, so a client that lost the response can retry. After the grace, or for a token
 * retired by an earlier rotation, the token counts as copied and the grant is revoked. A token
 * that was never the grant's does nothing to it.
 * <p>
 * The grant is the authority for its access tokens: a token is accepted only while its grant
 * exists, and runs with the grant's current restriction. Revoking or changing a grant therefore
 * applies at the tokens' next use.
 * <p>
 * A grant expires after a lifetime the caller may set. A user holds a bounded number of grants,
 * in all and per client; beyond the bound the oldest is dropped.
 * <p>
 * Concurrent redeems of one token, from any node, get the same answer.
 */
@PublicApi
@NullMarked
public interface GrantService
{
    /**
     * Records a grant and returns it with its first refresh token.
     */
    GrantWithRefreshToken create( CreateGrantParams params );

    /**
     * Redeems a refresh token. Returns the grant with the token to use next time: the same one within
     * the rotation window, a new one after it, or the successor already issued if a retired token is
     * retried within grace. Empty if the token is unknown, expired or replayed, in which case the grant
     * is revoked. A redeem whose conditions the grant does not meet is empty and changes nothing.
     */
    Optional<GrantWithRefreshToken> redeem( RedeemRefreshTokenParams params );

    /**
     * The user's current grants, newest first.
     */
    List<Grant> list( PrincipalKey user );

    /**
     * The user's grant with the given id, or empty if the user has no such grant or it has expired.
     */
    Optional<Grant> get( PrincipalKey user, String grantId );

    /**
     * Changes a grant's scopes, audiences or restriction. Returns the changed grant, or empty if
     * the user has no such grant or it has expired.
     */
    Optional<Grant> update( UpdateGrantParams params );

    /**
     * Revokes one grant; false when the user has no such grant.
     */
    boolean revoke( PrincipalKey user, String grantId );

    /**
     * Revokes every grant of the user.
     */
    void revokeAll( PrincipalKey user );
}
