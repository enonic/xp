package com.enonic.xp.portal.impl.idprovider;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.internal.security.MessageDigests;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleRestriction;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizationCodeStoreTest
{
    private static final IdProviderKey IDP = IdProviderKey.system();

    private static final PrincipalKey ALICE = PrincipalKey.ofUser( IDP, "alice" );

    private static final String VERIFIER = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk";

    private static final String CHALLENGE = challenge( VERIFIER );

    private Instant now = Instant.parse( "2026-10-14T12:00:00Z" );

    private final AuthorizationCodeStore store = new AuthorizationCodeStore( new InMemorySharedMapService(), new Clock()
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

    private static String challenge( final String verifier )
    {
        final MessageDigest digest = MessageDigests.sha256();
        digest.update( verifier.getBytes( StandardCharsets.US_ASCII ) );
        return Base64.getUrlEncoder().withoutPadding().encodeToString( digest.digest() );
    }

    @Test
    void a_request_is_taken_once()
    {
        final AuthorizationCodeStore.PendingAuthorization pending =
            store.createRequest( IDP, "cli", "http://127.0.0.1:9/cb", "s", CHALLENGE, Set.of( "content:read" ), Set.of( "xp" ), ALICE );

        assertThat( pending.id() ).hasSize( 43 );
        final PrincipalKey bob = PrincipalKey.ofUser( IDP, "bob" );
        assertThat( store.takeRequest( IDP, pending.id(), bob ) ).as( "shown to Alice" ).isEmpty();
        assertThat( store.showRequest( IDP, pending.id(), bob ) ).map( AuthorizationCodeStore.PendingAuthorization::shownTo ).contains( bob );
        assertThat( store.takeRequest( IDP, pending.id(), ALICE ) ).as( "shown to Bob now" ).isEmpty();
        assertThat( store.showRequest( IDP, pending.id(), ALICE ) ).isPresent();
        assertThat( store.takeRequest( IDP, pending.id(), ALICE ) ).contains( pending );
        assertThat( store.takeRequest( IDP, pending.id(), ALICE ) ).as( "taken" ).isEmpty();
        assertThat( store.showRequest( IDP, pending.id(), ALICE ) ).as( "taken" ).isEmpty();
        assertThat( store.takeRequest( IdProviderKey.from( "other" ), pending.id(), ALICE ) ).isEmpty();
        assertThat( store.takeRequest( IDP, "nope", ALICE ) ).isEmpty();
    }

    @Test
    void showing_a_request_again_does_not_move_its_deadline()
    {
        final AuthorizationCodeStore.PendingAuthorization untouched =
            store.createRequest( IDP, "cli", "http://127.0.0.1:9/cb", null, CHALLENGE, Set.of(), Set.of( "xp" ), ALICE );
        final AuthorizationCodeStore.PendingAuthorization reshown =
            store.createRequest( IDP, "cli", "http://127.0.0.1:9/cb", null, CHALLENGE, Set.of(), Set.of( "xp" ), ALICE );
        final PrincipalKey bob = PrincipalKey.ofUser( IDP, "bob" );
        now = now.plusSeconds( 1 );
        assertThat( store.showRequest( IDP, reshown.id(), bob ) ).isPresent();
        assertThat( store.takeRequest( IDP, reshown.id(), ALICE ) ).as( "shown to Bob" ).isEmpty();
        now = now.plusSeconds( AuthorizationCodeStore.REQUEST_TTL_SECONDS );
        assertThat( store.takeRequest( IDP, untouched.id(), ALICE ) ).isEmpty();
        assertThat( store.showRequest( IDP, reshown.id(), ALICE ) ).as( "timed out all the same" ).isEmpty();
        assertThat( store.takeRequest( IDP, reshown.id(), bob ) ).isEmpty();
    }

    @Test
    void a_code_carries_what_consent_fixed_and_is_redeemed_once()
    {
        final AuthorizationCodeStore.PendingAuthorization pending =
            store.createRequest( IDP, "cli", "http://127.0.0.1:9/cb", null, CHALLENGE, Set.of( "content:read", "profile" ), Set.of( "xp" ), ALICE );
        final PrincipalKey alice = PrincipalKey.ofUser( IDP, "alice" );
        final RoleRestriction restriction = RoleRestriction.to( PrincipalKeys.from( "role:cms.cm.app" ) );

        final AuthorizationCodeStore.IssuedCode issued = store.issue( pending, alice, Set.of( "content:read" ), restriction );

        assertThat( issued.code() ).hasSize( 43 ).isNotEqualTo( pending.id() );
        final Optional<AuthorizationCodeStore.IssuedCode> redeemed = store.redeem( IDP, issued.code() );
        assertThat( redeemed ).contains( issued );
        assertThat( redeemed.orElseThrow().subject() ).isEqualTo( alice );
        assertThat( redeemed.orElseThrow().scope() ).containsExactly( "content:read" );
        assertThat( redeemed.orElseThrow().audience() ).containsExactly( "xp" );
        assertThat( redeemed.orElseThrow().restriction() ).isEqualTo( restriction );
        assertThat( redeemed.orElseThrow().codeChallenge() ).isEqualTo( CHALLENGE );
        assertThat( store.redeem( IDP, issued.code() ) ).as( "single use" ).isEmpty();

        final AuthorizationCodeStore.IssuedCode unrestricted = store.issue( pending, alice, Set.of(), RoleRestriction.none() );
        assertThat( store.redeem( IDP, unrestricted.code() ).orElseThrow().restriction() ).isEqualTo( RoleRestriction.none() );
    }

    @Test
    void pkce()
    {
        assertThat( AuthorizationCodeStore.isChallenge( CHALLENGE ) ).isTrue();
        assertThat( AuthorizationCodeStore.isChallenge( "short" ) ).isFalse();
        assertThat( AuthorizationCodeStore.isChallenge( CHALLENGE + "=" ) ).isFalse();
        assertThat( AuthorizationCodeStore.isChallenge( null ) ).isFalse();

        assertThat( AuthorizationCodeStore.verifies( VERIFIER, CHALLENGE ) ).isTrue();
        assertThat( AuthorizationCodeStore.verifies( VERIFIER + "x", CHALLENGE ) ).isFalse();
        assertThat( AuthorizationCodeStore.verifies( "tooshort", challenge( "tooshort" ) ) ).as( "a verifier is at least 43 characters" ).isFalse();
        assertThat( AuthorizationCodeStore.verifies( null, CHALLENGE ) ).isFalse();
        assertThat( AuthorizationCodeStore.verifies( VERIFIER, null ) ).isFalse();
        assertThat( AuthorizationCodeStore.verifies( CHALLENGE, CHALLENGE ) ).as( "the challenge is not its own verifier" ).isFalse();
    }
}
