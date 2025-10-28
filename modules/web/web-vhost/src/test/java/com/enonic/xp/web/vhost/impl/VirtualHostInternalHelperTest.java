package com.enonic.xp.web.vhost.impl;

import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.web.vhost.impl.mapping.VirtualHostIdProvidersMapping;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VirtualHostInternalHelperTest
{
    @Test
    void testGetFullTargetPath()
    {
        final VirtualHostMapping virtualHostMapping =
            new VirtualHostMapping( "host", "foo.no", "/a", "/b/c", VirtualHostIdProvidersMapping.create().build(), 0 );

        final HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getRequestURI() ).thenReturn( "/a/other/service" );

        final String fullTarget = VirtualHostInternalHelper.getFullTargetPath( virtualHostMapping, req );

        assertEquals( "/b/c/other/service", fullTarget  );
    }

    @Test
    void testGetFullTargetPath_root_target()
    {
        final VirtualHostMapping virtualHostMapping =
            new VirtualHostMapping( "host", "foo.no", "/a", "/", VirtualHostIdProvidersMapping.create().build(), 0 );

        final HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getRequestURI() ).thenReturn( "/a/other/service" );

        final String fullTarget = VirtualHostInternalHelper.getFullTargetPath( virtualHostMapping, req );

        assertEquals( "/other/service", fullTarget  );
    }

    @Test
    void testGetFullTargetPath_source_equals_request()
    {
        final VirtualHostMapping virtualHostMapping =
            new VirtualHostMapping( "host", "foo.no", "/a", "/", VirtualHostIdProvidersMapping.create().build(), 0 );

        final HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getRequestURI() ).thenReturn( "/a" );

        final String fullTarget = VirtualHostInternalHelper.getFullTargetPath( virtualHostMapping, req );

        assertEquals( "/", fullTarget  );
    }

    @Test
    void testGetFullTargetPath_source_equals_request_2()
    {
        final VirtualHostMapping virtualHostMapping =
            new VirtualHostMapping( "host", "foo.no", "/a", "/foo/", VirtualHostIdProvidersMapping.create().build(), 0 );

        final HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getRequestURI() ).thenReturn( "/a" );

        final String fullTarget = VirtualHostInternalHelper.getFullTargetPath( virtualHostMapping, req );

        assertEquals( "/foo/", fullTarget  );
    }

    @Test
    void testGetFullTargetPathWithTrailingSlash()
    {
        final VirtualHostMapping virtualHostMapping =
            new VirtualHostMapping( "host", "foo.no", "/a", "/b/c", VirtualHostIdProvidersMapping.create().build(), 0 );

        final HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getRequestURI() ).thenReturn( "/a/other/service/" );

        final String fullTarget = VirtualHostInternalHelper.getFullTargetPath( virtualHostMapping, req );

        assertEquals( "/b/c/other/service/", fullTarget );
    }


    @Test
    void testGetFullTargetPath_root_source()
    {
        final VirtualHostMapping virtualHostMapping =
            new VirtualHostMapping( "host", "foo.no", "/", "/b/c", VirtualHostIdProvidersMapping.create().build(), 0 );

        final HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getRequestURI() ).thenReturn( "/" );

        final String fullTarget = VirtualHostInternalHelper.getFullTargetPath( virtualHostMapping, req );

        assertEquals( "/b/c", fullTarget );
    }

    @Test
    void testGetFullTargetPath_root_source_2()
    {
        final VirtualHostMapping virtualHostMapping =
            new VirtualHostMapping( "host", "foo.no", "/", "/b/c", VirtualHostIdProvidersMapping.create().build(), 0 );

        final HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getRequestURI() ).thenReturn( "/d/e" );

        final String fullTarget = VirtualHostInternalHelper.getFullTargetPath( virtualHostMapping, req );

        assertEquals( "/b/c/d/e", fullTarget );
    }

    @Test
    void testGetFullTargetPath_root_source_and_target()
    {
        final VirtualHostMapping virtualHostMapping =
            new VirtualHostMapping( "host", "foo.no", "/", "/", VirtualHostIdProvidersMapping.create().build(), 0 );

        final HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getRequestURI() ).thenReturn( "/b/c" );

        final String fullTarget = VirtualHostInternalHelper.getFullTargetPath( virtualHostMapping, req );

        assertEquals( "/b/c", fullTarget );
    }
}
