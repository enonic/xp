package com.enonic.xp.web.vhost;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WellKnownPathsTest
{
    @Test
    void the_resource_path_follows_the_segment()
    {
        assertEquals( "/", WellKnownPaths.resourcePath( "/.well-known/oauth-protected-resource" ) );
        assertEquals( "/", WellKnownPaths.resourcePath( "/.well-known/oauth-protected-resource/" ) );
        assertEquals( "/api/mcp", WellKnownPaths.resourcePath( "/.well-known/oauth-protected-resource/api/mcp" ) );
        assertEquals( "/api/mcp/", WellKnownPaths.resourcePath( "/.well-known/oauth-protected-resource/api/mcp/" ) );
        assertEquals( "/_/idprovider/system", WellKnownPaths.resourcePath( "/.well-known/oauth-authorization-server/_/idprovider/system" ) );
        assertNull( WellKnownPaths.resourcePath( "/.well-known/oauth-protected-resources" ) );
        assertNull( WellKnownPaths.resourcePath( "/.well-known/openid-configuration" ) );
        assertNull( WellKnownPaths.resourcePath( "/api/mcp" ) );
        assertNull( WellKnownPaths.resourcePath( null ) );
    }

    @Test
    void the_document_is_named_by_the_segment()
    {
        assertEquals( WellKnownPaths.PROTECTED_RESOURCE, WellKnownPaths.documentOf( "/.well-known/oauth-protected-resource/x" ) );
        assertEquals( WellKnownPaths.AUTHORIZATION_SERVER, WellKnownPaths.documentOf( "/.well-known/oauth-authorization-server" ) );
        assertNull( WellKnownPaths.documentOf( "/.well-known/other" ) );
    }
}
