package com.enonic.xp.core.impl.security.token;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.token.DeviceAuthorization;
import com.enonic.xp.security.token.DeviceAuthorizationParams;
import com.enonic.xp.security.token.DeviceAuthorizationPoll;
import com.enonic.xp.security.token.DeviceAuthorizationState;
import com.enonic.xp.shared.SharedMap;
import com.enonic.xp.shared.SharedMapService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeviceAuthServiceImplTest
{
    private static final IdProviderKey IDP = IdProviderKey.from( "myidp" );

    private DeviceAuthServiceImpl service;

    @BeforeEach
    void setUp()
    {
        this.service = new DeviceAuthServiceImpl( new FakeSharedMapService() );
    }

    private DeviceAuthorization start()
    {
        return service.start( DeviceAuthorizationParams.create()
                                  .idProvider( IDP )
                                  .clientId( "cli" )
                                  .scope( "openid" )
                                  .audience( "https://api.example.com" )
                                  .ttl( Duration.ofMinutes( 10 ) )
                                  .pollInterval( Duration.ZERO )
                                  .build() );
    }

    @Test
    void approve_flow()
    {
        final DeviceAuthorization auth = start();

        assertEquals( DeviceAuthorizationState.PENDING, service.poll( IDP, auth.deviceCode() ).getState() );

        final Optional<String> byUserCode = service.findByUserCode( IDP, auth.userCode() );
        assertTrue( byUserCode.isPresent() );
        assertEquals( auth.deviceCode(), byUserCode.get() );

        assertTrue( service.resolve( IDP, auth.deviceCode(), true, PrincipalKey.from( "user:myidp:john" ) ) );

        final DeviceAuthorizationPoll approved = service.poll( IDP, auth.deviceCode() );
        assertEquals( DeviceAuthorizationState.APPROVED, approved.getState() );
        assertEquals( PrincipalKey.from( "user:myidp:john" ), approved.getSubject() );
        assertEquals( "https://api.example.com", approved.getAudience() );

        // Single use: the code is consumed once approved.
        assertEquals( DeviceAuthorizationState.EXPIRED, service.poll( IDP, auth.deviceCode() ).getState() );
    }

    @Test
    void deny_flow()
    {
        final DeviceAuthorization auth = start();
        assertTrue( service.resolve( IDP, auth.deviceCode(), false, null ) );
        assertEquals( DeviceAuthorizationState.DENIED, service.poll( IDP, auth.deviceCode() ).getState() );
    }

    @Test
    void unknown_device_code_is_expired()
    {
        assertEquals( DeviceAuthorizationState.EXPIRED, service.poll( IDP, "nope" ).getState() );
    }

    @Test
    void slow_down_when_polling_faster_than_interval()
    {
        final DeviceAuthorization auth = service.start( DeviceAuthorizationParams.create()
                                                            .idProvider( IDP )
                                                            .clientId( "cli" )
                                                            .ttl( Duration.ofMinutes( 10 ) )
                                                            .pollInterval( Duration.ofSeconds( 5 ) )
                                                            .build() );
        assertEquals( DeviceAuthorizationState.PENDING, service.poll( IDP, auth.deviceCode() ).getState() );
        assertEquals( DeviceAuthorizationState.SLOW_DOWN, service.poll( IDP, auth.deviceCode() ).getState() );
    }

    @Test
    void resolve_unknown_returns_false()
    {
        assertFalse( service.resolve( IDP, "nope", true, PrincipalKey.from( "user:myidp:john" ) ) );
    }

    private static final class FakeSharedMapService
        implements SharedMapService
    {
        private final ConcurrentMap<String, SharedMap<?, ?>> maps = new ConcurrentHashMap<>();

        @Override
        @SuppressWarnings("unchecked")
        public <K, V> SharedMap<K, V> getSharedMap( final String name )
        {
            return (SharedMap<K, V>) maps.computeIfAbsent( name, n -> new FakeSharedMap<>() );
        }
    }

    private static final class FakeSharedMap<K, V>
        implements SharedMap<K, V>
    {
        private final ConcurrentMap<K, V> map = new ConcurrentHashMap<>();

        @Override
        public V get( final K key )
        {
            return map.get( key );
        }

        @Override
        public void delete( final K key )
        {
            map.remove( key );
        }

        @Override
        public void set( final K key, final V value )
        {
            set( key, value, -1 );
        }

        @Override
        public void set( final K key, final V value, final int ttlSeconds )
        {
            if ( value == null )
            {
                map.remove( key );
            }
            else
            {
                map.put( key, value );
            }
        }

        @Override
        public V modify( final K key, final Function<V, V> modifier )
        {
            return modify( key, modifier, -1 );
        }

        @Override
        public synchronized V modify( final K key, final Function<V, V> modifier, final int ttlSeconds )
        {
            final V newValue = modifier.apply( map.get( key ) );
            if ( newValue == null )
            {
                map.remove( key );
            }
            else
            {
                map.put( key, newValue );
            }
            return newValue;
        }
    }
}
