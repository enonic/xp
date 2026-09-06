package com.enonic.xp.impl.server.rest.api;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.WebRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SameOriginCheckTest
{
    @Test
    void nullOriginIsAllowed()
    {
        assertTrue( SameOriginCheck.isSameOrigin( null, request( "https", "example.com", 443 ) ) );
    }

    @Test
    void sameOriginMatchesDefaultsAndCase()
    {
        assertTrue( SameOriginCheck.isSameOrigin( "https://example.com", request( "https", "example.com", 443 ) ) );
        assertTrue( SameOriginCheck.isSameOrigin( "https://example.com:443", request( "https", "example.com", 443 ) ) );
        assertTrue( SameOriginCheck.isSameOrigin( "http://Example.COM", request( "HTTP", "example.com", 80 ) ) );
        assertTrue( SameOriginCheck.isSameOrigin( "http://localhost:8080", request( "http", "localhost", 8080 ) ) );
    }

    @Test
    void crossOriginMismatchesAreRejected()
    {
        assertFalse( SameOriginCheck.isSameOrigin( "https://evil.example.org", request( "https", "example.com", 443 ) ) );
        assertFalse( SameOriginCheck.isSameOrigin( "https://api.example.com", request( "https", "example.com", 443 ) ) );
        assertFalse( SameOriginCheck.isSameOrigin( "http://example.com", request( "https", "example.com", 443 ) ) );
        assertFalse( SameOriginCheck.isSameOrigin( "https://example.com:8443", request( "https", "example.com", 443 ) ) );
    }

    @Test
    void originsWithoutHostAreRejected()
    {
        assertFalse( SameOriginCheck.isSameOrigin( "file:///app", request( "https", "example.com", 443 ) ) );
    }

    @Test
    void requestMissingSchemeOrHostIsRejected()
    {
        assertFalse( SameOriginCheck.isSameOrigin( "https://example.com", request( null, "example.com", 443 ) ) );
        assertFalse( SameOriginCheck.isSameOrigin( "https://example.com", request( "https", null, 443 ) ) );
    }

    @Test
    void unknownSchemeNeedsExplicitPort()
    {
        assertFalse( SameOriginCheck.isSameOrigin( "ftp://example.com", request( "ftp", "example.com", 21 ) ) );
        assertTrue( SameOriginCheck.isSameOrigin( "ftp://example.com:21", request( "ftp", "example.com", 21 ) ) );
    }

    @Test
    void opaqueAndMalformedOriginsAreRejected()
    {
        assertFalse( SameOriginCheck.isSameOrigin( "null", request( "https", "example.com", 443 ) ) );
        assertFalse( SameOriginCheck.isSameOrigin( "example.com", request( "https", "example.com", 443 ) ) );
        assertFalse( SameOriginCheck.isSameOrigin( "https://exa mple.com", request( "https", "example.com", 443 ) ) );
    }

    private static WebRequest request( final String scheme, final String host, final int port )
    {
        final WebRequest request = new WebRequest();
        request.setScheme( scheme );
        request.setHost( host );
        request.setPort( port );
        return request;
    }
}
