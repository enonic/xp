package com.enonic.xp.portal.impl.idprovider;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableSet;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.core.internal.security.MessageDigests;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleRestriction;
import com.enonic.xp.shared.SharedMap;
import com.enonic.xp.shared.SharedMapService;

/**
 * The state of the authorization code flow, in a shared map so any node serves the next step: an
 * authorization request while the user decides, and an issued code until the client redeems it.
 * Both are single-use and short-lived. A code carries what consent fixed, so redeeming it needs no
 * other state.
 */
@NullMarked
final class AuthorizationCodeStore
{
    static final String MAP_NAME = "com.enonic.xp.portal.idprovider.code";

    /**
     * How long the user has to decide.
     */
    static final int REQUEST_TTL_SECONDS = 600;

    /**
     * How long the client has to redeem a code.
     */
    static final int CODE_TTL_SECONDS = 60;

    /**
     * A PKCE verifier (RFC 7636): 43 to 128 unreserved characters.
     */
    private static final Pattern VERIFIER = Pattern.compile( "[A-Za-z0-9._~-]{43,128}" );

    /**
     * A PKCE challenge: the base64url digest of a verifier, 43 characters.
     */
    private static final Pattern CHALLENGE = Pattern.compile( "[A-Za-z0-9_-]{43}" );

    private static final SecureRandom RANDOM = new SecureRandom();

    private final SharedMap<String, Map<String, Object>> map;

    private final Clock clock;

    AuthorizationCodeStore( final SharedMapService sharedMapService, final Clock clock )
    {
        this.map = sharedMapService.getSharedMap( MAP_NAME );
        this.clock = clock;
    }

    /**
     * An authorization request the user has not decided on, shown to a user for a decision.
     */
    record PendingAuthorization(IdProviderKey idProvider, String id, String clientId, String redirectUri, @Nullable String state,
                                String codeChallenge, Set<String> scope, Set<String> audience, long createdAt, PrincipalKey shownTo)
    {
        PendingAuthorization
        {
            scope = ImmutableSet.copyOf( scope );
            audience = ImmutableSet.copyOf( audience );
        }

        /**
         * The deadline for the decision. Fixed at creation; showing the request again does not move it.
         */
        long expiresAt()
        {
            return createdAt + REQUEST_TTL_SECONDS * 1000L;
        }

        boolean isExpired( final long now )
        {
            return now >= expiresAt();
        }

        /**
         * Whole seconds left until the deadline, at least one, so a write never outlives it.
         */
        int remainingSeconds( final long now )
        {
            return (int) Math.max( 1, ( expiresAt() - now + 999 ) / 1000 );
        }

        private Map<String, Object> toMap()
        {
            final Map<String, Object> value = new HashMap<>();
            value.put( "idProvider", idProvider.toString() );
            value.put( "id", id );
            value.put( "clientId", clientId );
            value.put( "redirectUri", redirectUri );
            value.put( "state", state );
            value.put( "codeChallenge", codeChallenge );
            value.put( "scope", new ArrayList<>( scope ) );
            value.put( "audience", new ArrayList<>( audience ) );
            value.put( "createdAt", createdAt );
            value.put( "shownTo", shownTo.toString() );
            return value;
        }

        @SuppressWarnings("unchecked")
        private static PendingAuthorization fromMap( final Map<String, Object> value )
        {
            return new PendingAuthorization( IdProviderKey.from( (String) value.get( "idProvider" ) ), (String) value.get( "id" ),
                                             (String) value.get( "clientId" ), (String) value.get( "redirectUri" ),
                                             (String) value.get( "state" ), (String) value.get( "codeChallenge" ),
                                             ImmutableSet.copyOf( (List<String>) value.get( "scope" ) ),
                                             ImmutableSet.copyOf( (List<String>) value.get( "audience" ) ), (Long) value.get( "createdAt" ),
                                             PrincipalKey.from( (String) value.get( "shownTo" ) ) );
        }
    }

