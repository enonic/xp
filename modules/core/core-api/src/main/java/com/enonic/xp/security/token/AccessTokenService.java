package com.enonic.xp.security.token;

import java.util.Optional;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.IdProviderKey;

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

    /**
     * Registers a client without storing it (RFC 7591): the returned client's id is its record,
     * signed with the token keys, and is what the client sends as {@code client_id} from then on.
     * The registration lasts a fixed time, after which the id reads as unregistered and the client
     * registers again.
     */
    Client registerClient( RegisterClientParams params );

    /**
     * The client an id from {@link #registerClient} describes, while the registration holds. Empty
     * for any other id, for an id of another id provider, a signature that does not verify or a
     * registration that has run out.
     */
    Optional<Client> getRegisteredClient( IdProviderKey idProvider, String clientId );

    /**
     * Whether an id has the shape of one {@link #registerClient} issues. Such an id names a client
     * only while {@link #getRegisteredClient} finds it; afterwards it names no client at all, and
     * is not an ordinary client id either.
     */
    boolean isSelfRegisteredClientId( String clientId );
}
