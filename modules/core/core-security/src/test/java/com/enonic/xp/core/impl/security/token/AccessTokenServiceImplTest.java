package com.enonic.xp.core.impl.security.token;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.token.AccessToken;
import com.enonic.xp.security.token.AccessTokenClaims;
import com.enonic.xp.security.token.AccessTokenService;
import com.enonic.xp.security.token.AccessTokenVerification;
import com.enonic.xp.security.token.IssueAccessTokenParams;
import com.enonic.xp.security.token.VerifyAccessTokenParams;
import com.enonic.xp.server.ServerInfo;
import com.enonic.xp.shared.SharedMap;
import com.enonic.xp.shared.SharedMapService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccessTokenServiceImplTest
{
    private static final PrincipalKey ALICE = PrincipalKey.ofUser( IdProviderKey.system(), "alice" );

    private static final PrincipalKey SERVICE = PrincipalKey.ofUser( IdProviderKey.system(), "automation" );

    private static final String SECRET_A = Base64.getEncoder().encodeToString( new byte[64] );

    private static final String SECRET_B = Base64.getEncoder().encodeToString( secret( 7 ) );

    private static byte[] secret( final int fill )
    {
        final byte[] bytes = new byte[64];
        java.util.Arrays.fill( bytes, (byte) fill );
        return bytes;
    }

    private final SharedMapService sharedMapService = new InMemorySharedMapService();

    private final Instant now = Instant.parse( "2026-10-14T12:00:00Z" );

    private AccessTokenServiceImpl service( final Map<String, ?> config, final Instant time )
    {
        final AccessTokenServiceImpl service = new AccessTokenServiceImpl( sharedMapService, Clock.fixed( time, ZoneOffset.UTC ) );
        service.activate( config );
        return service;
    }

    @Test
    void issue_and_verify_round_trip_with_all_claims()
    {
        final AccessTokenServiceImpl service = service( Map.of(), now );

        final AccessToken token = service.issue( IssueAccessTokenParams.create()
                                                    .subject( ALICE )
                                                    .audience( "api", "admin" )
                                                    .scope( "content:read", "content:write" )
                                                    .actor( SERVICE )
                                                    .clientId( "my-cli" )
                                                    .grantId( "g-1" )
                                                    .ttl( Duration.ofMinutes( 5 ) )
                                                    .build() );

        assertThat( token.value() ).matches( "[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+" );
        assertThat( token.toString() ).doesNotContain( token.value() );

        final AccessTokenClaims issued = token.claims();
        assertThat( issued.getIssuer() ).isEqualTo( "xp" );
        assertThat( issued.getIssuedAt() ).isEqualTo( now );
        assertThat( issued.getExpiresAt() ).isEqualTo( now.plus( Duration.ofMinutes( 5 ) ) );
        assertThat( issued.getTokenId() ).isNotBlank();

        final AccessTokenClaims verified = verified( verify( service, token.value() ) );
        assertThat( verified ).isEqualTo( issued );
        assertThat( verified.getSubject() ).isEqualTo( ALICE );
        assertThat( verified.getAudience() ).containsExactlyInAnyOrder( "api", "admin" );
        assertThat( verified.getScope() ).containsExactlyInAnyOrder( "content:read", "content:write" );
        assertThat( verified.getActor() ).isEqualTo( SERVICE );
        assertThat( verified.getClientId() ).isEqualTo( "my-cli" );
        assertThat( verified.getGrantId() ).isEqualTo( "g-1" );
        assertThat( verify( service, token.value() ) ).isInstanceOf( AccessTokenVerification.Verified.class );
    }

    @Test
    void minimal_token_uses_defaults()
    {
        final AccessTokenServiceImpl service = service( Map.of(), now );
        final AccessToken token = service.issue( minimal().build() );
        final AccessTokenClaims claims = verified( verify( service, token.value() ) );

        assertThat( claims.getExpiresAt() ).isEqualTo( now.plus( AccessTokenServiceImpl.DEFAULT_TTL ) );
        assertThat( claims.getAudience() ).containsExactly( "cli" );
        assertThat( claims.getScope() ).isEmpty();
        assertThat( claims.getActor() ).isNull();
        assertThat( claims.getClientId() ).isEqualTo( "cli" );
        assertThat( claims.getGrantId() ).isNull();
    }

    @Test
    void issuer_defaults_to_the_installation_name()
    {
        final String before = System.getProperty( "xp.name" );
        try
        {
            System.setProperty( "xp.name", "prod-eu" );
            final AccessTokenServiceImpl named = service( Map.of(), now );
            final AccessToken token = named.issue( minimal().build() );
            assertThat( token.claims().getIssuer() ).isEqualTo( "prod-eu" );
            assertThat( named.getIssuer() ).isEqualTo( "prod-eu" );
            assertThat( verify( named, token.value() ) ).isInstanceOf( AccessTokenVerification.Verified.class );

            System.clearProperty( "xp.name" );
            assertThat( service( Map.of(), now ).issue( minimal().build() ).claims().getIssuer() )
                .as( "the installation's default name" ).isEqualTo( ServerInfo.DEFAULT_NAME );

            final AccessTokenServiceImpl explicit = service( Map.of( "token.issuer", "explicit" ), now );
            assertThat( explicit.issue( minimal().build() ).claims().getIssuer() ).isEqualTo( "explicit" );
            assertThat( explicit.getIssuer() ).isEqualTo( "explicit" );
        }
        finally
        {
            if ( before == null )
            {
                System.clearProperty( "xp.name" );
            }
            else
            {
                System.setProperty( "xp.name", before );
            }
        }
    }

    @Test
    void configured_issuer_and_ttl()
    {
        final AccessTokenServiceImpl service = service( Map.of( "token.issuer", "https://xp.example.com", "token.ttl", "PT15M" ), now );
        final AccessToken token = service.issue( minimal().build() );
        assertThat( token.claims().getIssuer() ).isEqualTo( "https://xp.example.com" );
        assertThat( token.claims().getExpiresAt() ).isEqualTo( now.plus( Duration.ofMinutes( 15 ) ) );
        assertThat( verify( service, token.value() ) ).isInstanceOf( AccessTokenVerification.Verified.class );

        final AccessTokenServiceImpl other = service( Map.of(), now );
        assertThat( verify( other, token.value() ) ).isInstanceOf( AccessTokenVerification.InvalidIssuer.class );
    }

    @Test
    void activate_with_osgi_service_properties()
    {
        final Map<String, Object> properties =
            Map.of( "service.pid", "com.enonic.xp.security", "service.bundleid", 42L, "felix.fileinstall.filename",
                    "file:/opt/xp/config/com.enonic.xp.security.cfg", "token.keys.a", SECRET_A, "token.ttl", " PT15M " );
        final AccessTokenServiceImpl service = service( properties, now );
        final AccessToken token = service.issue( minimal().build() );
        assertThat( token.claims().getExpiresAt() ).isEqualTo( now.plus( Duration.ofMinutes( 15 ) ) );
        assertThat( verify( service, token.value() ) ).isInstanceOf( AccessTokenVerification.Verified.class );
    }

    @Test
    void lifetimes_are_whole_seconds_at_least_one()
    {
        for ( String ttl : new String[]{"PT0S", "PT-1H", "PT0.5S", "PT1.5S"} )
        {
            assertThatThrownBy( () -> service( Map.of( "token.ttl", ttl ), now ) ).as( ttl ).isInstanceOf( IllegalArgumentException.class );
        }
        for ( Duration ttl : new Duration[]{Duration.ZERO, Duration.ofMillis( 500 ), Duration.ofMillis( 1500 )} )
        {
            assertThatThrownBy( () -> minimal().ttl( ttl ).build() ).as( ttl.toString() )
                .isInstanceOf( IllegalArgumentException.class );
        }
        assertThat( service( Map.of( "token.ttl", "PT1S" ), now ).issue( minimal().build() )
                        .claims()
                        .getExpiresAt() ).isEqualTo( now.plusSeconds( 1 ) );
    }

    @Test
    void expired_token_is_rejected_after_skew()
    {
        final AccessTokenServiceImpl issuer = service( Map.of(), now );
        final AccessToken token =
            issuer.issue( minimal().ttl( Duration.ofMinutes( 1 ) ).build() );

        final Instant justExpired = now.plus( Duration.ofMinutes( 1 ) ).plusSeconds( 30 );
        assertThat( verify( service( Map.of(), justExpired ), token.value() ) ).as( "within skew" ).isInstanceOf( AccessTokenVerification.Verified.class );

        final Instant wellExpired = now.plus( Duration.ofMinutes( 1 ) ).plus( AccessTokenServiceImpl.CLOCK_SKEW ).plusSeconds( 1 );
        assertThat( verify( service( Map.of(), wellExpired ), token.value() ) ).as( "beyond skew" ).isInstanceOf( AccessTokenVerification.Invalid.class );
    }

    @Test
    void tampered_and_foreign_tokens_are_rejected()
    {
        final AccessTokenServiceImpl service = service( Map.of(), now );
        final AccessToken token = service.issue( minimal().build() );
        final String[] parts = token.value().split( "\\." );

        final String forgedPayload = Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString( ( "{\"iss\":\"xp\",\"sub\":\"user:system:su\",\"iat\":" + now.getEpochSecond() + ",\"exp\":" +
                now.plusSeconds( 3600 ).getEpochSecond() + ",\"jti\":\"x\"}" ).getBytes() );
        assertThat( verify( service, parts[0] + "." + forgedPayload + "." + parts[2] ) ).isInstanceOf( AccessTokenVerification.Invalid.class );
        assertThat( verify( service, parts[0] + "." + parts[1] + ".AAAA" ) ).isInstanceOf( AccessTokenVerification.Invalid.class );
        assertThat( verify( service, "not.a.token" ) ).isInstanceOf( AccessTokenVerification.InvalidIssuer.class );
        assertThat( verify( service, "" ) ).isInstanceOf( AccessTokenVerification.InvalidIssuer.class );
        assertThat( verify( service, "garbage" ) ).isInstanceOf( AccessTokenVerification.InvalidIssuer.class );
    }

    @Test
    void unknown_key_is_rejected()
    {
        final AccessTokenServiceImpl a = service( Map.of( "token.keys.a", SECRET_A ), now );
        final AccessTokenServiceImpl b = service( Map.of( "token.keys.b", SECRET_B ), now );
        final AccessToken token = a.issue( minimal().build() );
        assertThat( verify( b, token.value() ) ).as( "but the key is unknown" ).isInstanceOf( AccessTokenVerification.Invalid.class );
    }

    @Test
    void rotation_keeps_old_tokens_valid_until_the_key_is_dropped()
    {
        final AccessTokenServiceImpl before = service( Map.of( "token.keys.a", SECRET_A ), now );
        final AccessToken old = before.issue( minimal().build() );

        final AccessTokenServiceImpl rotated =
            service( Map.of( "token.keys.a", SECRET_A, "token.keys.b", SECRET_B, "token.signingKey", "b" ), now );
        assertThat( verify( rotated, old.value() ) ).as( "old key still verifies" ).isInstanceOf( AccessTokenVerification.Verified.class );
        final AccessToken fresh = rotated.issue( minimal().build() );
        assertThat( JwsHs512.parse( fresh.value() ).orElseThrow().kid() ).contains( "b" );

        final AccessTokenServiceImpl dropped = service( Map.of( "token.keys.b", SECRET_B ), now );
        assertThat( verify( dropped, old.value() ) ).as( "old key dropped" ).isInstanceOf( AccessTokenVerification.Invalid.class );
        assertThat( verify( dropped, fresh.value() ) ).isInstanceOf( AccessTokenVerification.Verified.class );
    }

    @Test
    void generated_key_is_shared_between_nodes()
    {
        final AccessTokenServiceImpl nodeA = service( Map.of(), now );
        final AccessTokenServiceImpl nodeB = service( Map.of(), now );
        final AccessToken token = nodeA.issue( minimal().build() );
        assertThat( verify( nodeB, token.value() ) ).isInstanceOf( AccessTokenVerification.Verified.class );
    }

    @Test
    void subject_must_be_a_user()
    {
        assertThatThrownBy( () -> IssueAccessTokenParams.create().subject( RoleKeys.ADMIN ).audience( "cli" ).clientId( "cli" ).build() ).isInstanceOf(
            IllegalArgumentException.class );
        assertThatThrownBy( () -> minimal().ttl( Duration.ofSeconds( -1 ) ).build() ).isInstanceOf(
            IllegalArgumentException.class );
    }

    @Test
    void malformed_claims_in_a_validly_signed_token_are_rejected()
    {
        final AccessTokenServiceImpl service = service( Map.of( "token.keys.a", SECRET_A ), now );
        final SigningKey key = new SigningKey( "a", new byte[64] );
        final String token = JwsHs512.sign( key, JwsHs512.newPayload().put( "iss", "xp" ).put( "sub", "role:system.admin" ) );
        assertThat( verify( service, token ) ).isInstanceOf( AccessTokenVerification.Invalid.class );
    }

    /**
     * The least a token can be issued with: a subject, and the audience and client every token names.
     */
    private static IssueAccessTokenParams.Builder minimal()
    {
        return IssueAccessTokenParams.create().subject( ALICE ).audience( "cli" ).clientId( "cli" );
    }

    private static AccessTokenClaims verified( final AccessTokenVerification verification )
    {
        assertThat( verification ).isInstanceOf( AccessTokenVerification.Verified.class );
        return ( (AccessTokenVerification.Verified) verification ).claims();
    }

    private static AccessTokenVerification verify( final AccessTokenService service, final String token )
    {
        return service.verify( VerifyAccessTokenParams.create().token( token ).build() );
    }

    @Test
    void rejected_configuration_update_keeps_the_running_settings()
    {
        final AccessTokenServiceImpl service =
            service( Map.of( "token.issuer", "one", "token.ttl", "PT2H", "token.keys.a", SECRET_A ), now );
        final AccessToken before = service.issue( minimal().build() );

        assertThatThrownBy( () -> service.activate( Map.of( "token.issuer", "two", "token.ttl", "PT-1H", "token.keys.a", SECRET_A ) ) )
            .isInstanceOf( IllegalArgumentException.class );
        assertThatThrownBy( () -> service.activate( Map.of( "token.issuer", "two", "token.keys.a", "not base64 !!" ) ) )
            .isInstanceOf( IllegalArgumentException.class );

        assertThat( verify( service, before.value() ) ).as( "earlier tokens still verify" ).isInstanceOf( AccessTokenVerification.Verified.class );
        final AccessToken after = service.issue( minimal().build() );
        assertThat( after.claims().getIssuer() ).isEqualTo( "one" );
        assertThat( after.claims().getExpiresAt() ).isEqualTo( now.plus( Duration.ofHours( 2 ) ) );
        assertThat( verify( service( Map.of( "token.issuer", "one", "token.keys.a", SECRET_A ), now ), after.value() ) )
            .as( "still signed with key a" )
            .isInstanceOf( AccessTokenVerification.Verified.class );
    }

    @Test
    void a_claim_in_another_form_makes_the_token_invalid()
    {
        final AccessTokenServiceImpl service = service( Map.of( "token.keys.a", SECRET_A ), now );
        final SigningKey key = new SigningKey( "a", new byte[64] );

        assertThat( verify( service, signed( key, "" ) ) ).isInstanceOf( AccessTokenVerification.Verified.class );
        assertThat( verify( service, signed( key, ",\"aud\":{}" ) ) ).isInstanceOf( AccessTokenVerification.Invalid.class );
        assertThat( verify( service, signed( key, ",\"scope\":[\"a\"]" ) ) ).isInstanceOf( AccessTokenVerification.Invalid.class );
        assertThat( verify( service, signed( key, ",\"client_id\":1" ) ) ).isInstanceOf( AccessTokenVerification.Invalid.class );
        assertThat( verify( service, signed( key, ",\"act\":\"user:system:x\"" ) ) ).isInstanceOf( AccessTokenVerification.Invalid.class );
        assertThat( verify( service, signed( key, ",\"grant\":[]" ) ) ).isInstanceOf( AccessTokenVerification.Invalid.class );
    }

    @Test
    void every_token_names_its_audience_and_client()
    {
        assertThatThrownBy( () -> IssueAccessTokenParams.create().subject( ALICE ).clientId( "cli" ).build() ).as( "no audience" )
            .isInstanceOf( IllegalArgumentException.class )
            .hasMessageContaining( "audience" );
        assertThatThrownBy( () -> IssueAccessTokenParams.create().subject( ALICE ).audience( "cli" ).build() ).as( "no client" )
            .isInstanceOf( NullPointerException.class )
            .hasMessageContaining( "clientId" );
        assertThatThrownBy( () -> IssueAccessTokenParams.create().subject( ALICE ).audience( "cli" ).clientId( " " ).build() ).as( "blank client" )
            .isInstanceOf( IllegalArgumentException.class );

        final AccessTokenServiceImpl service = service( Map.of( "token.keys.a", SECRET_A ), now );
        final SigningKey key = new SigningKey( "a", new byte[64] );
        final String withoutAudience = signedPayload( key, claimsJson( "" ).replace( ",\"aud\":[\"cli\"]", "" ) );
        assertThat( verify( service, withoutAudience ) ).as( "a token naming no audience is invalid" )
            .isInstanceOf( AccessTokenVerification.Invalid.class );
        assertThat( verify( service, signedPayload( key, claimsJson( "" ).replace( ",\"aud\":[\"cli\"]", ",\"aud\":[]" ) ) ) )
            .as( "an empty audience is invalid" )
            .isInstanceOf( AccessTokenVerification.Invalid.class );
        assertThat( verify( service, signedPayload( key, claimsJson( "" ).replace( ",\"client_id\":\"cli\"", "" ) ) ) )
            .as( "a token naming no client is invalid" )
            .isInstanceOf( AccessTokenVerification.Invalid.class );
        assertThat( verified( verify( service, signedPayload( key, claimsJson( "" ).replace( "[\"cli\"]", "\"cli\"" ) ) ) ).getAudience() )
            .as( "a single-string audience is read" )
            .containsExactly( "cli" );
    }

    @Test
    void token_type_and_algorithm_are_checked_before_the_key()
    {
        final RecordingSharedMapService lookups = new RecordingSharedMapService( sharedMapService );
        final AccessTokenServiceImpl service = new AccessTokenServiceImpl( lookups, Clock.fixed( now, ZoneOffset.UTC ) );
        service.activate( Map.of() );
        final Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        final String payload = enc.encodeToString( claimsJson( "" ).getBytes() );
        for ( String header : new String[]{"{\"alg\":\"HS512\",\"kid\":\"unknown\"}", "{\"alg\":\"HS512\",\"typ\":\"JWT\",\"kid\":\"unknown\"}",
            "{\"alg\":\"HS512\",\"typ\":\"at+jwt\",\"kid\":\"unknown\",\"crit\":[\"b64\"]}",
            "{\"alg\":\"none\",\"typ\":\"at+jwt\",\"kid\":\"unknown\"}"} )
        {
            assertThat( verify( service, enc.encodeToString( header.getBytes() ) + "." + payload + ".AAAA" ) ).as( header )
                .isInstanceOf( AccessTokenVerification.Invalid.class );
        }
        assertThat( lookups.lookedUp ).as( "no key is looked up for a token whose header is rejected" ).doesNotContain( "unknown" );

        final String accepted = "{\"alg\":\"HS512\",\"typ\":\"at+jwt\",\"kid\":\"unknown\"}";
        assertThat( verify( service, enc.encodeToString( accepted.getBytes() ) + "." + payload + ".AAAA" ) ).isInstanceOf(
            AccessTokenVerification.Invalid.class );
        assertThat( lookups.lookedUp ).as( "an accepted header reaches the key lookup" ).contains( "unknown" );

        final AccessToken token = service.issue( minimal().build() );
        assertThat( verify( service, token.value() ) ).isInstanceOf( AccessTokenVerification.Verified.class );
    }

    @Test
    void scopes_are_scope_names_and_round_trip_unchanged()
    {
        for ( String bad : new String[]{"", " ", "content:read content:write", "a\tb", "a\nb", "a\"b", "a\\b", "æ"} )
        {
            assertThatThrownBy( () -> minimal().scope( bad ).build() ).as( bad )
                .isInstanceOf( IllegalArgumentException.class );
            assertThatThrownBy( () -> AccessTokenClaims.create().scope( List.of( bad ) ) ).as( bad ).isInstanceOf( IllegalArgumentException.class );
        }
        final AccessTokenServiceImpl service = service( Map.of(), now );
        final Set<String> scopes = Set.of( "content:read", "content:write", "a-b_c.d/e", "!#$%&'()*+,:;<=>?@[]^`{|}~" );
        final AccessToken token = service.issue( minimal().scope( scopes ).build() );
        assertThat( verified( verify( service, token.value() ) ).getScope() ).isEqualTo( scopes );
    }

    @Test
    void timestamps_are_whole_seconds_in_the_token_and_in_the_returned_claims()
    {
        final Instant fractional = Instant.parse( "2026-10-14T12:00:00.123456789Z" );
        final AccessTokenServiceImpl service = service( Map.of( "token.ttl", "PT2S" ), fractional );
        final AccessToken token = service.issue( minimal().build() );
        assertThat( token.claims().getIssuedAt() ).isEqualTo( Instant.parse( "2026-10-14T12:00:00Z" ) );
        assertThat( token.claims().getExpiresAt() ).isEqualTo( Instant.parse( "2026-10-14T12:00:02Z" ) );
        assertThat( verified( verify( service, token.value() ) ) ).isEqualTo( token.claims() );

        final AccessToken explicit = service.issue( minimal().ttl( Duration.ofSeconds( 3 ) ).build() );
        assertThat( explicit.claims().getExpiresAt() ).isEqualTo( Instant.parse( "2026-10-14T12:00:03Z" ) );
        assertThat( verified( verify( service, explicit.value() ) ) ).isEqualTo( explicit.claims() );
    }

    private String claimsJson( final String extra )
    {
        return "{\"iss\":\"xp\",\"sub\":\"" + ALICE + "\",\"iat\":" + now.getEpochSecond() + ",\"exp\":" + now.plusSeconds( 3600 ).getEpochSecond() +
            ",\"jti\":\"j\",\"aud\":[\"cli\"],\"client_id\":\"cli\"" + extra + "}";
    }

    private String signed( final SigningKey key, final String extraClaims )
    {
        return signedPayload( key, claimsJson( extraClaims ) );
    }

    private static String signedPayload( final SigningKey key, final String payload )
    {
        final Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        final String header = enc.encodeToString( ( "{\"alg\":\"HS512\",\"typ\":\"" + JwsHs512.TYP + "\",\"kid\":\"" + key.kid() + "\"}" ).getBytes() );
        final String signingInput = header + "." + enc.encodeToString( payload.getBytes() );
        return signingInput + "." + enc.encodeToString( hmac( key, signingInput ) );
    }

    private static byte[] hmac( final SigningKey key, final String signingInput )
    {
        try
        {
            final javax.crypto.Mac mac = javax.crypto.Mac.getInstance( "HmacSHA512" );
            mac.init( new javax.crypto.spec.SecretKeySpec( key.secret(), "HmacSHA512" ) );
            return mac.doFinal( signingInput.getBytes( java.nio.charset.StandardCharsets.US_ASCII ) );
        }
        catch ( java.security.GeneralSecurityException e )
        {
            throw new IllegalStateException( e );
        }
    }

    private static final class RecordingSharedMapService
        implements SharedMapService
    {
        private final SharedMapService delegate;

        private final List<Object> lookedUp = new ArrayList<>();

        private RecordingSharedMapService( final SharedMapService delegate )
        {
            this.delegate = delegate;
        }

        @Override
        public <K, V> SharedMap<K, V> getSharedMap( final String name )
        {
            final SharedMap<K, V> map = delegate.getSharedMap( name );
            return new SharedMap<>()
            {
                @Override
                public V get( final K key )
                {
                    lookedUp.add( key );
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
                    return map.modify( key, modifier, ttlSeconds );
                }
            };
        }
    }
}
