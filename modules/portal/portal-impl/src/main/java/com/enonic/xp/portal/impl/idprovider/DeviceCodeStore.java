package com.enonic.xp.portal.impl.idprovider;

import java.security.SecureRandom;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


import com.google.common.collect.ImmutableSet;


import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleRestriction;
import com.enonic.xp.shared.SharedMap;
import com.enonic.xp.shared.SharedMapService;

/**
 * The cluster's pending device authorizations (RFC 8628): created when a client asks for a code,
 * resolved when a user approves or denies, consumed when the client polls. Kept in a shared map
 * with the code lifetime as TTL, so an abandoned request disappears on its own.
 * <p>
 * Values are plain maps and lists of standard Java types, which the shared map carries between
 * nodes without class-loading concerns.
 */
final class DeviceCodeStore
{
    static final String MAP_NAME = "com.enonic.xp.portal.idprovider.device";

    static final int CODE_TTL_SECONDS = 600;

    static final int POLL_INTERVAL_SECONDS = 5;

    /**
     * The base-20 alphabet of RFC 8628 section 6.1: consonants without Y, so a code never spells a
     * word. Entered codes are matched case-insensitively, ignoring hyphens and spaces.
     */
    private static final char[] USER_CODE_ALPHABET = "BCDFGHJKLMNPQRSTVWXZ".toCharArray();

    private static final SecureRandom RANDOM = new SecureRandom();

    private final SharedMap<String, Map<String, Object>> map;

    private final Clock clock;

    DeviceCodeStore( final SharedMapService sharedMapService, final Clock clock )
    {
        this.map = sharedMapService.getSharedMap( MAP_NAME );
        this.clock = clock;
    }

    enum Status
    {
        PENDING, APPROVED, DENIED
    }

