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
        // RFC 8252 section 7.3: the literal loopback IPs only (any port).
        assertTrue( DeviceLoginHandler.isLoopback( "http://127.0.0.1" ) );
        assertTrue( DeviceLoginHandler.isLoopback( "http://127.0.0.1:12345/callback" ) );
        assertTrue( DeviceLoginHandler.isLoopback( "http://[::1]:9000/x" ) );
    }

    @Test
    void non_loopback_redirects_are_not_auto_allowed()
    {
        // Private-use scheme, claimed https, remote http, bare scheme and relative paths are not
        // auto-allowed - each must be registered for the client via the configure hook. 'localhost' is
        // not a loopback literal per RFC 8252 (it can resolve off-loopback), so it is not auto-allowed.
        assertFalse( DeviceLoginHandler.isLoopback( "http://localhost/cb" ) );
        assertFalse( DeviceLoginHandler.isLoopback( "com.example.app:/oauth/cb" ) );
        assertFalse( DeviceLoginHandler.isLoopback( "https://app.example.com/cb" ) );
        assertFalse( DeviceLoginHandler.isLoopback( "http://evil.example.com/cb" ) );
        assertFalse( DeviceLoginHandler.isLoopback( "myapp:/cb" ) );
        assertFalse( DeviceLoginHandler.isLoopback( "/relative/path" ) );
    }

    @Test
    void redirect_matches_exactly_for_non_loopback()
    {
        // Non-loopback redirects must match the registered entry exactly.
        assertTrue( DeviceLoginHandler.redirectMatches( "com.example.app:/oauth", "com.example.app:/oauth" ) );
        assertTrue( DeviceLoginHandler.redirectMatches( "https://app.example.com/cb", "https://app.example.com/cb" ) );
        assertFalse( DeviceLoginHandler.redirectMatches( "https://app.example.com/cb", "https://app.example.com/other" ) );
        assertFalse( DeviceLoginHandler.redirectMatches( "com.example.app:/oauth", "com.evil.app:/oauth" ) );
    }

    @Test
    void redirect_matches_loopback_on_flexible_port_only()
    {
        // Loopback: the port is flexible, but scheme, host and path must still match.
        assertTrue( DeviceLoginHandler.redirectMatches( "http://127.0.0.1/cb", "http://127.0.0.1:54321/cb" ) );
        assertTrue( DeviceLoginHandler.redirectMatches( "http://127.0.0.1:1/cb", "http://127.0.0.1:2/cb" ) );
        assertTrue( DeviceLoginHandler.redirectMatches( "http://[::1]/cb", "http://[::1]:8080/cb" ) );
        // Different path - not a match even on loopback.
        assertFalse( DeviceLoginHandler.redirectMatches( "http://127.0.0.1/cb", "http://127.0.0.1:54321/other" ) );
        // Different loopback host literal - not a match.
        assertFalse( DeviceLoginHandler.redirectMatches( "http://127.0.0.1/cb", "http://[::1]/cb" ) );
    }
}
