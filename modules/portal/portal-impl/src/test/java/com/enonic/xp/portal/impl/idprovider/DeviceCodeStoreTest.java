package com.enonic.xp.portal.impl.idprovider;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceCodeStoreTest
{
    private static final IdProviderKey IDP = IdProviderKey.system();

    private static final PrincipalKey ALICE = PrincipalKey.ofUser( IDP, "alice" );

    private Instant now = Instant.parse( "2026-10-14T12:00:00Z" );

    private final DeviceCodeStore store = new DeviceCodeStore( new InMemorySharedMapService(), new Clock()
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

        assertThat( store.poll( IDP, pending.deviceCode() ) ).isInstanceOf( DeviceCodeStore.Outcome.Waiting.class );
        now = now.plusSeconds( 2 );
        assertThat( store.poll( IDP, pending.deviceCode() ) ).isInstanceOf( DeviceCodeStore.Outcome.SlowDown.class );
        now = now.plusSeconds( DeviceCodeStore.POLL_INTERVAL_SECONDS );
        assertThat( store.poll( IDP, pending.deviceCode() ) ).isInstanceOf( DeviceCodeStore.Outcome.Waiting.class );

        assertThat( store.resolve( IDP, typed, true, ALICE ) ).isTrue();
        assertThat( store.resolve( IDP, typed, false, ALICE ) ).as( "already resolved" ).isFalse();
        assertThat( store.findByUserCode( IDP, typed ) ).map( DeviceCodeStore.Pending::status ).contains( DeviceCodeStore.Status.APPROVED );

        final DeviceCodeStore.Pending approved = switch ( store.poll( IDP, pending.deviceCode() ) )
        {
            case DeviceCodeStore.Outcome.Approved( var request ) -> request;
            case DeviceCodeStore.Outcome other -> throw new AssertionError( "Expected approved, got " + other );
        };
        assertThat( approved.subject() ).isEqualTo( ALICE );
        assertThat( approved.clientId() ).isEqualTo( "cli" );
        assertThat( approved.scope() ).containsExactly( "content:read" );
        assertThat( approved.audience() ).containsExactly( "api" );

        assertThat( store.poll( IDP, pending.deviceCode() ) ).isInstanceOf( DeviceCodeStore.Outcome.Expired.class );
        assertThat( store.findByUserCode( IDP, typed ) ).isEmpty();
    }

    @Test
    void deny_is_consumed_too()
    {
        final DeviceCodeStore.Pending pending = store.create( IDP, "cli", Set.of(), Set.of() );
        assertThat( store.resolve( IDP, pending.userCode(), false, ALICE ) ).isTrue();
        assertThat( store.poll( IDP, pending.deviceCode() ) ).isInstanceOf( DeviceCodeStore.Outcome.Denied.class );
        assertThat( store.poll( IDP, pending.deviceCode() ) ).isInstanceOf( DeviceCodeStore.Outcome.Expired.class );
    }

    @Test
    void unknown_codes()
    {
        assertThat( store.poll( IDP, "nope" ) ).isInstanceOf( DeviceCodeStore.Outcome.Expired.class );
        assertThat( store.resolve( IDP, "NOPE", true, ALICE ) ).isFalse();
        assertThat( store.findByUserCode( IDP, null ) ).isEmpty();
        final Optional<DeviceCodeStore.Pending> none = store.findByUserCode( IDP, "" );
        assertThat( none ).isEmpty();
    }

    @Test
    void device_codes_are_scoped_by_id_provider()
    {
        final DeviceCodeStore.Pending pending = store.create( IDP, "cli", Set.of(), Set.of() );
        assertThat( store.poll( IdProviderKey.from( "other" ), pending.deviceCode() ) ).isInstanceOf( DeviceCodeStore.Outcome.Expired.class );
        assertThat( store.poll( IDP, pending.deviceCode() ) ).isInstanceOf( DeviceCodeStore.Outcome.Waiting.class );
    }
}
