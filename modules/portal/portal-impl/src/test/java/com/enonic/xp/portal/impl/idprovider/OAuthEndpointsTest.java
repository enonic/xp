package com.enonic.xp.portal.impl.idprovider;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.vhost.IdProviderFlow;

import static org.assertj.core.api.Assertions.assertThat;

class OAuthEndpointsTest
{
    @Test
    void the_five_endpoints_and_nothing_else()
    {
        assertThat( OAuthEndpoints.isEndpoint( "register" ) ).isTrue();
        assertThat( OAuthEndpoints.isEndpoint( "device/code" ) ).isTrue();
        assertThat( OAuthEndpoints.isEndpoint( "device" ) ).isTrue();
        assertThat( OAuthEndpoints.isEndpoint( "authorize" ) ).isTrue();
        assertThat( OAuthEndpoints.isEndpoint( "token" ) ).isTrue();
        assertThat( OAuthEndpoints.isEndpoint( "login" ) ).isFalse();
        assertThat( OAuthEndpoints.isEndpoint( "device/" ) ).isFalse();
        assertThat( OAuthEndpoints.isEndpoint( null ) ).isFalse();
    }

    @Test
    void each_endpoint_needs_its_flow_and_the_token_endpoint_any_issuing_flow()
    {
        assertThat( OAuthEndpoints.requiredFlow( "device/code" ) ).isEqualTo( IdProviderFlow.DEVICE );
        assertThat( OAuthEndpoints.requiredFlow( "device" ) ).isEqualTo( IdProviderFlow.DEVICE );
        assertThat( OAuthEndpoints.requiredFlow( "authorize" ) ).isEqualTo( IdProviderFlow.CODE );
        assertThat( OAuthEndpoints.requiredFlow( "register" ) ).isEqualTo( IdProviderFlow.REGISTER );
        assertThat( OAuthEndpoints.requiredFlow( "token" ) ).isNull();
        assertThat( OAuthEndpoints.ISSUING_FLOWS ).containsExactlyInAnyOrder( IdProviderFlow.DEVICE, IdProviderFlow.CODE );
    }
}
