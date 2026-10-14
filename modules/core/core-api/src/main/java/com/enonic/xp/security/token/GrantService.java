package com.enonic.xp.security.token;

import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.PrincipalKey;

/**
 * Keeps a user's grants and redeems them with refresh tokens. The grant is the one durable record
 * of an approval; access tokens are issued from it and never kept. Grants are stored under the
 * user, apart from the user's own data, and go when the user goes.
 * <p>
 * Refresh tokens rotate (RFC 6749 section 10.4, OAuth 2.1): redeeming one past a rotation window
 * returns a new one and retires the old. A retired token presented again within a short grace
 * after its rotation is read as a lost response and gets the same successor back, as often as
 * asked, with nothing written; a retired token presented after that, or any token retired by an
 * earlier rotation, means it was copied, and the whole grant is revoked. Every token a grant ever
 * issued is recognised as the grant's for as long as the grant lives, so no amount of rotation
 * ages a copied token out of detection; a token that was never the grant's does nothing to it.
 * <p>
 * Revocation needs nothing kept about access tokens. The user's grants belong to the user's
 * {@link #getSid sid}, an opaque random id every access token issued under them carries.
 * Revoking a grant stops it refreshing; revoking all of a user's grants retires the sid, and a
 * token carrying a retired sid is rejected by whoever checks tokens. A user deleted and created
 * again under the same key gets a new sid too, so tokens of the former user do not pass as the
 * new one's.
 * <p>
 * A grant expires after a lifetime the caller may set, and a user holds a bounded number of
 * grants in all and per client: beyond a bound, the oldest is dropped.
 * <p>
 * Two redeems of one token at the same instant, from any nodes of a cluster, get the same
 * answer; a grant is never half written.
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
     * Redeems a refresh token: returns the grant with the token to use next time (the same one
     * within the rotation window, a new one after it, the successor already issued when a
     * retired token is retried within grace), or empty when the token is unknown, expired, or a
     * retired token being replayed, in which case the grant is gone too.
     */
    Optional<GrantWithRefreshToken> redeem( String refreshToken );

    /**
     * The user's current grants, newest first.
     */
    List<Grant> list( PrincipalKey user );

    /**
     * Revokes one grant; false when the user has no such grant.
     */
    boolean revoke( PrincipalKey user, String grantId );

    /**
     * Revokes every grant of the user and retires the user's sid: every access token issued so
     * far is to be rejected, and the next grant brings a new sid.
     */
    void revokeAll( PrincipalKey user );

    /**
     * The user's current sid: the id an access token of the user must carry to be accepted,
     * however valid its signature. Empty when the user has no sid, including after
     * {@link #revokeAll} or deletion of the user; then no token of the user is to be accepted.
     * Revoking individual grants does not retire the sid.
     */
    Optional<String> getSid( PrincipalKey user );
}
