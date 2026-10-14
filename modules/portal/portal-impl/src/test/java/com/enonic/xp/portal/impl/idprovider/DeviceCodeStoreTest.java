package com.enonic.xp.portal.impl.idprovider;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleRestriction;
import com.enonic.xp.shared.SharedMap;
import com.enonic.xp.shared.SharedMapService;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceCodeStoreTest
{
    private static final IdProviderKey IDP = IdProviderKey.system();

    private static final PrincipalKey ALICE = PrincipalKey.ofUser( IDP, "alice" );

    private Instant now = Instant.parse( "2026-10-14T12:00:00Z" );

    private final List<Integer> ttlsWritten = new ArrayList<>();

    private final DeviceCodeStore store = new DeviceCodeStore( recording( new InMemorySharedMapService() ), new Clock()
    {
        @Override
        public ZoneOffset getZone()
        {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone( final java.time.ZoneId zone )
        {
            return this;
        }

        @Override
        public Instant instant()
        {
            return now;
        }
    } );

    @Test
    void create_find_approve_poll()
    {
        final DeviceCodeStore.Pending pending = store.create( IDP, "cli", Set.of( "content:read" ), Set.of( "api" ) );
        assertThat( pending.deviceCode() ).hasSize( 43 );
        assertThat( pending.userCode() ).hasSize( 8 ).matches( "[BCDFGHJKLMNPQRSTVWXZ]{8}" );
        assertThat( pending.status() ).isEqualTo( DeviceCodeStore.Status.PENDING );
        assertThat( DeviceCodeStore.formatUserCode( pending.userCode() ) ).matches( "[A-Z]{4}-[A-Z]{4}" );

        final String typed = " " + DeviceCodeStore.formatUserCode( pending.userCode() ).toLowerCase() + " ";
        assertThat( store.findByUserCode( IDP, typed ) ).map( DeviceCodeStore.Pending::deviceCode ).contains( pending.deviceCode() );
        assertThat( store.findByUserCode( IDP, "ZZZZ-ZZZZ" ) ).isEmpty();
        assertThat( store.findByUserCode( IdProviderKey.from( "other" ), pending.userCode() ) ).as( "scoped by id provider" ).isEmpty();

        assertThat( store.poll( IDP, pending.deviceCode(), "cli" ) ).isInstanceOf( DeviceCodeStore.Outcome.Waiting.class );
        now = now.plusSeconds( 2 );
        assertThat( store.poll( IDP, pending.deviceCode(), "cli" ) ).isInstanceOf( DeviceCodeStore.Outcome.SlowDown.class );
        now = now.plusSeconds( DeviceCodeStore.POLL_INTERVAL_SECONDS );
        assertThat( store.poll( IDP, pending.deviceCode(), "cli" ) ).isInstanceOf( DeviceCodeStore.Outcome.Waiting.class );

        assertThat( store.resolve( IDP, typed, true, ALICE, Set.of( "content:read" ), RoleRestriction.to( PrincipalKeys.from( "role:cms.cm.app" ) ) ) ).isTrue();
        assertThat( store.resolve( IDP, typed, false, ALICE, Set.of(), RoleRestriction.none() ) ).as( "already resolved" ).isFalse();
        assertThat( store.findByUserCode( IDP, typed ) ).map( DeviceCodeStore.Pending::status ).contains( DeviceCodeStore.Status.APPROVED );

        final DeviceCodeStore.Pending approved = switch ( store.poll( IDP, pending.deviceCode(), "cli" ) )
        {
            case DeviceCodeStore.Outcome.Approved( var request ) -> request;
            case DeviceCodeStore.Outcome other -> throw new AssertionError( "Expected approved, got " + other );
        };
        assertThat( approved.subject() ).isEqualTo( ALICE );
        assertThat( approved.clientId() ).isEqualTo( "cli" );
        assertThat( approved.scope() ).containsExactly( "content:read" );
        assertThat( approved.approvedScope() ).containsExactly( "content:read" );
        assertThat( approved.approvedRestriction() ).isEqualTo( RoleRestriction.to( PrincipalKeys.from( "role:cms.cm.app" ) ) );
        assertThat( approved.audience() ).containsExactly( "api" );

        assertThat( store.poll( IDP, pending.deviceCode(), "cli" ) ).isInstanceOf( DeviceCodeStore.Outcome.Expired.class );
        assertThat( store.findByUserCode( IDP, typed ) ).isEmpty();
    }

    @Test
    void polling_and_approval_never_move_the_deadline()
    {
        final DeviceCodeStore.Pending pending = store.create( IDP, "cli", Set.of(), Set.of( "api" ) );
        final Instant deadline = now.plusSeconds( DeviceCodeStore.CODE_TTL_SECONDS );
        ttlsWritten.clear();

        for ( int i = 0; i < 100; i++ )
        {
            now = now.plusSeconds( DeviceCodeStore.POLL_INTERVAL_SECONDS );
            assertThat( store.poll( IDP, pending.deviceCode(), "cli" ) ).as( "poll %d", i ).isInstanceOf( DeviceCodeStore.Outcome.Waiting.class );
            assertThat( ttlsWritten.getLast() ).as( "write %d carries the time left, not a fresh lifetime", i )
                .isLessThanOrEqualTo( (int) ( deadline.getEpochSecond() - now.getEpochSecond() ) )
                .isGreaterThan( 0 );
        }
        assertThat( now ).isEqualTo( deadline.minusSeconds( 100 ) );

        now = deadline.minusSeconds( 1 );
        assertThat( store.findByUserCode( IDP, pending.userCode() ) ).isPresent();
        assertThat( store.resolve( IDP, pending.userCode(), true, ALICE, Set.of(), RoleRestriction.none() ) ).isTrue();
        assertThat( ttlsWritten.getLast() ).isEqualTo( 1 );

        now = deadline;
        assertThat( store.poll( IDP, pending.deviceCode(), "cli" ) ).as( "approved, but past the advertised lifetime" )
            .isInstanceOf( DeviceCodeStore.Outcome.Expired.class );
        assertThat( store.findByUserCode( IDP, pending.userCode() ) ).isEmpty();
        assertThat( store.resolve( IDP, pending.userCode(), true, ALICE, Set.of(), RoleRestriction.none() ) ).isFalse();
    }

    @Test
    void a_pending_request_expires_at_the_deadline_however_often_polled()
    {
        final DeviceCodeStore.Pending pending = store.create( IDP, "cli", Set.of(), Set.of() );
        now = now.plusSeconds( DeviceCodeStore.CODE_TTL_SECONDS - 1 );
        assertThat( store.poll( IDP, pending.deviceCode(), "cli" ) ).isInstanceOf( DeviceCodeStore.Outcome.Waiting.class );
        now = now.plusSeconds( 1 );
        assertThat( store.poll( IDP, pending.deviceCode(), "cli" ) ).isInstanceOf( DeviceCodeStore.Outcome.Expired.class );
        assertThat( store.resolve( IDP, pending.userCode(), true, ALICE, Set.of(), RoleRestriction.none() ) ).as( "nothing left to approve" ).isFalse();
    }

    @Test
    void a_wrong_clients_poll_does_not_consume_the_approval()
    {
        final DeviceCodeStore.Pending pending = store.create( IDP, "cli", Set.of(), Set.of() );
        assertThat( store.resolve( IDP, pending.userCode(), true, ALICE, Set.of(), RoleRestriction.none() ) ).isTrue();
        now = now.plusSeconds( DeviceCodeStore.POLL_INTERVAL_SECONDS );

        assertThat( store.poll( IDP, pending.deviceCode(), "other" ) ).isInstanceOf( DeviceCodeStore.Outcome.ClientMismatch.class );
        assertThat( store.poll( IDP, pending.deviceCode(), "other" ) ).as( "still there" ).isInstanceOf( DeviceCodeStore.Outcome.ClientMismatch.class );
        assertThat( store.findByUserCode( IDP, pending.userCode() ) ).isPresent();

        assertThat( store.poll( IDP, pending.deviceCode(), "cli" ) ).isInstanceOf( DeviceCodeStore.Outcome.Approved.class );
        assertThat( store.poll( IDP, pending.deviceCode(), "cli" ) ).as( "consumed by the right client" ).isInstanceOf( DeviceCodeStore.Outcome.Expired.class );
    }

    @Test
    void deny_is_consumed_too()
    {
        final DeviceCodeStore.Pending pending = store.create( IDP, "cli", Set.of(), Set.of() );
        assertThat( store.resolve( IDP, pending.userCode(), false, ALICE, Set.of(), RoleRestriction.none() ) ).isTrue();
        assertThat( store.poll( IDP, pending.deviceCode(), "cli" ) ).isInstanceOf( DeviceCodeStore.Outcome.Denied.class );
        assertThat( store.poll( IDP, pending.deviceCode(), "cli" ) ).isInstanceOf( DeviceCodeStore.Outcome.Expired.class );
    }

    @Test
    void unknown_codes()
    {
        assertThat( store.poll( IDP, "nope", "cli" ) ).isInstanceOf( DeviceCodeStore.Outcome.Expired.class );
        assertThat( store.resolve( IDP, "ZZZZZZZZ", true, ALICE, Set.of(), RoleRestriction.none() ) ).isFalse();
        assertThat( store.findByUserCode( IDP, null ) ).isEmpty();
        final Optional<DeviceCodeStore.Pending> none = store.findByUserCode( IDP, "" );
        assertThat( none ).isEmpty();
    }

    @Test
    void device_codes_are_scoped_by_id_provider()
    {
        final DeviceCodeStore.Pending pending = store.create( IDP, "cli", Set.of(), Set.of() );
        assertThat( store.poll( IdProviderKey.from( "other" ), pending.deviceCode(), "cli" ) ).isInstanceOf( DeviceCodeStore.Outcome.Expired.class );
        assertThat( store.poll( IDP, pending.deviceCode(), "cli" ) ).isInstanceOf( DeviceCodeStore.Outcome.Waiting.class );
    }


    private SharedMapService recording( final SharedMapService delegate )
    {
        return new SharedMapService()
        {
            @Override
            public <K, V> SharedMap<K, V> getSharedMap( final String name )
            {
                final SharedMap<K, V> map = delegate.getSharedMap( name );
                return new SharedMap<>()
                {
                    @Override
                    public V get( final K key )
                    {
                        return map.get( key );
                    }

                    @Override
                    public void delete( final K key )
                    {
                        map.delete( key );
                    }

                    @Override
                    public void set( final K key, final V value )
                    {
                        map.set( key, value );
                    }

                    @Override
                    public void set( final K key, final V value, final int ttlSeconds )
                    {
                        ttlsWritten.add( ttlSeconds );
                        map.set( key, value, ttlSeconds );
                    }

                    @Override
                    public V modify( final K key, final Function<V, V> modifier )
                    {
                        return map.modify( key, modifier );
                    }

                    @Override
                    public V modify( final K key, final Function<V, V> modifier, final int ttlSeconds )
                    {
                        ttlsWritten.add( ttlSeconds );
                        return map.modify( key, modifier, ttlSeconds );
                    }
                };
            }
        };
    }
}
