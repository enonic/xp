package com.enonic.xp.portal.impl.idprovider;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeviceLoginHandlerTest
{
    @Test
    void flow_endpoints_are_recognized()
    {
        assertTrue( DeviceLoginHandler.isFlowEndpoint( "device/code" ) );
        assertTrue( DeviceLoginHandler.isFlowEndpoint( "device" ) );
        assertTrue( DeviceLoginHandler.isFlowEndpoint( "authorize" ) );
        assertTrue( DeviceLoginHandler.isFlowEndpoint( "token" ) );
    }

    @Test
    void non_flow_paths_are_not_recognized()
    {
        assertFalse( DeviceLoginHandler.isFlowEndpoint( "" ) );
        assertFalse( DeviceLoginHandler.isFlowEndpoint( "login" ) );
        assertFalse( DeviceLoginHandler.isFlowEndpoint( "logout" ) );
        assertFalse( DeviceLoginHandler.isFlowEndpoint( "device/code/extra" ) );
        assertFalse( DeviceLoginHandler.isFlowEndpoint( "foo" ) );
    }

    @Test
    void only_loopback_is_auto_allowed()
    {
        assertTrue( DeviceLoginHandler.isLoopback( "http://127.0.0.1" ) );
        assertTrue( DeviceLoginHandler.isLoopback( "http://127.0.0.1:12345/callback" ) );
        assertTrue( DeviceLoginHandler.isLoopback( "http://localhost/cb" ) );
        assertTrue( DeviceLoginHandler.isLoopback( "http://[::1]:9000/x" ) );
    }

    @Test
    void non_loopback_redirects_go_to_the_idp_hook()
    {
        // Private-use scheme, claimed https, remote http, bare scheme and relative paths are not
        // auto-allowed - each is the id provider's allowRedirectUri decision.
        assertFalse( DeviceLoginHandler.isLoopback( "com.example.app:/oauth/cb" ) );
        assertFalse( DeviceLoginHandler.isLoopback( "https://app.example.com/cb" ) );
        assertFalse( DeviceLoginHandler.isLoopback( "http://evil.example.com/cb" ) );
        assertFalse( DeviceLoginHandler.isLoopback( "myapp:/cb" ) );
        assertFalse( DeviceLoginHandler.isLoopback( "/relative/path" ) );
    }
}
