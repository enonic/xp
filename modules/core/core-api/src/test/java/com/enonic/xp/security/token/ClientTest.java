package com.enonic.xp.security.token;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClientTest
{
    private static final IdProviderKey IDP = IdProviderKey.system();

    @Test
    void defaults()
    {
        final Client client = Client.create().idProvider( IDP ).id( "cli" ).build();

        assertThat( client.getDisplayName() ).as( "the name falls back to the id" ).isEqualTo( "cli" );
        assertThat( client.getUri() ).isNull();
        assertThat( client.getGrantTypes() ).isEmpty();
        assertThat( client.getRedirectUris() ).isEmpty();
        assertThat( client.getScope() ).isEmpty();
        assertThat( client.getResource() ).isEmpty();
        assertThat( client.getGrantTtl() ).isNull();
        assertThat( client.isEnabled() ).isTrue();
    }

    @Test
    void invalid_values_are_refused()
    {
        assertThatThrownBy( () -> Client.create().idProvider( IDP ).id( " " ).build() ).isInstanceOf( IllegalArgumentException.class );
        assertThatThrownBy( () -> Client.create().idProvider( IDP ).id( "cli" ).grantTypes( List.of( "password" ) ).build() )
            .isInstanceOf( IllegalArgumentException.class )
            .hasMessageContaining( "password" );
        assertThatThrownBy( () -> Client.create().idProvider( IDP ).id( "cli" ).uri( "enonic.com" ).build() )
            .isInstanceOf( IllegalArgumentException.class )
            .hasMessageContaining( "absolute" );
        assertThatThrownBy( () -> Client.create().idProvider( IDP ).id( "cli" ).redirectUris( List.of( "https://a/#x" ) ).build() )
            .isInstanceOf( IllegalArgumentException.class )
            .hasMessageContaining( "fragment" );
        assertThatThrownBy( () -> Client.create().idProvider( IDP ).id( "cli" ).logoUri( "ht tp://x" ).build() )
            .isInstanceOf( IllegalArgumentException.class )
            .hasMessageContaining( "not a URI" );
        assertThatThrownBy( () -> Client.create().idProvider( IDP ).id( "cli" ).policyUri( "javascript:alert(1)" ).build() )
            .isInstanceOf( IllegalArgumentException.class )
            .hasMessageContaining( "http" );
        assertThat( Client.create().idProvider( IDP ).id( "app" ).redirectUris( List.of( "com.example.app:/oauth" ) ).build().getRedirectUris() )
            .as( "a native app's redirect URI may use its own scheme" )
            .containsExactly( "com.example.app:/oauth" );
        assertThatThrownBy( () -> Client.create().idProvider( IDP ).id( "cli" ).scope( List.of( "a b" ) ).build() )
            .isInstanceOf( IllegalArgumentException.class );
        assertThatThrownBy( () -> Client.create().idProvider( IDP ).id( "cli" ).grantTtl( Duration.ZERO ).build() )
            .isInstanceOf( IllegalArgumentException.class );
        assertThatThrownBy( () -> Client.create().id( "cli" ).build() ).isInstanceOf( NullPointerException.class );
    }

    @Test
    void update_keeps_what_is_unset_clears_what_is_null_and_replaces_sets()
    {
        final Client client = Client.create()
            .idProvider( IDP )
            .id( "cli" )
            .displayName( "CLI" )
            .uri( "https://a" )
            .grantTypes( List.of( GrantTypes.DEVICE_CODE ) )
            .scope( List.of( "x" ) )
            .grantTtl( Duration.ofDays( 1 ) )
            .build();

        assertThat( UpdateClientParams.create().idProvider( IDP ).id( "cli" ).editor( e -> {
        } ).build().applyTo( client ) ).isEqualTo( client );

        final Client changed = UpdateClientParams.create()
            .idProvider( IDP )
            .id( "cli" )
            .editor( e -> {
                e.displayName = null;
                e.uri = null;
                e.scope = Set.of();
                e.grantTypes = Set.of( GrantTypes.REFRESH_TOKEN );
                e.grantTtl = null;
                e.enabled = false;
            } )
            .build()
            .applyTo( client );
        assertThat( changed.getDisplayName() ).isEqualTo( "cli" );
        assertThat( changed.getUri() ).isNull();
        assertThat( changed.getScope() ).isEmpty();
        assertThat( changed.getGrantTypes() ).containsExactly( GrantTypes.REFRESH_TOKEN );
        assertThat( changed.getGrantTtl() ).isNull();
        assertThat( changed.isEnabled() ).isFalse();
    }

    @Test
    void grant_types()
    {
        assertThat( GrantTypes.isKnown( GrantTypes.DEVICE_CODE ) ).isTrue();
        assertThat( GrantTypes.isKnown( GrantTypes.AUTHORIZATION_CODE ) ).isTrue();
        assertThat( GrantTypes.isKnown( "implicit" ) ).isFalse();
    }
}
