package com.enonic.xp.core.impl.security.token;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.token.Client;
import com.enonic.xp.security.token.GrantTypes;
import com.enonic.xp.security.token.RegisterClientParams;

import static org.assertj.core.api.Assertions.assertThat;

class StatelessClientsTest
{
    private static final IdProviderKey IDP = IdProviderKey.system();

    private static final Instant NOW = Instant.parse( "2026-10-25T12:00:00Z" );

    private static final SigningKey KEY = new SigningKey( "k1", new byte[64] );

    private static final SigningKey OTHER = new SigningKey( "k2", new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
        22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56,
        57, 58, 59, 60, 61, 62, 63, 64} );

    private static TokenSigningKeys keys( final SigningKey signing, final SigningKey... known )
    {
        return new TokenSigningKeys()
        {
            @Override
            public SigningKey signingKey()
            {
                return signing;
            }

            @Override
            public Optional<SigningKey> verificationKey( final String kid )
            {
                return List.of( known ).stream().filter( k -> k.kid().equals( kid ) ).findFirst();
            }
        };
    }

    private static StatelessClients at( final Instant now, final TokenSigningKeys keys )
    {
        return new StatelessClients( keys, Clock.fixed( now, ZoneOffset.UTC ), Duration.ofDays( 90 ) );
    }

    private static Client declared()
    {
        return RegisterClientParams.create()
            .idProvider( IDP )
            .displayName( "Claude" )
            .uri( "https://claude.ai" )
            .grantTypes( List.of( GrantTypes.AUTHORIZATION_CODE, GrantTypes.REFRESH_TOKEN ) )
            .redirectUris( List.of( "https://claude.ai/api/mcp/auth_callback" ) )
            .scope( List.of( "content:read" ) )
            .resource( List.of( "https://example.com/api/mcp" ) )
            .build()
            .getClient();
    }

    @Test
    void the_id_carries_the_record_and_reads_back_while_it_lasts()
    {
        final StatelessClients clients = at( NOW, keys( KEY, KEY ) );
        final Client registered = clients.register( declared() );

        assertThat( StatelessClients.isStatelessId( registered.getId() ) ).isTrue();
        assertThat( registered.getId() ).startsWith( "eyJ" ).contains( "." );
        assertThat( registered.getDisplayName() ).isEqualTo( "Claude" );
        assertThat( registered.isEnabled() ).isTrue();

        final Client read = clients.read( IDP, registered.getId() ).orElseThrow();
        assertThat( read ).isEqualTo( registered );
        assertThat( read.getRedirectUris() ).containsExactly( "https://claude.ai/api/mcp/auth_callback" );
        assertThat( read.getScope() ).containsExactly( "content:read" );
        assertThat( read.getResource() ).containsExactly( "https://example.com/api/mcp" );
        assertThat( read.getGrantTypes() ).containsExactlyInAnyOrder( GrantTypes.AUTHORIZATION_CODE, GrantTypes.REFRESH_TOKEN );

        assertThat( at( NOW.plus( Duration.ofDays( 89 ) ), keys( KEY, KEY ) ).read( IDP, registered.getId() ) ).isPresent();
        assertThat( at( NOW.plus( Duration.ofDays( 90 ) ), keys( KEY, KEY ) ).read( IDP, registered.getId() ) ).as( "run out" ).isEmpty();
    }

    @Test
    void an_id_for_another_id_provider_key_or_kind_reads_as_no_client()
    {
        final StatelessClients clients = at( NOW, keys( KEY, KEY ) );
        final String id = clients.register( declared() ).getId();

        assertThat( clients.read( IdProviderKey.from( "other" ), id ) ).isEmpty();
        assertThat( at( NOW, keys( OTHER, OTHER ) ).read( IDP, id ) ).as( "unknown key" ).isEmpty();
        assertThat( at( NOW, keys( OTHER, KEY ) ).read( IDP, id ) ).as( "an old key still verifies" ).isPresent();

        final String accessToken = JwsHs512.sign( KEY, JwsHs512.newPayload().put( "idp", "system" ).put( "exp", NOW.getEpochSecond() + 100 ) );
        assertThat( clients.read( IDP, accessToken ) ).as( "an access token is not a client id" ).isEmpty();
        assertThat( clients.read( IDP, id.substring( 0, id.length() - 2 ) + "xx" ) ).as( "tampered" ).isEmpty();
        assertThat( clients.read( IDP, "eyJ.not.a.jws" ) ).isEmpty();
        assertThat( StatelessClients.isStatelessId( "claude" ) ).isFalse();
    }

    @Test
    void an_unnamed_client_is_named_after_the_id_it_gets()
    {
        final StatelessClients clients = at( NOW, keys( KEY, KEY ) );
        final Client registered = clients.register( RegisterClientParams.create().idProvider( IDP ).build().getClient() );

        assertThat( registered.getDisplayName() ).isEqualTo( registered.getId() );
        assertThat( clients.read( IDP, registered.getId() ).orElseThrow().getDisplayName() ).isEqualTo( registered.getId() );
    }
}
