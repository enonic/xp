package com.enonic.xp.portal.impl.handler;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.WebRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SameOriginCheckTest
{
    private static WebRequest request( final String scheme, final String host, final int port )
    {
        final WebRequest request = new WebRequest();
        request.setScheme( scheme );
        request.setHost( host );
        request.setPort( port );
        return request;
    }

    @Test
    void absentOriginIsSameOrigin()
    {
        assertTrue( SameOriginCheck.isSameOrigin( null, request( "https", "example.com", 443 ) ) );
    }

    @Test
    void matchingOrigin()
    {
        assertTrue( SameOriginCheck.isSameOrigin( "https://example.com", request( "https", "example.com", 443 ) ) );
        assertTrue( SameOriginCheck.isSameOrigin( "https://example.com:443", request( "https", "example.com", 443 ) ) );
        assertTrue( SameOriginCheck.isSameOrigin( "http://Example.COM", request( "HTTP", "example.com", 80 ) ) );
        assertTrue( SameOriginCheck.isSameOrigin( "http://localhost:8080", request( "http", "localhost", 8080 ) ) );
    }

    @Test
    void differentOrigin()
    {
        assertFalse( SameOriginCheck.isSameOrigin( "https://evil.example.org", request( "https", "example.com", 443 ) ) );
        assertFalse( SameOriginCheck.isSameOrigin( "https://api.example.com", request( "https", "example.com", 443 ) ) );
        assertFalse( SameOriginCheck.isSameOrigin( "http://example.com", request( "https", "example.com", 443 ) ) );
        assertFalse( SameOriginCheck.isSameOrigin( "https://example.com:8443", request( "https", "example.com", 443 ) ) );
    }

    @Test
    void opaqueOrMalformedOrigin()
    {
        assertFalse( SameOriginCheck.isSameOrigin( "null", request( "https", "example.com", 443 ) ) );
        assertFalse( SameOriginCheck.isSameOrigin( "example.com", request( "https", "example.com", 443 ) ) );
        assertFalse( SameOriginCheck.isSameOrigin( "https://exa mple.com", request( "https", "example.com", 443 ) ) );
    }
}