    /**
     * A device authorization request, as created and as resolved.
     */
    record Pending(IdProviderKey idProvider, String deviceCode, String userCode, String clientId, Set<String> scope, Set<String> audience,
                   Status status, PrincipalKey subject, Set<String> approvedScope, RoleRestriction approvedRestriction,
                   long createdAt, long lastPolledAt)
    {
        /**
         * The deadline advertised to the client. Fixed at creation; nothing extends it.
         */
        long expiresAt()
        {
            return createdAt + CODE_TTL_SECONDS * 1000L;
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

        Pending
        {
            scope = ImmutableSet.copyOf( scope );
            audience = ImmutableSet.copyOf( audience );
            approvedScope = ImmutableSet.copyOf( approvedScope );
        }

        private Map<String, Object> toMap()
        {
            final Map<String, Object> value = new HashMap<>();
            value.put( "idProvider", idProvider.toString() );
            value.put( "deviceCode", deviceCode );
            value.put( "userCode", userCode );
            value.put( "clientId", clientId );
            value.put( "scope", new ArrayList<>( scope ) );
            value.put( "audience", new ArrayList<>( audience ) );
            value.put( "status", status.name() );
            value.put( "subject", subject == null ? null : subject.toString() );
            value.put( "approvedScope", new ArrayList<>( approvedScope ) );
            value.put( "approvedPrincipals", switch ( approvedRestriction )
            {
                case RoleRestriction.None _ -> null;
                case RoleRestriction.To( PrincipalKeys principals ) -> principals.stream().map( PrincipalKey::toString ).toList();
            } );
            value.put( "createdAt", createdAt );
            value.put( "lastPolledAt", lastPolledAt );
            return value;
        }

        @SuppressWarnings("unchecked")
        private static Pending fromMap( final Map<String, Object> value )
        {
            final String subject = (String) value.get( "subject" );
            final List<String> approvedPrincipals = (List<String>) value.get( "approvedPrincipals" );
            return new Pending( IdProviderKey.from( (String) value.get( "idProvider" ) ), (String) value.get( "deviceCode" ),
                                (String) value.get( "userCode" ), (String) value.get( "clientId" ),
                                ImmutableSet.copyOf( (List<String>) value.get( "scope" ) ),
                                ImmutableSet.copyOf( (List<String>) value.get( "audience" ) ), Status.valueOf( (String) value.get( "status" ) ),
                                subject == null ? null : PrincipalKey.from( subject ),
                                ImmutableSet.copyOf( (List<String>) value.get( "approvedScope" ) ), approvedPrincipals == null
                                    ? RoleRestriction.none()
                                    : RoleRestriction.to(
                                        approvedPrincipals.stream().map( PrincipalKey::from ).collect( PrincipalKeys.collector() ) ),
                                (Long) value.get( "createdAt" ), (Long) value.get( "lastPolledAt" ) );
        }
    }

    /**
     * What a poll found. A resolved request is consumed by the poll, so an approval is redeemed
     * exactly once.
     */
    sealed interface Outcome
        permits Outcome.Waiting, Outcome.SlowDown, Outcome.Approved, Outcome.Denied, Outcome.Expired, Outcome.ClientMismatch
    {
        /**
         * The user has not decided yet.
         */
        record Waiting()
            implements Outcome
        {
        }

        /**
         * Polled faster than the interval allows.
         */
        record SlowDown()
            implements Outcome
        {
        }

        /**
         * Approved by {@link Pending#subject()}: the request is consumed and returned.
         */
        record Approved(Pending request)
            implements Outcome
        {
        }

        /**
         * Denied: the request is consumed.
         */
        record Denied()
            implements Outcome
        {
        }

        /**
         * Unknown, already consumed, or timed out.
         */
        record Expired()
            implements Outcome
        {
        }

        /**
         * Approved, but for another client. The request is left for the right client to collect.
         */
        record ClientMismatch()
            implements Outcome
        {
        }
    }

    Pending create( final IdProviderKey idProvider, final String clientId, final Set<String> scope, final Set<String> audience )
    {
        final long now = clock.millis();
        final String deviceCode = newDeviceCode();
        final String userCode = newUserCode();
        final Pending pending =
            new Pending( idProvider, deviceCode, userCode, clientId, scope, audience, Status.PENDING, null, Set.of(), RoleRestriction.none(), now, 0 );
        map.set( key( idProvider, deviceCode ), pending.toMap(), CODE_TTL_SECONDS );
        final Map<String, Object> ref = new HashMap<>();
        ref.put( "deviceCode", deviceCode );
        map.set( userCodeKey( idProvider, userCode ), ref, CODE_TTL_SECONDS );
        return pending;
    }

    Optional<Pending> findByUserCode( final IdProviderKey idProvider, final String userCode )
    {
        final Map<String, Object> ref = map.get( userCodeKey( idProvider, normalizeUserCode( userCode ) ) );
        if ( ref == null )
        {
            return Optional.empty();
        }
        final long now = clock.millis();
        return Optional.ofNullable( map.get( key( idProvider, (String) ref.get( "deviceCode" ) ) ) )
            .map( Pending::fromMap )
            .filter( pending -> !pending.isExpired( now ) );
    }

    /**
     * Records the user's decision, with the scopes kept and the restriction they imply; false if
     * no request is pending for the user code. The restriction is fixed here, at consent.
     */
    boolean resolve( final IdProviderKey idProvider, final String userCode, final boolean approved, final PrincipalKey subject,
                     final Set<String> approvedScope, final RoleRestriction approvedRestriction )
    {
        final Optional<Pending> found = findByUserCode( idProvider, userCode );
        if ( found.isEmpty() )
        {
            return false;
        }
        final boolean[] resolved = new boolean[1];
        map.modify( key( idProvider, found.get().deviceCode() ), value -> {
            if ( value == null )
            {
                return null;
            }
            final Pending pending = Pending.fromMap( value );
            if ( pending.isExpired( clock.millis() ) )
            {
                return null;
            }
            if ( pending.status() != Status.PENDING )
            {
                return value;
            }
            resolved[0] = true;
            return new Pending( pending.idProvider(), pending.deviceCode(), pending.userCode(), pending.clientId(), pending.scope(),
                                pending.audience(), approved ? Status.APPROVED : Status.DENIED, approved ? subject : null,
                                approved ? approvedScope : Set.of(), approved ? approvedRestriction : RoleRestriction.none(),
                                pending.createdAt(),
                                pending.lastPolledAt() ).toMap();
        }, found.get().remainingSeconds( clock.millis() ) );
        return resolved[0];
    }

    /**
     * The client's poll. Polling faster than the interval answers slow-down. A request past its
     * deadline is expired whatever its status; polling never moves the deadline. An approval is
     * consumed only by the client the request was made for, checked under the same lock, so a wrong
     * client's poll cannot destroy it.
     */
    Outcome poll( final IdProviderKey idProvider, final String deviceCode, final String clientId )
    {
        final Map<String, Object> current = map.get( key( idProvider, deviceCode ) );
        if ( current == null )
        {
            return new Outcome.Expired();
        }
        final Outcome[] result = {new Outcome.Expired()};
        map.modify( key( idProvider, deviceCode ), value -> {
            if ( value == null )
            {
                return null;
            }
            final Pending pending = Pending.fromMap( value );
            final long now = clock.millis();
            if ( pending.isExpired( now ) )
            {
                map.delete( userCodeKey( idProvider, pending.userCode() ) );
                return null;
            }
            switch ( pending.status() )
            {
                case APPROVED ->
                {
                    if ( !clientId.equals( pending.clientId() ) )
                    {
                        result[0] = new Outcome.ClientMismatch();
                        return value;
                    }
                    result[0] = new Outcome.Approved( pending );
                    map.delete( userCodeKey( idProvider, pending.userCode() ) );
                    return null;
                }
                case DENIED ->
                {
                    result[0] = new Outcome.Denied();
                    map.delete( userCodeKey( idProvider, pending.userCode() ) );
                    return null;
                }
                case PENDING ->
                {
                    final boolean tooFast = now - pending.lastPolledAt() < POLL_INTERVAL_SECONDS * 1000L;
                    result[0] = tooFast ? new Outcome.SlowDown() : new Outcome.Waiting();
                    final Map<String, Object> updated = new HashMap<>( value );
                    updated.put( "lastPolledAt", now );
                    return updated;
                }
            }
            return value;
        }, Pending.fromMap( current ).remainingSeconds( clock.millis() ) );
        return result[0];
    }

    static String normalizeUserCode( final String userCode )
    {
        return userCode == null ? "" : userCode.trim().toUpperCase( java.util.Locale.ROOT ).replace( "-", "" ).replace( " ", "" );
    }

    static String formatUserCode( final String userCode )
    {
        return userCode.length() == 8 ? userCode.substring( 0, 4 ) + "-" + userCode.substring( 4 ) : userCode;
    }

    private static String key( final IdProviderKey idProvider, final String deviceCode )
    {
        return idProvider + ":dc:" + deviceCode;
    }

    private static String userCodeKey( final IdProviderKey idProvider, final String userCode )
    {
        return idProvider + ":uc:" + userCode;
    }

    private static String newDeviceCode()
    {
        final byte[] bytes = new byte[32];
        RANDOM.nextBytes( bytes );
        return Base64.getUrlEncoder().withoutPadding().encodeToString( bytes );
    }

    private static String newUserCode()
    {
        final StringBuilder code = new StringBuilder( 8 );
        for ( int i = 0; i < 8; i++ )
        {
            code.append( USER_CODE_ALPHABET[RANDOM.nextInt( USER_CODE_ALPHABET.length )] );
        }
        return code.toString();
    }
}