    /**
     * An authorization code and what redeeming it grants: what consent fixed, and what the token
     * request must match.
     */
    record IssuedCode(IdProviderKey idProvider, String code, String clientId, String redirectUri, String codeChallenge, PrincipalKey subject,
                      Set<String> scope, Set<String> audience, RoleRestriction restriction)
    {
        IssuedCode
        {
            scope = ImmutableSet.copyOf( scope );
            audience = ImmutableSet.copyOf( audience );
        }

        private Map<String, Object> toMap()
        {
            final Map<String, Object> value = new HashMap<>();
            value.put( "idProvider", idProvider.toString() );
            value.put( "code", code );
            value.put( "clientId", clientId );
            value.put( "redirectUri", redirectUri );
            value.put( "codeChallenge", codeChallenge );
            value.put( "subject", subject.toString() );
            value.put( "scope", new ArrayList<>( scope ) );
            value.put( "audience", new ArrayList<>( audience ) );
            value.put( "principals", switch ( restriction )
            {
                case RoleRestriction.None _ -> null;
                case RoleRestriction.To( PrincipalKeys principals ) -> principals.stream().map( PrincipalKey::toString ).toList();
            } );
            return value;
        }

        @SuppressWarnings("unchecked")
        private static IssuedCode fromMap( final Map<String, Object> value )
        {
            final List<String> principals = (List<String>) value.get( "principals" );
            return new IssuedCode( IdProviderKey.from( (String) value.get( "idProvider" ) ), (String) value.get( "code" ),
                                   (String) value.get( "clientId" ), (String) value.get( "redirectUri" ), (String) value.get( "codeChallenge" ),
                                   PrincipalKey.from( (String) value.get( "subject" ) ),
                                   ImmutableSet.copyOf( (List<String>) value.get( "scope" ) ),
                                   ImmutableSet.copyOf( (List<String>) value.get( "audience" ) ), principals == null
                                       ? RoleRestriction.none()
                                       : RoleRestriction.to( principals.stream().map( PrincipalKey::from ).collect( PrincipalKeys.collector() ) ) );
        }
    }

    /**
     * Records an authorization request as it is shown to a user for a decision.
     */
    PendingAuthorization createRequest( final IdProviderKey idProvider, final String clientId, final String redirectUri,
                                        final @Nullable String state, final String codeChallenge, final Set<String> scope,
                                        final Set<String> audience, final PrincipalKey shownTo )
    {
        final PendingAuthorization pending =
            new PendingAuthorization( idProvider, newToken(), clientId, redirectUri, state, codeChallenge, scope, audience, clock.millis(),
                                      shownTo );
        map.set( requestKey( idProvider, pending.id() ), pending.toMap(), REQUEST_TTL_SECONDS );
        return pending;
    }

    /**
     * Takes the authorization request with the id for the user's decision, so a decision is
     * recorded once and only by the user the request was shown to. Empty if there is none, it
     * timed out, or it was shown to another user.
     */
    Optional<PendingAuthorization> takeRequest( final IdProviderKey idProvider, final String id, final PrincipalKey subject )
    {
        final String key = requestKey( idProvider, id );
        final Map<String, Object> current = map.get( key );
        if ( current == null )
        {
            return Optional.empty();
        }
        final long now = clock.millis();
        final List<PendingAuthorization> taken = new ArrayList<>( 1 );
        map.modify( key, value -> {
            if ( value == null )
            {
                return null;
            }
            final PendingAuthorization pending = PendingAuthorization.fromMap( value );
            if ( pending.isExpired( now ) )
            {
                return null;
            }
            if ( !subject.equals( pending.shownTo() ) )
            {
                return value;
            }
            taken.add( pending );
            return null;
        }, PendingAuthorization.fromMap( current ).remainingSeconds( now ) );
        return taken.stream().findFirst();
    }

