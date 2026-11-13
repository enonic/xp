package com.enonic.xp.portal.impl.idprovider;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OAuthConsentContextTest
{
    @Test
    void the_redirect_origin_is_what_a_form_action_source_names()
    {
        assertThat( OAuthConsentContext.Confirm.redirectOrigin( "http://127.0.0.1:51234/callback?x=1" ) ).isEqualTo( "http://127.0.0.1:51234" );
        assertThat( OAuthConsentContext.Confirm.redirectOrigin( "HTTPS://Claude.AI/api/mcp/auth_callback" ) ).isEqualTo( "https://claude.ai" );
        assertThat( OAuthConsentContext.Confirm.redirectOrigin( "http://localhost/cb" ) ).as( "no port, none added" ).isEqualTo( "http://localhost" );
        assertThat( OAuthConsentContext.Confirm.redirectOrigin( "http://[::1]:8080/cb" ) ).isEqualTo( "http://[::1]:8080" );
        assertThat( OAuthConsentContext.Confirm.redirectOrigin( "com.example.app:/oauth2redirect" ) ).as( "a private-use scheme" ).isEqualTo( "com.example.app:" );
        assertThat( OAuthConsentContext.Confirm.redirectOrigin( "not a uri" ) ).isNull();
    }
}
