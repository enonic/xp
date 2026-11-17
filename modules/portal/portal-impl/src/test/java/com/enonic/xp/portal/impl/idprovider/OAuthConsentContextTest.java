package com.enonic.xp.portal.impl.idprovider;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.User;

import static org.assertj.core.api.Assertions.assertThat;

class OAuthConsentContextTest
{
    @Test
    void the_redirect_origin_is_what_a_form_action_source_names()
    {
        assertThat( OAuthConsentContext.Confirm.redirectOrigin( "http://127.0.0.1:51234/callback?x=1" ) ).isEqualTo( "http://127.0.0.1:51234" );
        assertThat( OAuthConsentContext.Confirm.redirectOrigin( "HTTPS://Claude.AI/api/mcp/auth_callback" ) ).isEqualTo( "https://claude.ai" );
        assertThat( OAuthConsentContext.Confirm.redirectOrigin( "http://localhost/cb" ) ).as( "no port, none added" ).isEqualTo( "http://localhost" );
        assertThat( OAuthConsentContext.Confirm.redirectOrigin( "http://[::1]:8080/cb" ) ).as( "a policy cannot name an IPv6 address" ).isNull();
        assertThat( OAuthConsentContext.Confirm.redirectOrigin( "com.example.app:/oauth2redirect" ) ).as( "a private-use scheme" ).isEqualTo( "com.example.app:" );
        assertThat( OAuthConsentContext.Confirm.redirectOrigin( "not a uri" ) ).isNull();
    }

    @Test
    void the_user_is_shown_with_the_email()
    {
        final User user = User.create()
            .key( PrincipalKey.ofUser( IdProviderKey.from( "cloud" ), "auth0-123" ) )
            .login( "auth0-123" )
            .displayName( "Alice" )
            .email( "alice@example.com" )
            .build();

        assertThat( user( new OAuthConsentContext.Enter( IdProviderKey.from( "cloud" ), user, null ) ) ).isEqualTo(
            Map.of( "key", "user:cloud:auth0-123", "login", "auth0-123", "displayName", "Alice", "email", "alice@example.com" ) );
    }

    @Test
    void a_user_without_an_email_has_none()
    {
        final User user = User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "alice" ) ).login( "alice" ).displayName( "Alice" ).build();

        assertThat( user( new OAuthConsentContext.Enter( IdProviderKey.system(), user, null ) ) ).doesNotContainKey( "email" )
            .containsEntry( "login", "alice" );
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> user( final OAuthConsentContext context )
    {
        return (Map<String, Object>) context.toMap().get( "user" );
    }
}
