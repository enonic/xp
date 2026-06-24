package com.enonic.xp.core.impl.security.token;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.token.DeviceAuthService;
import com.enonic.xp.security.token.DeviceAuthorization;
import com.enonic.xp.security.token.DeviceAuthorizationParams;
import com.enonic.xp.security.token.DeviceAuthorizationPoll;
import com.enonic.xp.security.token.DeviceAuthorizationState;
import com.enonic.xp.shared.SharedMap;
import com.enonic.xp.shared.SharedMapService;

@Component(service = DeviceAuthService.class)
@NullMarked
public class DeviceAuthServiceImpl
    implements DeviceAuthService
{
    private static final String MAP_PREFIX = "com.enonic.xp.security.deviceauth.";

    private static final String USER_CODE_INDEX_PREFIX = "uc:";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final Base64.Encoder B64URL_NOPAD = Base64.getUrlEncoder().withoutPadding();

    private static final char[] USER_CODE_ALPHABET = "BCDFGHJKLMNPQRSTVWXZ".toCharArray();

    private final SharedMapService sharedMapService;

    @Activate
    public DeviceAuthServiceImpl( @Reference final SharedMapService sharedMapService )
    {
        this.sharedMapService = sharedMapService;
    }

    @Override
    public DeviceAuthorization start( final DeviceAuthorizationParams params )
    {
        final SharedMap<String, Object> map = getMap( params.getIdProvider() );
        final String deviceCode = generateDeviceCode();
        final String userCode = generateUserCode();
        final long now = System.currentTimeMillis();

        final int ttlSeconds = (int) params.getTtl().toSeconds();
        final int pollIntervalSeconds = (int) params.getPollInterval().toSeconds();

        final HashMap<String, Object> record = new HashMap<>();
        record.put( "status", "pending" );
        record.put( "userCode", userCode );
        record.put( "clientId", nullToEmpty( params.getClientId() ) );
        record.put( "scope", nullToEmpty( params.getScope() ) );
        record.put( "audience", nullToEmpty( params.getAudience() ) );
        record.put( "createdAt", now );
        record.put( "ttlSeconds", ttlSeconds );
        record.put( "pollIntervalSeconds", pollIntervalSeconds );
        record.put( "lastPolledAt", 0L );

        map.set( deviceCode, record, ttlSeconds );
        map.set( USER_CODE_INDEX_PREFIX + userCode, deviceCode, ttlSeconds );

        return new DeviceAuthorization( deviceCode, userCode, ttlSeconds, pollIntervalSeconds );
    }

    @Override
    public Optional<String> findByUserCode( final IdProviderKey idProvider, final String userCode )
    {
        final SharedMap<String, Object> map = getMap( idProvider );
        final Object deviceCode = map.get( USER_CODE_INDEX_PREFIX + userCode );
        if ( !( deviceCode instanceof String ) )
        {
            return Optional.empty();
        }
        final Object record = map.get( (String) deviceCode );
        if ( !( record instanceof Map ) || !"pending".equals( ( (Map<?, ?>) record ).get( "status" ) ) )
        {
            return Optional.empty();
        }
        return Optional.of( (String) deviceCode );
    }

    @Override
    public boolean resolve( final IdProviderKey idProvider, final String deviceCode, final boolean approved,
                            @Nullable final PrincipalKey subject )
    {
        final SharedMap<String, Object> map = getMap( idProvider );
        final AtomicReference<Boolean> updated = new AtomicReference<>( Boolean.FALSE );

        map.modify( deviceCode, value -> {
            final HashMap<String, Object> record = asRecord( value );
            if ( record == null || !"pending".equals( record.get( "status" ) ) )
            {
                return value;
            }
            updated.set( Boolean.TRUE );
            if ( approved )
            {
                record.put( "status", "approved" );
                record.put( "subject", subject.toString() );
                record.put( "idProvider", idProvider.toString() );
            }
            else
            {
                record.put( "status", "denied" );
            }
            return record;
        }, remainingTtl( map.get( deviceCode ) ) );

        return updated.get();
    }

    @Override
    public DeviceAuthorizationPoll poll( final IdProviderKey idProvider, final String deviceCode )
    {
        final SharedMap<String, Object> map = getMap( idProvider );
        final AtomicReference<DeviceAuthorizationPoll> result =
            new AtomicReference<>( DeviceAuthorizationPoll.of( DeviceAuthorizationState.EXPIRED ) );

        map.modify( deviceCode, value -> {
            final HashMap<String, Object> record = asRecord( value );
            if ( record == null )
            {
                result.set( DeviceAuthorizationPoll.of( DeviceAuthorizationState.EXPIRED ) );
                return null;
            }

            final long now = System.currentTimeMillis();
            final long pollIntervalMillis = ( (Number) record.get( "pollIntervalSeconds" ) ).longValue() * 1000;
            final long lastPolledAt = ( (Number) record.get( "lastPolledAt" ) ).longValue();
            if ( lastPolledAt != 0 && ( now - lastPolledAt ) < pollIntervalMillis )
            {
                result.set( DeviceAuthorizationPoll.of( DeviceAuthorizationState.SLOW_DOWN ) );
                return record;
            }
            record.put( "lastPolledAt", now );

            final String status = (String) record.get( "status" );
            if ( "denied".equals( status ) )
            {
                result.set( DeviceAuthorizationPoll.of( DeviceAuthorizationState.DENIED ) );
                return null;
            }
            if ( "approved".equals( status ) )
            {
                result.set( DeviceAuthorizationPoll.create()
                                .state( DeviceAuthorizationState.APPROVED )
                                .subject( PrincipalKey.from( (String) record.get( "subject" ) ) )
                                .idProvider( IdProviderKey.from( (String) record.get( "idProvider" ) ) )
                                .audience( (String) record.get( "audience" ) )
                                .scope( (String) record.get( "scope" ) )
                                .clientId( (String) record.get( "clientId" ) )
                                .build() );
                return null; // single use
            }

            result.set( DeviceAuthorizationPoll.of( DeviceAuthorizationState.PENDING ) );
            return record;
        }, remainingTtl( map.get( deviceCode ) ) );

        return result.get();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static HashMap<String, Object> asRecord( @Nullable final Object value )
    {
        if ( value instanceof HashMap )
        {
            return (HashMap<String, Object>) value;
        }
        if ( value instanceof Map )
        {
            return new HashMap<>( (Map<String, Object>) value );
        }
        return null;
    }

    private static int remainingTtl( @Nullable final Object value )
    {
        final HashMap<String, Object> record = asRecord( value );
        if ( record == null )
        {
            return 1;
        }
        final long createdAt = ( (Number) record.get( "createdAt" ) ).longValue();
        final long ttlSeconds = ( (Number) record.get( "ttlSeconds" ) ).longValue();
        final long remaining = ttlSeconds - ( System.currentTimeMillis() - createdAt ) / 1000;
        return (int) Math.max( 1, remaining );
    }

    private SharedMap<String, Object> getMap( final IdProviderKey idProvider )
    {
        return sharedMapService.getSharedMap( MAP_PREFIX + idProvider.toString() );
    }

    private static String generateDeviceCode()
    {
        final byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes( bytes );
        return B64URL_NOPAD.encodeToString( bytes );
    }

    private static String generateUserCode()
    {
        final StringBuilder sb = new StringBuilder( 9 );
        for ( int i = 0; i < 8; i++ )
        {
            if ( i == 4 )
            {
                sb.append( '-' );
            }
            sb.append( USER_CODE_ALPHABET[SECURE_RANDOM.nextInt( USER_CODE_ALPHABET.length )] );
        }
        return sb.toString();
    }

    private static String nullToEmpty( final String value )
    {
        return value == null ? "" : value;
    }
}
