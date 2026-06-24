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
    void loopback_redirects_are_allowed()
    {
        assertTrue( DeviceLoginHandler.isAllowedRedirect( "http://127.0.0.1" ) );
        assertTrue( DeviceLoginHandler.isAllowedRedirect( "http://127.0.0.1:12345/callback" ) );
        assertTrue( DeviceLoginHandler.isAllowedRedirect( "http://localhost/cb" ) );
        assertTrue( DeviceLoginHandler.isAllowedRedirect( "http://[::1]:9000/x" ) );
    }

    @Test
    void private_use_scheme_redirects_are_allowed()
    {
        // RFC 8252 reverse-DNS private-use scheme (PKCE-protected).
        assertTrue( DeviceLoginHandler.isAllowedRedirect( "com.example.app:/oauth/cb" ) );
        assertTrue( DeviceLoginHandler.isAllowedRedirect( "org.enonic.cli:/done" ) );
    }

    @Test
    void remote_http_and_bare_schemes_are_rejected()
    {
        // Remote http(s) needs client registration (out of scope) - never an open redirect.
        assertFalse( DeviceLoginHandler.isAllowedRedirect( "http://evil.example.com/cb" ) );
        assertFalse( DeviceLoginHandler.isAllowedRedirect( "https://app.example.com/cb" ) );
        // A non-reverse-DNS custom scheme is not accepted without registration.
        assertFalse( DeviceLoginHandler.isAllowedRedirect( "myapp:/cb" ) );
        assertFalse( DeviceLoginHandler.isAllowedRedirect( "/relative/path" ) );
    }
}
