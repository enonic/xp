package com.enonic.xp.portal.impl.idprovider;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
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

    AuthorizationCodeStore( final SharedMapService sharedMapService )
    {
        this.map = sharedMapService.getSharedMap( MAP_NAME );
    }

    /**
     * An authorization request the user has not decided on.
     */
    record PendingAuthorization(IdProviderKey idProvider, String id, String clientId, String redirectUri, @Nullable String state,
                                String codeChallenge, Set<String> scope, Set<String> audience)
    {
        PendingAuthorization
        {
            scope = ImmutableSet.copyOf( scope );
            audience = ImmutableSet.copyOf( audience );
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
            return value;
        }

        @SuppressWarnings("unchecked")
        private static PendingAuthorization fromMap( final Map<String, Object> value )
        {
            return new PendingAuthorization( IdProviderKey.from( (String) value.get( "idProvider" ) ), (String) value.get( "id" ),
                                             (String) value.get( "clientId" ), (String) value.get( "redirectUri" ),
                                             (String) value.get( "state" ), (String) value.get( "codeChallenge" ),
                                             ImmutableSet.copyOf( (List<String>) value.get( "scope" ) ),
                                             ImmutableSet.copyOf( (List<String>) value.get( "audience" ) ) );
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
     * Keeps an authorization request while the user decides.
     */
    PendingAuthorization createRequest( final IdProviderKey idProvider, final String clientId, final String redirectUri,
                                        final @Nullable String state, final String codeChallenge, final Set<String> scope,
                                        final Set<String> audience )
    {
        final PendingAuthorization pending =
            new PendingAuthorization( idProvider, newToken(), clientId, redirectUri, state, codeChallenge, scope, audience );
        map.set( requestKey( idProvider, pending.id() ), pending.toMap(), REQUEST_TTL_SECONDS );
        return pending;
    }

    /**
     * Takes the authorization request with the id, so a decision is recorded once. Empty if there
     * is none, or it timed out.
     */
    Optional<PendingAuthorization> takeRequest( final IdProviderKey idProvider, final String id )
    {
        return take( requestKey( idProvider, id ) ).map( PendingAuthorization::fromMap );
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
