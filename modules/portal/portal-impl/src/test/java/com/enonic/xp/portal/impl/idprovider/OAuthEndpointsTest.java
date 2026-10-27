package com.enonic.xp.portal.impl.idprovider;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.vhost.IdProviderFlow;

import static org.assertj.core.api.Assertions.assertThat;

class OAuthEndpointsTest
{
    @Test
    void the_three_endpoints_and_nothing_else()
    {
        assertThat( OAuthEndpoints.isEndpoint( "device/code" ) ).isTrue();
        assertThat( OAuthEndpoints.isEndpoint( "device" ) ).isTrue();
        assertThat( OAuthEndpoints.isEndpoint( "token" ) ).isTrue();
        assertThat( OAuthEndpoints.isEndpoint( "login" ) ).isFalse();
        assertThat( OAuthEndpoints.isEndpoint( "device/" ) ).isFalse();
        assertThat( OAuthEndpoints.isEndpoint( null ) ).isFalse();
    }

    @Test
    void the_page_and_the_code_need_the_device_flow_and_the_token_endpoint_any_issuing_flow()
    {
        assertThat( OAuthEndpoints.requiredFlow( "device/code" ) ).isEqualTo( IdProviderFlow.DEVICE );
        assertThat( OAuthEndpoints.requiredFlow( "device" ) ).isEqualTo( IdProviderFlow.DEVICE );
        assertThat( OAuthEndpoints.requiredFlow( "token" ) ).isNull();
        assertThat( OAuthEndpoints.ISSUING_FLOWS ).containsExactly( IdProviderFlow.DEVICE );
    }
}
