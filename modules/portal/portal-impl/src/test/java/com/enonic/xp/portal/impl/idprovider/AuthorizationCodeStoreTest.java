package com.enonic.xp.portal.impl.idprovider;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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

    private static final String VERIFIER = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk";

    private static final String CHALLENGE = challenge( VERIFIER );

    private final AuthorizationCodeStore store = new AuthorizationCodeStore( new InMemorySharedMapService() );

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
            store.createRequest( IDP, "cli", "http://127.0.0.1:9/cb", "s", CHALLENGE, Set.of( "content:read" ), Set.of( "xp" ) );

        assertThat( pending.id() ).hasSize( 43 );
        assertThat( store.takeRequest( IDP, pending.id() ) ).contains( pending );
        assertThat( store.takeRequest( IDP, pending.id() ) ).as( "taken" ).isEmpty();
        assertThat( store.takeRequest( IdProviderKey.from( "other" ), pending.id() ) ).isEmpty();
        assertThat( store.takeRequest( IDP, "nope" ) ).isEmpty();
    }

    @Test
    void a_code_carries_what_consent_fixed_and_is_redeemed_once()
    {
        final AuthorizationCodeStore.PendingAuthorization pending =
            store.createRequest( IDP, "cli", "http://127.0.0.1:9/cb", null, CHALLENGE, Set.of( "content:read", "profile" ), Set.of( "xp" ) );
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