    /**
     * Shows the authorization request with the id to another user, whose decision then counts.
     * Empty if there is none, or it timed out.
     */
    Optional<PendingAuthorization> showRequest( final IdProviderKey idProvider, final String id, final PrincipalKey subject )
    {
        final String key = requestKey( idProvider, id );
        final Map<String, Object> current = map.get( key );
        if ( current == null )
        {
            return Optional.empty();
        }
        final long now = clock.millis();
        final List<PendingAuthorization> shown = new ArrayList<>( 1 );
        map.modify( key, value -> {
            if ( value == null )
            {
                return null;
            }
            final PendingAuthorization pending = PendingAuthorization.fromMap( value );
            if ( pending.isExpired( now ) )
            {
                return null;
            }
            final PendingAuthorization reshown =
                new PendingAuthorization( pending.idProvider(), pending.id(), pending.clientId(), pending.redirectUri(), pending.state(),
                                          pending.codeChallenge(), pending.scope(), pending.audience(), pending.createdAt(), subject );
            shown.add( reshown );
            return reshown.toMap();
        }, PendingAuthorization.fromMap( current ).remainingSeconds( now ) );
        return shown.stream().findFirst();
    }

    /**
     * Issues a code for an approved request, with what consent fixed.
     */
    IssuedCode issue( final PendingAuthorization request, final PrincipalKey subject, final Set<String> approvedScope,
                      final RoleRestriction restriction )
    {
        final IssuedCode issued =
            new IssuedCode( request.idProvider(), newToken(), request.clientId(), request.redirectUri(), request.codeChallenge(), subject,
                            approvedScope, request.audience(), restriction );
        map.set( codeKey( request.idProvider(), issued.code() ), issued.toMap(), CODE_TTL_SECONDS );
        return issued;
    }

    /**
     * Takes the code, so it is redeemed once whatever the outcome. Empty if it is unknown, used
     * or timed out.
     */
    Optional<IssuedCode> redeem( final IdProviderKey idProvider, final String code )
    {
        return take( codeKey( idProvider, code ) ).map( IssuedCode::fromMap );
    }

    /**
     * Whether the string is a PKCE challenge as S256 produces it.
     */
    static boolean isChallenge( final @Nullable String codeChallenge )
    {
        return codeChallenge != null && CHALLENGE.matcher( codeChallenge ).matches();
    }

    /**
     * Whether the verifier is well-formed and its S256 digest is the challenge (RFC 7636).
     */
    static boolean verifies( final @Nullable String codeVerifier, final @Nullable String codeChallenge )
    {
        if ( codeVerifier == null || codeChallenge == null || !VERIFIER.matcher( codeVerifier ).matches() )
        {
            return false;
        }
        final MessageDigest digest = MessageDigests.sha256();
        digest.update( codeVerifier.getBytes( StandardCharsets.US_ASCII ) );
        final String computed = Base64.getUrlEncoder().withoutPadding().encodeToString( digest.digest() );
        return MessageDigest.isEqual( computed.getBytes( StandardCharsets.US_ASCII ), codeChallenge.getBytes( StandardCharsets.US_ASCII ) );
    }

    private Optional<Map<String, Object>> take( final String key )
    {
        if ( map.get( key ) == null )
        {
            return Optional.empty();
        }
        final List<Map<String, Object>> taken = new ArrayList<>( 1 );
        map.modify( key, value -> {
            if ( value != null )
            {
                taken.add( value );
            }
            return null;
        } );
        return taken.isEmpty() ? Optional.empty() : Optional.of( taken.getFirst() );
    }

    private static String requestKey( final IdProviderKey idProvider, final String id )
    {
        return idProvider + ":ar:" + id;
    }

    private static String codeKey( final IdProviderKey idProvider, final String code )
    {
        return idProvider + ":ac:" + code;
    }

    private static String newToken()
    {
        final byte[] bytes = new byte[32];
        RANDOM.nextBytes( bytes );
        return Base64.getUrlEncoder().withoutPadding().encodeToString( bytes );
    }
}
