package com.enonic.xp.portal.impl.idprovider;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.vhost.IdProviderFlow;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceLoginEndpointsTest
{
    @Test
    void the_three_endpoints_and_nothing_else()
    {
        assertThat( DeviceLoginEndpoints.isEndpoint( "device/code" ) ).isTrue();
        assertThat( DeviceLoginEndpoints.isEndpoint( "device" ) ).isTrue();
        assertThat( DeviceLoginEndpoints.isEndpoint( "token" ) ).isTrue();
        assertThat( DeviceLoginEndpoints.isEndpoint( "login" ) ).isFalse();
        assertThat( DeviceLoginEndpoints.isEndpoint( "device/" ) ).isFalse();
        assertThat( DeviceLoginEndpoints.isEndpoint( null ) ).isFalse();
    }

    @Test
    void the_page_and_the_code_need_the_device_flow_and_the_token_endpoint_any_issuing_flow()
    {
        assertThat( DeviceLoginEndpoints.requiredFlow( "device/code" ) ).isEqualTo( IdProviderFlow.DEVICE );
        assertThat( DeviceLoginEndpoints.requiredFlow( "device" ) ).isEqualTo( IdProviderFlow.DEVICE );
        assertThat( DeviceLoginEndpoints.requiredFlow( "token" ) ).isNull();
        assertThat( DeviceLoginEndpoints.ISSUING_FLOWS ).containsExactly( IdProviderFlow.DEVICE );
    }
}
