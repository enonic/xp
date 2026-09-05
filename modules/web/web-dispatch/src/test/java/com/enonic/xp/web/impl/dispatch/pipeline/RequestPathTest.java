package com.enonic.xp.web.impl.dispatch.pipeline;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RequestPathTest
{
    private static HttpServletRequest request( final String servletPath, final String pathInfo )
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        Mockito.when( req.getServletPath() ).thenReturn( servletPath );
        Mockito.when( req.getPathInfo() ).thenReturn( pathInfo );
        return req;
    }

    @Test
    void mappedOnEverything()
    {
        // how the dispatch servlet is mapped: the whole path within the context is the path info
        assertEquals( "/admin/tool", RequestPath.of( request( "", "/admin/tool" ) ) );
    }

    @Test
    void mappedOnPrefix()
    {
        assertEquals( "/admin/tool", RequestPath.of( request( "/admin", "/tool" ) ) );
    }

    @Test
    void mappedExactly()
    {
        assertEquals( "/health", RequestPath.of( request( "/health", null ) ) );
    }

    @Test
    void noPath()
    {
        assertNull( RequestPath.of( request( null, null ) ) );
    }
}
